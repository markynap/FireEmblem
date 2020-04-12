package characters;

import java.awt.Color;

import gameMain.Game;
import items.*;

public class AllyPlayer extends Player {
	
	/**This will dictate if the game sees this Ally as a member or not */
	public boolean outOfGame = false;
	/**The unique ID to each AllyPlayer*/
	public int playerID;
	
	public AllyPlayer(String name, String Class, Game game, int xPos, int yPos) {
		super(name, Class, xPos, yPos, game);
		teamColor = Color.BLUE;
		playerID = getID();
		repOk();
	}
	
}
