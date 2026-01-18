/*
Name: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: RiddleTask.java
Program Description:
Riddle task class. Extends the Task parent class. Presents the player with a
riddle-based challenge that must be solved to unlock a secret. Successfully
solving the riddle completes the task and allows the player to progress through
the game.
*/

package task; // Specifies that this class belongs to the task package

import javax.imageio.ImageIO; // Used to load image files for the task icon

import main.gamePanel; // Imports the main game panel to access game settings and utilities

public class RiddleTask extends Task { // RiddleTask child class that extends the Task parent class
	
	public RiddleTask(gamePanel gp) { // Constructor called when a RiddleTask object is created
		
		super(gp); // Calls the parent Task constructor to initialize shared task variables
		
		name = "Riddle Task"; // Sets the name of the task
		
		description = "Solve the riddle to unlock the secret!"; // Sets the task description shown to the player
		
		try { // Attempts to load and scale the task image
			
			image = gp.uTool.scaleImage( // Scales the image to match the game's tile size
				ImageIO.read( // Reads the image from the resources folder
					getClass().getResourceAsStream("/tasks/singleRiddleTask.png")
				),
				gp.tileSize, // Image width equals one tile
				gp.tileSize  // Image height equals one tile
			);
			
		} catch (Exception e) { // Handles errors if the image fails to load
			
			e.printStackTrace(); // Prints error details to help with debugging
			
		}
	}
}
