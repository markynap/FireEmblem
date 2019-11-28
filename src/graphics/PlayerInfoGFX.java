package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

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
	public final static String[] itemOptions = {"EQUIP", "USE", "DROP"};

	public PlayerInfoGFX(Game game) {
		this.game = game;
		sizes = new int[2];

	}
	
	public void setPlayer(Player player) {
		if (player != null) {
			this.carrier = player;
			this.wallet = carrier.wallet;
			sizes[0] = wallet.weapons.size();
			sizes[1] = wallet.utilities.size();
			currentItem = wallet.getFirstWeapon();
		}
	}
	
	public void render(Graphics g) {
		int walletHeight = Game.HEIGHT*3/10;
		int walletWidth = Game.WIDTH;
		
		int HACx = 10; //hit avoid crit x location
		int statsX = 9*Game.scale/4;
		int expX = 4*Game.scale;
		int walletTop = Game.HEIGHT - walletHeight;
		int statSpacing = 23;
		int statStart = walletTop + 45;
		
		int itemBoxX = 11*Game.scale/2;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, walletTop , walletWidth, walletHeight);
		Image playerImage = carrier.getImage();
		g.drawImage(playerImage, 0, walletTop, Game.scale*2, Game.scale*2, null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		g.drawString("Hit: " + carrier.hit, HACx, walletTop+Game.scale*2+(5*Game.scale/12)-18);
		g.drawString("Avoid: " + carrier.avoid, HACx, walletTop+Game.scale*2+(5*Game.scale/12));
		g.drawString("Crit: " + carrier.crit, HACx, walletTop+Game.scale*2+(5*Game.scale/12)+18);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));
		g.drawString("Name: " + carrier.name, statsX, walletTop+20);
		g.drawString("HP: " + carrier.currentHP + "/" + carrier.HP, statsX, statStart);
		g.drawString("EXP: " + carrier.EXP, expX, walletTop+30);
		for (int i = 1; i < carrier.stats.length; i++) {
			g.drawString(Player.StatNames[i] + ": " + carrier.stats[i], statsX, statStart + (statSpacing * i));
		}
		
	//	g.drawString(carrier.skill.skillString, Game.scale*4, Game.HEIGHT-walletHeight+130);
	//	g.setFont(new Font("Times New Roman", Font.ITALIC, 13));
	//	g.drawString(carrier.skill.description, Game.scale*3 + Game.scale/2, Game.HEIGHT-walletHeight+150);
		
		g.drawRect(itemBoxX,  walletTop +10, walletWidth/2, walletHeight-40);
		g.drawString("Items", Game.scale*5 + walletWidth/4, Game.HEIGHT-walletHeight+25);
		g.setFont(new Font("Times New Roman", Font.BOLD, 12));
		g.drawString("Name:                                    DMG      HIT      CRIT      WGT      RNG        DUR", itemBoxX + 10, walletTop +50);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		drawEquipped(g);
		if (inItemOptions) drawOptions(g);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		for (int i = 0; i < wallet.weapons.size(); i++) {
			CombatItem it = wallet.weapons.get(i);
			g.drawString(it.name + "      " + it.damage + ",      " + it.hit + ",      " + it.crit + ",      " + it.weight + ",      " + it.range + ",     " + it.duration, itemBoxX + 10, walletTop + 70 + (20*i));
			//g.drawImage(it., Game.scale*11+28, Game.HEIGHT-walletHeight+50 + (20*i), 20, 20, null);
		}

		for (int j = 0; j < wallet.utilities.size(); j++) {
			UtilityItem it = wallet.utilities.get(j);
			g.setColor(Color.white);
			g.drawString(it.name + "                " + it.description, itemBoxX + 10, walletTop +75+(20*wallet.weapons.size()));
		}
	}
	public void drawOptions(Graphics g) {
		int walletHeight = Game.HEIGHT*3/10;
		int walletTop = Game.HEIGHT - walletHeight;
		int itemBoxX = 11*Game.scale/2;
		int beginX = itemBoxX + 40 + Game.WIDTH/3;
		int beginY = walletTop + 30 + (20*weaponIndex);
		int height = 20;
		int width = 65;
		int thickness = 3;
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		for (int i = 0; i < 3; i++) {
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
		int walletHeight = Game.HEIGHT*3/10;
		int walletTop = Game.HEIGHT - walletHeight;
		int itemBoxX = 11*Game.scale/2;
		int width = Game.WIDTH/3 + 35;
		int height = 20;
		
		g.setColor(Color.blue);
		g.fillRect(itemBoxX + 5, walletTop + 50 + (20*weaponIndex) + 4, width, height);
	}
	
	public void renderAdv(Graphics g) {
		
		int walletHeight = Game.HEIGHT*3/10;
		int walletWidth = Game.WIDTH;
		int weaponBoxWidth = 150;
		int walletTop = Game.HEIGHT - walletHeight;
		int statsX = 9*Game.scale/4;
		int classnLvY = walletTop + Game.scale*2 + 25;
		int weaponBoxX = 11*Game.scale/2;
		int masteryBoxX = 15*Game.scale/2 + 10;
		int charX = Game.scale * 9 + 20;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, walletTop , walletWidth, walletHeight);
		Image playerImage = carrier.getImage();
		g.drawImage(playerImage, 0, walletTop, Game.scale*2, Game.scale*2, null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString(carrier.Class, 10, classnLvY);
		g.setFont(new Font("Times New Roman", Font.BOLD, 21));
		g.drawString("Level: " + carrier.level, 10, classnLvY + 25);
		g.drawString("Name: " + carrier.name, Game.scale*2, walletTop+20);
		for (int i = 0; i < carrier.growths.length; i++) {
			g.drawString(Player.StatNames[i] + ": " + carrier.growths[i] + "%", statsX, walletTop + 45 + (25 * i));
		}
		
		g.drawRect(weaponBoxX, walletTop+5, walletWidth/2, walletHeight-40);
		g.drawString("Weapon Skill Tree", Game.scale*5 + walletWidth/4 - 50, Game.HEIGHT-walletHeight+24);
		g.setFont(new Font("Times New Roman", Font.BOLD, 18));
		int strTop = walletTop + 60;
		int boxTop = strTop - 16;
		int distBetween = 50;
		for (int i = 0; i < Item.weaponTypes.length; i++) {
			g.drawString(Item.weaponTypes[i], Game.scale*7, strTop + (distBetween*i));
			g.drawRect(masteryBoxX, boxTop +(distBetween*i), weaponBoxWidth, 25);
			g.drawString("" + carrier.weaponMasteriesGrade[i], charX, strTop + (distBetween * i));
		}
		
		g.setColor(Color.cyan);
		for (int i = 0; i < 4; i++) {
			g.fillRect(masteryBoxX, boxTop + (distBetween*i), 1 + weaponBoxWidth*(carrier.weaponMasteries[i]+1)/carrier.weaponUpgrade, 25);
		}
		
		
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
		if (itemOptionIndex < 0) itemOptionIndex = 2;
		if (itemOptionIndex > 2) itemOptionIndex = 0;
	}
}
