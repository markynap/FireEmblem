package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;

import chapterDesign.ChapterDesignMenu;
import gameMain.Game.STATE;

public class ChapterChooser extends ChapterDesignMenu{

	public Game game;
	public int maxBoxes, screenNum;
	
	public ChapterChooser(Game game) {
		super(game);
		this.game = game;
		this.maxBoxes = 8;
		this.screenNum = 0;
		this.title = "Chapter Chooser";
	}
	@Override
	public void chooseChapter() {
		ChapterOrganizer oldOrg = game.chapterOrganizer;
		game.setChapterOrganizer(new ChapterOrganizer(game, oldOrg.allys, oldOrg.enemys, ((6*screenNum) + boxIndex+1)));
		game.setGameState(STATE.Game);
	}
	
	
}
