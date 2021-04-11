package chapterDesign;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import characters.AllyPlayer;
import characters.EnemyPlayer;
import characters.Player;
import gameMain.ChapterOrganizer;
import gameMain.Game;
import tiles.Tile;

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
	public Map<String, String> keyCommandMap = new TreeMap<>();
	/** File that holds our key commands : res//designInfo//designKeyCommands */
	private File keyCommandsFile;
	
	public boolean inCommandViewMode;
	/** If selecting enemy or ally players to put on the map */
	public boolean allySelect, enemySelect;
	
	private ArrayList<AllyPlayer> allAllies;
	
	private ArrayList<EnemyPlayer> allEnemies;
	/** Selected player index of who to drop on the current tile */
	private int playerSelect;
	
	private int xSep = 160, ySep = 118, startY =  Game.WIDTH/10 + 6, startX = Game.HEIGHT/10 + 20;
	
	private int imgW = 68, imgH = 85;
	
	/**
	 * Creates a new ChapterDesigner that will save files into a folder called /chapters
	 */
	public ChapterDesigner(Game game) {
		this.game = game;
		game.removeKeyListener(game.getKeyListeners()[0]);
		game.addKeyListener(new ChapterDesignKeyInput(game, this));
		menu = new ChapterDesignMenu(game);
		setUpKeyCommandMap();
		allAllies = new ArrayList<>();
		allEnemies = new ArrayList<>();
		setAlliesAndEnemies();
	}
	
	private void setAlliesAndEnemies() {
		allAllies.clear();
		allEnemies.clear();
		for (int playerID : game.playerIDMap.keySet()) {
			Player p = game.getPlayerByID(playerID, 0, 0, 1);
			if (p.teamID.equalsIgnoreCase("Ally")) {
				allAllies.add((AllyPlayer)p);
			} else {
				allEnemies.add((EnemyPlayer)p);
			}
		}
		
	}
	
	public void render(Graphics g) {
		if (organizer != null) organizer.render(g);
		if (inCommandViewMode) renderCommandViewMode(g);
		if (inSaveMode) renderSaveBox(g);
		if (allySelect || enemySelect) renderAllySelect(g);
	}
	
	public void renderCommandViewMode(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(Game.WIDTH/10, Game.HEIGHT/10, 4*Game.WIDTH/5, 4*Game.HEIGHT/5);
		g.setColor(Color.DARK_GRAY);
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		int inc = 0;
		int xInc = 0;
		int startY = Game.HEIGHT/10 + 25;
		int startX = Game.WIDTH/10 + 5;
		
		for (String s : keyCommandMap.keySet()) {
			g.drawString("Set An Ally:  a", startX, startY);
			g.drawString("Set An Enemy:  e", startX, startY + 35);
			g.drawString("Change Sprite: s", startX, startY+ 70);
			g.drawString("Reset Tile:    Backspace", startX, startY  + 105);
			g.drawString("Exit Designer:  Escape", startX, startY + 140);
			g.drawString("---------------------------TILES---------------------------", startX + Game.WIDTH/12, startY + 175);
			
			g.drawString(s +"  " + keyCommandMap.get(s), startX + 230*xInc, startY + 210 + (35*inc));
			inc++;
			if (inc % 10== 0) {
				xInc++;
				inc = 0;
			}
		}
	}
	
	public void renderAllySelect(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(Game.WIDTH/10, Game.HEIGHT/10, 4*Game.WIDTH/5, 4*Game.HEIGHT/5);
		g.setColor(Color.DARK_GRAY);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		int inc = 0;
		int xInc = 0;

		if (allySelect) {
			for (int i = 0; i < allAllies.size(); i++) {
				Player p = allAllies.get(i);
				g.drawString(p.name, startX + xSep*xInc + 5, startY + ySep*inc);
				g.drawImage(p.image, startX + xSep*xInc - 2,
						startY + 5 + ySep*inc, imgW, imgH, null);
				inc++;
				if (inc % 5 == 0) {
					xInc++;
					inc = 0;
				}
			}
		} else {
			for (int i = 0; i < allEnemies.size(); i++) {
				Player p = allEnemies.get(i);
				if (p == null) continue;
				g.drawString(p.name, startX + xSep*xInc + 5, startY + ySep*inc);
				g.drawImage(p.image, startX + xSep*xInc - 2,
						startY + 5 + ySep*inc, imgW, imgH, null);
				inc++;
				if (inc % 5 == 0) {
					xInc++;
					inc = 0;
				}
			}
		}
		renderPlayerSelection(g);
			
	}
	
	public void incPlayerSelection(int amount) {
		int max = 0;
		if (allySelect) max = allAllies.size();
		else max = allEnemies.size();
		
		playerSelect += amount;
		if (playerSelect >= max) playerSelect = 0;
		else if (playerSelect < 0) playerSelect = max-1;
	}
	
	private void renderPlayerSelection(Graphics g) {
		g.setColor(Color.blue);
		for (int i = 0; i < 4; i++) {
			g.drawRect(startX + xSep*(playerSelect/5)  - 4 + i, startY + ySep * (playerSelect % 5) - 20 + i,
					2*xSep/3 - 2*i, ySep - 2*i);
		}
	}
	
	public void choosePlayer(int x, int y) {
		if (allySelect) {
			AllyPlayer choice = allAllies.get(playerSelect);
			Tile playerTile = organizer.currentMap.getTileAtAbsolutePos(x, y);
			playerTile.setCarrier(choice);
			choice.setCurrentTile(playerTile);
			choice.xPos = x;
			choice.yPos = y;
			choice.currentTile.x = x;
			choice.currentTile.y = y;
			organizer.addAlly(choice);
			allySelect = false;
			
		} else {
			
			EnemyPlayer choice = allEnemies.get(playerSelect);
			Tile playerTile = organizer.currentMap.getTileAtAbsolutePos(x, y);
			playerTile.setCarrier(choice);
			choice.setCurrentTile(playerTile);
			choice.xPos = x;
			choice.yPos = y;
			choice.currentTile.x = x;
			choice.currentTile.y = y;
			organizer.addEnemy(choice);
			enemySelect = false;

		}	
		
		setAlliesAndEnemies();
		
	}
	
	public void setAllySelect() {
		if (playerSelect >= allAllies.size()) playerSelect = 0;
		enemySelect = false;
		allySelect = true;
	}
	
	public void setEnemySelect() {
		if (playerSelect >= allEnemies.size()) playerSelect = 0;
		allySelect = false;
		enemySelect = true;
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
			fileReader.close();
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
	public void chooseChapter(String chaptEnd) {
		String filename = "res//chapters//chapter" + chaptEnd;
		editor = new LevelEditor(game, new File(filename));	
	}
	
	public void renderSaveBox(Graphics g) {

		int thickness = 4;
		int[] boxPos = {Game.WIDTH/7, 100, 2*Game.WIDTH/3 - 25, Game.HEIGHT/4};
		g.setColor(Color.black);
		for (int i = 1; i <= thickness; i++) {
		g.drawRect(boxPos[0] - thickness + i, boxPos[1] - thickness + i, boxPos[2], boxPos[3]);
		}
		g.setColor(Color.white);
		g.fillRect(boxPos[0] + 1, boxPos[1] + 1, boxPos[2] - thickness, boxPos[3] - thickness);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 32));
		g.drawString("Do you wish to save your progress?", boxPos[0] + 10, boxPos[1] + 30);
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
