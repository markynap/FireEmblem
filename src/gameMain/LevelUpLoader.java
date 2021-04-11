package gameMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import characters.Player;
import extras.TimeKeeper;
import gameMain.ChapterMap.WinCondition;
import gameMain.Game.STATE;

public class LevelUpLoader {

	public Game game;
	/** Player who is gaining exp or leveling up */
	private Player player;
	/** Amount of EXP the player gained from the last fight */
	public int expGained;
	/** Stats for coordinate of the experience box */
	private int[] box = {Game.WIDTH/2 - 200, Game.HEIGHT/2 - 80, 400, 160};
	/** Stats for coordinate of the level up box */
	private int[] Lbox = {Game.WIDTH/2 - 200, Game.HEIGHT/2 - 200, 400, 400};
	/** The player's previous experience */
	private int prevEXP;
	/** If the EXP goes over 100 */
	private boolean goesOver;
	/** If the player gains 100 EXP */
	private boolean fullLevelUp;
	/** Speed of the EXP green bar */
	private int EXP_SPEED = 5;
	
	public boolean isEnemyPhase;
	
	/** Responsible for displaying all level ups as well as the
	 *  increase in EXP bar after each battle 
	 * @param game
	 */
	public LevelUpLoader(Game game) {
		this.game = game;
	}
	/** What happens when user presses 's' */
	public void handleBackCommand() {
		
		if (game.gameState != STATE.LevelUp) return;
		
		if (player == null) {
			if (isEnemyPhase) {
				game.setGameState(STATE.EnemyPhase);
			} else {	
				game.setGameState(STATE.Game);
			}
			game.chapterOrganizer.checkForLoss();
			if (game.chapterOrganizer.getCurrentMap().winConditionEquals(WinCondition.Route)) {
				if (game.chapterOrganizer.enemys.isEmpty()) {
					game.endChapter();
				}
			}
			System.out.println("handleBackCommand LevelUpLoader - player was null");
			return;
		}
		if (player.level == 20) {
			game.promotionManager.setPlayer(player, isEnemyPhase);
			game.setGameState(STATE.Promotion);
			reset();
			return;
		}
		
		if (isEnemyPhase) {
			game.setGameState(STATE.EnemyPhase);
		} else {	
			game.setGameState(STATE.Game);
		}
		game.chapterOrganizer.checkForLoss();
		if (game.chapterOrganizer.getCurrentMap().winConditionEquals(WinCondition.Route)) {
			if (game.chapterOrganizer.enemys.isEmpty()) {
				game.endChapter();
			}
		}
		reset();
	}
	
	public void tick() {
		
		if (game.gameState == STATE.LevelUp) {
			
			
		} else { //STATE.GainEXP

			if (goesOver) {
				
				// we are going to level up
				
				if (prevEXP < 100) {
				
					// we have not passed 100 yet

					TimeKeeper.threadWait(EXP_SPEED);
					prevEXP++;
				}
				else {
					// we hit 100 already, reset and do not allow to overflow again
					prevEXP = 0;
					goesOver = false;
				}
					
			} else {
				// no level up
				if (prevEXP < player.EXP) {
					
					TimeKeeper.threadWait(EXP_SPEED);
					prevEXP++;
				}
				else {
						
					if (fullLevelUp) {
						
						game.SFX.playSong(1);
						game.setGameState(STATE.LevelUp);
							
					} else {
						// no level up occurred
						// hold frame for a second so they can see frame						
						
						if (game.chapterOrganizer.getCurrentMap().winConditionEquals(WinCondition.Route)) {
							if (game.chapterOrganizer.enemys.isEmpty()) {
								game.endChapter();
								return;
							}
						}
						if (isEnemyPhase) {

							TimeKeeper.threadWait(180);
							game.setGameState(STATE.EnemyPhase);
						} else {
							
							TimeKeeper.threadWait(400);
							game.setGameState(STATE.Game);
						}
							
						game.chapterOrganizer.checkForLoss();
						reset();
						return;
						
					}
				}
			}			
		}
	}

