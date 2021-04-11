package characters;

import gameMain.Game;
import items.*;

public class Brigand extends EnemyPlayer{
	
	public Brigand(int xPos, int yPos, Game game, int whichChapter) {
		super("Bandit", "Brigand", game.getBaseStatsForEnemy("Brigand"), game.getBaseGrowthsForEnemy("Brigand"), xPos, yPos, game, new BronzeAxe(), whichChapter, false);
		image = Game.IM.getImage("/characterPics/bandit.png");
		this.sightRange = 12;
		this.equipStartingItem('A');
		if (whichChapter > 15) weaponMasteriesGrade[2] = 'B';
	}

}
