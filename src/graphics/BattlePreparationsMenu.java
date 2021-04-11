package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import characters.AllyPlayer;
import characters.Player;
import gameMain.ChapterMap;
import gameMain.ChapterOrganizer;
import gameMain.Game;
import gameMain.Game.STATE;
import items.Convoy;
import tiles.Tile;

public class BattlePreparationsMenu {
	
	/** Instance of our game */
	private Game game;
	/** Our Chapter Organizer containing our allies */
	private ChapterOrganizer organizer;
	/** All allies that have been selected for deployment for this chapter */
	private ArrayList<AllyPlayer> deployedAllys;
	/** Number of Player's in each column */
	private int nCols = 2;
	/** Size of each Player's image */
	private int imageScale = 50;
	/** spacing between our players in the y direction*/
	private int spacing = 16;
	/** Index of what we are selecting */
	private int selectedIndex;
	/** Spacing between units in the x-direction */
	private int xSpacing = (int)(3*Game.scale);	
	/** Maximum number of units deployable for this chapter */
	private int MAX_DEPLOYABLE = 9;
	/** Selectable options in our menu */
	private String[] menuOptions = {"Select Allys", "Ally Positions", "Trade Items", "Start Chapter"};
	/** Blue background color */
	private Color backgroundColor = new Color(15,65,160);
	/** Used for changing the positions of our units */
	private AllyPlayer selectedPlayer;
	/** Instance of the ChapterOrganizer's Convoy */
	private Convoy convoy;
	/** List of allies to be deployed or traded with */
	private ArrayList<AllyPlayer> allySelection;
	/** List of tiles where allys are able to be swapped with */
	private ArrayList<Tile> allyPositionOptions;
	/** Which state of the battle preparations we are in */
	public enum PreparationState {
		MainMenu,
		AllySelection,
		AllyPositions,
		AllyInfo
	}
	
	public PreparationState prepState = PreparationState.MainMenu;
	
