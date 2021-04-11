package enemy_ai;

import java.awt.Color;
import java.util.ArrayList;

import characters.Player;
import gameMain.ChapterMap;
import gameMain.Game;
import tiles.Tile;

/**
 * Responsible for generating a path on a grid
 * @author mark
 *
 */
public class PathGenerator {
	
	/** Instance of our parent game */
	public Game game;
	/** Selected player, usually the carrier of the startTile */
	public Player player;
	/** Our current Path of tiles, for Attacking, Moving, or Trading GameStates */
	public ArrayList<Tile> currentPath;
	/** Our current Chapter Map */
	public ChapterMap map;
	/** Source tile, where all paths begin */
	public Tile startTile;
	/** The number of tiles away from the source we can generate a path to */
	public int move;
	/** Tiles arranged in a 3x3 grid to perform Skills such as Teraform */
	public ArrayList<Tile> terraformedTiles;
	/** The color at which specific tiles will be rendered */
	public Color tileColor;
	
	/**
	 * Creates a new PathGenerator that will highlight every tile in range as Pathable
	 * @param game
	 * @param startTile
	 * @param move
	 */
	public PathGenerator(Game game, Tile startTile, int move) {
		this.game = game;
		this.startTile = startTile;
		this.move = move;
		currentPath = new ArrayList<>();
		currentPath.add(startTile);
		map = game.chapterOrganizer.currentMap;
		player = startTile.carrier;
		if (player != null) {
			tileColor = player.teamColor;
		}
		terraformedTiles = new ArrayList<>();
	}
	
	/** Sets the 3x3 Grid needed to cast teraform */
	public void setTeraformMode() {
		if (player.currentTile.xPos < Game.nRow - 3) {
			// start it to the right of the player
			if (player.currentTile.yPos < Game.nCol - 3) {
				// let it go down beneath the player
				for (int i = 1; i <= 3; i++) {
					for (int j = 1; j <= 3; j++) {
						map.getTileAtAbsolutePos(player.currentTile.x + j, player.currentTile.y + i).inTerraformMode = true;
						terraformedTiles.add(map.getTileAtAbsolutePos(player.currentTile.x + j, player.currentTile.y + i));
					}
				}
			} else {
				// start it going up from the player, current tile is near bottom of screen
				for (int i = 1; i <= 3; i++) {
					for (int j = 1; j <= 3; j++) {
						map.getTileAtAbsolutePos(player.currentTile.x + j, player.currentTile.y - i).inTerraformMode = true;
						terraformedTiles.add(map.getTileAtAbsolutePos(player.currentTile.x + j, player.currentTile.y - i));
					}
				}
			}
		} else {
			// start it left of the player
			if (player.currentTile.yPos < Game.nCol - 3) {
				// let it go down beneath the player
				for (int i = 1; i <= 3; i++) {
					for (int j = 1; j <= 3; j++) {
						map.getTileAtAbsolutePos(player.currentTile.x - j, player.currentTile.y + i).inTerraformMode = true;
						terraformedTiles.add(map.getTileAtAbsolutePos(player.currentTile.x - j, player.currentTile.y + i));
					}
				}
			} else {
				// start it going up from the player, current tile is near bottom of screen
				for (int i = 1; i <= 3; i++) {
					for (int j = 1; j <= 3; j++) {
						map.getTileAtAbsolutePos(player.currentTile.x - j, player.currentTile.y - i).inTerraformMode = true;
						terraformedTiles.add(map.getTileAtAbsolutePos(player.currentTile.x - j, player.currentTile.y - i));
					}
				}
			}
		}
	}
	/** Moves our terraformation in the given direction N E S W */
	public void moveTeraformation(char dir) {
				
		if (dir == 'N') {
			// going north, make sure we have no terraformed tiles at the top of the map
			int minY = 20;
			for (int i = 0; i < terraformedTiles.size(); i++) {
				if (terraformedTiles.get(i).y < minY) {
					minY = terraformedTiles.get(i).y;
				}
			}
			if (minY > 0) {
				ArrayList<Tile> newTiles = new ArrayList<>();
				for (int i = 0; i < terraformedTiles.size(); i++) {
					newTiles.add(map.getTileAtAbsolutePos(terraformedTiles.get(i).x, terraformedTiles.get(i).y - 1));
					terraformedTiles.get(i).inTerraformMode = false;
				}
				terraformedTiles.clear();
				for (int i = 0; i < newTiles.size(); i++) {
					newTiles.get(i).inTerraformMode = true;
					terraformedTiles.add(newTiles.get(i));
				}
			}
			map.setAlternativeTile(terraformedTiles.get(4), map.getTileAtAbsolutePos(terraformedTiles.get(4).x, terraformedTiles.get(4).y - 1));
		} else if (dir == 'E') {
			// going east, make sure we have no terraformed tiles at the rightmost side of the map
			int maxX = 2;
			for (int i = 0; i < terraformedTiles.size(); i++) {
				if (terraformedTiles.get(i).x > maxX) {
					maxX = terraformedTiles.get(i).x;
				}
			}
			if (maxX < map.cols-1) {
				ArrayList<Tile> newTiles = new ArrayList<>();
				for (int i = 0; i < terraformedTiles.size(); i++) {
					newTiles.add(map.getTileAtAbsolutePos(terraformedTiles.get(i).x+1, terraformedTiles.get(i).y));
					terraformedTiles.get(i).inTerraformMode = false;
				}
				terraformedTiles.clear();
				
				for (int i = 0; i < newTiles.size(); i++) {
					newTiles.get(i).inTerraformMode = true;
					terraformedTiles.add(newTiles.get(i));
				}
			}
			map.setAlternativeTile(terraformedTiles.get(4), map.getTileAtAbsolutePos(terraformedTiles.get(4).x+1, terraformedTiles.get(4).y));

		} else if (dir == 'W') {
			// going west, make sure we have no terraformed tiles at the leftmost side of the map
			int minX= 20;
			for (int i = 0; i < terraformedTiles.size(); i++) {
				if (terraformedTiles.get(i).x < minX) {
					minX = terraformedTiles.get(i).x;
				}
			}
			if (minX > 0) {
				ArrayList<Tile> newTiles = new ArrayList<>();
				for (int i = 0; i < terraformedTiles.size(); i++) {
					newTiles.add(map.getTileAtAbsolutePos(terraformedTiles.get(i).x-1, terraformedTiles.get(i).y));
					terraformedTiles.get(i).inTerraformMode = false;
				}
				terraformedTiles.clear();
							
				for (int i = 0; i < newTiles.size(); i++) {
					newTiles.get(i).inTerraformMode = true;
					terraformedTiles.add(newTiles.get(i));
				}
			}
			map.setAlternativeTile(terraformedTiles.get(4), map.getTileAtAbsolutePos(terraformedTiles.get(4).x-1, terraformedTiles.get(4).y));

		} else if (dir == 'S') {
			// going south, make sure we have no terraformed tiles at the bottom of the map
			int maxY = 2;
			for (int i = 0; i < terraformedTiles.size(); i++) {
				if (terraformedTiles.get(i).y > maxY) {
					maxY = terraformedTiles.get(i).y;
				}
			}
			if (maxY < map.rows-1) {
				ArrayList<Tile> newTiles = new ArrayList<>();
				for (int i = 0; i < terraformedTiles.size(); i++) {
					newTiles.add(map.getTileAtAbsolutePos(terraformedTiles.get(i).x, terraformedTiles.get(i).y + 1));
					terraformedTiles.get(i).inTerraformMode = false;
				}
				terraformedTiles.clear();
				for (int i = 0; i < newTiles.size(); i++) {
					newTiles.get(i).inTerraformMode = true;
					terraformedTiles.add(newTiles.get(i));
				}
			}
			map.setAlternativeTile(terraformedTiles.get(4), map.getTileAtAbsolutePos(terraformedTiles.get(4).x, terraformedTiles.get(4).y + 1));

		}
		
	}
	
