package tiles;

import java.awt.Color;
import java.awt.Graphics;

import gameMain.ChapterMap;

public class GrassTerrainTile extends Tile{

	public GrassTerrainTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		
	}
	
	public void render(Graphics g) {
		g.drawImage(IM.getImage("/grass.png"), xPos * scale, yPos * scale, scale-1, scale-1, null);
		g.drawImage(image, xPos * scale, yPos * scale, scale-1, scale-1, null);
		if (carrier != null) carrier.render(g);
		if (pathable) {
			g.setColor(Color.blue);
			g.fillRect(xPos * scale, yPos * scale, scale - 1, scale - 1);
			if (carrier != null) carrier.render(g);
			return;
		}
	}

}
