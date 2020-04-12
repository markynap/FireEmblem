package characters;

import gameMain.Game;
import items.Staff;

public class Raymond extends AllyPlayer{

	//public static int[] stats = {22, 5, 4, 4, 3, 3, 1, 5, 7, 1};
	
//	public static int[] growths = {85, 60, 55, 45, 50, 45, 30};
	
	public Raymond(int xPos, int yPos, Game game) {
		//super("Raymond", "Healer", stats, growths, game, xPos, yPos, new Staff());
		super("Raymond", "Healer", game, xPos, yPos);
		image = Game.IM.getImage("/wang.png");
	}

}
