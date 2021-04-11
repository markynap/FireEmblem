package characters;
/** Each Character possesses a Skill, all Skills will do in this class is
 *  render the appropriate name and description associated with each skill 
 * @author mark
 *
 */
public class Skill {
	/** The name of this Skill */
	private String name;
	/** A short description of what this skill does */
	private String description;
	/** False if ability can be activated, true if it occurs on its own */
	private boolean passive;
	/** True if this skill is used to aid in combat and should be checked prior */
	private boolean combative;
	/** If this skill is activated on a turn-by-turn basis */
	private boolean turnDependant;
	
	public Skill(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Skill(String name) {
		this.name = name;
		findDescription();
	}
	
	private void findDescription() {
		
		switch (name) {
		
		case "Aether":
			this.description = "Chance to deal a holy    blow";
			this.passive = true;
			this.combative = true;
			this.turnDependant = false;
			break;
		case "Sure Shot":
			this.description = "Chance to hit target     regardless of odds, adds DMG";
			this.passive = true;
			this.combative = true;
			this.turnDependant = false;
			break;
		case "Momento":
			this.description = "Plus 2 to movement";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Airbourne":
			this.description = "Ignore impassible terrainand terrain movement costs";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Higher Learning":
			this.description = "Chance to double EXP     gained";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Teleportation":
			this.description = "Ability to relocate      anywhere on the map";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Divine Blessing":
			this.description = "Heals all adjacent allies";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Soul Juice":
			this.description = "Dances for all adjacent  allies";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Bone Crusher":
			this.description = "Chance to negate enemy   defenses";
			this.passive = true;
			this.combative = true;
			this.turnDependant = false;
			break;
		case "Pick":
			this.description = "Can open any door or     chest without a key";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Demolition":
			this.description = "Plus one damage";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Great Shield":
			this.description = "Chance to negate enemy    damage when defending";
			this.passive = true;
			this.combative = true;
			this.turnDependant = false;
			break;
		case "Empire Might":
			this.description = "Regenerates 10% HP every turn";
			this.passive = true;
			this.combative = false;
			this.turnDependant = true;
			break;
		case "Divine Wellness":
			this.description = "Regenerates MAG as HP    every turn";
			this.passive = true;
			this.combative = false;
			this.turnDependant = true;
			break;
		case "Is Boss":
			this.description = "IS BOSS AND WILL FK U UP";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Summoning":
			this.description = "Summon an Ally Unit to   fight for you";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Assassination":
			this.description = "Chance to murder an enemy regardless of damage";
			this.passive = true;
			this.combative = true;
			this.turnDependant = false;
		case "Confessed":
			this.description = "This unit has been forced to fight against his will";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Summoned":
			this.description = "This unit has been summoned to fight for you";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Confession":
			this.description = "Turn a weaker enemy unit into your loyal slave";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Condar":
			this.description = "Turn all nearby enemies  into loyal slaves";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;	
		case "Rage":
			this.description = "Greatly increases critical strike chance";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;	
		case "Teraform":
			this.description = "Bends the Earth itself   changing its form";
			this.passive = false;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Magic Counter":
			this.description = "Counters all forms of    Earthly magic (Fi, Ice, Ea)";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Loot Bringer":
			this.description = "Higher chance of dropping gold when killed";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		case "Gunman":
			this.description = "Increased accuracy and   proficiency with firearms";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		default:
			this.description = "No description given";
			this.passive = true;
			this.combative = false;
			this.turnDependant = false;
			break;
		}
		
	}
	
	/** Tests to see if this skill is the same as another skill */
	public boolean equals(Skill other) {
		return (this.name.equalsIgnoreCase(other.name) && this.description.equalsIgnoreCase(other.description));
	}
	/** True if this name equals another name regardless of case */
	public boolean nameEquals(String otherName) {
		return name.equalsIgnoreCase(otherName);
	}
	/** Returns the name of this skill */
	public String getName() {
		return name;
	}
	/** Returns the description associated with this skill */
	public String getDescription() {
		return description;
	}
	/** False if this ability can be activated, true if it passively occurs */
	public boolean isPassive() {
		return passive;
	}
	/** True if this skill is used in combat and should be checked for */
	public boolean isCombatSkill() {
		return combative;
	}
	/** True if this skill is reactivated once every turn */
	public boolean isTurnDependant() {
		return turnDependant;
	}
	/** Returns the RNG chance of a combat skill occuring */
	public static int getCombatChance(Player userOfSkill) {
		int chance = 0;
		
		if (userOfSkill.skill.isCombatSkill()) {
			switch (userOfSkill.skill.getName()) {
			
			case "Aether":
						chance = userOfSkill.SK/2 + userOfSkill.LCK/4;
						break;
			case "Bone Crusher":
						chance = userOfSkill.SK;
						break;
			case "Sure Shot":
						chance = userOfSkill.SK;
						break;
			case "Great Shield":
						chance = userOfSkill.SK/5 + userOfSkill.DEF/3 + userOfSkill.LCK/4;
						break;
			
			case "Assassination":
						chance = userOfSkill.getCrit()/4;
						break;
			
			}
		} else {
			return 0;
		}
	//	System.out.println(userOfSkill.skill.getName() + " has a chance " + chance + " of occurring");
		return chance;
		
	}
	/** Returns the RNG chance of a passive skill occuring from the user */
	public static int getPassiveChance(Player user) {
		
		int chance = 0;
		
		switch (user.skill.getName()) {
		
		case "Higher Learning":
					chance = Math.max(20 - user.level, 0) + user.LCK/4;
					break;
		
		// need to add more passive chances here			
		
		}
		
		return chance;
	}
	
	public static int getHigherLearningChance(Player user) {
		int chance = (Math.max(20 - user.level, 0) + user.LCK/4);
	//	System.out.println("Higher Learning has a chance " + chance + " of occurring");
		return chance;
		
	}
	
}
