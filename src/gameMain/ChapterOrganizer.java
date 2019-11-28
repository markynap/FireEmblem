package gameMain;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import characters.*;
import tiles.*;

public class ChapterOrganizer {

	public Game game;
	/**The chapters in this game*/
	public ChapterMap[] chapters = new ChapterMap[2];
	/**Chapter we are currently playing*/
	public int currentChapter;
	/**Chapter Map we are currently on*/
	public ChapterMap currentMap;
	/**All the allys in this game will be stored here */
	public LinkedList<AllyPlayer> allys = new LinkedList<>();
	/**All the enemys in this game will be stored here */
	public LinkedList<EnemyPlayer> enemys = new LinkedList<>();

	
	public ChapterOrganizer(Game game) {
		this.game = game;
		allys = new LinkedList<>();
		enemys = new LinkedList<>();
		chapters[0] = new ChapterMap(24, 30, game);
		chapters[1] = new ChapterMap(17, 34, game);
		currentChapter = 1;
		currentMap = chapters[0];
		currentMap.setTile(currentMap.getTileAtAbsolutePos(7, 8), new TreeTile(7, 8, currentMap));
	
	}
	
	public ChapterMap getChapterMap() {
		return chapters[currentChapter-1];
	}
	
	public void nextChapter() {
		currentMap = chapters[currentChapter];
		currentChapter++;
	}
	
	public void tick() {
		currentMap.tick();
		for (int i = 0; i < allys.size(); i++) {
			allys.get(i).tick();
		}
		for (int i = 0; i < enemys.size(); i++) {
			enemys.get(i).tick();
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		currentMap.render(g);
	//	Tiles will render each unit, for screen moving purposes 
	}
	
	public void addAlly(AllyPlayer ally) {
		if (ally == null) return;
		allys.add(ally);
		currentMap.getTileAtAbsolutePos(ally.xPos, ally.yPos).setCarrier(ally);
	}
	public void addEnemy(EnemyPlayer enemy) {
		if (enemy == null) return;
		enemys.add(enemy);
		currentMap.getTileAtAbsolutePos(enemy.xPos, enemy.yPos).setCarrier(enemy);
	}

}

