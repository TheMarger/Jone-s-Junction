/*
Name: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: TileSelectTask.java
Program Description:
Tile selection task class. Extends the Task parent class. Presents the player
with a tile-based challenge where the player must select the correct tile.
Successfully selecting the correct tile completes the task and allows the
player to progress through the game levels.
*/

package task; // Specifies that this class belongs to the task package

import javax.imageio.ImageIO; // Used to load image files for the task icon

import main.gamePanel; // Imports the main game panel to access game settings and utilities

public class TileSelectTask extends Task { // TileSelectTask child class that extends the Task parent class
	
	public TileSelectTask(gamePanel gp) { // Constructor called when a TileSelectTask object is created
		
		super(gp); // Calls the parent Task constructor to initialize shared task variables
		
		name = "Tile Select Task"; // Sets the name of the task
		
		description = "Select the correct tile to complete the task!"; // Sets the description shown to the player
		
		try { // Attempts to load and scale the task image
			
			image = gp.uTool.scaleImage( // Scales the image to match the game's tile size
				ImageIO.read( // Reads the image from the resources folder
					getClass().getResourceAsStream("/tasks/tileSelectTask.png")
				),
				gp.tileSize, // Image width equals one tile
				gp.tileSize  // Image height equals one tile
			);
			
		} catch (Exception e) { // Handles errors if the image fails to load
			
			e.printStackTrace(); // Prints error details to help with debugging
			
		}
	}
}
