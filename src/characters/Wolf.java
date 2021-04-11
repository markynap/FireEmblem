package characters;

import gameMain.Game;

public class Wolf extends AllyPlayer{

	public Wolf(int xPos, int yPos, Game game, int whichChapter) {
		super("Wolf", "Archer", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Wolf.png");
		if (whichChapter == 3) {
			this.wallet.clear();
			this.setAllyBaseStats();
		}
		
		if (this.Class.equalsIgnoreCase("Marksman")) {
			this.bowExtention = 1;
		}
		this.race = "Daharan";
	}
}
