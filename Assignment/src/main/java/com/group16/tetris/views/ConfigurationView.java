package com.group16.tetris.views;

import com.group16.tetris.utils.UIUtils;
import com.group16.tetris.controllers.ConfigurationController;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.models.ConfigurationModel.PlayerType;
import com.group16.tetris.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigurationView extends View implements UI {

    // Constants for slider limits
    private static final int SLIDER_MIN_WIDTH = 5;
    private static final int SLIDER_MAX_WIDTH = 15;
    private static final int SLIDER_MIN_HEIGHT = 15;
    private static final int SLIDER_MAX_HEIGHT = 30;
    private static final int SLIDER_MIN_LEVEL = 1;
    private static final int SLIDER_MAX_LEVEL = 10;

    private final ConfigurationController configurationController;

    private JSlider fieldWidthSlider, fieldHeightSlider, gameLevelSlider;
    private JCheckBox musicCheckbox, soundEffectsCheckbox, extendModeCheckbox;
    private JLabel fieldWidthVal, fieldHeightVal, gameLevelVal;

    private ButtonGroup player1TypeGroup, player2TypeGroup;
    private List<JRadioButton> player1 = new ArrayList<>();
    private List<JRadioButton> player2 = new ArrayList<>();

    // Constructor to set up the configuration view
    public ConfigurationView(CardLayout cardLayout, JPanel parentPanel, ConfigurationController configurationController) {
        super(cardLayout, parentPanel);
        this.configurationController = configurationController;
        this.player1TypeGroup = new ButtonGroup();
        this.player2TypeGroup = new ButtonGroup();
        player1.add(createRadioButtons(1, "Human", PlayerType.HUMAN));
        player1.add(createRadioButtons(1, "AI", PlayerType.AI));
        player1.add(createRadioButtons(1, "External", PlayerType.EXTERNAL));

        player2.add(createRadioButtons(2, "Human", PlayerType.HUMAN));
        player2.add(createRadioButtons(2, "AI", PlayerType.AI));
        player2.add(createRadioButtons(2, "External", PlayerType.EXTERNAL));

        initialiseComponents();  // Set up UI components
    }

    // Initialise and add components to the layout
    @Override
    protected void initialiseComponents() {
        this.setLayout(new BorderLayout());
        this.add(createHeadingPanel(), BorderLayout.NORTH);
        this.add(createConfigurationItemsPanel(), BorderLayout.CENTER);
        addBackButtonPanel(cardLayout, this);
    }

    // Create heading panel for the configuration window
    private JPanel createHeadingPanel() {
        JPanel heading = new JPanel(new GridBagLayout());
        JLabel header = new JLabel("Configuration", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 0, 0, 0);

        heading.add(header, gbc);

        return heading;
    }

    // Create panel for sliders, checkboxes, and radio buttons
    private JPanel createConfigurationItemsPanel() {
        JPanel configurationItems = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(7, 0, 35, 0);

        fieldWidthSlider = createSlider(SLIDER_MIN_WIDTH, SLIDER_MAX_WIDTH, configurationController.getSettings().getFieldWidth());
        fieldHeightSlider = createSlider(SLIDER_MIN_HEIGHT, SLIDER_MAX_HEIGHT, configurationController.getSettings().getFieldHeight());
        gameLevelSlider = createSlider(SLIDER_MIN_LEVEL, SLIDER_MAX_LEVEL, configurationController.getSettings().getGameLevel());

        musicCheckbox = createCheckbox(configurationController.getSettings().isMusicEnabled(), configurationController.getSettings()::setMusic);
        soundEffectsCheckbox = createCheckbox(configurationController.getSettings().isSoundEffectsEnabled(), configurationController.getSettings()::setSoundEffects);
        extendModeCheckbox = createCheckbox(configurationController.getSettings().isExtendModeEnabled(), configurationController.getSettings()::setExtendMode);

        fieldWidthVal = new JLabel(String.valueOf(configurationController.getSettings().getFieldWidth()));
        fieldHeightVal = new JLabel(String.valueOf(configurationController.getSettings().getFieldHeight()));
        gameLevelVal = new JLabel(String.valueOf(configurationController.getSettings().getGameLevel()));

        // Add sliders and checkboxes to the layout
        addSliderWithLabel(configurationItems, gbc, "Field Width:", fieldWidthSlider, fieldWidthVal, configurationController.getSettings()::setFieldWidth);
        addSliderWithLabel(configurationItems, gbc, "Field Height:", fieldHeightSlider, fieldHeightVal, configurationController.getSettings()::setFieldHeight);
        addSliderWithLabel(configurationItems, gbc, "Game Level:", gameLevelSlider, gameLevelVal, configurationController.getSettings()::setGameLevel);

        addCheckboxWithLabel(configurationItems, gbc, "Music (On | Off):", musicCheckbox);
        addCheckboxWithLabel(configurationItems, gbc, "Sound Effects (On | Off):", soundEffectsCheckbox);
        addCheckboxWithLabel(configurationItems, gbc, "Extend Mode (On | Off):", extendModeCheckbox);

        addRadioButtonsWithLabel(configurationItems, gbc, "Player One Type:", player1);
        addRadioButtonsWithLabel(configurationItems, gbc, "Player Two Type:", player2);

        return configurationItems;
    }

    // Add a slider with label and change listener
    private void addSliderWithLabel(JPanel panel, GridBagConstraints gbc, String label, JSlider slider, JLabel valueLabel, Consumer<Integer> valueSetter) {
        JLabel sliderLabel = new JLabel(label);
        gbc.gridx = 0;
        panel.add(sliderLabel, gbc);
        gbc.gridx++;

        gbc.gridx++;
        panel.add(slider, gbc);
        gbc.gridx++;

        slider.addChangeListener(e -> {
            int value = slider.getValue();
            valueSetter.accept(value);
            valueLabel.setText(String.valueOf(value));
        });

        gbc.gridx = 0;
        gbc.gridy++;
    }

    // Add a checkbox with label
    private void addCheckboxWithLabel(JPanel panel, GridBagConstraints gbc, String label, JCheckBox checkbox) {
        JLabel checkboxLabel = new JLabel(label);
        gbc.gridx = 0;
        panel.add(checkboxLabel, gbc);
        gbc.gridx++;

        panel.add(checkbox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
    }

    // Create a slider with specific settings
    private JSlider createSlider(int min, int max, int initialValue) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, initialValue);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(false);
        slider.setPaintLabels(true);
        slider.setPreferredSize(new Dimension(290, 50));
        slider.setMinimumSize(new Dimension(290, 50));
        return slider;
    }

    // Create a checkbox and set its initial state
    private JCheckBox createCheckbox(boolean initialState, Consumer<Boolean> valueSetter) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected(initialState);
        checkbox.addActionListener(e -> valueSetter.accept(checkbox.isSelected()));
        return checkbox;
    }

    // Add radio buttons with label
    private void addRadioButtonsWithLabel(JPanel panel, GridBagConstraints gbc, String label, List<JRadioButton> jRadioButtonList) {
        JLabel checkboxLabel = new JLabel(label);
        gbc.gridx = 0;
        panel.add(checkboxLabel, gbc);
        gbc.gridx++;

        for (int i = 0; i < jRadioButtonList.size(); i++) {
            panel.add(jRadioButtonList.get(i), gbc);
            gbc.gridx++;
        }

        gbc.gridy++;
        gbc.gridx = 0;
    }

    // Create radio buttons for player types
    private JRadioButton createRadioButtons(int playerGroup, String name, PlayerType type) {
        JRadioButton radioButton = new JRadioButton(name);
        if (playerGroup == 1) {
            player1TypeGroup.add(radioButton);
            radioButton.addActionListener(e -> configurationController.getSettings().setPlayer1Type(type));
        } else {
            player2TypeGroup.add(radioButton);
            radioButton.addActionListener(e -> configurationController.getSettings().setPlayer2Type(type));
        }
        radioButton.setMinimumSize(new Dimension(150, 15));
        if (type == PlayerType.HUMAN) {
            radioButton.setSelected(true);
        }
        return radioButton;
    }

    // Add back and reset buttons
    @Override
    public void addBackButtonPanel(CardLayout cardLayout, JPanel panel) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        JButton resetBtn = new JButton("Reset to Default");
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(e -> {
            configurationController.getSettings().resetToDefaults();
            updateUIComponents();
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

        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Return the current settings
    public ConfigurationModel getSettings() {
        return configurationController.getSettings();
    }

    // Update UI components to match current settings
    public void updateUIComponents() {
        fieldWidthSlider.setValue(configurationController.getSettings().getFieldWidth());
        fieldHeightSlider.setValue(configurationController.getSettings().getFieldHeight());
        gameLevelSlider.setValue(configurationController.getSettings().getGameLevel());

        musicCheckbox.setSelected(configurationController.getSettings().isMusicEnabled());
        soundEffectsCheckbox.setSelected(configurationController.getSettings().isSoundEffectsEnabled());
        extendModeCheckbox.setSelected(configurationController.getSettings().isExtendModeEnabled());

        player1.get(configurationController.getSettings().getPlayer1Type().getValue() - 1).setSelected(true);
        player2.get(configurationController.getSettings().getPlayer2Type().getValue() - 1).setSelected(true);

        fieldWidthVal.setText(String.valueOf(configurationController.getSettings().getFieldWidth()));
        fieldHeightVal.setText(String.valueOf(configurationController.getSettings().getFieldHeight()));
        gameLevelVal.setText(String.valueOf(configurationController.getSettings().getGameLevel()));
    }
}
