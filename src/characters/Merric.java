package characters;

import gameMain.Game;

public class Merric extends AllyPlayer{

	public Merric(int xPos, int yPos, Game game, int whichChapter) {
		super("Merric", "EarthMage", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Merric.png");
		this.isMagicUser = true;
		this.recruitableUnit = true;
		if (whichChapter == 5) {
			this.wallet.clear();
			this.setAllyBaseStats();
		}
	
		if (Class.equalsIgnoreCase("Sage")) {
			this.isHealer = true;
			this.duoWeaponHeal = true;
		}
		this.race = "Daharan";
	}

}
