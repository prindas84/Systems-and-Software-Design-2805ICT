package com.group16.tetris.controllers;

import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.models.ConfigurationModel.PlayerType;

public class ConfigurationController {
    private final ConfigurationModel configManager; // Handles configuration settings

    // Constructor initialises the configuration manager
    public ConfigurationController(ConfigurationModel configManager) {
        this.configManager = configManager;
    }

    // Get the current configuration settings
    public ConfigurationModel getSettings() {
        return configManager;
    }

    // Update the field width setting
    public void updateFieldWidth(int width) {
        configManager.setFieldWidth(width);
    }

    // Update the field height setting
    public void updateFieldHeight(int height) {
        configManager.setFieldHeight(height);
    }

    // Update the game level setting
    public void updateGameLevel(int level) {
        configManager.setGameLevel(level);
    }

    // Set the type of Player 1
    public void setPlayer1Type(PlayerType type) {
        configManager.setPlayer1Type(type);
    }

    // Set the type of Player 2
    public void setPlayer2Type(PlayerType type) {
        configManager.setPlayer2Type(type);
    }

    // Enable or disable music
    public void setMusic(boolean isEnabled) {
        configManager.setMusic(isEnabled);
    }

    // Enable or disable sound effects
    public void setSoundEffects(boolean isEnabled) {
        configManager.setSoundEffects(isEnabled);
    }

    // Reset all settings to default values
    public void resetToDefaults() {
        configManager.resetToDefaults();
    }

    // Enable or disable extended game mode
    public void setExtendMode(boolean isEnabled) {
        configManager.setExtendMode(isEnabled);
    }
}
