package items.UtilityItems;

import items.UtilityItem;

public class Gold extends UtilityItem{
	
	public Gold(int quantity) {
		super("Gold", "Adds Gold To Inventory", quantity*50, 0, quantity*50);
		this.imagePath = "/itemPics/gold.png";
	}

	public void use() {
		if (carrier == null) {
			System.out.println("GOLD USE BROKEN CARRIER IS NULL");
			return;
		}
		carrier.game.chapterOrganizer.incGameGold(this.duration);
		carrier.wallet.utilities.remove(this);
		carrier.game.playerGFX.decWeaponIndex();
	}
}
