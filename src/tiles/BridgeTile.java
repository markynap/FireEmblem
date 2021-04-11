package tiles;

import gameMain.ChapterMap;

public class BridgeTile extends Tile{

	public BridgeTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Bridge";
		this.isGround = true;
		this.isCrossable = true;
		this.spriteMapImage = Tile.waterTerrainMap;
		this.spriteMapCoordinates[0] = 48;
		this.spriteMapCoordinates[1] = 110;
		this.lengthWidthsOfSpriteMaps[0] = 16;
		this.lengthWidthsOfSpriteMaps[1] = 20;
	}

	@Override
	public void setSprite(int spriteIndex) {
		
		switch (spriteIndex) {
		case 0: this.spriteMapCoordinates[0] = 48;
				this.spriteMapCoordinates[1] = 110;
				this.lengthWidthsOfSpriteMaps[0] = 16;
				this.lengthWidthsOfSpriteMaps[1] = 20;
				break;
		case 1: this.spriteMapCoordinates[0] = 240;
				this.spriteMapCoordinates[1] = 31;
				this.lengthWidthsOfSpriteMaps[0] = 16;
				this.lengthWidthsOfSpriteMaps[1] = 18;
				break;
		}
		
	}

	@Override
	public int getSpriteIndex() {

		switch (spriteMapCoordinates[0]) {
		
		case 48: return 0;
		case 240: return 1;
		default: System.out.println("Wrong sprite index BridgeTile"); return 0;
		
		}
		
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 2;
	}

}
