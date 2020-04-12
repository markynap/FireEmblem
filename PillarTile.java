package tiles;

import java.awt.Graphics;

import gameMain.ChapterMap;

public class PillarTile extends Tile{

	public PillarTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		image = IM.getImage("/pillar.png");
		this.category = "Pillar";
		this.isGround = false;
		this.isCrossable = false;
	}
	
	public void render(Graphics g) {
		g.drawImage(IM.getImage("/floor.png"), xPos * scale, yPos * scale, scale-1, scale-1, null);
		g.drawImage(image, xPos * scale, yPos * scale, scale, scale-1, null);
		if (carrier != null) carrier.render(g);
	}

}
