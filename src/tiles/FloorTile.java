package tiles;

import gameMain.ChapterMap;

public class FloorTile extends Tile {

	public FloorTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Floor";
		this.isGround = true;
		this.isCrossable = true;
		this.spriteMapImage = Tile.pillarStonesMap;
		this.spriteMapCoordinates[0] = 4*15 + 5;
		this.spriteMapCoordinates[1] = 0;
		this.lengthWidthsOfSpriteMaps[0] = 15;
		this.lengthWidthsOfSpriteMaps[1] = 15;
	}

	@Override
	public void setSprite(int spriteIndex) {

		this.spriteMapCoordinates[0] = 4*15 + 5;
		this.spriteMapCoordinates[1] = 0;
		this.lengthWidthsOfSpriteMaps[0] = 15;
		this.lengthWidthsOfSpriteMaps[1] = 15;
		
	}

	@Override
	public int getSpriteIndex() {
		return 0; //default
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	
}
