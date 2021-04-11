package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameMain.ChapterMap;
import gameMain.Game;

/** Menu controlling the different options available to change the look of the game */
public class OptionsMenu {

	private Game game;
	
	private ChapterMap map;
	
	private int[] menuStats = {Game.WIDTH - 200, Game.HEIGHT/3 - 170, 200, 460}; 
	
	private int startY = Game.HEIGHT/3 - 100, spacing = 45;
	
	private final String[] options = {"Toggle Grid Lines", "Toggle HUD", "Mute Music", "Enemy Animations",
			"Enemy Turn Delay", "1   2   3   4   5", "Animation Speed", "1   2   3   4   5", "Toggle EXP Sound"};
	
	private int selectedOption;
	
	public boolean horizontalOption = false;
	
	private int horizontalSelection;
	
	private int animationSel = 0, delaySel = 2;
	
	public OptionsMenu(Game game, ChapterMap map) {
		this.game = game;
		this.map = map;
		selectedOption = 0;
		restorePositions();
	}
	/** Returns the animation selection and enemy delay speed option to their current positions */
	private void restorePositions() {
		delaySel = (game.chapterOrganizer.ENEMY_RUN_SPEED - 16)/10;
		animationSel = game.AttackManager.ADVANCE_SPEED-1;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(menuStats[0], menuStats[1], menuStats[2], menuStats[3]);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.ITALIC|Font.BOLD, 30));
		g.drawString("OPTIONS", menuStats[0] + 25, menuStats[1] + 30);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawLine(menuStats[0], menuStats[1] + 40, Game.WIDTH, menuStats[1] + 40);

		for (int i = 0; i < options.length; i++) {
			g.drawString(options[i], menuStats[0] + 15, startY + i*spacing);
		}
		g.setColor(Color.blue);
		if (!horizontalOption) {
			for (int i = 0; i < 4; i++) {
				g.drawRect(menuStats[0]+i, startY - spacing/2 + selectedOption*spacing + i, menuStats[2] - 5 -2*i, 3*spacing/4 - 2*i);
			}
		} else {
			for (int i = 0; i < 4; i++) {
				g.drawRect(menuStats[0]+i + (30 * horizontalSelection), startY - spacing/2 + selectedOption*spacing + i, (menuStats[2])/5 - 5 -2*i, 3*spacing/4 - 2*i);
			}
		}
		
		g.setColor(Color.green);
		g.drawRect(menuStats[0] + (30*animationSel), startY - spacing/2 + 7*spacing, menuStats[2]/5 - 5, 3*spacing/4);
		g.drawRect(menuStats[0] + (30*delaySel), startY - spacing/2 + 5*spacing, menuStats[2]/5 - 5, 3*spacing/4);

		
	}
	public void incSelectedOption(int amount) {
		selectedOption += amount;
		if (selectedOption >= options.length) selectedOption = 0;
		if (selectedOption < 0) selectedOption = options.length-1;
	}
	public void incHorizSelection(int amount) {
		horizontalSelection += amount;
		if (horizontalSelection >= 5) horizontalSelection = 0;
		if (horizontalSelection < 0) horizontalSelection = 4;
	}
	
	public void select() {
		switch (selectedOption) {
		
		case 0: horizontalOption = false;
				if (map.getGridLines() == 0) {
					map.setGridLines(1);
				} else {
					map.setGridLines(0);
				}
				break;
		
		case 1: horizontalOption = false;
				if (map.drawHUD) map.drawHUD = false;
				else map.drawHUD = true; 
				break;
		
		case 2: horizontalOption = false;
				if (game.MP.running) {
					game.MP.running = false;
					if (game.MP.song != null) game.MP.song.stop();
				} else {
					game.MP.running = true;
					game.MP.playSong(0);
				}
				break;
				
		case 3: 
				if (game.chapterOrganizer.enemyMove.showAnimations) game.chapterOrganizer.enemyMove.showAnimations = false;
				else game.chapterOrganizer.enemyMove.showAnimations = true;
				break;
		
		case 4: incSelectedOption(1);
				horizontalOption = true;
				break;
		case 5: 
				if (!horizontalOption) {
					horizontalOption = true;
					horizontalSelection = 0;
					
				} else {
					horizontalOption = false; 
					game.chapterOrganizer.ENEMY_RUN_SPEED = 16 + (horizontalSelection * 10);
					game.chapterOrganizer.ENEMY_STOP_SPEED = 70 + (horizontalSelection * 70);
					this.delaySel = horizontalSelection;
				}
				
				break;
		case 6: incSelectedOption(1);
				horizontalOption = true;
				break;
		case 7:
			if (!horizontalOption) {
				horizontalOption = true;
				horizontalSelection = 0;
			} else {
				horizontalOption = false;
				game.AttackManager.ADVANCE_SPEED = (horizontalSelection + 1);
				this.animationSel = horizontalSelection;
			}
		case 8: game.expSoundOn = false;	
				break;
				
		}
		game.SFX.playSelect();
	}
}
