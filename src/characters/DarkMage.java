package characters;

import gameMain.Game;
import items.FluxTome;
import items.Nosferatu;

public class DarkMage extends EnemyPlayer{

	public DarkMage(int xPos, int yPos, Game game, int chaptNum) {
		super("DarkMage", "Dark Mage", game.getBaseStatsForEnemy("Dark Mage"), game.getBaseGrowthsForEnemy("Dark Mage"), xPos, yPos, game, new FluxTome(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/darkmage.png");
		this.sightRange = 12;
		this.weaponMasteriesGrade[3] = 'C';
		this.isMagicUser = true;
		if (r.nextInt(3) == 2) {
			this.weaponMasteriesGrade[3] = 'B';
			if (r.nextInt(5) == 3) {
				this.addFalseLevels(1);
				this.wallet.clear();
				this.wallet.addItem(new Nosferatu());
			}
		}
	}
	
}
