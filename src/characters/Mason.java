package characters;

import gameMain.Game;

public class Mason extends AllyPlayer{

	public Mason(int xPos, int yPos, Game game, int whichChapter) {
		super("Mason", "TimeTraveler", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Mason.png");
		this.isPromoted = true;
		if (whichChapter == 16) {
			this.setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		this.race = "American";
	}

	
}
