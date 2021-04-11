package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import extras.PasswordEncryptor;
import gameMain.Game.DIFFICULTY;
import gameMain.Game.STATE;

public class Menu {

	public Game game;
	public int optionIndex;
	public boolean[] options;
	public int boxX, boxW, boxH, boxDistApart, prettyRectDistApart;
	public int Adelay = 0;
	public String[] devTitles = {"New Game", "Load Game", "Choose Chapter", "Chapter Design"};
	public String[] userTitles = {"New Game", "Load Game", "Developer Login"};
	public String[] difficulties = {"Easy", "Normal", "Hard", "Crushing"};
	
	/** Coordinates for boxes */
	private int startX = Game.WIDTH/11, startY = Game.HEIGHT/20, SboxW = 4*Game.WIDTH/5-20, SboxH = Game.HEIGHT/4, spacing = 5*Game.HEIGHT/17, thickness = 5;
	/** Amount of gold we will start the new game with */
	private int gameGold;
	
	private int[] pwbox = {Game.WIDTH/4, Game.WIDTH/2, Game.WIDTH/2, Game.HEIGHT/8};
	
	private String attempted_pw;
	
	private String show_pw;
	
	public enum MODE {
		Main,
		DifficultySelection,
		LoadLevelSelection,
		DevLogin
	}
	
	public MODE menuMode = MODE.Main;
	
	public Menu(Game game) {
		this.game = game;
		if (game.inDevMode) {
			options = new boolean[4];
		} else {
			options = new boolean[3];
		}
		boxDistApart = 28;
		boxH = Game.HEIGHT/6;
		boxX = Game.WIDTH/11;
		boxW = 4*Game.WIDTH/5 - 20;
		prettyRectDistApart = 3;
		menuMode = MODE.Main;
		gameGold = 7500;
		attempted_pw = "";
		show_pw = "";
	}
	
