package characters;

import gameMain.Game;
import items.*;

public class Paladin extends EnemyPlayer{
	
	public Paladin(int xPos, int yPos, Game game, int whichChapter) {
		super("Paladin", "Paladin", game.getBaseStatsForEnemy("Paladin"), game.getBaseGrowthsForEnemy("Paladin"), xPos, yPos, game, new SteelLance(), whichChapter, true);
		image = Game.IM.getImage("/characterPics/paladin.png");
		this.wallet.addItem(new IronSword());
		this.sightRange = 50;
		int rng = r.nextInt(3);
		if (rng == 0) {
			this.equipStartingItem('S');
		} else if (rng == 1) {
			this.equipStartingItem('L');
		} else {
			this.equipStartingItem('A');
		}
	}
}
