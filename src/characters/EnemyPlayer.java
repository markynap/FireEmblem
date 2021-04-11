package characters;

import java.awt.Color;

import gameMain.Game;
import gameMain.Game.DIFFICULTY;
import items.*;
import items.UtilityItems.Gold;

public class EnemyPlayer extends Player {
	
	/** The number of which chapter we are spawning in this enemy at */
	private int chaptNum;
	/** The maximum range at which each enemy can detect ally players */
	public int sightRange;
		
	public EnemyPlayer(String name, String Class, int[] stats, int[] growths, int xPos, int yPos, Game game, CombatItem equiptItem, int chaptNum, boolean isPromoted) {
		super(name, Class, "Enemy", stats, growths, game, xPos, yPos, equiptItem, isPromoted);
		teamColor = Color.RED;
		this.chaptNum = chaptNum;
		buffStats();
		this.currentHP = this.HP;
		sightRange = 15;
		this.isPromoted = isPromoted;
		if (!isPromoted) {
			if (chaptNum >= 18) {
				this.addFalseLevels(7);
			} else if (chaptNum >= 12) {
				this.addFalseLevels(3);
			} else if (chaptNum > 6) {
				addFalseLevels(2);
			}
		} else {
			if (chaptNum > 20) {
				addFalseLevels(6);
			}
		}
		if (Class.equalsIgnoreCase("Mercenary")) {
			if (r.nextInt(15) == 7) {
				wallet.addItem(new Gold(r.nextInt(22)+1));
			}
		} else {
			if (r.nextInt(49) == 7) {
				wallet.addItem(new Gold(r.nextInt(15)+1));
			}
		}
		setSkill();
		this.race = "Daharan";
		repOk();
	}
	
	/** Sets the Skill of this enemy based on their name */
	private void setSkill() {
		
		switch (name) {
	
		case "Archer":
			this.skill = new Skill("Sure Shot");
			break;
		case "Cavalier":
			this.skill = new Skill("Momento");
			break;	
		case "Mage":
			this.skill = new Skill("Higher Learning");
			break;
		case "Bandit":
			this.skill = new Skill("Demolition");
			break;
		case "ArmorKnight":
			this.skill = new Skill("Great Shield");
			break;
		case "General":
			this.skill = new Skill("Great Shield");
			break;
		case "Paladin":
			this.skill = new Skill("Momento");
			break;
		case "Soldier":
			this.skill = new Skill("Empire Might");
			break;		
		case "Sage":
			this.skill = new Skill("Divine Wellness");
			break;	
		case "Wyvern":
			this.skill = new Skill("Airbourne");
			break;
		case "Sniper":
			this.skill = new Skill("Sure Shot");
			break;
		case "Berserker":
			this.skill = new Skill("Rage");
			break;
		case "Troubadour":
			this.skill = new Skill("Momento");
			break;
		case "SwordMaster":
			this.skill = new Skill("Rage");
			break;	
		case "DarkMage":
			this.skill = new Skill("Magic Counter");
			break;
		case "Druid":
			this.skill = new Skill("Magic Counter");
			break;
		case "Mercenary":
			this.skill = new Skill("Loot Bringer");
			break;
		default:
			this.skill = new Skill("DEFAULT");
		}	
	}
	
	
	/** Equips enemy unit with item depending on the current chapter and game difficulty
	 *  chance to give unique weapons (Killing Edge), or standard Iron, Bronze, or Steel weapons
	 *  if later in game can give silver weapons, etc etc
	 */
	protected void equipStartingItem(char weaponType) {
		wallet.weapons.clear();
		int rng;
		
		if (chaptNum >= 25) {
			
		} else if (chaptNum >= 18) {
			
			// chapters 18-24

			switch (weaponType) {
			
			case 'S': 
				rng = r.nextInt(39)+1;
				// random number between 1 - 39
				if (rng % 8 == 0) {
					wallet.addItem(new SilverSword());
				} else if (rng % 3 == 0) {
					wallet.addItem(new SteelSword());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronSword());
				} else {
					if (rng % 7 == 0) {
						wallet.addItem(new KillingEdge());
					} else if (rng % 13 == 0) {
						wallet.addItem(new ArmorSlayer());
					} else if (rng % 17 == 0) {
						wallet.addItem(new SilverDagger());
					} else if (rng > 16) {
						wallet.addItem(new IronSword());
					} else {
						wallet.addItem(new BronzeSword());
					}
				}
				break;
			
			case 'L':
				rng = r.nextInt(35)+1;
				// random number between 1 - 35
				if (rng % 8 == 0) {
					wallet.addItem(new SilverLance());
				} else if (rng % 3 == 0) {
					wallet.addItem(new SteelLance());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronLance());
				} else {
					if (rng < 18) {
						wallet.addItem(new Javelin());
					} else if (rng > 28) {
						wallet.addItem(new IronLance());
					} else {
						wallet.addItem(new BronzeLance());
					}
				}
				break;
			
			case 'A':
				rng = r.nextInt(35)+1;
				// random number between 1 - 35
				if (rng % 8 == 0) {
					wallet.addItem(new SilverAxe());
				} else if (rng % 3 == 0) {
					wallet.addItem(new SteelAxe());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronAxe());
				} else {
					if (rng < 15) {
						wallet.addItem(new HandAxe());
					} else if (rng > 25) {
						wallet.addItem(new IronAxe());
					} else {
						wallet.addItem(new BronzeAxe());
					}
				}
				break;
			
