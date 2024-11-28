package com.group16.tetris.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.group16.tetris.models.HighScoresModel;
import com.group16.tetris.models.PlayerModel;
import com.group16.tetris.models.ConfigurationModel.PlayerType;

public class HighScoresController {
    private final HighScoresModel highScores; // Manages high score data

    // Constructor to initialise the HighScoresModel
    public HighScoresController(HighScoresModel highScores) {
        this.highScores = highScores;
    }

    // Returns the top scores up to a given count
    public List<Map.Entry<String, Integer>> getTopScores(int count) {
        return highScores.getTopScores(count);
    }

    // Resets all high scores
    public void resetScores() {
        highScores.resetScores();
    }

    // Retrieves the player's configuration by name
    public String getPlayerConfig(String playerName) {
        return highScores.getConfig(playerName);
    }

    // Ensures a unique player name is generated if necessary
    public String newPlayerName(String playerName) {
        return highScores.newPlayerName(playerName);
    }

    // Constructs a configuration string based on the game settings
    public String constructConfig(int width, int height, int gameLevel, PlayerType playerType, boolean extendedMode) {
        return highScores.constructConfig(width, height, gameLevel, playerType, extendedMode);
    }

    // Adds a new player and their score to the high scores
    public void addNewPlayerAndScore(String playerName, String config, int score) {
        highScores.addNewPlayerAndScore(playerName, config, score);
    }

    // Loads high scores from a file and updates the player list
    public void loadScoresFromFile(ArrayList<PlayerModel> players) {
        highScores.loadFromFile(players);
    }

    // Returns the HighScoresModel for further access
    public HighScoresModel getScoresManager() {
        return highScores;
    }
}
