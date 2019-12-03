package characters;

import java.awt.Color;

import gameMain.Game;
import items.*;

public class AllyPlayer extends Player {
	
	/**This will dictate if the game sees this Ally as a member or not */
	public boolean outOfGame = false;
	
	public AllyPlayer(String name, String Class, int[] stats, int[] growths, Game game, int xPos, int yPos, CombatItem equiptItem) {
		super(name, Class, "Ally", stats, growths, game, xPos, yPos, equiptItem);
		teamColor = Color.BLUE;
		repOk();
	}
	
}
