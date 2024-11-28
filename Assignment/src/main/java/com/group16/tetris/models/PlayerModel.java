package com.group16.tetris.models;

public class PlayerModel {
    // Represents a player with a name, score, and game configuration
    private String name;
    private int score;
    private String config;

    // Constructor to initialise the player model with a name, score, and configuration
    public PlayerModel(String name, int score, String config) {
        this.name = name;
        this.score = score;
        this.config = config;
    }

    // Getter and setter methods for player configuration
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    // Getter and setter methods for player name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter methods for player score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
