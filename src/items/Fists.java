package items;

public class Fists extends CombatItem{
	
	public Fists() {
		super("Fists of Fury  ", "notAType", 0, 100, 0, 9999, 1, 2);
		this.imagePath = "/itemPics/fists.png";
		this.category = "Physical";
	}
	
	public String getDamageName() {
		return "DMG";
	}
	
}
