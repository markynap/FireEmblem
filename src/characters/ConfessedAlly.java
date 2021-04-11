package characters;

import java.awt.Image;

import gameMain.Game;
import items.Wallet;

public class ConfessedAlly extends AllyPlayer{
	
	public ConfessedAlly(int[] stats, int xPos, int yPos, Game game, Wallet wallet, Image image) {
		super(stats, xPos, yPos, game, wallet);
		this.image = image;
		this.isConfessed = true;
		this.teamID = "Ally";
		this.game.chapterOrganizer.allys.add(this);
		if (!wallet.weapons.isEmpty()) {
			if (wallet.weapons.get(0).isMagicItem()) this.isMagicUser = true;
		}
		this.weaponMasteriesGrade[0] = 'C';
		this.weaponMasteriesGrade[1] = 'C';
		this.weaponMasteriesGrade[2] = 'C';
		this.weaponMasteriesGrade[3] = 'C';
	}

}
