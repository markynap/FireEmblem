package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import characters.Player;
import gameMain.Game;
import items.*;

public class ArmoryHandler {

	private Game game;
	/** The Player currently shopping in an Armory or Vendor */
	private Player shopper;
	/** True if we are in a Vendor, false if an Armory */
	private boolean inVendor;
	/** File containing all of the armory items to be displayed */
	private File armoryListFile;
	/** List of purchasable items available */
	private ArrayList<Item> availableItems;
	/** List of all the costs in Gold of each Purchasable Item */
	private ArrayList<Integer> itemCosts;
	/** The chapter we are currently processing the shop for, determines which items are available */
	private int currentChapter;
	/** Background image of our shop */
	private Image bgImage;
	/** Image of our shopkeeper */
	private Image shopkeeper;
	/** Color of the shopping list */
	private Color shoppingListColor = new Color(255,215,0);
	
	private int horizSpacing = 52, vertSpacing = 38;
	/** True if we are selecting purchase options on the item */
	private boolean inItemPurchase;
	/** Item that is selected to either buy or sell */
	private Item selectedItem;
	
	private int[] boxStats = {Game.WIDTH/6, Game.HEIGHT/4, 
			Game.WIDTH/3 + 4*Game.scale + 20, Game.HEIGHT/3 + 3*Game.scale};
	/** Index of the item we are currently selecting */
	private int selectedIndex;
	/** True if a player purchased an item during his stay */
	private boolean playerPurchased;
	
	private boolean drawCantBuyString, sentToConvoy;
	
	/** Responsible for handling both Armory and Vendor shop information */
	public ArmoryHandler(Game game) {
		this.game = game;
		armoryListFile = new File("res//designInfo//armoryList");
		availableItems = new ArrayList<>();
		itemCosts = new ArrayList<>();
		this.shopkeeper = Game.IM.getImage("/characterPics/shopkeeper.png");
	}
	
	/** Sets the shopper of this Store to the player given, and determines whether we are
	 *  in an armory or a Vendor depending on this player's current tile
	 * @param shopper - the player to shop at this store
	 */
	public void setPlayer(Player shopper) {
		this.shopper = shopper;
		this.currentChapter = shopper.currentTile.map.currentChapter;
		availableItems.clear();
		itemCosts.clear();
		this.playerPurchased = false;
		this.selectedIndex = 0;
		this.selectedItem = null;
		this.inItemPurchase = false;
		this.drawCantBuyString = false;
		this.sentToConvoy = false;
		
		
		if (shopper.currentTile.category.equalsIgnoreCase("Armory")) {
			inVendor = false;
			bgImage = Game.IM.getImage("/itemPics/armoryBG.png");
			findAvailableItems(inVendor);
			
		} else {
			
			inVendor = true;
			bgImage = Game.IM.getImage("/itemPics/vendorBG.png");			
			findAvailableItems(inVendor);
		}
	}
	
	/** sells the selected Item to the shopper */
	public void sellItem() {
		
		if (selectedItem.name.equalsIgnoreCase("Key")) {
			Key keyItem = (Key) selectedItem;
			keyItem.setGame(game);
			selectedItem = keyItem;
		}
		
		if (game.chapterOrganizer.gameGold < itemCosts.get(selectedIndex)) {
			// we do not have enough funds to buy this item 
			this.inItemPurchase = false;
			this.drawCantBuyString = true;
			return;
		}
		if (selectedItem.category.equalsIgnoreCase("Utility")) {
			if (shopper.wallet.utilities.size() >= Wallet.MAX_SIZE) {
				this.inItemPurchase = false;
				this.sentToConvoy = true;
				this.playerPurchased = true;
				game.chapterOrganizer.convoy.addItem(selectedItem);
				game.chapterOrganizer.incGameGold(-1 * itemCosts.get(selectedIndex));
				return;
			}
		} else {
			if (shopper.wallet.weapons.size() >= Wallet.MAX_SIZE) {
				this.inItemPurchase = false;
				this.sentToConvoy = true;
				this.playerPurchased = true;
				game.chapterOrganizer.convoy.addItem(selectedItem);
				game.chapterOrganizer.incGameGold(-1 * itemCosts.get(selectedIndex));
				return;
			}
		}
		
		shopper.addItem(selectedItem);
		this.playerPurchased = true;
		this.inItemPurchase = false;
		game.chapterOrganizer.incGameGold(-1 * itemCosts.get(selectedIndex));
	}
	
