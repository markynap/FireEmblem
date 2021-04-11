package characters;

import gameMain.Game;
import items.*;

public class Kent extends AllyPlayer{

	public Kent(int xPos, int yPos, Game game, int whichChapter) {
		super("Kent", "Cavalier", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Kent.png");
		if (whichChapter == 1) {
			this.setAllyBaseStats();
			this.wallet.addItem(new BronzeLance());
		}
		this.race = "Ostian";
	}

}
