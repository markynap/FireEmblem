package characters;

import gameMain.Game;
import items.IronLance;
import items.UtilityItems.DracoShield;

public class Wyvern extends EnemyPlayer{

	public Wyvern( int xPos, int yPos, Game game, int chaptNum) {
		super("Wyvern", "Wyvern", game.getBaseStatsForEnemy("Wyvern"), game.getBaseGrowthsForEnemy("Wyvern"), xPos, yPos, game, new IronLance(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/wyvern.png");
		this.isFlier = true;
		this.sightRange = 24;
		this.weaponMasteriesGrade[1] = 'C';
		
		
		this.equipStartingItem('L');
		if (chaptNum > 15) weaponMasteriesGrade[1] = 'B';
		
		if (r.nextInt(38) == 1) {
			this.wallet.addItem(new DracoShield());
		}
	}

}
