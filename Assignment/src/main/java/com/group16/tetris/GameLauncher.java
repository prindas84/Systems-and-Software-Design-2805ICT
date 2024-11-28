package com.group16.tetris;

import com.group16.tetris.utils.UIUtils;
import com.group16.tetris.views.ConfigurationView;
import com.group16.tetris.views.GameView;
import com.group16.tetris.views.HighScoresView;
import com.group16.tetris.views.SplashScreen;
import com.group16.tetris.utils.UIFactory;
import com.group16.tetris.controllers.GameController;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.utils.TetrisMusicPlayer;

import java.net.URL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameLauncher extends JFrame {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private GameController gameController;
    private ConfigurationModel configurationSettings;
    private TetrisMusicPlayer musicPlayer;

    // Constructor to initialise the game window and its components
    public GameLauncher() {
        setTitle("Tetris: Group 16 (2805ICT)");
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel mainPanel = createMainPanel();

        musicPlayer = new TetrisMusicPlayer("sounds/Background Music - 1.wav");
        UIFactory uiFactory = new UIFactory(this, cardLayout, cardPanel, musicPlayer);

        GameView playPanel = (GameView) uiFactory.createUI("game");
        ConfigurationView configurationPanel = (ConfigurationView) uiFactory.createUI("configuration");
        HighScoresView highScoresPanel = (HighScoresView) uiFactory.createUI("highscores");

        this.gameController = playPanel.getGameController();
        this.configurationSettings = ConfigurationModel.getInstance();

        // Set up the panels for switching between game views
        setupPanels(mainPanel, playPanel, configurationPanel, highScoresPanel);
        setupMainMenu(mainPanel, playPanel, configurationPanel, highScoresPanel);

        add(cardPanel);
        configureFrame(playPanel);
    }

    // Create the main menu panel with a background image
    private JPanel createMainPanel() {
        JPanel mainPanel;

        // Load the background image using getClass().getResource
        URL imageUrl = getClass().getResource("/images/background_image.jpg");

        // Create the main panel with a custom background image if available
        if (imageUrl != null) {
            ImageIcon backgroundImage = new ImageIcon(imageUrl);

            mainPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };
        } else {
            mainPanel = new JPanel(new GridBagLayout());
        }

        cardPanel.add(mainPanel, "Main Menu");
        return mainPanel;
    }

    // Set up the main menu buttons and their respective actions
    private void setupMainMenu(JPanel mainPanel, GameView playPanel, ConfigurationView configurationPanel, HighScoresView highScoresPanel) {
        GridBagConstraints gbc = createGridBagConstraints();

        // Add buttons for Play, Configuration, High Scores, and Exit
        addButtonToMenu("Play", mainPanel, gbc, () -> startGame(playPanel));
        addButtonToMenu("Configuration", mainPanel, gbc, () -> goToConfigurationPanel(configurationPanel));
        addButtonToMenu("High Scores", mainPanel, gbc, () -> goToHighScoresPanel(highScoresPanel));
        addButtonToMenu("Exit", mainPanel, gbc, () -> exitApplication(playPanel));
    }

    private void goToHighScoresPanel(HighScoresView highScoresPanel){
        setSize(new Dimension(900, 700));
        UIUtils.centerWindow(this);
        cardLayout.show(cardPanel, "High Scores");
    }

    private void goToConfigurationPanel(ConfigurationView configurationPanel){
        setSize(new Dimension(1000, 700));
        UIUtils.centerWindow(this);
        cardLayout.show(cardPanel, "Configuration");
    }

    // Add panels to the card layout for switching between views
    private void setupPanels(JPanel mainPanel, GameView playPanel, ConfigurationView configurationPanel, HighScoresView highScoresPanel) {
        cardPanel.add(playPanel.getPanel(), "Play");
        cardPanel.add(configurationPanel.getPanel(), "Configuration");
        cardPanel.add(highScoresPanel.getPanel(), "High Scores");
    }

    // Create layout constraints for the main menu
    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 130, 30, 130);
        return gbc;
    }

    // Add a button to the main menu with specified action
    private void addButtonToMenu(String label, JPanel panel, GridBagConstraints gbc, Runnable action) {
        JButton button = new JButton(label);

        // Set button style
        button.setFont(new Font("Georgia", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(300, 30));
        button.setMinimumSize(new Dimension(300, 30));
        button.setBackground(new Color(76, 67, 205));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true));

        // Disable button content area fill to prevent colour change on click
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.setMargin(new Insets(5, 20, 5, 20));

        // Adjust button placement
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 40;

        button.addActionListener(e -> action.run());
        panel.add(button, gbc);
    }

    // Start a new game by resetting variables and switching to the game view
    private void startGame(GameView playPanel) {
        gameController.resetGameVariables();

        int width = gameController.getConfiguration().getFieldWidth();
        int height = gameController.getConfiguration().getFieldHeight();
        int blockSize = gameController.getBlockSize();
        int widthSize = width * blockSize + 200 + 120;

        if (gameController.isExtendMode()){
            widthSize = 2 * (120 + width * blockSize) + 20 + 200;
        }
        int heightSize = height * blockSize + 200;

        if (widthSize < 500){ widthSize = 500; }
        if (heightSize < 700){ heightSize = 700; }

        setSize(new Dimension(widthSize, heightSize));
        UIUtils.centerWindow(this);

        if (configurationSettings.isMusicEnabled()) {
            musicPlayer.playMusic();
        }

        cardLayout.show(cardPanel, "Play");

    }

    // Handle exit confirmation and save scores/configuration before exiting
    private void exitApplication(GameView playPanel) {
        this.repaint();

        // Display exit confirmation dialog
        JOptionPane optionPane = new JOptionPane(
                "Are you sure to exit the application?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
        );

        JDialog dialog = optionPane.createDialog("Exit Tetris Application");

        UIUtils.removeButtonFocus(optionPane);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    GameLauncher.this.repaint();
                });
            }
        });

        dialog.setVisible(true);

        int response = (Integer) optionPane.getValue();

        if (response == JOptionPane.YES_OPTION) {
            gameController.writeScoresJsonFile();
            gameController.writeConfigurationJsonFile();

            System.exit(0);
        } else {
            SwingUtilities.invokeLater(() -> {
                this.repaint();
            });
        }
    }

    // Configure the main JFrame window
    private void configureFrame(GameView playPanel) {
        setSize(new Dimension(500, 700));
        UIUtils.centerWindow(this);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameController.writeScoresJsonFile();
                gameController.writeConfigurationJsonFile();

                System.exit(0);
            }
        });
    }

    // Main method to launch the game
    public static void main(String[] args) {
        SplashScreen splashScreen = new SplashScreen(3000);
        splashScreen.showSplashScreen();

        // Ensure the game launcher runs on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(GameLauncher::new);
    }
}
