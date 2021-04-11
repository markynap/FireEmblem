package characters;

import gameMain.Game;
import items.BronzeLance;
public class Soldier extends EnemyPlayer{
	
	public Soldier(int xPos, int yPos, Game game, int whichChapter) {
		super("Soldier", "Soldier", game.getBaseStatsForEnemy("Soldier"), game.getBaseGrowthsForEnemy("Soldier"), xPos, yPos, game, new BronzeLance(), whichChapter, false);
		image = Game.IM.getImage("/characterPics/Soldier.png");
		this.equipStartingItem('L');
		if (whichChapter > 15) weaponMasteriesGrade[1] = 'B';
	}

}
