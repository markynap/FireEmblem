package gameMain;

import java.util.Random;

import characters.*;
import gameMain.Game.DIFFICULTY;
import tiles.Tile;

/** Spawns Ally or Enemy units in the game depending on the current turn */
public class Spawner {
	
	private Game game;
	
	private ChapterMap map;
	
	private Random random;
	
	
	public Spawner(Game game, ChapterMap map) {
		this.game = game;
		this.map = map;
		random = new Random();
	}
	
	public Spawner(Game game) {
		this.game = game;
		this.map = game.chapterOrganizer.currentMap;
	}
	/** Spawn the appropriate set of units for our current Chapter and Turn */
	public void spawnUnits() {
		
		int chapt = game.chapterOrganizer.currentChapter;
		
		if (chapt == 2) {
			
			switch (map.turnCount) {
			
			case 3: spawnUnit(new Mercenary(0,0,game,chapt));
					spawnUnit(new Brigand(0,0,game,chapt));
					break;
			
			case 4: spawnUnit(new Mage(0, 0, game, chapt));
					spawnUnit(new Cavalier(0, 0, game, chapt));
					break;
			
			case 6: spawnUnit(new Archer(0, 0, game, chapt));
					spawnUnit(new Cavalier(0, 0, game, chapt));
					break;
			
			case 7: spawnUnit(new Cavalier(14, 14, game, chapt));
					break;
					
			case 8: spawnUnit(new Archer(0,0,game,chapt));
					spawnUnit(new Archer(0,0,game,chapt));
					if (game.gameDifficulty == DIFFICULTY.Normal) {
						spawnUnit(randomEnemyUnit(chapt));
					} else if (game.gameDifficulty == DIFFICULTY.Hard) {
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(randomEnemyUnit(chapt));
					} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(randomEnemyUnit(chapt));
					}
					break;		
			}
			
		} else if (chapt == 3) {
			
			if (game.getChapterOrganizer().numEnemiesLeft() < 4) return;
			if (map.turnCount%5 == 1) {
				spawnUnit(new Cavalier(0,0,game,chapt));
				spawnUnit(new Archer(0,0,game,chapt));
			}
			
		} else if (chapt == 5) {
		
			if (map.turnCount > 3) {
				if (map.turnCount % 2 == 0) {
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
					if (game.gameDifficulty == DIFFICULTY.Hard) {
						spawnUnit(randomEnemyUnit(chapt));
					} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(new Wyvern(0,0,game,chapt));
					}
				}
			}
			
			
		} else if (chapt == 6) {
			
			if (map.turnCount % 2 == 0) {
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				if (map.turnCount % 4 == 0) {
					spawnUnit(randomEnemyUnit(chapt));
				}
				if (game.gameDifficulty == DIFFICULTY.Hard) {
					spawnUnit(randomEnemyUnit(chapt));
				} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
				}
			} else if (map.turnCount % 3 == 0) {
				spawnUnit(randomEnemyUnit(chapt));
			}
			
		} else if (chapt == 7) {
			
			spawnUnit(randomEnemyUnit(chapt));
			spawnUnit(randomEnemyUnit(chapt));
			if (map.turnCount % 2 == 0) {
				spawnUnit(new Wyvern(0,0,game,chapt));
				
				if (game.gameDifficulty == DIFFICULTY.Normal) {
					spawnUnit(randomEnemyUnit(chapt));
				} else if (game.gameDifficulty == DIFFICULTY.Hard) {
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
				} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
				}
			}
		} else if (chapt == 10) {
			
			if (map.turnCount % 2 == 0) {
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
			}
			if (map.turnCount % 3 == 0) {
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
			}
			
		} else if (chapt == 11) {
			
			if (map.turnCount % 3 == 0) {
				if (game.chapterOrganizer.enemys.size() > 3) {
					spawnRandomEnemyUnit(false);
					spawnRandomEnemyUnit(false);
					spawnRandomEnemyUnit(false);
				}
			}
		
			
		} else if (chapt == 12) {

			spawnUnit(randomEnemyUnit(chapt));
			spawnUnit(randomEnemyUnit(chapt));
			spawnUnit(randomEnemyUnit(chapt));
			spawnUnit(randomEnemyUnit(chapt));
			if (game.gameDifficulty == DIFFICULTY.Hard) {
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
			} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
			}
		} else if (chapt == 13) {
			
			if (map.turnCount % 3 == 0) {
				if (game.chapterOrganizer.enemys.size() < 3) return;
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				if (game.gameDifficulty == DIFFICULTY.Hard) {
					spawnUnit(randomEnemyUnit(chapt));
				} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
				}
			}

			
		} else if (chapt == 14) {
			
			spawnUnit(randomEnemyUnit(chapt));
			if (map.turnCount % 2 == 0) {
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
				spawnUnit(randomEnemyUnit(chapt));
			}
		} else if (chapt == 15) {
			
			if (map.turnCount > 1) {
				if (map.turnCount % 2 == 0) {
					spawnUnit(new Wyvern(0,0,game,chapt));
					spawnUnit(randomEnemyUnit(chapt));
					spawnRandomEnemyUnit(false);
					if (game.gameDifficulty == DIFFICULTY.Hard) {
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(randomEnemyUnit(chapt));
					} else if (game.gameDifficulty == DIFFICULTY.Crushing) {
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(randomEnemyUnit(chapt));
						spawnUnit(randomEnemyUnit(chapt));
					}
				}
				if (map.turnCount % 3 == 0) {
					spawnRandomEnemyUnit(false);
					spawnUnit(randomEnemyUnit(chapt));
					spawnUnit(randomEnemyUnit(chapt));
				}
			}
			
		}
		
	}
	
	private EnemyPlayer randomEnemyUnit(int chapt) {
		if (game.gameDifficulty == DIFFICULTY.Crushing) {
			return Player.randomEnemyUnit(Math.max(0, chapt - 6), chapt, game);
		} else {
			return Player.randomEnemyUnit(Math.max(0, chapt - 7), chapt, game);
		}
	}
	/** Spawns a random enemy unit that is either promoted or unpromoted */
	private void spawnRandomEnemyUnit(boolean promoted) {
		Player unit;
		if (promoted) {
			unit = randomEnemyUnit(14);
		} else {
			unit = randomEnemyUnit(2);
		}
		
		Tile fort = game.chapterOrganizer.currentMap.getUnoccupiedFort();
		if (fort != null) {
			unit.xPos = fort.x;
			unit.yPos = fort.y;
			unit.setCurrentTile(fort);
			game.addPlayer(unit);
		}
	}
	
	private void spawnUnit(Player unit) {
		if (unit.teamID.equalsIgnoreCase("Ally")) {
			if (!unit.currentTile.isOccupied()) {
				game.addPlayer(unit);
			}
			else {
				Tile copy;
				for (int i = 0; i < 25; i++) {
					copy = map.getTileAtAbsolutePos(random.nextInt(map.cols), random.nextInt(map.rows));
					if (!copy.isOccupied()) {
						unit.setCurrentTile(copy);
						unit.xPos = copy.x;
						unit.yPos = copy.y;
						game.addPlayer(unit);
						return;
					}
				}
			}
		} else {
			
			Tile fort = game.chapterOrganizer.currentMap.getUnoccupiedFort();
			if (fort != null) {
				unit.xPos = fort.x;
				unit.yPos = fort.y;
				unit.setCurrentTile(fort);
				game.addPlayer(unit);
			}
			
		}
	}
	

}
