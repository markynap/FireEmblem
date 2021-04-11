package enemy_ai;

import java.util.ArrayList;

import characters.Player;
import gameMain.*;
import tiles.Tile;

public class EnemyPathFinder {

	public Game game;
	public ChapterOrganizer chapterOrganizer;
	public ChapterMap currentMap;
	public Tile start;
	public Player player;
	public ArrayList<Tile> openSet;
	public ArrayList<Tile> closedSet;
	public ArrayList<Tile> finalpath;
	public ArrayList<Tile> path;
	public ArrayList<Tile> neighbors;
	public Tile end;
	/** Finds a Path for an enemy using the PathFindingOnSquaredGrid pathfinder */
	public EnemyPathFinder(Game game, ChapterOrganizer chapterOrganizer) {
		this.game = game;
		this.chapterOrganizer = chapterOrganizer;
		this.currentMap = chapterOrganizer.currentMap;
		openSet = new ArrayList<>();
		closedSet = new ArrayList<>();
		finalpath = new ArrayList<>();
		neighbors = new ArrayList<>();
	}

	public void setStartEnd(Tile startTile, Tile endTile) {
		if (startTile == null ) {
			System.err.println("Starttile is null!");
			return;
		}
		if (endTile == null) {
			System.err.println("Endtile is null!"); 
			return;
		}
			

		this.start = startTile;
		this.player = startTile.carrier;
		this.end = endTile;
	}
	/** Finds a path using our square grid path finder */
	public ArrayList<Tile> findPath() {
		PathFindingOnSquaredGrid fin = new PathFindingOnSquaredGrid(this);
		ArrayList<Node> path = fin.findPath(start, end);
		ArrayList<Tile> tPath = new ArrayList<>();
		for (int i = path.size()-1; i >= 0; i--) {
			tPath.add(currentMap.getTileAtAbsolutePos(path.get(i).y, path.get(i).x));
		}
		return tPath;
	}
	
	/** Returns the A* algorithm path -- less effective than findPath() */
	public ArrayList<Tile> findAstarPath() {
		
		openSet.clear();
		finalpath.clear();
		closedSet.clear();
		
		openSet.add(start);

		while (!openSet.isEmpty()) {
				int winnerIndex = 0;

				for (int i = 0; i < openSet.size(); i++) {
					if (openSet.get(i).f < openSet.get(winnerIndex).f) {
						winnerIndex = i;
					}
				}
				Tile current = openSet.get(winnerIndex);
				if (current.placeEquals(end)) {
					// Find the path
					path = new ArrayList<>();
					Tile temp = current;
					path.add(temp);
					int count = 0;
					while (temp.previous != null) {
						if (!path.contains(temp.previous)) 
							path.add(temp.previous);
						
						temp = temp.previous;
						
						count++;
						if (count > 50) {
							for (int i = path.size() -1; i >= 0; i--) {
								finalpath.add(path.get(i));
							}
							return finalpath;
						}
					}
					for (int i = path.size() -1; i >= 0; i--) {
						finalpath.add(path.get(i));
					}
					return finalpath;
				}

				openSet.remove(current);
				closedSet.add(current);
				current.findNeighbors();
				this.neighbors = current.neighbors;
				
				for (int i = 0; i < neighbors.size(); i++) {
					Tile neighbor = neighbors.get(i);
					
					if (!closedSet.contains(neighbor) && neighbor.isCrossable) {
					//	if (neighbor.isOccupied()) {
					//		if (!neighbor.carrier.sameTeam(player)) {
					//			closedSet.add(neighbor);
					//			continue;
					//		}
					//	}
						int tempG = current.g + 1;

						if (openSet.contains(neighbor)) {
							if (tempG < neighbor.g) {
								neighbor.g = tempG;
							}
						} else {
							neighbor.g = tempG;
							neighbor.h = heuristic(neighbor, end);
							neighbor.f = neighbor.g + neighbor.h;
							neighbor.previous = current;
							openSet.add(neighbor);
							continue;
						}

						neighbor.h = heuristic(neighbor, end);
						neighbor.f = neighbor.g + neighbor.h;

						neighbor.previous = current;
					}
				}
		}
		//There is no path that exists
		if (finalpath.size() == 0) {			
			finalpath.add(start);
		}
		return finalpath;
		// No solution

	}

	public int heuristic(Tile neighbor, Tile end) {
		if (neighbor != null && end != null) {
			return (Math.abs(neighbor.y - end.y) + Math.abs(neighbor.x - end.x));
		} else {
			return 1;
		}
	}
	
	public int[][] movMatrix(boolean isFlier) {
		currentMap = game.chapterOrganizer.currentMap;
		
		int[][] grid = new int[currentMap.rows][currentMap.cols];
		if (!isFlier) {
			
			for (int i = 0; i < currentMap.rows; i++) {
				for (int j = 0; j < currentMap.cols; j++) {
					grid[i][j] = currentMap.getTileAtAbsolutePos(j, i).movementTax() - 1;
				}
			}
			
		}
		return grid;
	}
	
	public boolean[][] boolMatrix(boolean isFlier) {
		currentMap = game.chapterOrganizer.currentMap;
		boolean[][] grid = new boolean[currentMap.rows][currentMap.cols];
		if (!isFlier) {
			for (int i = 0; i < currentMap.rows; i++) {
				for (int j = 0; j < currentMap.cols; j++) {
					//grid[i][j] = currentMap.getTileAtAbsolutePos(j, i).isCrossable && !currentMap.getTileAtAbsolutePos(j, i).isOccupied();
					if (currentMap.getTileAtAbsolutePos(j, i).isCrossable) {
						if (currentMap.getTileAtAbsolutePos(j, i).isOccupied()) {
							if (currentMap.getTileAtAbsolutePos(j, i).carrier.sameTeam(player)) grid[i][j] = true;
						} else grid[i][j] = true;
					}
				}
			}
		} else {
			for (int i = 0; i < currentMap.rows; i++) {
				for (int j = 0; j < currentMap.cols; j++) {
					if (currentMap.getTileAtAbsolutePos(j, i).isOccupied()) {
						if (currentMap.getTileAtAbsolutePos(j, i).carrier.sameTeam(player)) grid[i][j] = true;
					} else grid[i][j] = true;
				}
			}
		}
		return grid;
	}
}