	/**
	 * Sets every tile within (move) distance of the startTile to Pathable
	 */
	public void setAllPathableTiles(boolean isFlier) {
		
		
		ArrayList<Tile> pathables = new ArrayList<>();
		EnemyPathFinder finder = new EnemyPathFinder(game, game.chapterOrganizer);
		int pathDist;
		if (move == 0) {
			pathDist = 1;
		} else {
			pathDist = move;
		}
		if (tileColor.equals(Color.red)) {
			pathDist += player.maxWeaponRange();
		}
		if (!isFlier) {
			for (int i = 0; i < map.tiles.size(); i++) {
				Tile tile = map.tiles.get(i);
				if (game.getTrueDist(startTile, tile) <= pathDist) {
					if (!tile.isCrossable) continue;
					if (tile.carrier == null) {
						pathables.add(tile);				
					} else {
						if (player.teamID.equalsIgnoreCase(tile.carrier.teamID)) {
							pathables.add(tile);
						}
					}
				}
			}
			// loop through pathables and see if a path to it exists
			for (Tile tile : pathables) {
				finder.setStartEnd(startTile, tile);
				ArrayList<Tile> path = finder.findPath();
				if (path.size() <= 1) {
					// if the path has only one tile in it (probably start tile)
					if (game.getTrueDist(startTile, tile) <= 1) {
						tile.setPathable(true); //if the distance between is only one tile anyway
						tile.setColorOfPath(tileColor);
					}
					
				} else {
					
					// found more than one tile for the path
					if (tileColor.equals(Color.red)) {
						// an enemy's path
						if (path.size()-1 > pathDist-player.maxWeaponRange()) {
							// attack range
							if (path.size()-1 <= pathDist) {
								tile.setPathable(true);
								tile.setColorOfPath(Color.ORANGE);
								
							} else {
								// out of our movement range, we continue
								continue;
							}
							
						} else {
							// in our movement range, we can set it to our color
							tile.setPathable(true);
							tile.setColorOfPath(tileColor);
						}
					} else {
						// ally path generation
						if (path.size()-1 > player.getMOV()) {
							continue; //path greater than we can move to, not pathable
						} else {
							if (path.get(path.size()-1).placeEquals(tile)) {
								// path actually leads us to the tile, it is pathable
								// check if there is a mountain in our way obstructing our path
								int mov = 0;
								System.out.println("mov tax for " + player.name + ":  maxMove = " + player.getMOV());
								for (int j = 1; j < path.size(); j++) {
									mov += path.get(j).movementTax();
									System.out.println("" + path.get(j).movementTax());
								}
								if (mov <= player.getMOV()) {
									tile.setPathable(true);
									tile.setColorOfPath(tileColor);
								}
								//tile.setPathable(true);
								//tile.setColorOfPath(tileColor);
							}
							
						}
					}

				}
			}
			
		} else {
			//player is a flier! can traverse over walls

			if (tileColor.equals(Color.red)) {
				// enemy unit
				for (int i = 0; i < map.tiles.size(); i++) {
					Tile tile = map.tiles.get(i);
					if (game.getTrueDist(startTile, tile) <= pathDist - player.maxWeaponRange()) {
						tile.setPathable(true);
						tile.setColorOfPath(tileColor);
					} else if (game.getTrueDist(startTile, tile) <= pathDist) {
						tile.setPathable(true);
						tile.setColorOfPath(Color.ORANGE);
					}
				}
			} else {
				// ally unit
				for (int i = 0; i < map.tiles.size(); i++) {
					Tile tile = map.tiles.get(i);
					if (game.getTrueDist(startTile, tile) <= pathDist) {
						tile.setPathable(true);
						tile.setColorOfPath(tileColor);
					}
				}
			}
		}
	}
	
