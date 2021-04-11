package gameMain;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import enemy_ai.EnemyPathFinder;
import characters.AllyPlayer;
import characters.EnemyPlayer;
import characters.Player;
import extras.TimeKeeper;
import gameMain.ChapterMap.WinCondition;
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
	public Map<EnemyPlayer, ArrayList<Tile>> enemyMoveMap;
	
	public AllyPlayer choice;
	
	public int choiceTime;
	
	public String searchFactor;
	
	public EnemyPlayer nextPlayer;
	/** True if we show enemy animations */
	public boolean showAnimations = true;
	
	private ArrayList<ArrayList<Tile>> viablePaths;
	
	/** The motives of the enemy player */
	public enum Motive {
		Attack,
		Heal,
		Destroy,
		Sieze,
		Flee
	}
	
	private Motive enemyMotive = Motive.Attack;
	
	public EnemyPhaseProcessor(Game game, ChapterOrganizer chapterOrganizer) {
		this.game = game;
		this.chapterOrganizer = chapterOrganizer;
		this.currentMap = chapterOrganizer.currentMap;
		this.pathFinder = new EnemyPathFinder(game, chapterOrganizer);
		timekeep = new TimeKeeper();
		tempPath = new ArrayList<>();
		destTileMap = new HashMap<>();
		enemyMoveMap = new HashMap<>();
		searchFactor = "Distance";
		viablePaths = new ArrayList<>();
	}
	
	public void setEnemyChoice(AllyPlayer ally) {
		this.choice = ally;
		game.setGameState(STATE.EnemyChoice);
		choice.drawScope = true;
	}
	
	public void renderEnemyChoice(Graphics g) {
		
		choiceTime++;
		if (choiceTime > 25) {
			choice.drawScope = false;
			choiceTime = 0;
			game.AttackManager.Attack(nextPlayer, choice);
			if (!showAnimations) {
				game.setGameState(STATE.EnemyPhase); //we do not show the animation
			}
		}
	}
	
	/** Moves the next player to the designated tile */
	public void startTurn(EnemyPlayer nxtPlayer, Tile dest) {

		ArrayList<Tile> path;
		if (this.nextPlayer != nxtPlayer) {
			path = bestPathForEnemy(nxtPlayer);
			enemyMoveMap.replace(nxtPlayer, path);
		} else {
			path = enemyMoveMap.get(nextPlayer);
		}
		
		this.nextPlayer = nxtPlayer;
		
		if (currentMap == null) currentMap = game.chapterOrganizer.currentMap;
		
		currentMap.setCurrentTile(dest);
		currentMap.findRegion();
		
		if (path.isEmpty()) {
			if (!chapterOrganizer.lookForKills(nextPlayer)) {
				lookToHeal();
				nextPlayer.setMAUT(false);
			}
			return;
		}
		
		if (path.size() == 1) {
			// last stop in our destination
			currentMap.move(nextPlayer, dest); // move to the next tile in our path
			enemyMoveMap.get(nextPlayer).remove(dest); // remove the tile we just moved to from our path
			// look for enemies surrounding, if enemy is not found then we wait
			if (!chapterOrganizer.lookForKills(nextPlayer)) {
				lookToHeal();
				nextPlayer.setMAUT(false);
			}
		} else {
			// not at our path's final destination
			currentMap.move(nextPlayer, dest); // move to the next tile in our path
			enemyMoveMap.get(nextPlayer).remove(dest); // remove the tile we just moved to from our path
			nextPlayer.setCanMove(true); //let them move again since they are not at their destination yet
			if (!chapterOrganizer.lookForKills(nextPlayer)) {
				// nobody has been found, try to heal if we are a healer
				lookToHeal();
			}
		}
					
			

	}
	
	/** Checks nextPlayer for staff use and if any nearby allies are able to be healed */
	private void lookToHeal() {
		if (nextPlayer.isHealer()) {
			if (nextPlayer.wallet.hasHealingItem()) {
				Player healTarget = closestEnemyPlayerToHeal(nextPlayer);
				if (healTarget != null) {
					// there is a target to heal
					if (!nextPlayer.duoWeaponHeal) {
						// we are solely a healer, focus on them
						if (game.getTrueDist(healTarget.currentTile, nextPlayer.currentTile) <= nextPlayer.maxStaffRange()) {
							nextPlayer.wallet.equipHealingItem();
							nextPlayer.healPlayer(healTarget);
							nextPlayer.wallet.equipDamageItem();
							nextPlayer.setMAUT(false);
							return;
						}
					} else {
						// we can heal and attack, only prioritize heal if allies are too far to attack
						if (chapterOrganizer.closestAllyDistance(nextPlayer) >= 3 + nextPlayer.getMOV()) {
							// no allies nearby, we should try to heal
							if (game.getTrueDist(healTarget.currentTile, nextPlayer.currentTile) <= nextPlayer.maxStaffRange()) {
								nextPlayer.wallet.equipHealingItem();
								nextPlayer.healPlayer(healTarget);
								nextPlayer.wallet.equipDamageItem();
								nextPlayer.setMAUT(false);
								return;
							}
						}		
					}
				}
			}
		}
	}
	
	private void findEnemyMotive(EnemyPlayer enemy) {
		
		if (enemy.isHealer()) {
			if (enemy.duoWeaponHeal) {
				// we can use weapons and heal
				if (closestAllyDistance(enemy) > enemy.MOV + enemy.maxWeaponRange()) {
					// closest ally is out of range
					// see if other enemy team mates exist to heal
					EnemyPlayer healTarget = closestEnemyPlayerToHeal(enemy);
					
					if (healTarget == null) {
						// all enemies at full HP
						enemyMotive = Motive.Attack;
					} else {
						if (game.getTrueDist(healTarget.currentTile, enemy.currentTile) > enemy.MOV + enemy.maxStaffRange()) {
							enemyMotive = Motive.Attack;
						} else {
							enemyMotive = Motive.Heal;
						}
					}
				} else {
					enemyMotive = Motive.Attack;
				}
			} else {
				// we can only heal or flee
				if (enemy.wallet.hasHealingItem()) {
					enemyMotive = Motive.Heal;
				} else {
					enemyMotive = Motive.Flee;
				}
				return;
			}
		} else {
			// we can Attack, Destroy, Flee, or Sieze the Ally's Defense
			if (chapterOrganizer.getCurrentMap().winConditionEquals(WinCondition.Defend)) {
				// enemies are trying to attack/reach the objective
				// move toward objective if we are within 2*movement cost of it,
				// or if all ally units are out of sight range
				
				
			} else {
				
				if (enemy.wallet.weapons.isEmpty()) {
					// Destroy or Flee
				}
				
				// Attack, Destroy
				if (chapterOrganizer.listOfOpposingUnitsInRange(enemy, enemy.MOV + enemy.maxWeaponRange()).isEmpty()) {
					// no units in range
					// see if there is a village we can destroy
					
					// if not we attack
					enemyMotive = Motive.Attack;
					
				} else {
					// there are enemy units in range
					enemyMotive = Motive.Attack;
				}
				
			}
			
		}	
	}
	
	public ArrayList<Tile> bestPathForEnemy(EnemyPlayer enemy) {
		
		viablePaths = new ArrayList<>();
		ArrayList<Tile> bestPath = new ArrayList<>();
		ArrayList<AllyPlayer> allies_per_path = new ArrayList<>();

		// start by finding a path to all allies currently on the map, if possible
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i);
			if (ally.isBeingCarried) continue;
			Tile attackTile = getBestTileInRange(ally.currentTile, enemy.currentTile, enemy.maxWeaponRange());
			ArrayList<Tile> path = findPath(enemy.currentTile, attackTile);
			
			int dist = game.getTrueDist(enemy.currentTile, attackTile);
			
			if (dist > path.size()) {
				// there is no path to this ally
				// either this enemy or the ally are trapped in an enclosed space
			} else {
				// if the ally is visible to the enemy, add it as a viable path
				if (enemy.sightRange >= dist) {
					viablePaths.add(path);
					allies_per_path.add(ally);
				}
			}
			
		}
		// now that we have all of our possible routes, we will decide the motive
		findEnemyMotive(enemy);
				
		
		if (enemyMotive == Motive.Attack) {
						
			if (viablePaths.isEmpty()) {
				// no targets we can reach, proceed no further
				return bestPath;
			}
			
			// locate all targets that are in range
			ArrayList<ArrayList<Tile>> pathsInRange = new ArrayList<>();
			ArrayList<AllyPlayer> allies_in_range = new ArrayList<>();
			
			for (int i = 0; i < viablePaths.size(); i++) {			
				if (viablePaths.get(i).size() <= enemy.getMOV()) {
					pathsInRange.add(viablePaths.get(i));
					allies_in_range.add(allies_per_path.get(i));
				}
			}
			
			if (pathsInRange.isEmpty()) {
				// no targets in range, proceed toward closest one
				int least_index = 0;
				int min = 1002000000;
				for (int i = 0; i < viablePaths.size(); i++) {
					if (viablePaths.get(i).size() < min) {
						min = viablePaths.get(i).size();
						least_index = i;
					}
				}
				
				for (int i = 0; i <= enemy.MOV; i++) {
					try {
						Tile tile = viablePaths.get(least_index).get(i);
						if (tile != null) bestPath.add(tile);
					} catch (Exception e) {
						break;
					}
				}
				return bestPath;
			} else {
				
				// targets are in range, proceed toward one with most points
				if (pathsInRange.size() == 1) {
					return pathsInRange.get(0);
				}
				
				// multiple targets are in range
				// find the target with the most points
				int whichPath = calculatePoints(enemy, allies_in_range);
				
				return pathsInRange.get(whichPath);
			}
			
		} else if (enemyMotive == Motive.Heal) {
			
			EnemyPlayer closest = closestEnemyPlayerToHeal(enemy);
			
			if (closest == null) {
				return bestPath;
			}
			
			Tile healTile = this.getBestTileInRange(closest.currentTile, enemy.currentTile, enemy.maxStaffRange());
			ArrayList<Tile> path = findPath(enemy.currentTile, healTile);
			for (int i = 0; i < path.size(); i++) {
				bestPath.add(path.get(i));
				if (i == enemy.MOV) break;
			}
			return bestPath;
		
			
		} else if (enemyMotive == Motive.Flee) {
			
			return bestPath;
		}
				
				
		return bestPath;
		
	}
	
	private int closestAllyDistance(EnemyPlayer enemy) {
		
		if (viablePaths == null) return Integer.MAX_VALUE;
		
		int min = Integer.MAX_VALUE;
		
		for (int i = 0; i < viablePaths.size(); i++) {
			ArrayList<Tile> cPath = viablePaths.get(i);
			if (cPath.size() < min) {
				min = cPath.size();
			}
		}
		return min;
	}
	
	
	/** Returns the index of the ally player with the highest 'kill points' score 
	 *  higher the kill points score, the more desireable of a target*/
	private int calculatePoints(EnemyPlayer enemy, ArrayList<AllyPlayer> inRange) {
				
		if (inRange.isEmpty()) return 0;
		
		int[] killPoints = new int[inRange.size()];
		
		// calculating factors
		int hitPoints, damagePoints;
		
		for (int i = 0; i < inRange.size(); i++) {
			AllyPlayer ally = inRange.get(i);
			
			hitPoints = enemy.getEffectiveHit(ally);
			
			if (enemy.getEffectiveSpeed() - ally.getEffectiveSpeed() >= 4) {
				damagePoints = 2*enemy.getEffectiveDamage(ally);
			} else {
				damagePoints = enemy.getEffectiveDamage(ally);
			}
			
			if (ally.currentHP - damagePoints <= 0) {
				killPoints[i] += (hitPoints * 1.75);
			}
			
			if ((ally.isHealer() && !ally.duoWeaponHeal) || ally.isDancer()) {
				killPoints[i] += 100;
			} else if (ally.isHealer()) {
				killPoints[i] += 25;
			}
			
			if (ally.maxWeaponRange() < enemy.maxWeaponRange()) {
				killPoints[i] += 40;
			}
			
			if (Player.getWeaponTriangleStats(enemy, ally)[0] > 0) {
				killPoints[i] += 5;
			}
			
			killPoints[i] += hitPoints/5;
			killPoints[i] += 2*damagePoints;
			killPoints[i] += enemy.getEffectiveCrit(ally);
			killPoints[i] -= ally.currentHP/5;
		}
		
		int most_points = Integer.MIN_VALUE;
		int most_points_index = 0;
			
		for (int i = 0; i < killPoints.length; i++) {
			if (killPoints[i] > most_points) {
				most_points = killPoints[i];
				most_points_index = i;
			}
		}
		return most_points_index;
	}
	
	
	/** Sets the start and end tiles for the pathFinder, then finds the path*/
	private ArrayList<Tile> findPath(Tile startTile, Tile endTile) {
		pathFinder.setStartEnd(startTile, endTile);

		return pathFinder.findPath();
	}
	

	/** Returns the closest enemy player to the source to heal as an enemy */
	private EnemyPlayer closestEnemyPlayerToHeal(Player source) {
		EnemyPlayer target = null;
		EnemyPlayer option;
		for (int i = 0; i < chapterOrganizer.enemys.size(); i++) {
			option = chapterOrganizer.enemys.get(i);
			if (option.equals(source)) continue;
			if (option.currentHP < option.HP) {
				if (target == null) {
					target = option;
				} else {
					int distDiff = game.getTrueDist(option.currentTile, source.currentTile) - game.getTrueDist(target.currentTile, source.currentTile);
					if (distDiff < 0) {
						// option is closer than target
						target = option;
					} else if (distDiff == 0) {
						// same distance away
						// check which is lower health
						int diffHP = target.HP - target.currentHP;
						int diffHP2 = option.HP - option.currentHP;
						if (diffHP < diffHP2) target = option;
					}
				}
			}
		}
		return target;
	}
	
	/**
	 * finds the best tile surrounding the desired unit within a certain range
	 * @param startTile the tile belonging to our target
	 * @param observer the unit attacking the target
	 * @param range the attacking unit (observer's) range
	 * @return
	 */
	private Tile getBestTileInRange(Tile startTile, Tile observer, int range) {
		Tile tempTile;
		int shortestDist = 500;
		Tile bestTile = null;
		for (int i = 1; i <= range; i++) {
		
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x+i, startTile.y);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}
		
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x, startTile.y+i);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}
		
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x-i, startTile.y);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}			
			
			tempTile = currentMap.getTileAtAbsolutePos(startTile.x, startTile.y-i);
			if (tempTile != null) {
				if (tempTile.isCrossable && tempTile.carrier == null) {
					if (game.getTrueDist(tempTile, observer) < shortestDist) {
						shortestDist = game.getTrueDist(tempTile, observer);
						bestTile = tempTile;
					}
				}
			}
			
		}
		if (bestTile == null) {
			return startTile;
		} else return bestTile;
	}
	
	
	public void setDestTileMap() {
		this.currentMap = game.chapterOrganizer.currentMap;
		destTileMap.clear();
		enemyMoveMap.clear();
		this.nextPlayer = null;
		for (int i = 0; i < chapterOrganizer.enemys.size(); i++) {
			EnemyPlayer en = chapterOrganizer.enemys.get(i);
			// we are just adding current tiles because now it is all loaded player by player in startTurn()
			destTileMap.put(en, en.currentTile);
			ArrayList<Tile> tStack = new ArrayList<>();
			tStack.add(en.currentTile);
			enemyMoveMap.put(en, tStack);
			
		}
	}
	
	public boolean tileInRange(Tile start, Tile end, int range) {
		return ((Math.abs(start.x - end.x) + Math.abs(start.y - end.y)) <= range);
	}
	
	/** Sets the map the enemies can see to our current game map */
	public void updateMap() {
		this.currentMap = chapterOrganizer.currentMap;
	}
	
}
