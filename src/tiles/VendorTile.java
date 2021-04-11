package tiles;

import characters.Player;
import gameMain.ChapterMap;

public class VendorTile extends Tile{

	public VendorTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.category = "Vendor";
		this.isGround = true;
		this.isCrossable = true;
		this.spriteMapImage = Tile.waterTerrainMap;
		this.spriteMapCoordinates[0] = 336;
		this.spriteMapCoordinates[1] = 240;
		this.lengthWidthsOfSpriteMaps[0] = 16;
		this.lengthWidthsOfSpriteMaps[1] = 16;
	}
	
	public void visit(Player player) {
		map.game.setArmoryHandler(player);
	}

	@Override
	public void setSprite(int spriteIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSpriteIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numSprites() {
		// TODO Auto-generated method stub
		return 1;
	}

}
