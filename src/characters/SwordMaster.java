package characters;

import gameMain.Game;
import items.IronSword;
import items.KillingEdge;

public class SwordMaster extends EnemyPlayer {

	public SwordMaster(int xPos, int yPos, Game game, int chaptNum) {
		super("SwordMaster", "SwordMaster", game.getBaseStatsForEnemy("SwordMaster"), game.getBaseGrowthsForEnemy("SwordMaster"), xPos, yPos, game, new IronSword(), chaptNum, true);
		image = Game.IM.getImage("/characterPics/SwordMaster.png");
		this.sightRange = 14;
		
		this.equipStartingItem('S');
		if (chaptNum > 15) weaponMasteriesGrade[0] = 'A';
		
		int rng = r.nextInt(7);
		if (rng == 1) {
			this.wallet.weapons.clear();
			this.wallet.addItem(new KillingEdge());
		}
	}
	
}
