package chapterDesign;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import characters.*;
import gameMain.*;
import gameMain.Game;
import gameMain.Game.STATE;
import gameMain.Menu.MODE;
import items.*;
import tiles.*;

public class ChapterDesignKeyInput extends KeyAdapter {
	
	public Game game;
	public char key;
	public int keyCode;
	public Tile currentTile;
	public ChapterDesigner designer;
	public ChapterMap currentMap;
	
	public AllyPlayer chosenAlly;
	public EnemyPlayer chosenEnemy;
	
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
				designer.inCommandViewMode = false;
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
			if (designer.allySelect || designer.enemySelect) {
				if (keyCode == KeyEvent.VK_RIGHT) {
					designer.incPlayerSelection(5);
				} else if (keyCode == KeyEvent.VK_LEFT) {
					designer.incPlayerSelection(-5);
				} else if (keyCode == KeyEvent.VK_UP) {
					designer.incPlayerSelection(-1);
				} else if (keyCode == KeyEvent.VK_DOWN) {
					designer.incPlayerSelection(1);
				}
				
				if (key == 'a') {
					designer.choosePlayer(currentTile.x, currentTile.y);
					return;
				} else if (key == 's') {
					designer.allySelect = false;
					designer.enemySelect = false;
					return;
				}
			} else {
			moveCurrentTile();
			}
			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				if (currentTile.isOccupied()) {
					designer.organizer.removePlayer(currentTile.carrier);
					currentTile.carrier = null;
				}
				currentMap.setTile(currentMap.currentTile, new GrassTile(currentTile.x, currentTile.y, currentMap), 0);
			} else if (keyCode == KeyEvent.VK_SPACE) {
				currentMap.setTile(currentMap.currentTile, new WallTile(currentTile.x, currentTile.y, currentMap), 0);
			}
			
			if (key == 's') {
				currentMap.currentTile.nextSprite();
			} else if (key == 'q') {
				currentTile.allySpawnTile = true;
			}
			
			if (key == 'e') {
				designer.setEnemySelect();
			} else if (key == 'a') {
				designer.setAllySelect();
			}
			
			if (key == '`') {
				
				
			}
			
			switch (key) {
			
			case '`':
				if (designer.inCommandViewMode) designer.inCommandViewMode = false;
				else designer.inCommandViewMode = true;
				break;
			case 't':
				currentMap.setTile(currentTile, new TreeTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'm':
				currentMap.setTile(currentMap.currentTile, new MountainTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'v':
				currentMap.setTile(currentMap.currentTile, new Village(currentTile.x, currentTile.y, currentMap, new Vulnery()), 0);
				break;
			case 'w':
				currentMap.setTile(currentMap.currentTile, new WaterTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'n':
				currentMap.setTile(currentMap.currentTile, new Throne(currentTile.x, currentTile.y, currentMap), 0);				
				break;
			case 'f':
				currentMap.setTile(currentMap.currentTile, new FloorTile(currentTile.x, currentTile.y, currentMap), 0);				
				break;
			case 'p':
				currentMap.setTile(currentMap.currentTile, new PillarTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'S':
				currentMap.setTile(currentMap.currentTile, new StairsTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'F':
				currentMap.setTile(currentMap.currentTile, new FortTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'T':
				currentMap.setTile(currentMap.currentTile, new ChestTile(currentTile.x, currentTile.y, currentMap, new Vulnery()), 0);
				break;
			case 'u':
				currentMap.setTile(currentMap.currentTile, new MudTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'b':
				currentMap.setTile(currentMap.currentTile, new BridgeTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'd':
				currentMap.setTile(currentMap.currentTile, new DoorTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'A':
				currentMap.setTile(currentMap.currentTile, new ArmoryTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'V':
				currentMap.setTile(currentMap.currentTile, new VendorTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
			case 'D':
				currentMap.setTile(currentMap.currentTile, new DamagedWallTile(currentTile.x, currentTile.y, currentMap), 0);
				break;
				
			
			}
			
			
		} else if (game.gameState == STATE.ChapterDesignMenu) {
			
			if (keyCode == KeyEvent.VK_RIGHT) {
				designer.menu.incBoxIndex();
			} else if (keyCode == KeyEvent.VK_LEFT) {
				designer.menu.decBoxIndex();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				designer.menu.boxIndex = 6;
			} else if (keyCode == KeyEvent.VK_UP) {
				designer.menu.boxIndex = 0;
			}
			if (key == 'a') {
				if (designer.menu.boxIndex == 6) { //clicks on left arrow
					designer.menu.decScreenNum();
				} else if (designer.menu.boxIndex == 7) { //clicks on right arrow
					designer.menu.incScreenNum();
				} else {
					//clicks on any of the chapter boxes
					designer.menu.chooseChapter();
				}
			}
			if (key == 's') {
				game.setGameState(STATE.LoadGame);
				game.setChapterOrganizer(new ChapterOrganizer(game, 1, 1));
				game.removeKeyListener(game.getKeyListeners()[0]);
				game.addKeyListener(new KeyInput(game));
				game.menu.menuMode = MODE.Main;
				game.setGameState(STATE.Menu);
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
