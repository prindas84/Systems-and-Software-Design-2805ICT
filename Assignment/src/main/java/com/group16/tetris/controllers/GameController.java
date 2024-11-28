package com.group16.tetris.controllers;

import com.group16.tetris.models.GameModel;
import com.group16.tetris.facades.SystemFacade;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.utils.UIUtils;
import com.group16.tetris.utils.TetrisMusicPlayer;

import javax.swing.*;
import java.awt.*;

public class GameController {
    private final GameModel gameManager;
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel parentPanel;
    private TetrisMusicPlayer musicPlayer;
    private ConfigurationModel configurationSettings;
    private SystemFacade gameFacade;

    // Constructor to initialise GameController with required components
    public GameController(GameModel gameManager, JFrame frame, CardLayout cardLayout, JPanel parentPanel, TetrisMusicPlayer musicPlayer) {
        this.gameManager = gameManager;
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.parentPanel = parentPanel;
        this.musicPlayer = musicPlayer;
        this.configurationSettings = ConfigurationModel.getInstance();
    }

    // Setter for the SystemFacade
    public void setGameFacade(SystemFacade gameFacade) {
        this.gameFacade = gameFacade;
    }

    // Handles the behaviour when the back button is pressed
    public void handleBackButton() {
        boolean wasPaused = gameManager.isGamePaused();
        boolean gameOver = gameManager.isGameOver();
        boolean wasPaused2 = gameManager.isGamePaused2();
        boolean gameOver2 = gameManager.isGameOver2();

        gameManager.pauseGame();
        gameManager.pauseGame2();

        // Pause music if enabled
        if (configurationSettings.isMusicEnabled()) {
            musicPlayer.pauseMusic();
        }

        if (!gameOver || !gameOver2) {
            JOptionPane optionPane = new JOptionPane(
                    "Are you sure you want to stop the current game?",
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION
            );
            JDialog dialog = optionPane.createDialog("Exit Game");
            UIUtils.removeButtonFocus(optionPane);
            dialog.setVisible(true);

            int response = (Integer) optionPane.getValue();

            if (response == JOptionPane.YES_OPTION) {
                // Stop music if enabled
                if (configurationSettings.isMusicEnabled()) {
                    musicPlayer.stopMusic();
                }
                exitGameWithHighScores();
                navigateToMainMenu();
            } else {
                resumeGamesIfNeeded(wasPaused, wasPaused2);
            }
        } else {
            exitGameWithHighScores();
            navigateToMainMenu();
        }
    }

    // Handles game exit and manages high scores if required
    private void exitGameWithHighScores() {
        if (gameManager.isExtendMode()) {
            handleHighScoreForPlayer(1);
            handleHighScoreForPlayer(2);
            gameManager.endGame2();
        } else {
            handleHighScoreForPlayer(1);
        }
        gameManager.endGame();
    }

    // Records the high score for a given player if applicable
    private void handleHighScoreForPlayer(int playerNumber) {
        if (playerNumber == 1 && gameManager.getScore() > 0) {
            String playerName = JOptionPane.showInputDialog(null, "Enter name for Player 1:", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
            if (playerName != null) {
                gameFacade.addNewHighScore(playerName, gameFacade.getGameManager().getScore(), gameFacade.getGameManager().getGameLevelPlayer1(), 1);
            }
        } else if (playerNumber == 2 && gameFacade.getGameManager().getScore2() > 0) {
            String playerName = JOptionPane.showInputDialog(null, "Enter name for Player 2:", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
            if (playerName != null) {
                gameFacade.addNewHighScore(playerName, gameFacade.getGameManager().getScore2(), gameFacade.getGameManager().getGameLevelPlayer2(), 2);
            }
        }
    }

    // Resumes games if they were paused before exiting the menu
    private void resumeGamesIfNeeded(boolean wasPaused1, boolean wasPaused2) {
        if (!wasPaused1) {
            gameManager.resumeGame();
        } else {
            gameManager.pauseGame();
        }

        if (!wasPaused2) {
            gameManager.resumeGame2();
        } else {
            gameManager.pauseGame2();
        }

        gameManager.requestFocusInWindow();
    }

    // Navigates back to the main menu
    private void navigateToMainMenu() {
        frame.setSize(new Dimension(500, 700));
        UIUtils.centerWindow(frame);
        cardLayout.show(parentPanel, "Main Menu");
    }

    // Loads scores from the system facade
    public void loadScores() {
        gameFacade.loadScores();
    }

    // Loads configuration settings from the system facade
    public void loadConfiguration() {
        gameFacade.loadConfiguration();
    }

    // Resets game variables using the system facade
    public void resetGameVariables() {
        gameFacade.resetGameVariables();
    }

    // Returns the current configuration settings
    public ConfigurationModel getConfiguration() {
        return gameFacade.getConfigurationUI().getSettings();
    }

    // Retrieves the block size for the game
    public int getBlockSize() {
        return gameFacade.getGameManager().getBlockSize();
    }

    // Checks if extended mode is enabled
    public boolean isExtendMode() {
        return gameFacade.getGameManager().isExtendMode();
    }

    // Writes high scores to a JSON file
    public void writeScoresJsonFile() {
        gameFacade.writeScoresJsonFile();
    }

    // Writes configuration settings to a JSON file
    public void writeConfigurationJsonFile() {
        gameFacade.writeConfigurationJsonFile();
    }

    // Retrieves the game panel
    public JPanel getGamePanel() {
        return gameManager.getGamePanel();
    }
}