	public void resetTiles() {
		for (int i = 0; i < map.tiles.size(); i++) {
			Tile t = map.tiles.get(i);
			t.setPathable(false);
			t.setArrow(false);
			t.setArrowHead(false);
			t.inTerraformMode = false;
		}
		currentPath.clear();
		terraformedTiles.clear();
	}
	/** Sets the tile as part of a move or trade state
	 * 
	 * @param t - tile to be added
	 * @param isFlier - true if player can move across walls
	 */
	public void setTileArrow(Tile t, boolean isFlier) {
		if (t == null) return;
		if (!isFlier) if (!t.pathable) return;
		if (map == null) this.map = game.chapterOrganizer.currentMap;
		if (currentPath.contains(t) || t.placeEquals(startTile)) {
			Tile oldTop = currentPath.get(currentPath.size()-1);
			oldTop.setArrow(false);
			oldTop.setArrowHead(false);
			t.setArrowHead(true);
			currentPath.remove(oldTop);
			map.selectedBoxTile = t;
			return;
		} else {
			startTile.setArrowHead(false);
		}
		
		if (currentPath.size() > move) return;
		if (this.tileColor.equals(Color.red)) {
			
		} else {
			if (isFlier) {
				if (game.getTrueDist(t, player.currentTile) > move) return;
			}
		}
		currentPath.add(t);
		// checks if our path is not longer than terrain allows
		int taxSum = 1;
		if (!isFlier) {
			for (int i = 0; i < currentPath.size(); i++) {
				taxSum += currentPath.get(i).movementTax();
			}
			if (taxSum-1 > player.getMOV()) {
				currentPath.remove(t);
				return;
			}
		}
		
		t.setArrow(true);
		if (currentPath.size() > 1) {
			map.setAlternativeTile(currentPath.get(currentPath.size()-2), t);
			map.selectedBoxTile = t;
		} else {
			map.setAlternativeTile(map.selectedBoxTile, t);
			map.selectedBoxTile = t;
		}
		
		if (!currentPath.isEmpty()) {
			currentPath.get(currentPath.size()-1).arrowHead = true;
			if (currentPath.size() > 1) {
				for (int i = 0; i < currentPath.size() - 1; i++) {
					currentPath.get(i).arrowHead = false;
				}
			}
		}
	}
	
	public Tile getTopTile() {
		if (currentPath.size() == 0) return startTile;
		return currentPath.get(currentPath.size() - 1);
	}
	public void drawTilePath(Tile t) {
		if (t == null) return;
		if (Math.abs(t.x - startTile.x) + Math.abs(t.y - startTile.y) > move) return;
		currentPath.add(t);
		map.setAlternativeTile(map.selectedBoxTile, t);
		game.chapterOrganizer.currentMap.selectedBoxTile = t;
		if (t != startTile) {
			if (t.isOccupied()) {
				if (!player.teamID.equalsIgnoreCase(t.carrier.teamID)) {
					t.carrier.drawScope = true;
				}
			}
		}
	}
	public void eraseScopes() {
		for (int i = 0; i < game.chapterOrganizer.allys.size(); i++) {
			game.chapterOrganizer.allys.get(i).drawScope = false;
		}
		for (int i = 0; i < game.chapterOrganizer.enemys.size(); i++) {
			game.chapterOrganizer.enemys.get(i).drawScope = false;
		}
	}
	
}
