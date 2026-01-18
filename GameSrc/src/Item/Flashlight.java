/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Flashlight.java
Program Description:
Flashlight class. Represents a usable item in the game that can
illuminate dark areas. Stores item name, sprite size, and loads the
image from resources. Can be held or used by the player.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class Flashlight extends Item { // Flashlight class extends base Item class

    gamePanel gp; // Reference to the main game panel

    public Flashlight(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor
        
        name = "Flashlight"; // Set item name
        sizeX = 28; // Width of the sprite in pixels
        sizeY = 28; // Height of the sprite in pixels

        try { // Load and scale the item image
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/flashlight.png")); // Load image from resources
            uTool.scaleImage(image, sizeX, sizeY); // Scale image to desired size
        } catch (Exception e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
