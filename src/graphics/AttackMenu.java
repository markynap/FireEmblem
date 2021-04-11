package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import gameMain.Game;

public class AttackMenu {
	/** Menu positional data */
	private int xPos, yPos, menuH, menuW;
	/** Player's involved in combat */
	public Player attacker, defender;
	/** Player damage information */
	private int attDamage, attHit, attCrit, defDamage, defHit, defCrit;
	/** True if attacker has the weapon triangle advantage */
	private boolean attackerHasAdvantage;
	/** True if there exists no advantage due to the Weapon Triangle in this combat */
	private boolean noAdvantage;
	/** True if the defender does not attack back in this fight */
	private boolean defenderNoStrike;
	/** The total amount of damage attacker/defender will take if all hits successfully land */
	private int attDMGTaken, defDMGTaken;
	/** True if either attacker or defender will double attack the opposing unit */
	private boolean attDouble, defDouble;
	
	public AttackMenu(Player attacker, Player defender) {
		
		this.xPos = 0;
		this.yPos = Game.HEIGHT - Game.HEIGHT/3;
		this.menuW = Game.WIDTH;
		this.menuH = Game.HEIGHT/3;
		this.attackerHasAdvantage = false;
		this.noAdvantage = true;
		this.attacker = attacker;
		this.defender = defender;
		
		attDamage = attacker.getEffectiveDamage(defender);
		attHit = attacker.getEffectiveHit(defender);
		attCrit = attacker.getEffectiveCrit(defender);
		defDamage = defender.getEffectiveDamage(attacker);
		defHit = defender.getEffectiveHit(attacker);
		defCrit = defender.getEffectiveCrit(attacker);
		
		// whether or not the defender has a chance to attack back
		if (attacker.getTrueDistanceFromPlayer(defender) > defender.equiptItem.range) {
			// defender cannot attack back, out of their attack range
			defDamage = 0;
			defHit = 0;
			defCrit = 0;
			defenderNoStrike = true;
			attDMGTaken = 0;
			
		} else {
			// defender can strike back, calculate total damage they will do
			if (defHit > 0) {
				if (defender.willDouble(attacker)) {
					attDMGTaken = 2*defDamage;
				} else {
					attDMGTaken = defDamage;
				}
			}
		}
		
		if (attacker.willDouble(defender)) {
			defDMGTaken = 2*attDamage;
			attDouble = true;
		} else {
			defDMGTaken = attDamage;
			attDouble = false;
			if (defender.willDouble(attacker)) {
				defDouble = true;
			}
		}
		
		
		int[] stats = Player.getWeaponTriangleStats(attacker, defender);
		if (stats[0] == 0) {
			if (attacker.equiptItem.weaponType.equalsIgnoreCase("Bow") && defender.isFlier) {
				noAdvantage = false;
				attackerHasAdvantage = true;
			} else if (attacker.equiptItem.name.equalsIgnoreCase("ArmorSlayer") && defender.isArmoredUnit()) {
				noAdvantage = false;
				attackerHasAdvantage = true;
			}
		} else {
			noAdvantage = false;
			if (stats[0] > 0) attackerHasAdvantage = true;
		}
	}
	
