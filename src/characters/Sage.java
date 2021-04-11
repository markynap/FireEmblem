package characters;

import gameMain.Game;
import items.LightningTome;
import items.Vulnery;

public class Sage extends EnemyPlayer{

	public Sage(int xPos, int yPos, Game game, int chaptNum) {
		super("Sage", "Sage", game.getBaseStatsForEnemy("Sage"), game.getBaseGrowthsForEnemy("Sage"), xPos, yPos, game, new LightningTome(), chaptNum, true);
		image = Game.IM.getImage("/characterPics/Sage.png");
		this.wallet.addItem(new Vulnery());
		this.sightRange = 17;
		this.weaponMasteriesGrade[0] = 'A';
		this.isMagicUser = true;
		this.duoWeaponHeal = true;
	}

}
