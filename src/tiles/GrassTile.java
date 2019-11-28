package tiles;

import gameMain.ChapterMap;

public class GrassTile extends Tile{

	public GrassTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Grass";
		image = IM.getImage("/grass.png");
		this.isCrossable = true;
		this.isGround = true;
	}
	
	

}
