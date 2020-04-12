package characters;

import gameMain.Game;
import items.*;

public class Hector extends AllyPlayer{
	
	//public static int[] stats = {26, 7, 4, 5, 3, 4, 2, 5, 12, 4};
	
	//public static int[] growths = {50, 50, 50, 50, 50, 50, 50};
	
	public Hector(int xPos, int yPos, Game game) {
		super("Hector", "AxeLord", game, xPos, yPos);
		image = Game.IM.getImage("/hector.png");
	}

}
