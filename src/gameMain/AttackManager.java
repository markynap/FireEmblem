package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import characters.Player;
import characters.Skill;
import extras.TimeKeeper;
import gameMain.Game.DIFFICULTY;
import gameMain.Game.STATE;
import items.UtilityItem;

public class AttackManager {

	/** p1 didHit, p1 didDoubleHit, p2 didHit, p2 didDoubleHit */
	public boolean[] attackHitData;
	/**p1 didCrit, p1DidCritSecond,  p2didCrit, p2didCritSecond*/
	public boolean[] attackCritData;
	/** p2 didAttack, p1 didDouble, p2 didDouble */
	public boolean[] attackFrequencyData;
	/** p1didHit, p1didDoubleHit, p2didHit, p2didDoubleHit*/
	private int[] critRNGs, hitRNGs;
	/** attacker used combat skill, defender used combat skill */
	private boolean[] skillUses;
	
	private final long MAX_ANIMATION_DURATION = 1200;

	private long current_Animation_Duration;
	
	private Game game;
	
	private int attackerXVel, defenderXVel, attackerX, defenderX;
	
	private int attackerMaxX = Game.WIDTH/2 - 250, defenderMaxX = Game.WIDTH/2;
	
	private int attackerDMG, defenderDMG, attackerHit, attackerCrit, defenderHit, defenderCrit;
	
	public Player attacker, defender;
	
	private boolean attDead;
	
	private int attEXP, defEXP;
	/** Number of times our character has lunged forward in our animation */
	private int numMoves;
	
	public int ADVANCE_SPEED = 2;
	
	private long animationEndTime;
	
	private boolean stoppedMoving;
	/** if initial wait = INITIAL_SETTER then system will pause for INITIAL_TIMER miliseconds */
	private int initialWait;
	
	private final int INITIAL_SETTER = 1;
	
	private final long INITIAL_TIMER = 50;
	
	/** Amount the EXP is damped due to the difficulty of the game */
	private double EXPDAMPER;
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
		skillUses = new boolean[2];
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

		clearAllData();
		if (!attacker.canAttack) {
			System.out.println("attacker cannot attack if their canAttack is False!!"); 
			return;
		}
		defender.drawScope = false;
		int distApart = game.getTrueDist(attacker.currentTile, defender.currentTile);
		if (distApart > attacker.equiptItem.range + attacker.bowExtention) {
			System.out.println(attacker.toString() + " tried attacking " + defender.toString() + " but distance apart is " + distApart);
			System.out.println("attacker is on tile: " + attacker.currentTile.toSring());
			System.out.println("defender is on tile: " + defender.currentTile.toSring());
			return;
		}
		
		this.attacker = attacker;
		this.defender = defender;
		
		boolean canDefend = true; // whether or not the defender has a chance to attack back
		
		if (distApart > defender.equiptItem.range) {
			// distance apart is greater than defender's range, so they cannot defend
			canDefend = false;
		
		}
		attackFrequencyData[0] = canDefend;
		
		int pureSpeedAdv = Math.abs(attacker.getEffectiveSpeed() - defender.getEffectiveSpeed());
		
		attackerDMG = attacker.getEffectiveDamage(defender);
		attackerHit = attacker.getEffectiveHit(defender);
		attackerCrit = attacker.getEffectiveCrit(defender);

		defenderHit = defender.getEffectiveHit(attacker);
		defenderDMG = defender.getEffectiveDamage(attacker);
		defenderCrit = defender.getEffectiveCrit(attacker);
		
		int attSkillChance = 0, defSkillChance = 0;
		
		// calculate % chance of landing a particular combat skill
		if (attacker.skill.isCombatSkill()) {
			attSkillChance = Skill.getCombatChance(attacker);
		} else if (attacker.skill.nameEquals("Higher Learning")) {
			attSkillChance = Skill.getHigherLearningChance(attacker);
		}
		// likewise with defender
		if (defender.skill.isCombatSkill()) {
			defSkillChance = Skill.getCombatChance(defender);
		} else if (defender.skill.nameEquals("Higher Learning")) {
			defSkillChance = Skill.getHigherLearningChance(defender);
		}
		// RNG for Skills
		int attRNG = r.nextInt(100) + 1;
		int defRNG = r.nextInt(100) + 1;
		
