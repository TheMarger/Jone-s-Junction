/*
Name: Rafay
Course: ICS4U0
Assignment Title: Jone's Junction
File: Task.java
Program Description:
Task parent class used as a base for all game tasks. Stores shared task
properties such as name, description, completion state, position, collision
data, and image rendering logic. Child task classes extend this class to
implement specific task types.
*/

package task; // Specifies that this class belongs to the task package

import java.awt.Graphics2D; // Used for drawing task images to the screen
import java.awt.Rectangle; // Used to define the collision area for the task
import java.awt.image.BufferedImage; // Stores the task image

import main.gamePanel; // Imports the main game panel to access game data and player info

public class Task { // Parent class for all task types
	
	gamePanel gp; // Reference to the main game panel
	
	protected String description = "Generic Task Description"; // Task description (accessible to child classes)
	
	private boolean isCompleted; // Tracks whether the task has been completed
	
	public String name = "Generic Task"; // Task name
	
	BufferedImage image; // Image representing the task
	
	public boolean collision = false; // Determines whether the task has collision enabled
	
	public int worldX, worldY; // Task position in the game world
	
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // Collision hitbox for the task
	
	public int solidAreaDefaultX = 0; // Default X offset for the collision area
	public int solidAreaDefaultY = 0; // Default Y offset for the collision area
	
	public int sizeX = 48; // Width of the task image
	public int sizeY = 48; // Height of the task image
	
	public int pickupDelay = 0; // Delay timer to prevent instant task pickup
	
	
	public Task(gamePanel gp) { // Constructor that initializes a task object
		
		this.gp = gp; // Stores the reference to the game panel
		
		isCompleted = false; // Sets the task as incomplete by default
	}
	

	public void draw(Graphics2D g2) { // Draws the task on the screen
		
	    int screenX = worldX - gp.player.worldX + gp.player.getScreenX(); // Converts world X to screen X
	    int screenY = worldY - gp.player.worldY + gp.player.getScreenY(); // Converts world Y to screen Y
	    
	    // Checks if the task is within the player's visible screen area
	    if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
	        worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
	        worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
	        worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {
	        
	        g2.drawImage(image, screenX, screenY, sizeX, sizeY, null); // Draws the task image
	    }
	}

	public String getDescription() { // Returns the task description
		return description;
	}

	public boolean isCompleted() { // Returns whether the task is completed
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) { // Updates the task completion state
		this.isCompleted = isCompleted;
	}
	
	public String getName() { // Returns the task name
		return name;
	}
	
	@Override
	public String toString() { // Returns a formatted string representation of the task
		return (isCompleted ? "[X] " : "[ ] ") + description; // Shows completion status and description
	}

}
