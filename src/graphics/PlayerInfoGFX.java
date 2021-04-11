package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import characters.*;
import gameMain.*;
import items.*;

public class PlayerInfoGFX {
	public final int rectSize = 15;
	public final int EQrectX = 19*Game.scale/2;
	public final int USErectX = Game.scale * 10;
	public Game game;
	public Player carrier;
	public Wallet wallet;
	public int[] sizes;
	public int weaponIndex;
	public Item currentItem;
	/**is true if a player pressed a on an item and can equip, use, or remove it */
	public boolean inItemOptions;
	public int itemOptionIndex;
	
	private ArrayList<CombatItem> damageItems;
	private ArrayList<CombatItem> healingItems;
	/** True if we are in a healing weapon selection */
	public boolean isHealing;
	
	public String[] itemOptions;
	
	private int walletHeight = Game.HEIGHT*2/5 + 25;
	
	private int walletTop = Game.HEIGHT - walletHeight;
	
	/** Used in rendering weapon selection before combat */
	private int[] playerbox = {Game.WIDTH/2 + 2*Game.scale, Game.HEIGHT/3, (int)(Game.WIDTH/3.0), Game.HEIGHT/4};
	/** Used in rendering weapon selection before combat */
	private int[] weaponsBox = { Game.WIDTH/10 + Game.scale/2, Game.HEIGHT/3, (int)(Game.WIDTH/4.0) + Game.scale/2, (int)(Game.HEIGHT/7)};
	/** Index responsible for selecting a weapon to attack with */
	public int weaponChooseIndex;
	/** Range of the items we can show to be selected by the user */
	private int weaponSelectionRange;
	
	

	public PlayerInfoGFX(Game game) {
		this.game = game;
		sizes = new int[2];
		healingItems = new ArrayList<>();
		damageItems = new ArrayList<>();
		itemOptions = new String[2];
		itemOptions[1] = "DROP";
	}
	/** Sets a player to be viewed in Info State as well as Weapon Selection State */
	public void setPlayer(Player player) {
		if (player != null) {
			this.carrier = player;
			this.wallet = carrier.wallet;
			this.healingItems.clear();
			this.damageItems.clear();
			sizes[0] = wallet.weapons.size();
			sizes[1] = wallet.utilities.size();
			currentItem = wallet.getFirstWeapon();
			
			weaponChooseIndex = 0;
			weaponIndex = 0;
			itemOptionIndex = 0;
			for (int i = 0; i < wallet.weapons.size(); i++) {
				CombatItem item = wallet.weapons.get(i);
				if (item.category.equalsIgnoreCase("Healing")) {
					healingItems.add(item);
				} else {
					
					if (item.category.equalsIgnoreCase("Magical")) {
						if (player.isMagicUser) {
							if (item.range >= this.weaponSelectionRange) damageItems.add(item);
						}
					} else {
						if (!player.isMagicUser) {
							if (item.range >= this.weaponSelectionRange) damageItems.add(item);
						}
					}
				}
			}
			this.weaponSelectionRange = 0;
		}
	}
	/** Sets player for weapon selection state with the minimum range they are away from an enemy */
	public void setPlayer(Player player, int range) {
		if (range > player.maxWeaponRange()) {
			this.weaponSelectionRange = player.maxWeaponRange();
		} else {
			this.weaponSelectionRange = range;	
		}
		setPlayer(player);
	}
	
