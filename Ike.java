package characters;

import gameMain.Game;
import items.*;

public class Ike extends AllyPlayer{
//	public static int[] stats = {22, 5, 4, 4, 3, 3, 1, 5, 7, 1};
	
//	public static int[] growths = {85, 60, 55, 45, 50, 45, 30};
	
	public Ike(int xPos, int yPos, Game game) {
//		super("Ike", "Lord", stats, growths, game, xPos, yPos, new BronzeSword());
		super("Ike", "Lord", game, xPos, yPos);
		image = Game.IM.getImage("/Ike.png");
	}
	
}
