package tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import characters.*;
import extras.*;
import gameMain.*;

public class Tile {
	/** Where the tile is in terms of the current grid of tiles */
	public int xPos, yPos;
	/** The permanent position of this tile when the grid is created (top left 0, 0)*/
	public int x,y;
	/**The size of each tile*/
	public int scale = Game.scale;
	/** Ground, Mud, Water, Grass, */
	public String category;
	/** The imageManager for drawing tiles to the screen */
	public ImageManager IM = Game.IM;
	/**The image that will be displayed for this tile*/
	public Image image;
	/**The player who is standing upon this tile */
	public Player carrier;
	/**Whether or not this tile is being viewed by the player*/
	public boolean isVisible;
	/**Instance of the map holding these tiles*/
	public ChapterMap map;
	/**indicates whether this tile can be pathed to or not */
	public boolean pathable;
	/**Indicats whether or not this tile is part of a path*/
	public boolean arrow, arrowHead;
	/** a Tile isCrossable if it is not a wall or water*/
	public boolean isCrossable;
	/** a Tile isGround if it has no specific terrain effects and isCrossable*/
	public boolean isGround;
	
	public Tile(int x, int y, ChapterMap map) {
		this.x = x;
		this.y = y;
		this.xPos = x;
		this.yPos = y;
		this.map = map;
		map.tiles.add(this);
	}
	
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	public void setCarrier(Player p) {
		carrier = p;
	}
	public boolean isOccupied() {
		if (carrier != null) return true;
		else return false;
	}
	public void render(Graphics g) {
		if (arrow) {
			g.setColor(Color.black);
			g.fillRect(xPos * scale, yPos * scale, scale - 1, scale - 1);
			if (carrier != null) carrier.render(g);
			return;
		}
		if (pathable) {
			g.setColor(Color.blue);
			g.fillRect(xPos * scale, yPos * scale, scale - 1, scale - 1);
			if (carrier != null) carrier.render(g);
			return;
		}
		g.drawImage(image, xPos *  scale, yPos * scale, scale-1, scale-1, null);
		if (carrier != null) carrier.render(g);
		
	}
	public void setPathable(boolean tf) {
		pathable = tf;
	}
	public void setArrow(boolean tf) {
		this.arrow = tf;
	}
	public void setArrowHead(boolean tf) {
		this.arrowHead = tf;
	}
}
