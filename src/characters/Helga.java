package characters;

import gameMain.Game;

public class Helga extends AllyPlayer{
	
	public Helga(int xPos, int yPos, Game game, int whichChapter) {
		super("Helga", "Sentry", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Helga.png");
		if (whichChapter == 13) {
			setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		this.race = "Polarian";

	}
}
