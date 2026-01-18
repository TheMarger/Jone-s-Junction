/*
 * Name: Rafay
 * Date: 1/19/2026
 * Course Code: ICS4U0
 * Description: EventHandler class manages tile-based events in the game world. It creates
 *              event rectangles for each tile on the map and checks if the player interacts
 *              with specific tiles. This class handles event triggering based on player
 *              position and direction, and manages critical game events like player death.
 *              Event rectangles are centered within tiles and sized at half the tile size
 *              to provide precise interaction zones.
 */

package main; // Declares this class belongs to the main package
import java.awt.Graphics2D; // Imports Graphics2D class for rendering (currently unused)
import java.awt.Rectangle; // Imports Rectangle class for hitbox calculations
import entity.entity; // Imports the base entity class (currently unused)
public class EventHandler { // Declares the public EventHandler class
	
	gamePanel gp; // Reference to the main gamePanel object
	EventRect eventRect[][]; // Two-dimensional array storing event rectangles for each tile in the world
	
	public EventHandler(gamePanel gp) { // Constructor that takes a gamePanel parameter
	    this.gp = gp; // Assigns the gamePanel parameter to the class's gp field
	    eventRect = new EventRect[gp.maxWorldCol][gp.maxWorldRow]; // Initializes the event rectangle array with world dimensions
	    int col = 0; // Initializes column counter to 0
	    int row = 0; // Initializes row counter to 0
	    while (col < gp.maxWorldCol && row < gp.maxWorldRow) { // Loops until all tiles have event rectangles created
	        eventRect[col][row] = new EventRect(); // Creates a new EventRect object for this tile
	        int size = gp.tileSize / 2; // Calculates event rectangle size as half the tile size
	        int offset = (gp.tileSize - size) / 2; // Calculates offset to center the event rectangle within the tile
	        eventRect[col][row].x = offset; // Sets the X offset of the event rectangle
	        eventRect[col][row].y = offset; // Sets the Y offset of the event rectangle
	        eventRect[col][row].width = size; // Sets the width of the event rectangle
	        eventRect[col][row].height = size; // Sets the height of the event rectangle
	        col++; // Increments the column counter
	        if (col == gp.maxWorldCol) { // Checks if we've reached the end of a row
	            col = 0; // Resets column counter to 0
	            row++; // Increments row counter to move to next row
	        }
	    }
	}
	
	public void checkEvent() { // Method called each frame to check for tile-based events
	    // Tile event
	    //if (hitTile(6, 19, "any")) { // Example commented code that checks if player hit tile at column 6, row 19
	     //   gp.player.tasksList.get(1).setCompleted(true); // Example commented code that would complete task at index 1
	    //}
	}
	
	public boolean hitTile(int col, int row, String reqDirection) { // Method to check if player is touching a specific tile with optional direction requirement
	    // Player world hitbox (temporary)
	    Rectangle playerArea = new Rectangle( // Creates a Rectangle representing the player's current hitbox
	        gp.player.worldX + gp.player.solidArea.x, // X position of player's hitbox in world coordinates
	        gp.player.worldY + gp.player.solidArea.y, // Y position of player's hitbox in world coordinates
	        gp.player.solidArea.width, // Width of player's hitbox
	        gp.player.solidArea.height // Height of player's hitbox
	    );
	    // Event world hitbox (temporary)
	    Rectangle eventArea = new Rectangle( // Creates a Rectangle representing the event area for the specified tile
	        col * gp.tileSize + eventRect[col][row].x, // X position of event rectangle in world coordinates
	        row * gp.tileSize + eventRect[col][row].y, // Y position of event rectangle in world coordinates
	        eventRect[col][row].width, // Width of event rectangle
	        eventRect[col][row].height // Height of event rectangle
	    );
	    if (playerArea.intersects(eventArea)) { // Checks if player's hitbox intersects with the event area
	        return reqDirection.equals("any") || // Returns true if any direction is allowed
	               gp.player.direction.equals(reqDirection); // Or returns true if player's direction matches required direction
	    }
	    return false; // Returns false if player is not touching the event tile
	}
	public void playerDied() { // Method called when the player dies
		gp.gameState = gp.deathState; // Changes the game state to death state
		System.out.println("You Died!"); // Prints death message to console
		gp.playSoundEffect(4); // Plays death sound effect (sound index 4)
		gp.stopMusic(); // Stops the background music
	}
	
}