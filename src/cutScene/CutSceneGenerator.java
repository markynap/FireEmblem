package cutScene;

import java.awt.Graphics;

import gameMain.Game;

/** Generates the appropriate cutscene depending on where we are in the game */
public class CutSceneGenerator {
	
	public Game game;
	
	public outGameCutScene cutScene;
	
	public inGameCutScene inGameCutScene;
	
	public CutSceneGenerator(Game game) {
		this.game = game;
	}
	
	public void tick() {
		if (cutScene != null) cutScene.tick();
		else if (inGameCutScene != null) inGameCutScene.tick();
	}
	
	public void render(Graphics g) {
		if (cutScene != null) cutScene.render(g);
		else if (inGameCutScene != null) {
			inGameCutScene.render(g);
		}
	}
	
	/** Starts the CutScene for the oppropriate Chapter, be it the start or ending cutscene
	 *  if startScene is true it will launch the opening cutScene*/
	public void startScene(int whichChapter, boolean startScene) {
		String suffix = "";
		if (startScene) suffix = "start";
		else suffix = "end";
		String fileName = "res//cutScenes//chapter" + whichChapter + suffix;
		cutScene = new outGameCutScene(game, fileName, startScene);
	}
	
	/** Resets all of our cutScenes back to null */
	public void nullAllScenes() {
		cutScene = null;
		inGameCutScene = null;
	}
	
	public void startInGameScene(int whichChapter, int whichSceen) {
		
		//	res//cutScenes//inGameScenes//chapter1_1 is the first inGame scene of chapter 1
		cutScene = null;
		String filename = "res//cutScenes//inGameScenes//chapter" + whichChapter + "_" + whichSceen;
		inGameCutScene = new inGameCutScene(game, filename);
		
	}

}
