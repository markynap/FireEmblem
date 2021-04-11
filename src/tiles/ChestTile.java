package tiles;

import characters.Player;
import gameMain.ChapterMap;
import gameMain.ChapterMap.WinCondition;
import items.Item;
import items.Key;
import items.Wallet;
import items.UtilityItems.Gold;

public class ChestTile extends Tile{
	
	/** The treasure stored in this chest */
	private Item gift;
	/** Whether the chest has been opened or not */
	private boolean opened = false;
	
	public ChestTile(int x, int y, ChapterMap map, Item gift) {
		super(x, y, map);
		if (gift == null) {
			this.gift = Item.getRandomChestItem();
		} else {
			this.gift = gift;
		}
		this.isCrossable = true;
		this.isGround = true;
		this.category = "Chest";
		this.spriteMapImage = Tile.throneMap;
		this.spriteMapCoordinates[0] = 31;
		this.spriteMapCoordinates[1] = 223;
		this.lengthWidthsOfSpriteMaps[0] = 17;
		this.lengthWidthsOfSpriteMaps[1] = 17;
	}
	
	public void giveGift(Player receiver) {
		if (!opened) {
			if (gift.name.equalsIgnoreCase("Key")) {
				Key keyItem = (Key) gift;
				keyItem.setGame(receiver.game);
				gift = keyItem;
			} else if (gift.name.equalsIgnoreCase("Gold")) {
				Gold goldItem = (Gold)gift;
				goldItem.carrier = receiver;
			}
			if (gift.category.equalsIgnoreCase("Utility")) {
				if (receiver.wallet.utilities.size() >= Wallet.MAX_SIZE) {
					map.game.chapterOrganizer.convoy.addItem(gift);
				} else receiver.addItem(gift);
			} else {
				if (receiver.wallet.weapons.size() >= Wallet.MAX_SIZE) {
					map.game.chapterOrganizer.convoy.addItem(gift);
				} else receiver.addItem(gift);
			}
			opened = true;
			receiver.setMAU(false);
			if (receiver.teamID.equalsIgnoreCase("Ally")) {
				receiver.game.setItemReceivedState(gift.name);
			}
			if (map.winConditionEquals(WinCondition.Capture)) {
				map.currentNumChests++;
				if (map.currentNumChests == map.maxChestsToWin) {
					map.game.endChapter();
				}
			}
		}
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
