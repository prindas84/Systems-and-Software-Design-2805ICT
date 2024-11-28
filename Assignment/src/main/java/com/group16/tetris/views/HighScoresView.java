package com.group16.tetris.views;

import com.group16.tetris.controllers.HighScoresController;
import com.group16.tetris.utils.UIUtils;
import com.group16.tetris.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HighScoresView extends View implements UI {

    private final HighScoresController highScoresController;  // Manages high score logic
    private final List<JLabel> nameLabels;                    // Labels for player names
    private final List<JLabel> scoreLabels;                   // Labels for player scores
    private final List<JLabel> configLabels;                  // Labels for configuration info

    private static final int HEADER_FONT_SIZE = 20;           // Font size for the header
    private static final int TABLE_HEADER_FONT_SIZE = 15;     // Font size for table headers
    private static final Insets GRID_INSETS = new Insets(3, 70, 7, 70);  // Padding for grid elements
    private static final Insets HEADER_INSETS = new Insets(40, 0, 0, 0); // Padding for header
    private static final Insets NAMES_INSETS = new Insets(3, 70, 7, 0);  // Padding for name columns
    private static final Insets SCORES_INSETS = new Insets(3, 50, 7, 70); // Padding for score columns

    // Constructor to initialise high score UI and labels
    public HighScoresView(CardLayout cardLayout, JPanel main, HighScoresController highScoresController) {
        super(cardLayout, main);  // Pass layout and main panel to the parent class
        this.highScoresController = highScoresController;  // Set the high score controller
        this.nameLabels = new ArrayList<>();  // List for name labels
        this.scoreLabels = new ArrayList<>(); // List for score labels
        this.configLabels = new ArrayList<>(); // List for config labels

        // Initialise labels for top 10 players
        for (int i = 0; i < 10; i++) {
            nameLabels.add(new JLabel("----", JLabel.LEFT));
            configLabels.add(new JLabel("----", JLabel.RIGHT));
            scoreLabels.add(new JLabel("0", JLabel.RIGHT));
        }

        initialiseComponents();  // Set up UI components
    }

    // Set up and organise components in the layout
    @Override
    protected void initialiseComponents() {
        this.setLayout(new BorderLayout());
        this.add(addHeading(this), BorderLayout.NORTH);         // Add heading at the top
        this.add(addScoreTables(this), BorderLayout.CENTER);    // Add score tables in the centre
        addBackButtonPanel(cardLayout, this);                   // Add back button at the bottom
    }

    // Create the heading panel
    private JPanel addHeading(JPanel panel) {
        JPanel headingPanel = new JPanel(new GridBagLayout());
        JLabel headerLabel = new JLabel("High Scores", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, HEADER_FONT_SIZE));  // Set header style

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = HEADER_INSETS;

        headingPanel.add(headerLabel, gbc);  // Add the header to the panel
        panel.add(headingPanel, BorderLayout.NORTH);
        return headingPanel;
    }

    // Create and return the score tables panel
    private JPanel addScoreTables(JPanel panel) {
        JPanel namesPanel = createGridPanel("Player Name");
        JPanel scoresPanel = createGridPanel("Score");
        JPanel configPanel = createGridPanel("Config");

        GridBagConstraints gbcNames = createGridBagConstraints(GridBagConstraints.WEST, NAMES_INSETS);
        GridBagConstraints gbcScores = createGridBagConstraints(GridBagConstraints.EAST, SCORES_INSETS);
        GridBagConstraints gbcConfig = createGridBagConstraints(GridBagConstraints.EAST, SCORES_INSETS);

        List<Map.Entry<String, Integer>> topScores = highScoresController.getTopScores(10);  // Retrieve top 10 scores

        // Display names, scores, and config for the top 10 players
        for (int i = 0; i < 10; i++) {
            if (i >= topScores.size()) {
                namesPanel.add(nameLabels.get(i), gbcNames);
                scoresPanel.add(scoreLabels.get(i), gbcScores);
                configPanel.add(configLabels.get(i), gbcConfig);
            } else {
                Map.Entry<String, Integer> entry = topScores.get(i);
                nameLabels.get(i).setText(entry.getKey());
                scoreLabels.get(i).setText(String.valueOf(entry.getValue()));
                configLabels.get(i).setText(highScoresController.getPlayerConfig(entry.getKey()));
                namesPanel.add(nameLabels.get(i), gbcNames);
                scoresPanel.add(scoreLabels.get(i), gbcScores);
                configPanel.add(configLabels.get(i), gbcConfig);
            }
        }

        JPanel scoreTablePanel = new JPanel(new BorderLayout());
        scoreTablePanel.add(namesPanel, BorderLayout.WEST);
        scoreTablePanel.add(scoresPanel, BorderLayout.CENTER);
        scoreTablePanel.add(configPanel, BorderLayout.EAST);

        return scoreTablePanel;
    }

    // Create a panel with a header for names, scores, or config
    private JPanel createGridPanel(String headerText) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = GRID_INSETS;

        JLabel headerLabel = new JLabel(headerText, JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, TABLE_HEADER_FONT_SIZE));  // Set header style
        panel.add(headerLabel, gbc);

        return panel;
    }

    // Create GridBagConstraints for panel positioning
    private GridBagConstraints createGridBagConstraints(int anchor, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = anchor;
        gbc.insets = insets;
        return gbc;
    }

    // Add the back and reset buttons
    @Override
    public void addBackButtonPanel(CardLayout cardLayout, JPanel panel) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        JButton resetBtn = new JButton("Reset Scores");
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(e -> {
            highScoresController.resetScores();  // Reset scores when clicked
            updateNameAndScoreLabels();  // Update the displayed scores
        });

        JButton backBtn = new JButton("Back");
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.setSize(new Dimension(500, 700));
                UIUtils.centerWindow(topFrame);
            }
            cardLayout.show(parentPanel, "Main Menu");
        });

        buttonPanel.add(resetBtn);
        buttonPanel.add(backBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);  // Add button panel at the bottom
    }

    // Update the labels to display the latest high scores
    public void updateNameAndScoreLabels() {
        List<Map.Entry<String, Integer>> topScores = highScoresController.getTopScores(10);
        for (int i = 0; i < 10; i++) {
            nameLabels.get(i).setText("----");
            scoreLabels.get(i).setText("0");
            configLabels.get(i).setText("----");
        }
        for (int i = 0; i < topScores.size(); i++) {
            nameLabels.get(i).setText(topScores.get(i).getKey());
            scoreLabels.get(i).setText(String.valueOf(topScores.get(i).getValue()));
            configLabels.get(i).setText(highScoresController.getPlayerConfig(topScores.get(i).getKey()));
        }
    }
}
