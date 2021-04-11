package characters;

import gameMain.Game;

public class Navarre extends AllyPlayer {
	
	public Navarre(int xPos, int yPos, Game game, int whichChapter) {
		super("Navarre", "SwordMaster", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Navarre.png");
		if (whichChapter == 13) {
			setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		this.race = "Polarian";
	}
}
