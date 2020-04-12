package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Menu {

	public Game game;
	public int optionIndex;
	public boolean[] options;
	public int boxX, boxW, boxH, boxDistApart, prettyRectDistApart;
	public int Adelay = 0;
	public String[] titles = {"New Game", "Load Game", "Choose Chapter", "Chapter Design"};
	
	public Menu(Game game) {
		this.game = game;
		options = new boolean[4];
		boxX = 175;
		boxW = 750;
		boxH = 180;
		boxDistApart = 35;
		prettyRectDistApart = 3;
	}
	
	public void render(Graphics g) {
		int thickness = 4;
		int startHeight = 60;
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		for (int i = 0; i < options.length; i++) {
			drawGapStringRectangle(g, Color.RED, boxX, startHeight + ((boxH + boxDistApart) * i), boxW, boxH, thickness, titles[i]);
		}
		g.setColor(Color.BLUE);
		for (int i = 0; i < (thickness + prettyRectDistApart + 1); i++) {
			g.drawRect(boxX + thickness + i, startHeight + ((boxH + boxDistApart) * optionIndex) + thickness + i, boxW - thickness - (2*i), boxH - thickness - (2*i));
		}
		}
	
	private void drawPrettyRect(Graphics g, Color color, int startX, int startY, int width, int height, int thickness) {
		
		g.setColor(color);
		for (int i = 0; i < thickness; i++) {
			g.drawRect(startX + i, startY + i, width, height);
			g.drawRect(startX + (prettyRectDistApart * thickness) + i, startY +(prettyRectDistApart * thickness) + i, width - ((2*prettyRectDistApart) * thickness), height - ((2*prettyRectDistApart) * thickness));
		}
	
	}
	
	public void drawGapStringRectangle(Graphics g, Color color, int startX, int startY, int width, int height, int thickness, String string) {
		drawPrettyRect(g, color, startX, startY, width, height, thickness);
		int font = width/20;
		g.setFont(new Font("Times New Roman", Font.BOLD, font));
		if (string != null) g.drawString(string, startX + (width/2) - (font*2) - (3*string.length()), startY + (height/2) + 10);
		
	}
	
	public void incSelectedOptions() {
		options[optionIndex] = false;
		optionIndex++;
		if (optionIndex >= options.length)
			optionIndex = 0;
		options[optionIndex] = true;
	}

	public void decSelectedOptions() {
		options[optionIndex] = false;
		optionIndex--;
		if (optionIndex < 0)
			optionIndex = options.length - 1;
		options[optionIndex] = true;
	}
}
