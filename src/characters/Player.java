package characters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import gameMain.Game;
import items.CombatItem;
import items.Fists;
import items.Item;
import items.Wallet;
import tiles.Tile;
/**
 * A player in this game
 * @author mark
 *
 */
public class Player {
	/**List of stat names*/
	public static String[] StatNames = {"HP", "STR", "SK", "SP", "LCK", "DEF", "RES", "MOV", "CON", "Level"};
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
	/**DEF, AVOID*/
	public int[] terrainBonuses;
	/**HP, STR, DEF, HIT, AVOID, CRIT*/
	public int[] classBonuses;
	/**Item this player has equipped, null if none in wallet*/
	public CombatItem equiptItem;
	/** This player's EXP, player levels up if it surpasses 100 */
	public int EXP;
	/** Factors that help with fighting */
	public int hit, avoid, crit;
	/** Swords, Lances, Axes, Bows  --> the letter grade*/
	public char[] weaponMasteriesGrade;
	/** Swords, Lances, Axes, Bows --> how many uses */
	public int[] weaponMasteries;
	/** The integer that tracks how long till next upgrade for each type*/
	public int weaponUpgrade;
	/** Responsible for moving, attacking, and using items*/
	public boolean canMove, canAttack, canUse;
	/**This is true when this player is being looked at for attack by another player*/
	public boolean drawScope = false;
	/**String that separates an Ally from Enemy from NPC */
	public String teamID;
	/**Damage this player can deal*/
	public int damage;
	public Color teamColor;
	/** tracks the most recent level ups */
	public boolean[] levelUps;
	
	public Random r;
	
	public Player(String name, String Class, String teamID, int[] stats, int[] growths, Game game, int xPos, int yPos, CombatItem equiptItem) {
		this.game = game;
		this.name = name;
		this.Class = Class;
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
		this.xPos = xPos;
		this.yPos = yPos;
		classBonuses = new int[6];
		this.teamID = teamID;
		setClassBonuses();
		this.currentTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(xPos, yPos);
		setMasteryBonuses();
		terrainBonuses = new int[2];
		wallet = new Wallet(this);
		this.equiptItem = equiptItem;
		if (equiptItem != null) {
			wallet.weapons.add(equiptItem);
		} else {
			wallet.weapons.add(new Fists());
		}
		if (teamID.equalsIgnoreCase("Ally")) {
			teamColor = Color.BLUE;
		} else if (teamID.equalsIgnoreCase("Enemy")) {
			teamColor = Color.RED;
		}
		EXP = 0;
		currentHP = stats[0];
		repOk();
		weaponUpgrade = 100;
		canMove = true;
		canAttack = true;
		canUse = true;
		levelUps = new boolean[growths.length];
		r = new Random();
	}
	
	public void tick() {
		currentHP = Game.clamp(currentHP, 0, HP);
		equiptItem = wallet.getFirstWeapon();
		if (equiptItem == null) {
			Fists myfists = new Fists();
			wallet.addItem(myfists);
			equiptItem = myfists;
		}
		hit = (4 * SK) + (LCK/2) + classBonuses[3] + equiptItem.hit; // + weapon's hit
		avoid = (3 * SP) + LCK + classBonuses[4] + terrainBonuses[1] + (2*(CON - equiptItem.weight));
		crit = SK/2 + LCK/3 + classBonuses[5] + equiptItem.crit;
		damage = STR + equiptItem.damage + classBonuses[1];
		if (currentHP <= 0) {
			System.out.println("should be dying");
			die();
		}
		for (int i = 0; i < wallet.weapons.size(); i++) {
			if (wallet.weapons.get(i).duration <= 0) wallet.weapons.remove(i);
		}
		for (int i = 0; i < wallet.utilities.size(); i++) {
			if (wallet.utilities.get(i).duration <= 0) wallet.utilities.remove(i);
		}
	}
	
	/**Sets stat bonuses depending on Class */
	public void setClassBonuses() {
		
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
		
		if (Class.equalsIgnoreCase("Lord")) {
			weaponMasteriesGrade[0] = 'C';
			weaponMasteries[0] = 20;
		}
	}
	
	public void setCurrentTile(Tile t) {
		if (t != null)
			currentTile = t;
		else
			throw new IllegalArgumentException("The tile assigned to player " + name + " is null!");
	}
	
	public void render(Graphics g) {
		if (canMove && canAttack) {
			if (teamID.equalsIgnoreCase("Ally")) {
				g.setColor(Color.cyan);
			} else {
				g.setColor(Color.red);
			}
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1);
		} else if (canAttack) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1);
		} else {
			g.setColor(Color.black);
			g.fillRect(currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1);
		}
		g.drawImage(image, currentTile.xPos * Game.scale, currentTile.yPos * Game.scale, Game.scale-1, Game.scale-1, null);
		if (drawScope) drawScopeImage(g);
		drawHealthBar(g);
	}
	
	public boolean isAlive() {
		return currentHP > 0;
	}
	
	public void addItem(Item it) {
		if (it == null) throw new NullPointerException("Cannot add a null item to a Player");
		else wallet.addItem(it);
	}
	public void removeItem(Item it) {
		if (it == null) throw new NullPointerException("Cannot remove a null item from a Player");
		else wallet.removeItem(it);
	}
	
	public void repOk() {
		if (stats.length != 10) throw new IllegalArgumentException("Player must have 10 stats!");
		for (int a : stats) if (a < 0) throw new IllegalArgumentException("Player cannot have negative stats");
		if (growths.length != 7) throw new IllegalArgumentException("Player must have 7 growth stats!");
	}
	
	public Image getImage() {
		return image;
	}
	/**Populates canMove, canAttack, and canUse*/
	public void populateMAU() {
		canMove = true;
		canAttack = true;
		canUse = true;
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
		currentTile.setCarrier(null);
	}
	
	public void takeDamage(int damage) {
		currentHP -= damage;
		if (currentHP > HP) currentHP = HP;
	}
	/**sets canUse, canAttack, canMove all together */
	public void setMAU(boolean tf) {
		canUse = tf;
		canAttack = tf;
		canMove = tf;
	}
	public void addEXP(int exp) {
		if (exp < 0) throw new RuntimeException("Cannot award a player negative experience!");
		if (EXP + exp > 100) {
			System.out.println("LEVEL UPPP");
			EXP = (EXP + exp) % 100;
			level++;
			stats[9]++;
		} else {
			EXP += exp;
		}
	}
	public void levelUp() {
		for (int i = 0; i < levelUps.length; i++) {
			levelUps[i] = false;
		}
		int RNG = 0;
		for (int i = 0; i < growths.length; i++) {
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
	}
	public Color otherTeamColor(Color input) {
		if (input == Color.RED) {
			return Color.BLUE;
		} else if (input == Color.BLUE) {
			return Color.RED;
		} else return Color.orange;
	}
	public boolean canMA() {
		if (canAttack || canMove) return true;
		else return false;
	}
}