	public void render(Graphics g) {
		
		if (game.gameState == STATE.LevelUp) {
			try {
			if (player == null) return;
			if (Lbox == null) return;
			g.setFont(new Font("Times New Roman", Font.BOLD, 26));
			g.setColor(Color.DARK_GRAY);
			g.fillRect(Lbox[0], Lbox[1], Lbox[2], Lbox[3]);
			
			g.setColor(Color.white);
			for (int i = 0; i < 5; i++) {
				g.drawRect(Lbox[0] - 5 + i, Lbox[1] - 5 + i, Lbox[2] + 10 - 2*i, Lbox[3] + 10 - 2*i);
			}
			if (player != null) g.drawString(player.name, Lbox[0] + 10, Lbox[1] + 40);
			
			int spacing = 35;
			
			for (int i = 0; i < 7; i++) {
				if (!player.isMagicUser) {
					if (player.levelUps[i]) g.drawString(Player.StatNames[i] + ":  " + (player.stats[i]-1) + " + 1", Lbox[0] + 10, Lbox[1] + 80 + (spacing * i));
					else g.drawString(Player.StatNames[i] + ":  " + player.stats[i], Lbox[0] + 10, Lbox[1] + 80 + (spacing * i));
				} else {
					if (player.levelUps[i]) g.drawString(Player.MagStatNames[i] + ":  " + (player.stats[i]-1) + " + 1", Lbox[0] + 10, Lbox[1] + 80 + (spacing * i));
					else g.drawString(Player.MagStatNames[i] + ":  " + player.stats[i], Lbox[0] + 10, Lbox[1] + 80 + (spacing * i));
				}
			}
			g.drawString("Level: " + (player.level-1) + " + 1", Lbox[0] + 10, Lbox[1] + 80 + (spacing * 8) + spacing/2);
			g.drawImage(player.image, Lbox[0] + Lbox[2]/2 - 20, Lbox[1] + 15, Lbox[2]/2, Lbox[3] - 30, null);
			
			g.setColor(Color.red);
			g.setFont(new Font("Times New Roman", Font.BOLD|Font.ITALIC, 50));
			g.drawString("Press S To Continue", Lbox[0], Lbox[1] - 35);
			} catch (NullPointerException e) {
				System.err.println("Level up loader still has null pointer. not sure what causes it");
			}
		} else { // STATE.GainEXP
		
			
			g.setColor(Color.DARK_GRAY);
			g.fillRect(box[0], box[1], box[2], box[3]);
			
			g.setColor(Color.white);
			for (int i = 0; i < 5; i++) {
				g.drawRect(box[0] - 5 + i, box[1] - 5 + i, box[2] + 10 - 2*i, box[3] + 10 - 2*i);
			}
			g.drawRect(box[0] + box[2]/12, box[1] + box[3]/2, 5*box[2]/6, box[3]/4);
			
			g.setColor(Color.green);
			g.setFont(new Font("Times New Roman", Font.ITALIC, 35));
			
			g.drawString("EXP", box[0] + box[2]/2 - 25, box[1] + 30);
			
			g.fillRect(box[0] + box[2]/12, box[1] + box[3]/2, (int)((prevEXP/100.0)*(5*box[2]/6)), box[3]/4);
			
			
		}
		
	}
	
	public void setPlayer(Player player, int prevEXP, boolean isEnemyPhase) {
		reset();
		this.isEnemyPhase = isEnemyPhase;
		this.player = player;
		this.prevEXP = prevEXP;
		if (player.EXP <= prevEXP) {
			fullLevelUp = true;
			goesOver = true;
		}
	}
	
	public void reset() {
		game.setPlayerForEXP(null, 0);
		goesOver = false;
		prevEXP = 0;
		player = null;
		fullLevelUp = false;
		isEnemyPhase = false;
	}
}
