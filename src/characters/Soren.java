package characters;

import gameMain.Game;

public class Soren extends AllyPlayer {
	
	public Soren(int xPos, int yPos, Game game, int whichChapter) {
		super("Soren", "Elementalist", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Soren.png");
		this.isMagicUser = true;
		if (whichChapter == 13) {
			setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		this.race = "Polarian";

	}
}
