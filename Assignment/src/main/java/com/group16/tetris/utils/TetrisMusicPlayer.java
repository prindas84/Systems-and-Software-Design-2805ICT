package com.group16.tetris.utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class TetrisMusicPlayer {
    private Clip clip;
    private long clipTimePosition = 0;  // Position in the clip when paused
    private boolean isPlaying = false;
    private final String filePath;

    // Constructor to initialise the music file path
    public TetrisMusicPlayer(String filePath) {
        this.filePath = filePath;
        loadMusicFile(filePath);
    }

    // Load the audio file using the ClassLoader from the classpath
    private void loadMusicFile(String filePath) {
        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(filePath);
            if (audioSrc == null) {
                throw new IOException("Music file not found: " + filePath);
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to start playing the music in a new thread with a lambda
    public void playMusic() {
        if (clip != null && !isPlaying) {
            Thread musicThread = new Thread(() -> {
                clip.setMicrosecondPosition(clipTimePosition);  // Resume from last position
                clip.loop(Clip.LOOP_CONTINUOUSLY);              // Loop continuously
                clip.start();
            });
            musicThread.start();
            isPlaying = true;
        }
    }

    // Pause the music and save the position
    public void pauseMusic() {
        if (clip != null && clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();  // Pause the clip
            isPlaying = false;
        }
    }

    // Stop the music completely
    public void stopMusic() {
        if (clip != null) {
            clip.stop();  // Stop the music
            clip.setMicrosecondPosition(0);  // Reset the position to the start
            clipTimePosition = 0;
            isPlaying = false;
        }
    }
}

