package characters;

import gameMain.Game;
import items.IronBow;
import items.UtilityItems.Elixir;

public class Shinnon extends AllyPlayer{

	public Shinnon(int xPos, int yPos, Game game, int whichChapter) {
		super("Shinnon", "Sniper", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Shinnon.png");
		this.isPromoted = true;
		this.recruitableUnit = true;
		if (whichChapter == 12) {
			this.setAllyBaseStats();
			this.spawnsOnChaptStart = true;
			this.wallet.addItem(new IronBow());
			this.wallet.addItem(new Elixir());
		}
		this.race = "Daharan";
	}

}
