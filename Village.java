package tiles;

import characters.Player;
import gameMain.ChapterMap;
import items.Item;

public class Village extends Tile{

	public boolean visited;
	
	public Item gift;
	
	public Village(int x, int y, ChapterMap map, Item gift) {
		super(x, y, map);
		this.category = "Village";
		image = IM.getImage("/village.png");
		this.isGround = false;
		this.isCrossable = true;
		terrainBonuses[0] = 0;
		terrainBonuses[1] = 15;
		visited = false;
		this.gift = gift;
	}
	
	public void visit(Player player) {
		if (visited) return;
		visited = true;
		image = IM.getImage("/destroyedvillage.png");
		player.addItem(gift);
	}

}
