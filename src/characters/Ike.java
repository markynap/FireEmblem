package characters;

import gameMain.Game;
import items.*;

public class Ike extends AllyPlayer{
	
	public Ike(int xPos, int yPos, Game game, int whichChapter) {
		super("Ike", "Lord", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/Ike.png");
		if (whichChapter == 1) {
			// spawning chapter
			this.setAllyBaseStats();
			
			// add his key
			this.wallet.addItem(new Key(game));
		} else if (whichChapter == 6) {
			Durandal durandal = new Durandal();
			if (!wallet.contains(durandal)) {
				if (wallet.weapons.size() < Wallet.MAX_SIZE) {
					this.wallet.addItem(durandal);
				} else {
					wallet.weapons.remove(3);
					wallet.weapons.add(durandal);
				}
			}
		}
		
		this.race = "Daharan";
		
	}
	
}
