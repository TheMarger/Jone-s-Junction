/*
Name: Rafay, Christina
Course: ICS4U0
Date: 1/19/2026
Assignment Title: Jone's Junction
File: BillyGoat.java
Program Description:
BillyGoat class. Handles NPC behavior for the Billy Goat character.
Stores NPC name, movement state, speed, direction, dialogues,
and loads the NPC sprite image. Allows interaction with the player
through dialogues.
*/

package entity; // Package declaration indicating this class belongs to the entity package

import main.gamePanel; // Imports the gamePanel class from the main package

public class BillyGoat extends entity { // Declares the BillyGoat class as a subclass of entity
    public BillyGoat(gamePanel gp) { // Constructor for BillyGoat, takes a gamePanel instance
        super(gp); // Calls the superclass constructor to initialize entity properties
        name = "Billy Goat"; // Sets the name of this NPC
        this.isMoving = false; // Sets the initial movement state to false
        this.speed = walkSpeed; // Sets the NPC's speed to the walking speed defined in the entity class
        this.direction = "down"; // Sets the initial facing direction to down

        getImage(); // Calls the method to load the NPC's image
        setDialogues(); // Calls the method to initialize the NPC's dialogues
    }

    public void getImage() { // Method to load the NPC sprite image
        down1 = setup("/npcs/BillyGoat"); // Loads the down-facing image for the NPC from the resource path
    }
    
    public void setDialogues() { // Method to set all dialogues for this NPC
        dialogues[0] = "Heh… you think you’re getting out of here?\n Everyone says that on their first day."; // Dialogue 1
        dialogues[1] = "There are about four keys scattered around this place: red, yellow, green, and blue.\n Truth is, the blue key is the only one that matters.\n It open the door outside that leads to a van."; // Dialogue 2
        dialogues[2] = "And even if you manage to get all the keys, you still have to finish 2 stupid tasks first."; // Dialogue 3
        dialogues[3] = "With guards patrolling every hallway.\n One wrong move and you’re done!"; // Dialogue 4
        dialogues[4] = "But go ahead.\n I’ll still be here when they drag you back..."; // Dialogue 5
    }
    
    public void speak() { // Method to trigger speaking interaction with the player
        super.speak(); // Calls the speak method from the entity superclass
    }
}
