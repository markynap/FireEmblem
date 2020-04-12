package tiles;

import java.awt.Graphics;

import gameMain.ChapterMap;

public class Throne extends Tile{

	public Throne(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Throne";
		image = IM.getImage("/throne.png");
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 3;
		terrainBonuses[1] = 20;
		}

	public void render(Graphics g) {
		g.drawImage(IM.getImage("/floor.png"), xPos * scale, yPos * scale, scale-1, scale-1, null);
		g.drawImage(image, xPos * scale, yPos * scale, scale-1, scale-1, null);
		if (carrier != null) carrier.render(g);
	}
}