	public void compare_passwords() {
		
		try {
		Scanner reader = new Scanner(new File("res//designInfo//login"));
		
		String saved_pw = "/.;'";
		
		if (reader.hasNextLine()) {
			saved_pw = reader.nextLine();
		}
		reader.close();
		
		if (PasswordEncryptor.getHashedMessage(attempted_pw).equals(saved_pw)) {
			
			// we are allowed entry!
			game.setDevMode(true);
			options = new boolean[4];
			game.menu.setMenuMode(MODE.Main);
			
		}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public void addToAttemptedPassword(char key) {
		
		attempted_pw += key;
		show_pw += "*";
		
	}
	
	public void backSpace() {
		if (attempted_pw.isEmpty()) return;
		
		attempted_pw = attempted_pw.substring(0, attempted_pw.length()-1);
		show_pw = show_pw.substring(0, show_pw.length()-1);
	}
	
	public void render(Graphics g) {
		if (menuMode == MODE.DifficultySelection) {
			renderDifficultySelection(g);
		} else if (menuMode == MODE.Main) {
			int thickness = 4;
			int startHeight = 40;
			g.setColor(Color.black);
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			String title = "";
			for (int i = 0; i < options.length; i++) {
				if (game.inDevMode) title = devTitles[i];
				else title = userTitles[i];
				drawGapStringRectangle(g, Color.RED, boxX, startHeight + ((boxH + boxDistApart) * i), boxW, boxH, thickness, title);
			}
			g.setColor(Color.BLUE);
			for (int i = 0; i < (thickness + prettyRectDistApart + 1); i++) {
				g.drawRect(boxX + thickness + i, startHeight + ((boxH + boxDistApart) * optionIndex) + thickness + i, boxW - thickness - (2*i), boxH - thickness - (2*i));
			}
		} else if (menuMode == MODE.LoadLevelSelection){
			// load level selection
			if (game.gameLoader != null) {
			
				g.setColor(Color.black);
				g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
				String[] quotes = new String[2];
				
				quotes[0] = "Save over Empty Game";
				quotes[1] = "Save over Existing Game chapt: ";
			
				Color boxColor;
				
				for (int i = 0; i < 3; i++) {
					
					switch (game.gameLoader.getChapterDifficulties()[i]) {
					
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
					
					if (game.gameLoader.getChapterProgresses()[i] == 1) 	drawGapStringRectangle(g, boxColor, startX, startY + i*spacing, SboxW, SboxH, thickness, quotes[0]);
					else drawGapStringRectangle(g, boxColor, startX, startY + i*spacing, SboxW, SboxH, thickness, quotes[1] + game.gameLoader.getChapterProgresses()[i]);

					g.setColor(Color.white);
					g.setFont(new Font("Times New Roman", Font.ITALIC, 28));
					if (game.gameLoader.getInIkesMode()[i]) {
						g.drawString("Ike's Mode", startX + SboxW/2 - 3*Game.scale/2 + 4, startY + i*spacing + 2*SboxH/3 + 25);
					} else {
						g.drawString("Hector's Mode", startX + SboxW/2 - 3*Game.scale/2 - 8, startY + i*spacing + 2*SboxH/3 + 25);
					}
				}
				g.setColor(Color.magenta);
				for (int i = 0; i < thickness + 3; i++) {
					g.drawRect(startX + thickness + 1 + i, startY + optionIndex*spacing + thickness + 1 + i, SboxW - thickness - 2*i - 1, SboxH - thickness - 1 - 2*i);
				}
				
			}
		} else if (menuMode == MODE.DevLogin) {
			
			renderDevLogin(g);
		}
	}
	
	private void renderDevLogin(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g.setColor(Color.red);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 80));
		
		g.drawString("Password", Game.WIDTH/3 - 30, Game.HEIGHT/3);
		for (int i = 0; i < 3; i++) {
			g.drawRect(pwbox[0] + i, pwbox[1] + i, pwbox[2] - 2*i, pwbox[3] - 2*i);
		}
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		g.drawString(show_pw, pwbox[0] + 5, pwbox[1] + pwbox[3]/3);
		
	}
	
	
	public void setMenuMode(MODE mode) {
		this.menuMode = mode;
	}
	
	public void selectDifficulty() {
		//set up our chapter organizer for a new game
		
		if (optionIndex == 0) {
			game.gameDifficulty = DIFFICULTY.Easy;
			gameGold = 7500;
		} else if (optionIndex == 1) {
			game.gameDifficulty = DIFFICULTY.Normal;
			gameGold = 6000;
		} else if (optionIndex == 2) {
			game.gameDifficulty = DIFFICULTY.Hard;
			gameGold = 5000;
		} else if (optionIndex == 3) {
			game.gameDifficulty = DIFFICULTY.Crushing;
			gameGold = 3500;
		} else {
			System.out.println("KEYINPUT MENU STATE -- option index not accounted for");
		}
	
		// set cut scene to start at the first chapter intro
		menuMode = MODE.LoadLevelSelection;
		optionIndex = 0;
	}
	
	public void createNewGame() {
		game.setChapterOrganizer(new ChapterOrganizer(game, 1, optionIndex+1));
		game.setLoadLevel(optionIndex+1);
		game.chapterOrganizer.gameGold = this.gameGold;
		game.cutScenes.startScene(1, true);
		game.gameState = STATE.outGameCutScene;
		System.out.println("Created a new game at load level - " + (optionIndex+1));
		System.out.println("Game gold: " + game.chapterOrganizer.gameGold);
	}
	
	public void renderDifficultySelection(Graphics g) {
		int thickness = 4;
		int startHeight = 40;
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		for (int i = 0; i < difficulties.length; i++) {
			drawGapStringRectangle(g, Color.RED, boxX, startHeight + ((boxH + boxDistApart) * i), boxW, boxH, thickness, difficulties[i]);
		}
		g.setColor(Color.BLUE);
		for (int i = 0; i < (thickness + prettyRectDistApart + 1); i++) {
			g.drawRect(boxX + thickness + i, startHeight + ((boxH + boxDistApart) * optionIndex) + thickness + i, boxW - thickness - (2*i), boxH - thickness - (2*i));
		}
	}
	
	private void drawPrettyRect(Graphics g, Color color, int startX, int startY, int width, int height, int thickness) {
		
		g.setColor(color);
		for (int i = 0; i < thickness; i++) {
			g.drawRect(startX + i, startY + i, width, height);
			g.drawRect(startX + (prettyRectDistApart * thickness) + i, startY +(prettyRectDistApart * thickness) + i, width - ((2*prettyRectDistApart) * thickness), height - ((2*prettyRectDistApart) * thickness));
		}
	
	}
	
	public void drawGapStringRectangle(Graphics g, Color color, int startX, int startY, int width, int height, int thickness, String string) {
		drawPrettyRect(g, color, startX, startY, width, height, thickness);
		int font = width/20;
		g.setFont(new Font("Times New Roman", Font.BOLD, font));
		if (string != null) g.drawString(string, startX + (width/2) - (font*2) - (3*string.length()), startY + (height/2) + 10);
		
	}
	
	public void incSelectedOptions() {
		
		if (menuMode == MODE.LoadLevelSelection) {
			optionIndex++;
			if (optionIndex >= 3) {
				optionIndex = 0;
			}
			return;
		} else if (menuMode == MODE.DifficultySelection) {
			optionIndex++;
			if (optionIndex > 3) {
				optionIndex = 0;
			}
			return;
		}
		
		options[optionIndex] = false;
		optionIndex++;
		if (optionIndex >= options.length)
			optionIndex = 0;
		options[optionIndex] = true;
	}

	public void decSelectedOptions() {
		if (menuMode == MODE.LoadLevelSelection) {
			optionIndex--;
			if (optionIndex < 0) {
				optionIndex = 2;
			}
			return;
		} else if (menuMode == MODE.DifficultySelection) {
			optionIndex--;
			if (optionIndex < 0) {
				optionIndex = 3;
			}
			return;
		}
		options[optionIndex] = false;
		optionIndex--;
		if (optionIndex < 0)
			optionIndex = options.length - 1;
		options[optionIndex] = true;
	}
}
