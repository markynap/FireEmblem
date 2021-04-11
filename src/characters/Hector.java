package characters;

import gameMain.Game;

public class Hector extends AllyPlayer{
	
	public Hector(int xPos, int yPos, Game game, int whichChapter) {
		super("Hector", "AxeLord", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Hector.png");
		if (whichChapter == 1) this.setAllyBaseStats();
		
		this.race = "Ostian";
	}

}
