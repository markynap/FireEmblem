package extras;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameMain.Game;

public class LoadScreen {


	public LoadScreen() {
		
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 80));
		g.drawString("Loading . . .", Game.WIDTH/3, Game.HEIGHT/3 + 10);
	}
}
