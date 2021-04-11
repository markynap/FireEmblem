package characters;

import gameMain.Game;
import items.*;

public class Nino extends AllyPlayer{

	public Nino(int xPos, int yPos, Game game, int whichChapter) {
		super("Nino", "Mage", game, xPos, yPos);
		this.image = Game.IM.getImage("/characterPics/Nino.png");
		this.isMagicUser = true;
		if (whichChapter == 2) {
			this.wallet.clear();
			this.setAllyBaseStats();
			this.wallet.addItem(new IceTome());
			this.wallet.addItem(new Vulnery());
			
		}
		if (Class.equalsIgnoreCase("Sage")) {
			this.isHealer = true;
			this.duoWeaponHeal = true;
		}
		this.race = "Daharan";
	}

}
