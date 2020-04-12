package chapterDesign;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import gameMain.Game;

/**
 * Menu where we can choose which chapter to build Chooses files that are
 * disguised as Chapters and edits their contents
 * 
 * @author mark
 *
 */
public class ChapterDesignMenu {

	public Game game;
	public File selectedFile;
	public String title;
	
	public int boxIndex, maxBoxes, screenNum;
	public final int maxScreen = 5;
	public final int startX = 175;
	public final int boxW = 250;
	public final int boxH = 250;
	public final int startY = 140;
	public final int thickness = 3;
	public final int arrowLength = 80;
	public final int[] initRect = {Game.WIDTH - 200, Game.HEIGHT - 200, 80, 60};
	public final int[] xPoint = {initRect[0] + initRect[2], initRect[0] + initRect[2] + arrowLength, initRect[0] + initRect[2]};
	public final int[] yPoint = {initRect[1] - initRect[3]/2, initRect[1] + initRect[3]/2, initRect[1] + 3*initRect[3]/2};
	
	public final int[] initRect2 = {120, Game.HEIGHT - 200, 80, 60};
	public final int[] xPoint2 = {initRect2[0], initRect2[0] - arrowLength, initRect2[0]};
	public final int[] yPoint2 = {initRect2[1] - initRect2[3]/2, initRect2[1] + initRect2[3]/2, initRect2[1] + 3*initRect2[3]/2};
	

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
		//draw boxes for players to select which chapter to edit
		for (int i = 0; i < 3; i++) {
			drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), startY, boxW, boxH, thickness, "Chapter " + ((6 * screenNum) + (i+1)));
			drawGapStringRectangle(g, Color.blue, startX + ((boxW + 20)*i), boxH + (3*startY/2), boxW, boxH, thickness, "Chapter " + ((6 * screenNum) + (4 + i)));
		}
		drawSelectedBox(g);
		drawArrows(g);
		//draw arrows for going forward/backward between chapter selections
	}
	
	public void drawTitle(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 50));
		g.drawString(title, Game.WIDTH/3, 50);
	}

	public void chooseChapter() {
		game.designer.chooseChapter((6*screenNum) + boxIndex+1);
	}
	
	public void incScreenNum() {
		screenNum++;
		if (screenNum >= maxScreen) screenNum = 0;
	}
	public void decScreenNum() {
		screenNum--;
		if (screenNum < 0) screenNum = maxScreen-1;
	}
	
	private void drawArrows(Graphics g) {
		

		if (boxIndex == 6) g.setColor(Color.BLUE);
		else g.setColor(Color.WHITE);
		g.fillRect(initRect[0], initRect[1], initRect[2], initRect[3]);
		g.fillPolygon(xPoint, yPoint, 3);
		
		if (boxIndex == 7) g.setColor(Color.BLUE);
		else g.setColor(Color.WHITE);
		g.fillRect(initRect2[0], initRect2[1], initRect2[2], initRect2[3]);
		g.fillPolygon(xPoint2, yPoint2, 3);
		
	}
	
	private void drawSelectedBox(Graphics g) {
		int tempIndex = 0;
		int boxDist = 65;
		int startY2 =  boxH + (3*startY/2);
		int W_H = boxW - thickness;
		g.setColor(Color.WHITE);
		if (boxIndex < 6) {
		if (boxIndex > 2) {
			tempIndex = boxIndex - 3;
		for (int i = 0; i < thickness+2; i++)
			g.drawRect(startX + ((boxW+20)*tempIndex) + thickness + i, startY2 + thickness + i, W_H - (2*1), W_H - (2*i));
		} else {
		for (int i = 0; i < thickness+2; i++)
			g.drawRect(startX + ((boxW+20)*boxIndex) + thickness + i, startY + thickness + i, W_H - (2*i), W_H - (2*i));
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
	public void setSelectedFile(File file) {
		if (file == null)
			throw new NullPointerException("Cannot set file to null in ChapterDesignMenu setSelectedFile method");
		selectedFile = file;
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
}
