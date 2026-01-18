/*
Name: Rafay, Jeevan
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: Food.java
Program Description:
Food class. Represents consumable food items in the game that
restore the player's health or stamina. Stores the restoration value
and inherits all functionality from Pebble base class.
*/

package Item; // Package declaration for all item-related classes

import main.gamePanel; // Import game panel for context and access to utility tools

public class Food extends Pebble { // Food class extends Pebble base class

    public float restoreValue; // Amount of health/stamina restored when consumed

    public Food(gamePanel gp) { // Constructor receives reference to game panel
        super(gp); // Call to superclass constructor
    }

    public float getRestoreValue() { // Getter method to retrieve restoration value
        return restoreValue; // Return the restoreValue of this food item
    }
}
