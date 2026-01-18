/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Can.java
Program Description:
Can class. Represents a throwable item in the game.
Stores item name, allowed interaction radius, sound effect index
for throwing, sprite size, and loads the item image from resources.
The item can be thrown by the player or other entities in the game.
*/

package Item; // Package declaration for all item-related classes

import java.io.IOException; // Import for handling IO exceptions during image loading
import javax.imageio.ImageIO; // Import for reading images from resources
import main.gamePanel; // Import game panel for context and access to utility tools

public class Can extends Throwable { // Can class extends Throwable base class

    public Can(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor

        name = "Can"; // Set item name
        allowedRadiusTiles = 10; // Maximum distance (in tiles) player can throw/interact with item
        throwSoundIndex = 5; // Sound effect index when item is thrown
        sizeX = 28; // Width of the item sprite in pixels
        sizeY = 28; // Height of the item sprite in pixels

        try { // Load and scale the item image
            image = gp.uTool.scaleImage(
                        ImageIO.read(getClass().getResourceAsStream("/items/Soda Can.png")), 
                        sizeX, 
                        sizeY
                    ); // Read image from resources and scale
        } catch (IOException e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
