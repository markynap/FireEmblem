package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game;
import items.CombatItem;
import items.Convoy;
import items.Item;
import items.UtilityItem;
import items.Wallet;

public class TradeMenu {
	/** Game coordinates of Menu */
	public int xPos, yPos;
	/** Game's scale, the side length in pixels of 1 tile */
	private int scale = Game.scale;
	/** Parent Game for reference */
	public Game game;
	/** Player's responsible for trading with each other */
	public Player trader1, trader2;
	/** Height of our trade box (in-Game) */
	private int boxH;
	/** Width of our trade box (in-Game) */
	private int boxW = Game.WIDTH - 5;
	/** size of white outline of trade box */
	private int thickness = 4;
	/** Distance between wallet elements */
	private int spacing = 25;
	/** For splitting the menu into pieces */
	private int midLineY, centerLineX;
	/** The index of the item we are hovering over */
	private int selectedItem;
	/** True if trader1 is selectd */
	private boolean onTraderOne;
	/** A convoy to trade with if it exists */
	private Convoy convoy;
	/** affects the scrolling of our convoy */
	private int causeScrolling = 7, startScrolling = 0; 
	/** True if we have selected an item to trade and are selecting an index of the player's wallet to trade with */
	private boolean inOtherTraderSpace;
	/** for swapping weapons with players */
	private int otherTradingSpaceIndex;

	public TradeMenu(Player one, Player two) {
		this.trader1 = one;
		this.trader2 = two;
		this.xPos = 0;
		this.yPos = Game.HEIGHT/2 + Game.HEIGHT/12;
		boxH = Game.HEIGHT - yPos;
		midLineY = yPos + 40;
		centerLineX = Game.WIDTH/2;
		onTraderOne = true;
		this.game = one.game;
	}
	
	public TradeMenu(Player one, Convoy convoy) {
		this.trader1 = one;
		this.convoy = convoy;
		this.xPos = 0;
		this.yPos = Game.HEIGHT/2 + Game.HEIGHT/12;
		boxH = Game.HEIGHT - yPos;
		midLineY = yPos + 40;
		centerLineX = Game.WIDTH/2;
		onTraderOne = true;
		this.game = one.game;
		
	}
	
	public void render(Graphics g) {
		
		// draws the two traders actively trading
		g.drawImage(trader1.image, Game.WIDTH/5, yPos - 180, 140, 200, null);
		g.drawImage(trader2.image, 2*Game.WIDTH/3, yPos - 180, 140, 200, null);
		
		// draws our trading box
		g.setColor(Color.white);
		for (int i = 0; i < thickness; i++) {
			g.drawRect(xPos + i, yPos + i, boxW - (2*i), boxH - (2*i));
		}
		g.setColor(Color.gray);
		g.fillRect(xPos + thickness, yPos + thickness, boxW - 2*thickness, boxH - 2*thickness);

		//draws the selected item we are hovering over
		g.setColor(Color.blue);
		if (onTraderOne) g.fillRect(xPos + thickness, midLineY + scale/5 + (spacing * selectedItem), boxW/2 - thickness + 1, (int)(2*(spacing/3.0)));	
		else g.fillRect(xPos + thickness/2 + boxW/2, midLineY + scale/5 + (spacing * selectedItem), boxW/2 - thickness + 1, (int)(2*(spacing/3.0)));
		
		// draws item swapping box if one should exist 
		if (inOtherTraderSpace) {
			g.setColor(Color.green);
			if (onTraderOne) {
				// this means we are on trader 2 for swapping the indexing
				g.fillRect(xPos + thickness/2 + boxW/2, midLineY + scale/5 + (spacing * this.otherTradingSpaceIndex), boxW/2 - thickness + 1, (int)(2*(spacing/3.0)));
			} else {
				g.fillRect(xPos + thickness, midLineY + scale/5 + (spacing * otherTradingSpaceIndex), boxW/2 - thickness + 1, (int)(2*(spacing/3.0)));				}
		}
		
		// clean up trading box and identify traders
		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.setColor(Color.white);
		g.drawLine(centerLineX, yPos + thickness, centerLineX, yPos + boxH - thickness);
		
		g.drawString(trader1.name, xPos + boxW/8 + 80/trader1.name.length(), yPos + thickness + 25);
		g.drawString(trader2.name, xPos + 3*boxW/5 + Game.scale + 80/trader1.name.length(), yPos + thickness + 25);
		
		g.drawLine(xPos, midLineY, xPos + boxW + thickness, midLineY);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		
		
		for (int i = 0; i < trader1.wallet.weapons.size(); i++) {
			CombatItem item = trader1.wallet.weapons.get(i);
			g.drawString(item.name + " (" + item.duration + ")", xPos + 12, midLineY + scale/2 + (spacing * i));
		}
		
		for (int i = 0; i < trader1.wallet.utilities.size(); i++) {
			UtilityItem item = trader1.wallet.utilities.get(i);
			g.drawString(item.name + " (" + item.duration + ")", xPos + 12, midLineY + scale/2 + (spacing * i) + (spacing * trader1.wallet.weapons.size()));
		}
		
		for (int i = 0; i < trader2.wallet.weapons.size(); i++) {
			CombatItem item = trader2.wallet.weapons.get(i);
			g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/2 + (spacing * i));
		}
		
