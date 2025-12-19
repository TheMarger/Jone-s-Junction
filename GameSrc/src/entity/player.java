package entity;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.gamePanel;
import main.keyHandler;
import tile.TileManager;

public class player extends entity {
	
	gamePanel gp;
	keyHandler keyH;
	int speed;
	
	private final int screenX;
	private final int screenY;
	int hasKey = 0;
	boolean hasBlueKey = false;
	boolean hasRedKey = false;
	boolean hasGreenKey = false;
	
	public int getScreenX() {
		return screenX;
	}
	public int getScreenY() {
		return screenY;
	}
	
	public player(gamePanel gp, keyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		screenX = gp.screenWidth / 2 - (gp.tileSize /2);
		screenY = gp.screenHeight /2 - (gp.tileSize /2);
		
		solidArea = new java.awt.Rectangle();
		solidArea.x = 14;
		solidArea.y = 18;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		solidArea.width = 18;
		solidArea.height = 18;
		
		setDefaultValues();
		getPlayerImage();
	}
	public void setDefaultValues() {
		worldX = gp.tileSize * 6;
		worldY = gp.tileSize * 16;
		walkSpeed = 4;
		sprintSpeed = 8;
		crouchSpeed = 2;
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

	    int dx = 0;
	    int dy = 0;

	    // determine direction per key input; dx/dy are step signs (we'll step speed times)
	    if (keyH.upPressed) {
	        dy = -1;
	        direction = "up";
	    }
	    if (keyH.downPressed) {
	        dy = 1;
	        direction = "down";
	    }
	    if (keyH.leftPressed) {
	        dx = -1;
	        direction = "left";
	    }
	    if (keyH.rightPressed) {
	        dx = 1;
	        direction = "right";
	    }
	    if (keyH.sprintPressed) {
	        speed = sprintSpeed;
	    } else if (keyH.crouchPressed) {
	        speed = crouchSpeed;
	    } else {
	        speed = walkSpeed;
	    }

	    // only run when some key is pressed
	    if (dx != 0 || dy != 0) {

	        // reset collision flag for this frame
	        collisionOn = false;

	        // Move one pixel at a time, up to 'speed' pixels this frame.
	        // For each pixel, check X then Y (axis separated) so we get natural sliding.
	        for (int step = 0; step < speed; step++) {

	            // attempt X movement
	            if (dx != 0) {
	                if (!gp.cChecker.willCollide(this, dx, 0)) {
	                    worldX += dx;
	                } else {
	                    collisionOn = true;
	                    // do not break — still allow Y movement to create sliding
	                }
	            }

	            // attempt Y movement
	            if (dy != 0) {
	                if (!gp.cChecker.willCollide(this, 0, dy)) {
	                    worldY += dy;
	                } else {
	                    collisionOn = true;
	                }
	            }
	        }
	        
	       int itemIndex = gp.cChecker.checkItem(this, true);
	       pickUpItem(itemIndex);
	       
	     

	        // animation (only when keys pressed)
	        spriteCounter++;
	        if (spriteCounter > 19 - (1.5 * speed)) {
	            spriteNum = (spriteNum == 1) ? 2 : 1;
	            spriteCounter = 0;
	        }
	    }
	}
	
	public void pickUpItem(int index) {
		if (index != 999) {
			String itemName = gp.items[index].getName();
			if (itemName.equals("Key")) {
				gp.items[index] = null;
				TileManager.unlockTile(227);
				System.out.println("You picked up the yellow Key!");
			}
			if (itemName.equals("Red Key")) {
				gp.items[index] = null;
				TileManager.unlockTile(193);
				System.out.println("You picked up the Red Key!");
			}
			if (itemName.equals("Green Key")) {
				gp.items[index] = null;
				TileManager.unlockTile(219);
				System.out.println("You picked up the Green Key!");
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
		g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
		// DEBUG: draw player hitbox
		g2.setColor(new Color(0, 255, 0, 120)); // translucent green
		g2.fillRect(
		    screenX + solidArea.x,
		    screenY + solidArea.y,
		    solidArea.width,
		    solidArea.height
		);

		// optional outline
		g2.setColor(Color.GREEN);
		g2.drawRect(
		    screenX + solidArea.x,
		    screenY + solidArea.y,
		    solidArea.width,
		    solidArea.height
		);
	}

}
