package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game;
import tiles.Tile;

public class PopUpMenu {
	public final static String[] commands = {"Move", "Items", "Attack", "Trade", "Wait", "End", "Heal"};
	public Game game;
	public boolean[] selectedOptions;
	public int selectedIndex;
	public Tile tile;
	public Player player;

	public PopUpMenu(Game game, Tile tile) {
		this.game = game;
		this.tile = tile;
		this.selectedOptions = new boolean[6];
		selectedOptions[0] = true;
		selectedIndex = 0;
		for (int i = 1; i < 6; i++)
			selectedOptions[i] = false;
		player = tile.carrier;
	}

	public void render(Graphics g) {
		int xPos = tile.xPos * Game.scale;
		int yPos = tile.yPos * Game.scale;
		int menuwidth = 80;
		int menuheight = 130;
		int menuLength = 6;
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));

		g.setColor(Color.black);
		g.drawRect(xPos + Game.scale, yPos + menuheight/5, menuwidth, menuheight);

		for (int i = 0; i < menuLength; i++) {
			if (selectedOptions[i]) g.setColor(Color.blue);
			else g.setColor(Color.white);
			g.fillRect(xPos + Game.scale, yPos + ((i)*menuheight)/5, menuwidth, menuheight/5);
			g.setColor(Color.black);
			g.drawString(commands[i], xPos + Game.scale + 10, yPos + ((i+1)*menuheight)/5 - 4);
		}
	}

	public void incSelectedOptions() {
		selectedOptions[selectedIndex] = false;
		selectedIndex++;
		if (selectedIndex >= 6)
			selectedIndex = 0;
		selectedOptions[selectedIndex] = true;
	}

	public void decSelectedOptions() {
		selectedOptions[selectedIndex] = false;
		selectedIndex--;
		if (selectedIndex < 0)
			selectedIndex = 5;
		selectedOptions[selectedIndex] = true;
	}
}
