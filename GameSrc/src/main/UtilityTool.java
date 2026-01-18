/*
 * Name: Rafay
 * Course Code: ICS4U0
 * Date: 1/19/2026
 * Description: UtilityTool class that provides helper methods for the game.
 *              Includes image scaling, debugging tools for hitboxes and tiles,
 *              and methods to refresh map and task data.
 */

package main; // Declares the package name for this class

import java.awt.Color; // Imports Color class for setting colors
import java.awt.Graphics2D; // Imports Graphics2D for 2D drawing operations
import java.awt.image.BufferedImage; // Imports BufferedImage for image manipulation
import entity.entity; // Imports the entity class from the entity package
import main.*; // Imports all classes from the main package

public class UtilityTool { // Defines the UtilityTool class for helper functions
	
	gamePanel gp; // Reference to the main game panel
	
	public UtilityTool(gamePanel gp) { // Constructor that receives game panel reference
		this.gp = gp; // Stores the game panel reference for later use
	}
	
	public BufferedImage scaleImage(BufferedImage original, int width, int height) { // Scales an image to specified dimensions
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType()); // Creates new image with target size
		Graphics2D g2d = scaledImage.createGraphics(); // Gets graphics context for drawing
		g2d.drawImage(original, 0, 0, width, height, null); // Draws original image scaled to new dimensions
		g2d.dispose(); // Releases graphics resources
		return scaledImage; // Returns the scaled image
	}
	
	public void drawEntityHitbox(Graphics2D g2, entity e) { // Draws a red rectangle showing entity's collision hitbox
	    // Converts entity's world position to screen position relative to player
	    int screenX = e.worldX - gp.player.worldX + gp.player.getScreenX(); // Calculates screen X coordinate
	    int screenY = e.worldY - gp.player.worldY + gp.player.getScreenY(); // Calculates screen Y coordinate
	    
	    // Calculates hitbox position (solidArea is offset from entity position)
	    int hitboxX = screenX + e.solidArea.x; // Adds solidArea offset to screen X
	    int hitboxY = screenY + e.solidArea.y; // Adds solidArea offset to screen Y
	    
	    g2.setColor(java.awt.Color.RED); // Sets drawing color to red for visibility
	    g2.drawRect(hitboxX, hitboxY, e.solidArea.width, e.solidArea.height); // Draws the hitbox rectangle
	}
	
	public void highlightTile(Graphics2D g2, int col, int row) { // Highlights a specific tile for debugging purposes
		// Highlights tile at specified column and row position for testing
	    int testCol = col; // Stores the column to highlight
	    int testRow = row; // Stores the row to highlight
	    int testWorldX = testCol * gp.tileSize; // Converts column to world X coordinate
	    int testWorldY = testRow * gp.tileSize; // Converts row to world Y coordinate
	    int testScreenX = testWorldX - gp.player.worldX + gp.player.getScreenX(); // Converts world X to screen X
	    int testScreenY = testWorldY - gp.player.worldY + gp.player.getScreenY(); // Converts world Y to screen Y
	    
	    g2.setColor(new Color(255, 0, 0, 100)); // Sets semi-transparent red color (alpha = 100)
	    g2.fillRect(testScreenX, testScreenY, gp.tileSize, gp.tileSize); // Fills the tile with the highlight color
	}
	
	public void showTileNumber(Graphics2D g2, int screenX, int screenY, int tileNum) { // Displays tile number on screen for debugging
		// Draws tile number on top of tile for identification
    	g2.setColor(new Color(0, 0, 0, 150)); // Sets semi-transparent black background
    	g2.fillRect(screenX, screenY, 24, 16); // Draws background rectangle for text
    	g2.setColor(Color.WHITE); // Sets text color to white for contrast
    	g2.drawString( // Draws the tile number as text
    	    String.valueOf(tileNum), // Converts tile number to string
    	    screenX + 4, // X position with 4 pixel padding
    	    screenY + 12 // Y position with 12 pixel padding
    	);
	}
	
	public void showPlayerPosition(Graphics2D g2, int worldX, int worldY, int row, int col) { // Displays player's position information on screen
		String positionText = "WorldX: " + worldX + " WorldY: " + worldY + " Row: " + row + " Col: " + col; // Formats position text
	    g2.setColor(new Color(0, 0, 0, 150)); // Sets semi-transparent black background
	    g2.fillRect(10, 10, 300, 20); // Draws background rectangle in top-left corner
	    g2.setColor(Color.WHITE); // Sets text color to white
	    g2.drawString(positionText, 15, 25); // Draws position text with slight padding
	}
	
	public void refreshMap() { // Reloads the game map from file
		gp.tileM.loadMap("/maps/Level1Map.txt"); // Loads Level 1 map data from text file
	}
	
	public void refreshTasks() { // Resets all game tasks
		gp.aSetter.setTasks(); // Calls the asset setter to reinitialize tasks
	}
}