	public void setIsHealing(boolean isHealing) {
		this.isHealing = isHealing;
		if (isHealing) {
			weaponsBox[3] = (int)(Game.HEIGHT/12) * healingItems.size();
		} else {
			weaponsBox[3] = (int)(Game.HEIGHT/12) * damageItems.size();
		}
	}
	/** Draws the player, their stats, and the contents of their wallet */
	public void render(Graphics g) {
		
		int HACx = 10; //hit avoid crit x location
		int statsX = 9*Game.scale/4;
		int expX = 4*Game.scale + Game.scale/3;
		int statSpacing = 23;
		int statStart = walletTop + 45;
		
		int itemBoxX = 7*Game.scale;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, walletTop , Game.WIDTH, walletHeight);
		Image playerImage = carrier.getImage();
		g.drawImage(playerImage, 0, walletTop + Game.scale/2, Game.scale*2, Game.scale*3, null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		g.drawString("Hit: " + carrier.getHit(), HACx, walletTop+Game.scale*4+(5*Game.scale/12) - 20);
		g.drawString("Avoid: " + carrier.getAvoid(), HACx, walletTop+Game.scale*4+(5*Game.scale/12)-2);
		g.drawString("Crit: " + carrier.getCrit(), HACx, walletTop+Game.scale*4+(5*Game.scale/12)+16);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));
		g.drawString("Name: " + carrier.name, statsX, walletTop+20);
		if (carrier.statCapped(0)) g.setColor(Color.green);
		else g.setColor(Color.white);
		g.drawString("HP: " + carrier.currentHP + "/" + carrier.HP, statsX, statStart);
		g.setColor(Color.white);
		g.drawString("EXP: " + carrier.EXP, expX, walletTop+50);
		
		// player class data
		g.setFont(new Font("Times New Roman", Font.BOLD, 19));
		g.drawString("Class:", expX, walletTop + 70);
		g.setFont(new Font("Times New Roman", Font.BOLD, 23 - (carrier.Class.length()+2)/2));
		g.drawString(carrier.Class, expX + 55, walletTop+70);

		// player stat data
		g.setFont(new Font("Times New Roman", Font.BOLD, 19));
		for (int i = 1; i < carrier.stats.length; i++) {
			if (carrier.statCapped(i)) g.setColor(Color.green);
			else g.setColor(Color.white);
			if (!carrier.isMagicUser) {
				g.drawString(Player.StatNames[i] + ": " + carrier.stats[i] + statBuffString(i), statsX, statStart + (statSpacing * i));
			} else {
				g.drawString(Player.MagStatNames[i] + ": " + carrier.stats[i] + statBuffString(i), statsX, statStart + (statSpacing * i));
			}
		}
		
		// draw Player's Skill Details
		g.setColor(Color.white);
		g.drawString(carrier.skill.getName(), expX - 22 + 300/carrier.skill.getName().length(), walletTop + 160);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 21));
		
		g.drawString(" ---SKILL--- ", expX, walletTop + 134);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 14));
		if (carrier.skill.getDescription().length() >= 25) {
			String s = carrier.skill.getDescription();
			g.drawString(s.substring(0,25), expX - 10, walletTop + 180);
			if (s.length() > 25) g.drawString(s.substring(25), expX - 10, walletTop + 200);
		} else {
			g.drawString(carrier.skill.getDescription(), expX - 10, walletTop + 180);
		}
		
		// draw Player's race details
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		if (carrier.race != null) {
			if (carrier.race.length() > 10) {
				g.setFont(new Font("Times New Roman", Font.BOLD, 18));
			}
			g.drawString("Race: " + carrier.race, expX, walletTop + 90);
		}
		
		// draws the contents of wallet
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));		
		g.drawRect(itemBoxX,  walletTop +10, Game.WIDTH/2, walletHeight-40);
		g.drawString("Items", itemBoxX + Game.WIDTH/5, Game.HEIGHT-walletHeight+25);
		g.setFont(new Font("Times New Roman", Font.BOLD, 12));
		g.drawString("Name:                                    DMG      HIT      CRIT     WGT     RNG      DUR", itemBoxX + 10, walletTop +50);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		drawEquipped(g);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		for (int i = 0; i < wallet.weapons.size(); i++) {
			CombatItem it = wallet.weapons.get(i);
			g.drawString(it.name, itemBoxX + 10, walletTop + 70 + 20*i);
			g.drawString("" + it.damage, itemBoxX + 160, walletTop + 70 + 20*i);
			g.drawString("" + it.hit, itemBoxX + 200, walletTop + 70 + 20*i);
			g.drawString("" + it.crit, itemBoxX + 248, walletTop + 70 + 20*i);
			g.drawString("" + it.weight, itemBoxX + 290, walletTop + 70 + 20*i);
			g.drawString("" + it.range, itemBoxX + 335, walletTop + 70 + 20*i);
			g.drawString("" + it.duration, itemBoxX + 375, walletTop + 70 + 20*i);
			g.drawImage(it.weaponTypeImage(), itemBoxX + 400, walletTop + 50 + 20*i, 25, 25, null);
			
		}
		for (int j = 0; j < wallet.utilities.size(); j++) {
			UtilityItem it = wallet.utilities.get(j);
			g.setColor(Color.white);
			g.drawString(it.name + "  (" + it.duration + ")" + "        " + it.description, itemBoxX + 10, walletTop +75+(20*wallet.weapons.size())+ 20*j);
			g.drawImage(it.getImage(), itemBoxX + 400,  walletTop +75+(20*wallet.weapons.size())+ 20*j - 20, 25, 25, null);
		}
		
		if (inItemOptions) drawOptions(g);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 15));
		g.drawString("Press Q for Advanced Stats", itemBoxX + Game.WIDTH/6, Game.HEIGHT - Game.scale + 5);
		
	}
	/** The value that our stat buffs are affecting our stats */
	private String statBuffString(int index) {
		int[] statBuffs = carrier.getStatBuffs();
		if (index == 1) {
			if (statBuffs[0] != 0) {
				return " +" + statBuffs[0];
			}
		} else if (index == 5) {
			if (statBuffs[1] != 0) {
				return " +" + statBuffs[1];
			}
		} else if (index == 6) {
			if (statBuffs[2] != 0) {
				return " +" + statBuffs[2];
			}
		} else if (index == 7) {
			if (statBuffs[3] != 0) {
				return " +" + statBuffs[3];
			}
		}
		return "";
	}
	
	public void drawOptions(Graphics g) {
		int itemBoxX = 11*Game.scale/2;
		int beginX = itemBoxX + 40 + Game.WIDTH/3;
		int beginY = walletTop + 40 + (20*weaponIndex);
		int height = 25;
		int width = 65;
		int thickness = 3;
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		if (currentItem.category.equalsIgnoreCase("Utility")) {
			itemOptions[0] = "USE";
		} else {
			itemOptions[0] = "EQUIP";
		}
		
		for (int i = 0; i < 2; i++) {
		g.setColor(Color.black);
		g.drawRect(beginX, beginY + (i * height), width, height);
		g.setColor(Color.white);
		g.fillRect(beginX+1, beginY + (i * height) + 1, width - 2, height - 2);
		g.setColor(Color.RED);		
		g.drawString(itemOptions[i], beginX + 2, beginY + (i * height) + height - 4);
		}
		g.setColor(Color.blue);
		for (int i = 0; i < thickness; i++)
		g.drawRect(beginX + i, beginY + (itemOptionIndex * height) + i, width - (2*i), height - (2*i));
	}
	
	public void drawEquipped(Graphics g) {

		int itemBoxX = 11*Game.scale/2;
		
		g.setColor(Color.blue);
		g.fillRect(itemBoxX + (Game.WIDTH/3 + 120)/4 - 10, walletTop + 50 + (20*weaponIndex) + 4, Game.WIDTH/3 + 120, 20);
	}
	
	public void renderAdv(Graphics g) {
		
		int walletWidth = Game.WIDTH;
		int weaponBoxWidth = 150;
		int statsX = 9*Game.scale/4;
		int classnLvY = walletTop + Game.scale*4;
		int weaponBoxX = 11*Game.scale/2;
		int masteryBoxX = 8*Game.scale + 10;
		int charX = 21*Game.scale/2 + 40;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, walletTop , walletWidth, walletHeight);
		Image playerImage = carrier.getImage();
		g.drawImage(playerImage, 0, walletTop, Game.scale*2, Game.scale*3, null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString(carrier.Class, 10, classnLvY);
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));
		g.drawString("Level: " + carrier.level, 10, classnLvY + 25);
		g.drawString("Name: " + carrier.name, Game.scale*2, walletTop+20);
		for (int i = 0; i < carrier.growths.length; i++) {
			if (carrier.equiptItem.getDamageName().equalsIgnoreCase("DMG")) {
				g.drawString(Player.StatNames[i] + ": " + carrier.growths[i] + "%", statsX, walletTop + 45 + (25 * i));
			} else {
				g.drawString(Player.MagStatNames[i] + ": " + carrier.growths[i] + "%", statsX, walletTop + 45 + (25 * i));
			}
		}
		
		g.drawRect(weaponBoxX, walletTop+5, walletWidth/2, walletHeight-40);
		g.drawString("Weapon Skill Tree", Game.scale*5 + walletWidth/4 - 50, Game.HEIGHT-walletHeight+24);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		int strTop = walletTop + 60;
		int boxTop = strTop - 16;
		int distBetween = 50;
		for (int i = 0; i < Item.weaponTypes.length; i++) {
			if (this.carrier.equiptItem.getDamageName().equalsIgnoreCase("DMG"))
				g.drawString(Item.weaponTypes[i], Game.scale*7, strTop + (distBetween*i));
			else 
				g.drawString(Item.magWeaponTypes[i], Game.scale*7, strTop + (distBetween*i));
			g.drawRect(masteryBoxX, boxTop +(distBetween*i), weaponBoxWidth, 25);
			g.drawString(String.valueOf(carrier.weaponMasteriesGrade[i]), charX, strTop + (distBetween * i));
		}
		
		g.setColor(Color.cyan);
		for (int i = 0; i < 4; i++) {
			g.fillRect(masteryBoxX, boxTop + (distBetween*i), 1 + weaponBoxWidth*(carrier.weaponMasteries[i]+1)/carrier.weaponUpgrade, 25);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 15));
		g.drawString("Press Q for Basic Stats", masteryBoxX, Game.HEIGHT - Game.scale + 5);
		
	}
	/** Shows the screen where attacker chooses their item before attacking */
	public void renderWeaponSelection(Graphics g) {
		
		int thickness = 4;
		g.drawImage(carrier.image, playerbox[0] + playerbox[2]/6, playerbox[1] - 150, 2*playerbox[2]/3, 300, null);
		g.setColor(new Color(169,169,169));
		for (int i = 0; i < thickness; i++) {
		g.drawRect(playerbox[0] + i, playerbox[1] + i, playerbox[2] - i/2, playerbox[3] - i/2);
		g.drawRect(weaponsBox[0] + i, weaponsBox[1] + i, weaponsBox[2] - i/2, weaponsBox[3] - i/2);
		}
		g.setColor(new Color(123, 108, 227));
		g.fillRect(playerbox[0] + thickness, playerbox[1] + thickness, playerbox[2] - thickness, playerbox[3] - thickness);
		g.fillRect(weaponsBox[0] + thickness, weaponsBox[1] + thickness, weaponsBox[2] - thickness, weaponsBox[3] - thickness);
		
		CombatItem playerItem = (CombatItem)currentItem;
		g.setColor(Color.white);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		int startX =  playerbox[0] + playerbox[2]/2 - Game.scale;
		int startY = playerbox[1] + 30;
		int spacing = 30;
		int nameShift = 60/carrier.name.length();
		g.drawString(carrier.name, startX + nameShift, startY);
		if (playerItem.isHealingItem()) {
			g.drawString("Weapon Type: Staff",  playerbox[0] + playerbox[2]/7 - 12, startY + spacing);
			g.drawString("Heal: " + (carrier.STR + playerItem.damage), startX, startY + spacing*2);
			g.drawString("Hit: 100", startX, startY + spacing*3);
			g.drawString("Range: " + playerItem.range, startX, startY + spacing*4);
		} else {
			g.drawString("Weapon Type: " + playerItem.weaponType,  playerbox[0] + playerbox[2]/7 - 12, startY + spacing);
			g.drawString("Attack: " + (carrier.getDamage() - carrier.equiptItem.damage + playerItem.damage), startX, startY + spacing*2);
			g.drawString("Hit: " + (carrier.getHit() - carrier.equiptItem.hit + playerItem.hit), startX, startY + spacing*3);
			g.drawString("Crit: " + (carrier.getCrit() - carrier.equiptItem.crit + playerItem.crit), startX, startY + spacing*4);
		}
		//where we draw the items
		int height;
		if (isHealing) {
			height = (int)(weaponsBox[3]/(double)healingItems.size()+1);
		}
		else {
			height = (int)(weaponsBox[3]/(double)damageItems.size()+1);
		}
		
		if (isHealing) {
			for (int i = 0; i < healingItems.size(); i++) {
				CombatItem it = healingItems.get(i);
				if (i == weaponChooseIndex) {
					g.setColor(Color.BLUE);
					g.fillRect(weaponsBox[0], weaponsBox[1] + i *height, 
							weaponsBox[2], height);
				}
				g.setColor(Color.white);
				g.drawString(it.name + " (" + it.duration + ")", weaponsBox[0] + 15, weaponsBox[1] + i*height + height/2);
			}
		} else {
			for (int i = 0; i < damageItems.size(); i++) {
				CombatItem it = damageItems.get(i);
				if (i == weaponChooseIndex) {
					g.setColor(Color.BLUE);
					g.fillRect(weaponsBox[0], weaponsBox[1] + i *height, 
							weaponsBox[2], height);
				}
				g.setColor(Color.white);
				g.drawString(it.name + " (" + it.duration + ")", weaponsBox[0] + 15, weaponsBox[1] + i*height + height/2 + 5);
				g.drawImage(it.weaponTypeImage(), weaponsBox[0] + weaponsBox[2] - 35, weaponsBox[1] + i*height + height/4, 30, 30, null);
			}
		}
		
	}
	/** Index responsible for selecting an attack weapon */
	public void incWeaponChooseIndex(int amount) {
		weaponChooseIndex += amount;
		int max = 0;
		if (this.isHealing) {
			max = this.healingItems.size();
		} else {
			max = this.damageItems.size();
		}
		
		if (weaponChooseIndex < 0) weaponChooseIndex = max-1;
		else if (weaponChooseIndex >= max) weaponChooseIndex = 0;
		
		if (isHealing) this.currentItem = healingItems.get(weaponChooseIndex);
		else this.currentItem = damageItems.get(weaponChooseIndex);
		
	}
	
	public void incWeaponIndex() {
		sizes[0] = wallet.weapons.size();
		sizes[1] = wallet.utilities.size();
		if (sizes[0] == 0 && sizes[1] == 0) return;
		weaponIndex++;
		if (weaponIndex >= (sizes[0] + sizes[1])) {
			weaponIndex = 0;
		}
		if (weaponIndex > sizes[0] - 1) {
			currentItem = wallet.utilities.get((weaponIndex-sizes[0]));
		} else {
			currentItem = wallet.weapons.get(weaponIndex);
		}
	}
	public void decWeaponIndex() {
		sizes[0] = wallet.weapons.size();
		sizes[1] = wallet.utilities.size();
		if (sizes[0] == 0 && sizes[1] == 0) return;
		weaponIndex--;
		if (weaponIndex < 0) {
			weaponIndex = (sizes[0] + sizes[1]) -1;
		}
		if (weaponIndex > sizes[0] - 1) {
			currentItem = wallet.utilities.get((weaponIndex-sizes[0]));
		} else {
			currentItem = wallet.weapons.get(weaponIndex);
		}
	}
	public void setItemOptions(boolean tf) {
		inItemOptions = tf;
	}
	public void incItemOptionIndex(int val) {
		itemOptionIndex += val;
		if (itemOptionIndex < 0) itemOptionIndex = 1;
		if (itemOptionIndex > 1) itemOptionIndex = 0;
	}
	/** Returns and equips the selected item our unit will use to attack */
	public CombatItem getSelectedItem() {
		CombatItem item;
		if (isHealing) {
			item = healingItems.get(weaponChooseIndex);
		} else {
			item = damageItems.get(weaponChooseIndex);
		}
		
		carrier.wallet.equipt(item);
		carrier.equiptItem = item;
		return item;
	}
	
}
