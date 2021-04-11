package gameMain;

import chapterDesign.ChapterDesignMenu;

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
	
}
