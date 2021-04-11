package characters;

import gameMain.Game;

public class Heath extends AllyPlayer {

	public Heath(int xPos, int yPos, Game game, int whichChapter) {
		super("Heath", "Wyvern", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Heath.png");
		this.isFlier = true;
		if (whichChapter == 9) {
			this.wallet.clear();
			this.setAllyBaseStats();
			
		}
		this.race = "Daharan";

	}
}
