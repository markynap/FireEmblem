package cutScene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import gameMain.Game;
import gameMain.Game.STATE;

/** Represents a CutScene performed in-game, using the game background */
public class inGameCutScene extends CutScene{
	
	private int playerOneX = 25, playerTwoX = Game.WIDTH - 250, playerW = 155, playerH = 250, playerY = Game.HEIGHT - 350;
	
	public inGameCutScene(Game game, String fileName) {
		super(game, fileName);
		this.startOfScene = true;
		this.inGAME = true;
		game.setGameState(STATE.inGameCutScene);
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(dialoguePlayerMap.get(dialogue.get(posInDialogue)), playerOneX, playerY, playerW, playerH, null);
		
		if (playersSpokenTo.get(posInDialogue) != null) {
			g.drawImage(playersSpokenTo.get(posInDialogue), playerTwoX, playerY, playerW, playerH, null);
		}
		
		drawStandardTextBox(g);
		g.setFont(new Font("Times New Roman", Font.BOLD, 29));
		g.setColor(Color.black);
		g.drawString(dialogueNames.get(posInDialogue) + ": " + completeString, 20, textBoxY + Game.HEIGHT/10);
		
	}


}
