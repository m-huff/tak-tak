package tak.util;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import tak.com.TakTakMain;

public class Sound implements Runnable {

    Thread myThread;
    File soundFile;
    SourceDataLine src;
    public boolean donePlaying = false;

    public Sound(String _name) {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }

    public void run() {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
            src = source;
            source.open(format);
            source.start();
            int read = 0;
            byte[] audioData = new byte[16384];
            boolean isMuted = TakTakMain.muted;
            while (read > -1) {
            	if (!TakTakMain.muted) {
	                read = ais.read(audioData, 0, audioData.length);
	                if (read >= 0) {
	                    source.write(audioData, 0, read);
	                }
            	}
                isMuted = TakTakMain.muted;
            }
            donePlaying = true;

            source.drain();
            source.close();
        } catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
    
    public void stop() {
    	myThread.stop();
    	src.drain();
    	src.close();
    }
}