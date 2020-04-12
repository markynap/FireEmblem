package gameMain;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ai_MachineIntelligence.EnemyPathFinder;
import characters.AllyPlayer;
import characters.EnemyPlayer;
import extras.TimeKeeper;
import gameMain.Game.STATE;
import tiles.Tile;

public class EnemyPhaseProcessor {

	public Game game;
	public ChapterOrganizer chapterOrganizer;
	public ChapterMap currentMap;
	public TimeKeeper timekeep;
	public EnemyPathFinder pathFinder;
	public ArrayList<Tile> tempPath;
	public Map<EnemyPlayer, Tile> destTileMap;
	public Map<EnemyPlayer, Stack<Tile>> enemyMoveMap;
	
	public EnemyPhaseProcessor(Game game) {
		this.game = game;
		this.chapterOrganizer = game.chapterOrganizer;
		this.currentMap = game.chapterOrganizer.currentMap;
		this.pathFinder = new EnemyPathFinder(game);
		timekeep = new TimeKeeper();
		tempPath = new ArrayList<>();
		destTileMap = new HashMap<>();
		enemyMoveMap = new HashMap<>();
	}
	
	public void render(Graphics g) {
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (game.getEnemiesWithMove().isEmpty()) {
			game.setGameState(STATE.Game);
			return;
		}
		EnemyPlayer nextPlayer = game.getNextEnemyWithMove();
		Stack<Tile> path = enemyMoveMap.get(nextPlayer);
//		currentMap.setCurrentTile(nextPlayer.currentTile);
		if (nextPlayer.currentTile.placeEquals(destTileMap.get(nextPlayer))) {
			game.lookForKills(nextPlayer);
			nextPlayer.setMAU(false);
			return;
		} else {
			if (path == null) {
				game.lookForKills(nextPlayer);
				nextPlayer.setMAU(false);
				return;
			} else if (path.isEmpty()) {
				game.lookForKills(nextPlayer);
				nextPlayer.setMAU(false);
				return;
			}
			currentMap.move(nextPlayer, path.firstElement());
			enemyMoveMap.get(nextPlayer).remove(0);
			nextPlayer.setCanMove(true);
			game.lookForKills(nextPlayer);
		}
	}
	public void setDestTileMap() {
		destTileMap.clear();
		enemyMoveMap.clear();
		for (int i = 0; i < chapterOrganizer.enemys.size(); i++) {
			EnemyPlayer en = chapterOrganizer.enemys.get(i);
			destTileMap.put(en, getFinalDestinationForEnemy(en));
			enemyMoveMap.put(en, getPathForEnemy(en));
		}
	}
	
