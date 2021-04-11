package gameMain;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import characters.AllyPlayer;
import characters.Dorcas;
import characters.EnemyPlayer;
import characters.Merric;
import characters.Player;
import characters.Shinnon;
import gameMain.Game.DIFFICULTY;
import gameMain.Game.STATE;
import graphics.BattlePreparationsMenu;
import items.CombatItem;
import items.Convoy;
import items.Item;
import items.Key;
import items.Vulnery;
import tiles.*;

public class ChapterOrganizer {
	
	/** Parent game so we can manipulate game data */
	public Game game;
	/**The chapters in this game*/
	public ChapterMap[] chapters = new ChapterMap[2];
	/**Chapter we are currently playing*/
	public int currentChapter;
	/**Chapter Map we are currently on*/
	public ChapterMap currentMap;
	/**All the allys in this game will be stored here */
	public LinkedList<AllyPlayer> allys = new LinkedList<>();
	/**All the enemys in this game will be stored here */
	public LinkedList<EnemyPlayer> enemys = new LinkedList<>();
	/** Unit that is summoned by other players */
	public AllyPlayer summonedUnit;
	/** Chapter Map as read in from the chapter file */
	public ArrayList<String[]> stringMap;
	/** Reader for reading from files */
	public Scanner reader;
	/** Writer for writing to files */
	public PrintWriter writer;
	/** Which Save-State are we in */
	public int loadLevel;
	/** Enemy Movement Handler that moves and makes decisions for the enemies */
	public EnemyPhaseProcessor enemyMove;
	/** Complete list of all ally players, whether they are currently active or not */
	public ArrayList<AllyPlayer> totalAllies;
	/** Speeds at which enemy turn is processed */
	public int ENEMY_RUN_SPEED = 36, ENEMY_STOP_SPEED = 210;	
	/** The amount of gold shared from our army in this game, never negative */
	public int gameGold = 5000;
	/** Handles the selecting of allys for this particular battle among other things */
	public BattlePreparationsMenu battlePrep;
	/** Holds excess items for Players */
	public Convoy convoy;
	/** File containing Ally Recruitment information */
	private File recruitmentFile = new File("res//designInfo//recruitmentInfo");
	/** For delaying enemy phase to be observable by user */
	private long enemyPhaseTimer;

	/** Creates a brand new Chapter Organizer with no enemies or allies for chapter designer and default game constructor*/
	public ChapterOrganizer(Game game, ChapterMap map, int loadLevel) {
		this.game = game;
		game.setLoadLevel(loadLevel);
		allys = new LinkedList<>();
		enemys = new LinkedList<>();
		currentChapter = 1;
		currentMap = map;
		setVillages();
		totalAllies = new ArrayList<>();
		enemyMove = new EnemyPhaseProcessor(game, this);
		this.loadLevel = loadLevel;
		setAllySkills();
		convoy = new Convoy(loadLevel);
	}
	/** Creates a Chapter Organizer instance based off a current chapter file and save state location */
	public ChapterOrganizer(Game game, int currentChapter, int loadLevel) {
		this.currentChapter = currentChapter;
		this.game = game;
		game.setLoadLevel(loadLevel);
		this.allys = new LinkedList<>();
		this.enemys = new LinkedList<>();
		this.loadLevel = loadLevel;
		totalAllies = new ArrayList<>();
		// sets the reader to the current chapter, i or h extention if past chapter 15
		setCurrentMap(currentChapter);
		if (currentChapter < 10) game.onHectorMode = false;
		setVillages();
		removeDeadPlayers();
		enemyMove = new EnemyPhaseProcessor(game, this);
		setAllySkills();
		convoy = new Convoy(loadLevel);
	}
	
	public void setBattlePreparationMenu() {
		this.battlePrep = new BattlePreparationsMenu(game, this);
		game.setGameState(STATE.BattlePreparations);
	}

