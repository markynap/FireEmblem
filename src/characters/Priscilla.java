package characters;

import gameMain.Game;

public class Priscilla extends AllyPlayer{

	public Priscilla(int xPos, int yPos, Game game, int whichChapter) {
		super("Priscilla", "Troubadour", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Priscilla.png");
		if (whichChapter == 10) this.setAllyBaseStats();
		this.isHealer = true;
		this.recruitableUnit = true;
		if (this.Class.equalsIgnoreCase("Valkyrie")) {
			duoWeaponHeal = true;
			isMagicUser = true;
			isHealer = true;
		} else if (Class.equalsIgnoreCase("Paladin")) {
			this.isHealer = false;
			this.duoWeaponHeal = false;
			this.isMagicUser = false;
		}
		this.skill = new Skill("Momento");
		this.race = "Daharan";
	}

}
