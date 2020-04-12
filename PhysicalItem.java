package items;

public class PhysicalItem extends CombatItem{

	public PhysicalItem(String name, int damage, int hit, int weight, int duration, int range, int crit) {
		super(name, damage, hit, weight, duration, range, crit);
		this.category = "Physical";
	}
	
	public String getDamageName() {
		return "DMG";
	}
	
}
