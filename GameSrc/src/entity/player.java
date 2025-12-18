package entity;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.gamePanel;
import main.keyHandler;

public class player extends entity {
	
	gamePanel gp;
	keyHandler keyH;
	int speed;
	
	public player(gamePanel gp, keyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		setDefaultValues();
		getPlayerImage();
	}
	public void setDefaultValues() {
		x = 100;
		y = 100;
		walkSpeed = 2;
		speed = walkSpeed;
		direction = "down";
	}
	
	public void getPlayerImage() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_2.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_1.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_2.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void update() {
		if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
			if (keyH.upPressed == true) {
				direction = "up";
				y -= speed;
			}
			if (keyH.downPressed == true) {
				direction = "down";
				y += speed;
			}
			if (keyH.leftPressed == true) {
				direction = "left";
				x -= speed;
			}
			if (keyH.rightPressed == true) {
				direction = "right";
				x += speed;
			}
			spriteCounter++;
			if (spriteCounter > 19-(1.5*speed)) {
				if (spriteNum == 1) {
					spriteNum = 2;
				} else if (spriteNum == 2) {
					spriteNum = 1;
				}
				spriteCounter = 0;
			}
		}
	}
	public void draw(java.awt.Graphics2D g2) {
		BufferedImage image = null;
		switch(direction) {
			case "up":
				if (spriteNum == 1) {
					image = up1;
				}
				if (spriteNum == 2) {
					image = up2;
				}
				break;
			case "down":
				if (spriteNum == 1) {
					image = down1;
				}
				if (spriteNum == 2) {
					image = down2;
				}
				break;
			case "left":
				if (spriteNum == 1) {
					image = left1;
				}
				if (spriteNum == 2) {
					image = left2;
				}
				break;
			case "right":
				if (spriteNum == 1) {
					image = right1;
				}
				if (spriteNum == 2) {
					image = right2;
				}
				break;
		}
		g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
	}

}