	/** Progresses the game to the next chapter location */
	public void nextChapter(int saveLevel) {
		System.out.println("Next chapter() has been called");
		removeSummonedUnits();
		updatePlayers(saveLevel);
		removeAllCarriedPlayers();
		currentMap = null;
		currentChapter++;
		game.setPopUpMenu(null);
		try {
			this.reader = new Scanner(new File("res//chapters//chapter" + currentChapter));
		} catch (FileNotFoundException e) {
			System.out.println("could not find " + "res//chapters//chapter" + currentChapter);
		}
		this.loadLevel = saveLevel;
		game.setLoadLevel(saveLevel);
		convoy.setLoadLevel(saveLevel);
		if (this.currentChapter == 2) {
			// it was just a new game, set mode to Ikes mode
			convoy.resetConvoy();
			this.nullRecruitmentData();
		} else {
			// not a new game, check if we have recruited any units
			checkRecruitments();
		}
		allys.clear();
		enemys.clear();
		setCurrentMap(currentChapter);
		this.convoy.saveConvoySupply();
		this.convoy.resupplyConvoy();
		this.removeAllStatBuffs();
		setVillages();
		removeDeadPlayers();
		removeOtherPartyAllies();
		enemyMove.updateMap();
		setAllySkills();
		for (int i = 0; i < totalAllies.size(); i++) {
			if (totalAllies.get(i) != null) totalAllies.get(i).setMAUT(true);
		}
		currentMap.findAllyWithMoves();
	}
	/** Removes allies from Hector's party if we are on Ike's Mode */
	private void removeOtherPartyAllies() {
		if (currentChapter == 10) {
			// remove Ike and units who fled with him
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < totalAllies.size(); j++) {
					AllyPlayer player = totalAllies.get(j);
					if (player.name.equalsIgnoreCase("Ike") || player.name.equalsIgnoreCase("Raymond") || player.name.equalsIgnoreCase("Heath")) {
						totalAllies.remove(player);
					}
				}
			}
		} else if (currentChapter == 11) {
			for (int i = 0; i < 3*totalAllies.size(); i++) {
				for (int j = 0; j < totalAllies.size(); j++) {
					AllyPlayer player = totalAllies.get(j);
					if (!player.name.equalsIgnoreCase("Ike") && !player.name.equalsIgnoreCase("Raymond") && !player.name.equalsIgnoreCase("Heath")
							&& !player.name.equalsIgnoreCase("Dorcas")) {
						totalAllies.remove(player);
					}
				}
			}
		} else if (currentChapter == 12) {
			for (int i = 0; i < 2*totalAllies.size(); i++) {
				for (int j = 0; j < totalAllies.size(); j++) {
					AllyPlayer player = totalAllies.get(j);
					if (player.name.equalsIgnoreCase("Ike") || player.name.equalsIgnoreCase("Raymond") || player.name.equalsIgnoreCase("Heath")
							|| player.name.equalsIgnoreCase("Dorcas")) {
						totalAllies.remove(player);
					}
				}
			}
		} else if (currentChapter == 13) {
			for (int i = 0; i < 3*totalAllies.size(); i++) {
				for (int j = 0; j < totalAllies.size(); j++) {
					AllyPlayer player = totalAllies.get(j);
					if (!player.name.equalsIgnoreCase("Ike") && !player.name.equalsIgnoreCase("Raymond") && !player.name.equalsIgnoreCase("Heath")
							&& !player.name.equalsIgnoreCase("Dorcas")) {
						totalAllies.remove(player);
					}
				}
			}
		} else if (currentChapter == 14) {
			for (int i = 0; i < 4*totalAllies.size(); i++) {
				for (int j = 0; j < totalAllies.size(); j++) {
					AllyPlayer player = totalAllies.get(j);
					if (player.name.equalsIgnoreCase("Ike") || player.name.equalsIgnoreCase("Raymond") || player.name.equalsIgnoreCase("Heath")
							|| player.name.equalsIgnoreCase("Dorcas") || player.name.equalsIgnoreCase("Helga")
							|| player.name.equalsIgnoreCase("Soren") || player.name.equalsIgnoreCase("Navarre") || player.name.equalsIgnoreCase("Jetson")) {
						totalAllies.remove(player);
					}
				}
			}
		} else if (currentChapter == 15) {
			for (int i = 0; i < 3*totalAllies.size(); i++) {
				for (int j = 0; j < totalAllies.size(); j++) {
					AllyPlayer player = totalAllies.get(j);
					if (!player.name.equalsIgnoreCase("Ike") && !player.name.equalsIgnoreCase("Raymond") && !player.name.equalsIgnoreCase("Heath")
							&& !player.name.equalsIgnoreCase("Dorcas") && !player.name.equalsIgnoreCase("Helga")
							&& !player.name.equalsIgnoreCase("Soren") && !player.name.equalsIgnoreCase("Navarre") && !player.name.equalsIgnoreCase("Jetson")) {
						totalAllies.remove(player);
					}
				}
			}
		} else if (currentChapter == 16) {
			if (game.onHectorMode) {
				
				for (int i = 0; i < 4*totalAllies.size(); i++) {
					for (int j = 0; j < totalAllies.size(); j++) {
						AllyPlayer player = totalAllies.get(j);
						if (player.name.equalsIgnoreCase("Ike") || player.name.equalsIgnoreCase("Raymond") || player.name.equalsIgnoreCase("Heath")
								|| player.name.equalsIgnoreCase("Dorcas") || player.name.equalsIgnoreCase("Helga")
								|| player.name.equalsIgnoreCase("Soren") || player.name.equalsIgnoreCase("Navarre") || player.name.equalsIgnoreCase("Jetson")) {
							totalAllies.remove(player);
						}
					}
				}
				
			} else {
				for (int i = 0; i < 3*totalAllies.size(); i++) {
					for (int j = 0; j < totalAllies.size(); j++) {
						AllyPlayer player = totalAllies.get(j);
						if (!player.name.equalsIgnoreCase("Ike") && !player.name.equalsIgnoreCase("Raymond") && !player.name.equalsIgnoreCase("Heath")
								&& !player.name.equalsIgnoreCase("Dorcas") && !player.name.equalsIgnoreCase("Helga")
								&& !player.name.equalsIgnoreCase("Soren") && !player.name.equalsIgnoreCase("Navarre") && !player.name.equalsIgnoreCase("Jetson")) {
							totalAllies.remove(player);
						}
					}
				}
			}
		}
	}
	
	/** Checks if we have recruited an ally this chapter, if so set their recruitment data to True */
	private void checkRecruitments() {
		
		if (currentChapter == 6) {
			// check if we have recruited Merric
			for (int i = 0; i < allys.size(); i++) {
				if (allys.get(i).name.equalsIgnoreCase("Merric")) {
					this.setRecruitmentData("Merric", true);
					break;
				} 
			}	
		} else if (currentChapter == 13) {
			//check if we have recruited Shinnon
			for (int i = 0; i < allys.size(); i++) {
				if (allys.get(i).name.equalsIgnoreCase("Shinnon")) {
					this.setRecruitmentData("Shinnon", true);
					break;
				} 
			}
		} else if (currentChapter == 11) {
			// check if we have recruited Priscilla
			for (int i = 0; i < allys.size(); i++) {
				if (allys.get(i).name.equalsIgnoreCase("Priscilla")) {
					this.setRecruitmentData("Priscilla", true);
					break;
				} 
			}
		} else if (currentChapter == 12) {
			// check if we have recruited Dorcas
			for (int i = 0; i < allys.size(); i++) {
				if (allys.get(i).name.equalsIgnoreCase("Dorcas")) {
					this.setRecruitmentData("Dorcas", true);
					break;
				} 
			}
		}
	}
	
	/** Removes all carried stats from the player */
	public void removeAllCarriedPlayers() {
		for (int i = 0; i < totalAllies.size(); i++) {
			totalAllies.get(i).carryPlayer(null);
		}
	}
	/** Adds the Gold gained from beating the level 8 armory chapter */
	public void receiveArmoryGold() {
			// once we beat chapter 8 we get lots of gold
		switch (game.gameDifficulty) {
			
		case Easy:
			incGameGold(9000);
			break;
		case Normal:
			incGameGold(8000);
			break;
		case Hard:
			incGameGold(7000);
			break;
		case Crushing:
			incGameGold(5000);
			break;
		default:
			incGameGold(6000);
							
		}
	
	}
	
	/** Removes all summoned and confessed units */
	public void removeSummonedUnits() {
		AllyPlayer summoned = null;
		AllyPlayer confessed = null;
		for (int i = 0; i < totalAllies.size(); i++) {
			if (totalAllies.get(i).name.equalsIgnoreCase("SummonedUnit")) {
				summoned = totalAllies.get(i);
			} else if (totalAllies.get(i).isConfessed) {
				confessed = totalAllies.get(i);
			}
		}
		
		if (summoned == null) {
			this.summonedUnit = null;
			if (confessed != null) {
				confessed.die();
			}
			return;
		}
		summoned.die();
		if (confessed != null) {
			confessed.die();
		}
		totalAllies.remove(summoned);
		
	}
	/** True if there is a summoned unit already on the board */
	public boolean hasSummonedUnit() {
		return (summonedUnit != null);
	}
	
	/** Sets all ally's skill use stat to true since it is the start of the chapter */
	private void setAllySkills() {
		for (int i = 0; i < allys.size(); i++) {
			allys.get(i).setUseSkill(true);
		}
	}

	
	private void removeDeadPlayers() {
		if (game.gameDifficulty == DIFFICULTY.Easy) return;
		for (int i = 0; i < game.deadPlayers.size(); i++) {
			game.deadPlayers.get(i).die();
			totalAllies.remove(game.deadPlayers.get(i));
		}
		game.deadPlayers.clear();
	}
	
	
	
	/** Saves player stats into their unique player files at the end of chapter */
	public void updatePlayers(int saveLevel) {

		AllyPlayer ally;
		String filename;
		File file;
		ArrayList<String> fileContents = new ArrayList<>();
		
		for (int i = 0; i < totalAllies.size(); i++) {
		
			ally = totalAllies.get(i);
			if (ally.name.equalsIgnoreCase("SummonedUnit") || ally.isConfessed) {
				continue;
			}
			filename = "res//res_characters//" + ally.name;
			file = new File(filename);
			fileContents.clear();
			
			try {
				//writer = new PrintWriter(new File(filename));
				reader = new Scanner(file);
				for (int load = 0; load < 4; load++) {
					//each of the load levels
					if (load != saveLevel) {
						
						for (int row = 0; row < ally.NUM_LINES; row++) {
							fileContents.add(reader.nextLine());
						}
						
					} else {
						//we are at our correct load spot!
						for (int t = 0; t < ally.NUM_LINES; t++) reader.nextLine();
						
						fileContents.add("LOAD_LEVEL " + load);
						if (game.gameDifficulty == DIFFICULTY.Easy) {
							// automatically respawn on Easy Mode
							fileContents.add("1");
						} else {
							if (ally.currentHP > 0) fileContents.add("1"); // alive
							else fileContents.add("0"); // dead
						}
						fileContents.add(ally.Class);
						fileContents.add(ally.skill.getName());
						fileContents.add(ally.stats[0] + ":" + ally.growths[0]);
						fileContents.add(ally.stats[1] + ":" + ally.growths[1]);
						fileContents.add(ally.stats[2] + ":" + ally.growths[2]);
						fileContents.add(ally.stats[3] + ":" + ally.growths[3]);
						fileContents.add(ally.stats[4] + ":" + ally.growths[4]);
						fileContents.add(ally.stats[5] + ":" + ally.growths[5]);
						fileContents.add(ally.stats[6] + ":" + ally.growths[6]);
						fileContents.add(String.valueOf(ally.stats[7]));
						fileContents.add(String.valueOf(ally.stats[8]));
						fileContents.add(String.valueOf(ally.stats[9]));
						fileContents.add(String.valueOf(ally.EXP));
						fileContents.add(ally.weaponMasteriesGrade[0] + ":" + ally.weaponMasteries[0]);
						fileContents.add(ally.weaponMasteriesGrade[1] + ":" + ally.weaponMasteries[1]);
						fileContents.add(ally.weaponMasteriesGrade[2] + ":" + ally.weaponMasteries[2]);
						fileContents.add(ally.weaponMasteriesGrade[3] + ":" + ally.weaponMasteries[3]);
						for (int j = 0; j < 4; j++) {
							fileContents.add(weaponIDofWallet(ally, "Weapons", j) + weaponDurationofWallet(ally, "Weapons", j));
						}
						for (int j = 0; j < 4; j++) {
							fileContents.add(weaponIDofWallet(ally, "Utilities", j) + weaponDurationofWallet(ally, "Utilities", j));
						}
						//fileContents.add(String.valueOf(ally.equiptItem.getWeaponID()) + ":" + ally.equiptItem.duration);
					}
						
				}
				reader.close();
				writer = new PrintWriter(file);
				for (String str : fileContents) {
					writer.println(str);
				}
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String weaponIDofWallet(AllyPlayer ally, String whichWallet, int index) {
		if (whichWallet.equalsIgnoreCase("Weapons")) {
			if (index >= ally.wallet.weapons.size()) return "none";
			else return String.valueOf(ally.wallet.weapons.get(index).getWeaponID());
		} else {
			if (index >= ally.wallet.utilities.size()) return "none";
			else return String.valueOf(ally.wallet.utilities.get(index).getWeaponID());
		}
	}
	
	private String weaponDurationofWallet(AllyPlayer ally, String whichWallet, int index) {
		if (whichWallet.equalsIgnoreCase("Weapons")) {
			if (index >= ally.wallet.weapons.size()) return "";
			else return String.valueOf(":" + ally.wallet.weapons.get(index).duration);
		} else {
			if (index >= ally.wallet.utilities.size()) return "";
			else return String.valueOf(":" + ally.wallet.utilities.get(index).duration);
		}
	}
	
	/** Sets the current Map based on the information obtained by the reader */
	public void setCurrentMap(int currentChapter) {
		
		if (currentChapter > 15) {
			if (game.onHectorMode) {
				try {
					this.reader = new Scanner(new File("res//chapters//chapter" + currentChapter + "h"));
				} catch (FileNotFoundException e) {
					System.out.println("could not find " + "res/chapters/chapter" + currentChapter + "h");
				}
			} else {
				try {
					this.reader = new Scanner(new File("res//chapters//chapter" + currentChapter + "i"));
				} catch (FileNotFoundException e) {
					System.out.println("could not find " + "res/chapters/chapter" + currentChapter + "i");
				}
			}
		} else {
			try {
				this.reader = new Scanner(new File("res//chapters//chapter" + currentChapter));
			} catch (FileNotFoundException e) {
				System.out.println("could not find " + "res/chapters/chapter" + currentChapter);
			}
		}
		
		String[] stringLine;
		stringMap = new ArrayList<>();
		while (reader.hasNextLine()) {
			stringLine = reader.nextLine().split(" ");
			stringMap.add(stringLine);
			
		}
		currentMap = new ChapterMap(stringMap.get(0).length, stringMap.size(), game);
		currentMap.setCurrentChapter(currentChapter);
		setTiles(stringMap, currentMap, game, loadLevel);
	}
	
	/** Sets the tiles for our current Chapter Map given our chapter File
	 *  StringMap should follow tile conventions laid out in res//designInfo//tileIDs
	 * @param stringMap - map of the chapter as an arraylist of strings
	 * @param currentMap - map we are editing
	 * @param game
	 */
	public void setTiles(ArrayList<String[]> stringMap, ChapterMap currentMap, Game game, int loadLevel) {
		
		game.setLoadLevel(loadLevel);
		
		for (int i = 0; i < stringMap.get(0).length; i++) {
			for (int j = 0; j < stringMap.size(); j++) {
				
				String[] tileSprite = getBeginCode(stringMap.get(j)[i]).split(":");
				int spriteCode = 0;
				if (tileSprite.length > 1) spriteCode = Integer.parseInt(tileSprite[1]); 
				
				switch (tileSprite[0]) {
				

				case "0": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new GrassTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "1": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new TreeTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "2": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new WallTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "3": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new MountainTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "4": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new WaterTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "5": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new Village(i, j, currentMap, new Vulnery()), spriteCode);
						  break;
				case "6": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new FloorTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "7": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new Throne(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "8": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new PillarTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "9": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new StairsTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
				  		  break;
				case "11": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new MudTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "12": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new ChestTile(i, j, currentMap, null), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
						  break;
				case "13": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new FortTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
				  		  break;
				case "14": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new BridgeTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
		  		  		  break;
				case "15": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new DoorTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j,  currentChapter), this, spriteCode);
		  		  		  break;
				case "16": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new ArmoryTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
		  		  		  break;
				case "17": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new VendorTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
		  		  		  break;
				case "18": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new DamagedWallTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, currentChapter), this, spriteCode);
		  		  		  break;
				}
			}
		}
	}
	/**Returns tile information from the stringMap*/
	public static String getBeginCode(String code) {
		if (code.isEmpty() || code == null) return "";
		char c;
		for (int i = 0; i < code.length(); i++) {
			c = code.charAt(i);
			if (c == '-') return code.substring(0, i);
		}
		return code;
	}

	/**Returns tile information from the stringMap*/
	public static String getEndCode(String code) {
		if (code.isEmpty() || code == null) return "";
		char c;
		for (int i = 0; i < code.length(); i++) {
			c = code.charAt(i);
			if (c == '-') return code.substring(i+1);
		}
		return code;
	}
	/** Adds an amount to our Game's gold, ensuring it does not fall below zero */
	public void incGameGold(int amount) {
		this.gameGold += amount;
		if (gameGold < 0) gameGold = 0;
	}
	
	public void tick() {
		
		if (currentMap == null) return;		
		
		if (game.gameState == STATE.EnemyPhase) {

			long last = System.currentTimeMillis();
			if (last - this.enemyPhaseTimer >= ENEMY_RUN_SPEED*6) {
				enemyPhaseTimer = System.currentTimeMillis();
				handleEnemyTurn();
			}

			return;
		}
		
		if (game.gameState == STATE.GainEXP || game.gameState == STATE.LevelUp) game.levelUpLoader.tick();
		//if (allys.size() == 0 && enemys.size() == 0) return;
		currentMap.tick();
		for (int i = 0; i < allys.size(); i++) {
			allys.get(i).tick();
		}
		for (int i = 0; i < enemys.size(); i++) {
			enemys.get(i).tick();
		}
		if (noMAU(currentMap.currentPhase)) currentMap.nextPhase();
		
	}
	
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		if (currentMap == null) return;
		currentMap.render(g);
		if (game.gameState == STATE.EnemyChoice) {
			enemyMove.renderEnemyChoice(g);
		}
		
	}
	
	/** Returns the Lord of the current Game, first player in ally list if no lord present */
	public AllyPlayer getLord() {
		for (int i = 0; i < allys.size(); i++) {
			if (allys.get(i).name.equalsIgnoreCase("Ike")) {
				return allys.get(i);
			}
		}
		return allys.get(0);
	}
	/** Returns a list of all units range tiles away from the source and of opposite team of source*/
	public ArrayList<Player> listOfOpposingUnitsInRange(Player source, int range) {
		ArrayList<Player> list = new ArrayList<>();
		if (source.isAlly()) {
			for (int i = 0; i < enemys.size(); i++) {
				if (game.getTrueDist(enemys.get(i).currentTile, source.currentTile) <= range) {
					if (source.equals(enemys.get(i))) continue;
					list.add(enemys.get(i));
				}
			}
		} else {
			for (int i = 0; i < allys.size(); i++) {
				if (game.getTrueDist(allys.get(i).currentTile, source.currentTile) <= range) {
					if (source.equals(allys.get(i))) continue;
					list.add(allys.get(i));
				}
			}
		}
		return list;
	}
	
	private void handleEnemyTurn() {
		// get next enemy with movement 
		
		EnemyPlayer next = getNextEnemyWithMove();
		
		// if the next player is null, enemies are done moving
		if (next == null) {
			enemyMove.nextPlayer = null;
			// bring chapter to next turn
			currentMap.nextTurn();
			// bring cursor to an ally
			currentMap.findAllyWithMoves();
			// if not a cut scene or the game being over, set it to Game State
			if (!currentMap.inCutScene && !currentMap.chapterOver) game.setGameState(STATE.Game);
			return;
		}
		// remaining path of player
		ArrayList<Tile> path = enemyMove.enemyMoveMap.get(next);
		// if path does not exist in two ways, null or empty
		if (path == null) {
			// look for kills and set MAUT to false
			lookForKills(next);
			next.setMAUT(false);
			currentMap.setCurrentTile(next.currentTile);
			currentMap.findRegion();
			// sleep for a little
	
			long last = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while ((now - last) < 2*ENEMY_STOP_SPEED) {
				now = System.currentTimeMillis();
			}
		} else if (path.isEmpty()) {
			// same as above
			lookForKills(next);
			next.setMAUT(false);
			currentMap.setCurrentTile(next.currentTile);
			currentMap.findRegion();
			long last = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while ((now - last) < 2*ENEMY_STOP_SPEED) {
				now = System.currentTimeMillis();
			}
		} else {
			// we have a path, get the next tile and start the next enemy move
			Tile nextTile = path.get(0);
			enemyMove.startTurn(next, nextTile);
		}
	}
	
	/** Checks if Main Lord is dead, if he is not then the game is not over */
	public void checkForLoss() {
		boolean allDead = true;
		if (allys.size() == 0) {
			game.loseGame();
			return;
		}
		if (game.onHectorMode) {
			// check if Hector has died
			for (int i = 0; i < this.totalAllies.size(); i++) {
				AllyPlayer ally = totalAllies.get(i);
				if (ally.name.equalsIgnoreCase("Hector")) {
					if (ally.dead || ally.currentHP <= 0) {
						game.loseGame();
						return;
					}
				}
			}
		} else {
			// check if Ike has died
			for (int i = 0; i < this.totalAllies.size(); i++) {
				AllyPlayer ally = totalAllies.get(i);
				if (ally.name.equalsIgnoreCase("Ike")) {
					if (ally.dead || ally.currentHP <= 0) {
						game.loseGame();
						return;
					}
				}
			}
		}
		
		for (int i = 0; i < this.totalAllies.size(); i++) {
			AllyPlayer ally = totalAllies.get(i);
			if (ally.currentHP > 0) allDead = false;
		}
		if (allDead) {
			System.out.println("All Ally Units have died.. game over");
			game.loseGame();
		}
	
	}
	/** Returns a list of all Ally Players who are selectable in the battle preparations menu */
	public ArrayList<AllyPlayer> getPlayersForBattlePreparation() {
		ArrayList<AllyPlayer> list = new ArrayList<>();
		if (game.onHectorMode) {
			for (int i = 0; i < totalAllies.size(); i++) {
				if (totalAllies.get(i).name.equalsIgnoreCase("Hector")) {
					list.add(totalAllies.get(i));
					break;
				}
			}
			for (int i = 0; i < totalAllies.size(); i++) {
				AllyPlayer player = totalAllies.get(i);
				if (!player.dead && !player.isConfessed && !player.spawnsOnChaptStart) {
					if (!player.name.equalsIgnoreCase("Hector")) list.add(player);
				}
			}
		} else {
			for (int i = 0; i < totalAllies.size(); i++) {
				if (totalAllies.get(i).name.equalsIgnoreCase("Ike")) {
					list.add(totalAllies.get(i));
					break;
				}
			}
			for (int i = 0; i < totalAllies.size(); i++) {
				AllyPlayer player = totalAllies.get(i);
				if (!player.dead && !player.isConfessed && !player.spawnsOnChaptStart) {
					if (!player.name.equalsIgnoreCase("Ike")) list.add(player);
				}
			}
		}
		return list;
	}
	/** Adds an ally to our game regardless of conditions */
	public void directlyAddAlly(AllyPlayer ally) {
		if (ally == null) return;
		boolean isPresent = false;
		for (int i = 0; i < totalAllies.size(); i++) {
			if (totalAllies.get(i).name.equalsIgnoreCase(ally.name)) {
				totalAllies.remove(i);
				totalAllies.add(ally);
				isPresent = true;
				break;
			}
		}
		if (!isPresent) {
			if (!ally.isConfessed) {
				if (!ally.dead) {
					totalAllies.add(ally);
					if (!allys.contains(ally)) {
						allys.add(ally);
						currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(ally);
					}
				}
			}
		}
	}
	
	/** Adds an ally to our game if proper conditions have been met */
	public void addAlly(AllyPlayer ally) {
		if (ally == null) return;
		boolean isPresent = false;
		for (int i = 0; i < totalAllies.size(); i++) {
			if (totalAllies.get(i).name.equalsIgnoreCase(ally.name)) {
				totalAllies.remove(i);
				totalAllies.add(ally);
				isPresent = true;
				break;
			}
		}
		if (!isPresent) {
			if (!ally.isConfessed) {
				if (ally.recruitableUnit) {
					// if ally is a recruitable unit, check if they have been recruited this game
					if (playerHasBeenRecruited(ally.name)) {
						totalAllies.add(ally);
					} else {
						System.out.println("Cannot add " + ally.name + " because they were not recruited on their recruitment chapter");
						// if not, return so unit is not added to allys also
						if (ally.currentTile != null) {
							currentMap.getTileAtAbsolutePos(ally.currentTile.x, ally.currentTile.y).setCarrier(null);
						}
						return;
					}
				} else {
					// not recruitable unit, fine to be added to totalAllies
					totalAllies.add(ally);
				}
			}
		}
		
		
		if (!ally.dead) {
			if (!allys.contains(ally)) {
				allys.add(ally);
				currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(ally);
			} else {
				currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(null);
			}
		} else {
			currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(null);
		}
	}
	
	private boolean playerHasBeenRecruited(String allyName) {
		try {
			Scanner reader = new Scanner(recruitmentFile);
			String line;
			String[] lineParts;
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				lineParts = line.split(":");
				if (lineParts.length == 1) {
					if (line.equalsIgnoreCase(allyName)) {
						if (loadLevel == 1) {
							line = reader.nextLine();
							lineParts = line.split(":");
							reader.close();
							if (lineParts[1].equals("1")) return true;
							else return false;
						} else if (loadLevel == 2) {
							reader.nextLine();
							line = reader.nextLine();
							lineParts = line.split(":");
							reader.close();
							if (lineParts[1].equals("1")) return true;
							else return false;
						} else if (loadLevel == 3) {
							reader.nextLine();
							reader.nextLine();
							line = reader.nextLine();
							lineParts = line.split(":");
							reader.close();
							if (lineParts[1].equals("1")) return true;
							else return false;
						} else {
							System.err.println("LOAD LEVEL = " + loadLevel + "  Load Level should be 0 for new game/designer or 1-3 for save states!"
									+ "\nSRC: playerHasBeenRecruited() - ChapterOrganizer");
							reader.close();
							return true;	
						}
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/** Returns the closest number of tiles distance between the closest enemy and the specified ally 
	 *  also looks for Damaged Wall Tiles and takes their distance into consideration
	 *  zero if there are no enemies around*/
	public int closestEnemyDistance(Player ally) {
		if (ally == null) return 0;
		int lowest = 10000;
		for (int i = 0; i < enemys.size(); i++) {
			int dist = game.getTrueDist(enemys.get(i).currentTile, ally.currentTile);
			if (dist < lowest) lowest = dist;
		}
		
		for (int i = 0; i < currentMap.tiles.size(); i++) {
			Tile t = currentMap.tiles.get(i);
			if (t.category.equalsIgnoreCase("DamagedWall")) {
				int dist = game.getTrueDist(t, ally.currentTile);
				if (dist < lowest) lowest = dist;
			}
		}
		
		if (lowest == 10000) return 0;
		else return lowest;
	}
	/** Returns the closest number of tiles distance between the enemy and the closest ally */
	public int closestAllyDistance(Player enemy) {
		if (enemy == null) return 0;
		int lowest = 10000;
		for (int i = 0; i < allys.size(); i++) {
			int dist = game.getTrueDist(allys.get(i).currentTile, enemy.currentTile);
			if (dist < lowest) lowest = dist;
		}
		if (lowest == 10000) return 0;
		else return lowest;
	}
	/** Removes all stat buffs from every ally, should be used at start and end of chapters */
	public void removeAllStatBuffs() {
		for (int i = 0; i < allys.size(); i++) {
			allys.get(i).nullStatBuffs();
		}
	}
	/** Decreases the stat buff duration of each ally, does nothing if ally has no stat buffs 
	 *  should be used after each turn completes*/
	public void decAllyStatBuffs() {
		for (int i = 0; i < allys.size(); i++) {
			allys.get(i).decreaseStatBuffTurns();
		}
	}
	
	public void removeAlly(AllyPlayer ally) {
		if (ally == null) return;
		currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(null);
		allys.remove(ally);
	}
	public void removeEnemy(EnemyPlayer enemy) {
		if (enemy == null) return;
		currentMap.getTileAtAbsolutePos(enemy.xPos, enemy.yPos).setCarrier(null);
		enemys.remove(enemy);
	}
	public void addEnemy(EnemyPlayer enemy) {
		if (enemy == null) return;
		enemys.add(enemy);
		currentMap.getTileAtAbsolutePos(enemy.xPos, enemy.yPos).setCarrier(enemy);
	}
	public void removePlayer(Player player) {
		if (player == null) return;
		if (player.teamID.equalsIgnoreCase("Ally")) removeAlly((AllyPlayer) player);
		else removeEnemy((EnemyPlayer)player);
	}
	public void addPlayer(Player player) {
		if (player == null) return;
		if (player.teamID.equalsIgnoreCase("Ally")) addAlly((AllyPlayer) player);
		else addEnemy((EnemyPlayer)player);
	}
	/**
	 * Evaluates each player depending on the phase passed in, if every player has no Move Attack or Uses, returns true
	 * @param phaseID the team phase
	 * @return
	 */
	public boolean noMAU(String phaseID) {
		int count = 0;
		if (phaseID.equalsIgnoreCase("AllyPhase")) {
			for (int i = 0; i < allys.size(); i++) {
				if (!allys.get(i).canMA()) count++;
			}
			return (count == allys.size());
		} else if (phaseID.equalsIgnoreCase("EnemyPhase")) {
			for (int i = 0; i < enemys.size(); i++) {
				if (!enemys.get(i).canMA()) count++;
			}
			return (count == enemys.size());
		}
		return false;
	}
	/**
	 * Enemy searches around him based on his equipped item's range, if a unit is in range he will Attack
	 * True if an ally is found
	 * @param enemy
	 */
	public boolean lookForKills(EnemyPlayer enemy) {
		if (!enemy.canAttack) return false;
		if (enemy.isHealer()) {
			if (!enemy.duoWeaponHeal) return false;
		}
		// loop through allies and find the shortest range an ally is from us, as well as which ally
		// if two allies are the same shortest range, ally with lowest current HP
		AllyPlayer closest = null;
		int closestRange = 100000;
		for (int i = 0; i < allys.size(); i++) {
			if (allys.get(i).isBeingCarried) continue;
			AllyPlayer ally = allys.get(i);
			int dist = game.getTrueDist(ally.currentTile, enemy.currentTile);
			if (dist < closestRange) {
				closestRange = dist;
				closest = ally;
			} else if (dist == closestRange) {
				// the distance between them is the same
				if (closest != null) {
					
					if (ally.currentHP < closest.currentHP) {
						closest = ally;
					} else if (ally.currentHP == closest.currentHP) {
						if ((ally.DEF + ally.RES)/2 < (closest.DEF + closest.RES)/2) closest = ally;
					}
					
				} else closest = ally;
			}
		}
		// we have the closest and lowest health ally
		// loop through items to find strongest damage item for range provided
		if (closest == null) return false;
		if (closestRange > enemy.maxCombatItemRange()) return false;
		CombatItem strongest = null;
		int mostDMG = 0;
		for (int i = 0; i < enemy.wallet.weapons.size(); i++) {
			CombatItem item = enemy.wallet.weapons.get(i);
			if (!item.isHealingItem()) {
				if (item.range >= closestRange) {
					if (item.damage > mostDMG) {
						strongest = item;
						mostDMG = item.damage;
					} else if (item.damage == mostDMG) {
						if (strongest != null) {
							if (item.hit > strongest.hit) strongest = item;
						} else strongest = item;
					}
				}
			}
		}
		// now we have the strongest item within our range
		if (strongest != null) {
			if (game.getTrueDist(enemy.currentTile, closest.currentTile) <= strongest.range) {
				enemy.wallet.equipt(strongest);
				long last = System.currentTimeMillis();
				long now = System.currentTimeMillis();
				while ((now - last) < 90) {
					now = System.currentTimeMillis();
				}
				enemyMove.setEnemyChoice(closest);
				return true;
			} else return false;
		} else return false;
		
	}

/*	public boolean lookForKills(EnemyPlayer enemy) {
		if (!enemy.canAttack) return false;
		if (enemy.isHealer()) {
			if (!enemy.duoWeaponHeal) return false;
		}
		for (int i = 0; i < allys.size(); i++) {
			if (allys.get(i).isBeingCarried) continue;
			AllyPlayer ally = allys.get(i);
			if (game.getTrueDist(ally.currentTile, enemy.currentTile) <= enemy.equiptItem.range) {
				long last = System.currentTimeMillis();
				long now = System.currentTimeMillis();
				while ((now - last) < 90) {
					now = System.currentTimeMillis();
				}
				this.enemyMove.setEnemyChoice(ally);			
				return true;
			}
		}
		
		return false;
	}*/
	/** Transforms an enemy player into a permanent AllyPlayer, from talking to them */
	public void transformEnemyIntoAlly(Player enemy) {
		if (enemy.name.equalsIgnoreCase("Merric")) {
			enemy.die();
			directlyAddAlly(new Merric(enemy.currentTile.x, enemy.currentTile.y, game, currentChapter));
			currentMap.setInGameCutScene(9);
		} else if (enemy.name.equalsIgnoreCase("Shinnon")) {
			enemy.die();
			directlyAddAlly(new Shinnon(enemy.currentTile.x, enemy.currentTile.y, game, currentChapter));
			currentMap.setInGameCutScene(9);
		} else if (enemy.name.equalsIgnoreCase("Dorcas")) {
			enemy.die();
			directlyAddAlly(new Dorcas(enemy.currentTile.x, enemy.currentTile.y, game, currentChapter));
			currentMap.setInGameCutScene(9);
		}
	}
	/** Sets all recruitment data to false, for a new game */
	public void nullRecruitmentData() {
		ArrayList<String> data = new ArrayList<>();
		try {
			Scanner reader = new Scanner(recruitmentFile);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String[] lineParts = line.split(":");
				if (lineParts.length==1) {
					data.add(line);
				} else {
					data.add(lineParts[0] + ":0");
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			
		}
		try {
			PrintWriter writer = new PrintWriter(recruitmentFile);
			for (int i = 0; i < data.size(); i++) {
				writer.println(data.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/** Sets the data for recruiting certain allies by talking to them 
	 *  @param enemyName - the enemy who we are changing recruitment info for
	 *  @param on - True if we are recruiting them, false if we are resetting the recruitment*/
	public void setRecruitmentData(String enemyName, boolean on) {

		ArrayList<String> data = new ArrayList<>();
		try {
			Scanner reader = new Scanner(recruitmentFile);
			if (on) {
				// setting recruitment data to true
				while (reader.hasNextLine()) {
					String line = reader.nextLine();
					String[] lineParts = line.split(":");
					if (lineParts.length == 1) {
						if (line.equalsIgnoreCase(enemyName)) {
							// we are on the right name
							data.add(line);
							if (this.loadLevel == 1) {
								reader.nextLine();
								data.add(enemyName + ":1");
								data.add(reader.nextLine());
								data.add(reader.nextLine());
							} else if (loadLevel == 2) {
								data.add(reader.nextLine());
								reader.nextLine();
								data.add(enemyName + ":1");
								data.add(reader.nextLine());
							} else if (loadLevel == 3) {
								data.add(reader.nextLine());
								data.add(reader.nextLine());
								reader.nextLine();
								data.add(enemyName + ":1");
							} else {
								data.add(reader.nextLine());
								data.add(reader.nextLine());
								data.add(reader.nextLine());
							}
						} else data.add(line);
					} else data.add(line);
			
				}
			} else {
				// we are setting recruitment data to FALSE (0)
				while (reader.hasNextLine()) {
					String line = reader.nextLine();
					String[] lineParts = line.split(":");
					if (lineParts.length == 1) {
						if (line.equalsIgnoreCase(enemyName)) {
							// we are on the right name
							if (this.loadLevel == 1) {
								reader.nextLine();
								data.add(enemyName + ":0");
								data.add(reader.nextLine());
								data.add(reader.nextLine());
							} else if (loadLevel == 2) {
								data.add(reader.nextLine());
								reader.nextLine();
								data.add(enemyName + ":0");
								data.add(reader.nextLine());
							} else if (loadLevel == 3) {
								data.add(reader.nextLine());
								data.add(reader.nextLine());
								reader.nextLine();
								data.add(enemyName + ":0");
							} else {
								data.add(reader.nextLine());
								data.add(reader.nextLine());
								data.add(reader.nextLine());
							}
						} else data.add(line);
					} else data.add(line);
			
				}
				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// now we write our data to the file
		try {
			PrintWriter writer = new PrintWriter(recruitmentFile);
			for (int i = 0; i < data.size(); i++) {
				writer.println(data.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/** True if we are 1 space away from a unit we can talk to to get them onto our team */
	public boolean nextToTalkedToUnit(Player source) {
		for (int i = 0; i < enemys.size(); i++) {
			Player enemy = enemys.get(i);
			if (game.getTrueDist(enemy.currentTile, source.currentTile) <= 1) {
				if (enemy.talkedToUnit) {
					if (enemy.name.equalsIgnoreCase("Merric")) {
						if (source.name.equalsIgnoreCase("Nino")) return true;
					} else if (enemy.name.equalsIgnoreCase("Shinnon")) {
						if (source.name.equalsIgnoreCase("Wolf")) return true;			
					} else if (enemy.name.equalsIgnoreCase("Dorcas")) {
						if (source.name.equalsIgnoreCase("Ike")) return true;
					}
				}
			}
		}
		return false;
	}
	
	public ArrayList<AllyPlayer> getAlliesWithMove() {
		ArrayList<AllyPlayer> alliesWMove = new ArrayList<>();
		for (int i = 0; i < allys.size(); i++) {
			if (allys.get(i).canMove) alliesWMove.add(allys.get(i));
		}
		return alliesWMove;
	}
	
	/** Returns the next EnemyPlayer that has not moved yet this turn */
	public EnemyPlayer getNextEnemyWithMove() {
		EnemyPlayer player;
		if (enemys.isEmpty()) {
			game.setGameState(STATE.Game);
			return null;
		}
		for (int i = 0; i < enemys.size(); i++) {
			player = enemys.get(i);
			if (player.canMove) return player;
			
		}
		return null;
	}
	
	/** Provides a list of all enemys on the map that have not moved yet this turn */
	public ArrayList<EnemyPlayer> getEnemiesWithMove() {
		ArrayList<EnemyPlayer> enemyList = new ArrayList<>();
		for (int i = 0; i < enemys.size(); i++) {
			EnemyPlayer enemy = enemys.get(i);
			if (enemy.canMove) enemyList.add(enemy);
		}
		return enemyList;
	}
	
	public void setVillages() {
		ArrayList<Village> villages = currentMap.getAllVillages();
		if (villages.isEmpty()) {
		}
		else {
			ArrayList<Item> villageItems = getVillageItems();
			if (villageItems.isEmpty()) return;
			for (int i = 0; i < villages.size(); i++) {
				villages.get(i).gift = villageItems.get(i % villageItems.size());
			}
		}
	}
	/** Returns a list of Items this chapter has in its villages as referenced by the File:
	 *  res//chapters//chapterVillages -- items not included in this file will not be added
	 * @return
	 */
	public ArrayList<Item> getVillageItems() {
		ArrayList<Item> items = new ArrayList<>();
		try {
			Scanner reader = new Scanner(new File("res//chapters//chapterVillages"));
			String line = "";
			String[] chap;
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				if (line.contains(":")) {
					chap = line.split(":");
					if (Integer.parseInt(chap[1]) == currentChapter) {
						line = reader.nextLine();
						while (line != null && !line.isEmpty() && !line.contains(":")) {
							if (line.equalsIgnoreCase("none")) break;
							Item item = Item.getItemByName(line);
							if (item.name.equalsIgnoreCase("Key")) {
								Key keyItem = (Key) item;
								keyItem.setGame(game);
								items.add(keyItem);
							} else {
								items.add(item);
							}
							try {
								line = reader.nextLine();
							} catch (NoSuchElementException e1) {
								break;
							}
						}
						break;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		return items;
	}
	/** True if the enemies have a boss unit */
	public boolean containsBoss() {
		for (int i = 0; i < enemys.size(); i++) {
			if (enemys.get(i).isBoss) return true;
		}
		return false;
	}
	/** Returns true if there is an unoccupied tile 1 space away from source */
	public boolean emptyTileAdjacentFromSource(Tile source) {
		if (currentMap.getTileAtAbsolutePos(source.x-1, source.y) != null) {
			if (!currentMap.getTileAtAbsolutePos(source.x-1, source.y).isOccupied()) return true;
		}
		if (currentMap.getTileAtAbsolutePos(source.x+1, source.y) != null) {
			if (!currentMap.getTileAtAbsolutePos(source.x+1, source.y).isOccupied()) return true;
		}
		if (currentMap.getTileAtAbsolutePos(source.x, source.y-1) != null) {
			if (!currentMap.getTileAtAbsolutePos(source.x, source.y-1).isOccupied()) return true;
		}
		if (currentMap.getTileAtAbsolutePos(source.x, source.y+1) != null) {
			if (!currentMap.getTileAtAbsolutePos(source.x, source.y+1).isOccupied()) return true;
		}
		return false;
	}
	
	/** Returns a list of all Players on the same team as and adjacent to the source */
	public ArrayList<Player> getAdjacentAllies(Player source) {
		ArrayList<Player> adjPlayers = new ArrayList<>();
		
		Tile temp = currentMap.getTileAtAbsolutePos(source.currentTile.x-1, source.currentTile.y);
		if (temp != null) {
			if (temp.isOccupied()) {
				if (temp.carrier.sameTeam(source)) adjPlayers.add(temp.carrier);
			}
		}
		temp = currentMap.getTileAtAbsolutePos(source.currentTile.x+1, source.currentTile.y);
		if (temp != null) {
			if (temp.isOccupied()) {
				if (temp.carrier.sameTeam(source)) adjPlayers.add(temp.carrier);
			}
		}
		temp = currentMap.getTileAtAbsolutePos(source.currentTile.x, source.currentTile.y-1);
		if (temp != null) {
			if (temp.isOccupied()) {
				if (temp.carrier.sameTeam(source)) adjPlayers.add(temp.carrier);
			}
		}
		temp = currentMap.getTileAtAbsolutePos(source.currentTile.x, source.currentTile.y+1);
		if (temp != null) {
			if (temp.isOccupied()) {
				if (temp.carrier.sameTeam(source)) adjPlayers.add(temp.carrier);
			}
		}
		
		return adjPlayers;
		
	}
	
	public ChapterMap getCurrentMap() {
		return currentMap;
	}
	
	public int numEnemiesLeft() {
		return enemys.size();
	}
}

