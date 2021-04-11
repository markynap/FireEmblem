package characters;

import gameMain.Game;

public class Volke extends AllyPlayer{

	public Volke(int xPos, int yPos, Game game, int whichChapter) {
		super("Volke", "Thief", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Volke.png");
		if (whichChapter == 8) {
			this.wallet.clear();
			this.setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		this.race = "Daharan";
		
	}
}
