package items.UtilityItems;

import items.UtilityItem;

public class Elixir extends UtilityItem{

	public Elixir() {
		super("Elixir", "Restores Unit to Full HP", 100, 0, 4);
		this.imagePath = "/itemPics/elixir.png";
	}

	public void use() {
		if (carrier == null) {
			System.out.println("Elixir carrier is null, fix this!");
		} else {
			if (carrier.canUse) {
				carrier.currentHP = carrier.HP;
				carrier.setMAUT(false);
				carrier.decItemDuration(this);
			}
		}
	}
}
