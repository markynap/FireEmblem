package items;
import gameMain.Game;

/** An item which can open any doors or chests */
public class Key extends UtilityItem{

	private Game game;
	
	public Key(Game game) {
		super("Key", "Opens locked doors or chests", 1, 0, 3);
		this.game = game;
		this.imagePath = "/itemPics/key.png";
	}
	public Key() {
		super("Key", "Opens locked doors or chests", 1, 0, 3);
		this.imagePath = "/itemPics/key.png";
	}

	@Override
	public void use() {
		if (carrier == null) {
			System.out.println("KEY CARRIER IS NULL");
			return;
		}
		if (carrier.canUse) {
			if (game != null) game.setKeyUseState(this);
		}
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

}
