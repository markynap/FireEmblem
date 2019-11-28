package characters;

import gameMain.Game;
import items.*;

public class AllyPlayer extends Player {
	
	/**This will dictate if the game sees this Ally as a member or not */
	public boolean outOfGame = false;
	
	public AllyPlayer(String name, String Class, int[] stats, int[] growths, Game game, int xPos, int yPos, CombatItem equiptItem) {
		super(name, Class, stats, growths, game, xPos, yPos, equiptItem);
		
		game.chapterOrganizer.allys.add(this);
		teamID = "Ally";
		repOk();
	}
	
	public void repOk() {
		super.repOk();
		if (growths.length != 7) throw new IllegalArgumentException("Player must have 7 growth stats!");
	}
}