	public void render(Graphics g) {
		
		// draw our attack data box
		g.setColor(Color.white);
		g.drawRect(xPos, yPos, menuW, menuH);
		g.setColor(Color.cyan);
		g.fillRect(xPos+1, yPos+1, menuW/2, menuH);
		g.setColor(Color.red);
		g.fillRect(xPos + menuW/2 + 1, yPos + 1, menuW/2, menuH);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		g.drawLine(xPos + menuW/2, yPos, xPos + menuW/2, yPos + menuH);
		g.setColor(Color.BLACK);

		int spacing = 32;
		int startY = yPos + 19;
		
		// fighter information
		
		// fighters' names
		g.drawString(attacker.name, xPos - 25 + menuW/4- 4*attacker.name.length(), startY + 4);
		g.drawString(defender.name, xPos + 3*menuW/4 - 25 - 4*defender.name.length(), startY + 4);
		// fighters' images
		g.drawImage(attacker.image, Game.WIDTH/4 - Game.scale/2, yPos - Game.HEIGHT/5, Game.WIDTH/7, Game.HEIGHT/5, null);
		g.drawImage(defender.image, 3*Game.WIDTH/4 - Game.scale/2, yPos - Game.HEIGHT/5, Game.WIDTH/7, Game.HEIGHT/5, null);

		// weapon type information
		g.setFont(new Font("Times New Roman", Font.BOLD, 24));
		g.drawString("Weapon Type: (" + attacker.equiptItem.weaponType + ")", xPos + menuW/9 + 5, startY + 30);
		g.drawString("Weapon Type: (" + defender.equiptItem.weaponType + ")", xPos + menuW/2 + menuW/9, startY + 30);
		
		
		// advantage information, if weapon triangle advantage is here
		if (!noAdvantage) {
			if (attackerHasAdvantage) {
				drawUpArrow(g, xPos + menuW/2 - Game.scale/2, startY - 5);
				drawDownArrow(g, menuW - Game.scale/2 - 5, startY - 5);
			}
			else {
				drawUpArrow(g, xPos + menuW - Game.scale/2 - 5, startY - 5);
				drawDownArrow(g, xPos + menuW/2 - Game.scale/2, startY - 5);
			}
		}
		
			
		// fighters' attacking data
		g.setFont(new Font("Times New Roman", Font.BOLD, 27));
		int statStart = startY + 32;
		int attStatStartX = xPos + menuW/7 + 25;
		int defStatStartX = xPos + menuW/2 + menuW/7 + 25;
		
		// attacker's attacking stats
		g.drawString("Health: " + attacker.currentHP, attStatStartX, statStart + spacing);
		g.drawString("Hit: " + attHit, attStatStartX, statStart +(spacing*3));
		if (attDouble) {
			g.drawString("Damage: " + attDamage + "x2", attStatStartX, statStart + (spacing*2));
		} else {
			g.drawString("Damage: " + attDamage, attStatStartX, statStart + (spacing*2));
		}
		g.drawString("Crit: " + attCrit, attStatStartX, statStart + (spacing*4));
		
		// defender's attacking stats
		g.drawString("Health: " + defender.currentHP, defStatStartX, statStart + spacing);	
		if (defenderNoStrike) {
			g.drawString("Damage: --", defStatStartX, statStart + (spacing*2));
			g.drawString("Hit: --", defStatStartX, statStart + (spacing*3));
			g.drawString("Crit: --", defStatStartX, statStart + (spacing*4));
		} else {
			// defender is attacking back
			if (defDouble) {
				g.drawString("Damage: " + defDamage + "x2", defStatStartX, statStart + (spacing*2));
			} else {
				g.drawString("Damage: " + defDamage, defStatStartX, statStart + (spacing*2));
			}
			g.drawString("Hit: " + defHit, defStatStartX, statStart + (spacing*3));
			g.drawString("Crit: " + defCrit, defStatStartX, statStart + (spacing*4));
		}
		
		if (attDMGTaken > 0) {
			// write in red the amount of health damage the attacker will take
			g.setColor(Color.red);
			g.drawString("-" + attDMGTaken, attStatStartX + 2*Game.scale + 22, statStart + spacing);
		}
		
		if (defDMGTaken > 0) {
			// write in red the amount of health damage the defender will take
			g.setColor(Color.CYAN);
			g.drawString("-" + defDMGTaken, defStatStartX + 2*Game.scale + 22, statStart + spacing);	
		}
		
	}

	
	
	private void drawUpArrow(Graphics g, int startX, int startY) {
		g.setColor(Color.black);
		for (int i = 0; i < 5; i++) {
		g.drawLine(startX + i, startY + 26, startX + i, startY);
		g.drawLine(startX - 14 + i, startY + 10, startX + i, startY);
		g.drawLine(startX + 14 + i, startY + 10, startX + i, startY);
		}
	}
	
	private void drawDownArrow(Graphics g, int startX, int startY) {
		g.setColor(Color.black);
		for (int i = 0; i < 4; i++) {
		g.drawLine(startX + i, startY, startX + i, startY + 26);
		g.drawLine(startX - 10 + i, startY + 14, startX + i, startY + 26);
		g.drawLine(startX + 10 + i, startY + 14, startX + i, startY + 26);
		}
	}
}
