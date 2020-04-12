package chapterDesign;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import gameMain.ChapterOrganizer;
import gameMain.Game;

public class ChapterDesigner {

	public Game game;	
	/** New Chapter organizer to assign the game */
	public ChapterOrganizer organizer;
	/** Chapter Choosing Menu */
	public ChapterDesignMenu menu;
	/** Save or don't save */
	public boolean inSaveMode;
	/** Which box (chapter) are we currently hovering over */
	public int boxIndex;
	/** The editor which will edit the chapter we choose */
	public LevelEditor editor;
	/** Map of our keyboard click to value pairings while in design mode */
	public Map<String, String> keyCommandMap = new HashMap<>();
	/** File that holds our key commands : res//designInfo//designKeyCommands */
	private File keyCommandsFile;
	
	/**
	 * Creates a new ChapterDesigner that will save files into a folder called /chapters
	 */
	public ChapterDesigner(Game game) {
		this.game = game;
		game.removeKeyListener(game.getKeyListeners()[0]);
		game.addKeyListener(new ChapterDesignKeyInput(game, this));
		menu = new ChapterDesignMenu(game);
		setUpKeyCommandMap();
	}
	
	public void render(Graphics g) {
		if (organizer != null) organizer.render(g);
		if (inSaveMode) renderSaveBox(g);
		renderKeyCommands(g);
	}
	
	public void renderKeyCommands(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 28));
		int inc = 0;
		for (String s : keyCommandMap.keySet()) {
			g.drawString(s +"  " + keyCommandMap.get(s), 20, 100 + (40*inc));
			inc++;
		}
	}
	
	public void tick() {
		if (organizer != null) organizer.tick();
	}
	/** Sets up the Mapping of our key commands to design inputs */
	public void setUpKeyCommandMap() {
		keyCommandsFile = new File("res//designInfo//designKeyCommands");
		try {
			Scanner fileReader = new Scanner(keyCommandsFile);
			while (fileReader.hasNextLine()) {
				String[] line = fileReader.nextLine().split(" ");
				keyCommandMap.put(line[0], line[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/** Flips our save mode, either save or dont save */
	public void flipSaveMode() {
		if (inSaveMode) {
			inSaveMode = false;
		} else {
			inSaveMode = true;
		}
	}
	/** Chooses a chapter for us to begin editing */
	public void chooseChapter(int chaptNum) {
		System.out.println("Chapter number: " + chaptNum);
		String filename = "res\\chapters\\chapter" + chaptNum;
		editor = new LevelEditor(game, new File(filename));	
	}
	
	public void renderSaveBox(Graphics g) {

		int thickness = 4;
		int[] boxPos = {Game.WIDTH/2 - 325, 120, 650, 475};
		g.setColor(Color.black);
		for (int i = 1; i <= thickness; i++) {
		g.drawRect(boxPos[0] - thickness + i, boxPos[1] - thickness + i, boxPos[2], boxPos[3]);
		}
		g.setColor(Color.white);
		g.fillRect(boxPos[0] + 1, boxPos[1] + 1, boxPos[2] - thickness, boxPos[3] - thickness);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 40));
		g.drawString("Do you wish to save your progress?", boxPos[0] + 25, boxPos[1] + boxPos[3]/2 - 150);
		int yesX = boxPos[0] + 140;
		int yesY = boxPos[1] + boxPos[3]/2 - 10;
		int noX = boxPos[0] + boxPos[2] - 175;
		int thick = 2;
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		g.drawString("Yes", yesX, yesY);
		g.drawString("No", noX, yesY);
		
		g.setColor(Color.black);
		for (int i = 0; i < 2; i++) {
			g.drawRect(yesX + 10 + i, yesY + 10 + i, 25, 25);
			g.drawRect(noX + 10 + i, yesY + 10 + i, 25, 25);
		}
		g.setColor(Color.blue);
		if (boxIndex == 0) {
			g.fillRect(yesX + 10 + thick, yesY + 10 + thick, 25 - thick, 25 - thick);
		} else {
			g.fillRect(noX + 10 + thick, yesY + 10 + thick, 25 - thick, 25 - thick);
		}
	}
	
	public void flipYesNoBox() {
		if (boxIndex == 0) boxIndex = 1;
		else boxIndex = 0;
	}
}
