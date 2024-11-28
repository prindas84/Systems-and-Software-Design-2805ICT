package com.group16.tetris.models;

import java.util.*;

import com.group16.tetris.models.ConfigurationModel.PlayerType;

public class HighScoresModel {

    private Map<String, Integer> scoresMap;  // Stores player names and their scores
    private List<Map.Entry<String, Integer>> topScores;  // Holds the sorted list of top scores
    private Map<String, String> config;  // Stores player configurations

    // Constructor to initialise high scores and configurations
    public HighScoresModel() {
        this.scoresMap = new HashMap<>();
        this.topScores = new ArrayList<>();
        this.config = new HashMap<>();
    }

    // Updates the top scores by sorting scores in descending order
    private void updateTopScores() {
        topScores = new ArrayList<>(scoresMap.entrySet());
        topScores.sort(valueComparator());
    }

    // Retrieves the top scores up to the specified limit
    public List<Map.Entry<String, Integer>> getTopScores(int limit) {
        return topScores.subList(0, Math.min(limit, topScores.size()));
    }

    // Adds a new player with a score and configuration
    public void addNewPlayerAndScore(String playerName, String configuration, int score) {
        scoresMap.put(playerName, score);
        config.put(playerName, configuration);
        updateTopScores();
    }

    // Loads scores and configurations from a list of PlayerModel
    public void loadFromFile(ArrayList<PlayerModel> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            scoresMap.put(list.get(i).getName(), list.get(i).getScore());
            config.put(list.get(i).getName(), list.get(i).getConfig());
        }
        updateTopScores();
    }

    // Generates a unique player name if the provided name already exists
    public String newPlayerName(String playerName) {
        if (scoresMap.containsKey(playerName)) {
            int num = 1;
            String newPlayerName = playerName;
            while (scoresMap.containsKey(newPlayerName)) {
                newPlayerName = playerName.concat(String.valueOf(num));
                num++;
            }
            return newPlayerName;
        }
        return playerName;
    }

    // Resets all scores and configurations
    public void resetScores() {
        scoresMap = new HashMap<>();
        topScores = new ArrayList<>();
        config = new HashMap<>();
    }

    // Retrieves the configuration for a given player
    public String getConfig(String playerName) {
        return config.get(playerName);
    }

    // Constructs a configuration string based on game settings
    public String constructConfig(int width, int height, int gameLevel, PlayerType typeOfPlayer, boolean extended) {
        String result = String.valueOf(width);
        result = result + "x" + String.valueOf(height) + "(" + String.valueOf(gameLevel) + ") ";
        if (typeOfPlayer == PlayerType.HUMAN) {
            result = result + "Human ";
        } else if (typeOfPlayer == PlayerType.AI) {
            result = result + "AI ";
        } else {
            result = result + "External ";
        }
        if (!extended) {
            result = result + "Single";
        } else {
            result = result + "Double";
        }
        return result;
    }

    // Comparator to sort scores in descending order
    public static Comparator<Map.Entry<String, Integer>> valueComparator() {
        return (element1, element2) -> element2.getValue().compareTo(element1.getValue());
    }
}
