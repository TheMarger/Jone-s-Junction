package entity;

import main.gamePanel;

public class OldManJone extends entity {
	public OldManJone(gamePanel gp) {
		super(gp);

		this.isMoving = false;
		this.direction = "down";
		this.speed = walkSpeed;
		name = "Old Man Jone";

		getImage();
		setDialogues();
	}
	public void getImage() {
        up1 = setup("/npcs/oldman_up_1");
        up2 = setup("/npcs/oldman_up_2");
        down1 = setup("/npcs/oldman_down_1");
        down2 = setup("/npcs/oldman_down_2");
        left1 = setup("/npcs/oldman_left_1");
        left2 = setup("/npcs/oldman_left_2");
        right1 = setup("/npcs/oldman_right_1");
        right2 = setup("/npcs/oldman_right_2");
    }
	
	public void setDialogues() {
		dialogues[0] = "Hello there young adventurer!";
		dialogues[1] = "Be careful in the forest.";
		dialogues[2] = "Monsters have been seen around.";
		dialogues[3] = "Take this torch to light your way.";
	}
	
	public void speak() {
		super.speak();
	}
	

}
