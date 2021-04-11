package characters;

import gameMain.Game;

public class Florina extends AllyPlayer{

	public Florina(int xPos, int yPos, Game game, int whichChapter) {
		super("Florina", "Pegasus", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Florina.png");
		this.isFlier = true;
		if (whichChapter == 4) {
			this.wallet.clear();
			this.setAllyBaseStats();
		}
		this.race = "Sacaean";
	
	}

}
