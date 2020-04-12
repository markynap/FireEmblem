package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import characters.Player;
import gameMain.Game.STATE;

public class AttackManager {

	/** p1 didHit, p1 didDoubleHit, p2 didHit, p2 didDoubleHit */
	public boolean[] attackHitData;
	/**p1 didCrit, p1DidCritSecond,  p2didCrit, p2didCritSecond*/
	public boolean[] attackCritData;
	/** p2 didAttack, p1 didDouble, p2 didDouble */
	public boolean[] attackFrequencyData;
	/** p1didHit, p1didDoubleHit, p2didHit, p2didDoubleHit*/
	private int[] critRNGs, hitRNGs;
	
	private Game game;
	
	private int attackerXVel, defenderXVel, attackerX, defenderX;
	
	private int attackerDMG, defenderDMG, attackerHit, attackerCrit, defenderHit, defenderCrit;
	
	public Player attacker, defender;
	/**
	 * The speed advantage a unit must have over another in order to double attack
	 * them
	 */
	public final static int DOUBLE_SPEED = 4;
	/** The RNG responsible for many things */
	public Random r;

	public AttackManager(Game game) {
		attackHitData = new boolean[4];
		attackCritData = new boolean[4];
		attackFrequencyData = new boolean[3];
		critRNGs = new int[4];
		hitRNGs = new int[4];
		r = new Random();
		setPositions();
		this.game = game;
	}

