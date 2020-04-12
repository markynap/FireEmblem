package gameMain;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import characters.AllyPlayer;
import characters.EnemyPlayer;
import characters.Ike;
import characters.Player;
import extras.TimeKeeper;
import gameMain.Game.STATE;
import items.Vulnery;
import tiles.*;

public class ChapterOrganizer {

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

	public ArrayList<String[]> stringMap;
	
	public Scanner reader;
	
	public PrintWriter writer;
	/** Which Save-State are we in */
	public int loadLevel;
	
	public TimeKeeper timekeep;
	
	/** Creates a brand new Chapter Organizer with no enemies or allies */
	public ChapterOrganizer(Game game, ChapterMap map, int loadLevel) {
		this.game = game;
		game.setLoadLevel(loadLevel);
		allys = new LinkedList<>();
		enemys = new LinkedList<>();
		currentChapter = 1;
		currentMap = map;
		timekeep = new TimeKeeper();
		timekeep.startSession();
		this.loadLevel = loadLevel;
		
	}
	/** Creates a Chapter Organizer instance with a list of allies and enemies already set */
	public ChapterOrganizer(Game game, int currentChapter, int loadLevel) {
		this.game = game;
		game.setLoadLevel(loadLevel);
		timekeep = new TimeKeeper();
		timekeep.startSession();
		this.allys = new LinkedList<>();
		this.enemys = new LinkedList<>();
		this.currentChapter = currentChapter;
		this.loadLevel = loadLevel;
		try {
			this.reader = new Scanner(new File("res\\chapters\\chapter" + currentChapter));
		} catch (FileNotFoundException e) {
			System.out.println("could not find " + "res\\chapters\\chapter" + currentChapter);
		}
		setCurrentMap();
	}

	/** Progresses the game to the next chapter location */
	public void nextChapter(int saveLevel) {
		//BEFORE I NULL THE MAP I NEED TO LOOP THROUGH ALLIES AND UPDATE STATS!
		updatePlayers(saveLevel);
		currentMap = null;
		currentChapter++;
		game.setPopUpMenu(null);
		try {
			this.reader = new Scanner(new File("res\\chapters\\chapter" + currentChapter));
		} catch (FileNotFoundException e) {
			System.out.println("could not find " + "res\\chapters\\chapter" + currentChapter);
		}
		this.loadLevel = saveLevel;
		game.setLoadLevel(saveLevel);
		setCurrentMap();
	}
	/** Saves player stats into their unique player files at the end of chapter */
	public void updatePlayers(int saveLevel) {

		AllyPlayer ally;
		String filename;
		File file;
		ArrayList<String> fileContents = new ArrayList<>();
		
		for (int i = 0; i < allys.size(); i++) {
		
			ally = allys.get(i);
			filename = "res//characters//" + ally.name;
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
						if (ally.currentHP > 0) fileContents.add("1"); // alive
						else fileContents.add("0"); // dead
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
	
	/** Sets the current Map based on the information obtained by the reader */
	public void setCurrentMap() {
		String[] stringLine;
		stringMap = new ArrayList<>();
		while (reader.hasNextLine()) {
			stringLine = reader.nextLine().split(" ");
			stringMap.add(stringLine);
		}
		currentMap = new ChapterMap(stringMap.get(0).length, stringMap.size(), game);
		setTiles(stringMap, currentMap, game, loadLevel);
	}
	
	/** Sets the tiles for our current Chapter Map given our chapter File
	 *  StringMap should follow tile conventions laid out in res//designInfo//tileIDs
	 * @param stringMap - map of the chapter as an arraylist of strings
	 * @param currentMap - map we are editing
	 * @param game
	 */
	public void setTiles(ArrayList<String[]> stringMap, ChapterMap currentMap, Game game, int loadLevel) {
		for (int i = 0; i < stringMap.get(0).length; i++) {
			for (int j = 0; j < stringMap.size(); j++) {
				
				switch (getBeginCode(stringMap.get(j)[i])) {
				
				case "0": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new GrassTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "1": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new TreeTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "2": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new WallTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "3": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new MountainTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "4": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new WaterTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "5": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new Village(i, j, currentMap, new Vulnery()));
						  break;
				case "6": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new FloorTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "7": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new Throne(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
						  break;
				case "8": currentMap.setTile(currentMap.getTileAtAbsolutePos(i, j), new PillarTile(i, j, currentMap), game.getPlayerByID(Integer.parseInt(getEndCode(stringMap.get(j)[i])), i, j, loadLevel), this);
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
	
	public void tick() {

		if (currentMap == null) return;
		//if (allys.size() == 0 && enemys.size() == 0) return;
		currentMap.tick();
		for (int i = 0; i < allys.size(); i++) {
			allys.get(i).tick();
		}
		for (int i = 0; i < enemys.size(); i++) {
			enemys.get(i).tick();
		}
		if (noMAU(currentMap.currentPhase)) currentMap.nextPhase();
		if (timekeep.sessionAtDesiredTime(40)) checkForLoss();
	}
	
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		if (currentMap == null) return;
		currentMap.render(g);
	//	Tiles will render each unit, for screen moving purposes 
	}
	/** Checks if there are no allys left in the game */
	public void checkForLoss() {
		if (game.gameState == STATE.Game) {
			if (allys.size() == 0) {
				game.loseGame();
			}
		}
	}
	
	public void addAlly(AllyPlayer ally) {
		if (ally == null) return;
		allys.add(ally);
		currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(ally);
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
		if (player.teamID.equalsIgnoreCase("Ally")) removeAlly((AllyPlayer) player);
		else removeEnemy((EnemyPlayer)player);
	}
	public void addPlayer(Player player) {
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
	
}

