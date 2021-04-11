package characters;

import gameMain.Game;
import items.BlizzardTome;
import items.EarthQuakeTome;
import items.FireTome;
import items.IceTome;
import items.LightningTome;
import items.TornadoTome;

public class Mage extends EnemyPlayer{
	
	public Mage(int xPos, int yPos, Game game, int chaptNum) {
		super("Mage", "Mage", game.getBaseStatsForEnemy("Mage"), game.getBaseGrowthsForEnemy("Mage"), xPos, yPos, game, new FireTome(), chaptNum, false);
		image = Game.IM.getImage("/characterPics/enemymage.png");
		this.sightRange = 12;
		int rng = r.nextInt(5);
		if (rng == 0) {
			this.wallet.clear();
			this.weaponMasteriesGrade[0] = 'F';
			if (chaptNum > 8) {
				wallet.addItem(new BlizzardTome());
				this.weaponMasteriesGrade[1] = 'B';
			} else {
				this.wallet.addItem(new IceTome());
				this.weaponMasteriesGrade[1] = 'C';
			}
		} else if (rng == 1 || rng == 2) {
			this.wallet.clear();
			this.weaponMasteriesGrade[0] = 'F';
			if (chaptNum > 8) {
				wallet.addItem(new TornadoTome());
				this.weaponMasteriesGrade[2] = 'B';
			} else {
				this.wallet.addItem(new EarthQuakeTome());
				this.weaponMasteriesGrade[2] = 'C';
			}
			
		} else {
			if (chaptNum > 9) {
				this.wallet.clear();
				wallet.addItem(new LightningTome());
				this.weaponMasteriesGrade[2] = 'B';
			}
		}
		this.isMagicUser = true;
	}
	
}
