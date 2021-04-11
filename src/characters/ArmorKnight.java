package characters;

import gameMain.Game;
import items.BronzeAxe;

public class ArmorKnight extends EnemyPlayer{
	
	public ArmorKnight(int xPos, int yPos, Game game, int chaptNum) {
		super("ArmorKnight", "Knight", game.getBaseStatsForEnemy("Armor Knight"), game.getBaseGrowthsForEnemy("Armor Knight"), xPos, yPos, game, new BronzeAxe(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/armorknight.png");
		this.sightRange = 8;
		
		this.equipStartingItem('A');
		if (chaptNum > 15) weaponMasteriesGrade[2] = 'B';
		
	}

}
