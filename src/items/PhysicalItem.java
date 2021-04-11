package items;

public class PhysicalItem extends CombatItem{

	public PhysicalItem(String name, String weaponType, int damage, int hit, int weight, int duration, int range, int crit) {
		super(name, weaponType, damage, hit, weight, duration, range, crit);
		this.category = "Physical";
	}
	
	public String getDamageName() {
		return "DMG";
	}
	
}
