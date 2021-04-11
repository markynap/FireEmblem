package tiles;

import gameMain.ChapterMap;

public class DoorTile extends Tile{

	public DoorTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.isCrossable = false;
		this.isGround = false;
		this.category = "Door";
		this.spriteMapImage = Tile.throneMap;
		this.spriteMapCoordinates[0] = 144;
		this.spriteMapCoordinates[1] = 64;
		this.lengthWidthsOfSpriteMaps[0] = 16;
		this.lengthWidthsOfSpriteMaps[1] = 17;

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
