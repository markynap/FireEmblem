package characters;

import gameMain.Game;

public class Guy extends AllyPlayer{
	
	public Guy(int xPos, int yPos, Game game, int whichChapter) {
		super("Guy", "Duelist", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Guy.png");
		if (whichChapter == 6) {
			this.wallet.clear();
			this.setAllyBaseStats();
			this.spawnsOnChaptStart = true;
			
		}
		this.race = "Sacaean";

	}


}
