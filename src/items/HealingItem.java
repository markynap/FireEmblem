package items;

public class HealingItem extends CombatItem{
	
	/** Amount of experience gained for using this staff */
	private int healExperience;
	
	public HealingItem(String name, int damage, int weight, int duration, int range, int crit, int healEXP) {
		super(name, "Staff", damage, 100, weight, duration, range, crit);
		this.category = "Healing";
		this.healExperience = healEXP;
	}
	public String getDamageName() {
		return "HEAL";
	}
	
	public int getHealExperience() {
		return healExperience;
	}
}