	public BattlePreparationsMenu(Game game, ChapterOrganizer organizer) {
		this.game = game;
		this.organizer = organizer;
		this.convoy = organizer.convoy;
		convoy.saveConvoySupply();
		convoy.resupplyConvoy();
		deployedAllys = new ArrayList<>();
		allyPositionOptions = new ArrayList<>();
		
		setHectorMode();
		
		allySelection = organizer.getPlayersForBattlePreparation();
		setMaxDeployable();
		
		for (int i = 0; i < MAX_DEPLOYABLE; i++) {
			if (i < allySelection.size() && deployedAllys.size() < MAX_DEPLOYABLE) {
				deployedAllys.add(allySelection.get(i));
			}
		}
		for (int i = 0; i < organizer.currentMap.tiles.size(); i++) {
			Tile tile = organizer.currentMap.tiles.get(i);
			if (tile.isOccupied()) {
				Player player = tile.carrier;
				if (player != null) {
					if (player.isAlly()) {
						if (!player.spawnsOnChaptStart) {
							player.currentTile.setCarrier(null);
						}
					}
				}
			} else if (tile.allySpawnTile) {
				allyPositionOptions.add(tile);
			}
		}
		
		for (int i = 0; i < deployedAllys.size(); i++) {
			if (i >= allyPositionOptions.size()) break;
			allyPositionOptions.get(i).setCarrier(deployedAllys.get(i));
			deployedAllys.get(i).setCurrentTile(allyPositionOptions.get(i));
		}
	}
	/** Sets game.onHectorMode for the various chapters */
	private void setHectorMode() {
		if (organizer.currentChapter == 10) {
			game.onHectorMode = true;
		} else if (organizer.currentChapter == 12) {
			game.onHectorMode = true;
		} else if (organizer.currentChapter == 14) {
			game.onHectorMode = true;
		} else if (organizer.currentChapter <= 15){
			game.onHectorMode = false;
		} else if (organizer.currentChapter > 15){
			// over chapter 16
			try {
				Scanner reader = new Scanner(new File("res//chapters//chapterStatus"));
				String line;
				String[] lineParts;
				if (this.organizer.loadLevel == 1) {
					line = reader.nextLine();
					lineParts = line.split(":");
					if (lineParts.length == 4) {
						if (lineParts[3].equalsIgnoreCase("Ike")) {
							game.onHectorMode = false;
						} else {
							game.onHectorMode = true;
						}
					} else {
						game.onHectorMode = false;
					}
				} else if (organizer.loadLevel == 2) {
					reader.nextLine();
					line = reader.nextLine();
					lineParts = line.split(":");
					if (lineParts.length == 4) {
						if (lineParts[3].equalsIgnoreCase("Ike")) {
							game.onHectorMode = false;
						} else {
							game.onHectorMode = true;
						}
					} else {
						game.onHectorMode = false;
					}
				} else if (organizer.loadLevel == 3) {
					reader.nextLine();
					reader.nextLine();
					line = reader.nextLine();
					lineParts = line.split(":");
					if (lineParts.length == 4) {
						if (lineParts[3].equalsIgnoreCase("Ike")) {
							game.onHectorMode = false;
						} else {
							game.onHectorMode = true;
						}
					} else {
						game.onHectorMode = false;
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				System.err.println("chapterStatus file not found in BattlePreparationsMenu, setting Ike mode to true");
				game.onHectorMode = false;
			}
		}
	}
	
	/** Sets the maximum deployable units based on the chapter */
	private void setMaxDeployable() {
		
		switch (organizer.currentChapter) {
		
		case 5: MAX_DEPLOYABLE = 8;
				break;
		case 7: MAX_DEPLOYABLE = 11;
				break;		
		case 8: MAX_DEPLOYABLE = 11;
				break;	
		case 9: MAX_DEPLOYABLE = 12;
				break;	
		case 10: MAX_DEPLOYABLE = 9;
				break;	
		case 11: MAX_DEPLOYABLE = 3;
				break;
		case 12: MAX_DEPLOYABLE = 10;
				break;
		case 13: MAX_DEPLOYABLE = 4;
				break;
		case 14: MAX_DEPLOYABLE = 11;
				break;
		case 15: MAX_DEPLOYABLE = 8;
				break;
		case 16: MAX_DEPLOYABLE = 11;
				break;
		}
		
	}
	
	public void render(Graphics g) {
		if (this.prepState == PreparationState.AllySelection) {
			renderAllySelection(g);
		} else if (this.prepState == PreparationState.MainMenu) {
			renderMainMenu(g);
			renderMainMenuMiniMap(g);
		} else if (this.prepState == PreparationState.AllyPositions) {
			renderAllyPositions(g);
		} else if (this.prepState == PreparationState.AllyInfo) {
			renderAllyTradeSelection(g);
		}
	}
	
	public void setSelectedPlayer(Player selected) {
		if (selected == null) {
			this.selectedPlayer = null;
			return;
		}
		if (selected.teamID.equalsIgnoreCase("Ally")) {
			this.selectedPlayer = (AllyPlayer)selected;
		}
	}
	/**
	 * Trades the position of two units
	 * @param swap
	 */
	public void swapWithPlayer(Player swap) {
		if (selectedPlayer == null) return;
		if (swap == null) {
			if (this.organizer.currentMap.currentTile.allySpawnTile) {
				selectedPlayer.currentTile.setCarrier(null);
				organizer.currentMap.currentTile.setCarrier(selectedPlayer);
				selectedPlayer.setCurrentTile(organizer.currentMap.currentTile);
				return;
			}
		}
		if (selectedPlayer.currentTile.placeEquals(swap.currentTile)) return;
		Tile oldSelTile = selectedPlayer.currentTile;
		Tile swapTile = swap.currentTile;
		oldSelTile.setCarrier(swap);
		swapTile.setCarrier(selectedPlayer);
	}
	
	public void swapWithTile() {
		
	}
	
	public boolean hasSelectedPlayer() {
		return selectedPlayer != null;
	}
	
	public void setPrepState(PreparationState state) {
		this.prepState = state;
		this.selectedIndex = 0;
	}
	
	private void renderAllyPositions(Graphics g) {
		
		//organizer.currentMap.render(g);
		ChapterMap map = organizer.currentMap;
		
		for (int i = 0; i < map.tiles.size(); i++) {
			Tile tile = map.tiles.get(i);
			if (tile.isOccupied()) {
				if (tile.carrier.isAlly()) {
					tile.renderDeployableUnits(g);
					continue;
				}
			}
			tile.renderDeployableUnits(g);
		}
		map.drawSelectedBox(g);
	
		if (selectedPlayer != null) {
			g.setColor(Color.BLUE);
			for (int i = 0; i < 6; i++) {
				g.drawRect(selectedPlayer.currentTile.xPos*Game.scale + i, selectedPlayer.currentTile.yPos*Game.scale + i, 
						Game.scale - 2*i, Game.scale - 2*i);
			}
		}
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 46));
		g.drawString("Select An Ally To Swap Positions", Game.WIDTH/8, 38);
	}
	
