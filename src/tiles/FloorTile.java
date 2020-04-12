package tiles;

import gameMain.ChapterMap;

public class FloorTile extends Tile {

	public FloorTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		image = IM.getImage("/floor.png");
		this.category = "Floor";
		this.isGround = true;
		this.isCrossable = true;
	}
	
	
}
