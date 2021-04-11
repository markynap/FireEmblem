package characters;

import gameMain.Game;
import items.BronzeAxe;


public class SummonedUnit extends AllyPlayer{
	
	public SummonedUnit(int xPos, int yPos, Game game, int whichChapter) {
		super("SummonedUnit", "Summoned", game, xPos, yPos);
		image = Game.IM.getImage("/characterPics/summonedAlly.png");
		this.wallet.addItem(equiptItem = new BronzeAxe());
		stats[0] = 1;
		
		this.currentHP = 1;
		stats[1] = 2 + whichChapter;
		stats[2] = 1;
		stats[7] = 5;
		stats[8] = 6;
		stats[9] = 1;
		this.skill = new Skill("Summoned");
		this.resetStatsFromStatsArray();
		game.chapterOrganizer.summonedUnit = this;
		this.race = "Daharan";

	}

}
