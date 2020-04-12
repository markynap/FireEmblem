package tiles;

import java.awt.Color;
import java.awt.Graphics;

import gameMain.ChapterMap;

public class TreeTile extends GrassTerrainTile{

	public TreeTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Tree";
		image = IM.getImage("/tree.png");
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 1;
		terrainBonuses[1] = 10;
	}
	
}
