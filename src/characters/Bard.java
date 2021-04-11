package characters;

import gameMain.Game;

public class Bard extends AllyPlayer{

	public Bard(int xPos, int yPos, Game game, int whichChapter) {
		super("Bard", "Dancer", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Bard.png");
		if (whichChapter == 2) this.setAllyBaseStats();
		this.isDancer = true;
		this.race = "Daharan";
	}
}
