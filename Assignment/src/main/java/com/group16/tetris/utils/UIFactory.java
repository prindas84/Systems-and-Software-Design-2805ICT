package com.group16.tetris.utils;

import com.group16.tetris.controllers.GameController;
import com.group16.tetris.controllers.HighScoresController;
import com.group16.tetris.controllers.ConfigurationController;
import com.group16.tetris.facades.SystemFacade;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.models.GameModel;
import com.group16.tetris.models.HighScoresModel;
import com.group16.tetris.views.ConfigurationView;
import com.group16.tetris.views.GameView;
import com.group16.tetris.views.HighScoresView;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class UIFactory {
    private ConfigurationView configurationUI;
    private ConfigurationController configurationController;
    private HighScoresView highScoresUI;
    private HighScoresController highScoresController;

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel panel;

    private TetrisMusicPlayer musicPlayer;

    // Constructor to initialise UI components and controllers
    public UIFactory(JFrame frame, CardLayout cardLayout, JPanel panel, TetrisMusicPlayer musicPlayer) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.panel = panel;
        this.musicPlayer = musicPlayer;

        ConfigurationModel configModel = ConfigurationModel.getInstance();
        this.configurationController = new ConfigurationController(configModel);
        this.configurationUI = new ConfigurationView(this.cardLayout, this.panel, configurationController);

        HighScoresModel highScoresModel = new HighScoresModel();
        this.highScoresController = new HighScoresController(highScoresModel);
        this.highScoresUI = new HighScoresView(this.cardLayout, this.panel, highScoresController);
    }

    // Creates and returns the appropriate UI based on the type
    public UI createUI(String type) {
        switch (type.toLowerCase()) {
            case "game":
                return createGameUI();
            case "highscores":
                return highScoresUI;
            case "configuration":
                return configurationUI;
            default:
                throw new IllegalArgumentException("Unknown UI type");
        }
    }

    // Creates and returns the Game UI
    private UI createGameUI() {
        GameModel gameModel = createGameManager();
        GameController gameController = new GameController(gameModel, frame, cardLayout, panel, musicPlayer);
        SystemFacade gameFacade = new SystemFacade(gameModel, configurationUI, highScoresUI, highScoresController);

        gameController.setGameFacade(gameFacade);  // Link game controller with facade

        gameFacade.loadScores();
        gameFacade.loadConfiguration();

        return new GameView(cardLayout, panel, gameController);
    }

    // Creates and returns the GameModel using current configuration settings
    public GameModel createGameManager() {
        ConfigurationModel config = configurationController.getSettings();

        return new GameModel(
                config.getFieldWidth(),
                config.getFieldHeight(),
                config.getGameLevel(),
                config.isMusicEnabled(),
                config.isSoundEffectsEnabled(),
                config.getPlayer1Type(),
                config.getPlayer2Type(),
                config.isExtendModeEnabled(),
                musicPlayer
        );
    }
}
