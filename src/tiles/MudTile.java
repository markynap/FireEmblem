package tiles;

import gameMain.ChapterMap;

public class MudTile extends Tile {
	
	public MudTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Mud";
		this.spriteMapImage = Tile.grassyTerrainMap;
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 1;
		terrainBonuses[1] = -10;
	}
	
	@Override
	public int movementTax() {
		return 3;
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
