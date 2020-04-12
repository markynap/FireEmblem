package characters;

import java.awt.Color;

import gameMain.Game;
import items.*;

public class EnemyPlayer extends Player {
	
	public final static int[] growths = {20, 20, 20, 20, 20, 20, 20};
	
	public EnemyPlayer(String name, String Class, int[] stats, int xPos, int yPos, Game game, CombatItem equiptItem) {
		super(name, Class, "Enemy", stats, growths, game, xPos, yPos, equiptItem);
		teamColor = Color.RED;
		repOk();
	}

}
