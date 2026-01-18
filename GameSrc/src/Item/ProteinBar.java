/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: ProteinBar.java
Program Description:
ProteinBar class. Represents a consumable food item in the game.
Stores item name, allowed interaction radius, sound effect index
for throwing, sprite size, and the amount of health/stamina it restores.
Loads the item image from resources and scales it to the desired size.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class ProteinBar extends Food { // ProteinBar class extends Food base class

    public ProteinBar(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor
        
        name = "Protein Bar"; // Set the item name
        allowedRadiusTiles = 5; // Maximum distance (in tiles) player can use/interact with item
        throwSoundIndex = 3; // Sound effect index when item is thrown
        sizeX = 32; // Width of the item sprite in pixels
        sizeY = 32; // Height of the item sprite in pixels
        restoreValue = 1f; // Amount of stamina/health restored when consumed

        try { // Load and scale the item image
            image = gp.uTool.scaleImage(
                        javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/Chocolate Bar.png")), 
                        sizeX, 
                        sizeY
                    ); // Read image from resources and scale
        } catch (java.io.IOException e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
