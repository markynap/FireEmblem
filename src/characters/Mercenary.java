package characters;

import gameMain.Game;
import items.BronzeSword;

public class Mercenary extends EnemyPlayer{
	
	public Mercenary(int xPos, int yPos, Game game, int chaptNum) {
		super("Mercenary", "Mercenary", game.getBaseStatsForEnemy("Mercenary"), game.getBaseGrowthsForEnemy("Mercenary"), xPos, yPos, game, new BronzeSword(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/mercenary.png");
		this.sightRange = 16;

		this.equipStartingItem('S');
		if (chaptNum > 15) weaponMasteriesGrade[0] = 'B';
		
	}

}
