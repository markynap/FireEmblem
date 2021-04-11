package chapterDesign;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameMain.ChapterOrganizer;
import gameMain.Game;
import gameMain.Game.DIFFICULTY;
import gameMain.Game.STATE;

/**
 * Menu where we can choose which chapter to build Chooses files that are
 * disguised as Chapters and edits their contents
 * 
 * @author mark
 *
 */
public class ChapterDesignMenu {

	private Game game;
	protected String title;
	public int boxIndex, maxBoxes, screenNum;
	private final int maxScreen = 5;
	private final int startX = Game.WIDTH/10;
	private final int boxW = Game.WIDTH/4 - 10;
	private final int boxH = Game.HEIGHT/3 - 18;
	private final int startY = Game.HEIGHT/10 + 10;
	private final int thickness = 3;
	private final int arrowLength = 50;
	// right arrow
	private final int[] initRect = {Game.WIDTH - 200, Game.HEIGHT - 100, 80, 35};
	private final int[] xPoint = {initRect[0] + initRect[2], initRect[0] + initRect[2] + arrowLength, initRect[0] + initRect[2]};
	private final int[] yPoint = {initRect[1] - initRect[3]/2, initRect[1] + initRect[3]/2, initRect[1] + 3*initRect[3]/2};
	// left arrow
	private final int[] initRect2 = {120, Game.HEIGHT - 100, 80, 35};
	private final int[] xPoint2 = {initRect2[0], initRect2[0] - arrowLength, initRect2[0]};
	private final int[] yPoint2 = {initRect2[1] - initRect2[3]/2, initRect2[1] + initRect2[3]/2, initRect2[1] + 3*initRect2[3]/2};
	/** Index of the chapter we are selecting */
	
