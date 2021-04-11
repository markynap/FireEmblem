package tiles;
import gameMain.ChapterMap;

public class PillarTile extends Tile{

	public PillarTile(int x, int y, ChapterMap map) {
		super(x, y, map);
		this.spriteMapImage = Tile.pillarStonesMap;
		this.spriteMapCoordinates[0] = 95;
		this.spriteMapCoordinates[1] = 79;
		this.lengthWidthsOfSpriteMaps[1] = 24;
		this.category = "Pillar";
		this.isGround = false;
		this.isCrossable = false;
		
	}

	@Override
	public void setSprite(int spriteIndex) {
		// TODO Auto-generated method stub
		this.spriteMapCoordinates[0] = 95;
		this.spriteMapCoordinates[1] = 79;
		this.lengthWidthsOfSpriteMaps[1] = 24;
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