		for (int i = 0; i < trader2.wallet.utilities.size(); i++) {
			UtilityItem item = trader2.wallet.utilities.get(i);
			g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/2 + (spacing * i) + (spacing * trader2.wallet.weapons.size()));
		}
		
		
	}
	/** Returns the item to swap with in the other units trading space */
	public Item getOtherTradingSpaceItem() {
		if (onTraderOne) {
			if (this.otherTradingSpaceIndex >= trader2.wallet.size()) return null;
			else return trader2.wallet.getFinalIndex(otherTradingSpaceIndex);
		} else {
			if (otherTradingSpaceIndex >= trader1.wallet.size()) return null;
			else return trader1.wallet.getFinalIndex(otherTradingSpaceIndex);
		}
	}
	
	public void renderBattlePrepMenu(Graphics g) {
		
		// draws the two traders actively trading
		g.drawImage(trader1.image, Game.WIDTH/5, yPos - 180, 140, 200, null);
		if (trader2 != null) {
			g.drawImage(trader2.image, 2*Game.WIDTH/3, yPos - 180, 140, 200, null);
		}
		// draws our trading box
		g.setColor(Color.white);
		for (int i = 0; i < thickness; i++) {
			g.drawRect(xPos + i, yPos + i, boxW - (2*i), boxH - (2*i));
		}
		g.setColor(Color.gray);
		g.fillRect(xPos + thickness, yPos + thickness, boxW - 2*thickness, boxH - 2*thickness);

		//draws the selected item we are hovering over
		g.setColor(Color.blue);
		if (onTraderOne) g.fillRect(xPos + thickness, midLineY + scale/7 + (spacing * selectedItem), boxW/2 - thickness/2, (int)(2*(spacing/3.0)) + 8);	
		else {
					
			if (trader2 != null) {
				g.fillRect(xPos + thickness + boxW/2, midLineY + scale/7 + (spacing * selectedItem), boxW/2, (int)(2*(spacing/3.0)) + 8);
			} else {
				g.fillRect(xPos + thickness + boxW/2, midLineY + scale/7 + (spacing * (selectedItem-startScrolling)), boxW/2, (int)(2*(spacing/3.0)));
			}
		}
		// clean up trading box and identify traders
		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.setColor(Color.white);
		g.drawLine(centerLineX, yPos + thickness, centerLineX, yPos + boxH - thickness);
				
		g.drawString(trader1.name, xPos + boxW/8 + 80/trader1.name.length(), yPos + thickness + 25);
		if (trader2 != null) {
			g.drawString(trader2.name, xPos + 3*boxW/5 + Game.scale + 80/trader1.name.length(), yPos + thickness + 25);
		} else {
			g.drawString("Convoy", xPos + 3*boxW/5 + Game.scale + 12, yPos + thickness + 25);
		}
				
		g.drawLine(xPos, midLineY, xPos + boxW + thickness, midLineY);
				
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
				
				
		for (int i = 0; i < trader1.wallet.weapons.size(); i++) {
			CombatItem item = trader1.wallet.weapons.get(i);
			g.drawString(item.name + " (" + item.duration + ")", 12, midLineY + scale/2 + (spacing * i));
		}
				
		for (int i = 0; i < trader1.wallet.utilities.size(); i++) {
			UtilityItem item = trader1.wallet.utilities.get(i);
			g.drawString(item.name + " (" + item.duration + ")", xPos + 12, midLineY + scale/2 + (spacing * i) + (spacing * trader1.wallet.weapons.size()));
		}

		if (trader2 != null) {
			for (int i = 0; i < trader2.wallet.weapons.size(); i++) {
				CombatItem item = trader2.wallet.weapons.get(i);
				g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/2 + (spacing * i));
			}
				
			for (int i = 0; i < trader2.wallet.utilities.size(); i++) {
				UtilityItem item = trader2.wallet.utilities.get(i);
				g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/2 + (spacing * i) + (spacing * trader2.wallet.weapons.size()));
			}
		} else {
			for (int i = startScrolling; i < Math.min(convoy.getCombatItems().size(), startScrolling + 8); i++) {
				CombatItem item = convoy.getCombatItems().get(i);
				if (item == null) continue;
				g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/2 + (spacing * (i-startScrolling)));
			}
					
			for (int i = 0; i < convoy.getUtilityItems().size(); i++) {
				UtilityItem item = convoy.getUtilityItems().get(i);
				if (item == null) continue;
				g.drawString(item.name + " (" + item.duration + ")", centerLineX + 5, midLineY + scale/2 + (spacing * i) + (spacing * (convoy.getCombatItems().size()-startScrolling)));
			}
		}
	}
	
	public void updateSelectedItem(int amount) {
		
		int max = 0;
		if (onTraderOne) max = Math.max(trader1.wallet.weapons.size()+ trader1.wallet.utilities.size()-1, 0);
		else {
			if (trader2 != null) {
				max = Math.max(trader2.wallet.weapons.size()+ trader2.wallet.utilities.size()-1, 0);
			} else {
				max = convoy.convoyItems.size()-1;
			}
		}
		
		selectedItem += amount;
		
		if (selectedItem > max) selectedItem = 0;
		else if (selectedItem < 0) selectedItem = max;
		
		this.startScrolling = Math.max(0, selectedItem - causeScrolling);
		
		
	}
	/** Swaps who's items we are selecting over */
	public void swapTrader() {
		if (onTraderOne) onTraderOne = false;
		else onTraderOne = true;
		selectedItem = 0;
		this.startScrolling = Math.max(0, selectedItem - causeScrolling);
	}
	/** True if the selected item belongs to player one */
	public boolean isOnPlayerOne() {
		return onTraderOne;
	}
	
	public Item itemToTrade() {
		
		if (onTraderOne) {
			
			if (selectedItem >= trader1.wallet.weapons.size()) return trader1.wallet.utilities.get(selectedItem - trader1.wallet.weapons.size()); 
			else return trader1.wallet.weapons.get(selectedItem);
			
		} else {
			
			if (trader2 != null) {
				if (selectedItem >= trader2.wallet.weapons.size()) return trader2.wallet.utilities.get(selectedItem - trader2.wallet.weapons.size()); 
				else return trader2.wallet.weapons.get(selectedItem);
			} else {
				if (selectedItem >= convoy.convoyItems.size()) return null;
				if (selectedItem >= convoy.getCombatItems().size()) {
					return convoy.getUtilityItems().get(selectedItem - convoy.getCombatItems().size());
				} else return convoy.getCombatItems().get(selectedItem);
			}
		}
		
	}
	/** Get this game's convoy */
	public Convoy getConvoy() {
		return convoy;
	}
	/** Set to true if unit has selected an item to trade */
	public void setInOtherTradingSpace(boolean tf) {
		this.inOtherTraderSpace = tf;
	}
	/** Whether or not we have selected an item to trade and are choosing an index of opponents wallet to trade with */
	public boolean inOtherTradersSpace() {
		return inOtherTraderSpace;
	}
	/** Increments the position of the green bar in the opponents trade menu for swapping items */
	public void incOtherTradingSpaceIndex(int amount) {
		this.otherTradingSpaceIndex += amount;
		
		if (onTraderOne) {
			if (trader2 != null) {
				if (otherTradingSpaceIndex >= 2*Wallet.MAX_SIZE) otherTradingSpaceIndex = 0;
				else if (otherTradingSpaceIndex < 0) otherTradingSpaceIndex = 2*Wallet.MAX_SIZE-1;
			} else {
				if (otherTradingSpaceIndex >= convoy.convoyItems.size()) otherTradingSpaceIndex = 0;
				else if (otherTradingSpaceIndex < 0) otherTradingSpaceIndex = convoy.convoyItems.size()-1;
			}
		} else {
			if (otherTradingSpaceIndex >= 2*Wallet.MAX_SIZE) otherTradingSpaceIndex = 0;
			else if (otherTradingSpaceIndex < 0) otherTradingSpaceIndex = 2*Wallet.MAX_SIZE-1;
		}
		
	}
	/** Returns the index of the item being swapped by the player: trader1 if onPlayerOne() -- the blue bar*/
	public int getTradingIndex() {
		return selectedItem;
	}
	/** Returns the index of the person receiving an item to be swapped or traded, the green bar */
	public int getReceivingIndex() {
		return otherTradingSpaceIndex;
	}
}
