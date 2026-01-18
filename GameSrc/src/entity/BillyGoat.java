package entity;

import main.gamePanel;

public class BillyGoat extends entity {
	public BillyGoat(gamePanel gp) {
		super(gp);
		name = "Billy Goat";
		this.isMoving = false;
		this.speed = walkSpeed;
		this.direction = "down";

		getImage();
		setDialogues();
	}
	public void getImage() {
        down1 = setup("/npcs/BillyGoat");
    }
	
	public void setDialogues() {
		dialogues[0] = "Heh… you think you’re getting out of here?\n Everyone says that on their first day.";
		dialogues[1] = "There are about four keys scattered around this place: red, yellow, green, and blue.\n Truth is, the blue key is the only one that matters.\n It open the door outside that leads to a van.";
		dialogues[2] = "And even if you manage to get all the keys, you still have to finish 2 stupid tasks first.";
		dialogues[3] = "With guards patrolling every hallway.\n One wrong move and you’re done!";
		dialogues[4] = "But go ahead.\n I’ll still be here when they drag you back...";
	}
	
	public void speak() {
		super.speak();
	}
	
	
}