	/** Sets the start and end tiles for the pathFinder, then finds the path*/
	private ArrayList<Tile> findPath(Tile startTile, Tile endTile) {
		pathFinder.setStartEnd(startTile, endTile);
		//FIX THIS
		return pathFinder.findPath();
	}
	/** finds a target location for the enemy then the path to that location*/
	private ArrayList<Tile> findPathForEnemy(EnemyPlayer enemy) {
		AllyPlayer target = findOptimalAlly(enemy);
		if (target == null) {
			ArrayList<Tile> cur = new ArrayList<>();
			cur.add(enemy.currentTile);
			return cur;
		}
		Tile targetpos = getBestTileInRange(target.currentTile, enemy.currentTile, enemy.equiptItem.range);
		ArrayList<Tile> path = findPath(enemy.currentTile, targetpos); //this is where it all goes wrong
		//need to fix the findPath method!
		return path;
	}
	/**
	 * finds the best tile surrounding the desired unit within a certain range
	 * @param startTile the unit wanting to attack
	 * @param observer the unit attacking
	 * @param range the attacking unit (observer's) range
	 * @return
	 */
	private Tile getBestTileInRange(Tile startTile, Tile observer, int range) {
		Tile tempTile;
		for (int i = 1; i <= range; i++) {
			if (observer.x - startTile.x > 0) {
				tempTile = currentMap.getTileAtAbsolutePos(startTile.x+i, startTile.y);
				if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) return tempTile;
				}
			}
			if (observer.y - startTile.y > 0) {
				tempTile = currentMap.getTileAtAbsolutePos(startTile.x, startTile.y+i);
				if (tempTile != null) {
					if (tempTile.isCrossable && tempTile.carrier == null) return tempTile;
				}
			}
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x-i, startTile.y);
			if (tempTile != null) {
			if (tempTile.isCrossable && tempTile.carrier == null) return tempTile;
			}			
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x, startTile.y-i);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) return tempTile;
			}
		}
		return startTile;
	}
	/**
	 * The final Tile that this unit can move to as part of it's path
	 * @param enemy the enemy who's path will be assessed
	 * @return the tile this unit shall move to
	 */
	public Tile getFinalDestinationForEnemy(EnemyPlayer enemy) {
		ArrayList<Tile> path = findPathForEnemy(enemy);
		
		if (path.size() == 0) {
			System.out.println("path size is zero EPP L128");
			return enemy.currentTile;
		}
		if (path.size() <= enemy.MOV) {
			return path.get(path.size()-1);
		} else {
			return path.get(enemy.MOV);
		}
	}
	/**
	 * The entire path that this enemy can move to
	 * @param enemy
	 * @return
	 */
	public ArrayList<Tile> getPathWithinMoveForEnemy(EnemyPlayer enemy) {
		ArrayList<Tile> path = findPathForEnemy(enemy);
		ArrayList<Tile> newPath = new ArrayList<>();
		if (path.size() == 0) return null;
		if (path.size() <= enemy.MOV) {
			return path;
		} else {
			for (int i = 0; i <= enemy.MOV; i++) {
				newPath.add(path.get(i));
			}
			return newPath;
		}
	}
	
	private Stack<Tile> getPathForEnemy(EnemyPlayer enemy) {
		Stack<Tile> stack = new Stack<>();
		ArrayList<Tile> path = findPathForEnemy(enemy);
		if (path.size() == 0) return null;
		if (path.size() <= enemy.MOV) {
			for (Tile a : path) {
				stack.add(a);
			}
			return stack;
		} else {
			for (int i = 0; i <= enemy.MOV; i++) {
				stack.add(path.get(i));
			}
			return stack;
		}
	}
	/**
	 * Examines the current path for a new path that leads to attack an enemy directly along the way
	 * @param path
	 * @param enemy
	 * @return
	 */
	private ArrayList<Tile> examinePathForEnemies(ArrayList<Tile> path, EnemyPlayer enemy) {
		if (path == null) throw new NullPointerException("Cannot have a null path");
		if (path.isEmpty()) throw new IllegalArgumentException("Cannot have an empty path");
		ArrayList<Tile> newPath = new ArrayList<>();
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i); //for each ally on the board
			int count = 0;
			for (int j = 0; j < path.size(); j++) { //for each tile in the path
				Tile tile = path.get(j);
				if (tileInRange(tile, ally.currentTile, enemy.equiptItem.range)) {
					//if an ally is in range along the path
					for (int z = 0; z < count; z++) {
						newPath.add(path.get(z));
					}
					return newPath;
				}
				count++;
			}
		}
		return path;
	}
	
	public boolean tileInRange(Tile start, Tile end, int range) {
		if ((Math.abs(start.x - end.x) + Math.abs(start.y - end.y)) <= range) return true;
		return false;
	}
	/**
	 * This algorithm will search through every ally player in the area
	 * if the enemy is next to an ally or in attack range, they are the priorty
	 * otherwise, the ally with the lowest currentHP will be the target
	 * @return the AllyPlayer that fits this criteria 
	 */
	private AllyPlayer findOptimalAlly(EnemyPlayer enemy) {
		if (chapterOrganizer.allys.size() == 0) {
			game.loseGame();
			return null;
		}
		int attRange = enemy.equiptItem.range;
		int min = chapterOrganizer.allys.get(0).currentHP;
		AllyPlayer target = null;
		AllyPlayer closest = chapterOrganizer.allys.get(0);
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i);
			//if an ally is in range of this enemy
			if ((Math.abs(enemy.xPos - ally.xPos) + Math.abs(enemy.yPos - ally.yPos)) <= attRange) return ally;
			int dist = game.getTrueDist(ally.currentTile, enemy.currentTile);
			if (dist < game.getTrueDist(enemy.currentTile, closest.currentTile)) {
				closest = ally;
			}
			min = Math.min(min, ally.currentHP);
			if (min == ally.currentHP) {
				target = ally;
			}
		}
		if (target == null) return chapterOrganizer.allys.get(0);
		if (closest == null) return target;
		return closest;
	}
	
	public void update() {
		this.chapterOrganizer = game.chapterOrganizer;
		this.currentMap = game.chapterOrganizer.currentMap;
	}
}
