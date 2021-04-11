package cutScene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import gameMain.Game;

/** A cutscene performed either before or after a chapter begins/ends */
public class outGameCutScene extends CutScene{
	/** Responsible for drawing players to the screen */
	private int playerOneX = Game.WIDTH/8, playerTwoX = 7*Game.WIDTH/10, playerY = Game.HEIGHT/3 + 20;
	/** Height and width of displayed Player's images */
	private int playerH = 300, playerW = 150;
	
	public outGameCutScene(Game game, String fileName, boolean startScene) {
		super(game, fileName);
		this.startOfScene = startScene;
		this.inGAME = false;
	}
	@Override
	public void render(Graphics g) {
		if (posInDialogue >= dialogue.size()) return;
		if (posInDialogue >= playersSpokenTo.size()) return;
		g.drawImage(bgImage, 0, 0, Game.WIDTH, Game.HEIGHT, null);
		if (dialoguePlayerMap.get(dialogue.get(posInDialogue)) != null) {
			g.drawImage(dialoguePlayerMap.get(dialogue.get(posInDialogue)), playerOneX, playerY, playerW, playerH, null);
		} else System.out.println("drawing null");
		if (playersSpokenTo.get(posInDialogue) != null) {
			g.drawImage(playersSpokenTo.get(posInDialogue), playerTwoX, playerY, playerW, playerH, null);
		}
		
		drawStandardTextBox(g);
		g.setFont(new Font("Times New Roman", Font.BOLD, 28));
		g.setColor(Color.black);
		g.drawString(dialogueNames.get(posInDialogue) + ": " + completeString, 20, textBoxY + Game.HEIGHT/10);
		
	}
}
