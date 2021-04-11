package characters;

import gameMain.Game;
import items.PhysicStaff;

public class Troubadour extends EnemyPlayer{

	public Troubadour(int xPos, int yPos, Game game, int chaptNum) {
		super("Troubadour", "Troubadour", game.getBaseStatsForEnemy("Troubadour"), game.getBaseGrowthsForEnemy("Troubadour"), xPos, yPos, game, new PhysicStaff(), chaptNum, true);
		image = Game.IM.getImage("/characterPics/Troubadour.png");
		// ADD ABILITY TO HEAL HIS ALLIES INSTEAD OF ATTACKING
		this.sightRange = 45;
		this.isHealer = true;
		int rng = r.nextInt(5);
		if (rng == 3) {
			this.stats[1] += 5;
			this.STR = stats[1];
			
		}
			
	}


}
