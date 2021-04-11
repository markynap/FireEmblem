package items.UtilityItems;

import items.UtilityItem;

public class PureMilk extends UtilityItem{
	
	public PureMilk() {
		super("Pure Milk", "Raises MOV", 3, 0, 4);
		this.imagePath = "/itemPics/purewater.png";
	}

	public void use() {
		if (carrier == null) {
			System.out.println("Pure Milk carrier is null, fix this!");
		} else {
			if (carrier.canUse) {
				int[] stats = {0, 0, 0, damage};
				carrier.setStatBuffs(stats, 4);
				carrier.decItemDuration(this);
				carrier.setMAUT(false);
			}
		}
	}


}
