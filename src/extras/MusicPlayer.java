package extras;

import java.util.ArrayList;

public class MusicPlayer implements Runnable {

	private ArrayList<AudioFile> musicFiles;
	private int currentSongIndex;
	public boolean running;
	public AudioFile song;
	public AudioFile tempSong;
	
	public MusicPlayer(String...files) {
		musicFiles = new ArrayList<>();
		currentSongIndex = 0;
		for (String file : files) {
			musicFiles.add(new AudioFile("./res/" + file+ ".wav"));
		}
		song = musicFiles.get(currentSongIndex);
		running = true;
	}
	
	@Override
	public void run() {
		running = true;
		song = musicFiles.get(currentSongIndex);
		song.play();
		while (running) {
			if (!song.isPlaying()) {
				currentSongIndex++;
				if (currentSongIndex >= musicFiles.size()) {
					currentSongIndex = 0;
				}
				song = musicFiles.get(currentSongIndex);
				song.play();
				oneSongPlaying();
			}
		}
	
	}
	
	private int oneSongPlaying() {
		int num = 0;
		for (AudioFile song : musicFiles) {
			if (song.isPlaying()) num++;
			if (num >= 2) song.stop();
		}
		return num;
	}
	
	public void nextSong() {
		song.stop();
	}
	
	public void playSong(int index) {
		song.stop();
		song = musicFiles.get(index);
		song.play();
		running = true;
	}

	public void playSong(String fileName) {
		int songindex = -1;
		for (int i = 0; i < musicFiles.size(); i++) {
			tempSong = musicFiles.get(i);
			if (tempSong.fileName.equalsIgnoreCase(fileName)) {
				songindex = i;
				break;
			}
		}
		if (songindex >= 0) {
			currentSongIndex = songindex-1;
			song.stop();
		}
	}
}