	private static String[] chapterTitles = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
			"13", "14", "15", "16i", "16h", "17i", "17h", "18i", "18h", "19i", "19h", "20i", "20h", "21i", "21h",
			"22i", "22h", "23i"
	};
	
	public ChapterDesignMenu(Game game) {
		this.game = game;
		maxBoxes = 8;
		screenNum = 0;
		this.title = "Chapter Designer";
	}

	public void render(Graphics g) {
		
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		drawTitle(g);
		int index = (screenNum * 6);
		
		for (int i = 0; i < 3; i++) {
			drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), startY, boxW, boxH, thickness, "Chapter " + chapterTitles[index + i]);
			if (index + 3+i >= chapterTitles.length) continue;
			drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), boxH + (3*startY/2), boxW, boxH, thickness, "Chapter " + chapterTitles[index + 3+i]);
		}
		
		/*if (screenNum > 2) {
			chapterStart = 15 + 3*(screenNum-2) - 1;
		}
		*/
		//draw boxes for players to select which chapter to edit
	/*	if (chapterStart < 13) {
			for (int i = 0; i < 3; i++) {
				drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), startY, boxW, boxH, thickness, "Chapter " + (chapterStart + i));
				drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), boxH + (3*startY/2), boxW, boxH, thickness, "Chapter " + (chapterStart + 3 + i));
			}
		} else if (chapterStart == 13) {
			for (int i = 0; i < 3; i++) {
				drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), startY, boxW, boxH, thickness, "Chapter " + (chapterStart + i));
				drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), boxH + (3*startY/2), boxW, boxH, thickness, "Chapter " + (chapterStart + 3 + ((i)/2)));
			}
		} else {
			// chapterStart > 13
			if (screenNum % 2 == 0) {
				// if we are on screens 4, 6 (ch 21, 36)
				for (int i = 0; i < 3; i++) {
					drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), startY, boxW, boxH, thickness, "Chapter " + (chapterStart + ((i+1)/2)));
					drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), boxH + (3*startY/2), boxW, boxH, thickness, "Chapter " + (chapterStart + 2 + (i/2)));
				}
			} else {
				// screens 3, 5 (ch 18, 30)
				for (int i = 0; i < 3; i++) {
					drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), startY, boxW, boxH, thickness, "Chapter " + (chapterStart + ((i+1)/2)));
					drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), boxH + (3*startY/2), boxW, boxH, thickness, "Chapter " + (chapterStart + 2 + ((i)/2)));
				}
			}
			
		}*/
		drawSelectedBox(g);
		drawArrows(g);
		//draw arrows for going forward/backward between chapter selections
	}
	
	public void drawTitle(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 50));
		g.drawString(title, Game.WIDTH/3 - 3*title.length(), 45);
	}

	public void chooseChapter() {
		game.designer.chooseChapter(chapterTitles[6*screenNum + boxIndex]);
	}
	
	public void incScreenNum() {
		screenNum++;
		if (screenNum >= maxScreen) {
			screenNum = 0;
		}
	}
	public void decScreenNum() {
		screenNum--;
		if (screenNum < 0) {
			screenNum = maxScreen-1;
		}
	}
	
	private void drawArrows(Graphics g) {
		

		if (boxIndex == 7) g.setColor(Color.BLUE);
		else g.setColor(Color.WHITE);
		g.fillRect(initRect[0], initRect[1], initRect[2], initRect[3]);
		g.fillPolygon(xPoint, yPoint, 3);
		
		if (boxIndex == 6) g.setColor(Color.BLUE);
		else g.setColor(Color.WHITE);
		g.fillRect(initRect2[0], initRect2[1], initRect2[2], initRect2[3]);
		g.fillPolygon(xPoint2, yPoint2, 3);
		
	}
	
	private void drawSelectedBox(Graphics g) {
		int tempIndex = 0;
		int startY2 =  boxH + (3*startY/2);
		int _W = boxW - thickness;
		int _H = boxH - thickness;
		g.setColor(Color.WHITE);
		if (boxIndex < 6) {
		if (boxIndex > 2) {
			tempIndex = boxIndex - 3;
		for (int i = 0; i < thickness+2; i++)
			g.drawRect(startX + ((boxW+20)*tempIndex) + thickness + i, startY2 + thickness + i, _W - (2*1), _H - (2*i));
		} else {
		for (int i = 0; i < thickness+2; i++)
			g.drawRect(startX + ((boxW+20)*boxIndex) + thickness + i, startY + thickness + i, _W - (2*i), _H - (2*i));
		}
		}
	}
	
	public void incBoxIndex() {
		boxIndex++;
		if (boxIndex >= maxBoxes) boxIndex = 0;
	}

	
	public void decBoxIndex() {
		boxIndex--;
		if (boxIndex < 0) boxIndex = (maxBoxes-1);
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

	public void drawGapStringRectangle(Graphics g, Color color, int startX, int startY, int width, int height,
			int thickness, String string) {
		drawPrettyRect(g, color, startX, startY, width, height, thickness);
		int font = width / 8;
		g.setFont(new Font("Times New Roman", Font.BOLD, font));
		if (string != null)
			g.drawString(string, startX + (width / 2) - (3*font/2) - (3 * string.length()), startY + (height / 2) + 10);

	}
	/** Chooses to load the game at the specified chapter using the third save state information by default
	 *  the game will automatically be set to EASY mode
	 */
	public void chooseGameChapter() {
		game.setGameState(STATE.LoadScreen);
		game.gameDifficulty = DIFFICULTY.Easy;
		int chapt = 0;
		String chaptString = chapterTitles[6*screenNum + boxIndex];
		char ikeIndex;
		if (chaptString.length() > 2) {
			chapt = Integer.parseInt(chaptString.substring(0, chaptString.length()-1));
			ikeIndex = chaptString.charAt(chaptString.length()-1);
		} else {
			// it is 1-15
			chapt = Integer.parseInt(chapterTitles[6*screenNum + boxIndex]);
			ikeIndex = 'n';
		}
		if (ikeIndex == 'n') {
			System.out.println("Automatically loaded new data to save state 3 at Easy difficulty, between chapters 1-15");
		} else {
			if (ikeIndex == 'i') {
				game.onHectorMode = false;
			} else {
				game.onHectorMode = true;
			}
		}
		game.setChapterOrganizer(new ChapterOrganizer(game, chapt, 3));
		
	}
	
}
