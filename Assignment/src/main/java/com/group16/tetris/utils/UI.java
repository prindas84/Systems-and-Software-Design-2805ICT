package com.group16.tetris.utils;

import java.awt.CardLayout;
import javax.swing.JPanel;

public interface UI {
    // Common functions within different UI interfaces
    void addBackButtonPanel(CardLayout cardLayout, JPanel main);
}
