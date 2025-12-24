package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    Clip clip;
    URL soundURL[] = new URL[30];
    float volume = 1.0f; // 0.0f = mute, 1.0f = max

    public Sound() {
        soundURL[0] = getClass().getResource("/sound/BlueBoyAdventure.wav");
        soundURL[1] = getClass().getResource("/sound/coin.wav");
        soundURL[2] = getClass().getResource("/sound/fanfare.wav");
        soundURL[3] = getClass().getResource("/sound/powerUp.wav");
        soundURL[4] = getClass().getResource("/sound/unlock.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            setVolume(volume); // apply current volume
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
        	clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
        	clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
        	clip.stop();
        }
    }

    public void setVolume(float vol) {
        // vol: 0.0f to 1.0f
        volume = vol;
        if (clip != null) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gain.getMinimum(); // usually -80 dB
            float max = gain.getMaximum(); // usually 6 dB
            float dB = min + (max - min) * volume;
            gain.setValue(dB);
        }
    }
}
