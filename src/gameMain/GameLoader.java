package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import gameMain.Game.DIFFICULTY;
import gameMain.Game.STATE;

public class GameLoader {

	private Game game;
	/** Coordinates for boxes */
	private int startX = Game.WIDTH/11, startY = Game.HEIGHT/20, boxW = 4*Game.WIDTH/5-20, boxH = Game.HEIGHT/4, spacing = 5*Game.HEIGHT/17, thickness = 5;
	/** Index of Game Save State Memory Location */
	private int selectedIndex;
	/** Which chapter each of the 3 save states are on */
	private int[] chaptProgress;
	/** Easy 0, Normal 1, Hard 2, Crushing 3 */
	private int[] chaptDifficulties;
	/** List of all Gold for each game */
	private int[] gameGolds;
	/** All true if every save state is in Ike's Mode, false for Hectors */
	private boolean[] inIkesMode;
	/** Reading file containing Level data */
	private Scanner reader;
	/** File containing level data res//chapters//chapterStatus */
	private File inputFile;
	/** Writes to file containing Level Data */
	private PrintWriter writer;
	/** Whether or not we are loading a game or saving it */
	public boolean isSaving = false;
	
	/** Responsible for saving and spawning various Game states
	 *  based on data stored in text files 
	 */
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
			quotes[1] = "Load Game Chapter ";
		}
		
		Color boxColor;
		
		for (int i = 0; i < 3; i++) {
			
			switch (chaptDifficulties[i]) {
			
			case 0: boxColor = Color.green;
				break;
			case 1: boxColor = Color.blue;
				break;
			case 2: boxColor = Color.red;
				break;
			case 3: boxColor = Color.ORANGE;
				break;
			default: boxColor = Color.blue;	
			
			}
			
			if (chaptProgress[i] == 1) 	drawGapStringRectangle(g, boxColor, startX, startY + i*spacing, boxW, boxH, thickness, quotes[0]);
			else drawGapStringRectangle(g, boxColor, startX, startY + i*spacing, boxW, boxH, thickness, quotes[1] + chaptProgress[i]);

			g.setColor(Color.white);
			g.setFont(new Font("Times New Roman", Font.ITALIC, 28));
			if (inIkesMode[i]) {
				g.drawString("Ike's Mode", startX + boxW/2 - 3*Game.scale/2 + 4, startY + i*spacing + 2*boxH/3 + 25);
			} else {
				g.drawString("Hector's Mode", startX + boxW/2 - 3*Game.scale/2 - 8, startY + i*spacing + 2*boxH/3 + 25);
			}
		}
		g.setColor(Color.magenta);
		for (int i = 0; i < thickness + 3; i++) {
			g.drawRect(startX + thickness + 1 + i, startY + selectedIndex*spacing + thickness + 1 + i, boxW - thickness - 2*i - 1, boxH - thickness - 1 - 2*i);
		}
		
	}
	/** Starts the Game at the current Save-Slot location */
	public void loadGame() {
		
		switch (chaptDifficulties[selectedIndex]) {
		
		case 0: game.gameDifficulty = DIFFICULTY.Easy;
				break;
		case 1: game.gameDifficulty = DIFFICULTY.Normal;
				break;
		case 2: game.gameDifficulty = DIFFICULTY.Hard;
				break;
		case 3: game.gameDifficulty = DIFFICULTY.Crushing;
				break;
		default: System.out.println("GameLoader loadGame() default case not handled");		
		
		}
	
		if (chaptProgress[selectedIndex] > 15) {
			if (!inIkesMode[selectedIndex]) game.onHectorMode = true; 
			else game.onHectorMode = false;
		}
		
		game.setChapterOrganizer(new ChapterOrganizer(game, chaptProgress[selectedIndex], selectedIndex+1));
		
		game.chapterOrganizer.gameGold = gameGolds[selectedIndex];
		
		
	}
	
	/** Saves our game data to the text file at our Save-Slot location */
	public void saveGame() {
		game.setGameState(STATE.LoadScreen);
		setReaderToChapterStatus();
		
		try {
			writer = new PrintWriter(inputFile);
			String line;
			for (int i = 0; i < 3; i++) {
				if (i == selectedIndex) {
					if (game.chapterOrganizer.currentChapter < 15) {
						// always in Ike's mode
						line = (game.chapterOrganizer.currentChapter+1) + ":" + game.getDifficultyID() + ":" + game.chapterOrganizer.gameGold + ":Ike";
					} else {
						// check if we are in Ike or Hector's mode
						if (game.onHectorMode) {
							line = (game.chapterOrganizer.currentChapter+1) + ":" + game.getDifficultyID() + ":" + game.chapterOrganizer.gameGold + ":Hector";
						} else {
							line = (game.chapterOrganizer.currentChapter+1) + ":" + game.getDifficultyID() + ":" + game.chapterOrganizer.gameGold + ":Ike";
						}
					}
					writer.println(line);
				}
				else {
					if (inIkesMode[i]) {
						line = chaptProgress[i] + ":" + chaptDifficulties[i] + ":" + gameGolds[i] + ":Ike";
					} else {
						line = chaptProgress[i] + ":" + chaptDifficulties[i] + ":" + gameGolds[i] + ":Hector";
					}
					writer.println(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.close();
		setReaderToChapterStatus();
		game.chapterOrganizer.nextChapter(selectedIndex+1);
		
	}

	public void changeSelectedIndex(int amount) {
		selectedIndex += amount;
		if (selectedIndex > 2) selectedIndex = 0;
		if (selectedIndex < 0) selectedIndex = 2;
	}
	
	private void setReaderToChapterStatus() {
		chaptProgress = new int[3];
		chaptDifficulties = new int[3];
		gameGolds = new int[3];
		inIkesMode = new boolean[3];
		inputFile = new File("res//chapters//chapterStatus");
		try {
			reader = new Scanner(inputFile);
			String[] line;
			for (int i = 0; i < 3; i++) {
				line = reader.nextLine().split(":");
				chaptProgress[i] = Integer.parseInt(line[0]);
				if (line.length > 1) {
					chaptDifficulties[i] = Integer.parseInt(line[1]);
					if (line.length > 2) {
						gameGolds[i] = Integer.parseInt(line[2]);
						if (line.length > 3) {
							inIkesMode[i] = line[3].equalsIgnoreCase("Ike");
						}
					} else {
						gameGolds[i] = 7500;
						inIkesMode[i] = true;
					}
				} else {
					chaptDifficulties[i] = 0;
					gameGolds[i] = 7500;
					inIkesMode[i] = true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		reader.close();
	}
	private void drawGapStringRectangle(Graphics g, Color color, int startX, int startY, int width, int height,
			int thickness, String string) {
		drawPrettyRect(g, color, startX, startY, width, height, thickness);
		int font = (width / 15) - (2*string.length()/3);
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
	
	public int[] getChapterProgresses() {
		return chaptProgress;
	}
	
	public boolean[] getInIkesMode() {
		return inIkesMode;
	}
	
	public void setSelectedIndex(int val) {
		this.selectedIndex = val;
	}
	
	public int[] getChapterDifficulties() {
		return chaptDifficulties;
	}
	
}