		// check our skills vs RNG
		if (attSkillChance > 0) {
			// we have a combat skill
			if (attRNG <= attSkillChance) { // it triggered based on RNG
				skillUses[0] = true;
				switch (attacker.skill.getName()) {
					
				case "Aether":
						attackerDMG = 3*attacker.getDamage()/2;
						attackerHit = 100;
						break;
				case "Bone Crusher":
						attackerDMG = attacker.getDamage();
						break;
				case "Sure Shot":
						attackerDMG = 3*attackerDMG/2;
						attackerHit = 100;
						break;
				case "Great Shield":
						defenderDMG = 0;
						break;
				
				case "Assassination":
						attackerDMG = defender.HP*2;
						break;
						
				default: break;		
				}
			}
		}
		
		if (defSkillChance > 0) {
			// defender has a combat skill
			if (defRNG <= defSkillChance) {
				skillUses[1] = true;
				switch (defender.skill.getName()) {
				
				case "Aether":
						defenderDMG = 3*defender.getDamage()/2;
						defenderHit = 100;
						break;
				case "Bone Crusher":
						defenderDMG = defender.getDamage();
						break;
				case "Sure Shot":
						defenderDMG = 3*defenderDMG/2;
						defenderHit = 100;
						break;
				case "Great Shield":
						attackerDMG = 0;
						break;
				
				case "Assassination":
						defenderDMG = attacker.HP*2;
						break;
						
				default: break;		
				}
			}
		}
		// calculate averaged RNGs for hit and crit data
		for (int i = 0; i < 4; i++) {
			hitRNGs[i] = (int)(0.5 +(r.nextInt(101) + r.nextInt(101))/2);
			critRNGs[i] = (int)(0.5 +(r.nextInt(101) + r.nextInt(101))/2);
		}
	
