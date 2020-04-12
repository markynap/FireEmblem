package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import characters.AllyPlayer;
import characters.Player;
import gameMain.Game.STATE;
import graphics.AttackMenu;
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
	// COMBINE THE SCREEN OBSERVER WITH THE CHAPTER MAP
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
	public String currentPhase;
	public Game game;
	public Tile selectedBoxTile;
	
	public boolean inAttackMenu;
	public AttackMenu attackMenu;

	public ChapterMap(int col, int row, Game game) {
		this.game = game;
		tiles = new ArrayList<>();
		tileMap = new Tile[row][col];
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
		nrow = Game.nRow; //12
		ncol = 10;
		topLeft = tileMap[0][0];
		bottomRight = tileMap[nrow - 1][ncol - 1];
		turnCount = 1;
		currentPhase = "AllyPhase";
		selectedBoxTile = currentTile;
	}

	public void tick() {
	
	}
	
	public void findAllyWithMoves() {
		Player p = currentTile.carrier;
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = game.chapterOrganizer.allys.get(i);
			if (ally.canMove && ally != p) {
				int xDist = currentTile.x - ally.xPos;
				int yDist = currentTile.y - ally.yPos;
				int currentX = currentTile.x;
				int currentY = currentTile.y;
				for (int j = 0; j < Math.abs(xDist); j++) {
					if (xDist < 0) setCurrentTile(getTileAtAbsolutePos(currentX + j, currentY));
					else  setCurrentTile(getTileAtAbsolutePos(currentX - j, currentY));
				}
				for (int j = 0; j < Math.abs(yDist); j++) {
					if (yDist < 0)  setCurrentTile(getTileAtAbsolutePos(currentX, currentY + j));
					else  setCurrentTile(getTileAtAbsolutePos(currentX, currentY - j));
				}
				setCurrentTile(ally.currentTile);
				findRegion();
				
			}
		}
	}
	
	public void move(Player p, Tile destTile) {
		Tile prevTile = p.currentTile;
		if (prevTile.placeEquals(destTile)) return;
		if (destTile.isOccupied()) {
			System.out.println("Cannot move to occupied tile!");
			return;
		}
		p.xPos = destTile.x;
		p.yPos = destTile.y;
		p.setCurrentTile(destTile);
		destTile.setCarrier(p);
		prevTile.setCarrier(null);
		p.setCanMove(false);
		if (destTile.category.equalsIgnoreCase("Village")) {
			Village vill  = (Village)destTile;
			vill.visit(p);
		}
	}
	
	public void nextTurn() {
		turnCount++;
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			game.chapterOrganizer.allys.get(i).populateMAU();;
		}
		currentPhase = "AllyPhase";
		for (int i = 0; i < game.chapterOrganizer.enemys.size(); i++) {
			game.chapterOrganizer.enemys.get(i).populateMAU();;
		}
	}
	
	public void nextPhase() {
		if (game.gameState == STATE.Game) {
			if (currentPhase.equalsIgnoreCase("AllyPhase")) {
				for (AllyPlayer a : game.chapterOrganizer.allys) a.setMAU(false); 
				currentPhase = "EnemyPhase";
				game.enemyMove.setDestTileMap();
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
		drawHUD(g);
		if (game.gameState == STATE.AttackState || game.gameState == STATE.MoveState) {
			drawSelectedBoxOnTile(selectedBoxTile, g);
		} else {
		drawSelectedBox(g);
		}
		if (attackMenu != null) attackMenu.render(g);
	}
	
	public void drawHUD(Graphics g) {
		g.setColor(Color.black);
		int thickness = 2;
		int width = 130;
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
		System.out.println("no tile located at " + row + "," + col);
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



	private void moveScreen(String dir) {
		if (dir.equalsIgnoreCase("right")) {
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setxPos(tiles.get(i).xPos - 1);
			}
		} else if (dir.equalsIgnoreCase("left")) {
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setxPos(tiles.get(i).xPos + 1);
			}
		} else if (dir.equalsIgnoreCase("up")) {
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setyPos(tiles.get(i).yPos + 1);
			}
		} else if (dir.equalsIgnoreCase("down")) {
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).setyPos(tiles.get(i).yPos - 1);
			}
		}
	}

	private void drawSelectedBox(Graphics g) {
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

	public void setCurrentTile(Tile t) {
		if (t == null) return;
		boolean moveLeft = false;
		boolean moveRight = false; 
		boolean moveUp = false;
		boolean moveDown = false;
		if (t.x - currentTile.x < 0) moveLeft = true;
		if (t.x - currentTile.x > 0) moveRight = true;
		if (t.y - currentTile.y < 0) moveUp = true;
		if (t.y - currentTile.y > 0) moveDown = true;
		if (moveRight) {
			//need to check if on the rightmost side, if we are do not move the screen
			//if we are on the right half of the map we should move the screen
			if (bottomRight.x >= (cols - 1)) {
				this.currentTile = t;
				return;
			}
			if (currentTile.xPos > (nrow/2) -1) { //right side of screen, xPos > 5
				moveScreen("right");
				this.currentTile = t;
				findRegion();
				return;
			} else {	//left side of screen
				this.currentTile = t;
				findRegion();
				return;
			}
			
		} else if (moveLeft) {
			//check if on leftmost side
			if (topLeft.x == 0) {
				this.currentTile = t;
				return;
			}
			if (currentTile.xPos > (nrow/2) -1) { //right side of the screen
				this.currentTile = t;
				findRegion();
				return;
			} else { //left side of screen
				moveScreen("left");
				this.currentTile = t;
				findRegion();
				return;
			}
			
		} else if (moveUp) {
			if (topLeft.y == 0) {
				this.currentTile = t;
				return;
			}
			if (currentTile.yPos < (ncol/2)) { //top of the screen
				moveScreen("up");
				this.currentTile = t;
				findRegion();
				return;
			} else { 	//bottom of screen
				this.currentTile = t;
				findRegion();
				return;
			}
			
		} else if (moveDown) {
			if (bottomRight.y >= (rows - 1)) {
				this.currentTile = t;
				return;
			}
			if (currentTile.yPos <= (ncol/2)) { //top of the screen
				this.currentTile = t;
				findRegion();
				return;
			} else {	 //bottom of the screen
				moveScreen("down");
				this.currentTile = t;
				findRegion();
				return;
			}
		}		
	}

	public void findRegion() {
		int xp = 12 - Math.min(0, 6 - currentTile.xPos);
		if (xp > nrow - 1) {
			xp = nrow -1;
		}
		int yp =  10 - Math.min(0, 5 - currentTile.yPos);
		if (yp > ncol - 1) {
			yp = ncol -1;
		}
		Tile newRight = getTileAtCurrentPos(xp, yp);
	//	Tile newLeft = getTileAtCurrentPos(Math.max(0, bottomRight.xPos-12), Math.max(0, bottomRight.yPos-10));
		Tile newLeft = getTileAtCurrentPos(Math.max(0, xp-12), Math.max(0, yp-10));
		if (setBottomRight(newRight)) {
			if (setTopLeft(newLeft)) {
				
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
		if (tile != null) {
			return true;
		} else {
			System.out.println("Tile is null");
			return false;
		}
	}
	/** Sets the existing Tile to be of the type newTile*/
	public void setTile(Tile existingTile, Tile newTile) {
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
		
	}
	/** Sets the existing Tile to be of type newTile and contains player player*/
	public void setTile(Tile existingTile, Tile newTile, Player player, ChapterOrganizer org) {
		setTile(existingTile, newTile);
		//if (player != null) game.chapterOrganizer.addPlayer(player);
		if (player != null) org.addPlayer(player);
		tileMap[existingTile.y][existingTile.x].setCarrier(player);
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
}
