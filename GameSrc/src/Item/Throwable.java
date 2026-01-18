/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Throwable.java
Program Description:
Throwable class. Base class for all throwable items in the game.
Stores maximum throw distance (in tiles), sound effect index for throwing,
pickup and throw delays, and provides getter/setter methods for these
properties. Inherits all functionality from the Item base class.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class Throwable extends Item { // Throwable class extends base Item class

    public int allowedRadiusTiles = 8; // Maximum distance (in tiles) the item can be thrown
    public int throwSoundIndex = -1; // Sound effect index to play when thrown
    public int throwDelay = 0; // Delay before the item can be thrown again

    public Throwable(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor
    }

    public void setAllowedRadiusTiles(int tiles) { // Setter for maximum throw distance
        this.allowedRadiusTiles = tiles; // Update allowed throw radius
    }

    public int getAllowedRadiusTiles() { // Getter for maximum throw distance
        return allowedRadiusTiles; // Return allowed throw radius
    }

    public void setThrowSoundIndex(int index) { // Setter for throw sound effect
        this.throwSoundIndex = index; // Update sound effect index
    }

    public int getThrowSoundIndex() { // Getter for throw sound effect
        return throwSoundIndex; // Return current throw sound index
    }

    public void update() { // Update method called each frame
        if (pickupDelay > 0) { // Countdown pickup delay
            pickupDelay--; // Reduce by 1 each frame
        }
        if (throwDelay > 0) { // Countdown throw delay
            throwDelay--; // Reduce by 1 each frame
        }
    }
}
