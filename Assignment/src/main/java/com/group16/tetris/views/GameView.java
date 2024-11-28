package com.group16.tetris.views;

import com.group16.tetris.controllers.GameController;
import com.group16.tetris.utils.UI;

import javax.swing.*;
import java.awt.*;

public class GameView extends View implements UI {

    private final GameController gameController;  // Manages game state and logic

    // Constructor sets up the game view with game controls and layout
    public GameView(CardLayout cardLayout, JPanel main, GameController gameController) {
        super(cardLayout, main);  // Pass layout and panel to the parent class
        this.gameController = gameController;  // Initialise GameController

        initialiseComponents();  // Set up UI components
    }

    // Create and organise the game panel layout
    @Override
    protected void initialiseComponents() {
        this.setLayout(new BorderLayout());  // Use BorderLayout for positioning components
        JPanel gamePanel = new JPanel(new GridBagLayout());  // GridBagLayout for flexible positioning of the game area
        gamePanel.setPreferredSize(new Dimension(600, 600));  // Set game panel size
        gamePanel.add(gameController.getGamePanel());  // Add game content from GameController

        this.add(gamePanel, BorderLayout.CENTER);  // Place game panel at the centre of the layout
        addBackButtonPanel(cardLayout, this);  // Add the back button at the bottom
    }

    // Create and add a back button
    @Override
    public void addBackButtonPanel(CardLayout cardLayout, JPanel main) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));  // Single button layout
        JButton backButton = new JButton("Back");  // Back button to return to previous screen
        backButton.addActionListener(e -> gameController.handleBackButton());  // Handle back button press
        buttonPanel.add(backButton);  // Add button to panel
        this.add(buttonPanel, BorderLayout.SOUTH);  // Add panel to the bottom of the layout
    }

    // Return GameController instance for external use
    public GameController getGameController() {
        return gameController;
    }

    // Return the panel containing the UI components
    public JPanel getPanel() {
        return this;
    }
}
