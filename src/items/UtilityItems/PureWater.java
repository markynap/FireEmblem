package items.UtilityItems;

import items.UtilityItem;

public class PureWater extends UtilityItem{

	public PureWater() {
		super("Pure Water", "Raises DEF and RES", 5, 0, 3);
		this.imagePath = "/itemPics/purewater.png";
	}

	public void use() {
		if (carrier == null) {
			System.out.println("Pure Water carrier is null, fix this!");
		} else {
			if (carrier.canUse) {
				int[] stats = {0, damage, damage, 0};
				carrier.setStatBuffs(stats, 3);
				carrier.decItemDuration(this);
				carrier.setMAUT(false);
			}
		}
	}

}
