package characters;

import gameMain.Game;

public class Evelynn extends AllyPlayer{

	public Evelynn(int xPos, int yPos, Game game, int whichChapter) {
		super("Evelynn", "Sorcerer", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Evelynn.png");
		this.isMagicUser = true;
		if (whichChapter == 8) {
			this.wallet.clear();
			this.setAllyBaseStats();
			this.spawnsOnChaptStart = true;
		}
		
		if (Class.equalsIgnoreCase("Druid")) {
			this.isHealer = true;
			this.duoWeaponHeal = true;
		}
		this.race = "Celestial";
	
	}
}
