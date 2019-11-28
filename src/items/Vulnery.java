package items;

import characters.Player;

public class Vulnery extends UtilityItem {

	public Player carrier;
	
	public Vulnery(Player carrier) {
		super("Vulnery", "Restores 15 hp", 15, 0, 5);
		this.carrier = carrier;
	}
	@Override
	public void use() {
		System.out.println("used vulnery");
		carrier.currentHP += this.damage;
	}
}
