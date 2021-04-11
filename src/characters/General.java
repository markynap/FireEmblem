package characters;

import gameMain.Game;
import gameMain.Game.DIFFICULTY;
import items.SteelAxe;
import items.SteelLance;

public class General extends EnemyPlayer{

	public General(int xPos, int yPos, Game game, int chaptNum) {
		super("General", "General", game.getBaseStatsForEnemy("General"), game.getBaseGrowthsForEnemy("General"), xPos, yPos, game, new SteelLance(), chaptNum, true);
		image = Game.IM.getImage("/characterPics/general.png");
		this.sightRange = 10;
		this.weaponMasteriesGrade[0] = 'B';
		this.weaponMasteriesGrade[1] = 'A';
		this.weaponMasteriesGrade[2] = 'A';
		if (r.nextInt(5) == 3) {
			this.addFalseLevels(2);
			this.wallet.clear();
			this.wallet.addItem(equiptItem = new SteelAxe());
			if (game.gameDifficulty == DIFFICULTY.Crushing) {
				this.weaponMasteriesGrade[2] = 'S';
			}
		}
	}

}
