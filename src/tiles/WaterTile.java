package tiles;

import gameMain.ChapterMap;

public class WaterTile extends Tile{

	public WaterTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Water";
		this.isGround = false;
		this.isCrossable = false;
		terrainBonuses[0] = 0;
		terrainBonuses[1] = 30;
		this.spriteMapCoordinates[0] = 140;
		this.spriteMapCoordinates[1] = 88;
		this.spriteMapImage = Tile.waterTerrainMap;
	}
	/** Sets this water tile to have a shoreline in direction NSEW*/
	public void setShoreTile(char dir) {
		
		switch (dir) {
		
		case 'N': spriteMapCoordinates[0] = 4;
				  spriteMapCoordinates[1] = 84;
				  break;
		case 'E': spriteMapCoordinates[0] = 304;
		  		  spriteMapCoordinates[1] = 226;
		  		  break;
		case 'S': spriteMapCoordinates[0] = 268;
		  		  spriteMapCoordinates[1] = 14;
		  		  break;
		case 'W': spriteMapCoordinates[0] = 108;
		  		  spriteMapCoordinates[1] = 66;
		  		  break;
		case 'O': spriteMapCoordinates[0] = 140; //original
				  this.spriteMapCoordinates[1] = 88;
				  break;
		default: System.out.println("No case for direction: " + dir + " in setShoreTile in WaterTile.java");
		}
		
	}
	
	@Override
	public void setSprite(int spriteIndex) {
		
		switch (spriteIndex) {
		
		case 0: setShoreTile('O');
				break;
		case 1: setShoreTile('N');
				break;
		case 2: setShoreTile('E');
				break;
		case 3: setShoreTile('S');
				break;
		case 4: setShoreTile('W');
				break;
		default: System.out.println("No case for spriteIndex: " + spriteIndex + " in WaterTile.java");
				 setShoreTile('O');
				 break;
			
		}
		
	}
	@Override
	public int getSpriteIndex() {
		switch (spriteMapCoordinates[0]) {
		
		case 140: return 0;//original water tile
		case 4: return 1;
		case 304: return 2;
		case 268: return 3;
		case 108: return 4;
		default: System.out.println("Sprite Index not specified in WaterTile.java"); return 0;
			
			
		
		}
	}
	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 5;
	}

	
}
