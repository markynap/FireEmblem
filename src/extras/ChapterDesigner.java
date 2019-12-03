package extras;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import gameMain.Game;

public class ChapterDesigner {
	/** Used for writing chapter information to a text file to design chapters */
	public PrintWriter writer;
	/** The current file chapter we are working on */
	public File currentFile;
	/** The reader used to read from files */
	public Scanner fileReader;
	/**
	 * Creates a new ChapterDesigner that will save files into a folder called /chapters
	 */
	public ChapterDesigner() {
		new Game(true);
	}
	
	public void addChapter(String filename) {
		//goes through the current map line by line saving the data to a text file
		//with the name /chapters/filename.txt
	}
}
