package characters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import gameMain.Game;
import gameMain.Game.DIFFICULTY;
import items.CombatItem;
import items.Convoy;
import items.Fists;
import items.HealingItem;
import items.Item;
import items.Key;
import items.Wallet;
import tiles.Tile;
/**
 * A player in this game
 * @author mark
 *
 */
public class Player {
	/** how much each mastery field increases each use */
	public final int masteryIncrease = 5;
	/**List of stat names*/
	public static String[] StatNames = {"HP", "STR", "SK", "SP", "LCK", "DEF", "RES", "MOV", "CON", "Level"};
	/** List of Magic Stat Names */
	public static String[] MagStatNames = {"HP", "MAG", "SK", "SP", "LCK", "DEF", "RES", "MOV", "CON", "Level"};
	/**An instance of this game*/
	public Game game;
	/**The name of this player*/
	public String name;
	/**The class of this player*/
	public String Class;
	/** The tile this player is standing on */
	public Tile currentTile;
	/** This player's wallet to hold their items */
	public Wallet wallet;
	/** HP, STR, SK, SP, LUCK, DEF, RES, MOV, CON, LEVEL*/
	public int[] stats;
	/** HP, STR, SK, SP, LUCK, DEF, RES */
	public int[] growths;
	/**The stats broken up*/
	public int HP, STR, SK, SP, LCK, DEF, RES, MOV, CON, level;
	/**The picture representing this player*/
	public Image image;
	/**Current position of this player on the grid*/
	public int xPos, yPos;
	/**The current HP of this player*/
	public int currentHP;
	/**Item this player has equipped, null if none in wallet*/
	public CombatItem equiptItem;
	/** This player's EXP, player levels up if it surpasses 100 */
	public int EXP;
	/** Swords, Lances, Axes, Bows  --> the letter grade*/
	public char[] weaponMasteriesGrade;
	/** Swords, Lances, Axes, Bows --> how many uses */
	public int[] weaponMasteries;
	/** The integer that tracks how long till next upgrade for each type*/
	public final int weaponUpgrade = 200;
	/** Responsible for moving, attacking, and using items*/
	public boolean canMove, canAttack, canUse, canTrade, canUseSkill;
	/**This is true when this player is being looked at for attack by another player*/
	public boolean drawScope = false;
	/**String that separates an Ally from Enemy from NPC */
	public String teamID;
	/** Color representing each team */
	public Color teamColor;
	/** tracks the most recent level ups */
	public boolean[] levelUps;
	/** RNG */
	public Random r;
	/** Number of lines taken up in Player File per save */
	public final int NUM_LINES = 27;
	/** helps for ID sake */
	public boolean isBoss;
	/** True if this unit can fly over impassable terrain */
	public boolean isFlier;
	/** Indicates whether an ally unit is dead or not */
	public boolean dead;
	/** The previous tile this unit has stood on */
	public Tile previousTile;
	/** The unique Skill belonging to each player */
	public Skill skill;
	/** True if this player can Heal/Dance/heal and use weapons */
	public boolean isHealer, isDancer, duoWeaponHeal;
	/** True if this unit promoted from another class */
	public boolean isPromoted;
	/** The amount greater than the range of the staff that a player can heal/use bows from */
	public int staffExtention, bowExtention;
	/** The Player this player is carrying, null if none exists */
	public Player playerCarried;
	/** Helps understand logic behind carrying/dropping units */
	public boolean isBeingCarried, isCarryingUnit;
	/** True if this is a confessed enemy unit fighting for us */
	public boolean isConfessed;
	/** True if this player uses magic, false if this player uses physical weapons */
	public boolean isMagicUser;
	/** Buffs from Pure Water and Ally Skills that Raise STR, DEF, RES, MOV in that order */
	private int[] statBuffs;
	/** Number of turns that are left until the stat buffs run out */
	private int statBuffTurnsLeft;
	/** True if this player has their stats buffed by Skills or Utility Items */
	private boolean hasStatBuffs;
	/** Extra range a player can cast confession from */
	public int confessionRange;
	/** True if this unit can be talked to to turn onto the Ally Team */
	public boolean talkedToUnit;
	/** True if this unit needed to be talked to in order to be on our team */
	public boolean recruitableUnit;
	/** True if this unit spawns on the start of the chapter and cannot be traded with or selected for deployment in battle preparations */
	public boolean spawnsOnChaptStart;
	/** Which Country/Region this player originates from */
	public String race;
	
	/** Constructor for making an ally player */
	public Player(String name, String Class, int xPos, int yPos, Game game) {
		this.game = game;
		this.name = name;
		r = new Random();
		this.Class = Class;
		this.xPos = xPos;
		this.yPos = yPos;
		this.teamID = "Ally";
		this.currentTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(xPos, yPos);
		this.previousTile = currentTile;
		wallet = new Wallet(this);
		canMove = true;
		canAttack = true;
		canUse = true;
		canTrade = true;
		canUseSkill = true;
		levelUps = new boolean[7];
		weaponMasteriesGrade = new char[4];
		weaponMasteries = new int[4];
		setAllyStats();
		this.currentHP = HP;
		statBuffs = new int[4];
		if (dead) {
			game.deadPlayers.add(this);
		}
		if (this.level >= 20) this.isPromoted = true;
		applyModeBonus();
		repOk();
	}
	/** Constructor for making an enemy player */
	public Player(String name, String Class, String teamID, int[] stats, int[] growths, Game game, int xPos, int yPos, CombatItem equiptItem, boolean isPromoted) {
		this.game = game;
		this.name = name;
		r = new Random();
		this.isPromoted = isPromoted;
		this.Class = Class;
		this.xPos = xPos;
		this.yPos = yPos;
		this.teamID = teamID;
		this.currentTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(xPos, yPos);
		this.previousTile = currentTile;
		wallet = new Wallet(this);
		this.equiptItem = equiptItem;
		setMasteryBonuses();
		setEnemyStats(stats, growths);
		if (equiptItem != null) {
			wallet.weapons.add(equiptItem);
		} else {
			wallet.weapons.add(new Fists());
		}
		EXP = 0;
		currentHP = stats[0];
		statBuffs = new int[4];
		canMove = true;
		canAttack = true;
		canUse = true;
		canUseSkill = true;
		levelUps = new boolean[growths.length];
		applyModeBonus();
		repOk();
	}
	/** Constructor for turning an enemy into an ally via confession */
	public Player(int[] stats, int xPos, int yPos, Game game, Wallet wallet) {
		this.game = game;
		this.stats = stats;
		this.growths = new int[7];
		for (int i = 0; i < 7; i++) {
			growths[i] = 35;
		}
		r = new Random();
		this.name = "ConfessedUnit";
		this.Class = "Confessed";
		this.xPos = xPos;
		this.yPos = yPos;
		setEnemyStats(stats, this.growths);
		this.teamID = "Ally";
		this.currentTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(xPos, yPos);
		this.previousTile = currentTile;
		this.wallet = wallet;
		this.equiptItem = wallet.getFirstWeapon();
		EXP = 0;
		currentHP = stats[0];
		statBuffs = new int[4];
		canMove = true;
		canAttack = true;
		canUse = true;
		canTrade = true;
		levelUps = new boolean[growths.length];
		this.skill = new Skill("Confessed");
		setMasteryBonuses();
		repOk();
	}
	
