package characters;

import gameMain.Game;
import items.IronAxe;
import items.UtilityItems.Elixir;

public class Berserker extends EnemyPlayer{
	
	public Berserker(int xPos, int yPos, Game game, int whichChapter) {
		super("Berserker", "Berserker", game.getBaseStatsForEnemy("Berserker"), game.getBaseGrowthsForEnemy("Berserker"), xPos, yPos, game, new IronAxe(), whichChapter, true);
		image = Game.IM.getImage("/characterPics/berserker.png");
		this.sightRange = 11;
		this.isPromoted = true;
		this.equipStartingItem('A');
		if (r.nextInt(4) == 1) {
			this.wallet.addItem(new Elixir());
		}
		if (whichChapter > 15) weaponMasteriesGrade[2] = 'A';
	}

}
