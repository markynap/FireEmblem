package chapterDesign;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import characters.*;
import gameMain.*;
import gameMain.Game;
import gameMain.Game.STATE;
import items.*;
import tiles.*;

public class ChapterDesignKeyInput extends KeyAdapter {
	
	public Game game;
	public char key;
	public int keyCode;
	public Tile currentTile;
	public ChapterDesigner designer;
	public ChapterMap currentMap;
	
	public ChapterDesignKeyInput(Game game, ChapterDesigner designer) {
		this.game = game;
		this.designer = designer;
	}
	
	public void keyPressed(KeyEvent e) {
		key = e.getKeyChar();
		keyCode = e.getExtendedKeyCode();
		if (designer.organizer != null) {
			currentMap = designer.organizer.currentMap;
			currentTile = currentMap.currentTile;
		}
		
		
		if (game.gameState == STATE.ChapterDesign) {
			if (keyCode == KeyEvent.VK_ESCAPE) {
				designer.flipSaveMode();
			}
			if (designer.inSaveMode) {
				if (key != 'a' && keyCode != KeyEvent.VK_ENTER) {
					designer.flipYesNoBox();
					return;
				} else {
					//this is where we save our chapter info
					if (designer.boxIndex == 0) {
						//save changes
						designer.editor.saveContents();
					} else {
						//do not save changes
						designer.flipSaveMode();
					}
					game.backToMenu();
				}
			}
			moveCurrentTile();
			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				if (currentTile.isOccupied()) {
					designer.organizer.removePlayer(currentTile.carrier);
					currentTile.carrier = null;
				}
				currentMap.setTile(currentMap.currentTile, new GrassTile(currentTile.x, currentTile.y, currentMap));
			} else if (keyCode == KeyEvent.VK_SPACE) {
				currentMap.setTile(currentMap.currentTile, new WallTile(currentTile.x, currentTile.y, currentMap));
			}
			
			
			if (key == 't') {
				currentMap.setTile(currentTile, new TreeTile(currentTile.x, currentTile.y, currentMap));
			} else if (key == 'i') {
				designer.organizer.addAlly(new Ike(currentTile.x, currentTile.y, game));
			} else if (key == 'h') {
				designer.organizer.addAlly(new Hector(currentTile.x, currentTile.y, game));
			} else if (key == 'r') {
				designer.organizer.addAlly(new Raymond(currentTile.x, currentTile.y, game));
			} else if (key == 'e') {
				designer.organizer.addEnemy(new Brigand(currentTile.x, currentTile.y, game));
			} else if (key == 'm') {
				currentMap.setTile(currentMap.currentTile, new MountainTile(currentTile.x, currentTile.y, currentMap));
			} else if (key == 'v') {
				currentMap.setTile(currentMap.currentTile, new Village(currentTile.x, currentTile.y, currentMap, new Vulnery()));
			} else if (key == 'w') {
				currentMap.setTile(currentMap.currentTile, new WaterTile(currentTile.x, currentTile.y, currentMap));
			} else if (key == 'n') {
				currentMap.setTile(currentMap.currentTile, new Throne(currentTile.x, currentTile.y, currentMap));				
			} else if (key == 'f') {
				currentMap.setTile(currentMap.currentTile, new FloorTile(currentTile.x, currentTile.y, currentMap));				
			} else if (key == 'p') {
				currentMap.setTile(currentMap.currentTile, new PillarTile(currentTile.x, currentTile.y, currentMap));
			}
			
			
		} else if (game.gameState == STATE.ChapterDesignMenu) {
			
			if (keyCode == KeyEvent.VK_RIGHT) {
				designer.menu.incBoxIndex();
			} else if (keyCode == KeyEvent.VK_LEFT) {
				designer.menu.decBoxIndex();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				designer.menu.boxIndex = 6;
			} else if (keyCode == KeyEvent.VK_UP) {
				designer.menu.boxIndex = 7;
			}
			if (key == 'a') {
				if (designer.menu.boxIndex == 7) { //clicks on left arrow
					designer.menu.decScreenNum();
				} else if (designer.menu.boxIndex == 6) { //clicks on right arrow
					designer.menu.incScreenNum();
				} else {
					//clicks on any of the chapter boxes
					designer.menu.chooseChapter();
				}
			}
		}
		
	}

	/** responsible for moving the cursor around the map*/
	public void moveCurrentTile() {
		if (keyCode == KeyEvent.VK_RIGHT) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x + 1, currentTile.y));
		} else if (keyCode == KeyEvent.VK_LEFT) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x - 1, currentTile.y));
		} else if (keyCode == KeyEvent.VK_UP) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y - 1));
		} else if (keyCode == KeyEvent.VK_DOWN) {
			designer.organizer.currentMap.setCurrentTile(
					designer.organizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y + 1));
		}
	}
}
