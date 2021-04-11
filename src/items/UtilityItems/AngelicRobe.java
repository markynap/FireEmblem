package items.UtilityItems;

import items.UtilityItem;

public class AngelicRobe extends UtilityItem{
	
	public AngelicRobe() {
		super("Angelic Robe", "Permanently Raises HP", 8, 0, 1);
	}

	public void use() {
		if (carrier == null) {
			System.err.println("Angelic Robe carrier is null, fix this!");
		} else {
			if (carrier.canUse) {
				carrier.stats[0] += damage;
				carrier.HP = carrier.stats[0];
				carrier.setMAUT(false);
				carrier.decItemDuration(this);
			}
		}
	}


}
