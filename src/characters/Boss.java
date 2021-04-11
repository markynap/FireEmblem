package characters;

import gameMain.Game;
import items.*;

public class Boss extends EnemyPlayer{

	public Boss(int xPos, int yPos, Game game, int chaptNum) {
		super(game.getBossName(chaptNum),game.getBossClass(chaptNum), new int[10], game.getBaseGrowthsForEnemy("Boss"), xPos, yPos, game, game.getBossItem(chaptNum), chaptNum, false);
		String[] s = game.getBossStats(chaptNum);
		int[] stats = new int[10];
		for (int i = 0; i < s.length; i++) {
			if (i == 0) this.image = Game.IM.getImage(s[i]);
			if (i > 0) stats[i - 1] = Integer.valueOf(s[i]);
		}
		this.setEnemyStats(stats, growths);
		this.currentHP = HP;
		this.sightRange = 3;
		this.weaponMasteriesGrade[0] = 'C';
		this.weaponMasteriesGrade[1] = 'C';
		this.weaponMasteriesGrade[2] = 'C';
		this.weaponMasteriesGrade[3] = 'C';
		setDifferencesForUniqueBosses();
		isBoss = true;
		if (this.level >= 20) this.isPromoted = true;
		this.skill = new Skill("Is Boss");
	}
	
	private void setDifferencesForUniqueBosses() {
		if (name.equalsIgnoreCase("Alfred")) {
			this.weaponMasteriesGrade[1] = 'A';
			this.wallet.addItem(new Staff());
		} else if (name.equalsIgnoreCase("Marth")) {
			this.weaponMasteriesGrade[0] = 'A';
		} else if (name.equalsIgnoreCase("Ryoma")) {
			this.weaponMasteriesGrade[0] = 'A';
			this.wallet.addItem(new Vulnery());
		} else if (name.equalsIgnoreCase("Gonzales")) {
			weaponMasteriesGrade[2] = 'A';
		}
	}

}
