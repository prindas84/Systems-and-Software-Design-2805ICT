package com.group16.tetris.views;

import javax.swing.*;
import java.awt.*;

// Abstract View class, designed to be extended by various view types
public abstract class View extends JPanel {

    protected CardLayout cardLayout;                                        // Layout manager used for toggling between panels
    protected JPanel parentPanel;                                           // The main container that holds all the views

    // Constructor for the View class
    public View(CardLayout cardLayout, JPanel parentPanel) {
        super();                                                            // Call the superclass constructor
        this.cardLayout = cardLayout;                                       // Initialise the CardLayout
        this.parentPanel = parentPanel;                                     // Set the parent panel as the main container
        parentPanel.add(this);                                              // Add this view's panel to the parentPanel
    }

    // Abstract method to initialise UI components, to be implemented by subclasses
    protected abstract void initialiseComponents();

    // Method to switch views based on the view name provided
    public void showView(String viewName) {
        cardLayout.show(parentPanel, viewName); // Change the displayed view using CardLayout
    }

    // Method to retrieve the main panel, allowing interaction by subclasses if necessary
    public JPanel getPanel() {
        return this;
    }
}
