package items;

import characters.Player;

public class UtilityItem extends Item{
	
	public Player carrier;
	public String description;
	
	public UtilityItem(String name, String description, int effectiveness, int weight, int duration) {
		this.category = "Utility";
		this.description = description;
		this.name = name;
		this.damage = effectiveness;
		this.weight = weight;
		this.duration = duration;
	}
	public String getDamageName() {
		return "EFFECT";
	}
	
	public void use() {
		System.out.println("must specify the Use method for item: " + name);
	}
}
