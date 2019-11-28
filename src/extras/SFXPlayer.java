package extras;

import java.util.ArrayList;

public class SFXPlayer implements Runnable{
	
	private ArrayList<AudioFile> musicFiles;
	private int currentSFXIndex;
	private boolean running;
	public AudioFile song;
	public AudioFile tempSong;
	
	public SFXPlayer(String...files) {
		musicFiles = new ArrayList<>();
		currentSFXIndex = 0;
		for (String file : files) {
			musicFiles.add(new AudioFile("./res/" + file+ ".wav"));
		}
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
		int val = 0;
		if (index == 0) val = -9;
		if (index == 1) val = -5;
		if (index == 2) val = -9;
		if (index == 4) val = 3;
		song.play(val);
	}
	public void setSFXIndex(int index) {
		if (index < musicFiles.size())
		currentSFXIndex = index;
	}
	
	public void nextSong() {
		song.stop();
	}
}