	private void applyModeBonus() {
		
		if (game.gameDifficulty == DIFFICULTY.Easy) return;
		
		if (teamID.equalsIgnoreCase("Ally")) {
			

			
		} else {
			
			if (game.gameDifficulty == DIFFICULTY.Normal) {
				for (int i = 0; i < growths.length; i++) {
					this.growths[i] += 5;		
				}
			} else if (game.gameDifficulty == DIFFICULTY.Hard) {
				for (int i = 0; i < growths.length; i++) {
					this.growths[i] += 10;
				}
				for (int i = 0; i < 7; i++) {
					stats[i]++;
				}
				
			} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
				for (int i = 0; i < growths.length; i++) {
					this.growths[i] += 15;
				}
				for (int i = 0; i < 7; i++) {
					stats[i]++;
				}
				this.stats[9]--;
				this.level--;
			}
			
		}
		this.currentHP = stats[0];
		setEnemyStats(stats, growths);
	}
	
	/** Carries a player, removing them from the battlefield */
	public void carryPlayer(Player player) {
		
		if (player == null) {
			this.playerCarried = null;
			this.isCarryingUnit = false;
			this.isBeingCarried = false;
			return;
		}
		
		this.playerCarried = player;
		player.currentTile.setCarrier(null);
		player.xPos = this.xPos;
		player.yPos = this.yPos;
		player.currentTile = this.currentTile;
		this.setMAUT(false);
		this.isCarryingUnit = true;
		playerCarried.isBeingCarried = true;
		
	}
	/**
	 * Sets the stat buffs for this player and the number of turns until it expires
	 * @param stats - STR DEF RES MOV
	 * @param numTurns - number of turns until stats expire
	 */
	public void setStatBuffs(int[] stats, int numTurns) {
		if (stats.length != 4) {
			System.err.println("STATS LENGTH != 4   Player.setStatBuffs(int[] stats)");
			return;
		}
		for (int i = 0; i < statBuffs.length; i++) {
			statBuffs[i] = stats[i];
		}
		this.hasStatBuffs = true;
		this.statBuffTurnsLeft = numTurns;
	}
	/** Decreases the number of turns stat buffs are applied for */
	public void decreaseStatBuffTurns() {
		if (!this.hasStatBuffs) return;
		this.statBuffTurnsLeft--;
		if (statBuffTurnsLeft <= 0) {
			nullStatBuffs();
		}
	}
	/** Removes all stat buffs from unit */
	public void nullStatBuffs() {
		this.statBuffTurnsLeft = 0;
		this.hasStatBuffs = false;
		this.statBuffs = new int[4];
	}
	
	/** Drops the carried player on the specified tile */
	public void dropCarriedPlayer(Tile dropTile) {
		if (playerCarried == null) return;
		
		playerCarried.setCurrentTile(dropTile);
		dropTile.setCarrier(playerCarried);
		this.xPos = dropTile.x;
		this.yPos = dropTile.y;
		this.setMAUT(false);
		playerCarried.setMAUT(false);
		playerCarried.isBeingCarried = false;
		this.playerCarried = null;
		this.isCarryingUnit = false;
	}
	
	/** Sets Ally's states based on the vales obtained in their res//characters// file */
	private void setAllyStats() {
		stats = new int[10];
		growths = new int[7];
		if (name.equalsIgnoreCase("SummonedUnit")) return;
		String filename = "res//res_characters//" + name;
		try {
			Scanner reader = new Scanner(new File(filename));
			String[] line;
			int inc = 0;
			int currentLoadLevel = -1;
			while (reader.hasNextLine()) {
				reader.nextLine();
				inc++;
				if (inc % NUM_LINES == 1) currentLoadLevel++;
				
				if (game.getLoadLevel() == currentLoadLevel) { // if we are at the correct LOAD LEVEL
					
					this.dead = (Integer.parseInt(reader.nextLine()) == 0);
					this.Class = reader.nextLine();
					this.skill = new Skill(reader.nextLine());
					
					for (int i = 0; i < 7; i++) {
						line = reader.nextLine().split(":"); //stats : growths
						stats[i] = Integer.valueOf(line[0]);
						growths[i] = Integer.valueOf(line[1]);
					}
					stats[7] = Integer.valueOf(reader.nextLine()); //MOV
					stats[8] = Integer.valueOf(reader.nextLine()); //CON
					stats[9] = Integer.valueOf(reader.nextLine()); //LEVEL
					EXP = Integer.valueOf(reader.nextLine());
					for (int i = 0; i < 4; i++) { // MOV CON LV EXP
						line = reader.nextLine().split(":");
						weaponMasteriesGrade[i] = line[0].charAt(0);
						weaponMasteries[i] = Integer.valueOf(line[1]);
					}
					//get items and durations
					for (int i = 0; i < 8; i++) {
						String newLine = reader.nextLine();
						if (newLine.isEmpty() || newLine.equalsIgnoreCase("none")) continue;
						line = newLine.split(":");
						Item item = Item.getItemByID(Integer.valueOf(line[0]));
						if (item == null) continue;
						if (item.name.equalsIgnoreCase("Key")) {
							Key key = (Key)item;
							key.setGame(game);
						}
						if (line.length > 1) {
							item.duration = Integer.valueOf(line[1]);
						}
						wallet.addItem(item);
						if (i == 0) { //first item specified is equipt Item
							if (item.getClass().asSubclass(CombatItem.class) != null) {
								this.equiptItem = (CombatItem)item;
							}
						}
					}
					
					setEnemyStats(stats, growths);
					reader.close();
					break;
				}
				
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** Returns and Creates the player at the given position with the given name, raymond if
	 *  name is not included in the names of players 
	 * @param name - player's name
	 * @param x - player xPosition
	 * @param y - player yPosition
	 * @param loadLevel - which save state we are loading from
	 * @return Player with matching ID, Raymond if otherwise
	 */
	public static Player getPlayerByName(String name, int x, int y, Game game, int whichChapter) {	
			
		switch (name) {
			case "Ike": return new Ike(x, y, game, whichChapter);
			case "Hector": return new Hector(x, y, game, whichChapter);
			case "Raymond": return new Raymond(x, y, game, whichChapter);
			case "Kent": return new Kent(x, y, game, whichChapter);
			case "Nino": return new Nino(x, y, game, whichChapter);
			case "Marcus": return new Marcus(x, y, game, whichChapter);
			case "Wolf": return new Wolf(x, y, game, whichChapter);
			case "Bard": return new Bard(x, y, game, whichChapter);
			case "Florina": return new Florina(x, y, game, whichChapter);
			case "Volke": return new Volke(x,y,game,whichChapter);
			case "Evelynn": return new Evelynn(x,y,game,whichChapter);
			case "Shinnon": return new Shinnon(x,y,game,whichChapter);
			case "Kahlan": return new Kahlan(x,y,game,whichChapter);
			case "Guy": return new Guy(x,y,game,whichChapter);
			case "Heath": return new Heath(x,y,game,whichChapter);
			case "Merric": return new Merric(x,y,game,whichChapter);
			case "Dorcas": return new Dorcas(x,y,game,whichChapter);
			case "Jetson": return new Jetson(x,y,game,whichChapter);
			case "Soren": return new Soren(x,y,game,whichChapter);
			case "Navarre": return new Navarre(x,y,game,whichChapter);
			case "Helga": return new Helga(x,y,game,whichChapter);
			case "Priscilla": return new Priscilla(x,y,game,whichChapter);
			case "Mason": return new Mason(x,y,game,whichChapter);
			// ENEMY UNITS
			case "Bandit": return new Brigand(x, y, game, whichChapter);
			case "Cavalier": return new Cavalier(x, y, game, whichChapter);
			case "Mage": return new Mage(x, y, game, whichChapter);
			case "Archer": return new Archer(x, y, game, whichChapter);
			case "ArmorKnight": return new ArmorKnight(x,y,game,whichChapter);
			case "Soldier": return new Soldier(x,y,game,whichChapter);
			case "Paladin": return new Paladin(x,y,game,whichChapter);
			case "Wyvern": return new Wyvern(x,y,game,whichChapter);
			case "Sage": return new Sage(x,y,game,whichChapter);
			case "General": return new General(x,y,game,whichChapter);
			case "Sniper": return new Sniper(x,y,game,whichChapter);
			case "DarkMage": return new DarkMage(x,y,game,whichChapter);
			case "SwordMaster": return new SwordMaster(x,y,game,whichChapter);
			case "Mercenary": return new Mercenary(x,y,game,whichChapter);
			case "Berserker": return new Berserker(x,y,game,whichChapter);
			case "Druid": return new Druid(x,y,game,whichChapter);
			case "Troubadour": return new Troubadour(x,y,game,whichChapter);
			case "TalkedToEnemy": return new TalkedToEnemy(x,y,game,whichChapter);
			case "Boss": return new Boss(x, y, game, whichChapter);

			default: return new Raymond(x, y, game, whichChapter);
		}
	}
	
	/** Sets stats and growths according to inputs */
	protected void setEnemyStats(int[] stats, int[] growths) {
		this.stats = stats;
		this.HP = stats[0];
		this.STR = stats[1];
		this.SK = stats[2];
		this.SP = stats[3];
		this.LCK = stats[4];
		this.DEF = stats[5];
		this.RES = stats[6];
		this.MOV = stats[7];
		this.CON = stats[8];
		this.level = stats[9];
		this.growths = growths;
	}
	/** Sets HP, STR, SK ... from stats[] array */
	public void resetStatsFromStatsArray() {
		this.HP = stats[0];
		this.STR = stats[1];
		this.SK = stats[2];
		this.SP = stats[3];
		this.LCK = stats[4];
		this.DEF = stats[5];
		this.RES = stats[6];
		this.MOV = stats[7];
		this.CON = stats[8];
		this.level = stats[9];
	}
	
	public void tick() {
		
		currentHP = Game.clamp(currentHP, 0, HP);
		equiptItem = wallet.getFirstWeapon();
		if (equiptItem == null) wallet.weapons.add(equiptItem = new Fists());

		if (currentHP <= 0) {
			die();
		}
		
	}
	public boolean equals(Player other) {
		return (this.stats == other.stats && this.growths == other.growths && this.name.equalsIgnoreCase(other.name) &&
				this.Class.equalsIgnoreCase(other.Class) && this.currentTile.placeEquals(other.currentTile));
	}
	
	/** Returns the ID of this Player, Bosses always have the same ID */
	public int getID() {
		if (isBoss) return game.playerIDMapINV.get("Boss");
		if (game.playerIDMapINV.get(name) == null) return 0;
		return game.playerIDMapINV.get(name);
	}
	
	/**Sets the mastery bonuses for each Class */
	public void setMasteryBonuses() {
		weaponMasteriesGrade = new char[4];
		weaponMasteries = new int[4];
		weaponMasteriesGrade[0] = 'F';
		weaponMasteriesGrade[1] = 'F';
		weaponMasteriesGrade[2] = 'F';
		weaponMasteriesGrade[3] = 'F';
		weaponMasteries[0] = 0;
		weaponMasteries[1] = 0;
		weaponMasteries[2] = 0;
		weaponMasteries[3] = 0;
		
		switch (Class) {
		case "Brigand":
			weaponMasteriesGrade[2] = 'C';
			break;
		case "Soldier":
			weaponMasteriesGrade[1] = 'C';
			break;
		case "Archer":
			weaponMasteriesGrade[3] = 'C';
			break;
		case "Mage":
			weaponMasteriesGrade[0] = 'C';
			break;
		case "Armor Knight":
			weaponMasteriesGrade[2] = 'C';
			break;
		case "Cavalier":
			weaponMasteriesGrade[0] = 'D';
			weaponMasteriesGrade[1] = 'D';
			break;
		case "Paladin":
			weaponMasteriesGrade[0] = 'B';
			weaponMasteriesGrade[1] = 'B';
			weaponMasteriesGrade[2] = 'B';
			break;
		
		case "Sniper":
			weaponMasteriesGrade[3] = 'B';
			break;
		case "Druid":
			weaponMasteriesGrade[3] = 'B';
			break;
		case "General":
			weaponMasteriesGrade[2] = 'B';
			weaponMasteriesGrade[1] = 'B';
			break;
		case "Sage":
			weaponMasteriesGrade[0] = 'B';
			break;
		case "Mercenary":
			weaponMasteriesGrade[0] = 'C';
			break;
		case "SwordMaster":
			weaponMasteriesGrade[0] = 'B';
			break;
		case "DarkMage":
			weaponMasteriesGrade[3] = 'C';
			break;
		case "Berserker":
			weaponMasteriesGrade[2] = 'B';
			break;
		case "Wyvern":
			weaponMasteriesGrade[1] = 'C';
			break;
		}
		
	}
	/** Assigns the tile that this player is standing on */
	public void setCurrentTile(Tile t) {
		if (t != null)
			currentTile = t;
	}
	
	public void render(Graphics g) {
		int gridLine = currentTile.gridLines;
		int size = Game.scale-gridLine;
		
		if (canMove && canAttack) {
			if (teamID.equalsIgnoreCase("Ally")) {
				g.setColor(Color.cyan);
			} else {
				g.setColor(Color.red);
			}
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, size, size);
		} else if (canAttack) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, size, size);
		} else {
			if (currentTile != null) {
				if (isAlly()) {
					if (currentTile.map.inEnemyPhase()) g.setColor(Color.cyan);
					else g.setColor(Color.black);
				} else g.setColor(Color.black);
			} else g.setColor(Color.black);
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale,size, size);
		}
		g.drawImage(image, currentTile.xPos * Game.scale, currentTile.yPos * Game.scale,size, size, null);
		
		if (drawScope) drawScopeImage(g);
		drawHealthBar(g);
		if (isBoss) drawBossScope(g);
		if (isCarryingUnit) {
			g.setColor(Color.red);
			g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 25));
			g.drawString("C", currentTile.xPos * Game.scale + 2*Game.scale/3, currentTile.yPos * Game.scale + 2*Game.scale/3);
		}
		
	}
	/** True if this unit is Armored and therefore is susceptible to effective damage from ArmorSlayers() */
	public boolean isArmoredUnit() {
		if (Class == null) return false;
		return (Class.equalsIgnoreCase("Knight") || Class.equalsIgnoreCase("General")
				|| Class.equalsIgnoreCase("Paladin") || Class.equalsIgnoreCase("Cavalier") 
				|| Class.equalsIgnoreCase("AxeLord"));
	}
	
	private void drawBossScope(Graphics g) {
		g.setColor(Color.ORANGE);
		for (int i = 0; i < 5; i++) {
			g.drawLine(currentTile.xPos*Game.scale + Game.scale/2, currentTile.yPos*Game.scale + 2*Game.scale/3 + i, 
					Game.scale*(currentTile.xPos+1), Game.scale*(currentTile.yPos+1) - Game.scale/3 + i);
			g.drawLine(currentTile.xPos*Game.scale + 3*Game.scale/4 + i, currentTile.yPos*Game.scale + Game.scale/2, 
					Game.scale*(currentTile.xPos) + 3*Game.scale/4 + i, Game.scale*(currentTile.yPos+1));
		}
	}
	/** True if this player has Health Points remaining */
	public boolean isAlive() {
		return currentHP > 0;
	}
	/** The movement of this player, with stat buffs calculated */
	public int getMOV() {
		return MOV + statBuffs[3];
	}
	
	/** Adds an item to this Player's wallet */
	public void addItem(Item it) {
		if (it == null) return;
		else wallet.addItem(it);
	}
	/** Removes an item from this Player's wallet */
	public void removeItem(Item it) {
		if (it == null) return;
		else wallet.removeItem(it);
	}
	
	public void repOk() {
		if (stats.length != 10) throw new IllegalArgumentException("Player must have 10 stats!");
	//	for (int a : stats) if (a < 0) throw new IllegalArgumentException("Player cannot have negative stats: Stat: " + a);
		for (int i = 0; i < 7; i++) {
			 if (stats[i] < 0) throw new IllegalArgumentException("Stat[" + i + "]: " + stats[i]);
		}
		if (growths.length != 7) throw new IllegalArgumentException("Player must have 7 growth stats!");
	}
	
	public Image getImage() {
		return image;
	}
	
	/**Populates canMove, canAttack, and canUse*/
	public void populateMAU() {
		setMAU(true);
	}
	
	public void setCanMove(boolean tf) {
		canMove = tf;
	}
	public void setCanAttack(boolean tf) {
		canAttack = tf;
	}
	public void setCanUse(boolean tf) {
		canUse = tf;
	}
	
	public int getMountedAidBonus() {
		if (isFlier) {
			return 10;
		} else return 0;
	}
	
	/** Draws a golden scope indicating this unit is being targetted */
	public void drawScopeImage(Graphics g) {
		g.setColor(new Color(255,215,0));
		int offSet = Game.scale/8;
		int thickness = 2;
		int width = Game.scale-(2*offSet);
		int xPosition = (currentTile.xPos * Game.scale) + offSet;
		int yPosition = (currentTile.yPos * Game.scale) + offSet;
		for (int i = 0; i < thickness; i++) {
			g.drawArc(xPosition, yPosition + i, width, width, 0, 360);
		}
		int x1 = xPosition + width/2;
		g.drawLine(x1, yPosition, x1, yPosition + thickness + width);
		g.drawLine(xPosition, yPosition + width/2, xPosition + width, yPosition + width/2);
	}
	/** Draws a red scope indicating this unit has not been selected for deployment in this chapter */
	public void drawNotDeployedScope(Graphics g) {
		g.setColor(Color.red);
		int offSet = Game.scale/8;
		int thickness = 2;
		int width = Game.scale-(2*offSet);
		int xPosition = (currentTile.xPos * Game.scale) + offSet;
		int yPosition = (currentTile.yPos * Game.scale) + offSet;
		for (int i = 0; i < thickness; i++) {
			g.drawArc(xPosition, yPosition + i, width, width, 0, 360);
		}
		int x1 = xPosition + width/2;
		g.drawLine(x1, yPosition, x1, yPosition + thickness + width);
		g.drawLine(xPosition, yPosition + width/2, xPosition + width, yPosition + width/2);
	}
	
	/** Draws the health bar of this unit */
	public void drawHealthBar(Graphics g) {
		g.setColor(Color.black);
		int x = currentTile.xPos * Game.scale;
		int height = Game.scale/5;
		int y = (currentTile.yPos * Game.scale) + (Game.scale - height);
		
		g.drawRect(x, y, Game.scale, height);
		g.setColor(Color.white);
		g.fillRect(x+1, y+1, Game.scale-1, height-1);
		g.setColor(Color.green);
		g.fillRect(x+1, y+1,(int)((Game.scale-1) * ((double)currentHP/HP)), height);
	}
	
	/** This player is removed from this chapter, becoming unplayable, untargetable, and invisible */
	public void die() {
		currentHP = 0;
		HP = 0;
		if (teamID.equalsIgnoreCase("Ally")) {
			game.chapterOrganizer.allys.remove(this);
		} else if (teamID.equalsIgnoreCase("Enemy")) {
			game.chapterOrganizer.enemys.remove(this);
		} else {
			System.out.println("Did not account for other types of Player's dying");
		}
		
		if (name.equalsIgnoreCase("SummonedUnit")) {
			game.chapterOrganizer.summonedUnit = null;
		}
		this.dead = true;
		if (this.playerCarried != null) {
			this.dropCarriedPlayer(currentTile);

		} else {
			currentTile.setCarrier(null);
		}
	}
	
	/** Only works if this player is a healer */
	public void healPlayer(Player other) {
		if (isHealer()) {
			if (this.equiptItem.category.equalsIgnoreCase("Healing")) {
				HealingItem healItem = (HealingItem)equiptItem;
				other.currentHP += healItem.damage + this.STR;
				if (isEnemy()) {
					addEXP(25);
				} else {
					addEXP(healItem.getHealExperience());
				}
				setMAU(false);
				decItemDuration(equiptItem);
				game.startEXPScene(false);
			} else System.out.println("Tried healing with a non healing item - " + equiptItem.name + ":  " + equiptItem.category );
		}
	}
	
	public void castDivineBlessing() {
		if (!isHealer()) return;
		ArrayList<Player> players = game.chapterOrganizer.getAdjacentAllies(this);
		for (int i = 0; i < players.size(); i++) {
			players.get(i).currentHP += (equiptItem.damage + STR);
		}
		addEXP(Math.min(100, 20*players.size()));
		setMAU(false);
		game.startEXPScene(false);
	}
	
	/** Only works if this player is a dancer */
	public void danceForPlayer(Player other) {
		if (isDancer()) {
			other.setMAUT(true);
			addEXP(10);
			setMAUT(false);
		}
	}
	
	/** Inflicts damage on a player's health, applying negative damage will heal the player
	 *  however this player cannot be healed above his maximum HP threshold
	 * @param damage
	 */
	public void takeDamage(int damage) {
		currentHP -= damage;
		if (currentHP > HP) currentHP = HP;
	}
	
	/**sets canUse, canAttack, canMove, all together */
	public void setMAU(boolean tf) {
		canUse = tf;
		canAttack = tf;
		canMove = tf;
		if (this.playerCarried != null) {
			playerCarried.canUse = tf;
			playerCarried.canAttack = tf;
			playerCarried.canMove = tf;
		}
	}
	/**sets canUse, canAttack, canMove, canTrade, all together */
	public void setMAUT(boolean tf) {
		canUse = tf;
		canAttack = tf;
		canMove = tf;
		canTrade = tf;
		if (this.playerCarried != null) {
			playerCarried.canUse = tf;
			playerCarried.canAttack = tf;
			playerCarried.canMove = tf;
			playerCarried.canTrade = tf;
		}
	}
	
	public void setUseSkill(boolean tf) {
		this.canUseSkill = tf;
	}
	
	/** Adds EXP to unit and if it is ally starts EXP Scene 
	 *  If the unit's EXP goes over 100, unit levels up and EXP flows back over to zero*/
	public void addEXP(int exp) {
		if (exp < 0) throw new RuntimeException("Cannot award a player negative experience!");
		int prevEXP = EXP;
		if (EXP + exp >= 100) {
			EXP = (EXP + exp) % 100;
			levelUp();
		} else {
			EXP += exp;
		}
		if (teamID.equalsIgnoreCase("Ally")) {
			game.setPlayerForEXP(this, prevEXP);
		}
	}
	/** This player will have a chance to increase each of his first 7 stats according
	 *  to their respective growth rates */
	public void levelUp() {
		
		for (int i = 0; i < levelUps.length; i++) {
			levelUps[i] = false;
		}
		int RNG = 0;
		for (int i = 0; i < growths.length; i++) {
			if (statCapped(i)) continue;
			RNG = r.nextInt(101);
			if (RNG <= growths[i]) {
				stats[i]++;
				levelUps[i] = true;
			}
		}
		this.HP = stats[0];
		this.STR = stats[1];
		this.SK = stats[2];
		this.SP = stats[3];
		this.LCK = stats[4];
		this.DEF = stats[5];
		this.RES = stats[6];
		level++;
		stats[9]++;
	}
	/** Returns true if the specified stat is capped for this player 
	 * 
	 * @param statIndex - 0 HP - 1 STR - 2 SK - 3 SP - 4 LCK - 5 DEF - 6 RES
	 * @return
	 */
	public boolean statCapped(int statIndex) {
		if (statIndex > 6) return false;
		if (level < 20) {
			
			if (statIndex == 0) {
				// HP
				if (stats[statIndex] < 60) return false;
			} else {
				if (statIndex == 4) {
					if (stats[4] < 35) return false;
				} else {
					if (stats[statIndex] < 22) return false;
				}
			}
			
		} else {
			
			if (statIndex == 0) {
				if (stats[0] < 100) return false;
			} else {
				if (statIndex == 2 || statIndex == 3) {
					if (stats[statIndex] < 42) return false;
				} else if (statIndex == 4) {
					if (stats[4] < 50) return false;
				} else {
					if (stats[statIndex] < 38) return false;
				}
			}
			
		}
		return true;
	}
	
	/** This player will have a chance to increase each of his first 7 stats according
	 *  to their respective growth rates 
	 *  @param addLevels - false if we are not incrementing levels also, used for enemies*/
	public void levelUp(boolean addLevels) {
		
		for (int i = 0; i < levelUps.length; i++) {
			levelUps[i] = false;
		}
		int RNG = 0;
		for (int i = 0; i < growths.length; i++) {
			if (statCapped(i)) continue;
			RNG = r.nextInt(101);
			if (RNG <= growths[i]) {
				stats[i]++;
				levelUps[i] = true;
			}
		}
		this.HP = stats[0];
		this.STR = stats[1];
		this.SK = stats[2];
		this.SP = stats[3];
		this.LCK = stats[4];
		this.DEF = stats[5];
		this.RES = stats[6];
		if (addLevels) {
			level++;
			stats[9]++;
		} else {
			this.currentHP = HP;
		}

	}
	/** Levels a player up numTimes times without incrementing their level */
	public void addFalseLevels(int numTimes) {
		for (int i = 0; i < numTimes; i++) {
			levelUp(false);
		}
		this.currentHP = HP;
	}
	
	/** Returns the color of the opposite team:
	 * 	if color = red, output = blue and vice versa
	 *  if neither red nor blue, returns orange
	 */
	public Color otherTeamColor(Color input) {
		if (input == Color.RED) {
			return Color.BLUE;
		} else if (input == Color.BLUE) {
			return Color.RED;
		} else return Color.orange;
	}
	/** can Move or Attack*/
	public boolean canMA() {
		return (canAttack || canMove);
	}
	/** can Move or Attack or Use or Trade */
	public boolean canMAUT() {
		return (canAttack || canMove || canUse || canTrade);
	}
	
	/** Returns the name of this unit */
	public String toString() {
		return name;
	}
	/** Decrements the duration of the item specified, if null decrements the equipted item */
	public void decItemDuration(Item item) {
		if (item == null) {
			if (equiptItem.weaponType.equalsIgnoreCase("notAType")) return;
			equiptItem.duration--;
		}
		else {
			if (item.category.equalsIgnoreCase("Physical")) {
				CombatItem cItem = (CombatItem) item;
				if (cItem.weaponType.equalsIgnoreCase("notAType")) return;
			}
			item.duration--;
		}
		removeBrokenItems();
	}
	
	/** Removes the broken items from both your wallets */
	public void removeBrokenItems() {
		boolean removed = false;
		for (int i = 0; i < wallet.weapons.size(); i++) {
			if (wallet.weapons.get(i).duration <= 0) {
				wallet.weapons.remove(i);
				removed = true;
			}
		}
		for (int i = 0; i < wallet.utilities.size(); i++) {
			if (wallet.utilities.get(i).duration <= 0) {
				wallet.utilities.remove(i);
				removed = true;
			}
		}
		if (removed) {
			if (game.playerGFX != null) game.playerGFX.decWeaponIndex();
		}
	}
	
	/** Increments the mastery of the weapon used, upgrading them if necessary */
	public void incWeaponGrade(Item weapon) {
		if (weapon == null) return;
		if (weapon.category.equalsIgnoreCase("Physical") || weapon.category.equalsIgnoreCase("Magical")) {
			CombatItem item = (CombatItem) weapon;
			switch (item.weaponType) {
			case "Sword": 
				if (weaponMasteriesGrade[0] == 'B') {
					weaponMasteries[0] += 3;
				} else if (weaponMasteriesGrade[0] == 'A') {
					weaponMasteries[0] += 1;
				} else {
					weaponMasteries[0] += masteryIncrease;
				}
				break;
			case "Lance": 
				if (weaponMasteriesGrade[1] == 'B') {
					weaponMasteries[1] += 3;
				} else if (weaponMasteriesGrade[1] == 'A') {
					weaponMasteries[1] += 1;
				} else {
					weaponMasteries[1] += masteryIncrease;
				}
				break;
			case "Axe": 
				if (weaponMasteriesGrade[2] == 'B') {
					weaponMasteries[2] += 3;
				} else if (weaponMasteriesGrade[2] == 'A') {
					weaponMasteries[2] += 1;
				} else {
					weaponMasteries[2] += masteryIncrease;
				}
				break;
			case "Bow": 
				if (weaponMasteriesGrade[3] == 'B') {
					weaponMasteries[3] += 3;
				} else if (weaponMasteriesGrade[3] == 'A') {
					weaponMasteries[3] += 1;
				} else {
					weaponMasteries[3] += masteryIncrease;
				}
				break;
			case "Fire": 
				if (weaponMasteriesGrade[0] == 'B') {
					weaponMasteries[0] += 3;
				} else if (weaponMasteriesGrade[0] == 'A') {
					weaponMasteries[0] += 1;
				} else {
					weaponMasteries[0] += masteryIncrease;
				}
				break;
			case "Ice": 
				if (weaponMasteriesGrade[1] == 'B') {
					weaponMasteries[1] += 3;
				} else if (weaponMasteriesGrade[1] == 'A') {
					weaponMasteries[1] += 1;
				} else {
					weaponMasteries[1] += masteryIncrease;
				}
				break;
			case "Earth": 
				if (weaponMasteriesGrade[2] == 'B') {
					weaponMasteries[2] += 3;
				} else if (weaponMasteriesGrade[2] == 'A') {
					weaponMasteries[2] += 1;
				} else {
					weaponMasteries[2] += masteryIncrease;
				}
				break;
			case "Dark": 
				if (weaponMasteriesGrade[3] == 'B') {
					weaponMasteries[3] += 3;
				} else if (weaponMasteriesGrade[3] == 'A') {
					weaponMasteries[3] += 1;
				} else {
					weaponMasteries[3] += masteryIncrease;
				}
				break;
			}
			checkMasteryGrades();
		}
	}
		
	private void checkMasteryGrades() {
		for (int i = 0; i < 4; i++) {
			if (weaponMasteries[i] >= weaponUpgrade) {
				weaponMasteriesGrade[i] = upGrade(weaponMasteriesGrade[i]);
				weaponMasteries[i] = 0;
			}
		}
	}
	private char upGrade(char prev) {
		if (prev == 'F') return 'D';
		else if (prev == 'D') return 'C';
		else if (prev == 'C') return 'B';
		else if (prev == 'B') return 'A';
		else if (prev == 'A') return 'S';
		else return 'S';
	}
	
	private int weaponTierBonus(String whichBonus) {
		if (name.equalsIgnoreCase("SummonedUnit")) return 0;
		if (equiptItem == null || equiptItem.getClass().equals(Fists.class)) return 0;
		
		if (equiptItem.weaponType.equalsIgnoreCase("Sword")) return getBonusForMastery(weaponMasteriesGrade[0], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Lance")) return getBonusForMastery(weaponMasteriesGrade[1], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Axe")) return getBonusForMastery(weaponMasteriesGrade[2], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Bow")) return getBonusForMastery(weaponMasteriesGrade[3], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Fire")) return getBonusForMastery(weaponMasteriesGrade[0], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Ice")) return getBonusForMastery(weaponMasteriesGrade[1], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Earth")) return getBonusForMastery(weaponMasteriesGrade[2], whichBonus);
		else if (equiptItem.weaponType.equalsIgnoreCase("Dark")) return getBonusForMastery(weaponMasteriesGrade[3], whichBonus);
		else return 0;
		
	}
	/** Gives the oppropriate bonuses for the given masteryIndex*/
	private int getBonusForMastery(char grade, String bonus) {
		
		if (grade == 'F') return 0;
		if (grade == 'D') {
			switch (bonus) {
			case "hit": return 5;
			case "avoid": return 0;
			case "crit": return 1;
			case "damage": return 0;
			}
		} else if (grade == 'C') {
			switch (bonus) {
			case "hit": return 5;
			case "avoid": return 5;
			case "crit": return 0;
			case "damage": return 1;
			}
		} else if (grade == 'B') {
			switch (bonus) {
			case "hit": return 10;
			case "avoid": return 5;
			case "crit": return 5;
			case "damage": return 2;
			}
		} else if (grade == 'A') {
			switch (bonus) {
			case "hit": return 15;
			case "avoid": return 10;
			case "crit": return 8;
			case "damage": return 2;
			}
		} else if (grade == 'S') {
			switch (bonus) {
			case "hit": return 25;
			case "avoid": return 15;
			case "crit": return 15;
			case "damage": return 5;
			}
		}
		System.out.println("Player - getBonusOnMastery() un-accounted for input: " + bonus + " at grade: " + grade);
		return 0;
		
	}
	/** Trades the specified item with the player, returns true if trade was successful */
	public boolean giveItem(Player other, Item item) {
		if (item == null) return false;
		if (item.duration >= 9998) return false;
//		if (name.equalsIgnoreCase("Ike") && item.name.equalsIgnoreCase("Durandal")) return false;
		if (item.isUtilityItem()) {
			if (other.wallet.utilities.size() >= Wallet.MAX_SIZE) return false;
		} else {
			if (other.wallet.weapons.size() >= Wallet.MAX_SIZE) return false;
		}
		
		other.wallet.addItem(item);
		wallet.removeItem(item);
		
		return true;
	}

	/** Swaps items between two players */
	public boolean swapItem(Player other, Item myItem, Item theirItem) {
		if (myItem == null) {
			if (theirItem == null) return false;
		}
		if (other == null) return false;
		if (theirItem != null) {
			if (theirItem.isUtilityItem()) {
				// other is trading us a utility item
				if (wallet.utilities.size() < Wallet.MAX_SIZE) {
					// we have room, take his item
					other.giveItem(this, theirItem);
					// check if he has room for ours
					if (myItem != null) {
						if (myItem.isUtilityItem()) {
							// giving him utility item, he just gave us one so there is space
							giveItem(other, myItem);
							return true;
						} else {
							// we are giving him a combat item
							if (other.wallet.weapons.size() < Wallet.MAX_SIZE) {
								// he has space for the combat item
								giveItem(other, myItem);
								return true;
							} else {
								// he has no space for a weapon
								return true;
							}
						}
					} else return true;
				} else {
					// we do not have room for it!
					if (myItem != null) {
						if (myItem.isUtilityItem()) {
						// both utility items, we can swap them!
							if (other.wallet.utilities.size() < Wallet.MAX_SIZE) {
								// he has space for this item
								// give him first to clear space, then he gives us his
								giveItem(other, myItem);
								other.giveItem(this, theirItem);
								return true;
							} else {
								// he also has no space for this item
								removeItem(myItem);
								other.giveItem(this, theirItem);
								giveItem(other, myItem);
								return true;
							}
						} else {
							// my item is not a utility item
							// i do not get item, see if he can receive
							if (other.wallet.weapons.size() < Wallet.MAX_SIZE) {
								giveItem(other, myItem);
								return true;
							} else return false;
						}
					}
				}
			} else {
				// combat item!
				if (wallet.weapons.size() < Wallet.MAX_SIZE) {
					other.giveItem(this, theirItem);
					// check if we can give them our item
					if (myItem != null) {
						if (myItem.isUtilityItem()) {
							// he gave us weapon, check to see if he has room for utilities
							if (other.wallet.utilities.size() < Wallet.MAX_SIZE) {
								giveItem(other, myItem);
							}
						} else {
							// he just gave us a weapon, there is room for him to take ours
							giveItem(other, myItem);
						}
					}
					return true;
				} else {
					// no room for this weapon
					if (myItem != null) {
						if (!myItem.isUtilityItem()) {
							// they are both combat items, we can swap them!
							if (other.wallet.weapons.size() < Wallet.MAX_SIZE) {
								// he has space to hold this item, we will give him first
								giveItem(other, myItem);
								other.giveItem(this, theirItem);
								return true;
							} else {
								// he also has no space for this item
								removeItem(myItem);
								other.giveItem(this, theirItem);
								giveItem(other, myItem);
								return true;
							}
						} else {
							// my item is a utility item, check if he has space
							if (other.wallet.utilities.size() < Wallet.MAX_SIZE) {
								giveItem(other, myItem);
								return true;
							} else return false;
						}
					} else return false;
					
					
				}
			}
		} else {
			if (myItem == null) return false;
			else {
				// their item is null, my item is not null
				if (myItem.isUtilityItem()) {
					if (other.wallet.utilities.size() < Wallet.MAX_SIZE) {
						giveItem(other, myItem);
						return true;
					} else return false;
				} else {
					if (other.wallet.weapons.size() < Wallet.MAX_SIZE) {
						giveItem(other, myItem);
						return true;
					} else return false;
				}
			}
		}
		return false;
	}
	
	/** Gives the specified Item over to the convoy */
	public void giveItem(Convoy convoy, Item item) {
		if (item.duration >= 9998) return;
		if (name.equalsIgnoreCase("Ike") && item.name.equalsIgnoreCase("Durandal")) return;
		if (wallet.weapons.contains(item) || wallet.utilities.contains(item)) {
			convoy.addItem(item);
			wallet.removeItem(item);
		}
	}
	
	/** Determines whether the two players are on the same team */
	public boolean sameTeam(Player other) {
		if (other == null) return false;
		else if (other.teamID == null) return false;
		return teamID.equalsIgnoreCase(other.teamID);
	}
	public boolean isHealer() {
		return isHealer;
	}
	public boolean isDancer() {
		return isDancer;
	}
	public int getEffectiveSpeed() {
		return SP - Math.max(0, equiptItem.weight - CON);
	}
	/** True if this player has 4 or more effective speed than the opposing player */
	public boolean willDouble(Player other) {
		return this.getEffectiveSpeed() - other.getEffectiveSpeed() >= 4;
	}
	
	/** this player's chance of hitting defender */
	public int getEffectiveHit(Player defender) {

		if (equiptItem.weaponType.equalsIgnoreCase("Gun")) {
			int dist = game.getTrueDist(currentTile, defender.currentTile);
			if (skill.nameEquals("Gunman")) {
				return 10 + equiptItem.hit + 2*SK/3 - defender.getAvoid()/10 - dist/2;
			} else {
				return equiptItem.hit + SK/2 - defender.getAvoid()/4 - 2*dist;
			}
		} else {
			int[] stats = getWeaponTriangleStats(this, defender);
			return Math.max(0, getHit() - defender.getAvoid() + stats[1]);
		}
	}
	/** This player's damage against defender */
	public int getEffectiveDamage(Player defender) {
		if (isDancer()) return 0;
		if (equiptItem.category.equalsIgnoreCase("Healing")) return 0;
		int[] stats = getWeaponTriangleStats(this, defender);
		if (equiptItem.category.equalsIgnoreCase("Magical")) {
			return Math.max(0, this.getDamage() - defender.getResistance() + stats[0]);
		} else {
			if (equiptItem.weaponType.equalsIgnoreCase("Bow")) {
				if (defender.isFlier) {
					if (!defender.wallet.containsItemByName("Draco Shield")) {
						return (Math.max(0, 3*equiptItem.damage + weaponTierBonus("damage") + statBuffs[0] + (STR - defender.getDefense() + stats[0])));
					}
				}
			} else if (equiptItem.weaponType.equalsIgnoreCase("Sword")) {
				if (equiptItem.name.equalsIgnoreCase("ArmorSlayer")) {
					if (defender.isArmoredUnit()) {
						return (Math.max(0, 3*equiptItem.damage + weaponTierBonus("damage") + statBuffs[0] + (STR - defender.getDefense() + stats[0])));
					}
				}
			} else if (equiptItem.weaponType.equalsIgnoreCase("Gun")) {
				return Math.max(0,  equiptItem.damage + statBuffs[0] + (STR/2 + 2*SK + 2*LCK/3)/4 - (2*defender.getDefense() + defender.getResistance())/6);
			}
			return Math.max(0, this.getDamage() - defender.getDefense() + stats[0]);
		}
		
	}
	/** True if we have an item to heal with in our inventory */
	public boolean hasHealingItem() {
		for (int i = 0; i < wallet.weapons.size(); i++) {
			if (wallet.weapons.get(i).category.equalsIgnoreCase("Healing")) return true;
		}
		return false;
	}
	
	/** This player's crit against defender */
	public int getEffectiveCrit(Player defender) {
		
		int[] stats = getWeaponTriangleStats(this, defender);
		return Math.max(0, this.getCrit() - defender.LCK + stats[2]);
		
	}
	/** Stats for weapon triangle against two players [DMG, HIT, CRIT] */
	public static int[] getWeaponTriangleStats(Player attacker, Player defender) {
		int[] triangleStats = new int[3];
		int[] losingStats = new int[3];
		int[] winningStats = new int[3];
		losingStats[0] = -1;
		losingStats[1] = -15;
		losingStats[2] = -3;
		
		winningStats[0] = 1;
		winningStats[1] = 15;
		winningStats[2] = 3;
		if (attacker.isMagicUser && defender.isMagicUser) {
			// both magic users, use magic triangle
			if (attacker.equiptItem.weaponType.equalsIgnoreCase("Fire")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Fire": return triangleStats;
				
				case "Ice": 
					return losingStats;
					
				case "Earth": 
					return winningStats;
					
				case "Dark": 
					return losingStats;
				}
			} else if (attacker.equiptItem.weaponType.equalsIgnoreCase("Ice")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Ice": return triangleStats;
				
				case "Fire": 
					return winningStats;
					
				case "Earth": 
					return losingStats;
					
				case "Dark": 
					return losingStats;
				}
			} else if (attacker.equiptItem.weaponType.equalsIgnoreCase("Earth")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Earth": return triangleStats;
				
				case "Ice": 
					return winningStats;
					
				case "Fire": 
					return losingStats;
					
				case "Dark": 
					return losingStats;
				}
			} else if (attacker.equiptItem.weaponType.equalsIgnoreCase("Dark")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Dark": return triangleStats;
				
				case "Fire": 
					return winningStats;
					
				case "Ice": 
					return winningStats;

					
				case "Earth": 
					return winningStats;
				
				}
			}
		
		} else if (!attacker.isMagicUser && !defender.isMagicUser) {
			// both physical fighters, use physical triangle
			if (attacker.equiptItem.weaponType.equalsIgnoreCase("Sword")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Sword": return triangleStats;
				
				case "Lance": 
					return losingStats;
					
				case "Axe": 
					return winningStats;

				}
			} else if (attacker.equiptItem.weaponType.equalsIgnoreCase("Lance")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Lance": return triangleStats;
				
				case "Sword": 
					return winningStats;
					
				case "Axe": 
					return losingStats;
				}
			} else if (attacker.equiptItem.weaponType.equalsIgnoreCase("Axe")) {
				switch (defender.equiptItem.weaponType) {
				
				case "Axe": return triangleStats;
				
				case "Lance": 
					return winningStats;
					
				case "Sword": 
					return losingStats;
				}
			}
		
		} else {
			return triangleStats;
		}
		return triangleStats;
	}
	
	/** Sets Ally's stats to their base stats found in their res//characters// file */
	protected void setAllyBaseStats() {
		stats = new int[10];
		growths = new int[7];
		String filename = "res//res_characters//" + name;
		if (wallet != null) wallet.clear();
		try {
			Scanner reader = new Scanner(new File(filename));
			String[] line;
			while (reader.hasNextLine()) {
				reader.nextLine();
				//start at load level 0
				this.dead = (Integer.parseInt(reader.nextLine()) == 0);
				this.Class = reader.nextLine();
				this.skill = new Skill(reader.nextLine());
					
				for (int i = 0; i < 7; i++) {
					line = reader.nextLine().split(":"); //stats : growths
					stats[i] = Integer.valueOf(line[0]);
					growths[i] = Integer.valueOf(line[1]);
				}
					stats[7] = Integer.valueOf(reader.nextLine()); //MOV
					stats[8] = Integer.valueOf(reader.nextLine()); //CON
					stats[9] = Integer.valueOf(reader.nextLine()); //LEVEL
					EXP = Integer.valueOf(reader.nextLine());
					for (int i = 0; i < 4; i++) { // MOV CON LV EXP
						line = reader.nextLine().split(":");
						weaponMasteriesGrade[i] = line[0].charAt(0);
						weaponMasteries[i] = Integer.valueOf(line[1]);
					}
					//get items and durations
					for (int i = 0; i < 8; i++) {
						String newLine = reader.nextLine();
						if (newLine.isEmpty() || newLine.equalsIgnoreCase("none")) continue;
						line = newLine.split(":");
						Item item = Item.getItemByID(Integer.valueOf(line[0]));
						if (item.name.equalsIgnoreCase("Key")) {
							Key key = (Key)item;
							key.setGame(game);
						}
						if (line.length > 1) item.duration = Integer.valueOf(line[1]);
						wallet.addItem(item);
						if (i == 0) { //first item specified is equipt Item
							if (item.getClass().asSubclass(CombatItem.class) != null) {
								this.equiptItem = (CombatItem)item;
							}
						}
					}
					applyModeBonus();
					setEnemyStats(stats, growths);
					reader.close();
					break;
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Spawns a new random enemy unit at position 0,0 - the higher difficulty unlocks different strength enemies
	 * @param difficulty - 0 = base classes, 1 = base classes and promoted enemies
	 * @param chapt - the chapter we are spawning them on
	 * @return
	 */
	public static EnemyPlayer randomEnemyUnit(int difficulty, int chapt, Game game) {
		
		int RNG;
		Random r = new Random();
		
		if (difficulty <= 0) {
			RNG = r.nextInt(9);
			switch (RNG) {
				case 0: return new Brigand(0,0,game,chapt);
				case 1: return new Mage(0,0,game,chapt);
				case 2: return new Cavalier(0,0,game,chapt);
				case 3: return new Soldier(0,0,game,chapt);
				case 4: return new ArmorKnight(0,0,game,chapt);
				case 5: return new Archer(0,0,game,chapt);
				case 6: return new Wyvern(0,0,game,chapt);
				case 7: return new DarkMage(0,0,game,chapt);
				case 8: return new Mercenary(0,0,game,chapt);
				default: return new Soldier(0,0,game,chapt);
			}
		
		} else {
			RNG = r.nextInt(17);
			switch (RNG) {
				case 0: return new Brigand(0,0,game,chapt);
				case 1: return new Mage(0,0,game,chapt);
				case 2: return new Cavalier(0,0,game,chapt);
				case 3: return new Soldier(0,0,game,chapt);
				case 4: return new ArmorKnight(0,0,game,chapt);
				case 5: return new Archer(0,0,game,chapt);
				case 6: return new Wyvern(0,0,game,chapt);
				case 7: return new Sage(0,0,game,chapt);
				case 8: return new Paladin(0,0,game,chapt);
				case 9: return new General(0,0,game,chapt);
				case 10: return new Sniper(0,0,game,chapt);
				case 11: return new DarkMage(0,0,game,chapt);
				case 12: return new SwordMaster(0,0,game,chapt);
				case 13: return new Mercenary(0,0,game,chapt);
				case 14: return new Berserker(0,0,game,chapt);
				case 15: return new Druid(0,0,game,chapt);
				case 16: return new Troubadour(0,0,game,chapt);
				default: return new Wyvern(0,0,game,chapt);
			}
		}
	}
	
	/** Returns the number of tiles between the two players */
	public int getTrueDistanceFromPlayer(Player opp) {
		if (opp == null) {
			System.out.println("Tried to get distance from null opponent");
			return 10;
		}
		return game.getTrueDist(this.currentTile, opp.currentTile);
	}
	/** Units DEF + tile terrain bonuses + stat buffs */
	public int getDefense() {
		return DEF + currentTile.terrainBonuses[0] + statBuffs[1];
	}
	/** Units Resistance + stat buffs */
	public int getResistance() {
		return RES + statBuffs[2];
	}
	
	/** STR + item damage + weapon Tier Bonus + statBuffs[0] */
	public int getDamage() {
		if (isDancer()) return 0;
		if (isHealer()) {
			if (!duoWeaponHeal) return 0;
		}
		return STR + equiptItem.damage + weaponTierBonus("damage") + statBuffs[0];
	}
	/** SK, Luck, item crit, weapon tier bonus */
	public int getCrit() {
		if (isDancer()) return 0;
		if (isHealer()) {
			if (!duoWeaponHeal) return 0;
		}
		if (this.skill.nameEquals("Rage")) return (SK/2 + Math.max(0,LCK/5 - 1) + equiptItem.crit + weaponTierBonus("crit") + 15);
		return SK/2 + Math.max(0,LCK/5 - 1) + equiptItem.crit + weaponTierBonus("crit");
	}
	/** This units avoid */
	public int getAvoid() {
	//	return (3 * SP) + (SK + 2*LCK)/4 + currentTile.terrainBonuses[1] + (2*(CON - equiptItem.weight)) + weaponTierBonus("avoid");
		return (3*getEffectiveSpeed() + LCK)/2 + currentTile.terrainBonuses[1] + weaponTierBonus("avoid") + (CON - equiptItem.weight)/2;
	}
	/** This units hit percentage */
	public int getHit() {
	//	return (4 * SK) + (LCK + SP)/4 + equiptItem.hit + weaponTierBonus("hit");
		return equiptItem.hit + (SK*3 + LCK)/2 + weaponTierBonus("hit") + (CON - equiptItem.weight);
	}
	/** Returns the maximum weapon range available in this Player's wallet, includes staves */
	public int maxWeaponRange() {
		int max = 0;
		for (int i = 0; i < wallet.weapons.size(); i++) {
			int range = wallet.weapons.get(i).range;
			if (this.isMagicUser) {
				if (!wallet.weapons.get(i).category.equalsIgnoreCase("Magical")) continue;
			} else {
				if (!wallet.weapons.get(i).category.equalsIgnoreCase("Physical")) continue;
			}
			if (range >= max) {
				max = range;
			}
		}
		return max;
	}
	/** Returns maximum weapon range available, not from staffs! Only from magical type */
	public int maxCombatItemRange() {
		int max = 0;
		for (int i = 0; i < wallet.weapons.size(); i++) {
			if (wallet.weapons.get(i).isHealingItem()) continue;
			int range = wallet.weapons.get(i).range;
			if (this.isMagicUser) {
				if (!wallet.weapons.get(i).isMagicItem()) continue;
			} else {
				if (!wallet.weapons.get(i).isPhysicalItem()) continue;
			}
			if (range >= max) {
				max = range;
			}
		}
		return max;
	}
	
	/** Returns the maximum staff range available in this Player's wallet */
	public int maxStaffRange() {
		int max = 0;
		for (int i = 0; i < wallet.weapons.size(); i++) {
			if (wallet.weapons.get(i).category.equalsIgnoreCase("Healing")) {
				int range = wallet.weapons.get(i).range;
				if (range >= max) {
					max = range;
				}
			}
		}
		return max+this.staffExtention;
	}
	/** Returns a list of our stat buffs */
	public int[] getStatBuffs() {
		return statBuffs;
	}
	
	public boolean isAlly() {
		return teamID.equalsIgnoreCase("Ally");
	}
	
	public boolean isEnemy() {
		return teamID.equalsIgnoreCase("Enemy");
	}
	/** True if our wallet is at maximum capacity */
	public boolean maxWalletCapacity() {
		return wallet.size() >= 2*Wallet.MAX_SIZE;
	}
	
}
