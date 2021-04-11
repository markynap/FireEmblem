package tiles;

import gameMain.ChapterMap;

public class StairsTile extends Tile{
	
	public StairsTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Stairs";
		this.isGround = true;
		this.isCrossable = true;
		this.spriteMapImage = Tile.pillarStonesMap;
		this.spriteMapCoordinates[0] = 17;
		this.spriteMapCoordinates[1] = 112;
		this.lengthWidthsOfSpriteMaps[1] = 20;
	}

	@Override
	public void setSprite(int spriteIndex) {

		switch (spriteIndex) {
		
		case 0: 
			this.spriteMapCoordinates[0] = 17;
			this.spriteMapCoordinates[1] = 112;
			this.lengthWidthsOfSpriteMaps[0] = 18;
			this.lengthWidthsOfSpriteMaps[1] = 20;
			break;
		case 1:
			this.spriteMapCoordinates[0] = 232;
			this.spriteMapCoordinates[1] = 128;
			this.lengthWidthsOfSpriteMaps[0] = 21;
			this.lengthWidthsOfSpriteMaps[1] = 16;
		
		}
		
	}

	@Override
	public int getSpriteIndex() {
		// TODO Auto-generated method stub
		switch (spriteMapCoordinates[0]) {
		case 17: return 0;
		case 232: return 1;
		default: System.out.println("No case identified in StairsTile"); return 0;
		}
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 2;
	}

}
