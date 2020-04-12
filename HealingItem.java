package items;

public class HealingItem extends CombatItem{

	public HealingItem(String name, int damage, int weight, int duration, int range, int crit) {
		super(name, damage, 100, weight, duration, range, crit);
		this.category = "Healing";
	}
	public String getDamageName() {
		return "HEAL";
	}
}
