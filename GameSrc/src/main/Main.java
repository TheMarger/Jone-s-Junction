/*
 * Name: Rafay
 * Course Code: ICS4U0
 * Date: 1/19/2026
 * Description: Main class that initializes and launches a 2D game application.
 *              Sets up the JFrame window, adds the game panel, and starts the game thread.
 */

package main; // Declares the package name for this class

import javax.swing.*; // Imports all Swing components for GUI creation
import java.awt.*; // Imports AWT classes for graphics and GUI components

public class Main { // Defines the main class of the application
	
	public static void main(String[] args) { // Entry point of the program
		
		// Sets a default handler for any uncaught exceptions in any thread
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
		    e.printStackTrace(); // Prints the stack trace of the exception to console
		});
		
		// Schedules the GUI creation on the Event Dispatch Thread for thread safety
		SwingUtilities.invokeLater(() -> {
			
		    JFrame window = new JFrame("2D Game"); // Creates a new window with title "2D Game"
		    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exits program when window is closed
		    window.setResizable(false); // Prevents the window from being resized
		    
		    gamePanel gp = new gamePanel(); // Creates a new instance of the game panel
		    window.add(gp); // Adds the game panel to the window
		    window.pack(); // Sizes the window to fit the preferred size of the game panel
		    window.setLocationRelativeTo(null); // Centers the window on the screen
		    window.setVisible(true); // Makes the window visible to the user
		    
		    gp.setupGame(); // Initializes the game state and resources
		    gp.startGameThread(); // Starts the game loop thread
		});
		
	}
}