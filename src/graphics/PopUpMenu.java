package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import characters.Player;
import gameMain.Game;
import tiles.Throne;
import tiles.Tile;
import tiles.Village;

public class PopUpMenu {
	
	private ArrayList<String> options;
	
	public Game game;
	//public boolean[] selectedOptions;
	public int selectedIndex;
	public Tile tile;
	public Player player;
	
	private int xPos, yPos, menuwidth, menuheight;

	public PopUpMenu(Game game, Tile tile) {
		this.game = game;
		this.tile = tile;
		options = new ArrayList<>();
		player = tile.carrier;
		
		if (player != null) {
			
			if (!player.canMA() && !player.canUse) {
				options.add("Options");
				options.add("End");
				
			} else {
				//player can use and can attack just fine
				if (tile.getClass().equals(Throne.class)) {
					options.add("Sieze");	
				} else if (tile.getClass().equals(Village.class)) {
					if (tile.getSpriteIndex() == 0) options.add("Visit");
				} else if (tile.category.equalsIgnoreCase("Armory")) {
					options.add("Visit");
				} else if (tile.category.equalsIgnoreCase("Vendor")) {
					options.add("Visit");
				}
				if (game.chapterOrganizer.nextToTalkedToUnit(player)) {
					options.add("Talk");
				}
				if (player.isHealer()) {
					
					if (player.duoWeaponHeal) {
						if (game.enemiesSurrounding(player, player.maxWeaponRange())) {
							options.add("Attack");
						}
						if (game.alliesSurrounding(player, player.maxStaffRange() + player.staffExtention)) {
							options.add("Heal");
						}
					} else {
						if (game.alliesSurrounding(player, player.maxStaffRange() + player.staffExtention)) {
							options.add("Heal");
						}
					}
				} else if (player.isDancer()) {

					if (game.alliesSurrounding(player, 1)) {
						options.add("Dance");
					}
					
				} else if (player.skill.nameEquals("Pick")) {
					
					if (game.chapterOrganizer.currentMap.pickableAdjacent(player.currentTile)) {
						options.add("Pick");
					}
					if (game.enemiesSurrounding(player, player.maxWeaponRange())) {
						options.add("Attack");
					} 
					
				} else {
					
					if (game.enemiesSurrounding(player, player.maxWeaponRange()+player.bowExtention)) {
						options.add("Attack");
					} 
				}
				
				if (player.canUseSkill) {
					if (!player.skill.isPassive()) {
						options.add("Skill");
						if (player.skill.nameEquals("Divine Blessing")) {
							if (!player.hasHealingItem() || !game.alliesSurrounding(player, 1)) {
								options.remove("Skill");
							}
						} else if (player.skill.nameEquals("Confession")) {
							if (game.chapterOrganizer.listOfOpposingUnitsInRange(player, 1 + player.confessionRange).isEmpty()) {
								options.remove("Skill");
							}
						} else if (player.skill.nameEquals("Soul Juice")) {
							if (!game.alliesSurrounding(player, 1)) {
								options.remove("Skill");
							}
						}
					}
				}
				if (!player.isConfessed) {
					if (player.playerCarried == null) {
						if (game.alliesSurrounding(player, 1)) {
							ArrayList<Player> list = game.chapterOrganizer.getAdjacentAllies(player);
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i) == null) continue;
								if (list.get(i).CON < player.CON + player.getMountedAidBonus()) {
									if (!list.get(i).isCarryingUnit) {
										options.add("Carry");
										break;
									}
								}
							}
						}
					} else {
						if (game.chapterOrganizer.emptyTileAdjacentFromSource(player.currentTile)) {
							options.add("Drop");
						}
					}
				}
				if (player.canUse) {
					options.add("Items");
					if (player.canTrade) {
						if (game.alliesSurrounding(player, 1)) options.add("Trade");
					}
				}
				options.add("Wait");
			
			
			}
		} else {
			options.add("Options");
			options.add("Tutorial");
			options.add("End");
		}
		
		this.xPos = tile.xPos;
		this.yPos = tile.yPos;
				
		if (Game.nRow - xPos < 3) xPos -=2;	
		if (Game.nCol - yPos < 3) yPos -= 2;
		
		xPos *= Game.scale;
		yPos *= Game.scale;
		menuwidth = 80;
		menuheight = 20 * options.size();
		
	}

	public void render(Graphics g) {
		
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));

		g.setColor(Color.black);
		g.drawRect(xPos + Game.scale, yPos , menuwidth, menuheight);

		for (int i = 0; i < options.size(); i++) {
			if (selectedIndex == i) g.setColor(Color.blue);
			else g.setColor(Color.white);
			g.fillRect(xPos + Game.scale, yPos + (i*menuheight)/(options.size()), menuwidth, menuheight/(options.size()));
			g.setColor(Color.black);
			g.drawString(options.get(i), xPos + Game.scale + 10, yPos + ((i+1)*menuheight)/(options.size()) - 4);
		}
		
	}

	public void incSelectedOptions(int amount) {
		selectedIndex += amount;
		if (selectedIndex >= options.size()) selectedIndex = 0;
		else if (selectedIndex < 0) selectedIndex = options.size()-1;
		
	}
	
	/** Returns the option that was selected from this pop up menu */
	public String getSelectedOption() {
		return options.get(selectedIndex);
	}

	
}
