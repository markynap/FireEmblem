package items;

public class Fists extends CombatItem{
	
	public Fists() {
		super("Fists of Fury  ", 1, 100, 0, 9999, 1, 2);
		this.imagePath = "/fists.png";
		this.category = "Physical";
	}
	
	public String getDamageName() {
		return "DMG";
	}
	
}
