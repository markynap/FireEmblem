package items;

import java.util.ArrayList;

import characters.*;

public class Wallet {

	public ArrayList<CombatItem> weapons;
	
	public ArrayList<UtilityItem> utilities;
	/**The player who owns this wallet */
	public Player holder;
	
	public Wallet(Player holder) {
		this.holder = holder;
		weapons = new ArrayList<>();
		utilities = new ArrayList<>();
	}
	
	public void addItem(Item item) {
		if (item.category.equalsIgnoreCase("Utility")) {
			UtilityItem it = (UtilityItem)item;
			it.carrier = holder;
			utilities.add(it);
		} else {
			weapons.add((CombatItem) item);
		}
	}
	public void removeItem(Item item) {
		if (item.category.equalsIgnoreCase("Utility")) {
			if (utilities.contains(item)) utilities.remove(item);
		} else {
			if (weapons.contains(item)) weapons.remove(item);
		}
	}
	public void equipt(Item item) {
		if (item.category.equalsIgnoreCase("Utility")) {
			if (utilities.size() == 1) return;
			if (!utilities.contains(item)) return;
			int oldIndex = utilities.indexOf(item);
			UtilityItem oldFirst = utilities.get(0);
			utilities.set(0, (UtilityItem) item);
			utilities.set(oldIndex, oldFirst);
		} else {
			if (weapons.size() == 1) return;
			if (!weapons.contains(item)) return;
			int oldIndex = weapons.indexOf(item);
			CombatItem oldFirst = weapons.get(0);
			weapons.set(0, (CombatItem) item);
			weapons.set(oldIndex, oldFirst);
		}
	}
	public CombatItem getFirstWeapon() {
		if (weapons.size() == 0) return null;
		else return weapons.get(0);
	}
}
