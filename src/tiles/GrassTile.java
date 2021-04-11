package tiles;

import gameMain.ChapterMap;

public class GrassTile extends Tile {

	public GrassTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Grass";
		this.spriteMapImage = Tile.grassyTerrainMap;
		this.spriteMapCoordinates[0] = 0* 17;
		this.spriteMapCoordinates[1] = 7* 17;
		this.isCrossable = true;
		this.isGround = true;
	}

	@Override
	public void setSprite(int spriteIndex) {

		switch (spriteIndex) {
		
		case 0: this.spriteMapImage = Tile.grassyTerrainMap;
				this.spriteMapCoordinates[0] = 0;
				this.spriteMapCoordinates[1] = 7* 17;
				break;
		case 1: this.spriteMapImage = Tile.armoryMap;
				this.spriteMapCoordinates[0] = 140;
				this.spriteMapCoordinates[1] = 110;
				break;
		case 2: this.spriteMapImage = Tile.armoryMap;
				this.spriteMapCoordinates[0] = 83;
				this.spriteMapCoordinates[1] = 131;
				break;
		case 3: this.spriteMapImage = Tile.armoryMap;
				this.spriteMapCoordinates[0] = 37;
				this.spriteMapCoordinates[1] = 118;
				break;
		case 4: this.spriteMapImage = Tile.armoryMap;
				this.spriteMapCoordinates[0] = 2;
				this.spriteMapCoordinates[1] = 0;
				break;
		case 5: this.spriteMapImage = Tile.armoryMap;
				this.spriteMapCoordinates[0] = 68;
				this.spriteMapCoordinates[1] = 138;
				break;
		}
	}

	@Override
	public int getSpriteIndex() {
		switch (spriteMapCoordinates[0]) {
		
		case 0: return 0;
		case 140: return 1;
		case 83: return 2;
		case 37: return 3;
		case 2: return 4;
		case 68: return 5;
		default: return 0;
		
		}
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 6;
	}
	

}
