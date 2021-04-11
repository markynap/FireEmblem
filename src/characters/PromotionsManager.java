package characters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import gameMain.Game;
import gameMain.Game.STATE;

/** In charge of Choosing Between, Assigning, Accomplishing, and Rendering all Promotion operations
 *  throughout this game. Will boost player's stats when instructed, set new skills and swap Class name
 * @author mark
 *
 */
public class PromotionsManager {
	
	/** The Game running this promotion manager */
	private Game game;
	/** Player currently being promoted */
	private Player player;
	/** Player's options for which class they want to promote to */
	private ArrayList<String> promotionChoices;
	/** List of benefits / detriments of swapping to this class */
	private ArrayList<ArrayList<String>> benefits;
	/** Background of the promotion */
	private Image bgImage;
	/** x, y, width, height */
	private int[] playerPos = {5, Game.HEIGHT/2 - 2*Game.scale, Game.WIDTH/4, Game.HEIGHT/2};
	/** x, y, width, height */
	private int[] boxPos = {Game.WIDTH/6, Game.HEIGHT/8, 3*Game.WIDTH/4, 3*Game.HEIGHT/4};
	/** initial text value locations for reference */
	private int startX = boxPos[0] + 10, startY = boxPos[1] + 35, spacing = 40;
	/** The option for which class we are promoting to */
	private int selectedIndex;
	/** True if we return to EnemyPhase afterward */
	private boolean inEnemyPhase;
	
	/** Creates a new Promotions Manager with no Player selected for promoting */
	public PromotionsManager(Game game) {
		this.game = game;
		promotionChoices = new ArrayList<>();
		benefits = new ArrayList<>();
		this.bgImage = Game.IM.getImage("/cutScenes/cutSceneBG/background1.png");
	}
	/** Draws the promotion of the set Player unit */
	public void render(Graphics g) {
		if (player == null) return;
		
		g.drawImage(bgImage, 0, 0, Game.WIDTH, Game.HEIGHT, null);
		g.drawImage(player.image, playerPos[0],playerPos[1],playerPos[2],playerPos[3] , null);
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font("Times New Roman", Font.BOLD, 34));
		g.fillRect(boxPos[0], boxPos[1], boxPos[2], boxPos[3]);
		
		g.setColor(Color.WHITE);
		g.drawLine(boxPos[0] + boxPos[2]/2, boxPos[1], boxPos[0] + boxPos[2]/2, boxPos[1] + boxPos[3]);
		g.drawString(player.name, startX, startY);
		
		g.drawString(player.Class, startX, startY + spacing);
		
		
		g.drawString("HP: " + player.HP, startX, startY + 3*spacing);
		if (player.isMagicUser)	g.drawString("MAG: " + player.STR, startX, startY + 4*spacing);
		else g.drawString("STR: " + player.STR, startX, startY + 4*spacing);

		g.drawString("SK: " + player.SK, startX, startY + 5*spacing);
		g.drawString("SP: " + player.SP, startX, startY + 6*spacing);
		g.drawString("LCK: " + player.LCK, startX, startY + 7*spacing);
		g.drawString("DEF: " + player.DEF, startX, startY + 8*spacing);
		g.drawString("RES: " + player.RES, startX, startY + 9*spacing);
		g.drawString("MOV: " + player.MOV, startX, startY + 10*spacing);
		g.drawString("CON: " + player.CON, startX, startY + 11*spacing);

