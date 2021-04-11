package tiles;

import gameMain.ChapterMap;

public class Throne extends Tile{

	public Throne(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Throne";
		this.spriteMapImage = Tile.throneMap;
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 3;
		terrainBonuses[1] = 20;
		this.spriteMapCoordinates[0] = 81;
		this.spriteMapCoordinates[1] = 23;
		this.lengthWidthsOfSpriteMaps[0] = 14;
		this.lengthWidthsOfSpriteMaps[1] = 26;

		}

	@Override
	public void setSprite(int spriteIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSpriteIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 1;
	}
}
