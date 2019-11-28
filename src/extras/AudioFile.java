package extras;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class AudioFile implements LineListener{
	
	private File soundFile;
	public String fileName;
	private AudioInputStream ais;
	private AudioFormat format;
	private DataLine.Info info;
	private Clip clip;
	private FloatControl gainControl;
	private volatile boolean playing;
	
	public AudioFile(String fileName) {
		soundFile = new File(fileName);
		this.fileName = fileName;
		try {
			soundFile = new File(fileName);
			ais = AudioSystem.getAudioInputStream(soundFile);
			format = ais.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip)AudioSystem.getLine(info);
			clip.addLineListener(this);
			clip.open(ais);
			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-15.0f);
			
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Problem is in AudioFile constructor");
		}
	}
	
	public void play() {
		clip.start();
		playing = true;
	}
	public void stop() {
		clip.stop();
		clip.flush();
	//	clip.setFramePosition(0);
		playing = false;
	}
	public void play(float volumeDiff) {
		gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(volumeDiff);
		clip.start();
		playing = true;
	}
	public boolean isPlaying() {
		return playing;
	}

	@Override
	public void update(LineEvent event) {

		if (event.getType() == LineEvent.Type.START) {
			playing = true;
		} else if (event.getType() == LineEvent.Type.STOP) {
			clip.stop();
			clip.flush();
			clip.setFramePosition(0);
			playing = false;
		}
		
	}
	
}
