package com.group16.tetris.views;

// Importing necessary classes for UI components and event handling
import com.group16.tetris.utils.UIUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

// SplashScreen class extends JWindow to create a splash screen for the Tetris game
public class SplashScreen extends JWindow {

    private final int duration;                                                     // Duration for which the splash screen is displayed (in milliseconds)

    private static final String IMAGE_PATH = "/images/tetris_splash.jpg";           // Path to the splash image resource

    // Constants for the progress bar increment and maximum value
    private static final int PROGRESS_INCREMENT = 10;                               // Increment loading bar by this percentage each time
    private static final int PROGRESS_MAX = 100;                                    // Set the maximum progress to 100

    // Constructor that accepts the duration as a parameter
    public SplashScreen(int duration) {
        this.duration = duration;                                                   // Set the duration for the splash screen
    }

    // Method to show the splash screen
    public void showSplashScreen() {
        JPanel splashPanel = (JPanel) getContentPane();                             // Get the content pane of the JWindow and set up the splash panel
        splashPanel.setBackground(Color.white);                                     // Set the background color to white
        splashPanel.setLayout(new BorderLayout(0, 0));                   // Use BorderLayout for component positioning

        // Load the splash image from the resource path
        URL imageUrl = getClass().getResource(IMAGE_PATH);                          // Attempt to load the image from resources
        JLabel imageLabel;

        // If the image file can be found...
        if (imageUrl != null) {
            ImageIcon splashImage = new ImageIcon(imageUrl);                        // Create an ImageIcon from the loaded image
            imageLabel = new JLabel(splashImage);                                   // Create a JLabel to hold the image
            splashPanel.add(imageLabel, BorderLayout.CENTER);                       // Add the image label to the center of the splash panel
        // If the image file cannot be found, proceed to load a blank background.
        } else {
            splashPanel.setBackground(Color.GRAY);                                  // Set the background to grey if image is not found
            imageLabel = new JLabel("TETRIS");                                 // Create a JLabel with the text "TETRIS"
            imageLabel.setHorizontalAlignment(JLabel.CENTER);                       // Center the text horizontally
            imageLabel.setVerticalAlignment(JLabel.CENTER);                         // Center the text vertically
            imageLabel.setFont(imageLabel.getFont().deriveFont(48.0f));        // Set the font size
            splashPanel.add(imageLabel, BorderLayout.CENTER);                       // Add the label to the center of the splash panel
        }

        // Create a bottom panel to hold the progress bar and collaborator information
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Create and configure the progress bar
        JProgressBar progressBar = new JProgressBar(0, PROGRESS_MAX);               // Initialise the progress bar with a range from 0 to 100
        progressBar.setStringPainted(true);                                         // Display the percentage text on the progress bar
        bottomPanel.add(progressBar, BorderLayout.NORTH);                           // Add the progress bar to the top of the bottom panel

        // Create a label to display collaborator information
        JLabel collaboratorsLabel = new JLabel("Tetris Game (2805ICT - Group 16): Brandon Hannan, Matthew Prendergast, Mikhail Titov \u00A9 2024");
        bottomPanel.add(collaboratorsLabel, BorderLayout.SOUTH);                    // Add the label to the bottom of the bottom panel

        splashPanel.add(bottomPanel, BorderLayout.SOUTH);                           // Add the bottom panel to the bottom of the splash panel

        // Calculate the total height of the window based on the image height and bottom panel height
        int totalHeight = (imageUrl != null) ? new ImageIcon(imageUrl).getIconHeight() + bottomPanel.getPreferredSize().height : 400; // Default height if no image is loaded

        // Set the window size to the width of the image or a default width, and the calculated total height
        int width = (imageUrl != null) ? new ImageIcon(imageUrl).getIconWidth() : 600;  // Default width if no image is loaded
        setSize(new Dimension(width, totalHeight));

        // Center the window on the screen using the utility method
        UIUtils.centerWindow(this);

        // Ensure the splash screen window is always on top and visible
        setAlwaysOnTop(true);
        requestFocus();                                                             // Request focus for the window
        toFront();                                                                  // Bring the window to the front
        setAlwaysOnTop(false);                                                      // Revert the window's always-on-top status to allow normal window stacking behavior
        setVisible(true);                                                           // Make the splash screen visible

        // Calculate the number of steps and interval for the progress bar based on the duration
        int steps = PROGRESS_MAX / PROGRESS_INCREMENT;
        int interval = duration / steps;

        // Use a Timer to simulate the loading process by incrementing the progress bar
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int progress = 0;                                               // Initialise the progress value

            @Override
            public void run() {
                progressBar.setValue(progress);                                     // Update the progress bar with the current progress value
                progress += PROGRESS_INCREMENT;                                     // Increment the progress value

                // If the progress exceeds the maximum value, stop the timer and close the splash screen
                if (progress > PROGRESS_MAX) {
                    timer.cancel();                                                 // Stop the timer
                    setVisible(false);                                              // Hide the splash screen
                    dispose();                                                      // Dispose of the splash screen window
                }
            }
        }, 0, interval);                                                      // Schedule the task with an initial delay of 0 and the calculated interval

        try {
            // Pause the main thread for the duration of the splash screen
            Thread.sleep(duration);                                                 // Although this blocks the main thread, it is acceptable and intended functionality in the context of a splash screen
        } catch (InterruptedException e) {
            e.printStackTrace();                                                    // Print the stack trace if the thread is interrupted
        }
    }
}
