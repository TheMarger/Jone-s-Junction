/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Item.java
Program Description:
Item class. Base class for all items in the game. Handles item
position, sprite image, collision detection, pickup delay, and
drawing/updating logic. Provides basic functionality for all
subclasses like Food, Keys, or Throwable items.
*/

package Item; // Package declaration for all item-related classes

import java.awt.Graphics2D; // Import for drawing 2D graphics
import java.awt.Rectangle;  // Import for defining collision areas
import java.awt.image.BufferedImage; // Import for handling sprite images

import main.UtilityTool; // Import utility tool for scaling and other functions
import main.gamePanel;   // Import game panel for context and access to game systems

public class Item { // Base Item class

    gamePanel gp; // Reference to the main game panel

    public BufferedImage image; // Sprite image of the item
    public String name; // Name of the item
    public UtilityTool uTool = new UtilityTool(gp); // Utility tool instance
    public boolean collision = false; // Whether the item collides with entities
    public int worldX, worldY; // Item's position in the world
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // Collision area
    public int solidAreaDefaultX = 0; // Default X position of collision box
    public int solidAreaDefaultY = 0; // Default Y position of collision box
    public int sizeX = 48; // Width of the item sprite in pixels
    public int sizeY = 48; // Height of the item sprite in pixels
    public int pickupDelay = 0; // Delay before item can be picked up

    public Item(gamePanel gp) { // Constructor receives reference to game panel
        this.gp = gp; // Store reference to game panel
    }

    public void draw(Graphics2D g2) { // Draw the item on the screen
        int screenX = worldX - gp.player.worldX + gp.player.getScreenX(); // Calculate screen X
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY(); // Calculate screen Y

        // Only draw if the item is within the visible screen bounds
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {

            g2.drawImage(image, screenX, screenY, sizeX, sizeY, null); // Draw the item
        }
    }

    public void update() { // Update item per frame
        if (pickupDelay > 0) { // Countdown pickup delay
            pickupDelay--; // Decrease delay each frame
        }
    }

    public String getName() { // Getter for item name
        return name; // Return the item's name
    }
}
