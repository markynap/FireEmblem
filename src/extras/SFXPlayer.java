package extras;

import java.util.ArrayList;
import java.util.TreeMap;

public class SFXPlayer implements Runnable{
	
	private ArrayList<AudioFile> musicFiles;
	private int currentSFXIndex;
	private boolean running;
	public AudioFile song;
	public AudioFile tempSong;
	private TreeMap<Integer, Integer> songToVolume;
	
	public SFXPlayer(String...files) {
		musicFiles = new ArrayList<>();
		songToVolume = new TreeMap<>();
		currentSFXIndex = 0;
		for (String file : files) {
			musicFiles.add(new AudioFile("./res/" + file+ ".wav"));
		}
		songToVolume.put(0, -9);
		songToVolume.put(1, -5);
		songToVolume.put(2, -9);
		songToVolume.put(3, 3);
		songToVolume.put(4, -5);
	}
	@Override
	public void run() {
		running = false;
		song = musicFiles.get(currentSFXIndex);
		while (running) {
			song.play();
			if (!song.isPlaying()) {
				running = false;
			}
		}
	
	}
	public void playSong(int index) {
		song = musicFiles.get(index);
		song.play(songToVolume.get(index));
	}
	//"Cursor", "LevelUp", "Select", "Experience", "GameOver"
	public void playCursor() {
		playSong(0);
	}
	public void playLevelUp() {
		playSong(1);
	}
	public void playSelect() {
		playSong(2);
	}
	public void playExperience() {
		playSong(3);
	}
	public void playGameOver() {
		playSong(4);
	}
	public void setSFXIndex(int index) {
		if (index < musicFiles.size())
		currentSFXIndex = index;
	}
	
	public void nextSong() {
		song.stop();
	}
}
