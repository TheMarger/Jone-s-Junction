package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.UtilityTool;
import main.gamePanel;

public class entity {
	
	public gamePanel gp;
	
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
	
	
	public int spawnX; // The entity's original world X position (in pixels or tiles depending on usage) if u dont have this add it 

	public int spawnY; // The entity's original world Y position, used for resets/teleports if you dont have this add it 
	
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
	
	public void setAction() { // Placeholder method for subclasses to override with their own behavior logic
	    // Intentionally empty; base entities have no default action
	}
	public void hearSound(int x, int y) { // Called when a sound event occurs; subclasses override to react to it
	    // Intentionally empty in the base Entity class
	}
	public boolean hasLineOfSound(int sx, int sy) { // Checks whether this entity has a clear path to the sound source
	    return false; // Base entities do not process sound; guards override this with real logic
	}
	
	public void speak() {
		if (dialogues[getDialogueIndex()] == null) {
			setDialogueIndex(0);
		}
		gp.ui.currentDialogue = dialogues[getDialogueIndex()];
		setDialogueIndex(getDialogueIndex() + 1);
	}
	
	
	public void update() { // Main per-frame update for this entity (NPC/guard/etc.)
	    setAction(); // Let the subclass decide its behavior for this frame (movement, AI, etc.)
	    gp.cChecker.checkItem(this, false); // Check for item collisions (false = do not pick up, just detect)
	    
	    if (isMoving == false) { // If this entity is not currently moving
	        return; // Skip movement logic entirely
	    } else { // Otherwise, movement is active
	        int dx = 0; // Horizontal movement delta (-1, 0, 1)
	        int dy = 0; // Vertical movement delta (-1, 0, 1)
	        
	        switch (direction) { // Determine movement direction based on current facing
	            case "up": // Entity is facing upward
	                dy = -1; // Move upward by -1 pixel per step
	                break;
	            case "down": // Entity is facing downward
	                dy = 1; // Move downward by +1 pixel per step
	                break;
	            case "left": // Entity is facing left
	                dx = -1; // Move left by -1 pixel per step
	                break;
	            case "right": // Entity is facing right
	                dx = 1; // Move right by +1 pixel per step
	                break;
	            default: // Unknown or invalid direction
	                dx = 0; // No horizontal movement
	                dy = 0; // No vertical movement
	                break;
	        }
	        
	        // ---------- MOVEMENT ----------
	        if (dx != 0 || dy != 0) { // Only process movement if there is a non-zero direction
	            for (int step = 0; step < speed; step++) { // Move one pixel at a time for accurate collision detection
	                
	                // try X (horizontal movement)
	                if (dx != 0 && collisionOn) { // If attempting horizontal movement AND collision detection is enabled
	                    boolean hitPlayerX = gp.cChecker.checkPlayer(this, dx, 0); // Check if moving horizontally would hit the player
	                    int collidedTileX = gp.cChecker.getCollidingTile(this, dx, 0); // Check if moving horizontally would hit a solid tile
	                    int collidedNpcX = gp.cChecker.checkEntity(this, gp.npc, dx, 0); // Check if moving horizontally would hit an NPC
	                    int collidedGuardX = gp.cChecker.checkEntity(this, gp.gaurds, dx, 0); // Check if moving horizontally would hit another guard
	                    
	                    if (hitPlayerX) { // If entity would touch the player horizontally
	                        System.out.println("Player killed by guard"); // Debug message to console
	                        gp.eHandler.playerDied(); // Trigger player death event/sequence
	                    } else if (collidedTileX == -1 && collidedNpcX == 999 && collidedGuardX == 999) { // If no collisions detected (-1 and 999 indicate "no collision")
	                        worldX += dx; // Apply horizontal movement (move entity along X axis)
	                    }
	                    // If any collision occurred (other than player), do nothing (entity stays in place)
	                } else if (!collisionOn) { // If collision detection is disabled
	                    worldX += dx; // Apply horizontal movement unconditionally
	                }
	                
	                // try Y (vertical movement)
	                if (dy != 0 && collisionOn) { // If attempting vertical movement AND collision detection is enabled
	                    boolean hitPlayerY = gp.cChecker.checkPlayer(this, 0, dy); // Check if moving vertically would hit the player
	                    int collidedTileY = gp.cChecker.getCollidingTile(this, 0, dy); // Check if moving vertically would hit a solid tile
	                    int collidedNpcY = gp.cChecker.checkEntity(this, gp.npc, 0, dy); // Check if moving vertically would hit an NPC
	                    int collidedGuardY = gp.cChecker.checkEntity(this, gp.gaurds, 0, dy); // Check if moving vertically would hit another guard
	                    
	                    if (hitPlayerY) { // If entity would touch the player vertically
	                        System.out.println("Player killed by guard"); // Debug message to console
	                        gp.eHandler.playerDied(); // Trigger player death event/sequence
	                    } else if (collidedTileY == -1 && collidedNpcY == 999 && collidedGuardY == 999) { // If no collisions detected (-1 and 999 indicate "no collision")
	                        worldY += dy; // Apply vertical movement (move entity along Y axis)
	                    }
	                    // If any collision occurred (other than player), do nothing (entity stays in place)
	                } else if (!collisionOn) { // If collision detection is disabled
	                    worldY += dy; // Apply vertical movement unconditionally
	                }
	                
	            } // End per-pixel movement loop
	            
	            // ---------- ANIMATION ----------
	            spriteCounter++; // Increment animation counter each frame that entity moves
	            if (spriteCounter > 19 - (1.5 * speed)) { // Check if enough time has passed to flip sprite (faster movement = faster animation)
	                spriteNum = (spriteNum == 1) ? 2 : 1; // Toggle between sprite frame 1 and frame 2 for walking animation
	                spriteCounter = 0; // Reset animation counter to start timing next frame flip
	            }
	        } // End movement check
	    } // End isMoving check
	} // End update method
	
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
