package items.UtilityItems;

import items.UtilityItem;

public class DracoShield extends UtilityItem{

	public DracoShield() {
		super("Draco Shield", "Negates bonus dmg from arrows", 0, 0, 1);
		this.imagePath = "/itemPics/dracoshield.png";

	}

	public void use() {
		this.duration++;
		if (duration >= 10) duration = 1;
		
	}

}
