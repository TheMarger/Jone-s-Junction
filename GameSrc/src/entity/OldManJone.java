/*
Name: Rafay
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: OldManJone.java
Program Description:
OldManJone class. Handles NPC behavior for Old Man Jone character.
Stores NPC name, movement state, speed, direction, dialogues, and
loads the NPC sprite images for all directions. Allows interaction
with the player through dialogue sequences.
*/

package entity; // Package declaration for entity-related classes

import main.gamePanel; // Import main game panel for context and access to game systems

public class OldManJone extends entity { // Class declaration, extends base entity
    public OldManJone(gamePanel gp) { // Constructor, receives reference to game panel
        super(gp); // Call to entity superclass constructor

        this.isMoving = false; // NPC is initially stationary
        this.direction = "down"; // Initial facing direction is down
        this.speed = walkSpeed; // Initial movement speed is walking speed
        name = "Old Man Jone"; // Set NPC name

        getImage(); // Load NPC sprite images
        setDialogues(); // Initialize dialogue lines
    }

    public void getImage() { // Load all sprite images for the NPC
        up1 = setup("/npcs/oldman_up_1"); // Upward walking sprite 1
        up2 = setup("/npcs/oldman_up_2"); // Upward walking sprite 2
        down1 = setup("/npcs/oldman_down_1"); // Downward walking sprite 1
        down2 = setup("/npcs/oldman_down_2"); // Downward walking sprite 2
        left1 = setup("/npcs/oldman_left_1"); // Left walking sprite 1
        left2 = setup("/npcs/oldman_left_2"); // Left walking sprite 2
        right1 = setup("/npcs/oldman_right_1"); // Right walking sprite 1
        right2 = setup("/npcs/oldman_right_2"); // Right walking sprite 2
    }

    public void setDialogues() {
		dialogues[0] = "Psst. Hey, you. Yes you. I know who you are. And I know what you’re planning.";
		dialogues[1] = "Yeah, I know who you are. And I know what you’re planning.";
		dialogues[2] = "Not everyone running this place agrees with what’s happening here";
		dialogues[3] = "My brother has gone too far, he calls it order I call it control.";
		dialogues[4] = "I can't open the doors for you. But I can make sure the locks aren't as tight as they should be.";
	}

    public void speak() { // Trigger dialogue when player interacts
        super.speak(); // Call base entity speak method
    }
}
