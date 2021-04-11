package characters;

import gameMain.Game;
import items.BronzeSword;

public class Cavalier extends EnemyPlayer{

	
	public Cavalier(int xPos, int yPos, Game game, int chaptNum) {
		super("Cavalier", "Cavalier", game.getBaseStatsForEnemy("Cavalier"), game.getBaseGrowthsForEnemy("Cavalier"),xPos, yPos, game, new BronzeSword(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/enemycavalier.png");
		this.sightRange = 20;
		int RNG = this.r.nextInt(10);
		if (RNG < 5) {
			this.equipStartingItem('L');
		} else {
			this.equipStartingItem('S');
		}
		
		if (chaptNum > 15) {
			weaponMasteriesGrade[0] = 'B';
			weaponMasteriesGrade[1] = 'B';
		}
	}

}
