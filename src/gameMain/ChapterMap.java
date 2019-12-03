package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;

import characters.Player;
import gameMain.Game.STATE;
import tiles.*;

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
	
	public void move(Player p, Tile destTile) {
		Tile prevTile = p.currentTile;
		if (prevTile == destTile) return;
		p.xPos = destTile.xPos;
		p.yPos = destTile.yPos;
		p.setCurrentTile(destTile);
		destTile.setCarrier(p);
		prevTile.setCarrier(null);
		p.setCanMove(false);
	}
	
	public void nextTurn() {
		turnCount++;
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			game.chapterOrganizer.allys.get(i).populateMAU();;
		}
		for (int i = 0; i < game.chapterOrganizer.enemys.size(); i++) {
			game.chapterOrganizer.enemys.get(i).populateMAU();;
		}
		currentPhase = "AllyPhase";
	}
	
	public void nextPhase() {
		if (currentPhase.equalsIgnoreCase("AllyPhase")) {
		currentPhase = "EnemyPhase";
		} else {
			nextTurn();
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
			if (bottomRight.x == (rows - 1)) {
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
			if (bottomRight.y == (cols - 1)) {
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
		Tile st = currentTile;
		int xp = 12 - Math.min(0, 6 - st.xPos);
		if (xp > nrow - 1) {
			xp = nrow -1;
		}
		int yp =  10 - Math.min(0, 5 - st.yPos);
		if (yp > ncol - 1) {
			yp = ncol -1;
		}
		Tile newRight = getTileAtCurrentPos(xp, yp);
		Tile newLeft = getTileAtCurrentPos(Math.max(0, bottomRight.xPos-12), Math.max(0, bottomRight.yPos-10));
		if (setBottomRight(newRight)) {
			if (setTopLeft(newLeft)) {
				
			} else {
				System.out.println("top left dont work in findRegion");
			}
		} else {
			System.out.println("bottom right not work in findRegion");
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
		tileMap[existingTile.x][existingTile.y] = newTile;
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
	
}
