/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Tray.java
Program Description:
Tray class. Represents a throwable tray item in the game.
Stores item name, allowed interaction radius, throw sound index,
sprite size, and loads the tray image from resources. Can be
thrown by the player or other entities.
*/

package Item; // Package declaration for all item-related classes

import java.io.IOException; // Import for handling IO exceptions during image loading
import javax.imageio.ImageIO; // Import for reading images from resources
import main.gamePanel; // Import game panel for context and access to utility tools

public class Tray extends Throwable { // Tray class extends Throwable base class

    public Tray(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor
        
        name = "Tray"; // Set the item name
        allowedRadiusTiles = 3; // Maximum distance (in tiles) item can be thrown
        throwSoundIndex = 7; // Sound effect index when item is thrown
        sizeX = 48; // Width of the item sprite in pixels
        sizeY = 32; // Height of the item sprite in pixels

        try { // Load and scale the item image
            image = gp.uTool.scaleImage(
                        ImageIO.read(getClass().getResourceAsStream("/items/Tray.png")), 
                        sizeX, 
                        sizeY
                    ); // Read image from resources and scale
        } catch (IOException e) { // Handle exceptions if image fails to load
            e.printStackTrace(); // Print error for debugging
        }
    }
}
