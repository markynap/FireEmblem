package gameMain;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tiles.Tile;

public class ChapDesignKeyInput extends KeyAdapter {
	
	public Game game;
	public char key;
	public int keyCode;
	public Tile currentTile;
	
	public ChapDesignKeyInput(Game game) {
		this.game = game;
	}
	
	public void keyPressed(KeyEvent e) {
		key = e.getKeyChar();
		keyCode = e.getExtendedKeyCode();
		currentTile = game.chapterOrganizer.currentMap.currentTile;
		
		moveCurrentTile();
		
	}

	/** responsible for moving the cursor around the map*/
	public void moveCurrentTile() {
		if (keyCode == KeyEvent.VK_RIGHT) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x + 1, currentTile.y));
		} else if (keyCode == KeyEvent.VK_LEFT) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x - 1, currentTile.y));
		} else if (keyCode == KeyEvent.VK_UP) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y - 1));
		} else if (keyCode == KeyEvent.VK_DOWN) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y + 1));
		}
	}
}
