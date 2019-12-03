package gameMain;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import ai_MachineIntelligence.PathGenerator;
import characters.Brigand;
import characters.ExampleAlly;
import characters.Player;
import gameMain.Game.STATE;
import graphics.PopUpMenu;
import items.UtilityItem;
import tiles.Tile;

public class KeyInput extends KeyAdapter {

	public Game game;
	public char key;
	public int keyCode;
	public ChapterMap currentMap;
	public Tile currentTile;
	public Player currentPlayer;

	public KeyInput(Game game) {
		this.game = game;
	}

	public void keyPressed(KeyEvent e) {
		
		
		
		key = e.getKeyChar();
		keyCode = e.getExtendedKeyCode();
		currentMap = game.chapterOrganizer.currentMap;
		currentTile = currentMap.currentTile;
		if (game.gameState == STATE.LoseGame) return;
		
		if (key == 's') {
		if (game.pathGenerator != null)	{
			game.pathGenerator.resetTiles();
			game.pathGenerator.eraseScopes();
		}
		if (game.playerGFX!= null) game.playerGFX.setItemOptions(false);
			game.setGameState(STATE.Game);
		}
		
		if (game.gameState == STATE.Game) {
			
			moveCurrentTile();
			
			if (keyCode == KeyEvent.VK_ENTER) {
				game.chapterOrganizer.nextChapter();
			}
			
			if (key == 'n') {
				game.addAlly(new ExampleAlly(currentTile.x, currentTile.y, game));
			} else if (key == 'a') {
				if (currentTile.carrier == null) return;
				game.setPopUpMenu(new PopUpMenu(game, currentTile));
				game.setGameState(STATE.PopUpMenu);
			} else if (key == 'e') {
				game.addEnemy(new Brigand(currentTile.x, currentTile.y, game));
			}
		} else if (game.gameState == STATE.Info) {
			
			if (key == 'q') {
				game.setGameState(STATE.AdvInfo);
			} else if (key == 'a') {
				if (!game.playerGFX.inItemOptions) {
					game.playerGFX.setItemOptions(true);
				} else {
					if (game.playerGFX.itemOptionIndex == 0) {
						game.playerGFX.wallet.equipt(game.playerGFX.currentItem);
						game.playerGFX.setItemOptions(false);
					} else if (game.playerGFX.itemOptionIndex == 1) {
						//put in logic for using item
						if (game.playerGFX.currentItem.category.equalsIgnoreCase("Utility")) {
							UtilityItem it = (UtilityItem) game.playerGFX.currentItem;
							it.use();
						}
						game.playerGFX.setItemOptions(false);
					} else if (game.playerGFX.itemOptionIndex == 2) {
						currentTile.carrier.removeItem(game.playerGFX.currentItem);
						game.playerGFX.decWeaponIndex();
						game.playerGFX.setItemOptions(false);
					}
				}
			
			} else if (keyCode == KeyEvent.VK_DOWN) {
				if (!game.playerGFX.inItemOptions) game.playerGFX.incWeaponIndex();
				else game.playerGFX.incItemOptionIndex(1);
			} else if (keyCode == KeyEvent.VK_UP) {
				if (!game.playerGFX.inItemOptions) game.playerGFX.decWeaponIndex();
				else game.playerGFX.incItemOptionIndex(-1);
			} else {
				game.playerGFX.setItemOptions(false);
				game.setGameState(STATE.Game);
			}
			
			
		} else if (game.gameState == STATE.AdvInfo) {
			
			if (key == 'q') game.setGameState(STATE.Info);
			else game.setGameState(STATE.Game);
			
		} else if (game.gameState == STATE.PopUpMenu) {
			
			if (key == 's') game.setGameState(STATE.Game);
			if (keyCode == KeyEvent.VK_UP) {
				game.PUM.decSelectedOptions();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.PUM.incSelectedOptions();
			}
			currentPlayer = currentTile.carrier;
			if (key == 'a') {
				if (game.PUM.selectedIndex == 0) { //move state
					if (currentPlayer == null) return;
					if (!currentPlayer.canMove) return;
					game.setPathGenerator(new PathGenerator(game, currentTile, currentPlayer.MOV));
					game.pathGenerator.getAllTilesInRange();
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.MoveState);
				} else if (game.PUM.selectedIndex == 1) { //item state
					if (currentPlayer == null) return;
					game.playerGFX.setPlayer(currentTile.carrier);
					game.setGameState(STATE.Info);
				} else if (game.PUM.selectedIndex == 2) { //Attack State
					if (currentPlayer == null) return;
					if (!currentPlayer.canAttack) return;
					game.setPathGenerator(new PathGenerator(game, currentTile, currentPlayer.equiptItem.range));
					game.pathGenerator.getAllTilesInRange();
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.AttackState);
				} else if (game.PUM.selectedIndex == 3) { //Trade State
					
					if (currentPlayer == null) return;
					
				} else if (game.PUM.selectedIndex == 4) { //Wait State
					
					if (currentPlayer == null) return;
					currentPlayer.setMAU(false);
					
				} else if (game.PUM.selectedIndex == 5) { //End State
					
					currentMap.nextPhase();
					
				}
			}
			
		} else if (game.gameState == STATE.MoveState) {

			moveArrowTile();
			
			if (key == 'a') {
				if (game.pathGenerator.getTopTile().pathable) {
					game.chapterOrganizer.currentMap.move(currentTile.carrier, game.pathGenerator.getTopTile());
					game.chapterOrganizer.currentMap.setCurrentTile(game.pathGenerator.getTopTile());
					game.pathGenerator.resetTiles();
					game.setGameState(STATE.Game);
				}
			}
		} else if (game.gameState == STATE.AttackState) {
			
			currentPlayer = currentTile.carrier;
			if (currentPlayer == null) throw new RuntimeException("idk how this happened, attack state KeyInput");
			game.pathGenerator.eraseScopes();
			moveAttackTile();
			
			if (key == 'a' && game.pathGenerator.getTopTile().pathable) {
				if (game.pathGenerator.getTopTile().isOccupied()) {
					System.out.println(currentPlayer.name + " is attacking " + game.pathGenerator.getTopTile().carrier.name);
					//we need to store sufficient data about the attacks for rendering hit/miss/crits!
					//this is also where we should put in data regarding healers/dancers
					Player opponent = game.pathGenerator.getTopTile().carrier;
					if (currentPlayer.teamID.equalsIgnoreCase(opponent.teamID)) { //if opponent and player are on the same team
						if (currentPlayer.Class.equalsIgnoreCase("Healer")) { //if we are a healer
							
						} else if (currentPlayer.Class.equalsIgnoreCase("Dancer")) { //a dancer
							
						}
					} else { //opponent is on a different team than player
						//this is where we put in the logic for attacking
						game.AttackManager.Attack(currentPlayer, opponent);
						game.pathGenerator.resetTiles();
						game.pathGenerator.eraseScopes();
						game.setGameState(STATE.Game);
						
					}
				}
			}
			
		}
		
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
	/**for movestates or any state that draws a long trace */
	private void moveArrowTile() {
		Tile arrowTile;
		if (keyCode == KeyEvent.VK_RIGHT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x + 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.setTileArrow(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		} else if (keyCode == KeyEvent.VK_LEFT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x - 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.setTileArrow(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		} else if (keyCode == KeyEvent.VK_UP) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y - 1);
			game.pathGenerator.setTileArrow(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y + 1);
			game.pathGenerator.setTileArrow(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		}
	}
	private void moveAttackTile() {
		Tile arrowTile;
		if (keyCode == KeyEvent.VK_RIGHT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x + 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.drawTilePath(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		} else if (keyCode == KeyEvent.VK_LEFT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x - 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.drawTilePath(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		} else if (keyCode == KeyEvent.VK_UP) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y - 1);
			game.pathGenerator.drawTilePath(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y + 1);
			game.pathGenerator.drawTilePath(arrowTile);
			game.chapterOrganizer.currentMap.selectedBoxTile = arrowTile;
		}
	}
}
