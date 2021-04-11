package items.UtilityItems;

import items.UtilityItem;

public class EnergyRing extends UtilityItem{

	public EnergyRing() {
		super("Energy Ring", "Permanently Raises STR/MAG", 3, 0, 1);
	}

	public void use() {
		if (carrier == null) {
			System.err.println("Energy Ring carrier is null, fix this!");
		} else {
			if (carrier.canUse) {
				carrier.stats[1] += damage;
				carrier.STR = carrier.stats[1];
				carrier.setMAUT(false);
				carrier.decItemDuration(this);
			}
		}
	}
}