	public void render(Graphics g) {
		

		g.drawImage(bgImage, 0, 0, Game.WIDTH, Game.HEIGHT, null);
		
		g.drawImage(shopkeeper, Game.WIDTH/18, Game.HEIGHT/2 + Game.scale, Game.WIDTH/7, Game.HEIGHT/3, null);
		
		g.setColor(shoppingListColor);
		// the gold info box
		g.fillRect(boxStats[0] + boxStats[2] + 40, boxStats[1], boxStats[2]/4, boxStats[3]);
		// the weapon info box
		g.fillRect(boxStats[0], boxStats[1], boxStats[2], boxStats[3]);
		// gold amount box
		g.fillRect(Game.WIDTH - 200, 50, 200, 45);
		
		g.setColor(Color.white);
		
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 15));
				
		int startX = boxStats[0] + 4*Game.scale - 4;
		int startY = boxStats[1] + 55;
		
		if (this.drawCantBuyString) {
			g.drawString("Cannot Purchase! Not enough gold available", boxStats[0] - 2, boxStats[1] - 25);
		} else if (sentToConvoy) {
			g.drawString("Carrying too many items! Sent to the Convoy", boxStats[0] - 2, boxStats[1] - 25);

		}
		
		if (inVendor) {
			
			//title fonts
			
			g.drawString(" -- Welcome to the Vendor! --              DMG      HIT    CRIT   WGHT  RNG", boxStats[0] + 10, boxStats[1] + 18);
			g.drawString("Price:", boxStats[0] + boxStats[2] + 35 + boxStats[2]/12, boxStats[1] + 18);
			
			// weapon info font
			g.setFont(new Font("Times New Roman", Font.BOLD, 30));
			//display weapon information
			for (int i = 0; i < availableItems.size(); i++) {
				try {
					UtilityItem it = (UtilityItem)availableItems.get(i);
					g.setFont(new Font("Times New Roman", Font.BOLD, 32));
					g.drawString(it.name, boxStats[0] + 10, startY + vertSpacing*i);
					g.setFont(new Font("Times New Roman", Font.BOLD, 21));
					g.drawString(it.description.substring(0,Math.min(it.description.length(), 24)) + "", startX + horizSpacing, startY + vertSpacing*i);
				} catch (ClassCastException e) {
					g.setFont(new Font("Times New Roman", Font.BOLD, 32));
					CombatItem it = (CombatItem)availableItems.get(i);
					g.drawString(it.name, boxStats[0] + 10, startY + vertSpacing*i);
					g.drawString(it.damage + "", startX + horizSpacing, startY + vertSpacing*i);
					g.drawString(it.hit + "", startX + 2*horizSpacing, startY + vertSpacing*i);
					g.drawString(it.crit + "", startX + 3*horizSpacing, startY + vertSpacing*i);
					g.drawString(it.weight + "", startX + 4*horizSpacing, startY + vertSpacing*i);
					g.drawString(it.range + "", startX + 5*horizSpacing, startY + vertSpacing*i);
				}
				
			}
			
			g.setColor(Color.blue);
			for (int i = 0; i < 3; i++) {
				g.drawRect(boxStats[0] + 6 + i, startY + selectedIndex*vertSpacing - vertSpacing/2  - 12 + i, boxStats[2] - 8 - 2*i, vertSpacing - 2*i);
			}
			
			g.setColor(Color.blue);
			for (int i = 0; i < 3; i++) {
				g.drawRect(boxStats[0] + 6 + i, startY + selectedIndex*vertSpacing - vertSpacing/2  - 12 + i, boxStats[2] - 8 - 2*i, vertSpacing - 2*i);
				g.drawRect(boxStats[0] + boxStats[2] + 42 + i, startY + selectedIndex*vertSpacing - vertSpacing/2  - 12 + i, boxStats[2]/4 - 8 - 2*i, vertSpacing - 2*i);
			}
			g.setColor(Color.white);
			g.setFont(new Font("Times New Roman", Font.BOLD, 32));
			for (int i = 0; i < itemCosts.size(); i++) {
				g.drawString(itemCosts.get(i) + "G", boxStats[0] + boxStats[2] + 50, startY + i*vertSpacing);
			}
			
		} else {
			
			// we are in an armory
			
			g.drawString(" -- Welcome to the Armory! --              DMG      HIT    CRIT   WGHT  RNG", boxStats[0] + 10, boxStats[1] + 18);
			g.drawString("Price:", boxStats[0] + boxStats[2] + 35 + boxStats[2]/12, boxStats[1] + 18);
			
			// weapon info font
			g.setFont(new Font("Times New Roman", Font.BOLD, 30));
			//display weapon information
			for (int i = 0; i < availableItems.size(); i++) {
				CombatItem it = (CombatItem)availableItems.get(i);
				g.drawString(it.name, boxStats[0] + 10, startY + vertSpacing*i);
				g.drawString(it.damage + "", startX + horizSpacing, startY + vertSpacing*i);
				g.drawString(it.hit + "", startX + 2*horizSpacing, startY + vertSpacing*i);
				g.drawString(it.crit + "", startX + 3*horizSpacing, startY + vertSpacing*i);
				g.drawString(it.weight + "", startX + 4*horizSpacing, startY + vertSpacing*i);
				g.drawString(it.range + "", startX + 5*horizSpacing, startY + vertSpacing*i);
			}
			
			g.setColor(Color.blue);
			for (int i = 0; i < 3; i++) {
				g.drawRect(boxStats[0] + 6 + i, startY + selectedIndex*vertSpacing - vertSpacing/2  - 12 + i, boxStats[2] - 8 - 2*i, vertSpacing - 2*i);
				g.drawRect(boxStats[0] + boxStats[2] + 42 + i, startY + selectedIndex*vertSpacing - vertSpacing/2  - 12 + i, boxStats[2]/4 - 8 - 2*i, vertSpacing - 2*i);
			}
			g.setColor(Color.white);
			for (int i = 0; i < itemCosts.size(); i++) {
				g.drawString(itemCosts.get(i) + "G", boxStats[0] + boxStats[2] + 50, startY + i*vertSpacing);
			}
			
			
		}
				
		if (inItemPurchase) {
			
			int boxX = startX;
			int boxY = startY + (selectedIndex-1)*vertSpacing + vertSpacing/3 - 4;
			
			g.setColor(Color.blue);
			g.fillRect(startX, boxY, 100, vertSpacing);
			g.setColor(Color.BLACK);
			g.drawString("Buy", boxX + 22, boxY + 24);
			
		}
		
		g.setColor(Color.white);
		g.drawString("Gold: " + game.chapterOrganizer.gameGold, Game.WIDTH - 182, 80);
		
	}
	
	private void findAvailableItems(boolean isVendor) {
		try {
			Scanner reader = new Scanner(armoryListFile);
			
			if (!isVendor) {
				//if it is an armory 
				String line = reader.nextLine(); // "armory:"
				String[] lineParts;
				while (reader.hasNextLine()) {
					line = reader.nextLine(); 
					lineParts = line.split(":");
					if (lineParts.length > 1) {
						// chapter line
						if (this.currentChapter == Integer.parseInt(lineParts[1])) {
							//we are on the right chapter
							while (reader.hasNextLine()) {
								//start a new loop until we hit blank space or vendor
								line = reader.nextLine();
								if (line.isEmpty() || line.equalsIgnoreCase("vendor")) {
									break;
								}
								String[] linePart = line.split("-");
								Item newItem = Item.getItemByName(linePart[0]);
								if (newItem != null && !newItem.name.equalsIgnoreCase("Fists of Fury  ")) {
									availableItems.add(newItem);
									if (linePart.length > 1) itemCosts.add(Integer.valueOf(linePart[1]));
									else itemCosts.add(0);
								}
								
							}
							break;
						}
					}
				}
				reader.close();
			} else {
				String line = reader.nextLine(); // "armory:"
				String[] lineParts;
				while (reader.hasNextLine()) {
					line = reader.nextLine();
					if (line.equalsIgnoreCase("vendor")) break;
				}
				while (reader.hasNextLine()) {
					line = reader.nextLine(); 
					lineParts = line.split(":");
					if (lineParts.length > 1) {
						// chapter line
						if (this.currentChapter == Integer.parseInt(lineParts[1])) {
							//we are on the right chapter
							while (reader.hasNextLine()) {
								//start a new loop until we hit blank space or vendor
								line = reader.nextLine();
								if (line.isEmpty() || line.equalsIgnoreCase("vendor")) {
									break;
								}
								String[] linePart = line.split("-");
								Item newItem = Item.getItemByName(linePart[0]);
								if (newItem != null && !newItem.name.equalsIgnoreCase("Fists of Fury  ")) {
									availableItems.add(newItem);
									if (linePart.length > 1) itemCosts.add(Integer.valueOf(linePart[1]));
									else itemCosts.add(0);
								}
								
							}
							break;
						}
					}
				}
				reader.close();
			}
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	public void incSelectedIndex(int amount) {
		selectedIndex += amount;
		if (selectedIndex < 0) selectedIndex = availableItems.size() - 1;
		else if (selectedIndex >= availableItems.size()) selectedIndex = 0;
	}
	/** Whether or not the purchase information is being shown about a particular item */
	public void setItemPuchaseMode(boolean tf) {
		this.inItemPurchase = tf;
	}
	/** Sets the Selected Item to what is equipped and puts us in an item purchase state */
	public void setItemPurchaseSelection() {
		this.inItemPurchase = true;
		this.selectedItem = availableItems.get(selectedIndex);
	}
	/** True if the shopper purchased an item through his stay at our store */
	public boolean playerPurchasedItem() {
		return playerPurchased;
	}
	/** Returns the shopper shopping at our store */
	public Player getShopper() {
		return shopper;
	}
	/** Returns the boolean telling whether or not we are in a purchase item selection state */
	public boolean inItemPurchaseMode() {
		return this.inItemPurchase;
	}
}
