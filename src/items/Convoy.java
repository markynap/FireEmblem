package items;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import characters.Player;

public class Convoy {

	/** The Items held by this convoy */
	public ArrayList<Item> convoyItems;
	/** File that holds our convoy item information */
	private File convoyFile = new File("res//designInfo//convoy");
	/** The save state we are loading the convoy from */
	private int loadLevel;
	/** Picture of the convoy tent */
	public Image image;
	
	private ArrayList<CombatItem> combatItems;
	
	private ArrayList<UtilityItem> utilityItems;

	/** A merchant who holds excess goods for our players */
	public Convoy(int loadLevel) {
		convoyItems = new ArrayList<>();
		this.loadLevel = loadLevel;
		combatItems = new ArrayList<>();
		utilityItems = new ArrayList<>();
		resupplyConvoy();
	}
	/** Adds an item to our convoy */
	public void addItem(Item item) {
		if (item != null) {
			convoyItems.add(item);
			if (item.category.equalsIgnoreCase("Utility")) {
				utilityItems.add((UtilityItem)item);
			} else {
				combatItems.add((CombatItem)item);
			}
		}
	}
	
	public void giveItem(Player receiver, Item item) {
		if (convoyItems.contains(item)) {
			if (item.isUtilityItem()) {
				if (receiver.wallet.utilities.size() >= Wallet.MAX_SIZE) return;
				receiver.addItem(item);
				convoyItems.remove(item);
				utilityItems.remove(item);
			} else {
				if (receiver.wallet.weapons.size() >= Wallet.MAX_SIZE) return;
				receiver.addItem(item);
				convoyItems.remove(item);
				combatItems.remove(item);
			}
		}
	}
	
	public ArrayList<CombatItem> getCombatItems() {
		return combatItems;
	}
	
	public ArrayList<UtilityItem> getUtilityItems() {
		return utilityItems;
	}
	/** Deletes all the items in the convoy at the specified load level */
	public void resetConvoy() {
		ArrayList<String> oldData = new ArrayList<>();
		if (loadLevel == 0) loadLevel = 1;
		try {
			Scanner reader = new Scanner(convoyFile);
			String line;
			String[] lineParts;
			while (reader.hasNextLine()) {
				
				line = reader.nextLine();
				lineParts = line.split(":");
				if (lineParts[0].equalsIgnoreCase("LOAD_LEVEL")) {
					if (Integer.valueOf(lineParts[1]) == loadLevel) {
						oldData.add(line);
						oldData.add("101:5");
						while (reader.hasNextLine()) {
							line = reader.nextLine();
							if (line == null) break;
							if (line.isEmpty()) break;
							lineParts = line.split(":");
							if (lineParts[0].equalsIgnoreCase("LOAD_LEVEL")) {
								oldData.add(line);
								break;
							}
						}
					} else oldData.add(line);
				} else oldData.add(line);
				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		try {
			PrintWriter writer = new PrintWriter(convoyFile);
			for (int i = 0; i < oldData.size(); i++) {
				writer.println(oldData.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** Saves the current supply of our convoy to /designInfo/convoy file */
	public void saveConvoySupply() {
		ArrayList<String> oldData = new ArrayList<>();
		
		try {
			Scanner reader = new Scanner(convoyFile);
			String line;
			String[] lineParts;
			while (reader.hasNextLine()) {
				
				line = reader.nextLine();
				lineParts = line.split(":");
				if (lineParts[0].equalsIgnoreCase("LOAD_LEVEL")) {
					if (Integer.valueOf(lineParts[1]) == loadLevel) {
						oldData.add(line);
						for (int i = 0; i < convoyItems.size(); i++) {
							String newLine = convoyItems.get(i).getWeaponID() + ":" + convoyItems.get(i).duration;
							oldData.add(newLine);
						}
						while (reader.hasNextLine()) {
							line = reader.nextLine();
							lineParts = line.split(":");
							if (lineParts[0].equalsIgnoreCase("LOAD_LEVEL")) {
								oldData.add(line);
								break;
							}
						}
					} else oldData.add(line);
				} else oldData.add(line);
				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		try {
			PrintWriter writer = new PrintWriter(convoyFile);
			for (int i = 0; i < oldData.size(); i++) {
				writer.println(oldData.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** Stocks the convoy full of the items as referenced by /designInfo/convoy file */
	public void resupplyConvoy() {
		
		convoyItems.clear();
		utilityItems.clear();
		combatItems.clear();
		Item nextItem = null;
		try {
			Scanner reader = new Scanner(convoyFile);
			String line;
			String[] lineParts;
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				lineParts = line.split(":");
				if (lineParts[0].equalsIgnoreCase("LOAD_LEVEL")) {
					if (Integer.valueOf(lineParts[1]) == loadLevel) {
						while (reader.hasNextLine()) {
							line = reader.nextLine();
							lineParts = line.split(":");
							if (line == null || line.isEmpty()) break;
							if (lineParts[0].equalsIgnoreCase("LOAD_LEVEL")) break;
							
							nextItem = Item.getItemByID(Integer.valueOf(lineParts[0]));
							if (lineParts.length > 1) {
								nextItem.duration = Integer.valueOf(lineParts[1]);
							}
							addItem(nextItem);
						}
						break;
					}
				}	
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setLoadLevel(int loadLevel) {
		this.loadLevel = loadLevel;
	}
}
