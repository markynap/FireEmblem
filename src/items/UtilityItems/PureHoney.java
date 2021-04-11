package items.UtilityItems;

import items.UtilityItem;

public class PureHoney extends UtilityItem{
	
	public PureHoney() {
		super("Pure Honey", "Raises STR and MAG", 6, 0, 3);
		this.imagePath = "/itemPics/purewater.png";
	}

	public void use() {
		if (carrier == null) {
			System.out.println("Pure Honey carrier is null, fix this!");
		} else {
			if (carrier.canUse) {
				int[] stats = {damage, 0, 0, 0};
				carrier.setStatBuffs(stats, 4);
				carrier.decItemDuration(this);
				carrier.setMAUT(false);
			}
		}
	}


}
