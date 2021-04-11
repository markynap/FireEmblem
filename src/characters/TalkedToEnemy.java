package characters;

import gameMain.Game;
import items.BronzeAxe;

public class TalkedToEnemy extends EnemyPlayer{
	
	public TalkedToEnemy(int xPos, int yPos, Game game, int whichChapter) {
		super("TalkedToEnemy", "TalkedToEnemy", game.getBaseStatsForEnemy("Brigand"), game.getBaseGrowthsForEnemy("Brigand"), xPos, yPos, game, new BronzeAxe(), whichChapter, false);
		image = Game.IM.getImage("/characterPics/bandit.png");
		this.talkedToUnit = true;
		
		if (whichChapter == 5) {
			image = Game.IM.getImage("/characterPics/Merric.png");
			this.name = "Merric";
			this.setAllyBaseStats();
			this.sightRange = 12;
		} else if (whichChapter == 12) {
			image = Game.IM.getImage("/characterPics/Shinnon.png");
			this.name = "Shinnon";
			this.setAllyBaseStats();
			this.sightRange = 6;
		} else if (whichChapter == 11) {
			image = Game.IM.getImage("/characterPics/Dorcas.png");
			this.name = "Dorcas";
			this.setAllyBaseStats();
			this.sightRange = 9;
		}
		this.currentHP = HP;
	}
}
