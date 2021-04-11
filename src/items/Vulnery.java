package items;

import characters.Player;

public class Vulnery extends UtilityItem {

	
	public Vulnery() {
		super("Vulnery", "Restores 15 hp", 15, 0, 5);
		this.imagePath = "/itemPics/vulnery.png";
	}
	public Vulnery(Player carrier) {
		super("Vulnery", "Restores 15 hp", 15, 0, 5);
		this.carrier = carrier;
		this.imagePath = "/itemPics/vulnery.png";
	}
	@Override
	public void use() {
		if (carrier == null) {
			System.out.println("Vulnery carrier is null! MUST FIX");
			return;
		}
		if (carrier.canUse) {
			if (carrier.currentHP == carrier.HP) return;
			carrier.takeDamage(-this.damage);
			carrier.decItemDuration(this);
			carrier.setMAUT(false);
		}
	}
}