		for (int i = 0; i < 9; i++) {
			if (!benefits.get(selectedIndex).get(i).equalsIgnoreCase("0")) {
				g.drawString("  + " + benefits.get(selectedIndex).get(i), startX + 130, startY + 3*spacing + i*spacing);
			}
		}
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 30));
		int size = benefits.get(selectedIndex).size();
		if (size > 9) {
			for (int i = 9; i < size; i++) {
			if (benefits.get(selectedIndex).get(i).equalsIgnoreCase("None")) continue;	
				g.drawString(benefits.get(selectedIndex).get(i), startX + boxPos[2]/2 + spacing/4, startY + 6*spacing + (i-9)*spacing);
			}
		}
		g.drawString(promotionChoices.get(0), startX + boxPos[2]/2 + spacing/2, startY + 2*spacing);
		g.drawString(promotionChoices.get(1), startX + boxPos[2]/2 + spacing/2, startY + 4*spacing);
		
		
		g.setColor(Color.green);
		g.drawRect(startX + boxPos[2]/2 + spacing/2, startY + (2+(2*selectedIndex))*spacing - spacing + 5, 200, 40);
		
	}
	
	
	/** Moves the cursor up or down */
	public void incSelectedIndex(int amount) {
		selectedIndex += amount;
		if (selectedIndex < 0) selectedIndex = 1;
		else if (selectedIndex > 1) selectedIndex = 0;
	}
	
	/** Sets the player for the next Promotion Event, as well as the phase we are in and should return to */
	public void setPlayer(Player player, boolean inEnemyPhase) {
		this.player = player;
		this.inEnemyPhase = inEnemyPhase;
		setChoicesForPlayer(player);
	}
	/** Sets this player's promotion choices and benefits for each class */
	public void setChoicesForPlayer(Player player) {
		
		promotionChoices.clear();
		benefits.clear();
		if (player == null) return;
		
		ArrayList<String> firstbenefits = new ArrayList<>();
		ArrayList<String> secondbenefits = new ArrayList<>();
		
		
		try {
			Scanner reader = new Scanner(new File("res//designInfo//promotionData"));
			String line;
			String[] lineParts;
			
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				if (line.equalsIgnoreCase(player.name)) {
					//we are on the stats for our player
					
					while (reader.hasNextLine()) {
						line = reader.nextLine();
						if (line.equalsIgnoreCase("END")) break;
						//all promotional options and stat buffs
						// could have 2 or 1 part
						lineParts = line.split(":");
						if (lineParts.length == 3) {
							
							if (lineParts[2].equalsIgnoreCase("optionend")) {
								promotionChoices.add(lineParts[0]);
								promotionChoices.add(lineParts[1]);
							}
							
						} else if (lineParts.length == 2) {
							
							firstbenefits.add(lineParts[0]);
							secondbenefits.add(lineParts[1]);
							
						}
					}
					benefits.add(firstbenefits);
					benefits.add(secondbenefits);
					break;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Cant find file in res//designInfo//promotionData");
		}
		
	}
	
	/** When user selects a Class they would like to promote to */
	public void selectPromotion() {
		
		player.Class = promotionChoices.get(selectedIndex);
		player.isPromoted = true;
		
		for (int i = 0; i < 9; i++) {
			player.stats[i] += Integer.valueOf(benefits.get(selectedIndex).get(i));
		}
		
		player.resetStatsFromStatsArray();
		
		switch (player.name) {
		
		
		case "Ike":
			if (selectedIndex == 0) {
				player.skill = new Skill("Momento");
				player.MOV = 8;
			}
			break;
		
		case "Nino":
			if (selectedIndex == 0) {
				player.isHealer = true;
				player.duoWeaponHeal = true;
				player.skill = new Skill("Divine Wellness");
			} else {
				for (int i = 1; i < 4; i++) {
					player.weaponMasteriesGrade[i] = 'A';
				}
				player.skill = new Skill("Teraform");
			}
			break;
			
		case "Kent":
			if (selectedIndex == 1) {
				player.skill = new Skill("Great Shield");
			}
			break;
		
		case "Raymond":
			if (selectedIndex == 0) {
				player.isHealer = true;
				player.duoWeaponHeal = true;
				player.skill = new Skill("Divine Wellness");
				player.weaponMasteriesGrade[1] = 'B';
			} else {
				player.isHealer = true;
				player.duoWeaponHeal = false;
				player.staffExtention = 2;
			}
			break;
		
		case "Evelynn":
			if (selectedIndex == 0) {
				player.skill = new Skill("Summoning");
			} else {
				player.isHealer = true;
				player.duoWeaponHeal = true;
			}
			break;
		
		case "Bard":
			break;
		
		case "Florina":
			break;
		
		case "Hector":
			if (selectedIndex == 1) {
				player.skill = new Skill("Momento");
			}
			break;
		
		case "Volke":
			if (selectedIndex == 1) {
				player.skill = new Skill("Assassination");
			}
			break;
		
		case "Wolf":
			if (selectedIndex == 1) {
				player.skill = new Skill("Momento");
				player.weaponMasteriesGrade[0] = 'A';
			} else {
				player.bowExtention = 1;
			}
			break;
		
		case "Kahlan":
			if (selectedIndex == 0) {
				player.skill = new Skill("Condar");
			} else {
				player.confessionRange = 5;
			}
			break;
		
		case "Guy":
			break;
			
		case "Heath":
			if (selectedIndex == 0) {
				player.skill = new Skill("Bone Crusher");
			}
			break;
		
		case "Merric":
			if (selectedIndex == 0) {
				player.isHealer = true;
				player.duoWeaponHeal = true;
				player.skill = new Skill("Divine Wellness");
			} else {
				for (int i = 1; i < 4; i++) {
					player.weaponMasteriesGrade[i] = 'A';
				}
				player.skill = new Skill("Teraform");
			}
			break;
		
		case "Priscilla":
			if (selectedIndex == 0) {
				// Valkyrie
				player.weaponMasteriesGrade[0] = 'B';
				player.isHealer = true;
				player.duoWeaponHeal = true;
				player.isMagicUser = true;
				
			} else {
				// Paladin
				for (int i = 0; i < 3; i++) {
					player.weaponMasteriesGrade[i] = 'C';
					player.weaponMasteries[i] = 80 - 15*i;
				}
				player.isHealer = false;
				player.duoWeaponHeal = false;
				player.isMagicUser = false;
			}
		}
		if (inEnemyPhase) {
			game.setGameState(STATE.EnemyPhase);
		} else {
			game.setGameState(STATE.Game);
		}
	}

}