			case 'B':
				rng = r.nextInt(35)+1;
				// random number between 1 - 35
				if (rng % 8 == 0) {
					wallet.addItem(new SilverBow());
				} else if (rng % 3 == 0) {
					wallet.addItem(new SteelBow());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronBow());
				} else {
					if (rng < 7) {
						wallet.addItem(new LongBow());
					} else if (rng > 20) {
						wallet.addItem(new IronBow());
					} else {
						wallet.addItem(new BronzeBow());
					}
				}
				break;
				
			}

			
		} else if (chaptNum >= 10) {
			
			// chapters 10-17
			
			switch (weaponType) {
			
			case 'S': 
				rng = r.nextInt(30)+1;
				// random number between 1 - 30
				if (rng % 5 == 0) {
					wallet.addItem(new SteelSword());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronSword());
				} else {
					if (rng == 7) {
						wallet.addItem(new KillingEdge());
					} else if (rng == 13) {
						wallet.addItem(new BronzeDagger());
					} else if (rng == 17) {
						wallet.addItem(new ArmorSlayer());
					} else if (rng > 20) {
						wallet.addItem(new IronSword());
					} else {
						wallet.addItem(new BronzeSword());
					}
				}
				break;
			
			case 'L':
				rng = r.nextInt(30)+1;
				// random number between 1 - 30
				if (rng % 5 == 0) {
					wallet.addItem(new SteelLance());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronLance());
				} else {
					if (rng < 9) {
						wallet.addItem(new Javelin());
					} else if (rng > 20) {
						wallet.addItem(new IronLance());
					} else {
						wallet.addItem(new BronzeLance());
					}
				}
				break;
			
			case 'A':
				rng = r.nextInt(30)+1;
				// random number between 1 - 30
				if (rng % 5 == 0) {
					wallet.addItem(new SteelAxe());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronAxe());
				} else {
					if (rng < 7) {
						wallet.addItem(new HandAxe());
					} else if (rng > 20){
						wallet.addItem(new IronAxe());
					} else {
						wallet.addItem(new BronzeAxe());
					}
				}
				break;
			
			case 'B':
				rng = r.nextInt(30)+1;
				// random number between 1 - 30
				if (rng % 5 == 0) {
					wallet.addItem(new SteelBow());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronBow());
				} else {
					if (rng < 3) {
						wallet.addItem(new LongBow());
					} else if (rng > 20) {
						wallet.addItem(new IronBow());
					} else {
						wallet.addItem(new BronzeBow());
					}
				}
				break;
				
			}
			
		} else if (chaptNum >= 6) {
			
			// chapters 6 - 9

			switch (weaponType) {
			
			case 'S': 
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 10 == 0) {
					wallet.addItem(new SteelSword());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronSword());
				} else {
					if (rng == 13) {
						wallet.addItem(new BronzeDagger());
					} else if (rng == 17) {
						wallet.addItem(new ArmorSlayer());
					} else {
						wallet.addItem(new BronzeSword());
					}
				}
				break;
			
			case 'L':
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 10 == 0) {
					wallet.addItem(new SteelLance());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronLance());
				} else {
					if (rng < 6) {
						wallet.addItem(new Javelin());
					} else {
						wallet.addItem(new BronzeLance());
					}
				}
				break;
			
			case 'A':
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 10 == 0) {
					wallet.addItem(new SteelAxe());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronAxe());
				} else {
					if (rng < 4) {
						wallet.addItem(new HandAxe());
					} else {
						wallet.addItem(new BronzeAxe());
					}
				}
				break;
			
			case 'B':
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 10 == 0) {
					wallet.addItem(new SteelBow());
				} else if (rng % 2 == 0) {
					wallet.addItem(new IronBow());
				} else {
					if (rng == 7) {
						wallet.addItem(new LongBow());
					} else {
						wallet.addItem(new BronzeBow());
					}
				}
				break;
				
			}

		} else {
			
			// chapters 1-5

			switch (weaponType) {
			
			case 'S': 
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 6 == 0) {
					wallet.addItem(new IronSword());
				} else {
					if (rng == 13) {
						wallet.addItem(new BronzeDagger());
					} else {
						wallet.addItem(new BronzeSword());
					}
				}
				break;
			
			case 'L':
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 6 == 0) {
					wallet.addItem(new IronLance());
				} else {
					if (rng < 5) {
						wallet.addItem(new Javelin());
					} else {
						wallet.addItem(new BronzeLance());
					}
				}
				break;
			
			case 'A':
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 6 == 0) {
					wallet.addItem(new IronAxe());
				} else {
					if (rng < 4) {
						wallet.addItem(new HandAxe());
					} else {
						wallet.addItem(new BronzeAxe());
					}
				}
				break;
			
			case 'B':
				rng = r.nextInt(25)+1;
				// random number between 1 - 25
				if (rng % 3 == 0) {
					wallet.addItem(new IronBow());
				} else {
					wallet.addItem(new BronzeBow());
				}
				break;
				
			}
			
		}
		
	}
	
	private void buffStats() {
		
		if (game.gameDifficulty == DIFFICULTY.Normal) {

			for (int i = 0; i < chaptNum; i++) {
				levelUp();
			}
			if (chaptNum >= 8) {
				if (!isPromoted) {
					levelUp();
					levelUp();
					levelUp();
					levelUp();
					this.stats[9] -= 1;
					this.level -= 1;
				} else {
					this.stats[9] -= 1;
					this.level -= 1;
				}
			}
			
		} else if (game.gameDifficulty == DIFFICULTY.Hard || game.gameDifficulty == DIFFICULTY.Crushing) {
			for (int i = 0; i < chaptNum; i++) {
				levelUp();
				if (i % 4 == 3) levelUp();
			}
			if (chaptNum >= 8) {
				levelUp();
				if (!isPromoted) {
					levelUp();
					levelUp();
					levelUp();
					levelUp();
					levelUp();
					this.stats[9] -= 2;
					this.level -= 2;
				} else {
					if (stats[9] > 1) {
						this.stats[9] -= 1;
						this.level -= 1;
					}
				}
			}
			
		} else {
			// Easy difficulty
			for (int i = 1; i < chaptNum; i++) {
				levelUp();
			}
			if (chaptNum >= 8) {
				if (!isPromoted) {
					levelUp();
					levelUp();
				}
			}
			
		}
		
		
	}

}
