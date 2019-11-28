package characters;

import extras.IDAssigner;
import gameMain.Game;
import items.*;

public class Brigand extends EnemyPlayer{

	public static IDAssigner brigandID = new IDAssigner(1);
	public static int[] basicStats = {25, 5, 2, 2, 1, 1, 0, 5, 9, 2};
	
	public Brigand(String name, int[] stats, int xPos, int yPos, Game game, CombatItem equiptItem) {
		super(name, "Brigand", stats, xPos, yPos, game, equiptItem);
		image = Game.IM.getImage("/basicenemy.png");
	}
	
	public Brigand(int xPos, int yPos, Game game) {
		super("Bandit #" + brigandID.next(), "Brigand", basicStats, xPos, yPos, game, new BronzeAxe());
		image = Game.IM.getImage("/basicenemy.png");
	}

}
