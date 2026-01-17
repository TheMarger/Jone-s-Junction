package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.UtilityTool;
import main.gamePanel;

public class entity {
	
	gamePanel gp;
	
	public String name;
	public int actionCounter = 0;
	public int worldX, worldY;
	public int walkSpeed=2;
	public int sprintSpeed;
	public int crouchSpeed;
	public int speed = walkSpeed;
	public int level;
	public float maxStamina;
	public float stamina;
	public float staminaRegen;        // stamina points regenerated per second
	public float sprintStaminaCost;   // stamina points lost per second
	
	public boolean isMoving;
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
	public String direction;
	
	public int spriteCounter = 0;
	public int spriteNum = 1;
	
	public Rectangle solidArea;
	public int solidAreaDefaultX, solidAreaDefaultY;
	public boolean collisionOn = true; // does it have collisions
	
	String[] dialogues = new String[20];
	private int dialogueIndex = 0;
	
	public entity(gamePanel gp) {
		this.gp = gp;
		
		solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
	}
	
	public void setAction() {
		
	}
	
	public void speak() {
		if (dialogues[getDialogueIndex()] == null) {
			setDialogueIndex(0);
		}
		gp.ui.currentDialogue = dialogues[getDialogueIndex()];
		setDialogueIndex(getDialogueIndex() + 1);
	}
	
	
	public void update() {
		setAction();
		gp.cChecker.checkItem(this, false);
		
		if (isMoving == false) {
			return;
		} else {
			int dx=0; int dy=0;
			
			switch (direction) {
			case "up":
				dy = -1;
				break;
			case "down":
				dy = 1;
				break;
			case "left":
				dx = -1;
				break;
			case "right":
				dx = 1;
				break;
			default:
				dx = 0;
				dy = 0;
				break;
		}
		// MOVEMENT
	        if (dx != 0 || dy != 0) {
	        	
	            for (int step = 0; step < speed; step++) {
	                if (dx != 0 && collisionOn) {
	                    boolean hitPlayerX = gp.cChecker.checkPlayer(this, dx, 0);
	                    int collidedTileX = gp.cChecker.getCollidingTile(this, dx, 0);
	                    int collidedNpcX = gp.cChecker.checkEntity(this, gp.npc, dx, 0);
	                    int collidedGuardX = gp.cChecker.checkEntity(this, gp.gaurds, dx, 0);
	
	                    if (hitPlayerX) {
	                        System.out.println("Player killed by guard");
	                        gp.eHandler.playerDied();
	                    } else if (collidedTileX == -1 && collidedNpcX == 999 && collidedGuardX == 999) {
	                        worldX += dx;
	                    }
	                } else if (!collisionOn) {
	                	worldX += dx;
	                }
	
	                if (dy != 0 && collisionOn) {
	                    boolean hitPlayerY = gp.cChecker.checkPlayer(this, 0, dy);
	                    int collidedTileY = gp.cChecker.getCollidingTile(this, 0, dy);
	                    int collidedNpcY = gp.cChecker.checkEntity(this, gp.npc, 0, dy);
	                    int collidedGuardY = gp.cChecker.checkEntity(this, gp.gaurds, 0, dy);
	
	                    if (hitPlayerY) {
	                        System.out.println("Player killed by guard");
	                        gp.eHandler.playerDied();
	                    } else if (collidedTileY == -1 && collidedNpcY == 999 && collidedGuardY == 999) {
	                        worldY += dy;
	                    }
	                } else if (!collisionOn) {
	                	worldY += dy;
	                }
	
	            }
	
	            spriteCounter++;
	            if (spriteCounter > 19 - (1.5 * speed)) {
	                spriteNum = (spriteNum == 1) ? 2 : 1;
	                spriteCounter = 0;
	            }
	        }
		}	
	}
	 public BufferedImage setup(String imagePath) {
	    	UtilityTool uTool = new UtilityTool(gp);
	    	BufferedImage image = null;
	    	
	    	try {
	    		image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
	    		image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
	    	} catch (Exception e) {
				e.printStackTrace();
			}
	    	return image;
	    }
	
	// Sprint/walk/crouch implementations (frame-based, assume 60 FPS)
	 public void sprint() {
		    if (stamina > 0f) {
		        speed = sprintSpeed;
		        // subtract stamina per frame
		        stamina -= sprintStaminaCost / 60f;
		        if (stamina <= 0f) {
		            stamina = 0f;
		            speed = walkSpeed; // immediately fall back to walking
		        }
		    } else {
		        speed = walkSpeed;
		    }
		}


	 public void walk() {
	     speed = walkSpeed;
	     // regenerate per frame
	     if (stamina < maxStamina) {
	         stamina += staminaRegen / 60f;
	         if (stamina > maxStamina) stamina = maxStamina;
	     }
	 }

	 public void crouch() {
	     speed = crouchSpeed;
	     if (stamina < maxStamina) {
	         stamina += staminaRegen / 60f;
	         if (stamina > maxStamina) stamina = maxStamina;
	     }
	 }

	
	public int getWorldX() {
		return worldX;
	}
	public int getWorldY() {
		return worldY;
	}
	public void draw(Graphics2D g2) {
		int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
	    int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
	    
	    if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
	        worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
	        worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
	        worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {
	        
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
	        
	        g2.drawImage(image, screenX, screenY, null);
	    }
		
	}

	public int getDialogueIndex() {
		return dialogueIndex;
	}

	public void setDialogueIndex(int dialogueIndex) {
		this.dialogueIndex = dialogueIndex;
	}
	
	
}
