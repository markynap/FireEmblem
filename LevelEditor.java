package chapterDesign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import gameMain.ChapterMap;
import gameMain.ChapterOrganizer;
import gameMain.Game;
import gameMain.Game.STATE;
import tiles.Tile;

public class LevelEditor {
	/**current level map we are constructing*/
	public ChapterMap currentMap;
	public Game game;
	public ChapterDesigner designer;
	/**The file we are currently editing */
	public File currentFile;
	/**Responsible for reading from this file */
	public Scanner reader;
	/**Responsible for writing to this file*/
	public PrintWriter writer;
	/**Holds the tiles when the chapter is constructed*/
	public ArrayList<String[]> stringMap;
	/** File containing Tile ID information*/
	private File tileIDFile;
	
	public Map<String, Integer> tileIDMap;
	
	public LevelEditor(Game game, File currentFile) {
		this.game = game;
		this.designer = game.designer;
		this.currentFile = currentFile;
		tileIDMap = new TreeMap<>();
		setReaderToChapterFile();
		try {
		setCurrentMap();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("The file " + currentFile.getName() + " is empty");
			return;
		}
		game.setGameState(STATE.ChapterDesign);
		tileIDFile = new File("res\\designInfo\\tileIDs");
		
		setTileIDMap();
		
	}
	
	public void saveContents() {

		try {
			this.writer = new PrintWriter(currentFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("LevelEditor saveContents() could not read currentFile!");
		}
		currentMap = designer.organizer.currentMap;
		ArrayList<ArrayList<Tile>> multiList = new ArrayList<>();
		ArrayList<Tile> tempList = new ArrayList<>();
		for (int i = 0; i < currentMap.tiles.size(); i++) {
			tempList.add(currentMap.tiles.get(i));
		//	tilePlayerMap.put(currentMap.tiles.get(i), currentMap.tiles.get(i).category);
			if (i == 0) continue;
			if ((i+1) % currentMap.cols == 0) {
				if (!tempList.isEmpty()) multiList.add(tempList);
				tempList = new ArrayList<>();
			}
		}
		for (ArrayList<Tile> list : multiList) {
			for (Tile tile : list) {
		//		writer.print(getString(category) + " ");
				writer.print(getString(tile.category) + "-" + tile.getCarrierID() +" ");
			}
			writer.println();
		}
		writer.close();
	}
	
	private String getString(String category) {
		if (tileIDMap.get(category) == null) return "null";
		return String.valueOf(tileIDMap.get(category));
	}
	/**Responsible for searching the file for number of rows and columns*/
	public void setCurrentMap() {
		String[] stringLine;
		stringMap = new ArrayList<>();
		setReaderToChapterFile();
		while (reader.hasNextLine()) {
			stringLine = reader.nextLine().split(" ");
			stringMap.add(stringLine);
		}
		currentMap = new ChapterMap(stringMap.get(0).length, stringMap.size(), game);
				
		designer.organizer = new ChapterOrganizer(game, currentMap, 0);
		designer.organizer.setTiles(stringMap, currentMap, game, 0);
		
	}
	/** Sets our reader to read the next chapter file */
	private void setReaderToChapterFile() {
		try {
			this.reader = new Scanner(currentFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("LevelEditor constructor could not read currentFile!");
		}
	}
	/** Initiates our TileIDMap */
	private void setTileIDMap() {
		try {
			reader = new Scanner(tileIDFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(tileIDFile.getName() + " not found");
		}
		
		while (reader.hasNextLine()) {
			String[] line = reader.nextLine().split(" ");
			tileIDMap.put(line[0], Integer.valueOf(line[1]));
		}
		
	}
	
}
