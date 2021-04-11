package characters;

import gameMain.Game;
import items.HandAxe;
import items.IronLance;

public class Marcus extends AllyPlayer{

	public Marcus(int xPos, int yPos, Game game, int whichChapter) {
		super("Marcus", "Paladin", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Marcus.png");
		this.isPromoted = true;
		if (whichChapter == 5) {
			this.wallet.clear();
			this.setAllyBaseStats();
			this.wallet.addItem(new IronLance());
			this.wallet.addItem(new HandAxe());
			this.spawnsOnChaptStart = true;
		}
		this.race = "Ostian";
	}

}
