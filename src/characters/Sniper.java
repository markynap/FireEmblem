package characters;

import gameMain.Game;
import items.IronBow;
import items.UtilityItems.Elixir;

public class Sniper extends EnemyPlayer{

	public Sniper(int xPos, int yPos, Game game, int chaptNum) {
		super("Sniper", "Sniper", game.getBaseStatsForEnemy("Sniper"), game.getBaseGrowthsForEnemy("Sniper"), xPos, yPos, game, new IronBow(), chaptNum, true);
		image = Game.IM.getImage("/characterPics/Sniper.png");
		this.sightRange = 20;
		this.equipStartingItem('B');
		if (chaptNum > 15) weaponMasteriesGrade[3] = 'A';
		if (r.nextInt(10) == 1) wallet.addItem(new Elixir());
	}

}
