package items;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import extras.*;
import gameMain.*;
import items.UtilityItems.*;

public abstract class Item {
	/** Sword, Lances, Axes, Bows */
	public final static String[] weaponTypes = {"Swords", "Lances", "Axes", "Bows"};
	/** Fire, Ice, Earth, Dark */
	public final static String[] magWeaponTypes = {"Fire", "Ice", "Earth", "Dark"};
	/**What this item is called*/
	public String name;
	/**Physical, Magical, Healing, Utility*/
	public String category;
	/**stat used for effectiveness -- either dmg or healing amount or utility amount, 0 if no amount*/
	public int damage;
	/**all items have weight, players CON effects how many heavy items they can carry*/
	public int weight;
	/**how many times the item can be used before it is gone, negative for infinite times*/
	public int duration;
	/**The image manager that will handle rendering item images*/
	public ImageManager IM = Game.IM;
	/** The image path to the item */
	public String imagePath;	
		
	public Image getImage(String path) {
		return IM.getImage(path);
	}
	public Image getImage() {
		return IM.getImage(imagePath);
	}
	
	public abstract String getDamageName();
	
	public int getWeaponID() {
		try {
			Scanner reader = new Scanner(new File("res//designInfo//weaponIDs"));
			String[] line;
			while (reader.hasNextLine()) {
				line = reader.nextLine().split(":");
				if (line[0].equalsIgnoreCase(name)) {
					reader.close();
					return Integer.valueOf(line[1]);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("WEAPON ID NOT SPECIFIED FOR: " + name);
	}
	
	public static Item getItemByID(int ID) {
		try {
			Scanner reader = new Scanner(new File("res//designInfo//weaponIDs"));
			String[] line;
			while (reader.hasNextLine()) {
				line = reader.nextLine().split(":");
				if (Integer.valueOf(line[1]) == ID) {
					reader.close();
					return getItemByName(line[0]);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	/** Returns an instance of an Item by the corresponding name*/
	public static Item getItemByName(String name) {

		switch (name) {
		case "Fists of Fury  ": return new Fists();
		case "Durandal": return new Durandal();
		
		case "Bronze Axe": return new BronzeAxe();
		case "Iron Axe": return new IronAxe();
		case "Steel Axe": return new SteelAxe();
		case "Hand Axe": return new HandAxe();
		
		case "Bronze Sword": return new BronzeSword();
		case "Iron Sword": return new IronSword();
		case "Steel Sword": return new SteelSword();
		case "Bronze Dagger": return new BronzeDagger();
		
		case "Bronze Lance": return new BronzeLance();
		case "Iron Lance": return new IronLance();
		case "Steel Lance": return new SteelLance();
		case "Javelin": return new Javelin();
		
		case "Bronze Bow": return new BronzeBow();
		case "Iron Bow": return new IronBow();
		case "Steel Bow": return new SteelBow();
		case "Long Bow": return new LongBow();
		
		case "FireTome": return new FireTome();
		case "Lightning": return new LightningTome();
		case "IceTome": return new IceTome();
		case "Blizzard": return new BlizzardTome();
		case "Earth Quake": return new EarthQuakeTome();
		case "Tornado": return new TornadoTome();
		case "Flux": return new FluxTome();
		case "Nosferatu": return new Nosferatu();
		
		case "Staff": return new Staff();
		case "MendStaff": return new MendStaff();
		case "PhysicStaff": return new PhysicStaff();
		
		case "Vulnery": return new Vulnery();
		case "Key": return new Key();
		case "Pure Water": return new PureWater();
		case "Pure Honey": return new PureHoney();
		case "Pure Milk": return new PureMilk();
		case "Angelic Robe": return new AngelicRobe();
		case "Elixir": return new Elixir();
		case "Draco Shield": return new DracoShield();
		case "Energy Ring": return new EnergyRing();
		
		case "ArmorSlayer": return new ArmorSlayer();
		case "Killing Edge": return new KillingEdge();
		
		case "Gold": return new Gold(5);
		
		case "Silver Sword": return new SilverSword();
		case "Silver Lance": return new SilverLance();
		case "Silver Axe": return new SilverAxe();
		case "Silver Bow": return new SilverBow();
		case "Silver Dagger": return new SilverDagger();
		
		case "22 Caliber": return new GUN_22();
		case "45 Caliber": return new GUN_45();
		case "Sniper Rifle": return new GUN_Sniper();
		
		
		case "none": return null;
		
		default:
			System.out.println("ITEM NAME NOT SPECIFIED! NAME: " + name);
			return null;
		}
	}
	
	/** Returns a randomly selected item */
	public static Item getRandomItem() {
		Random r = new Random();
		int rng = r.nextInt(47);
		switch (rng) {
		case 0: return new BronzeAxe();
		case 1: return new IronAxe();
		case 2: return new SteelAxe();
		
		case 3: return new BronzeSword();
		case 4: return new IronSword();
		case 5: return new SteelSword();
		
		case 6: return new BronzeLance();
		case 7: return new IronLance();
		case 8: return new SteelLance();
		case 9: return new Javelin();
		
		case 10: return new BronzeBow();
		case 11: return new IronBow();
		case 12: return new SteelBow();
		
		case 13: return new FireTome();
		case 14: return new LightningTome();
		case 15: return new IceTome();
		
		case 16: return new Staff();
		case 17: return new Vulnery();
		case 18: return new Key();
		
		case 19: return new HandAxe();
		case 20: return new EarthQuakeTome();
		case 21: return new FluxTome();
		case 22: return new LongBow();
		case 23: return new Nosferatu();
		case 24: return new PhysicStaff();
		
		case 25: return new BronzeDagger();
		case 26: return new MendStaff();
		case 27: return new PureWater();
		case 28: return new PureHoney();
		case 29: return new PureMilk();
		case 30: return new AngelicRobe();
		case 31: return new Elixir();
		
		case 32: return new ArmorSlayer();
		case 33: return new KillingEdge();
		case 34: return new DracoShield();
		
		case 35: return new TornadoTome();
		case 36: return new BlizzardTome();
		
		case 37: return new Gold((r.nextInt(15)+1));
		
		case 38: return new SilverSword();
		case 39: return new SilverLance();
		case 40: return new SilverAxe();
		case 41: return new SilverBow();
		case 42: return new SilverDagger();
		
		case 43: return new GUN_22();
		case 44: return new GUN_45();
		case 45: return new GUN_Sniper();
		
		case 46: return new EnergyRing();
		
		default: 
			System.out.println("Random Item method in Item superclass:\n\t defaulted from: " + rng);
			return new Elixir();
		}
	}
	
	/** Returns a randomly selected Item that is of medium quality or better */
	public static Item getRandomChestItem() {
		Random r = new Random();
		int rng = r.nextInt(27);
		switch (rng) {
		
		case 0: return new HandAxe();
		case 1: return new Javelin();
		case 2: return new LongBow();
		
		case 3: return new ArmorSlayer();
		case 4: return new KillingEdge();
		
		case 5: return new LightningTome();
		case 6: return new TornadoTome();
		case 7: return new BlizzardTome();
		case 8: return new Nosferatu();
		
		case 9: return new PhysicStaff();
		case 10: return new MendStaff();
		case 11: return new PureWater();
		case 12: return new PureHoney();
		case 13: return new PureMilk();
		case 14: return new AngelicRobe();
		case 15: return new Elixir();
		case 16: return new DracoShield();
		
		case 17: return new Gold((r.nextInt(50)+1));
		
		case 18: return new SilverSword();
		case 19: return new SilverLance();
		case 20: return new SilverAxe();
		case 21: return new SilverBow();
		case 22: return new SilverDagger();
		
		case 23: return new GUN_22();
		case 24: return new GUN_45();
		case 25: return new GUN_Sniper();
		
		case 26: return new EnergyRing();
		
		default: 
			System.out.println("Random Chest Item method in Item superclass:\n\t defaulted from: " + rng);
			return new Elixir();
		}
	}
	
	public boolean isMagicItem() {
		return category.equalsIgnoreCase("Magical");
	}
	
	public boolean isPhysicalItem() {
		return category.equalsIgnoreCase("Physical");
	}
	
	public boolean isHealingItem() {
		return category.equalsIgnoreCase("Healing");
	}
	
	public boolean isUtilityItem() {
		return category.equalsIgnoreCase("Utility");
	}
	
}
