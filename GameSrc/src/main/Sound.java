/*
 * Name: Rafay
 * Course Code: ICS4U0
 * Date: 1/19/2026
 * Description: Sound class that manages audio playback for the game.
 *              Handles loading, playing, looping, stopping sounds, and volume control.
 */

package main; // Declares the package name for this class

import java.net.URL; // Imports URL class for locating sound file resources
import javax.sound.sampled.AudioInputStream; // Imports class for reading audio data
import javax.sound.sampled.AudioSystem; // Imports class for accessing audio system resources
import javax.sound.sampled.Clip; // Imports class for playing audio clips
import javax.sound.sampled.FloatControl; // Imports class for controlling audio properties like volume

public class Sound { // Defines the Sound class for audio management
    
    Clip clip; // Holds the current audio clip being played
    URL soundURL[] = new URL[30]; // Array to store URLs of up to 30 sound files
    float volume = 1.0f; // Current volume level: 0.0f = mute, 1.0f = maximum volume
    
    public Sound() { // Constructor that initializes sound file paths
        soundURL[0] = getClass().getResource("/sound/BlueBoyAdventure.wav"); // Loads background music file
        soundURL[1] = getClass().getResource("/sound/coin.wav"); // Loads coin collection sound
        soundURL[2] = getClass().getResource("/sound/fanfare.wav"); // Loads victory/fanfare sound
        soundURL[3] = getClass().getResource("/sound/powerUp.wav"); // Loads power-up sound effect
        soundURL[4] = getClass().getResource("/sound/unlock.wav"); // Loads unlock/door sound
        soundURL[5] = getClass().getResource("/sound/cursor.wav"); // Loads menu cursor sound
        soundURL[6] = getClass().getResource("/sound/cuttree.wav"); // Loads tree cutting sound
    }
    
    public void setFile(int i) { // Loads and prepares a sound file at index i for playback
        try { // Begins exception handling block
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]); // Creates audio stream from the sound file
            clip = AudioSystem.getClip(); // Gets a new audio clip from the system
            clip.open(ais); // Opens the audio stream in the clip
            setVolume(volume); // Applies the current volume setting to the clip
        } catch (Exception e) { // Catches any exceptions during audio loading
            e.printStackTrace(); // Prints error details to console
        }
    }
    
    public void play() { // Plays the currently loaded sound clip once
        if (clip != null) { // Checks if a clip is loaded
        	clip.start(); // Starts playing the clip from the beginning
        }
    }
    
    public void loop() { // Plays the currently loaded sound clip continuously
        if (clip != null) { // Checks if a clip is loaded
        	clip.loop(Clip.LOOP_CONTINUOUSLY); // Loops the clip indefinitely
        }
    }
    
    public void stop() { // Stops playback of the current sound clip
        if (clip != null) { // Checks if a clip is loaded
        	clip.stop(); // Stops the clip immediately
        }
    }
    
    public void setVolume(float vol) { // Sets the volume level for audio playback
        // vol parameter: 0.0f (mute) to 1.0f (maximum volume)
        volume = vol; // Stores the new volume value
        if (clip != null) { // Checks if a clip is currently loaded
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // Gets the volume control
            float min = gain.getMinimum(); // Gets minimum dB value (usually -80 dB)
            float max = gain.getMaximum(); // Gets maximum dB value (usually 6 dB)
            float dB = min + (max - min) * volume; // Converts linear volume (0-1) to decibel scale
            gain.setValue(dB); // Applies the calculated volume in decibels
        }
    }
}