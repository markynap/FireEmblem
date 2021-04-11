package tiles;

import gameMain.ChapterMap;

public class TreeTile extends Tile{

	public TreeTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Tree";
		this.spriteMapImage = Tile.grassyTerrainMap;
		this.spriteMapCoordinates[0] = 4 * lengthWidthsOfSpriteMaps[0];
		this.spriteMapCoordinates[1] = 0;
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 1;
		terrainBonuses[1] = 10;
	}

	@Override
	public void setSprite(int spriteIndex) {

		this.spriteMapCoordinates[0] = 4 * lengthWidthsOfSpriteMaps[0];
		this.spriteMapCoordinates[1] = 0;
		
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
