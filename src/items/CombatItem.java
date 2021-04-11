
package items;

import java.awt.Image;

import gameMain.Game;

public class CombatItem extends Item {
	/**The number of tiles this item can reach in a given direction */
	public int range;
	/**The chance this item has to use 3x it's effectiveness*/
	public int crit;
	/**This item's chance of hitting*/
	public int hit;
	/** Sword, Axe, Lance, Bow, Fire, Ice, Earth, Dark */
	public String weaponType;
	
	public final Image fireImage = Game.IM.getImage("/itemPics/firemagic.png");
	public final Image iceImage = Game.IM.getImage("/itemPics/icemagic.png");
	public final Image earthImage = Game.IM.getImage("/itemPics/earthmagic.png");
	public final Image darkImage = Game.IM.getImage("/itemPics/darkmagic.png");
	public final Image swordImage = Game.IM.getImage("/itemPics/ironsword.png");
	public final Image axeImage = Game.IM.getImage("/itemPics/steelAxe.png");
	public final Image bowImage = Game.IM.getImage("/itemPics/bow.png");
	public final Image lanceImage = Game.IM.getImage("/itemPics/godlance.png");
	public final Image staffImage = Game.IM.getImage("/itemPics/staff.png");

	
	public CombatItem(String name, String weaponType, int damage, int hit, int weight, int duration, int range, int crit) {
	//	this.category = "Utility"; must specify category further down in inheritance tree
		this.name = name;
		this.damage = damage;
		this.hit = hit;
		this.weight = weight;
		this.duration = duration;
		this.range = range;
		this.crit = crit;
		this.weaponType = weaponType;
	}
	
	public String toString() {
		return name + ": " + damage + ", " + weight + ", " + duration + ", " + range + ", " + crit;
	}
	
	public String getDamageName() {
		return "instanciated type Combat Item, no name detected";
	}
	
	public Image weaponTypeImage() {
		switch (weaponType) {
		case "Fire": return fireImage;
		case "Ice": return iceImage;
		case "Earth": return earthImage;
		case "Dark": return darkImage;
		case "Sword": return swordImage;
		case "Lance": return lanceImage;
		case "Axe": return axeImage;
		case "Bow": return bowImage;
		case "Staff": return staffImage;
		default: return null;
		}
	}
}