	private void renderMainMenuMiniMap(Graphics g) {
		
		int startX = Game.WIDTH/2 - Game.scale/2;
		int startY = Game.HEIGHT/7;
		int width = 450;
		
		this.organizer.currentMap.renderMiniMapVersion(g, width, startX, startY, 1);
	}
	
	private void renderMainMenu(Graphics g) {
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.black);
		int[] b = {Game.WIDTH/11, Game.HEIGHT/7, Game.WIDTH/3, Game.HEIGHT/2};
		for (int i = 0; i < menuOptions.length; i++) {
			g.fillRect(b[0],b[1] + i*(b[3]/4 + 20),b[2],b[3]/4);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 35));
		g.drawString("Chapter " + game.chapterOrganizer.currentChapter, Game.WIDTH/2 - Game.scale, 45);
		
		for (int i = 0; i < menuOptions.length; i++) {
			g.drawString(menuOptions[i], b[0] + b[2]/8, b[1] + i*(b[3]/menuOptions.length + 20) + b[3]/10);
		}
		
		g.setColor(Color.red);
		for (int i = 0; i < 3; i++) {
			g.drawRect(b[0] - 4 + i, b[1] + selectedIndex*(b[3]/4 + 20) - 4 + i, b[2] + 8 - 2*i, b[3]/menuOptions.length + 8 - 2*i);
		}
		
		
		
	}
	/** Sets the view of the Player Info GFX to the player we are hovered over */
	public void changeTrader() {
		if (selectedIndex >= allySelection.size()) return;
		this.game.playerGFX.setPlayer(allySelection.get(selectedIndex));
		
	}
	/** Selects a trader to begin looking for someone else to trade with */
	public void selectTrader() {
		if (selectedPlayer == null) {
			if (selectedIndex >= allySelection.size()) return;
			this.setSelectedPlayer(allySelection.get(selectedIndex));
			return;
		} else {
			if (selectedIndex >= allySelection.size()) {
				if (selectedPlayer != null) {
					game.setTradeMenu(selectedPlayer, convoy);
					game.setGameState(STATE.BattlePreparations);
					return;
				}
			}
			if (allySelection.get(selectedIndex).equals(selectedPlayer)) {
				selectedPlayer = null;
			} else {
				//set trade menu here
				game.setTradeMenu(selectedPlayer, allySelection.get(selectedIndex));
				game.setGameState(STATE.BattlePreparations);
			}
		}
	}
	
	public void chooseStateFromMenu() {
		switch (selectedIndex) {
		
		case 0: setPrepState(PreparationState.AllySelection);
				break;
		case 1: setPrepState(PreparationState.AllyPositions);
				selectedPlayer = null;
				organizer.currentMap.findAllyWithMoves();
				break;
		case 2: setPrepState(PreparationState.AllyInfo);
				selectedIndex = 0;
				selectedPlayer = null;
				changeTrader();
				break;
		case 3: convoy.saveConvoySupply();
				organizer.updatePlayers(organizer.loadLevel);
				startChapter();
				break;
		}
	}

	
	private void renderAllySelection(Graphics g) {
		
		int startX = Game.WIDTH/2 - Game.scale/2;
		int startY = 80;
		int[] box = {startX - 5, startY - 5, Game.WIDTH/2 + Game.scale/2, Game.HEIGHT - 160};
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(box[0],box[1],box[2],box[3]);
		
		int yInc = 0;
		int xInc = 0;
		
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 38));
		g.setColor(Color.white);
		g.drawString("Pick " + (MAX_DEPLOYABLE-deployedAllys.size()) + " More Units " + deployedAllys.size() + "/" + MAX_DEPLOYABLE, startX + 10, startY-20);	

		
		for (int i = 0; i < allySelection.size(); i++) {
			AllyPlayer player = allySelection.get(i);
			
			g.drawImage(player.image, startX + xInc*(imageScale + xSpacing), startY + yInc*(imageScale+spacing), imageScale, imageScale, null);
			g.setFont(new Font("Times New Roman", Font.BOLD, 35));
			if (deployedAllys.contains(player)) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.BLACK);
			}
			
			if (game.onHectorMode) {
				if (player.name.equalsIgnoreCase("Hector")) {
					g.setColor(Color.green);
				}
			} else {
				if (player.name.equalsIgnoreCase("Ike")) {
					g.setColor(Color.green);
				}
			}
				
			g.drawString(player.name, startX + xInc*(imageScale + xSpacing) + imageScale + 8, startY + yInc*(imageScale+spacing) + imageScale/2 + 3);
			xInc++;
			if (xInc >= nCols) {
				yInc++;
				xInc = 0;
			}
			
		}
		
		// green box around the selected unit
		g.setColor(Color.GREEN);
		for (int j = 0; j < 4; j++) {
			g.drawRect(startX + (selectedIndex%nCols)*(imageScale + xSpacing) - 5 + j, 
			startY + (selectedIndex/nCols)*(imageScale + spacing) - 5 + j, imageScale + 5 - 2*j, imageScale + 5 - 2*j);
		}
		
		// selected unit information box
		int[] boxx = {10, startY - 30, Game.WIDTH/3 + Game.scale, Game.HEIGHT - 130};
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(boxx[0], boxx[1], boxx[2], boxx[3]);
		g.setColor(new Color(210,180,140));
		g.fillRect(boxx[0], boxx[1], boxx[2], boxx[3]/4 - 20);
		
		int infoStartY = boxx[1] + boxx[3]/4 - 25;

		// selected unit information
		AllyPlayer player = allySelection.get(selectedIndex);
		if (player != null) {
			
			g.setColor(Color.white);
			// spacing between indexed weapons
			int infoSpacing = 40;
			// draw current level, EXP, and Class info
			g.setFont(new Font("Times New Roman", Font.BOLD, 30));
			g.drawString("LV:" + player.level + "  EXP:" + player.EXP, boxx[0] + boxx[2]/4 - 10, boxx[1] + boxx[3]/8);
			g.drawString(player.Class, boxx[0] + boxx[2]/4 + 32 + 40/player.Class.length(), boxx[1] + boxx[3]/6 + 10);
			// draw player's image and name
			g.setFont(new Font("Times New Roman", Font.BOLD, 34));
			g.drawImage(player.image, boxx[0] + 2, boxx[1] + 5, boxx[2]/5, boxx[3]/4 - 30, null);
			g.drawString(player.name, boxx[0] + boxx[2]/2 - 8*player.name.length(), boxx[1] + boxx[3]/18);
			// draw player's stats
			g.setFont(new Font("Times New Roman", Font.BOLD, 34));
			for (int i = 0; i < player.stats.length-1; i++) {
				if (player.statCapped(i)) g.setColor(Color.green);
				else g.setColor(Color.white);
				if (!player.isMagicUser) {
					g.drawString(Player.StatNames[i] + ": " + player.stats[i], boxx[0] + 20, infoStartY + infoSpacing*(i+1));
				} else {
					g.drawString(Player.MagStatNames[i] + ": " + player.stats[i], boxx[0] + 20, infoStartY + infoSpacing*(i+1));
				}
			}
			g.setColor(Color.white);
			g.setFont(new Font("Times New Roman", Font.BOLD, 30));
			g.drawString("Skill: " + player.skill.getName(), boxx[0] + 20, infoStartY + infoSpacing*(player.stats.length) + 8);
			
			
			
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 35));
		g.drawString("Press a To Deploy Ally", Game.WIDTH/2 - 3*Game.scale, Game.HEIGHT - 45);

		
	}
	
	private void renderAllyTradeSelection(Graphics g) {
		
		int startX = Game.WIDTH/2 - Game.scale/2;
		int startY = 80;
		int[] box = {startX - 5, startY - 5, Game.WIDTH/2 + Game.scale/2, Game.HEIGHT - 160};
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(box[0],box[1],box[2],box[3]);
		
		int yInc = 0;
		int xInc = 0;
		
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 35));
		g.setColor(Color.white);
		g.drawString("Trade Items Between Players", startX - 10, startY-20);	

		
		for (int i = 0; i < allySelection.size(); i++) {
			AllyPlayer player = allySelection.get(i);
			
			g.drawImage(player.image, startX + xInc*(imageScale + xSpacing), startY + yInc*(imageScale+spacing), imageScale, imageScale, null);
			g.setFont(new Font("Times New Roman", Font.BOLD, 35));
			if (player.name.equalsIgnoreCase("Ike")) {
				g.setColor(Color.green);
			} else if (deployedAllys.contains(player)) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.BLACK);
			}
				
			g.drawString(player.name, startX + xInc*(imageScale + xSpacing) + imageScale + 8, startY + yInc*(imageScale+spacing) + imageScale/2 + 3);
			xInc++;
			if (xInc >= nCols) {
				yInc++;
				xInc = 0;
			}
			
		}
		
		// green box around the selected unit
		g.setColor(Color.GREEN);
		for (int j = 0; j < 4; j++) {
			g.drawRect(startX + (selectedIndex%nCols)*(imageScale + xSpacing) - 5 + j, 
			startY + (selectedIndex/nCols)*(imageScale + spacing) - 5 + j, imageScale + 5 - 2*j, imageScale + 5 - 2*j);
		}
		
		if (selectedPlayer != null) {
			for (int j = 0; j < 4; j++) {
				g.drawRect(startX + (allySelection.indexOf(selectedPlayer)%nCols)*(imageScale + xSpacing) - 5 + j, 
					startY + (allySelection.indexOf(selectedPlayer)/nCols)*(imageScale + spacing) - 5 + j, imageScale + 5 - 2*j, imageScale + 5 - 2*j);
			}
		}
		
		// selected unit information box
		int[] boxx = {10, startY - 30, Game.WIDTH/3 + Game.scale, Game.HEIGHT - 130};
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(boxx[0], boxx[1], boxx[2], boxx[3]);
		g.setColor(new Color(210,180,140));
		g.fillRect(boxx[0], boxx[1], boxx[2], boxx[3]/4 - 20);
		int weaponStartY = boxx[1] + boxx[3]/4 + 25;

		// selected unit information
		AllyPlayer player = null;
		if (selectedIndex < allySelection.size()) {
			player = allySelection.get(selectedIndex);
		}
		if (player != null) {
			
			g.setColor(Color.white);
			// spacing between indexed weapons
			int weaponSpacing = 50;
			// draw current level and EXP info
			g.setFont(new Font("Times New Roman", Font.BOLD, 30));
			g.drawString("LV:" + player.level + "  EXP:" + player.EXP, boxx[0] + boxx[2]/4 - 10, boxx[1] + boxx[3]/8);
			// current weapon mastery info
			g.setFont(new Font("Times New Roman", Font.BOLD, 20));
			if (player.isMagicUser) {
				g.drawString("Fire: " + player.weaponMasteriesGrade[0], boxx[0] + boxx[2]/4 - 10, boxx[1] + boxx[3]/6);
				g.drawString("Ice: " + player.weaponMasteriesGrade[1], boxx[0] + boxx[2]/2 + 10, boxx[1] + boxx[3]/6);
				g.drawString("Earth: " + player.weaponMasteriesGrade[2], boxx[0] + boxx[2]/4 - 10, boxx[1] + boxx[3]/6 + 20);
				g.drawString("Dark: " + player.weaponMasteriesGrade[3], boxx[0] + boxx[2]/2 + 10, boxx[1] + boxx[3]/6 + 20);
			} else {
				g.drawString("Swords: " + player.weaponMasteriesGrade[0], boxx[0] + boxx[2]/4 - 10, boxx[1] + boxx[3]/6);
				g.drawString("Lances: " + player.weaponMasteriesGrade[1], boxx[0] + boxx[2]/2 + 10, boxx[1] + boxx[3]/6);
				g.drawString("Axes: " + player.weaponMasteriesGrade[2], boxx[0] + boxx[2]/4 - 10, boxx[1] + boxx[3]/6 + 20);
				g.drawString("Bows: " + player.weaponMasteriesGrade[3], boxx[0] + boxx[2]/2 + 10, boxx[1] + boxx[3]/6 + 20);
			}
			// draw contents of wallet
			g.setFont(new Font("Times New Roman", Font.BOLD, 35));
			g.drawImage(player.image, boxx[0] + 2, boxx[1] + 5, boxx[2]/5, boxx[3]/4 - 30, null);
			g.drawString(player.name, boxx[0] + boxx[2]/2 - 8*player.name.length(), boxx[1] + boxx[3]/18);
			for (int i = 0; i < player.wallet.weapons.size(); i++) {
				g.drawString(player.wallet.weapons.get(i).name, boxx[0] + 20, weaponStartY + weaponSpacing*i);
				g.drawImage(player.wallet.weapons.get(i).weaponTypeImage(),  boxx[0] + 2*boxx[2]/3 + Game.scale, weaponStartY + weaponSpacing*i - 20, 25, 25, null);
			}
			for (int i = 0; i < player.wallet.utilities.size(); i++) {
				g.drawString(player.wallet.utilities.get(i).name, boxx[0] + 20, weaponStartY + weaponSpacing*i + weaponSpacing*player.wallet.weapons.size());
				g.drawImage(player.wallet.utilities.get(i).getImage(),  boxx[0] + 2*boxx[2]/3 + Game.scale,  weaponStartY + weaponSpacing*i + weaponSpacing*player.wallet.weapons.size() - 20, 25, 25, null);
			}
			
		} else {
			// check if it is our convoy
			if (selectedIndex == allySelection.size()) {
				//we are hovering over the convoy
				g.setColor(Color.white);
				// spacing between indexed weapons
				int weaponSpacing = 27;
				
				g.drawString("Convoy", boxx[0] + boxx[2]/2 - 55, boxx[1] + boxx[3]/18);
				g.drawString("Holds Excess Items", boxx[0] + 18, boxx[1] + boxx[3]/8);
				g.setFont(new Font("Times New Roman", Font.BOLD, 24));
				// display convoy items
				for (int i = 0; i < convoy.convoyItems.size(); i++) {
					g.drawString(convoy.convoyItems.get(i).name, boxx[0] + 20, weaponStartY - 20 + weaponSpacing*i);
				}
			}
		}
		
		// render our convoy
		// when we get internet find a picture of a convoy tent thing
		//g.drawImage(convoy.image, startX + xInc*(imageScale + spacing), startY + yInc*(imageScale+spacing), imageScale, imageScale, null);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 35));
		g.drawRect(startX + xInc*(imageScale + xSpacing) - 2, startY + yInc*(imageScale+spacing) - 2, imageScale, imageScale);
		g.setColor(Color.CYAN);
		g.drawString("Convoy", startX + xInc*(imageScale + xSpacing) + imageScale + 8, startY + yInc*(imageScale+spacing) + imageScale/2 + 5);
		
		// render our trade menu
		if (game.tradeMenu != null) {
			game.tradeMenu.renderBattlePrepMenu(g);
		} else {
		/*	if (!renderPlayerAdvanced) {
				game.playerGFX.render(g);
			} else {
				game.playerGFX.renderAdv(g);
			}*/
		}
		
		
	}
	
	
	
	/** Sets the chapter's allys to our deployed allys */
	public void startChapter() {
		
		organizer.allys.clear();
		organizer.allys.addAll(deployedAllys);
		for (int i = 0; i < organizer.totalAllies.size(); i++) {
			if (organizer.totalAllies.get(i).spawnsOnChaptStart) {
				organizer.allys.add(organizer.totalAllies.get(i));
			}
		}
		for (int i = 0; i < this.allyPositionOptions.size(); i++) {
			allyPositionOptions.get(i).allySpawnTile = false;
		}
		deployedAllys.clear();
		organizer.currentMap.setScreenToLord();
		game.setGameState(STATE.Game);
		organizer.currentMap.checkForCutScene();
		
	}
	/** Either deploys or undeploys a unit when selected */
	public void flipDeployed() {
		AllyPlayer player = allySelection.get(selectedIndex);
		if (deployedAllys.contains(player)) {
			if (game.onHectorMode) {
				if (player.name.equalsIgnoreCase("Hector")) return;
			} else {
				if (player.name.equalsIgnoreCase("Ike")) return;
			}
			deployedAllys.remove(player);
			for (int i = 0; i < this.allyPositionOptions.size(); i++) {
				Tile t = allyPositionOptions.get(i);
				if (t.carrier == null) continue;
				if (t.carrier.name.equalsIgnoreCase(player.name)) {
					allyPositionOptions.get(i).setCarrier(null);
					break;
				}
			}
		} else {
			if (deployedAllys.size() < this.MAX_DEPLOYABLE) {
				if (game.onHectorMode) {
					if (player.name.equalsIgnoreCase("Hector")) return;
				} else {
					if (player.name.equalsIgnoreCase("Ike")) return;
				}
				deployedAllys.add(player);
				for (int i = 0; i < allyPositionOptions.size(); i++) {
					Tile t = allyPositionOptions.get(i);
					if (t.carrier == null) {
						t.setCarrier(player);
						player.setCurrentTile(t);
						break;
					}
				}
			}
		}
	}
	
	public void incSelectedIndex(int amount) {
		
		int max = 0;
		if (this.prepState == PreparationState.MainMenu) max = menuOptions.length;
		else if (this.prepState == PreparationState.AllySelection) max = allySelection.size();
		else if (this.prepState == PreparationState.AllyInfo) {
			max = allySelection.size() + 1;
		}
		selectedIndex += amount;
		if (selectedIndex < 0) selectedIndex = max - 1;
		else if (selectedIndex >= max) selectedIndex = 0;
	}
	/** The number of players in each column */
	public int nCols() {
		return nCols;
	}
	
}
