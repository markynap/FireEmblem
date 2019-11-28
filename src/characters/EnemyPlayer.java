package characters;

import gameMain.Game;
import items.*;

public class EnemyPlayer extends Player {
	
	public final static int[] growths = {0, 0, 0, 0, 0, 0, 0};
	
	public EnemyPlayer(String name, String Class, int[] stats, int xPos, int yPos, Game game, CombatItem equiptItem) {
		super(name, Class, stats, growths, game, xPos, yPos, equiptItem);
		game.chapterOrganizer.enemys.add(this);
		teamID = "Enemy";
		repOk();
	}

}
