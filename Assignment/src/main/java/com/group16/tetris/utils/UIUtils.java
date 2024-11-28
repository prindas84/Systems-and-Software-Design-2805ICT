package com.group16.tetris.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.*;
import java.awt.*;

public class UIUtils {

    /*  Method to center a window on the screen
        This method takes a Window object (e.g., JFrame, JDialog) and positions it in the center of the screen. */
    public static void centerWindow(Window window) {
        // Get the screen size using the Toolkit class
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calculate the x and y coordinates to position the window at the center of the screen
        int x = (screenSize.width - window.getWidth()) / 2;
        int y = (screenSize.height - window.getHeight()) / 2;

        // Set the location of the window to the calculated coordinates
        window.setLocation(x, y);
    }

    /*  Method to remove focus from buttons in a JOptionPane
        This method removes the focus highlight (focus rectangle) from buttons within a JOptionPane, improving UI aesthetics. */
    public static void removeButtonFocus(JOptionPane optionPane) {
        // Iterate through all components of the JOptionPane
        for (Component comp : optionPane.getComponents()) {
            // Check if the component is a JPanel
            if (comp instanceof JPanel) {
                // Iterate through all components within the JPanel
                for (Component buttonComp : ((JPanel) comp).getComponents()) {
                    // Check if the component is a JButton
                    if (buttonComp instanceof JButton) {
                        JButton button = (JButton) buttonComp;
                        button.setFocusable(false);                             // Remove the ability to focus on the button
                        button.setFocusPainted(false);                          // Remove the painted focus rectangle around the button
                    }
                }
            }
        }
    }
}