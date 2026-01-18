/*
Name: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: FuseRepairTask.java
Program Description:
Fuse repair task class. Extends the Task parent class. Presents the player with a
wire-connection repair puzzle where the player must correctly connect wires to
repair a fuse. Successfully repairing the fuse completes the task and allows the
player to progress through the game.
*/

package task; // Specifies that this class belongs to the task package

import javax.imageio.ImageIO; // Used to load image files for the task icon

import main.gamePanel; // Imports the main game panel to access game settings and utilities

public class FuseRepairTask extends Task { // FuseRepairTask child class that extends the Task parent class
	
	public FuseRepairTask(gamePanel gp) { // Constructor called when a FuseRepairTask object is created
		
		super(gp); // Calls the parent Task constructor to initialize shared task variables
		
		name = "Fuse Repair Task"; // Sets the name of the task
		
		description = "Connect the wires to repair the fuse."; // Sets the task description shown to the player
		
		try { // Attempts to load and scale the task image
			
			image = gp.uTool.scaleImage( // Scales the image to match the game's tile size
				ImageIO.read( // Reads the image from the resources folder
					getClass().getResourceAsStream("/tasks/fuseRepairTask.png")
				),
				gp.tileSize, // Image width equals one tile
				gp.tileSize  // Image height equals one tile
			);
			
		} catch (Exception e) { // Handles errors if the image fails to load
			
			e.printStackTrace(); // Prints error details to help with debugging
			
		}
	}
}
