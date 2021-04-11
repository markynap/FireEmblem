package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import gameMain.Game;
import gameMain.Game.STATE;

public class TutorialDisplayer {
	
	private Game game;
	
	private ArrayList<String> categories;
	
	private ArrayList<ArrayList<String>> data;
	
	private File tutorialFile;
	
	private Scanner reader;
	
	private int selectedIndex;
	
	private boolean inCategoryMode;
	
	private int catStartX = Game.WIDTH/3, catStartY = 95, catSpacing = 48;
	
	public TutorialDisplayer(Game game) {
		this.game = game;
		categories = new ArrayList<>();
		data = new ArrayList<>();
		tutorialFile = new File("res//designInfo//tutorialData");
		inCategoryMode = true;
		try {
			reader = new Scanner(tutorialFile);
			String line;
			String[] lineParts;
			while (reader.hasNextLine()) {
				
				line = reader.nextLine();
				lineParts = line.split(":");
				
				if (lineParts.length > 1) {
					// it is a category
					ArrayList<String> dataSegment = new ArrayList<>();
					if (lineParts[1].equalsIgnoreCase("option")) {
						categories.add(lineParts[0]);
						while (reader.hasNextLine()) {
							line = reader.nextLine();
							if (line.equalsIgnoreCase("ENDTUTORIAL")) break;
							dataSegment.add(line);
						}
						data.add(dataSegment);
					}
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/** True if we are selecting a tutorial category */
	public boolean inCategoryMode() {
		return inCategoryMode;
	}
	
	public void setCategoryMode(boolean tf) {
		inCategoryMode = tf;
	}
	
	/** When User Presses 's' when in Tutorial State */
	public void handleBackCommand() {
		if (inCategoryMode) {
			game.setGameState(STATE.Game);
		} else {
			inCategoryMode = true;
		}
	}
	
	public void incSelectedIndex(int amount) {
		selectedIndex += amount;
		if (selectedIndex < 0) selectedIndex = categories.size()-1;
		else if (selectedIndex >= categories.size()) selectedIndex = 0;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 46));
		
		g.drawString("Tutorial", Game.WIDTH/2 - 2*Game.scale, 45);
		
		if (inCategoryMode) {

			g.setFont(new Font("Times New Roman", Font.BOLD, 34));
			g.setColor(Color.blue);
			for (int i = 0; i < 4; i++) {
				g.drawRect(catStartX - 4 + i, catStartY - 30 + i + selectedIndex*catSpacing, 285 - 2*i, 40 - 2*i);
			}
			
			g.setColor(Color.white);
			for (int i = 0; i < categories.size(); i++) {
				g.drawString(categories.get(i), catStartX, catStartY + catSpacing*i);
			}
		} else {
			g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 19));

			for (int j = 0; j < data.get(selectedIndex).size(); j++) {
				g.drawString(data.get(selectedIndex).get(j), 15, 100 + 32*j);
			}
		}
	}
}
