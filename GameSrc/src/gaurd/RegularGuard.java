/*
Names: Sukhmanpreet Gill
Course: ICS4U
Assignment: Jone's Junction
Due date: January 18, 2026
Program Description: This program is a prison escape game.
Throughout 4 levels, users must complete tasks to progress to the next level.
Within our game there are three types of guards, each with varying FOV mechanics. As you progress into higher levels, more types of guards appear.
The game has food items, which can be used to regain stamina lost during sprinting. These items can also be thrown to create a distraction for guards.
Similarly, the game has throwable items, which can not be used to regain stamina but can be used to create greater guard distractions.
The game has the features of saving game files, loading game files, character customization, and exiting.
If the user collides with a guard, they are told they lost and they must restart the level.
The player has the option to speedrun the game, where they must beat each level in an allocated time frame. If they do not, they must restart the level.
File & Class description:
This file and class are used to house the logic of the Regular Guard's FOV (Field of View). It contains FOV calculations and states when this logic applies.
Putting this logic in its own class is more modular.
*/

package gaurd; // Places this class inside the 'gaurd' package so it stays grouped with other guard-related classes

import main.gamePanel; // Imports the gamePanel class so this guard can access world information like tile size
import java.awt.Rectangle; // Imports Rectangle so the guard can define its field of view area

public class RegularGuard extends gaurd { // Declares the RegularGuard class, which extends the base guard class

    public RegularGuard(gamePanel gp) { // Constructor that takes the gamePanel so the guard can access game data
        super(gp); // Calls the parent guard constructor to initialize shared guard properties
        name = "Regular Guard"; // Sets this guard's display name to identify its type
    }

    @Override
    public Rectangle getFOV() { // Overrides the base guard FOV method to define a regular-sized field of view
        int ts = gp.tileSize; // Stores the tile size locally for easier calculations
        Rectangle fov = new Rectangle(); // Creates a new Rectangle that will represent the guard's field of view

        switch (direction) { // Checks which direction the guard is currently facing
            case "up": // If the guard is facing upward
                fov.setBounds(worldX - ts, worldY - 3 * ts, 3 * ts, 3 * ts); // Creates a square FOV above the guard
                break; // Ends this case

            case "down": // If the guard is facing downward
                fov.setBounds(worldX - ts, worldY + ts, 3 * ts, 3 * ts); // Creates a square FOV below the guard
                break; // Ends this case

            case "left": // If the guard is facing left
                fov.setBounds(worldX - 3 * ts, worldY - ts, 3 * ts, 3 * ts); // Creates a square FOV to the left of the guard
                break; // Ends this case

            case "right": // If the guard is facing right
                fov.setBounds(worldX + ts, worldY - ts, 3 * ts, 3 * ts); // Creates a square FOV to the right of the guard
                break; // Ends this case
        }

        return fov; // Returns the completed FOV rectangle so the game can check for player detection
    }
}