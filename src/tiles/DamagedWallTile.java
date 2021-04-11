package tiles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameMain.ChapterMap;
import gameMain.Game;

public class DamagedWallTile extends Tile{
	
	/** The amount of damage it takes to break this wall */
	private int health;

	public DamagedWallTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.isCrossable = false;
		this.isGround = false;
		this.category = "DamagedWall";
		this.spriteMapImage = Tile.brokenWallMap;
		this.spriteMapCoordinates[0] = 50;
		this.spriteMapCoordinates[1] = 15;
		this.lengthWidthsOfSpriteMaps[0] = 11;
		this.lengthWidthsOfSpriteMaps[1] = 17;	
		this.health = 40;
		}
	/** Decrements the health of this Damaged Wall tile by a specified amount
	 * if the health of this wall falls below zero it gets replaced by a floor tile 
	 * @param amount - must be positive
	 */
	public void decHealth(int amount) {
		health -= amount;
		if (health <= 0) {
			setSprite(1);
		}
	}

	@Override
	public void setSprite(int spriteIndex) {

		switch (spriteIndex) {
		
		case 0: this.spriteMapImage = Tile.brokenWallMap;
				this.spriteMapCoordinates[0] = 50;
				this.spriteMapCoordinates[1] = 15;
				this.lengthWidthsOfSpriteMaps[0] = 11;
				this.lengthWidthsOfSpriteMaps[1] = 17;
				this.isCrossable = false;
				this.isGround = false;
				break;
			
		case 1: this.spriteMapImage = Tile.pillarStonesMap;
				this.spriteMapCoordinates[0] = 65;
				this.spriteMapCoordinates[1] = 0;
				this.lengthWidthsOfSpriteMaps[0] = 15;
				this.lengthWidthsOfSpriteMaps[1] = 15;	
				this.isCrossable = true;
				this.isGround = true;
				break;
		
		}
		
	}

	@Override
	public int getSpriteIndex() {

		switch (spriteMapCoordinates[0]) {
		
		case 50: return 0;
		case 65: return 1;
		default: 
			System.out.println("SPRITE INDEX NOT ACCOUNTED FOR IN DAMAGEDWALL TILE");
			return 0;
		
		}
		
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 2;
	}
	@Override
	public void render(Graphics g) {
		super.render(g);
		if (!isCrossable) {
			g.setColor(Color.red);
			g.setFont(new Font("Times New Roman", Font.BOLD, 15));
			g.drawString(String.valueOf(health), xPos * Game.scale + 2*Game.scale/3 - 2, yPos * Game.scale + Game.scale/4 + 2);
		}
	}

}
