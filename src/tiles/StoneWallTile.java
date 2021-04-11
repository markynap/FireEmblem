package tiles;

import gameMain.ChapterMap;

public class StoneWallTile extends Tile{

	public StoneWallTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "StoneWall";
		this.isGround = false;
		this.isCrossable = false;
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
