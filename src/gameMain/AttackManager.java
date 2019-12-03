package gameMain;

import java.awt.Graphics;
import java.util.Random;

import characters.Player;

public class AttackManager {

	/** p1 didHit, p1 didDoubleHit, p2 didHit, p2 didDoubleHit */
	public boolean[] attackHitData;
	/**p1 didCrit, p1DidCritSecond,  p2didCrit, p2didCritSecond*/
	public boolean[] attackCritData;
	/** p2 didAttack, p1 didDouble, p2 didDouble */
	public boolean[] attackFrequencyData;
	/** p1didHit, p1didDoubleHit, p2didHit, p2didDoubleHit*/
	private int[] critRNGs, hitRNGs;
	/**
	 * The speed advantage a unit must have over another in order to double attack
	 * them
	 */
	public final static int DOUBLE_SPEED = 4;
	/** The RNG responsible for many things */
	public Random r;

	public AttackManager() {
		attackHitData = new boolean[4];
		attackCritData = new boolean[4];
		attackFrequencyData = new boolean[3];
		critRNGs = new int[4];
		hitRNGs = new int[4];
		r = new Random();
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
			attacker.setMAU(false);
			return;
		}
		boolean canDefend = true; // whether or not the defender has a chance to attack back
		if (distApart > defender.equiptItem.range) canDefend = false;
		attackFrequencyData[0] = canDefend;
		int pureSpeedAdv = Math.abs(attacker.SP - defender.SP); // the raw difference in speed regardless of sign
		int attackerDMG, defenderDMG; // we'll add in weapon triangles later
		
		if (attacker.equiptItem.category.equalsIgnoreCase("Magical")) attackerDMG = attacker.damage - defender.RES;
		else attackerDMG = attacker.damage - defender.DEF;
		
		if (defender.equiptItem.category.equalsIgnoreCase("Magical")) defenderDMG = defender.damage - attacker.RES;
		else defenderDMG = defender.damage - attacker.DEF;
		
		int attackerHit = attacker.hit - defender.avoid;
		int defenderHit = defender.hit - attacker.avoid;
		int attackerCrit = attacker.crit - defender.LCK;
		int defenderCrit = defender.crit - attacker.LCK;
		
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
	}

	public void renderAttackAnimation(Graphics g) {

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
