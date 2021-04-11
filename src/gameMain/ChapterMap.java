package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import characters.AllyPlayer;
import characters.Heath;
import characters.Player;
import gameMain.Game.STATE;
import graphics.AttackMenu;
import items.Key;
import tiles.GrassTile;
import tiles.Tile;
import tiles.Village;

/**
 * A tile map that will be used to play chapters on Also renders all the tiles
 * for the chapter
 * 
 * @author mark
 *
 */
public class ChapterMap {
	
	/** The two dimensional array of tiles to form the map */
	public Tile[][] tileMap;
	/** Tiles used to help move about the screen */
	public Tile topLeft, bottomRight;
	/** A List of all the tiles in this map */
	public ArrayList<Tile> tiles;
	/** Number of rows/columns in this map */
	public int rows, cols;
	/** number of rows/cols visible on screen at a given time */
	public int nrow, ncol;
	/**The tile currently selected*/
	public Tile currentTile;
	/**Turn count for this chapter */
	public int turnCount;
	/** AllyPhase or EnemyPhase */
	public String currentPhase;
	/** Reference to our Game */
	public Game game;
	/** Tile for rendering movements or skills without changing the main Current Tile */
	public Tile selectedBoxTile;
	/** True if we are currently rendering an attack menu */
	public boolean inAttackMenu;
	/** Menu showing attackers stats against each other */
	public AttackMenu attackMenu;
	/** What we are checking for to consider a win */
//	public String winCondition;
	/** Turn count to reach in order to beat this chapter, if win condition is 'Survive' */
	public int surviveTurnCount;
	/** Spawns allys/enemies on this map */
	public Spawner spawner;
	/** The chapter we are currently on */
	public int currentChapter;
	/** The key that is used to open a chest or item at some point throughout a specific chapter */
	public Key selectedKey;
	/** True if we should draw the hud */
	public boolean drawHUD = true;
	/** If we are currently rendering an in-game cut scene */
	public boolean inCutScene = false;
	/** True if we have already finished the displayed cutscene */
	public boolean cutSceneDestroyed = false;
	/** Which sceen in this chapter of the cut scene are we in */
	public int whichSceen;
	/** The item received by some chest or village on this map */
	private String itemReceived;
	/** For tracking amount of time the Item Received state is rendered for */
	private long itemReceivedTimer;
	/** Index of our ally's with moves to prevent random selection when user presses 'q' */
	private int allyWithMovesIndex;
	/** True if this chapter has been ended */
	public boolean chapterOver;
	/** For chapter win conditions involving opening a certain number of chests */
	public int maxChestsToWin = 10, currentNumChests = 0;
	/** RNG for spawning enemies */
	private Random rand;
	/** Conditions specified to end a chapter */
	public enum WinCondition {
		Route,
		Sieze,
		KillBoss,
		Survive,
		Capture,
		Defend
	};
	/** The condition that needs to be met to end the current chapter */
	private WinCondition winCondition;
	/** Creates a new ChapterMap with the specified rows and columns
	 *  ChapterMap starts as "Empty", containing only Grass Tiles
	 * @param col number of columns
	 * @param row number of rows
	 * @param game reference to our Game
	 */
	public ChapterMap(int col, int row, Game game) {
		this.game = game;
		tiles = new ArrayList<>();
		tileMap = new Tile[row][col];
		this.winCondition = WinCondition.Route;
		this.rows = row;//30 columns
		this.cols = col;//24 rows
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				GrassTile grass = new GrassTile(j, i, this);
				tileMap[i][j] = grass;
				tiles.add(grass);
			}
		}
		for (int i = 0; i < tiles.size(); i++) {
			tiles.get(i).findNeighbors();
		}
		currentTile = tileMap[0][0];
		nrow = Game.nRow; //16
		ncol = Game.nCol; //12
		topLeft = tileMap[0][0];
		bottomRight = tileMap[nrow - 1][ncol - 1];
		turnCount = 1;
		currentPhase = "AllyPhase";
		selectedBoxTile = currentTile;
		surviveTurnCount = 10;
		whichSceen = 1;
		rand = new Random();
		spawner = new Spawner(game, this);
	}
	/**
	 * Renders a miniature version of this chapter's map with various characteristics
	 * @param g
	 * @param width - width of the minimap in pixels
	 * @param startX - starting X position of minimap
	 * @param startY - starting Y position of minimap
	 * @param withUnits - 0 = no units | 1 = unit colors | 2 = unit pictures
	 */
	public void renderMiniMapVersion(Graphics g, int width, int startX, int startY, int withUnits) {
		
		int avg = (tileMap[0].length + tileMap.length)/2;
		
		int scale = width / (avg+4);
	
		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			tile.drawTileForMiniMap(g, scale, startX, startY, withUnits);
		}
		
	}
	
	public void setCurrentChapter(int chapt) {
		this.currentChapter = chapt;
		if (chapt == 8) surviveTurnCount = 15;
		this.winCondition = getWinCondition(chapt);
	}
	/** Returns the win condition for the chapter specified */
	private WinCondition getWinCondition(int whichChapter) {
		switch (currentChapter) {
		case 1: return WinCondition.Sieze;
		case 2: return WinCondition.Route;
		case 3: return WinCondition.Sieze;
		case 4: return WinCondition.Route;
		case 5: return WinCondition.KillBoss;
		case 6: return WinCondition.KillBoss;
		case 7: return WinCondition.Survive;
		case 8: return WinCondition.Capture;
		case 9: return WinCondition.KillBoss;
		case 10: return WinCondition.Sieze;
		case 11: return WinCondition.Route;
		case 12: return WinCondition.Survive;
		case 13: return WinCondition.Route;
		case 14: return WinCondition.KillBoss;
		case 15: return WinCondition.Sieze;
		default: return WinCondition.Route;
		}
	}
	
	public void renderItemReceived(Graphics g) {
		int[] b = {Game.WIDTH/5, Game.HEIGHT/3, 2*Game.WIDTH/3, Game.HEIGHT/6};
		
		g.setColor(Color.white);
		int th = 4;
		for (int i = 0; i < th; i++) {
			g.drawRect(b[0] + i, b[1] + i, b[2] - 2*i, b[3] - 2*i);
		}
		g.setColor(Color.GRAY);
		g.fillRect(b[0]+th, b[1]+th, b[2] - 2*th, b[3] - 2*th);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 50));
		g.drawString("Received A " + itemReceived, b[0] + Game.scale/2, b[1] + b[3]/2 + 8);
		
		long now = System.currentTimeMillis();
		if (now - itemReceivedTimer >= 1200) {
			itemReceivedTimer = System.currentTimeMillis();
			game.setGameState(STATE.Game);
		}
		
	}
	
	public void setItemReceived(String itemName) {
		this.itemReceived = itemName;
		itemReceivedTimer = System.currentTimeMillis();
	}
	
	public void tick() {
		//no need to update or check info when in cutscene
		if (inCutScene) {
			return;
		}
		// fix this later... current chapter never wants to be remembered for some reason
		if (currentChapter == 0) if (game.chapterOrganizer != null) currentChapter = game.chapterOrganizer.currentChapter;
		// check the kill boss win condition.. there is probably a better place for this
		if (winCondition == WinCondition.KillBoss) {
			if (turnCount > 0) {
				if (!game.chapterOrganizer.containsBoss()) {
					if (game.gameState == STATE.Game || game.gameState == STATE.EnemyPhase) game.endChapter();
				}
			}
		}
	}
	
	/** Sets the in game cut scenes that occur on specific chapters and turns */
	public void checkForCutScene() {
		if (currentChapter == 2 && turnCount == 2) {
			this.setCurrentTile(getTileAtAbsolutePos(2, 12));
			this.setInGameCutScene(1);
		} else if (currentChapter == 4 && turnCount == 2) {
			this.setCurrentTile(getTileAtAbsolutePos(16, 14));
			this.setInGameCutScene(1);
		} else if (currentChapter == 5 && turnCount == 2) {
			this.setInGameCutScene(1);
		} else if (currentChapter == 5 && turnCount == 1) {
			this.setInGameCutScene(2);
		} else if (currentChapter == 6 && turnCount == 1) {
			setInGameCutScene(2);
		} else if (currentChapter == 6 && turnCount == 2) {
			this.setCurrentTile(getTileAtAbsolutePos(0, 12));
			this.setInGameCutScene(1);
		} else if (currentChapter == 8 && turnCount == 1) {
			setInGameCutScene(2);
		} else if (currentChapter == 8 && turnCount == 2) {
			this.setInGameCutScene(1);
		} else if (currentChapter == 9 && turnCount == 2) {
			this.setCurrentTile(getTileAtAbsolutePos(1, 31));
			this.game.chapterOrganizer.addAlly(new Heath(1, 31, game, 9));
			this.setInGameCutScene(1);
		} else if (currentChapter == 12 && turnCount == 1) {
			setInGameCutScene(2);
		} else if (currentChapter == 11 && turnCount == 1) {
			setInGameCutScene(1);
		}
	}
	/** Focuses our screen on the lord */
	public void setScreenToLord() {
		if (game.chapterOrganizer.getLord() == null) return;
		setCurrentTile(game.chapterOrganizer.getLord().currentTile);
		findRegion();
	}
	
	public void setInGameCutScene(int whichSceen) {
		if (game.cutScenes != null) {
			this.inCutScene = true;
			game.cutScenes.startInGameScene(currentChapter, whichSceen);
		}
	}
	
	
	/** Returns a list of all forts on this map */
	public ArrayList<Tile> getAllForts() {
		ArrayList<Tile> forts = new ArrayList<>();
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i).category.equalsIgnoreCase("Fort")) forts.add(tiles.get(i));
		}
		return forts;
	}
	
	/** Brings the screen and current tile to the next ally with movement abilities */
	public void findAllyWithMoves() {

		ArrayList<AllyPlayer> allysWMove = game.chapterOrganizer.getAlliesWithMove();
		if (allysWMove.size() == 0) {
			this.allyWithMovesIndex = 0;
			return;
		}
		if (allyWithMovesIndex >= allysWMove.size()) {
			allyWithMovesIndex = 0;
		}
		
		setCurrentTile(allysWMove.get(allyWithMovesIndex).currentTile);
		findRegion();
		allyWithMovesIndex++;
		
		
	}
	
	public void move(Player p, Tile destTile) {
		Tile prevTile = p.currentTile;
		if (prevTile.placeEquals(destTile)) return;
		if (destTile.isOccupied()) {
			return;
		}
		p.xPos = destTile.x;
		p.yPos = destTile.y;
		p.setCurrentTile(destTile);
		destTile.setCarrier(p);
		prevTile.setCarrier(null);
		p.setCanMove(false);
		p.previousTile = prevTile;
		
		
	}
	
	public void nextTurn() {
		turnCount++;
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			game.chapterOrganizer.allys.get(i).setMAUT(true);
		}
		currentPhase = "AllyPhase";
		for (int i = 0; i < game.chapterOrganizer.enemys.size(); i++) {
			game.chapterOrganizer.enemys.get(i).setMAU(true);
		}
		if (game.chapterOrganizer.summonedUnit != null) {
			game.chapterOrganizer.summonedUnit.setMAU(true);
		}
		healUnitsOnForts();
		healUnitsWithSkills();
		spawner.spawnUnits();
		game.chapterOrganizer.checkForLoss();
		
		if (winCondition == WinCondition.Survive) {
			if (turnCount >= surviveTurnCount) {
				game.endChapter();
				this.chapterOver = true;
				return;
			}
		}
		game.chapterOrganizer.decAllyStatBuffs();
		setScreenToLord();
		checkForCutScene();
		
	}
	/** Heals every unit in the game who regenerates health on a turn basis because of their skill */
	private void healUnitsWithSkills() {
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			Player p = game.chapterOrganizer.allys.get(i);
			if (p.skill.isTurnDependant()) {
				if (p.skill.nameEquals("Empire Might")) {
					p.currentHP += p.HP/10;
				} else if (p.skill.nameEquals("Divine Wellness")) {
					p.currentHP += p.STR;
				}
			}
		}
		for (int i = 0; i < game.chapterOrganizer.enemys.size(); i++) {
			Player p = game.chapterOrganizer.enemys.get(i);
			if (p.skill.isTurnDependant()) {
				if (p.skill.nameEquals("Empire Might")) {
					p.currentHP += p.HP/10;
				} else if (p.skill.nameEquals("Divine Wellness")) {
					p.currentHP += p.STR;
				}
			}
		}
	}
	/** Heals all units on forts by 10% of their maximum HP */
	private void healUnitsOnForts() {
		ArrayList<Tile> fortTile = getAllForts();
		if (fortTile == null) return;
		if (fortTile.size() == 0) return;
		else {
			for (Tile a : fortTile) {
				if (a.isOccupied()) {
					a.carrier.currentHP += (int)(a.carrier.HP/10.0);
				}
			}
		}
	}
	
	public void nextPhase() {
		if (game.gameState == STATE.Game) {
			if (currentPhase.equalsIgnoreCase("AllyPhase")) {
				for (AllyPlayer a : game.chapterOrganizer.allys) a.setMAU(false); 
				currentPhase = "EnemyPhase";
				game.chapterOrganizer.enemyMove.setDestTileMap();
				game.setGameState(STATE.EnemyPhase);
				
			} else {
				nextTurn();
			}
		}
	}

	public void render(Graphics g) {

		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			tile.render(g);
		}
			
		if (drawHUD) drawHUD(g);
			
		if (game.gameState == STATE.AttackState || game.gameState == STATE.MoveState) {
			drawSelectedBoxOnTile(selectedBoxTile, g);
		} else {
			drawSelectedBox(g);
		}
			
		if (attackMenu != null) attackMenu.render(g);
		
		if (game.gameState == STATE.MiniMapView) {
			//g.setColor(Color.black);
			//g.fillRect(Game.WIDTH/6 - 20, Game.HEIGHT/6 - 20, Game.WIDTH/3 + 40, ((Game.WIDTH/3 + 20)/tileMap[0].length)*tileMap.length + 20);
			renderMiniMapVersion(g, Game.WIDTH/2, Game.WIDTH/8, Game.HEIGHT/6, 1);
		}
	}
	
	public void drawHUD(Graphics g) {
		if (game.designer != null) return;
		g.setColor(Color.black);
		int thickness = 2;
		int width = 160;
		int height = 50;
		for (int i = 0; i < thickness; i++) {
			g.drawRect(0 + i, 0 + i, width, height);
		}
		if (currentPhase.equalsIgnoreCase("AllyPhase")) g.setColor(Color.cyan);
		else g.setColor(Color.red); 
		g.fillRect(thickness, thickness, width - thickness, height - thickness);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString("Turn: " + turnCount, 5, height/3 + 5);
		g.drawString(currentPhase, 5, height/2 + height/4 + 5);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
		if (winCondition == WinCondition.Survive) {
			g.setFont(new Font("Times New Roman", Font.ITALIC, 17));
			g.drawString(winConditionToString(winCondition), 2*width/3 - 26, height/3 + 5);
			g.drawString("(" + surviveTurnCount + ")", 2*width/3 + 27, height/3 + 5);
		} else if (winCondition == WinCondition.Capture) {
			g.setFont(new Font("Times New Roman", Font.ITALIC, 14));
			g.drawString("CHESTS: ", 2*width/3 - 26, height/3 + 5);
			g.drawString(this.currentNumChests + "/" + this.maxChestsToWin, 2*width/3 + 27, height/3 + 5);
		} else g.drawString(winConditionToString(winCondition), 2*width/3 - 12, height/3 + 5);

		// the tile indicator specifying the type of tile and terrain bonuses offered
		 
		if (inAllyPhase()) {
			if (this.currentTile.xPos < nrow/2) {
				drawTileInfoSquare(g, 9*Game.WIDTH/10 - 35);
				drawPlayerInfoSquare(g, 8*Game.WIDTH/10 - 20);
			} else {
				drawTileInfoSquare(g, 7);
				drawPlayerInfoSquare(g, 7);
			}
		}
		
	}
	/** Returns true if the current win condition is equal to the input */
	public boolean winConditionEquals(WinCondition condition) {
		return (winCondition == condition);
	}
	
	public String winConditionToString(WinCondition condition) {
		switch (condition) {
		case Route: return "Route";
		case Sieze: return "Sieze";
		case Capture: return "Capture";
		case KillBoss: return "Kill Boss";
		case Survive: return "Survive";
		case Defend: return "Defend";
		default: return "Not a known win condition";
		}
	}
	/** Draws a square depicting Player information of the current Player we are hovered over */
	private void drawPlayerInfoSquare(Graphics g, int startX) {
		if (currentTile.carrier == null) return;
		Player player = currentTile.carrier;
		int startY = 55;
		int width = Game.WIDTH/5;
		int height = Game.HEIGHT/7;
		// draw blue box if ally, red if enemy
		if (player.isAlly()) g.setColor(Color.cyan);
		else g.setColor(Color.RED);
		g.fillRect(startX, startY, width, height);
		// draw our player's picture
		if (player.image != null) g.drawImage(player.image, startX + 5, startY + 5, width/3, height - 10, null);
		// set fonts and color for name
		g.setFont(new Font("Times New Roman", Font.BOLD, 45 - 3*Math.min(9, player.name.length())));
		g.setColor(Color.black);
		g.drawString(player.name, startX + width/2 - 3*Math.min(7, player.name.length()), startY + 27);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString("HP: " + player.currentHP + "/" + player.HP, startX + width/3 + 5, startY + height/2 + 8);
		if (player.equiptItem != null) {
			g.setFont(new Font("Times New Roman", Font.BOLD, 28 - player.equiptItem.name.length()));
			g.drawString(player.equiptItem.name, startX + width/3 + (width-5)/player.equiptItem.name.length() - 5, startY + height - 16);
		}
	}
	
	/** Draws the square depicting Tile information
	 * @param startX - where we will be drawing the information screen
	 */
	private void drawTileInfoSquare(Graphics g, int startX) {
		g.setColor(new Color(105,105,105));
		int startY = Game.HEIGHT-160;
		int wid = Game.WIDTH/11;
		int hei = 96;
		int thic = 5;
		//draw boxes
		g.fillRect(startX, startY, wid, hei);
		g.setColor(Color.cyan);
		g.fillRect(startX + thic, startY + thic, wid - 2*thic+1, (hei-2*thic+1)/2);
		//draw tile info
		g.setColor(Color.black);
		if (currentTile.category.length() > 6) {
			g.setFont(new Font("Times New Roman", Font.BOLD, 16));
			if (currentTile.category.equalsIgnoreCase("DamagedWall")) {
				g.drawString(currentTile.category.substring(0, 7), startX + thic, startY + hei/3 - 10);
				g.drawString(currentTile.category.substring(7), startX + thic, startY + hei/3 + 5);
			} else 	g.drawString(currentTile.category, startX + 2*thic - 2, startY + hei/3);
		} else {
			g.setFont(new Font("Times New Roman", Font.BOLD, 22));
			g.drawString(currentTile.category, startX + 2*thic, startY + hei/3);
		}
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		g.setColor(Color.white);
		g.drawString("DEF: " + currentTile.terrainBonuses[0], startX + thic, startY + hei/2 + hei/5 + 2);
		g.drawString("AVO: " + currentTile.terrainBonuses[1], startX + thic - 1, startY + hei/2 + 3*hei/8 + 5);
	}

	/**
	 * Returns the tile at the absolute X, Y positions in terms of the whole grid
	 * 
	 * @param x the true X position of this tile on the grid
	 * @param y the true Y position of this tile on the grid
	 * @return
	 */
	public Tile getTileAtAbsolutePos(int col, int row) {
		for (int i = 0; i < tiles.size(); i++) {
			Tile t = tiles.get(i);
			if (t.x == col && t.y == row) return t;
		}
		
		return null;
	}

	/**
	 * Returns the tile at the x and y position on the current screen, top left tile
	 * will be 0,0
	 * 
	 * @param xPos x position of this tile on the current screen
	 * @param yPos y position of this tile on the current screen
	 * @return
	 */
	public Tile getTileAtCurrentPos(int xPos, int yPos) {
		for (int i = 0; i < tiles.size(); i++) {
			Tile t = tiles.get(i);
			if (t.xPos == xPos && t.yPos == yPos)
				return t;
		}
		return null;
	}


	public void setCurrentTile(Tile t) {
		if (t == null) return;
		boolean moveLeft = (t.x - currentTile.x < 0);
		boolean moveRight = (t.x - currentTile.x > 0); 
		boolean moveUp = (t.y - currentTile.y < 0);
		boolean moveDown = (t.y - currentTile.y > 0);
		if (moveRight) {
			//need to check if on the rightmost side, if we are do not move the screen
			//if we are on the right half of the map we should move the screen
			if (bottomRight.x >= (cols - 1)) {
				this.currentTile = t;
			
			} else if (currentTile.xPos > (nrow/2) -1) { //right side of screen, xPos > 7
				moveScreen("right");
				this.currentTile = t;
				findRegion();
		
			} else {	//left side of screen
				this.currentTile = t;
				findRegion();
			}
			
		} else if (moveLeft) {

			//check if on leftmost side
			if (topLeft.x == 0) {
				this.currentTile = t;
			}else if (currentTile.xPos > (nrow/2) -1) { //right side of the screen
				this.currentTile = t;
				findRegion();
			} else { //left side of screen
				moveScreen("left");
				this.currentTile = t;
				findRegion();
			}
			
		} 
		if (moveUp) {

			if (topLeft.y == 0) {
				this.currentTile = t;
			} else if (currentTile.yPos < (ncol/2)) { //top of the screen
				moveScreen("up");
				this.currentTile = t;
				findRegion();
				
			} else { 	//bottom of screen
				this.currentTile = t;
				findRegion();
			
			}
			
		} else if (moveDown) {

			if (bottomRight.y >= (rows - 1)) {
				this.currentTile = t;
				
			} else if (currentTile.yPos <= (ncol/2)) { //top of the screen
				this.currentTile = t;
				findRegion();
				
			} else {	 //bottom of the screen
				moveScreen("down");
				this.currentTile = t;
				findRegion();
			
			}
		}
		
	}
	/** True if we are in an Ally Phase */
	public boolean inAllyPhase() {
		return currentPhase.equalsIgnoreCase("AllyPhase");
	}
	/** True if we are in an Enemy Phase */
	public boolean inEnemyPhase() {
		return currentPhase.equalsIgnoreCase("EnemyPhase");
	}
	/** Centers the current tile to our screen */
	public void findRegion() {
		
		int max = 0;
		if (currentTile.yPos < 0) {
			max =  Math.abs(currentTile.yPos) + Game.nCol/2;
			for (int i = 0; i < max; i++) {
				moveScreen("up");
			}
		} else if (currentTile.yPos >= Game.nCol) {
			max = Math.abs(Game.nCol-1-currentTile.yPos) + Game.nCol/2;
			for (int i = 0; i < max; i++) {
				moveScreen("down");
			}
		}
		if (currentTile.xPos < 0) {
			max = Math.abs(currentTile.xPos) + Game.nRow/2;
			for (int i = 0; i < max; i++) {
				moveScreen("left");
			}
		}
		if (currentTile.xPos >= Game.nRow) {
			max = Math.abs(Game.nRow-1-currentTile.xPos) + Game.nRow/2;
			for (int i = 0; i < max; i++) {
				moveScreen("right");
			}
		}
		int xp = nrow - Math.min(1, nrow/2 - currentTile.xPos);
		int yp =  ncol - Math.min(1, (ncol/2) - currentTile.yPos);
		
		Tile newRight = getTileAtCurrentPos(Math.min(xp, nrow-1), Math.min(yp, ncol-1));
		Tile newLeft = getTileAtCurrentPos(Math.max(0, xp-nrow), Math.max(0, yp-ncol));
		if (setBottomRight(newRight)) {
			if (setTopLeft(newLeft)) {
				
			}
		}
	}
	/** True if there is a pickable object adjacent to the source tile */
	public boolean pickableAdjacent(Tile source) {
		Tile check = getTileAtAbsolutePos(source.x-1, source.y);
		if (check != null) {
			if (check.category.equalsIgnoreCase("Chest") || check.category.equalsIgnoreCase("Door")) return true;
		}
		check = getTileAtAbsolutePos(source.x+1, source.y);
		if (check != null) {
			if (check.category.equalsIgnoreCase("Chest") || check.category.equalsIgnoreCase("Door")) return true;
		}
		check = getTileAtAbsolutePos(source.x, source.y-1);
		if (check != null) {
			if (check.category.equalsIgnoreCase("Chest") || check.category.equalsIgnoreCase("Door")) return true;
		}
		check = getTileAtAbsolutePos(source.x, source.y+1);
		if (check != null) {
			if (check.category.equalsIgnoreCase("Chest") || check.category.equalsIgnoreCase("Door")) return true;
		}
		// none have been chests or doors, return false
		return false;
	}
	
	/** Sets an alternative tile as the focus without setting our current tile to it 
	 * reference - the previous tile
	 * t - the new tile we are trying to move to*/
	public void setAlternativeTile(Tile reference, Tile t) {
		if (t == null) return;
		boolean moveLeft = (t.x - reference.x < 0);
		boolean moveRight = (t.x - reference.x > 0); 
		boolean moveUp = (t.y - reference.y < 0);
		boolean moveDown = (t.y - reference.y > 0);
		
		int movAmount = 0;
		
		if (moveRight) {
			
			movAmount = t.x - reference.x;
			if (reference.xPos > (nrow/2) -1) { //right side of screen, xPos > 7
				for (int i = 0; i < movAmount; i++) moveScreen("right");
			}
			
		} else if (moveLeft) {
			
			movAmount = reference.x - t.x;
			if (reference.xPos <= (nrow/2) -1) { //right side of the screen
				for (int i = 0; i < movAmount; i++)moveScreen("left");
			}
			
		} 
		if (moveUp) {
			
			movAmount = reference.y - t.y;
			if (reference.yPos < (ncol/2)) { //top of the screen
				for (int i = 0; i < movAmount; i++)moveScreen("up");
			}
			
		} else if (moveDown) {
			
			movAmount = t.y - reference.y;
			if (reference.yPos > (ncol/2)) { //top of the screen
				for (int i = 0; i < movAmount; i++)moveScreen("down");
			}
		}		
	}
	
	/**
	 * Sets the tile and returns true if it did so successfully
	 * 
	 * @param tile
	 * @return
	 */
	public boolean setTopLeft(Tile tile) {
		if (isATile(tile)) {
			topLeft = tile;
			return true;
		}
		return false;
	}

	public boolean setBottomRight(Tile tile) {
		if (isATile(tile)) {
			bottomRight = tile;
			return true;
		}
		return false;
	}

	public boolean isATile(Tile tile) {
		return tile != null;
	}
	/** Sets the existing Tile to be of the type newTile*/
	public void setTile(Tile existingTile, Tile newTile, int sprite) {
		if (newTile == null) return;
		if (existingTile == null) return;
		if (existingTile.x >= cols) return;
		if (existingTile.y >= rows) return;
		Player p = existingTile.carrier;
		if (p!= null) newTile.setCarrier(p);
		tileMap[existingTile.y][existingTile.x] = newTile;
		replace(existingTile, newTile);
		
		newTile.setxPos(existingTile.xPos);
		newTile.setyPos(existingTile.yPos);
		existingTile = newTile;
		if (sprite > 1000) {
			String s = String.valueOf(sprite);
			int val = Integer.valueOf(s.substring(0, 3));
			if (val == 299) {
				newTile.allySpawnTile = true;
				newTile.setSprite(Integer.valueOf(s.substring(3)));
			}
		} else {
			newTile.setSprite(sprite);
		}
		
	}
	/** Sets the existing Tile to be of type newTile and contains player player*/
	public void setTile(Tile existingTile, Tile newTile, Player player, ChapterOrganizer org, int sprite) {
		setTile(existingTile, newTile, sprite);
		// old stuff
		//	tileMap[existingTile.y][existingTile.x].setCarrier(player);
		//  org.addPlayer(player);
		// new stuff
		org.addPlayer(player);
		
	}
	private void moveScreen(String dir) {
		if (dir.equalsIgnoreCase("right")) {
			if (maxRightxPos()[0] < Game.nRow) return;
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setxPos(tiles.get(i).xPos - 1);
			}
		} else if (dir.equalsIgnoreCase("left")) {
			if (minRightxPos()[0] == 0) return;
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setxPos(tiles.get(i).xPos + 1);
			}
		} else if (dir.equalsIgnoreCase("up")) {
			if (minRightxPos()[1] == 0) return;
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setyPos(tiles.get(i).yPos + 1);
			}
		} else if (dir.equalsIgnoreCase("down")) {
			if (maxRightxPos()[1] < Game.nCol) return;
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setyPos(tiles.get(i).yPos - 1);
			}
		}
	}
	/** maximum xPos and yPos value of tiles */
	private int[] maxRightxPos() {
		int[] pos = new int[2];
		int maxX = 0;
		int maxY = 0;
		for (Tile t : tiles) {
			if (t.xPos > maxX) maxX = t.xPos;
			if (t.yPos > maxY) maxY = t.yPos;
		}
		pos[0] = maxX;
		pos[1] = maxY;
		return pos;
	}
	/** minimum xPos and yPos value of tiles */
	private int[] minRightxPos() {
		int[] pos = new int[2];
		int minX = 11110;
		int minY = 11110;
		for (Tile t : tiles) {
			if (t.xPos < minX) minX = t.xPos;
			if (t.yPos < minY) minY = t.yPos;
		}
		pos[0] = minX;
		pos[1] = minY;
		return pos;
	}
	/**
	 * Draws the selected box on the current tile of this map
	 * @param g
	 */
	public void drawSelectedBox(Graphics g) {
		int thickness = 5;
		int scale = Game.scale;
		g.setColor(Color.black);
		for (int i = 0; i < thickness; i++) {
			g.drawLine(currentTile.xPos * scale, currentTile.yPos * scale + i, currentTile.xPos * scale + scale / 4, currentTile.yPos * scale + i); // -
			g.drawLine(currentTile.xPos * scale + i, currentTile.yPos * scale, currentTile.xPos * scale + i, currentTile.yPos * scale + scale / 4); // |
			g.drawLine(currentTile.xPos * scale + i, currentTile.yPos * scale + scale, currentTile.xPos * scale + i, currentTile.yPos * scale + 3 * scale / 4); // |
			g.drawLine(currentTile.xPos * scale, currentTile.yPos * scale + scale - i, currentTile.xPos * scale + scale / 4, currentTile.yPos * scale + scale - i); // -
			g.drawLine(currentTile.xPos * scale + scale - i, currentTile.yPos * scale, currentTile.xPos * scale + scale - i, currentTile.yPos * scale + scale / 4); // |
			g.drawLine(currentTile.xPos * scale + scale, currentTile.yPos * scale + i, currentTile.xPos * scale + 3 * scale / 4, currentTile.yPos * scale + i); // -
			g.drawLine(currentTile.xPos * scale + scale - i, currentTile.yPos * scale + scale, currentTile.xPos * scale + scale - i,
					currentTile.yPos * scale + 3 * scale / 4); // |
			g.drawLine(currentTile.xPos * scale + scale, currentTile.yPos * scale + scale - i, currentTile.xPos * scale + 3 * scale / 4,
					currentTile.yPos * scale + scale - i); // -
		}
	}

	public void drawSelectedBoxOnTile(Tile currentTile, Graphics g) {
		int thickness = 5;
		int scale = Game.scale;
		g.setColor(Color.black);
		for (int i = 0; i < thickness; i++) {
			g.drawLine(currentTile.xPos * scale, currentTile.yPos * scale + i, currentTile.xPos * scale + scale / 4, currentTile.yPos * scale + i); // -
			g.drawLine(currentTile.xPos * scale + i, currentTile.yPos * scale, currentTile.xPos * scale + i, currentTile.yPos * scale + scale / 4); // |
			g.drawLine(currentTile.xPos * scale + i, currentTile.yPos * scale + scale, currentTile.xPos * scale + i, currentTile.yPos * scale + 3 * scale / 4); // |
			g.drawLine(currentTile.xPos * scale, currentTile.yPos * scale + scale - i, currentTile.xPos * scale + scale / 4, currentTile.yPos * scale + scale - i); // -
			g.drawLine(currentTile.xPos * scale + scale - i, currentTile.yPos * scale, currentTile.xPos * scale + scale - i, currentTile.yPos * scale + scale / 4); // |
			g.drawLine(currentTile.xPos * scale + scale, currentTile.yPos * scale + i, currentTile.xPos * scale + 3 * scale / 4, currentTile.yPos * scale + i); // -
			g.drawLine(currentTile.xPos * scale + scale - i, currentTile.yPos * scale + scale, currentTile.xPos * scale + scale - i,
					currentTile.yPos * scale + 3 * scale / 4); // |
			g.drawLine(currentTile.xPos * scale + scale, currentTile.yPos * scale + scale - i, currentTile.xPos * scale + 3 * scale / 4,
					currentTile.yPos * scale + scale - i); // -
		}
	}
	
	public ArrayList<Tile> getTilesStartingWith(Tile start) {
		ArrayList<Tile> tilez = new ArrayList<>();
		int index = 0;
		for (int i = 0; i < tiles.size(); i++) if (tiles.get(i).equals(start)) index = i;
		tilez.add(tiles.get(index));
		for (int i = 0; i < tiles.size(); i++) {
			if (i == index) continue;
			tilez.add(tiles.get(i));
		}
		return tilez;
	}
	public ArrayList<Tile> getTiles() {
		ArrayList<Tile> tilez = new ArrayList<>();
		for (int i = 0; i < tiles.size(); i++) {
			tilez.add(tiles.get(i));
		}
		return tilez;
	}
	
	public Tile findTileForDesigner(int x, int y) {
		for (Tile[] tileRow : tileMap) {
			for (Tile t : tileRow) {
				if (t.x == x && t.y == y) return t;
			}
		}
		System.out.println("No tile for designer at " + x + "," + y);
		return tileMap[x][y];
	}
	
	public void replace(Tile out, Tile in) {
		if (out == null || in == null) throw new RuntimeException("Cannot replace null elements!");
		Iterator<Tile> tileIt = tiles.iterator();
		int count = 0;
		while (tileIt.hasNext()) {
			Tile temp = tileIt.next();
			if (temp == out) {
				tileIt.remove();
				tiles.add(count, in);
				return;
			}
			count++;
		}
	}
	public void setAttackMenu(AttackMenu menu) {
		this.attackMenu = menu;
		this.inAttackMenu = true;
	}
	public void nullAttackMenu() {
		this.attackMenu = null;
		inAttackMenu = false;
	}
	/** Returns the next unoccupied fort available, null if none exist */
	public Tile getUnoccupiedFort() {
		ArrayList<Tile> unoccupiedForts = new ArrayList<>();
		for (int i = 0; i < tiles.size(); i++) {
			Tile t = tiles.get(i);
			if (t.category.equalsIgnoreCase("Fort")) {
				if (!t.isOccupied()) unoccupiedForts.add(t);
			}
		}
		if (unoccupiedForts.size() == 0) {
			return null;
		} else if (unoccupiedForts.size() == 1) {
			return unoccupiedForts.get(0);
		} else {
			return unoccupiedForts.get(rand.nextInt(unoccupiedForts.size()));
		}
	}
	/** Returns a list of all villages in this map */
	public ArrayList<Village> getAllVillages() {
		ArrayList<Village> vills = new ArrayList<>();
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i).category.equalsIgnoreCase("Village")) {
				if (tiles.get(i).getSpriteIndex()==0)vills.add((Village)tiles.get(i));
			}
		}
		return vills;
	}
	public void setGridLines(int gridWidth) {
		for (Tile a : tiles) {
			a.gridLines = gridWidth;
		}
	}
	public int getGridLines() {
		return tiles.get(0).gridLines;
	}
	/** Returns the tile in the middle of our view */
	public Tile getCenterTile() {
		return getTileAtCurrentPos(nrow/2, ncol/2);
	}
}
