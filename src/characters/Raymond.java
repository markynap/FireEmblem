package characters;

import gameMain.Game;

public class Raymond extends AllyPlayer{

	public Raymond(int xPos, int yPos, Game game, int whichChapter) {
		super("Raymond", "Healer", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Raymond.png");
		if (whichChapter == 1) this.setAllyBaseStats();
		this.isHealer = true;
		
		if (this.Class.equalsIgnoreCase("Bishop")) {
			this.staffExtention = 2;
		} else if (Class.equalsIgnoreCase("Sage")) {
			this.isHealer = true;
			this.duoWeaponHeal = true;
			this.isMagicUser = true;
		}
		this.race = "Daharan";
	}

}
