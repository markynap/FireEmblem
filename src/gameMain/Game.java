package gameMain;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import ai_MachineIntelligence.PathGenerator;
import characters.AllyPlayer;
import characters.EnemyPlayer;
import characters.ExampleAlly;
import extras.*;
import graphics.*;
import tiles.Tile;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 12345L;
	//All of these do not pertain to the constructor
	public final static int WIDTH = 1200;
	public final static int HEIGHT = WIDTH/5 * 4 + 34;
	public static ImageManager IM = new ImageManager();
	public SFXPlayer SFX;
	private Thread thread;
	private boolean running = false;
	public ThreadPool pool;
	public MusicPlayer MP;
	public static int nRow = 12;
	public static int scale = WIDTH/nRow; //1200/15 = 80
	//These are for the constructor
	public TimeKeeper timekeep;
	public ChapterOrganizer chapterOrganizer;
	public PlayerInfoGFX playerGFX;
	public PopUpMenu PUM;
	public PathGenerator pathGenerator;
	public AttackManager AttackManager;
	public EnemyPhaseProcessor enemyMove;
	public boolean inChapDesign = false;
	
	
	public enum STATE {
		Game,
		Menu,
		Info,
		AdvInfo,
		PopUpMenu,
		MoveState,
		AttackState,
		EnemyPhase,
		LoseGame,
		ChapterDesign
	}
	public STATE gameState = STATE.Game;
	
	public Game(boolean isChapterDesigner) {
		if (isChapterDesigner) {
			inChapDesign = true;
			this.addKeyListener(new ChapDesignKeyInput(this));
			new Window(WIDTH, HEIGHT, "Sacred Stones", this);
			gameState = STATE.ChapterDesign;
			return;
		}
		chapterOrganizer = new ChapterOrganizer(this);
		timekeep = new TimeKeeper();
		playerGFX = new PlayerInfoGFX(this);
		AttackManager = new AttackManager();
		enemyMove = new EnemyPhaseProcessor(this);
		this.addKeyListener(new KeyInput(this));
		new Window(WIDTH, HEIGHT, "Sacred Stones", this);
		addAlly(new ExampleAlly(2, 0, this));
	}
	
	
	public void tick() {
		if (inChapDesign) return;
		chapterOrganizer.tick();
		//System.out.println(gameState);
	}
	public void render(Graphics g) {
		if (gameState == STATE.Game) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.Menu) {
			
		} else if (gameState == STATE.Info) {
			chapterOrganizer.render(g);
			playerGFX.render(g);
		} else if (gameState == STATE.AdvInfo) {
			chapterOrganizer.render(g);
			playerGFX.renderAdv(g);
		} else if (gameState == STATE.PopUpMenu) {
			chapterOrganizer.render(g);
			if (PUM != null) PUM.render(g);
		} else if (gameState == STATE.MoveState) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.AttackState) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.EnemyPhase) {
			chapterOrganizer.render(g);
		} else if (gameState == STATE.LoseGame) {
			renderLoseGame(g);
		}
	}
	
	public void enemyTurn() {
		enemyMove.update();
		for (int i = 0; i < chapterOrganizer.enemys.size(); i++) {
			EnemyPlayer enemy =  chapterOrganizer.enemys.get(i);
			Tile dest = enemyMove.getFinalDestinationForEnemy(enemy);
			if (!dest.isOccupied()) chapterOrganizer.currentMap.move(enemy, dest);
			lookForKills(enemy);
			enemy.setMAU(false);
		}
	}
	/**
	 * Enemy searches around him based on his equipped item's range, if a unit is in range he will Attack
	 * @param enemy
	 */
	public void lookForKills(EnemyPlayer enemy) {
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			AllyPlayer ally = chapterOrganizer.allys.get(i);
			if (getTrueDist(ally.currentTile, enemy.currentTile) <= enemy.equiptItem.range) {
				AttackManager.Attack(enemy, ally);
			}
		}
	}
	/** Returns the absolute value of x distance + y distance */
	public int getTrueDist(Tile a, Tile b) {
		System.out.println("The true dist: " + (Math.abs(a.x - b.x) + Math.abs(a.y - b.y)));
		return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
	}
	
	public void setGameState(STATE state) {
		this.gameState = state;
	}
	
	public void run() {
		running = true;
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		// int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running) {
				renderGame();
			}
			// frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				// System.out.println("FPS: " + frames);
				// frames = 0;
			}
		}
		stop();
	}
	public void renderGame() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		render(g);
		
		g.dispose();
		bs.show();
	}

	public synchronized void start() {
		// pool = new ThreadPool(3);
		// pool.runTask(this);
		thread = new Thread(this);
		thread.start();
		// MP = new MusicPlayer("FireEmblemTheme", "FireEmblemHomeTune");
		// pool.runTask(MP);
		//SFX = new SFXPlayer();
		//pool.runTask(SFX);
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static int clamp(int var, int min, int max) {
		if (var >= max) {
			return var = max;
		} else if (var <= min) {
			return var = min;
		} else {
			return var;
		}
	}
	public static void main(String[] args) {
		new Game(false);
	}
	public void setPopUpMenu(PopUpMenu PUM) {
		this.PUM = PUM;
	}
	public void setPathGenerator(PathGenerator PG) {
		this.pathGenerator = PG;
	}
	public void addAlly(AllyPlayer ally) {
		chapterOrganizer.addAlly(ally);
	}
	public void addEnemy(EnemyPlayer enemy) {
		chapterOrganizer.addEnemy(enemy);
	}
	public void loseGame() {
		gameState = STATE.LoseGame;
	}
	public void renderLoseGame(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.red);
		g.setFont(new Font("Times New Roman", Font.BOLD, 55));
		g.drawString("YOU LOSE", Game.HEIGHT/3 + Game.HEIGHT/11, Game.WIDTH/3 + Game.WIDTH/7);
	}
}
