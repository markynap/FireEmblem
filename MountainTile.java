package tiles;

import java.awt.Graphics;

import gameMain.ChapterMap;

public class MountainTile extends GrassTerrainTile{

	public MountainTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Mountain";
		image = IM.getImage("/mountain.png");
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 3;
		terrainBonuses[1] = 20;
	}

}
