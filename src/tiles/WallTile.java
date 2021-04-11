package tiles;

import gameMain.ChapterMap;

public class WallTile extends Tile{
	
	public WallTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.isCrossable = false;
		this.isGround = false;
		this.category = "Wall";
		this.spriteMapImage = Tile.pillarStonesMap;
		this.spriteMapCoordinates[0] = 48;
		this.spriteMapCoordinates[1] = 48;
		this.lengthWidthsOfSpriteMaps[1] = 16;
	}

	@Override
	public void setSprite(int spriteIndex) {

		switch (spriteIndex) {
		case 0: this.spriteMapImage = Tile.pillarStonesMap;
				this.spriteMapCoordinates[0] = 48;
				this.spriteMapCoordinates[1] = 48;
				this.lengthWidthsOfSpriteMaps[1] = 16;
				break;
		case 1: this.spriteMapImage = Tile.pillarStonesMap;
				this.spriteMapCoordinates[0] = 146;
				this.spriteMapCoordinates[1] = 32;
				this.lengthWidthsOfSpriteMaps[0] = 11;
				this.lengthWidthsOfSpriteMaps[1] = 16;
				break;
		case 2: this.spriteMapImage = Tile.pillarStonesMap;
				this.spriteMapCoordinates[0] = 0;
				this.spriteMapCoordinates[1] = 96;
				this.lengthWidthsOfSpriteMaps[0] = 15;
				this.lengthWidthsOfSpriteMaps[1] = 15;
				break;
		case 3: this.spriteMapImage = Tile.pillarStonesMap;
				this.spriteMapCoordinates[0] = 33;
				this.spriteMapCoordinates[1] = 109;
				this.lengthWidthsOfSpriteMaps[0] = 14;
				this.lengthWidthsOfSpriteMaps[1] = 33;
				break;
	
		default: System.out.println("setSprite Index not specified in WallTile.Java : " + spriteIndex);
		}
	}

	@Override
	public int getSpriteIndex() {
		
		switch (spriteMapCoordinates[0]) {
		
		case 48: return 0;
		case 146: return 1;
		case 0: return 2;
		case 33: return 3;
		default: System.out.println("Case not specified in getSpriteIndex() in WallTile.java"); return 0;
		}
		
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 4;
	}

}
