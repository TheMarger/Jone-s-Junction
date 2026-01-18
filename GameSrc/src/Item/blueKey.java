/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: blueKey.java
Program Description:
blueKey class. Represents a key item in the game used to unlock doors.
Stores item name and sprite image. Loads the key image from resources
and scales it to the appropriate size for display.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class blueKey extends Key { // blueKey class extends Key base class

    public blueKey(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call superclass constructor
        
        name = "Blue Key"; // Set the key name
        
        try { // Load and scale the key image
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/blueKey.png")); // Load image from resources
            uTool.scaleImage(image, sizeX, sizeY); // Scale image to key size
        } catch (Exception e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
