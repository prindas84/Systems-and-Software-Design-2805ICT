package com.group16.tetris.test;

import com.group16.tetris.utils.TetrisMusicPlayer;
import com.group16.tetris.utils.UI;
import com.group16.tetris.utils.UIFactory;
import com.group16.tetris.views.ConfigurationView;
import com.group16.tetris.views.GameView;
import com.group16.tetris.views.HighScoresView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class UIFactoryTest {
    UIFactory ui;
    @BeforeEach
    void setUp() {
        JFrame testFrame = new JFrame();
        CardLayout testCardLayout = new CardLayout();
        JPanel testPanel = new JPanel();
        TetrisMusicPlayer testTetrisMusicPlayer = new TetrisMusicPlayer("sounds/Background Music - 1.wav");
        ui = new UIFactory(testFrame, testCardLayout, testPanel, testTetrisMusicPlayer);
    }

    @Test
    void createUI() {
        assertTrue(ui.createUI("game") instanceof UI);
        assertTrue(ui.createUI("game") instanceof GameView);
        assertTrue(ui.createUI("highscores") instanceof UI);
        assertTrue(ui.createUI("highscores") instanceof HighScoresView);
        assertTrue(ui.createUI("configuration") instanceof UI);
        assertTrue(ui.createUI("configuration") instanceof ConfigurationView);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ui.createUI("unknown");
        });
        assertEquals("Unknown UI type", exception.getMessage());
    }
}