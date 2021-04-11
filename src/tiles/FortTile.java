package tiles;

import gameMain.ChapterMap;

/** Heals its carrier each turn and has the chance to spawn enemies */
public class FortTile extends Tile{

	public FortTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Fort";
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 1;
		terrainBonuses[1] = 15;
		this.spriteMapImage = Tile.grassyTerrainMap;
		this.spriteMapCoordinates[0] = 6* 17 - 7;
		this.spriteMapCoordinates[1] = 5* 17 - 6;
	}

	@Override
	public void setSprite(int spriteIndex) {

		this.spriteMapCoordinates[0] = 6* 17 - 7;
		this.spriteMapCoordinates[1] = 5* 17 - 6;
		
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
