package characters;

import gameMain.Game;

public class Dorcas extends AllyPlayer{

	public Dorcas(int xPos, int yPos, Game game, int whichChapter) {
		super("Dorcas", "Berserker", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Dorcas.png");
		this.recruitableUnit = true;
		if (whichChapter == 11) {
			this.wallet.clear();
			this.setAllyBaseStats();
		}
		this.skill = new Skill("Rage");
		this.race = "Daharan";
		
	}

	
}
