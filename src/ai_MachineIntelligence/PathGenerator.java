package ai_MachineIntelligence;

import java.awt.Graphics;
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

	public Game game;
	public Player player;
	public ArrayList<Tile> currentPath;
	public ChapterMap map;
	public Tile startTile;
	public int move;
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
	}
	/**
	 * Sets every tile within (move) distance of the startTile to Pathable
	 */
	public void getAllTilesInRange() {
		for (int i = 0; i < map.tiles.size(); i++) {
			Tile t = map.tiles.get(i);
			if ((Math.abs(startTile.x - t.x) + Math.abs(startTile.y - t.y)) <= move) {
				t.setPathable(true);
			}
		}
	//	map.currentTile.setPathable(false);
	}
	
	public void renderMove(Graphics g) {
		
	}
	public void resetTiles() {
		for (int i = 0; i < map.tiles.size(); i++) {
			Tile t = map.tiles.get(i);
			t.setPathable(false);
			t.setArrow(false);
			t.setArrowHead(false);
		}
		currentPath.clear();
	}
	
	public void setTileArrow(Tile t, boolean tf) {
		if (currentPath.size() > startTile.carrier.MOV) return;
		if (!t.pathable) return;
		if (currentPath.contains(t)) {
			currentPath.remove(t);
			t.setArrow(false);
			return;
		}
		t.setArrow(tf);
		if (tf == true) {
			currentPath.add(t);
		}
	}
	
	public Tile getTopTile() {
		return currentPath.get(currentPath.size() - 1);
	}
	public void drawTilePath(Tile t) {
		if (Math.abs(t.x - startTile.x) + Math.abs(t.y - startTile.y) > move) return;
		
		if (t == startTile) {
			currentPath.add(t);
		} else {
			currentPath.add(t);
			if (t.isOccupied()) {
				if (player.teamID.equalsIgnoreCase(t.carrier.teamID)) {
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