	/**
	 * Runs an attack situation between two players, the attacker goes first the
	 * attacker is always in range, defender may or may not be
	 * 
	 * @param attacker
	 * @param defender
	 */
	public void Attack(Player attacker, Player defender) {
		clearAll();

		int distApart = (Math.abs(attacker.xPos - defender.xPos)) + Math.abs(attacker.yPos - defender.yPos);
		if (distApart > attacker.equiptItem.range) {
		//	attacker.setMAU(false);
			System.out.println(attacker.toString() + " tried attacking " + defender.toString() + " but distance apart is " + distApart);
			System.out.println("attacker is on tile: " + attacker.currentTile.toSring());
			System.out.println("defender is on tile: " + defender.currentTile.toSring());
			return;
		}
		
		this.attacker = attacker;
		this.defender = defender;
		
		System.out.println(attacker.toString() + " is attacking " + defender.toString());
		
		boolean canDefend = true; // whether or not the defender has a chance to attack back
		if (distApart > defender.equiptItem.range) canDefend = false;
		attackFrequencyData[0] = canDefend;
		int pureSpeedAdv = Math.abs(attacker.SP - defender.SP); // the raw difference in speed regardless of sign
		
		if (attacker.equiptItem.category.equalsIgnoreCase("Magical")) attackerDMG = attacker.damage - defender.RES;
		else attackerDMG = attacker.damage - defender.defense;
		
		if (defender.equiptItem.category.equalsIgnoreCase("Magical")) defenderDMG = defender.damage - attacker.RES;
		else defenderDMG = defender.damage - attacker.defense;
		
		attackerHit = attacker.hit - defender.avoid;
		defenderHit = defender.hit - attacker.avoid;
		attackerCrit = attacker.crit - defender.LCK;
		defenderCrit = defender.crit - attacker.LCK;
		
		for (int i = 0; i < 4; i++) {
			hitRNGs[i] = r.nextInt(101);
			critRNGs[i] = r.nextInt(101);
		}
	
		if (!canDefend) { //if the enemy cannot defend themselves
			if (attacker.SP - defender.SP >= 4) { //attacker attacks twice
				attackFrequencyData[1] = true;
				attackFrequencyData[2] = false;
				
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0);
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 1);
				
			} else { //attacks once
				attackFrequencyData[1] = false;
				attackFrequencyData[2] = false;
				
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0);
			}
			attacker.setMAU(false);
			setForAttackStage(attacker, defender);
			return;
		}
		//must set EXP too

		if (pureSpeedAdv >= DOUBLE_SPEED) { // Somebody is attacking twice
			
			if (attacker.SP > defender.SP) { // Attacker attacks twice
				attackFrequencyData[1] = true;
				attackFrequencyData[2] = false;
				
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0);
				EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 0);
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 1);
				
			} else { // defender attacks twice
				attackFrequencyData[1] = false;
				attackFrequencyData[2] = true;
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0);
				EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 0);
				EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 1);
				
			}
			// After double attacks are over, not sure if anything unique happens

		} else { // it is only a one attack each battle
			attackFrequencyData[1] = false;
			attackFrequencyData[2] = false;
			Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0);
			EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 0);
			
		}
		// After the attacks are over

		attacker.setMAU(false);
		setForAttackStage(attacker, defender);
	}
	
	public void setForAttackStage(Player attacker, Player defender) {
		game.chapterOrganizer.currentMap.nullAttackMenu();
		setPositions();
		game.setGameState(STATE.AttackStage);
	}
	/**
	 * Sets the attacker and defender back to starting positions
	 */
	private void setPositions() {
		this.attackerX = 50;
		this.defenderX = Game.WIDTH - 200;
	}

	public void renderAttackAnimation(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0,0, Game.WIDTH, Game.HEIGHT);
		int picWidth = 180;
		int picHeight = 350;
		int picY = Game.HEIGHT/2 - (picHeight/2);
		int attHitMiss = Game.WIDTH/2 - 180;
		int defHitMiss = Game.WIDTH/2 + 50;
		int messageY = picY - 60;
		int message2Y = picY - 25;
		
		drawAttackBox(g);
		drawNames(g);
		g.drawImage(attacker.image, attackerX += attackerXVel, picY, picWidth, picHeight, null);
		g.drawImage(defender.image, defenderX -= defenderXVel, picY, picWidth, picHeight, null);
		g.setFont(new Font("Times New Roman", Font.BOLD, 36));
		g.setColor(Color.red);
		defenderXVel = 1;
		attackerXVel = 1;
		if (attackerX >= (Game.WIDTH/2 - 250)) attackerXVel = 0;
		if (defenderX <= Game.WIDTH/2) defenderXVel = 0;
		if (!attackFrequencyData[0]) defenderXVel = 0;
		
		if (attackFrequencyData[1]) { //attacker did double
			
			if (attackHitData[0]) { //attacker hit the first
				if (attackHitData[1]) { //attacker hit the second, both shots hit
					g.drawString("HIT", attHitMiss, messageY);
					g.drawString("HIT", attHitMiss, message2Y);
				} else { //attacker hit the first but missed the second
					g.drawString("HIT", attHitMiss, messageY);
					g.drawString("MISS", attHitMiss, message2Y);
				}
			} else { //attacker missed the first shot
				if (attackHitData[1]) { //attacker hit the second shot but missed the first
					g.drawString("MISS", attHitMiss, messageY);
					g.drawString("HIT", attHitMiss, message2Y);
				} else { //attacker missed both shots!
					g.drawString("MISS", attHitMiss, messageY);
					g.drawString("MISS", attHitMiss, message2Y);
				}
			}
			
			if (!attackFrequencyData[0]) { //defender did not attack at all
				
			} else { //defender did attack, but cannot double cause attacker doubled
				if (attackHitData[2]) { //defender hit
					g.drawString("HIT", defHitMiss, messageY);
				} else { //defender missed
					g.drawString("HIT", defHitMiss, messageY);
				}
			}
			
		} else { //attacker did not double
			
			if (attackHitData[0]) { //attacker hit the first
				g.drawString("HIT", attHitMiss, messageY);
			} else { //attacker missed the first
				g.drawString("MISS", attHitMiss, messageY);
			}
			
			if (!attackFrequencyData[0]) { //defender did not attack at all
				
			} else { //defender did attack
				if (attackFrequencyData[2]) {//defender did double
					
					if (attackHitData[2]) { //defender hit the first!
						if (attackHitData[3]) { //defender hit the second too! both hit
							g.drawString("HIT", defHitMiss, messageY);
							g.drawString("HIT", defHitMiss, message2Y);
						} else { //defender missed the second but hit the first
							g.drawString("HIT", defHitMiss, messageY);
							g.drawString("MISS", defHitMiss, message2Y);
						}
					} else { //defender missed the first
						if (attackHitData[3]) { //defender missed the first but hit the second
							g.drawString("MISS", defHitMiss, messageY);
							g.drawString("HIT", defHitMiss, message2Y);
						} else { // defender missed both shots
							g.drawString("MISS", defHitMiss, messageY);
							g.drawString("MISS", defHitMiss, message2Y);
						}
					}
					
				} else { //defender only attacked once
					
					if (attackHitData[2]) { //defender hit
						g.drawString("HIT", defHitMiss, messageY);
					} else { //defender missed
						g.drawString("MISS", defHitMiss, messageY);
					}
					
				}
			}
			
		}
		
	}
	
	private void drawAttackBox(Graphics g) {
		g.setColor(Color.black);
		int boxY = Game.WIDTH - 450;
		g.fillRect(0, boxY, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.WHITE);
		g.drawLine(Game.WIDTH/2, boxY, Game.WIDTH/2, Game.HEIGHT);
		g.setColor(Color.RED);
		g.setFont(new Font("Times New Roman", Font.BOLD, 45));
		g.drawString("Hit: " + atLeastZero(attackerHit) , 5, boxY + 50);
		g.drawString("DMG: " +  atLeastZero(attackerDMG) , 5, boxY + 110);
		g.drawString("Crit: " +  atLeastZero(attackerCrit) , 5, boxY + 170);
		g.drawString("Hit: " +  atLeastZero(defenderHit) , Game.WIDTH/2 + 5, boxY + 50);
		g.drawString("DMG: " +  atLeastZero(defenderDMG) , Game.WIDTH/2 + 5, boxY + 110);
		g.drawString("Crit: " +  atLeastZero(defenderCrit) , Game.WIDTH/2 + 5, boxY + 170);
	}
	private int atLeastZero(int val) {
		return Math.max(val, 0);
	}
	
	private void drawNames(Graphics g) {
		g.setColor(Color.cyan);
		int box2X = Game.WIDTH - 400;
		int boxW = 400;
		int boxH = 200;
		int thickness = 3;
		for (int i = 0; i < thickness; i++) {
			g.drawRect(0 + i, 0 + i, boxW, boxH);
			g.drawRect(box2X + i, 0 + i, boxW, boxH);
		}
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 80));
		g.drawString(attacker.name, 10, 60);
		g.drawString(defender.name, box2X + 10, 60);
		for (int i = 0; i < attacker.HP; i++) {
			drawHealthRect(20 + (5*i), 120, g, attacker, i);
		}
		for (int i = 0; i < defender.HP; i++) {
			drawHealthRect(box2X + 20 + (5*i), 120, g, defender, i);
		}
		
	}
	
	private void drawHealthRect(int x, int y, Graphics g, Player player, int index) {
		g.setColor(Color.green);
		g.drawRect(x, y, 4, 9);
		if (index < player.currentHP) {
		g.setColor(Color.white);
		} else {
			g.setColor(Color.black);
		}
		g.fillRect(x+1, y+1, 3, 8);
	}
	
	public void clearAll() {
		for (int i = 0; i < 4; i++) {
			attackHitData[i] = false;
			attackCritData[i] = false;
			if (i == 3) continue;
			attackFrequencyData[i] = false;
		}
	}
	
	private void Allyattack(Player attacker, int attackerDMG, int attackerHit, int attackerCrit,  Player defender, int numATT) {
		int allyEXP = 10 - (2*(attacker.level - defender.level));
		int defenderEXP = 1;
		double levelDiff = defender.level/attacker.level;
		int hitScale = (int)(15 * levelDiff);
		if (attackerHit >= hitRNGs[numATT]) { //we hit
			attackHitData[numATT] = true;
			if (attackerCrit >= critRNGs[numATT]) { //we crit!
				attackCritData[numATT] = true;
				if (defender.currentHP - (3 * attackerDMG) <= 0) {
					allyEXP += 3*hitScale;
				} else {
					allyEXP += 2*hitScale;
				}
				defender.takeDamage(3 * attackerDMG);
			} else { 					//no crit, just hit
				if (defender.currentHP - attackerDMG <= 0) { //if this blow kills them
					allyEXP += 3 * hitScale;
				} else {
					allyEXP += hitScale;
				}
				defender.takeDamage(attackerDMG);
			}
			attacker.equiptItem.duration--;
		} else { //we miss
			defenderEXP += 15;
		}
		if (allyEXP <= 0) allyEXP = 1;
		if (allyEXP >= 101) allyEXP = 100;
		attacker.addEXP(allyEXP);
		defender.addEXP(defenderEXP);
	}
	private void EnemyAttack(Player enemy, int enemyDMG, int enemyHit, int enemyCrit, Player ally, int numATT) {
		Allyattack(enemy, enemyDMG, enemyHit, enemyCrit, ally, 2 + numATT);
	}
}
