package characters;

import extras.IDAssigner;
import gameMain.Game;
import items.*;

public class ExampleAlly extends AllyPlayer {
	/** HP, STR, SK, SP, LUCK, DEF, RES, MOV, CON, LEVEL*/
	public static int[] stats = {18, 4, 3, 4, 2, 3, 1, 5, 8, 1};
	
	public static int[] growths = {50, 50, 50, 50, 50, 50, 50};
	
	public static IDAssigner EXAMPLEID = new IDAssigner(1);
	
	public ExampleAlly(int xPos, int yPos, Game game) {
		super("Example-" + EXAMPLEID.next(), "Lord", stats, growths, game, xPos, yPos, new BronzeSword());
		image = Game.IM.getImage("/Ike.png");
		addItem(new Fists());
		addItem(new BronzeAxe());
		addItem(new Vulnery(this));
	}

}
