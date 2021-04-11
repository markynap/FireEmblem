package items;

import java.util.ArrayList;

import characters.*;

public class Wallet {

	public ArrayList<CombatItem> weapons;
	/** Maximum Size for Weapons and Utilities */
	public final static int MAX_SIZE = 4;
	
	public ArrayList<UtilityItem> utilities;
	/**The player who owns this wallet */
	public Player holder;
	
	public Wallet(Player holder) {
		this.holder = holder;
		weapons = new ArrayList<>();
		utilities = new ArrayList<>();
	}
	/** True if there is an item with this name in either our weapons or utility wallets */
	public boolean containsItemByName(String name) {
		for (int i = 0; i < utilities.size(); i++) {
			if (utilities.get(i).name.equalsIgnoreCase(name)) return true;
		}
		for (int i = 0; i < weapons.size(); i++) {
			if (weapons.get(i).name.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	/** True if we have some form of healing item in our wallet */
	public boolean hasHealingItem() {
		for (int i = 0; i < weapons.size(); i++) {
			if (weapons.get(i).isHealingItem()) return true;
		}
		return false;
	}
	
	public void equipHealingItem() {
		if (!hasHealingItem()) return;
		for (int i = 0; i < weapons.size(); i++) {
			Item item = weapons.get(i);
			if (item.isHealingItem()) {
				equipt(item);
				return;
			}
		}
	}
	
	public void equipDamageItem() {
		for (int i = 0; i < weapons.size(); i++) {
			Item item = weapons.get(i);
			if (item.isPhysicalItem() || item.isMagicItem()) {
				equipt(item);
				return;
			}
		}
	}
	
	/** True if this wallet contains a copy of this item */
	public boolean contains(Item item) {
		if (item.category.equalsIgnoreCase("Utility")) {
			for (int i = 0; i < utilities.size(); i++) {
				if (utilities.get(i).name.equalsIgnoreCase(item.name)) {
					if (utilities.get(i).duration == item.duration) return true;
				}
			}
		} else {
			for (int i = 0; i < weapons.size(); i++) {
				if (weapons.get(i).name.equalsIgnoreCase(item.name)) {
					if (weapons.get(i).duration == item.duration) return true;
				}
			}
		}
		return false;
	}
	
	public void addItem(Item item) {
		if (item.category.equalsIgnoreCase("Utility")) {
			UtilityItem it = (UtilityItem)item;
			it.carrier = holder;
			utilities.add(it);
		} else {
			weapons.add((CombatItem) item);
			removeFists();
		}
	}
	/** Removes Fists() from the wallet if they have it */
	public void removeFists() {
		for (int i = 0; i < weapons.size(); i++) {
			Item it = weapons.get(i);
			if (it.duration > 9000) {
				weapons.remove(it);
				return;
			}
		}
	}
	/** Returns the index of the item in our wallet, -1 if it does not contain it
	 * 
	 * @param item
	 * @return if in weapons returns indexOf(item), else returns weapons.size() -1 + utilities.indexOf(item)
	 */
	public int indexOf(Item item) {
		if (!contains(item)) return -1;
		if (weapons.isEmpty()) return (utilities.indexOf(item));
		
		if (item.isUtilityItem()) {
			return (weapons.size()-1 + utilities.indexOf(item));
		} else {
			return weapons.indexOf(item);
		}
		
	}
	
	/** Sets the item in our wallet to the specified index
	 * 
	 * @param item - item contained in our wallet, returns if we do not have it
	 * @param index -  if (index < weapons.size()) returns weapons.get(i), else utilities.get(i -weapons.size())
	 */
	public void setItemLocation(Item item, int index) {
		if (!contains(item)) return;
		if (index >= 8) return;
		if (utilities.isEmpty()) return;
		if (item.isUtilityItem()) {
			// we are in the utilities
			int uIndex = index - weapons.size();
			if (uIndex >= utilities.size()) {
				uIndex = utilities.size() - 1;
			}
			if (utilities.size() == 1) return;
			int oldIndex = utilities.indexOf(item);
			UtilityItem oldFirst = utilities.get(0);
			System.out.println("Size: " + utilities.size() + "  index: " + uIndex );
			utilities.set(uIndex, (UtilityItem)item);
			utilities.set(oldIndex, oldFirst);
			
		} else {
			// we are in the weapons
			if (index >= weapons.size()) {
				index = weapons.size() - 1;
			}
			if (weapons.size() == 1) return;
			int oldIndex = weapons.indexOf(item);
			CombatItem oldFirst = weapons.get(0);
			weapons.set(index, (CombatItem)item);
			weapons.set(oldIndex, oldFirst);
			
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
		if (item == null) return;
		if (!containsItemByName(item.name)) return;
		if (item.category.equalsIgnoreCase("Utility")) {
			if (utilities.size() == 1) return;
			if (!utilities.contains(item)) return;
			int oldIndex = utilities.indexOf(item);
			UtilityItem oldFirst = utilities.get(0);
			utilities.set(0, (UtilityItem) item);
			utilities.set(oldIndex, oldFirst);
		} else {
			if (weapons.size() == 1) return;
			int oldIndex = weapons.indexOf(item);
			CombatItem oldFirst = weapons.get(0);
			weapons.set(0, (CombatItem) item);
			weapons.set(oldIndex, oldFirst);
			holder.equiptItem = (CombatItem)item;
		}
	}
	
	
	public CombatItem getFirstWeapon() {
		if (weapons.size() == 0) return null;
		else return weapons.get(0);
	}
	/** The size of both combat and utility pouches */
	public int size() {
		return weapons.size() + utilities.size();
	}
	/** Clears this entire wallet of items */
	public void clear() {
		weapons.clear();
		utilities.clear();
	}
	/** Returns the item in index of weapons, or if it rolls over the index of utilities */
	public Item getFinalIndex(int index) {
		if (index >= size()) return null;
		if (index >= weapons.size()) {
			return utilities.get(index - weapons.size());
		} else {
			return weapons.get(index);
		}
	}
}
