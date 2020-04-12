package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import gameMain.Game.STATE;

public class GameLoader {

	private Game game;
	/** Coordinates for boxes */
	private int startX = 300, startY = 100, boxW = 500, boxH = 200, spacing = 260, thickness = 5;
	/** Index of Game Save State Memory Location */
	private int selectedIndex;
	/** Which chapter each of the 3 save states are on */
	private int[] chaptProgress;
	/** Reading file containing Level data */
	private Scanner reader;
	/** File containing level data res//chapters//chapterStatus */
	private File inputFile;
	/** Writes to file containing Level Data */
	private PrintWriter writer;
	/** Whether or not we are loading a game or saving it */
	public boolean isSaving = false;
	
	public GameLoader(Game game) {
		this.game = game;
		setReaderToChapterStatus();
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		String[] quotes = new String[2];
		if (isSaving) {
			quotes[0] = "Save over Empty Game";
			quotes[1] = "Save over Existing Game chapt: ";
		} else {
			quotes[0] = "Start New Game";
			quotes[1] = "Load Game at Chapter ";
		}
		
		for (int i = 0; i < 3; i++) {
			
			if (chaptProgress[i] == 1) 	drawGapStringRectangle(g, Color.blue, startX, startY + i*spacing, boxW, boxH, thickness, quotes[0]);
			else drawGapStringRectangle(g, Color.blue, startX, startY + i*spacing, boxW, boxH, thickness, quotes[1] + chaptProgress[i]);
		}
		g.setColor(Color.red);
		for (int i = 0; i < thickness + 3; i++) {
			g.drawRect(startX + thickness + 1 + i, startY + selectedIndex*spacing + thickness + 1 + i, boxW - thickness - 2*i - 1, boxH - thickness - 1 - 2*i);
		}
		
	}
	
	public void loadGame() {
		game.setChapterOrganizer(new ChapterOrganizer(game, chaptProgress[selectedIndex], selectedIndex+1));
		game.setGameState(STATE.Game);
	}
	
	public void saveGame() {
		setReaderToChapterStatus();
		
		try {
			writer = new PrintWriter(inputFile);
			for (int i = 0; i < 3; i++) {
				if (i == selectedIndex) writer.println(chaptProgress[i]+1);
				else writer.println(chaptProgress[i]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.close();
		game.chapterOrganizer.nextChapter(selectedIndex+1);
		game.setGameState(STATE.Game);
	}
	
	private void drawGapStringRectangle(Graphics g, Color color, int startX, int startY, int width, int height,
			int thickness, String string) {
		drawPrettyRect(g, color, startX, startY, width, height, thickness);
		int font = (width / 10) - (2*string.length()/3);
		g.setFont(new Font("Times New Roman", Font.BOLD, font));
		if (string != null)
			g.drawString(string, startX + (width / 2) - (5*font/2) - (4 * string.length()), startY + (height / 2) + 10);

	}
	private void drawPrettyRect(Graphics g, Color color, int startX, int startY, int width, int height, int thickness) {
		int prettyRectDistApart = 3;
		g.setColor(color);
		for (int i = 0; i < thickness; i++) {
			g.drawRect(startX + i, startY + i, width, height);
			g.drawRect(startX + (prettyRectDistApart * thickness) + i, startY + (prettyRectDistApart * thickness) + i,
					width - ((2 * prettyRectDistApart) * thickness), height - ((2 * prettyRectDistApart) * thickness));
		}

	}
	public void changeSelectedIndex(int amount) {
		selectedIndex += amount;
		if (selectedIndex > 2) selectedIndex = 0;
		if (selectedIndex < 0) selectedIndex = 2;
	}
	
	private void setReaderToChapterStatus() {
		chaptProgress = new int[3];
		inputFile = new File("res//chapters//chapterStatus");
		try {
			reader = new Scanner(inputFile);
			for (int i = 0; i < 3; i++) chaptProgress[i] = Integer.parseInt(reader.nextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		reader.close();
	}
}
