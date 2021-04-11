package cutScene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import gameMain.Game;
import gameMain.Game.STATE;
/** A standard CutScene has all of these elements */
public abstract class CutScene {

	public Game game;
	
	/** Which Chapter we are currently on */
	public int chapterNumber;
	/** File we will use to read the cutscene */
	public File readFile;
	/** Contains a list of the lists of words spoken in order*/
	public ArrayList<String> dialogue;
	/** A mapping between specific dialogue and the players who speak it */
	public TreeMap<String, Image> dialoguePlayerMap;
	/** Image behind our speaking Players */
	public Image bgImage;
	/** a list of the players in order of dialogue that are receiving the words spoken */
	public ArrayList<Image> playersSpokenTo;
	/** Names of the people speaking the dialogue */
	public ArrayList<String> dialogueNames;
	
	/** Position of the TextBox */
	protected int textBoxY = 4*Game.HEIGHT/5;
	/** The String to grow toward our final dialogue */
	protected String completeString;
	/** Helps the text-flow come at a slower rate */
	protected int timeKeepTracker;
	/** The current character position in the string we are writing */
	protected int posInString;
	/** Which dialogue string we are processing */
	protected int posInDialogue;
	/** True if there is more dialogue left to process */
	protected boolean continueTyping;
	/** True if this is the beginning scene of a chapter */
	protected boolean startOfScene;
	/** The thickness of the text box */
	protected int thickness = 5;
	/** True if this cut scene takes place inGame - false if it takes place before or after the chapter starts/finishes */
	protected boolean inGAME;
	/** Current system time in miliseconds, used for drawing the text */
	private long currentTime;
	
	/** Creates a new standard CutScene filling in important information */
	public CutScene(Game game, String fileName) {
		this.game = game;
		readFile = new File(fileName);
		dialogue = new ArrayList<>();
		dialoguePlayerMap = new TreeMap<>();
		playersSpokenTo = new ArrayList<>();
		dialogueNames = new ArrayList<>();
		setDialogue();
		completeString = "" + dialogue.get(0).charAt(0);
		posInString = 1;
		posInDialogue = 0;
		continueTyping = true;
		currentTime = System.currentTimeMillis();
	}
	
	public void setDialogue() {
		try {
			Scanner reader = new Scanner(readFile);
			bgImage = Game.IM.getImage(reader.nextLine());
			String line = "";
			String[] dialogueChop;
			String[] playerChop;
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				dialogueChop = line.split(":");
				playerChop = dialogueChop[0].split("-");
				dialogue.add(dialogueChop[1]);
				dialoguePlayerMap.put(dialogueChop[1], Game.IM.getImage("/characterPics/" + playerChop[0] + ".png"));
				if (playerChop.length > 1) {
					playersSpokenTo.add(Game.IM.getImage("/characterPics/" + playerChop[1] + ".png"));
				} else playersSpokenTo.add(null);
				
				dialogueNames.add(playerChop[0]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			game.setGameState(STATE.Menu);
		}
	}
	
	protected void drawStandardTextBox(Graphics g) {
		g.setColor(Color.black);
		for (int i = 0; i < thickness; i++)
			g.drawRect(i, textBoxY + i, Game.WIDTH - 2*i - 5, Game.HEIGHT - textBoxY - 2*i);
		g.setColor(Color.white);
		g.fillRect(thickness, textBoxY + thickness, Game.WIDTH - 3*thickness, Game.HEIGHT - textBoxY - 9*thickness);
	}
	
	
	
	public void fillLine() {
		if (completeString.equalsIgnoreCase(dialogue.get(posInDialogue))) {
			setNextLine();
			return;
		}
		completeString = dialogue.get(posInDialogue);
		posInString = completeString.length();
		continueTyping = false;
	}
	
	public void setPreviousLine() {
		posInString = 0;
		posInDialogue--;
		completeString = "";
		if (posInDialogue < 0) posInDialogue = 0;
		continueTyping = true;
		currentTime = System.currentTimeMillis();
	}
	
	public void setNextLine() {
		
		posInString = 0;
		posInDialogue++;
		completeString = "";
		if (posInDialogue >= dialogue.size()) {
			posInDialogue = 0;
			currentTime = System.currentTimeMillis();
			posInString = 0;
			continueTyping = false;
			if (inGAME) {
				//different for inGame cut scenes
				game.chapterOrganizer.currentMap.inCutScene = false;
				game.chapterOrganizer.currentMap.cutSceneDestroyed = true;
				game.setGameState(STATE.Game);
				game.cutScenes.nullAllScenes();
				return;
			}
			game.destroyOutGameCutScene(startOfScene);
		} else continueTyping = true;
	}

	public void tick() {

		if (continueTyping) {
			long now = System.currentTimeMillis();
			if (now - currentTime >= 40) {
				completeString += dialogue.get(posInDialogue).charAt(posInString);
				posInString++;
				if (posInString >= dialogue.get(posInDialogue).length()) {
					continueTyping = false;
				}
				currentTime = System.currentTimeMillis();
			}
		}
	}
	
	public abstract void render(Graphics g);
}
