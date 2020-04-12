package items;

import java.awt.Image;

import extras.*;
import gameMain.*;

public abstract class Item {
	
	public final static String[] weaponTypes = {"Swords", "Lances", "Axes", "Bows"};
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
	
	public int uniqueID;
	
	public Image getImage(String path) {
		return IM.getImage(path);
	}
	public Image getImage() {
		return IM.getImage(imagePath);
	}
	public abstract String getDamageName();
}
