package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameMain.Game;
import gameMain.Game.STATE;

/** Responsible for rendering and handling the scene where the user decides if they want to follow Hector or Ike */
public class PlayerFollowChoiceGFX {
	/** Parent game for changing inGame stats like onHectorMode */
	private Game game;
	/** Hovering over 0 - Ike or 1 - Hector */
	private int selectedOption;
	/** Ike's Story selection box */
	private int[] ikeBox = {Game.WIDTH/10, Game.HEIGHT - 3*Game.scale + 15, Game.WIDTH/4, 2*Game.scale - 20};
	/** Hector's Story selection box */
	private int[] hectorBox = {2*Game.WIDTH/3 - Game.scale/2, Game.HEIGHT - 3*Game.scale + 15, Game.WIDTH/4, 2*Game.scale - 20};

	/** Should only be created once, to be displayed at the conclusion of Chapter 15 */
	public PlayerFollowChoiceGFX(Game game) {
		this.game = game;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 56));
		g.setColor(Color.white);
		
		g.drawString("Time To Choose", Game.WIDTH/4, 45);
		g.setFont(new Font("Times New Roman", Font.BOLD, 24));
		g.drawString("Players in your party have begun to dispute their ideals", 10, 110);
		g.drawString("It is clear they will be unable to work together to achieve their own goals", 10, 150);
		g.drawString("Members of your original Party have decided to follow Hector, Prince Ostia", 10, 190);
		g.drawString("Ike is not simply giving up on his mission, however.", 10, 230);
		g.drawString("It is very likely that these fighters' paths will cross again", 10, 270);
		g.drawString("somewhere in the wastelands of Dahara", 10, 310);
		g.drawString("You will be able to play MOST of the units available in this game", 10,350);
		g.drawString("however some are reserved for just Ike or Hector's story mode", 10, 390);
		g.drawString("You will eventually be able to recruit some favorite units back", 10, 430);
		g.drawString("But you must make a choice, to follow Hector and your previous family", 10, 470);	
		g.drawString("Or Follow the delusional Ike and meet new friends along the way.", 10, 510);
		
		// draw the selection boxes
		g.setColor(Color.RED);
		int thick = 4;
		for (int i = 0; i < thick; i++) {
			g.drawRect(ikeBox[0] + i, ikeBox[1] + i, ikeBox[2] - 2*i, ikeBox[3] - 2*i);
		}
		g.setColor(Color.blue);
		for (int i = 0; i < thick; i++) {
			g.drawRect(hectorBox[0]+i, hectorBox[1]+i, hectorBox[2]-2*i, hectorBox[3]-2*i);
		}
		// fill in the selected box
		g.setColor(Color.BLACK);
		if (selectedOption == 0) {
			g.fillRect(ikeBox[0] + thick, ikeBox[1] + thick, ikeBox[2] - 2*thick + 1, ikeBox[3] - 2*thick + 1);
		} else {
			g.fillRect(hectorBox[0] + thick, hectorBox[1] + thick, hectorBox[2] - 2*thick + 1, hectorBox[3] - 2*thick + 1);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.drawString("Ike's Path", ikeBox[0] + ikeBox[2]/10 + 12, ikeBox[1] + ikeBox[3]/2 + 5);
		g.drawString("Hector's Path", hectorBox[0] + hectorBox[2]/15, hectorBox[1] + hectorBox[3]/2 + 5);
	}
	
	/** Swaps the choice from Ike over to Hector, and Vice Versa */
	public void swapOption() {
		if (selectedOption == 0) selectedOption = 1;
		else selectedOption = 0;
	}
	/** Choose the selected index, whether it be Hector or Ike */
	public void chooseFollowPath() {
		// maybe prompt for an "Are you sure?" message
		if (selectedOption == 0) {
			game.onHectorMode = false;
		} else {
			game.onHectorMode = true;
		}
		// endChapter() function inside of Game() taken and used here
		game.cutScenes.startScene(game.chapterOrganizer.currentChapter, false);
		game.gameState = STATE.outGameCutScene;
	}
}
