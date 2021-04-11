package tiles;

import characters.AllyPlayer;
import characters.Player;
import characters.Priscilla;
import gameMain.ChapterMap;
import items.Item;
import items.Wallet;

public class Village extends Tile{

	public boolean visited;
	
	public Item gift;
	
	public Village(int x, int y, ChapterMap map, Item gift) {
		super(x, y, map);
		this.category = "Village";
		this.isGround = false;
		this.isCrossable = false;
		terrainBonuses[0] = 0;
		terrainBonuses[1] = 15;
		visited = false;
		this.gift = gift;
		this.spriteMapImage = Tile.waterTerrainMap;
		this.spriteMapCoordinates[0] = 351;
		this.spriteMapCoordinates[1] = 132;
		this.lengthWidthsOfSpriteMaps[1] = 20;
	}
	/** Spawns an Ally when this village is visited */
	private void visitAndSpawnAlly(Player visiter, int chapter) {
		// ally player to be spawned
		AllyPlayer spawned = null;
		// spawn ally to the tile directly left of village entry tile
		Tile spawnTile = map.getTileAtAbsolutePos(x-1, y);
		if (spawnTile.isOccupied()) {
			// original spawnTile has somebody on it, try the other 4 locations
			spawnTile = map.getTileAtAbsolutePos(x+1, y);
			if (spawnTile.isOccupied()) {
				spawnTile = map.getTileAtAbsolutePos(x, y-1);
				if (spawnTile.isOccupied()) {
					spawnTile = map.getTileAtAbsolutePos(x+1, y-1);
					if (spawnTile.isOccupied()) {
						spawnTile = map.getTileAtAbsolutePos(x-1, y-1);
					}
				}
			}
		}
		// check the chapter we are on to determine which unit we should be spawning
		if (chapter == 10) {
			// we are on chapter 10, we should be spawning a new Priscilla
			spawned = new Priscilla(spawnTile.x, spawnTile.y, map.game, chapter);
		} else {
			// we are not on any known chapter with a Village Spawn unit, return out of this function
			return;
		}
		// we have successfully created a spawned unit, now we add them to our game
		spawned.setCurrentTile(spawnTile);
		spawnTile.setCarrier(spawned);
		map.game.chapterOrganizer.directlyAddAlly(spawned);
		spawned.setMAUT(true);
		visiter.setMAUT(false);
		setSprite(3);
		if (visiter.teamID.equalsIgnoreCase("Ally")) {
			map.game.setItemReceivedState(spawned.name);
		}
	}
	
	public void visit(Player player) {
		if (visited) return;
		// set visited to true so we cannot visit multiple times
		visited = true;
		
		// check if we are on the visitable tile
		if (getSpriteIndex() == 0) {
			
			// check if this village is supposed to contain a spawned player!
			if (map.currentChapter == 10) {
				// we should be adding Priscilla
				if (x == 18 && y == 20) {
					// the southeast village containing priscilla is at location (18,20) in Tile Coordinates
					visitAndSpawnAlly(player, 10);
					return;
				}
			}
			
			// village is not supposed to spawn an ally player, we are receiving an item
			// check if we are receiving a utility item or combat item
			// if player's wallet is too full, send the item to the Convoy instead
			if (gift.category.equalsIgnoreCase("Utility")) {
				if (player.wallet.utilities.size() >= Wallet.MAX_SIZE) {
					map.game.chapterOrganizer.convoy.addItem(gift);
				} else player.addItem(gift);
			} else {
				if (player.wallet.weapons.size() >= Wallet.MAX_SIZE) {
					map.game.chapterOrganizer.convoy.addItem(gift);
				} else player.addItem(gift);
			}
		} else {
			// not the visitable village tile, some mistake has been made
			System.err.println("Visit village was called on a non-visitable village tile, check circumstances");
			return;
		}
		player.setMAUT(false);
		setSprite(3);
		if (player.teamID.equalsIgnoreCase("Ally")) {
			player.game.setItemReceivedState(gift.name);
		}
	}

	@Override
	public void setSprite(int spriteIndex) {
		
		switch (spriteIndex) {
		
		case 0: // the visit section
				this.spriteMapCoordinates[0] = 351;
				this.spriteMapCoordinates[1] = 132;
				this.isCrossable = true;
				break;
		case 1: //bottom left
				this.spriteMapCoordinates[0] = 335;
				this.spriteMapCoordinates[1] = 132;
				this.isCrossable = false;
				break;
		case 2: //top left
				this.spriteMapCoordinates[0] = 335;
				this.spriteMapCoordinates[1] = 112;
				this.isCrossable = false;
				break;
		case 3: //top middle
				this.spriteMapCoordinates[0] = 351;
				this.spriteMapCoordinates[1] = 112;
				this.isCrossable = false;
				break;
		case 4: //top right
				this.spriteMapCoordinates[0] = 367;
				this.spriteMapCoordinates[1] = 112;
				this.isCrossable = false;
				break;
		case 5: //bottom right
				this.spriteMapCoordinates[0] = 367;
				this.spriteMapCoordinates[1] = 132;
				this.isCrossable = false;
				break;
		}
		
	}

	@Override
	public int getSpriteIndex() {
		
		int x = spriteMapCoordinates[0];
		int y = spriteMapCoordinates[1];
		
		if (x == 351) {
			if (y == 132) return 0;
			else return 3;
		} else if (x == 335) {
			if (y == 132) return 1;
			else return 2;
		} else if (x == 367) {
			if (y == 112) return 4;
			else return 5;
		} else {
			System.out.println("getSpriteIndex() in Village has problems");
			return 0;
		}
		
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 6;
	}

}
