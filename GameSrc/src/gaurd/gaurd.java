package gaurd;

import entity.entity;
import main.gamePanel;

public class gaurd extends entity {

	public gaurd(gamePanel gp) {
		super(gp);

		name = "Regular Gaurd";
		walkSpeed = 2;
		sprintSpeed = 4;
		direction = "down";
		
		solidArea.x = 14;
		solidArea.y = 16;
		solidArea.width = 32;
		solidArea.height = 32;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;	
		isMoving = true;
		getImage();
	}
	
	public void getImage() {
		
		up1 = setup("/gaurds/Gaurd-1");
		up2 = setup("/gaurds/Gaurd-2");
		down1 = setup("/gaurds/Gaurd-1");
		down2 = setup("/gaurds/Gaurd-2");
		left1 = setup("/gaurds/Gaurd-1");
		left2 = setup("/gaurds/Gaurd-2");
		right1 = setup("/gaurds/Gaurd-1");
		right2 = setup("/gaurds/Gaurd-2");

	}
	
	public void setAction() {
		
		actionCounter++;
		
		// generate a random number from 1 to 100
		int randomNum = (int) (Math.random()*100+1);
		
		if (actionCounter == 120) {
			if (randomNum <= 25) {
			    direction = "down";
			}
			else if (randomNum <= 50 && randomNum > 25) {
			    direction = "up";
			}
			else if (randomNum <= 75 && randomNum > 50) {
			    direction = "right";
			}
			else if (randomNum <= 100 && randomNum > 75) {
			    direction = "left";
			}
			actionCounter = 0;
		}
		
	}
}
