package tiles;

import java.awt.Graphics;

import gameMain.ChapterMap;

public class TreeTile extends Tile{

	public TreeTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Tree";
		image = IM.getImage("/tree.png");
		this.isGround = false;
		this.isCrossable = true;
	}
	
	public void render(Graphics g) {
		g.drawImage(IM.getImage("/grass.png"), xPos * scale, yPos * scale, scale, scale, null);
		g.drawImage(image, xPos * scale, yPos * scale, scale, scale, null);
	}
}
