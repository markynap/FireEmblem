package gameMain;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import enemy_ai.PathGenerator;
import chapterDesign.ChapterDesigner;
import characters.*;
import cutScene.CutSceneGenerator;
import extras.*;
import gameMain.Menu.MODE;
import graphics.ArmoryHandler;
import graphics.OptionsMenu;
import graphics.PlayerFollowChoiceGFX;
import graphics.PlayerInfoGFX;
import graphics.PopUpMenu;
import graphics.TradeMenu;
import graphics.TutorialDisplayer;
import items.CombatItem;
import items.Convoy;
import items.Fists;
import items.Item;
import items.Key;
import tiles.Tile;
/**
 * Opens up a re-make of the famous Fire Emblem games that were available on the GBA years ago.
 * @author mark
 *
 */
public class Game extends Canvas implements Runnable {
	
	/** Stupid Long I need in order to prevent my game from having compile time errors */
	private static final long serialVersionUID = 123456L;
	/** WIDTH of the entire visible Screen */
	public final static int WIDTH = 800;
	/** Height of the entire visible Screen */
	public final static int HEIGHT = 4*WIDTH/5 + 31;
	/** Coordinates of the box allowing you to save your game */
	public final int[] saveBox = {WIDTH/8, HEIGHT/5, 2*WIDTH/3 + 25, HEIGHT/4};
	/** DAMAGE, HIT, AVOID */
	public final int[] WeaponTriangleBonuses = {1, 15, 10};
	/**True if yes to go back to menu, false if no */
	public boolean menuYes;  
	/** Responsible for getting all images in this game */
	public static ImageManager IM = new ImageManager();
	/** Renders all the sound effects of this game */
	public SFXPlayer SFX;
	/** Controls the running of our game loop */
	private boolean running = false;
	/** Pool of Threads that will execute their tasks synchronously */
	public ThreadPool pool;
	/** Controls the Music Played during our game on its own thread */
	public MusicPlayer MP;
	/** Number of tiles in each row on the screen */
	public static int nRow = 15;
	/** Number of columns visible on the screen */
	public static int nCol = 12;
	/** Scale of the game, approximate pixel size of each tile side length*/
	public static int scale = WIDTH/nRow; //1200/15 = 80
	/** Map of Player names to ID numbers */
	public TreeMap<Integer, String> playerIDMap;
	/** Inverse order of playerIDMap, for easy access */
	public TreeMap<String, Integer> playerIDMapINV;
	/** Responsible for loading the correct game and save-state information */
	public GameLoader gameLoader;
	/** Responsible for rendering EXP and Level Up States */
	public LevelUpLoader levelUpLoader;
	/** Which save state we are currently reading from or writing over */
	private int loadLevel;
	/** Player we will soon be setting for EXP mode rendering */	
	public Player playerForEXP;
	/** The Player for EXP mode rendering's previous EXP */
	public int playerPreviousEXP;
	/** Allows two players to trade items between themselves */
	public TradeMenu tradeMenu;
	/** Tracks the dead players to prevent them from being recognized in game */
	public ArrayList<Player> deadPlayers;
	/** Generates both in game and out game cutscenes when necessary */
	public CutSceneGenerator cutScenes;
	/** Controls various small functions throughout the game */
	public OptionsMenu optionsMenu;
	/** Handles all Armory and Vender transactions */
	public ArmoryHandler armoryHandler;
	/** Organizes and handles chapter and in-game events */
	public ChapterOrganizer chapterOrganizer;
	/** Player Info screen menu */
	public PlayerInfoGFX playerGFX;
	/** Pop Up Menu for selecting various tasks */
	public PopUpMenu PUM;
	/** Path Generator for most pathing operationg */
	public PathGenerator pathGenerator;
	/** Manages Attacks performed throughout this game */
	public AttackManager AttackManager;
	/** Designs and edits chapters */
	public ChapterDesigner designer;
	/** Main menu */
	public Menu menu;
	/** Chooses chapters and starts all allys at level 1, good for testing */
	public ChapterChooser chapterChooser;
	/** Loading Screen, shown if CPU is handling too much */
	public LoadScreen loadScreen;
	/** Time Keeper, not sure if its all that useful */
	public TimeKeeper timekeep;
	/** Manages Ally promotions that occur throughout the later levels of this game */
	public PromotionsManager promotionManager;
	/** True if we want the EXP sound to keep going.. */
	public boolean expSoundOn = true;
	/** Displays the tutorial READ_ME midgame */
	public TutorialDisplayer tutorialDisplay;
	/** Handles the choice between following Hector's story or Ike's story */
	public PlayerFollowChoiceGFX playerFollow;
	/** Draws/Handles all displays */
	private GameRenderer gameDisplay;
	/** True if we are in Developer Mode for the game */
	public boolean inDevMode = false;
	/** Possible States that the Game could be in */
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
		ChapterDesign,
		StartScreen,
		ChapterDesignMenu,
		AttackStage,
		ChapterChoose,
		LoadScreen,
		LoadGame,
		LevelUp,
		GainEXP,
		TradeState,
		EnemyChoice,
		weaponSelection,
		outGameCutScene,
		inGameCutScene,
		keyOpeningState,
		optionsMenu,
		itemReceived,
		skillUse,
		viewInfo,
		viewAdvInfo,
		Armory,
		BattlePreparations,
		MiniMapView,
		Promotion,
		playerCarry,
		TalkState,
		Tutorial,
		playerFollowChoice,
		ReturnToMenu
	}
	/** The Difficulty our game is currently running at */
	public enum DIFFICULTY {
		Easy,
		Normal,
		Hard,
		Crushing
	}
	/** The current difficulty of the game */
	public DIFFICULTY gameDifficulty = DIFFICULTY.Easy;
	/** Current State of the game */
	public STATE gameState = STATE.StartScreen;
	/** True if Hector is our main Lord and his death is the one that matters, false for Ike */
	public boolean onHectorMode;
	
	/** Creates a new Sacred Stones Game */
	public Game() {
		setPlayerIDMap();
		gameDisplay = new GameRenderer(this);
		menu = new Menu(this);
		timekeep = new TimeKeeper();
		deadPlayers = new ArrayList<>();
		chapterOrganizer = new ChapterOrganizer(this, new ChapterMap(24, 30, this), 0);
		playerGFX = new PlayerInfoGFX(this);
		AttackManager = new AttackManager(this);
		gameLoader = new GameLoader(this);
		this.addKeyListener(new KeyInput(this));
		new Window(WIDTH, HEIGHT, "Sacred Stones", this);
		loadScreen = new LoadScreen();
		levelUpLoader = new LevelUpLoader(this);
		menuYes = true;
		cutScenes = new CutSceneGenerator(this);
		armoryHandler = new ArmoryHandler(this);
		promotionManager = new PromotionsManager(this);
		tutorialDisplay = new TutorialDisplayer(this);
		playerFollow = new PlayerFollowChoiceGFX(this);
	}
	
	/** Returns and Creates the player at the given position with the given ID 
	 * @param ID - player ID
	 * @param x - player xPosition
	 * @param y - player yPosition
	 * @param loadLevel - which save state we are loading from
	 * @return Player with matching ID, Raymond if otherwise
	 */
	public Player getPlayerByID(int ID, int x, int y, int whichChapter) {	
		
		if (playerIDMap.get(ID) == null) return null;
		
		switch (playerIDMap.get(ID)) {
			case "Ike": return new Ike(x, y, this, whichChapter);
			case "Hector": return new Hector(x, y, this, whichChapter);
			case "Raymond": return new Raymond(x, y, this, whichChapter);
			case "Kent": return new Kent(x, y, this, whichChapter);
			case "Nino": return new Nino(x, y, this, whichChapter);
			case "Marcus": return new Marcus(x, y, this, whichChapter);
			case "Wolf": return new Wolf(x, y, this, whichChapter);
			case "Bard": return new Bard(x, y, this, whichChapter);
			case "Florina": return new Florina(x, y, this, whichChapter);
			case "Volke": return new Volke(x,y,this,whichChapter);
			case "Evelynn": return new Evelynn(x,y,this,whichChapter);
			case "Shinnon": return new Shinnon(x,y,this,whichChapter);
			case "Kahlan": return new Kahlan(x,y,this,whichChapter);
			case "Guy": return new Guy(x,y,this,whichChapter);
			case "Heath": return new Heath(x,y,this,whichChapter);
			case "Merric": return new Merric(x,y,this,whichChapter);
			case "Dorcas": return new Dorcas(x,y,this,whichChapter);
			case "Jetson": return new Jetson(x,y,this,whichChapter);
			case "Soren": return new Soren(x,y,this,whichChapter);
			case "Navarre": return new Navarre(x,y,this,whichChapter);
			case "Helga": return new Helga(x,y,this,whichChapter);
			case "Priscilla": return new Priscilla(x,y,this,whichChapter);
			case "Mason": return new Mason(x,y,this,whichChapter);
			// ENEMY UNITS
			case "Bandit": return new Brigand(x, y, this, whichChapter);
			case "Cavalier": return new Cavalier(x, y, this, whichChapter);
			case "Mage": return new Mage(x, y, this, whichChapter);
			case "Archer": return new Archer(x, y, this, whichChapter);
			case "ArmorKnight": return new ArmorKnight(x,y,this,whichChapter);
			case "Soldier": return new Soldier(x,y,this,whichChapter);
			case "Paladin": return new Paladin(x,y,this,whichChapter);
			case "Wyvern": return new Wyvern(x,y,this,whichChapter);
			case "Sage": return new Sage(x,y,this,whichChapter);
			case "General": return new General(x,y,this,whichChapter);
			case "Sniper": return new Sniper(x,y,this,whichChapter);
			case "DarkMage": return new DarkMage(x,y,this,whichChapter);
			case "SwordMaster": return new SwordMaster(x,y,this,whichChapter);
			case "Mercenary": return new Mercenary(x,y,this,whichChapter);
			case "Berserker": return new Berserker(x,y,this,whichChapter);
			case "Druid": return new Druid(x,y,this,whichChapter);
			case "Troubadour": return new Troubadour(x,y,this,whichChapter);
			case "TalkedToEnemy": return new TalkedToEnemy(x,y,this,whichChapter);
			case "Boss": return new Boss(x, y, this, whichChapter);

			default: return new Raymond(x, y, this, whichChapter);
		}
	}
	/** progresses the game frame by frame */
	public void tick() {

		if (gameState == STATE.LoadGame) return;
		if (gameState == STATE.outGameCutScene) {
			cutScenes.tick();
			return;
		} else if (gameState == STATE.inGameCutScene) {
			cutScenes.tick();
			return;
		}
		if (gameState == STATE.ChapterDesign) {
			designer.organizer.tick();
		} else {
			chapterOrganizer.tick();
		}
	}
	
	public void setItemReceivedState(String itemReceivedName) {
		chapterOrganizer.currentMap.setItemReceived(itemReceivedName);
		this.gameState = STATE.itemReceived;
	}
	

	/** Returns the absolute value of x distance + y distance */
	public int getTrueDist(Tile a, Tile b) {
		if (a == null) return 0;
		if (b == null) return 0;
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
		//long timer = System.currentTimeMillis();
		//int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			renderGame();
			TimeKeeper.threadWait(10);
				
//			frames++;

			/*if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				 System.out.println("FPS: " + frames);
				 frames = 0;
			}*/
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
		
		if (gameDisplay != null)
			gameDisplay.render(g, gameState);
		
		g.dispose();
		bs.show();
	}

	public synchronized void start() {
		pool = new ThreadPool(3);
		pool.runTask(this);
		MP = new MusicPlayer("FireEmblemTheme", "FireEmblemHomeTune", "windsAcrossThePlane");
		pool.runTask(MP);
		MP.playSong(0);
		SFX = new SFXPlayer("Cursor", "LevelUp", "Select", "Experience", "GameOver");
		pool.runTask(SFX);
		running = true;
	}

	public synchronized void stop() {
		try {
			pool.join();
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
		new Game();
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
	public void addPlayer(Player player) {
		chapterOrganizer.addPlayer(player);
	}
	/** Puts the game into a Lose Game State */
	public void loseGame() {
		gameState = STATE.LoseGame;
		if (MP.song != null) MP.song.stop();
		SFX.playSong(4);
	}
	/** When Chapter Designer is selected in menu */
	public void setChapterDesigner() {
		designer = new ChapterDesigner(this);
		gameState = STATE.ChapterDesignMenu;
	}
	/** When Choose Chapter is selected in menu */
	public void setChapterChooser() {
		chapterChooser = new ChapterChooser(this);
		gameState = STATE.ChapterChoose;
	}
	/** Sets the game in the Lose-Game Screen */
	public void renderLoseGame(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.red);
		g.setFont(new Font("Times New Roman", Font.BOLD, 55));
		g.drawString("YOU LOSE", Game.HEIGHT/3 + Game.HEIGHT/11, Game.WIDTH/3 + Game.WIDTH/7);
	}
	/** Renders the starting screen of the game */
	public void renderStartScreen(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g.setColor(Color.RED);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 80));
		g.drawString("Fire Emblem", Game.WIDTH/2 - 220, Game.HEIGHT/2-40);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 50));
		g.drawString("Press Any Button To Continue", Game.WIDTH/2 - 300, Game.HEIGHT/2 + 40);
	}
	/**True if the players are both on the same team, false otherwise*/
	public static boolean sameTeam(Player one, Player two) {
		return (one.teamID.equalsIgnoreCase(two.teamID));
	}
	public void setChapterOrganizer(ChapterOrganizer organizer) {
		this.chapterOrganizer = organizer;
	}
	/** Resets current game statuses and brings us back to the main menu */
	public void backToMenu() {
		gameState = STATE.LoadScreen;
		designer = null;
		gameLoader.isSaving = false;
		chapterOrganizer = new ChapterOrganizer(this, new ChapterMap(24, 30, this), 0);
		this.removeKeyListener(this.getKeyListeners()[0]);
		this.addKeyListener(new KeyInput(this));
		menu.menuMode = MODE.Main;
		gameDifficulty = DIFFICULTY.Easy;
		gameState = STATE.Menu;
	}
	public void renderMenuPopUp(Graphics g) {
		
		int thickness = 5;
		g.setColor(Color.blue);
		for (int i = 0; i < thickness; i++) g.drawRect(saveBox[0] + i, saveBox[1] + i, saveBox[2], saveBox[3]);
		g.setColor(Color.white);
		g.fillRect(saveBox[0] + thickness, saveBox[1] + thickness, saveBox[2] - thickness, saveBox[3] - thickness);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString("Would you like to return to the main menu without saving?", saveBox[0] + 5, saveBox[1] + 25);
		int xPos = saveBox[0] + saveBox[2]/2 - scale;
		int yesY =  saveBox[1] + 80;
		int noY = yesY + 50;
		g.setFont(new Font("Times New Roman", Font.BOLD, 35));
		g.drawString("Yes", xPos, yesY);
		g.drawString("No", xPos, noY);
		g.setColor(Color.blue);
		if (menuYes) g.drawRect(xPos - 8, yesY - 28, 75, 40);
		else g.drawRect(xPos - 8, noY - 28, 75, 40);
	}
	private void setPlayerIDMap() {
		playerIDMap = new TreeMap<>();
		playerIDMapINV = new TreeMap<>();
		try {
			Scanner reader = new Scanner(new File("res//res_characters//playerIDs"));
			String[] line;
			while (reader.hasNextLine()) {
				line = reader.nextLine().split(" ");
				playerIDMap.put(Integer.valueOf(line[1]), line[0]);
				playerIDMapINV.put(line[0], Integer.valueOf(line[1]));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		}
	}
	public int getLoadLevel() {
		return loadLevel;
	}
	public void setLoadLevel(int newLoadLevel) {
		this.loadLevel = newLoadLevel;
	}
	
	public int[] getBaseStatsForEnemy(String Class) {
		int[] stats = new int[10];
		String line;
		try {
			Scanner statsReader = new Scanner(new File("res//res_characters//enemyBaseStats"));
			while (statsReader.hasNextLine()) {
				line = statsReader.nextLine();
				String[] lines;
				if (line.equalsIgnoreCase(Class)) {
					for (int i = 0; i < 10; i++) {
						lines = statsReader.nextLine().split(":");
						stats[i] = Integer.parseInt(lines[0]);
						if (gameDifficulty == DIFFICULTY.Crushing) {
							stats[i]++;
						}
					}
					break;
				}
			}
			statsReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stats;
	}
	
	public int[] getBaseGrowthsForEnemy(String Class) {
		int[] growths = new int[7];
		String line;
		try {
			Scanner statsReader = new Scanner(new File("res//res_characters//enemyBaseStats"));
			while (statsReader.hasNextLine()) {
				line = statsReader.nextLine();
				String[] lines;
				if (line.equalsIgnoreCase(Class)) {
					for (int i = 0; i < 7; i++) {
						lines = statsReader.nextLine().split(":");
						growths[i] = Integer.parseInt(lines[1]);
					}
					break;
				}
			}
			statsReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return growths;
	}

	/** Removes a player from this game */
	public void removePlayer(Player player) {
		chapterOrganizer.removePlayer(player);
	}
	/** Starts the EXP scene, isEnemyPhase = false means it is Ally Phase */
	public void startEXPScene(boolean isEnemyPhase) {
		if (playerForEXP == null) {
			return;
		}
		levelUpLoader.setPlayer(playerForEXP, playerPreviousEXP, isEnemyPhase);
		if (expSoundOn) SFX.playSong(3);
		gameState = STATE.GainEXP;
	}
	/** Sets the player's previous EXP for the EXP rendering scene */
	public void setPlayerForEXP(Player player, int prevEXP) {
		this.playerForEXP = player;
		this.playerPreviousEXP = prevEXP;
	}
	/** Image, Stats */
	public String[] getBossStats(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//res_characters//bossStats"));
			String[] linePiece;
			String[] stats = new String[11];
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						reader.nextLine(); //name
						reader.nextLine(); //class
						reader.nextLine(); // weapon
						for (int i = 0; i < 11; i++) {
							stats[i] = reader.nextLine();
						}
						
					}
				}
			}
			reader.close();
			return stats;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new String[13];
	}
	/** Reads and returns the CombatItem equipt for our boss as stated in our boss stats file in res//res_characters */
	public CombatItem getBossItem(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//res_characters//bossStats"));
			String[] linePiece;
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						reader.nextLine();
						reader.nextLine();
						CombatItem weapon = (CombatItem)Item.getItemByID(Integer.valueOf(reader.nextLine()));
						reader.close();
						return weapon;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Fists();
	}
	/** Obtain the string representing our bosses name from our boss stats file located in res//res_characters/ 
	 * @param whichChapter - the chapter who's boss will be loaded */
	public String getBossName(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//res_characters//bossStats"));
			String[] linePiece;
			String name;
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						name = reader.nextLine();
						reader.close();
						return name;						
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "John Doe";
	}
	/** Obtain the string representing our bosses class from our boss stats file located in res//res_characters/ */
	public String getBossClass(int whichChapter) {
		try {
			Scanner reader = new Scanner(new File("res//res_characters//bossStats"));
			String[] linePiece;
			String clas;
			while (reader.hasNextLine()) {
				linePiece = reader.nextLine().split(":");
				if (linePiece.length == 2) {
					if (whichChapter == Integer.valueOf(linePiece[1])) {
						//we are on the correct part
						reader.nextLine(); //name
						clas = reader.nextLine();
						reader.close();
						return clas;						
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "John Doe";
	}
	/** Starts a trade menu between two players */
	public void setTradeMenu(Player one, Player two) {
		this.tradeMenu = new TradeMenu(one, two);
		gameState = STATE.TradeState;
	}
	/** Starts a trade menu between a player and the convoy */
	public void setTradeMenu(Player one, Convoy convoy) {
		this.tradeMenu = new TradeMenu(one, convoy);
	}
	/** Erases our current cut scene and if appropriate brings us to Save Game state */
	public void destroyOutGameCutScene(boolean start) {
		
		// GO TO BATTLE PREPARATIONS MENU AFTER CHAPTER 5
		if (start) {
			cutScenes.nullAllScenes();
			
			if (chapterOrganizer.currentChapter >= 5) {
				// GO TO BATTLE PREPARATIONS MENU
				chapterOrganizer.setBattlePreparationMenu();
			} else {
				gameState = STATE.Game;
				this.chapterOrganizer.currentMap.setScreenToLord();
			}
			
		} else {
			cutScenes.nullAllScenes();
			gameLoader.isSaving = true;
			gameLoader.setSelectedIndex(chapterOrganizer.loadLevel-1);
			gameState = STATE.LoadGame;
		}
	}
	/** Starts the ending Chapter cut scene */
	public void endChapter() {
		if (chapterOrganizer.currentChapter == 15) {
			setGameState(STATE.playerFollowChoice);
			return;
		}
		cutScenes.startScene(chapterOrganizer.currentChapter, false);
		gameState = STATE.outGameCutScene;
	}
	/** The state where someone is attempting to use a key to unlock something */
	public void setKeyUseState(Key key) {
		if (pathGenerator != null) pathGenerator.resetTiles();
		setPathGenerator(new PathGenerator(this, chapterOrganizer.currentMap.currentTile, 1));
		pathGenerator.setAllPathableTiles(true);
		chapterOrganizer.currentMap.selectedBoxTile = chapterOrganizer.currentMap.currentTile;
		chapterOrganizer.currentMap.selectedKey = key;
		gameState = STATE.keyOpeningState;
	}
	/** Opens the options menu and enables selection */
	public void setOptionsMenuState() {
		optionsMenu = new OptionsMenu(this, chapterOrganizer.currentMap);
		gameState = STATE.optionsMenu;
	}
	/** Returns true if there are ally players within range distance of this player */
	public boolean alliesSurrounding(Player player, int range) {
		
		if (chapterOrganizer.allys.isEmpty()) return false;
		
		for (int i = 0; i < chapterOrganizer.allys.size(); i++) {
			Player p = chapterOrganizer.allys.get(i);
			if (p != player) {
				if (getTrueDist(p.currentTile, player.currentTile) <= range) return true;
			}
		}
		return false;
		
	}
	/** Sets the Armory or Vendor state from the player shopper */
	public void setArmoryHandler(Player shopper) {
		armoryHandler.setPlayer(shopper);
		gameState = STATE.Armory;
	}
	
	/** Returns true if there are enemy players within range distance of this player 
	 *  ALSO returns true if there are broken walls in range of this player */
	public boolean enemiesSurrounding(Player player, int range) {
		
		if (chapterOrganizer.allys.isEmpty()) return false;
		if (chapterOrganizer.enemys.isEmpty()) return false;
		
		for (int i = 0; i < chapterOrganizer.enemys.size(); i++) {
			Player p = chapterOrganizer.enemys.get(i);
			if (p != player) {
				if (getTrueDist(p.currentTile, player.currentTile) <= range) return true;
			}
		}
		
		for (int i = 0; i < chapterOrganizer.currentMap.tiles.size(); i++) {
			Tile t = chapterOrganizer.currentMap.tiles.get(i);
			if (t.category.equalsIgnoreCase("DamagedWall")) {
				if (getTrueDist(player.currentTile, t) <= range) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	/** Determines whether or not the state of the game returns to the enemy's turn or Game */
	public void handleEXPDecision() {
		if (AttackManager.attacker.teamID.equalsIgnoreCase("Ally")) {
			startEXPScene(false);
			return;
		} else {
			startEXPScene(true);
			//game.setGameState(STATE.EnemyPhase);  this line disables EXP scene on enemy turn
			return;
		}
	}
	
	/** 0 Easy, 1 Normal, 2 Hard, 3 Crushing */
	public int getDifficultyID() {
		if (gameDifficulty == DIFFICULTY.Easy) return 0;
		else if (gameDifficulty == DIFFICULTY.Normal) return 1;
		else if (gameDifficulty == DIFFICULTY.Hard) return 2;
		else if (gameDifficulty == DIFFICULTY.Crushing) return 3;
		else return 100;
	}
	
	public void setDevMode(boolean tf) {
		inDevMode = tf;
	}
	
	public ChapterOrganizer getChapterOrganizer() {
		return chapterOrganizer;
	}
}
