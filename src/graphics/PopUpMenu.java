package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game;
import tiles.Throne;
import tiles.Tile;

public class PopUpMenu {
	public String[] commands = {"Sieze", "Attack", "Items", "Trade", "Wait", "End", "Heal"};
	public Game game;
	public boolean[] selectedOptions;
	public int selectedIndex;
	public Tile tile;
	public Player player;
	/** Which command we begin with */
	private int beginLoop;

	public PopUpMenu(Game game, Tile tile) {
		this.game = game;
		this.tile = tile;
		this.selectedOptions = new boolean[6];
		for (int i = 1; i < 6; i++)
			selectedOptions[i] = false;
		player = tile.carrier;
		if (tile.getClass().equals(Throne.class)) {
			beginLoop = 0;
			selectedIndex = 0;
			selectedOptions[0] = true;
		} else { //not on throne
			beginLoop = 1;
			selectedIndex = 1;
			selectedOptions[1] = true;
		}
		if (player.Class.equalsIgnoreCase("Healer")) commands[1] = "Heal";
		else if (player.Class.equalsIgnoreCase("Dancer")) commands[1] = "Dance";
	}

	public void render(Graphics g) {
		int xPos = tile.xPos * Game.scale;
		int yPos = tile.yPos * Game.scale;
		int menuwidth = 80;
		int menuheight = 100;
		int menuLength = 6;
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));

		g.setColor(Color.black);
		g.drawRect(xPos + Game.scale, yPos + menuheight/5, menuwidth, menuheight);

		for (int i = beginLoop; i < menuLength; i++) {
			if (selectedOptions[i]) g.setColor(Color.blue);
			else g.setColor(Color.white);
			g.fillRect(xPos + Game.scale, yPos + ((i-beginLoop)*menuheight)/(selectedOptions.length-1 - beginLoop), menuwidth, menuheight/(selectedOptions.length-1 - beginLoop));
			g.setColor(Color.black);
			g.drawString(commands[i], xPos + Game.scale + 10, yPos + ((i+1 - beginLoop)*menuheight)/(selectedOptions.length-1 - beginLoop) - 4);
		}
	}

	public void incSelectedOptions() {
		selectedOptions[selectedIndex] = false;
		selectedIndex++;
		if (selectedIndex >= selectedOptions.length)
			selectedIndex = beginLoop;
		selectedOptions[selectedIndex] = true;
	}

	public void decSelectedOptions() {
		selectedOptions[selectedIndex] = false;
		selectedIndex--;
		if (selectedIndex < beginLoop)
			selectedIndex = selectedOptions.length - 1;
		selectedOptions[selectedIndex] = true;
	}
}
