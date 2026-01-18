/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Key.java
Program Description:
Key class. Represents a generic key item in the game.
Stores the item name, sprite size, and loads the key image from
resources. Serves as a base class for specific key types like
blueKey or greenKey.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class Key extends Item { // Key class extends base Item class

    gamePanel gp; // Reference to the main game panel

    public Key(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor
        
        name = "Key"; // Set generic key name
        sizeX = 24; // Width of the key sprite in pixels
        sizeY = 24; // Height of the key sprite in pixels

        try { // Load and scale the key image
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/key.png")); // Load image from resources
            uTool.scaleImage(image, sizeX, sizeY); // Scale image to desired size
        } catch (Exception e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
