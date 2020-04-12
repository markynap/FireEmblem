package tiles;

import java.awt.Graphics;

import gameMain.ChapterMap;

public class WaterTile extends Tile{

	public WaterTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Water";
		image = IM.getImage("/water.png");
		this.isGround = false;
		this.isCrossable = false;
		terrainBonuses[0] = 0;
		terrainBonuses[1] = 30;
	}

	public void render(Graphics g) {
		g.drawImage(IM.getImage("/grass.png"), xPos * scale, yPos * scale, scale-1, scale-1, null);
		g.drawImage(image, xPos * scale, yPos * scale, scale-1, scale-1, null);
		if (carrier != null) carrier.render(g);
	}
}
