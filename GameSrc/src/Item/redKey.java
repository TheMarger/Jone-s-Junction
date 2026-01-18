/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: redKey.java
Program Description:
redKey class. Represents a key item in the game used to unlock doors.
Stores the item name and sprite image. Loads the key image from
resources and scales it to the appropriate size for display.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class redKey extends Key { // redKey class extends Key base class

    public redKey(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call superclass constructor
        
        name = "Red Key"; // Set the key name
        
        try { // Load and scale the key image
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/redKey.png")); // Load image from resources
            uTool.scaleImage(image, sizeX, sizeY); // Scale image to key size
        } catch (Exception e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
