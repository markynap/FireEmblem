package characters;

import gameMain.Game;
import items.FluxTome;
import items.Nosferatu;
import items.PhysicStaff;
import items.UtilityItems.Elixir;

public class Druid extends EnemyPlayer{

	public Druid(int xPos, int yPos, Game game, int chaptNum) {
		super("Druid", "Druid", game.getBaseStatsForEnemy("Druid"), game.getBaseGrowthsForEnemy("Druid"), xPos, yPos, game, new FluxTome(), chaptNum, true);
		image = Game.IM.getImage("/characterPics/druid.png");
		// ADD ABILITY TO HEAL HIS ALLIES INSTEAD OF ATTACKING
		this.sightRange = 10;
		this.weaponMasteriesGrade[3] = 'A';
		this.isMagicUser = true;
		this.duoWeaponHeal = true;
		this.isHealer = true;
		int rng = r.nextInt(8);
		if (rng == 5) {
			this.wallet.clear();
			this.wallet.addItem(new Nosferatu());
			this.wallet.addItem(new Elixir());
			this.sightRange = 16;
		} else if (rng% 3 == 0) {
			this.wallet.addItem(new PhysicStaff());
		}
	}

}
