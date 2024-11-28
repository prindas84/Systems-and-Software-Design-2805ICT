package com.group16.tetris.utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class TetrisSoundEffectsPlayer {

    private final Clip gameOverClip;
    private final Clip gameOverClip2;
    private final Clip moveBlockClip;
    private final Clip rotateBlockClip;
    private final Clip settleBlockClip;
    private final Clip eraseRowClip;

    // Constructor to load all sound effects
    public TetrisSoundEffectsPlayer() {
        gameOverClip = loadSoundEffect("sounds/Game Over - 1.wav");
        gameOverClip2 = loadSoundEffect("sounds/Game Over - 2.wav");
        moveBlockClip = loadSoundEffect("sounds/Right - Left.wav");
        rotateBlockClip = loadSoundEffect("sounds/Rotate.wav");
        settleBlockClip = loadSoundEffect("sounds/Settle.wav");
        eraseRowClip = loadSoundEffect("sounds/Row Erase - 1.wav");
    }

    // Method to load a sound effect and return a Clip object
    private Clip loadSoundEffect(String filePath) {
        try {
            // Use ClassLoader to load the audio file from the resources folder
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(filePath);
            if (audioSrc == null) {
                throw new IOException("Sound effect file not found: " + filePath);
            }

            // Convert InputStream to AudioInputStream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to play a sound effect Clip if it exists
    private void playSoundEffect(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);  // Rewind to the beginning of the clip
            clip.start();  // Play the sound effect
        }
    }

    // Method to play Game Over sound effect
    public void playGameOverSound() {
        playSoundEffect(gameOverClip);
    }

    // Method to play Game Over sound effect
    public void playGameOverSound2() {
        playSoundEffect(gameOverClip2);
    }

    // Method to play Move Block sound effect
    public void playMoveBlockSound() {
        playSoundEffect(moveBlockClip);
    }

    // Method to play Rotate Block sound effect
    public void playRotateBlockSound() {
        playSoundEffect(rotateBlockClip);
    }

    // Method to play Settle Block sound effect
    public void playSettleBlockSound() {
        playSoundEffect(settleBlockClip);
    }

    // Method to play Erase Row sound effect
    public void playEraseRowSound() {
        playSoundEffect(eraseRowClip);
    }

    // Clean up and release resources for the clips
    public void close() {
        if (gameOverClip != null) gameOverClip.close();
        if (moveBlockClip != null) moveBlockClip.close();
        if (rotateBlockClip != null) rotateBlockClip.close();
        if (settleBlockClip != null) settleBlockClip.close();
        if (eraseRowClip != null) eraseRowClip.close();
    }
}