		if (!canDefend) { //if the enemy cannot defend themselves
			if (attacker.getEffectiveSpeed() - defender.getEffectiveSpeed() >= 4) { //attacker attacks twice
				attackFrequencyData[1] = true;
				attackFrequencyData[2] = false;
				
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0, true);
				if (!attDead) {
					Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 1, true);
				} else {
					attackFrequencyData[1] = false;
				}
				
			} else { //attacks once
				attackFrequencyData[1] = false;
				attackFrequencyData[2] = false;
				
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0, true);
			}
			
			// check for higher learning
			if (attacker.skill.nameEquals("Higher Learning")) {
				if (skillUses[0]) attEXP *= 2;
			}
			if (defender.skill.nameEquals("Higher Learning")) {
				if (skillUses[1]) defEXP *= 2;
			}
			
			defender.addEXP(Math.min(Math.max(defEXP, 1), 100));
			attacker.addEXP(Math.min(Math.max(attEXP, 1), 100));
			reset();
			attacker.setMAU(false);
			setForAttackStage(attacker, defender);
			return;
		}
		//must set EXP too

		if (pureSpeedAdv >= DOUBLE_SPEED) { // Somebody is attacking twice
			
			if (attacker.getEffectiveSpeed() > defender.getEffectiveSpeed()) { // Attacker attacks twice
				attackFrequencyData[1] = true;
				attackFrequencyData[2] = false;
				
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0, true);
				if (!attDead) { //enemy did not die
					EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 0);
					attackFrequencyData[0] = true;
				} else {
					attackFrequencyData[0] = false;
				}
				if (!attDead) {
					Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 1, true);
					
				} else { //enemy died so we did not double hit
					attackFrequencyData[1] = false;
				}
				
			} else { // defender attacks twice
				attackFrequencyData[0] = false;
				attackFrequencyData[1] = false;
				attackFrequencyData[2] = true;
				Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0, true);
				if (!attDead) {
					EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 0);
					attackFrequencyData[0] = true;
				} else attackFrequencyData[2] = false;
				if (!attDead) {
					EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 1);
				} else attackFrequencyData[2] = false;
				
			}
			// After double attacks are over, not sure if anything unique happens

		} else { // it is only a one attack each battle
			attackFrequencyData[0] = false;
			attackFrequencyData[1] = false;
			attackFrequencyData[2] = false;
			Allyattack(attacker, attackerDMG, attackerHit, attackerCrit, defender, 0, true);
			if (!attDead) {
				EnemyAttack(defender, defenderDMG, defenderHit, defenderCrit, attacker, 0);
				attackFrequencyData[0] = true;
			}
			
		}
		// After the attacks are over
		// check if higher learning was accomplished
		if (attacker.skill.nameEquals("Higher Learning")) {
			if (skillUses[0]) attEXP *= 2;
		}
		if (defender.skill.nameEquals("Higher Learning")) {
			if (skillUses[1]) defEXP *= 2;
		}
		// dampens EXP Based on which Game Difficulty we are playing at
		attEXP = (int)(0.5 + attEXP * EXPDAMPER);
		defEXP = (int)(0.5 + defEXP * EXPDAMPER);
		
		// bosses give 100 EXP when killed, 2x EXP when attacked
		if (defender.isBoss) {
			if (defender.currentHP <= 0) attEXP = 100;
			else attEXP *= 2;
		} else if (attacker.isBoss) {
			if (attacker.currentHP <= 0) defEXP = 100;
			else defEXP *= 2;
		}
		// add EXP to defender and attacker
		defender.addEXP(Math.min(Math.max(defEXP, 1), 100));
		attacker.addEXP(Math.min(Math.max(attEXP, 1), 100));
		// reset instance variables
		reset();
		// prevent attacker from moving or attacking again
		attacker.setMAUT(false);
		// prepare the attack animation stage
		setForAttackStage(attacker, defender);
	}
	/** Prepares an Attack Animation Stage between two players given the data has been filled from the Attack() method */
	public void setForAttackStage(Player attacker, Player defender) {
		game.chapterOrganizer.currentMap.nullAttackMenu();
		setPositions();
		stoppedMoving = false;
		game.setGameState(STATE.AttackStage);
	}
	/**
	 * Sets the attacker and defender back to starting positions
	 */
	private void setPositions() {
		this.attackerX = 50;
		this.defenderX = Game.WIDTH - 200;
		attackerXVel = 1+ADVANCE_SPEED;
		defenderXVel = 1+ADVANCE_SPEED;
		initialWait = 0;
	}

	public void renderAttackAnimation(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0,0, Game.WIDTH, Game.HEIGHT);
		int picWidth = Game.WIDTH/7;
		int picHeight = Game.HEIGHT/4;
		int picY = Game.HEIGHT/2 - (picHeight/2);
		int attHitMiss = Game.WIDTH/2 - 180;
		int defHitMiss = Game.WIDTH/2 + 50;
		int messageY = picY - 60;
		int message2Y = picY - 25;
		
		drawAttackBox(g);
		drawNames(g);
		g.drawImage(attacker.image, attackerX, picY, picWidth, picHeight, null);
		g.drawImage(defender.image, defenderX, picY, picWidth, picHeight, null);
		g.setFont(new Font("Times New Roman", Font.BOLD, 36));
		g.setColor(Color.red);
		
		if (initialWait == 0) {
			initialWait++;
			return;
		} else if (initialWait == INITIAL_SETTER) {
			initialWait++;
			TimeKeeper.threadWait(INITIAL_TIMER);
		}
		initialWait++;

		if (attackerXVel == 0 && defenderXVel == 0) {
			if (stoppedMoving) {
				if (current_Animation_Duration - animationEndTime >= MAX_ANIMATION_DURATION) {
					game.handleEXPDecision();
					return;
				} else {
					current_Animation_Duration = System.currentTimeMillis();
				}
			} else {
				stoppedMoving = true;
				animationEndTime = System.currentTimeMillis();
				current_Animation_Duration = System.currentTimeMillis();
			}

		}
		
		if (attackerX >= attackerMaxX) attackerXVel = -1-ADVANCE_SPEED;
		else if (attackerX <= 25) {
			//if we double go back again
			if (attackFrequencyData[1]) {
				attackerXVel = 1+ADVANCE_SPEED;
				numMoves++;
				if (numMoves >= 2) {
					attackerXVel = 0;
				}
			}
			else attackerXVel = 0;
		}
		if (defenderX <= defenderMaxX) defenderXVel = -1-ADVANCE_SPEED;
		else if (defenderX >= Game.WIDTH - 190) {
			//if we double go back again
			if (attackFrequencyData[2]) {
				defenderXVel = 1+ADVANCE_SPEED;
				numMoves++;
				if (numMoves>= 2) {
					defenderXVel = 0;
				}
			}
			else defenderXVel = 0;
		}
		if (!attackFrequencyData[0]) defenderXVel = 0;
		
		attackerX += attackerXVel;
		defenderX -= defenderXVel;
		
		if (skillUses[0]) {
			// attacker used skill
			g.setColor(attacker.teamColor);
			g.drawString(attacker.skill.getName(), attHitMiss, message2Y + 35);
		}
		if (skillUses[1]) {
			//defender used skill
			g.setColor(defender.teamColor);
			g.drawString(defender.skill.getName(), defHitMiss, message2Y + 35);
		}
		
		if (attackFrequencyData[1]) { //attacker did double
			g.setColor(attacker.teamColor);
			if (attackHitData[0]) { //attacker hit the first
				if (attackHitData[1]) { //attacker hit the second, both shots hit
					if (attackCritData[0]) g.drawString("CRIT", attHitMiss, messageY);
					else g.drawString("HIT", attHitMiss, messageY);
					if (attackCritData[1]) g.drawString("CRIT", attHitMiss, message2Y);
					else g.drawString("HIT", attHitMiss, message2Y);
				} else { //attacker hit the first but missed the second
					if (attackCritData[0]) g.drawString("CRIT", attHitMiss, messageY);
					else g.drawString("HIT", attHitMiss, messageY);
					g.drawString("MISS", attHitMiss, message2Y);
				}
			} else { //attacker missed the first shot
				if (attackHitData[1]) { //attacker hit the second shot but missed the first
					g.drawString("MISS", attHitMiss, messageY);
					if (attackCritData[1]) g.drawString("CRIT", attHitMiss, message2Y);
					else g.drawString("HIT", attHitMiss, message2Y);
				} else { //attacker missed both shots!
					g.drawString("MISS", attHitMiss, messageY);
					g.drawString("MISS", attHitMiss, message2Y);
				}
			}
			
			if (!attackFrequencyData[0]) { //defender did not attack at all
				
			} else { //defender did attack, but cannot double cause attacker doubled
				g.setColor(defender.teamColor);
				if (attackHitData[2]) { //defender hit
					if (attackCritData[2]) g.drawString("CRIT", defHitMiss, messageY);
					else g.drawString("HIT!", defHitMiss, messageY);
				} else { //defender missed
					g.drawString("MISS!", defHitMiss, messageY);
				}
			}
		} else { //attacker did not double
			g.setColor(attacker.teamColor);
			if (attackHitData[0]) { //attacker hit the first
				if (attackCritData[0]) g.drawString("CRIT", attHitMiss, messageY);
				else g.drawString("HIT", attHitMiss, messageY);
			} else { //attacker missed the first
				g.drawString("MISS", attHitMiss, messageY);
			}
			
			if (!attackFrequencyData[0]) { //defender did not attack at all
				
			} else { //defender did attack
				g.setColor(defender.teamColor);
				if (attackFrequencyData[2]) {//defender did double
					if (attackHitData[2]) { //defender hit the first!
						if (attackHitData[3]) { //defender hit the second too! both hit
							if (attackCritData[2]) g.drawString("CRIT", defHitMiss, messageY);
							else g.drawString("HIT", defHitMiss, messageY);
							if (attackCritData[3])g.drawString("CRIT", defHitMiss, message2Y);
							else g.drawString("HIT", defHitMiss, message2Y);
						} else { //defender missed the second but hit the first
							if (attackCritData[2]) g.drawString("CRIT", defHitMiss, messageY);
							else g.drawString("HIT", defHitMiss, messageY);
							g.drawString("MISS", defHitMiss, message2Y);
						}
					} else { //defender missed the first
						if (attackHitData[3]) { //defender missed the first but hit the second
							g.drawString("MISS", defHitMiss, messageY);
							if (attackCritData[3]) g.drawString("CRIT", defHitMiss, message2Y);
							else g.drawString("HIT", defHitMiss, message2Y);
						} else { // defender missed both shots
							g.drawString("MISS", defHitMiss, messageY);
							g.drawString("MISS", defHitMiss, message2Y);
						}
					}
					
				} else { //defender only attacked once
					g.setColor(defender.teamColor);
					if (attackHitData[2]) { //defender hit
						if (attackCritData[2]) g.drawString("CRIT", defHitMiss, messageY);
						else g.drawString("HIT", defHitMiss, messageY);
					} else { //defender missed
						g.drawString("MISS", defHitMiss, messageY);
					}
					
				}
			}
			
		}
		
	}
	
	/** Clears all of the previous hit/crit/attack data that was stored */
	public void clearAllData() {
		for (int i = 0; i < 4; i++) {
			attackHitData[i] = false;
			attackCritData[i] = false;
			if (i == 3) continue;
			attackFrequencyData[i] = false;
		}
		skillUses[0] = false;
		skillUses[1] = false;
		reset();
		setEXPDamper();
	}
	
	private void Allyattack(Player attacker, int attackerDMG, int attackerHit, int attackerCrit,  Player defender, int numATT, boolean attackerIS) {
		
		if (attacker == null || defender == null) return;
		
		int allyEXP = Math.max(5 - (attacker.level - defender.level), 0);
		int defenderEXP = 1;
		double levelDiff = (double)defender.level/attacker.level;
		int hitScale = (int)(9 * levelDiff);
		if (attackerHit >= hitRNGs[numATT]) { //we hit
			attackHitData[numATT] = true;
			if (attackerCrit >= critRNGs[numATT]) { //we crit!
				attackCritData[numATT] = true;
				if (defender.currentHP - (3 * attackerDMG) <= 0) {
					kill(attacker, defender, hitScale, attackerIS);
					return;
				} else {
					allyEXP += (int)(0.5 + 2.5*hitScale);
				}
				defender.takeDamage(3 * attackerDMG);
			} else { 					//no crit, just hit
				if (defender.currentHP - attackerDMG <= 0) { //if this blow kills them
					kill(attacker, defender, hitScale, attackerIS);
					return;
				} else {
					allyEXP += hitScale;
				}
				defender.takeDamage(attackerDMG);
			}
			attacker.decItemDuration(attacker.equiptItem);
		} else { //we miss
			defenderEXP += 10;
			attackHitData[numATT] = false;
		}
		
		if (attackerIS) {
			attEXP += allyEXP;
			defEXP += defenderEXP;
		} else {
			attEXP += defenderEXP;
			defEXP += allyEXP;
		}
		if (attacker.equiptItem.name.equalsIgnoreCase("Nosferatu")) {
			if (attackHitData[numATT]) {
				attacker.currentHP += attackerDMG;
			}
		}
		if (attacker.skill.nameEquals("Aether")) {
			if (attackerIS) {
				if (skillUses[0]) {
					if (attackHitData[numATT]) {
						attacker.currentHP += attackerDMG/2;
					}
				}
			} else {
				if (skillUses[1]) {
					if (attackHitData[numATT]) {
						attacker.currentHP += attackerDMG/2;
					}
				}
			}
		}
		
		attacker.incWeaponGrade(attacker.equiptItem);
		defender.incWeaponGrade(defender.equiptItem);
	}
	private void EnemyAttack(Player enemy, int enemyDMG, int enemyHit, int enemyCrit, Player ally, int numATT) {
		Allyattack(enemy, enemyDMG, enemyHit, enemyCrit, ally, 2 + numATT, false);
	}
	private void kill(Player killer, Player dier, int expScale, boolean attackerIS) {
		
		if (attackerIS) attEXP += Math.min(Math.max(3 * expScale, 1), 100);
		else defEXP += Math.min(Math.max(3 * expScale, 1), 100);
		if (dier.wallet.utilities.size() > 0) {
			if (dier.wallet.containsItemByName("Gold")) {
				for (int i = 0; i < dier.wallet.utilities.size(); i++) {
					UtilityItem item = dier.wallet.utilities.get(i);
					if (item.name.equalsIgnoreCase("Gold")) {
						item.carrier = killer;
						dier.giveItem(killer, item);
					}
				}
			} else if (dier.wallet.containsItemByName("Elixir")) {
				for (int i = 0; i < dier.wallet.utilities.size(); i++) {
					UtilityItem item = dier.wallet.utilities.get(i);
					if (item.name.equalsIgnoreCase("Elixir")) {
						item.carrier = killer;
						dier.giveItem(killer, item);
					}
				}
			}
		}
		dier.die();
		killer.incWeaponGrade(killer.equiptItem);
		killer.decItemDuration(killer.equiptItem);
		attDead = true;
		
	}
	/** Sets EXP and the dead boolean to zero and false */
	public void reset() {
		attDead = false;
		attEXP = 0;
		defEXP = 0;
		numMoves = 0;
	}
	
	private void drawAttackBox(Graphics g) {
		g.setColor(Color.black);
		int boxY = Game.HEIGHT - Game.HEIGHT/4 - 50;
		g.fillRect(0, boxY, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.WHITE);
		g.drawLine(Game.WIDTH/2, boxY, Game.WIDTH/2, Game.HEIGHT);
		g.setColor(Color.RED);
		g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 40));
		g.setColor(attacker.teamColor);
		g.drawString(attacker.equiptItem.name, 5, boxY + 35);
		
		g.setColor(defender.teamColor);
		g.drawString(defender.equiptItem.name, Game.WIDTH/2, boxY + 35);
		g.setFont(new Font("Times New Roman", Font.BOLD, 45));

		g.drawString("Hit: " +  atLeastZero(defenderHit) , Game.WIDTH/2 + 5, boxY + 95);
		g.drawString("DMG: " +  atLeastZero(defenderDMG) , Game.WIDTH/2 + 5, boxY + 135);
		g.drawString("Crit: " +  atLeastZero(defenderCrit) , Game.WIDTH/2 + 5, boxY + 175);
		g.setColor(attacker.teamColor);
		g.drawString("Hit: " + atLeastZero(attackerHit) , 5, boxY + 95);
		g.drawString("DMG: " +  atLeastZero(attackerDMG) , 5, boxY + 135);
		g.drawString("Crit: " +  atLeastZero(attackerCrit) , 5, boxY + 175);
		
	}
	private int atLeastZero(int val) {
		return Math.max(val, 0);
	}
	
	private void drawNames(Graphics g) {
		g.setColor(Color.cyan);
		int box2X = Game.WIDTH - 280;
		int boxW = 300;
		int boxH = 140;
		int thickness = 3;
		for (int i = 0; i < thickness; i++) {
			g.drawRect(0 + i, 0 + i, boxW, boxH);
			g.drawRect(box2X + i, 0 + i, boxW, boxH);
		}
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 60));
		g.drawString(attacker.name, 10, 60);
		g.drawString(defender.name, box2X + 10, 60);
		for (int i = 0; i < attacker.HP; i++) {
			drawHealthRect(20 + (5*i), 100, g, attacker, i);
		}
		for (int i = 0; i < defender.HP; i++) {
			drawHealthRect(box2X + 20 + (5*i), 100, g, defender, i);
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
	
	private void setEXPDamper() {
		
		if (game.gameDifficulty == DIFFICULTY.Easy) {
			this.EXPDAMPER = 1.25;
		} else if (game.gameDifficulty == DIFFICULTY.Normal) {
			this.EXPDAMPER = 1;
		} else if (game.gameDifficulty == DIFFICULTY.Hard) {
			this.EXPDAMPER = 0.88;
		} else {
			this.EXPDAMPER = 0.65;
		}
		
	}
	
}
