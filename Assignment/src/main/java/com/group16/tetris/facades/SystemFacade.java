package com.group16.tetris.facades;

import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.models.GameModel;
import com.group16.tetris.utils.JsonReaderAndWriter;
import com.group16.tetris.views.ConfigurationView;
import com.group16.tetris.views.HighScoresView;
import com.group16.tetris.controllers.HighScoresController;

public class SystemFacade {
    private GameModel gameManager;  // Manages the current Tetris game state
    private JsonReaderAndWriter JsonReaderAndWriter;  // Handles reading and writing to JSON files
    private ConfigurationView configurationUI;  // Links configuration settings to the game
    private HighScoresView highScoresUI;  // Manages the display of high scores
    private HighScoresController highScoresController;  // Controls high scores logic

    // Constructor to initialise the facade with game, configuration, and high scores management
    public SystemFacade(GameModel gameManager, ConfigurationView configurationUI, HighScoresView highScoresUI, HighScoresController highScoresController) {
        this.gameManager = gameManager;
        this.highScoresUI = highScoresUI;
        this.configurationUI = configurationUI;
        this.highScoresController = highScoresController;
        this.JsonReaderAndWriter = new JsonReaderAndWriter();
    }

    // Resets game variables based on the current configuration
    public void resetGameVariables() {
        ConfigurationModel settings = configurationUI.getSettings();
        gameManager.resetGame(settings.getFieldWidth(), settings.getFieldHeight(), settings.getGameLevel(), settings.isMusicEnabled(),
                settings.isSoundEffectsEnabled(), settings.getPlayer1Type(), settings.getPlayer2Type(), settings.isExtendModeEnabled());
        gameManager.requestFocusInWindow();  // Ensures the game has focus for key inputs
    }

    // Adds a new high score with the current configuration
    public void addNewHighScore(String playerName, int score, int gameLevel, int player) {
        playerName = highScoresController.newPlayerName(playerName);  // Ensures unique player name if needed
        String ConfigurationString;
        ConfigurationModel settings = configurationUI.getSettings();

        if (player == 1) {
            ConfigurationString = highScoresController.constructConfig(settings.getFieldWidth(), settings.getFieldHeight(), gameLevel,
                    settings.getPlayer1Type(), settings.isExtendModeEnabled());
        } else {
            ConfigurationString = highScoresController.constructConfig(settings.getFieldWidth(), settings.getFieldHeight(), gameLevel,
                    settings.getPlayer2Type(), settings.isExtendModeEnabled());
        }

        highScoresController.addNewPlayerAndScore(playerName, ConfigurationString, score);  // Add player to high scores
        highScoresUI.updateNameAndScoreLabels();  // Update the UI with new high scores
    }

    // Saves high scores to a JSON file
    public void writeScoresJsonFile() {
        JsonReaderAndWriter.writeScoresJson(highScoresController.getScoresManager());
    }

    // Saves the current configuration to a JSON file
    public void writeConfigurationJsonFile() {
        JsonReaderAndWriter.writeConfigurationFile();
    }

    // Loads configuration from the JSON file and updates the UI
    public void loadConfiguration() {
        JsonReaderAndWriter.readConfigurationJson();
        configurationUI.updateUIComponents();
        JsonReaderAndWriter.clearConfigurationFile();
    }

    // Loads high scores from the JSON file and updates the UI
    public void loadScores() {
        JsonReaderAndWriter.readScoresJson();
        highScoresController.loadScoresFromFile(JsonReaderAndWriter.getPlayers());
        highScoresUI.updateNameAndScoreLabels();
        JsonReaderAndWriter.clearScoresFile();  // Resets the high scores file
    }

    public GameModel getGameManager() {
        return gameManager;
    }

    public ConfigurationView getConfigurationUI() {
        return configurationUI;
    }
}
