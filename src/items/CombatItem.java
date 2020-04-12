package items;

public class CombatItem extends Item {
	/**The number of tiles this item can reach in a given direction */
	public int range;
	/**The chance this item has to use 3x it's effectiveness*/
	public int crit;
	/**This item's chance of hitting*/
	public int hit;
	
	public CombatItem(String name, int damage, int hit, int weight, int duration, int range, int crit) {
	//	this.category = "Utility"; must specify category further down in inheritance tree
		this.name = name;
		this.damage = damage;
		this.hit = hit;
		this.weight = weight;
		this.duration = duration;
		this.range = range;
		this.crit = crit;
	}
	
	public String toString() {
		return name + ": " + damage + ", " + weight + ", " + duration + ", " + range + ", " + crit;
	}
	
	public String getDamageName() {
		return "instanciated type Combat Item, no name detected";
	}
}
