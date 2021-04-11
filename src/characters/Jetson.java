package characters;

import gameMain.Game;

public class Jetson extends AllyPlayer{

	public Jetson(int xPos, int yPos, Game game, int whichChapter) {
		super("Jetson", "Ranger", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Jetson.png");
		if (whichChapter == 13) {
			setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		this.race = "Polarian";

	}
}
