package ai_MachineIntelligence;

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

	public EnemyPathFinder(Game game) {
		this.game = game;
		this.chapterOrganizer = game.chapterOrganizer;
		this.currentMap = game.chapterOrganizer.currentMap;
		openSet = new ArrayList<>();
		closedSet = new ArrayList<>();
		finalpath = new ArrayList<>();
		neighbors = new ArrayList<>();
	}

	public void setStartEnd(Tile startTile, Tile endTile) {
		if (startTile == null || endTile == null)
			throw new NullPointerException("Can't set start or end tiles to be null!");

		this.start = startTile;
		this.player = startTile.carrier;
		this.end = endTile;
	}

	public ArrayList<Tile> findPath() {
		
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
					
					if (!closedSet.contains(neighbor) && neighbor.isCrossable && !neighbor.isOccupied()) {
						int tempG = current.g + 1;

						if (openSet.contains(neighbor)) {
							if (tempG < neighbor.g) {
								neighbor.g = tempG;
							}
						} else {
							neighbor.g = tempG;
							openSet.add(neighbor);
						}

						neighbor.h = heuristic(neighbor, end);
						neighbor.f = neighbor.g + neighbor.h;

						neighbor.previous = current;
					}
				}
		}
		//There is no path that exists
		if (finalpath.size() == 0) {
			System.out.println("no path that exists!! EnemyPathFinder L113");
			//We always get to here regardless of the path!
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

}
