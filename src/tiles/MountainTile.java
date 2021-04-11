package tiles;

import gameMain.ChapterMap;

public class MountainTile extends Tile{

	public MountainTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Mountain";
		this.spriteMapImage = Tile.grassyTerrainMap;
		this.spriteMapCoordinates[0] = 143;
		this.spriteMapCoordinates[1] = 63;
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 3;
		terrainBonuses[1] = 20;
	}
	
	@Override
	public int movementTax() {
		return 2;
	}

	@Override
	public void setSprite(int spriteIndex) {
		
		switch (spriteIndex) {
		case 0: this.spriteMapCoordinates[0] = 143;
				this.spriteMapCoordinates[1] = 63;
				this.isCrossable = true;
				break;
		
		case 1: this.spriteMapCoordinates[0] = 210;
				this.spriteMapCoordinates[1] = 1;
				this.isCrossable = false;
				break;
				
		case 2: this.spriteMapCoordinates[0] = 176;
				this.spriteMapCoordinates[1] = 71;
				this.isCrossable = false;
				break;
		
		case 3: this.spriteMapCoordinates[0] = 240;
				this.spriteMapCoordinates[1] = 1;	
				this.isCrossable = false;
				break;
				
		case 4: this.spriteMapCoordinates[0] = 228;
				this.spriteMapCoordinates[1] = 20;	
				this.isCrossable = false;
				break;
				
		case 5: this.spriteMapCoordinates[0] = 215;
				this.spriteMapCoordinates[1] = 22;	
				this.isCrossable = false;
				break;
		
		
		}
	}

	@Override
	public int getSpriteIndex() {

		switch (spriteMapCoordinates[0]) {
		
		case 143: return 0;
		case 210: return 1;
		case 176: return 2;
		case 240: return 3;
		case 228: return 4;
		case 215: return 5;
		default: return 0;
		
		}
		
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 6;
	}

}
