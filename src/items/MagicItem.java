package items;

public class MagicItem extends CombatItem{

	public MagicItem(String name, String type, int damage, int hit, int weight, int duration, int range, int crit) {
		super(name, type, damage, hit, weight, duration, range, crit);
		this.category = "Magical";
	}
	
	public String getDamageName() {
		return "MAG";
	}

}
