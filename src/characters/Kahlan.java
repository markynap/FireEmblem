package characters;

import gameMain.Game;

public class Kahlan extends AllyPlayer{

	public Kahlan(int xPos, int yPos, Game game, int whichChapter) {
		super("Kahlan", "Confessor", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Kahlan.png");
		if (whichChapter == 8) {
			this.setAllyBaseStats();
			this.spawnsOnChaptStart = true;
			
		}
		
		if (this.Class.equalsIgnoreCase("SuperConfessor")) {
			this.confessionRange = 5;
		} else {
			this.confessionRange = 0;
		}
		this.race = "Iedendrilian";
	}

}
