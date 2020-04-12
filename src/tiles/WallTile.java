package tiles;

import gameMain.ChapterMap;
import gameMain.Game;

public class WallTile extends Tile{

	public WallTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.isCrossable = false;
		this.isGround = false;
		this.image = Game.IM.getImage("/wall.png");
		this.category = "Wall";
	}

}
