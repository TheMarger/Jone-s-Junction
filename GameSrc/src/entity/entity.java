/*
Name: Rafay
Course: ICS4U0
Date: 1/19/2026
File: entity.java
Program Description:
Entity base class. Handles core properties and behavior of all entities
(NPCs, guards, player, etc.) including position, movement, speed, 
stamina, sprite animation, collision detection, dialogues, and drawing.
Provides base methods for movement, sprint/walk/crouch, speaking, 
updating per frame, and sprite loading. Subclasses extend this for 
specific behaviors.
*/

package entity; // Package declaration indicating this class belongs to the entity package

import java.awt.Graphics2D; // Used to draw entities to the screen
import java.awt.Rectangle;   // Defines collision boxes
import java.awt.image.BufferedImage; // Stores entity sprites

import javax.imageio.ImageIO; // Loads images from resources

import main.UtilityTool; // Utility class for image scaling and manipulation
import main.gamePanel;  // Reference to main game panel for context and access to game systems

public class entity { // Base class for all game entities (player, NPC, guard, etc.)
    
    public gamePanel gp; // Reference to the main game panel

    public String name; // Name of the entity
    public int actionCounter = 0; // Counter for actions (movement, AI logic, etc.)
    public int worldX, worldY; // Position in the game world
    public int walkSpeed = 2; // Default walking speed
    public int sprintSpeed; // Sprinting speed
    public int crouchSpeed; // Crouching speed
    public int speed = walkSpeed; // Current speed
    public int level; // Entity level (optional, for gameplay logic)
    public float maxStamina; // Maximum stamina
    public float stamina; // Current stamina
    public float staminaRegen; // Stamina points regenerated per second
    public float sprintStaminaCost; // Stamina points lost per second when sprinting
    
    public int spawnX; // Original spawn X position for resets or teleports
    public int spawnY; // Original spawn Y position for resets or teleports
    
    public boolean isMoving; // Whether entity is currently moving
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // Sprite images for each direction
    public String direction; // Current facing direction
    
    public int spriteCounter = 0; // Counter for animation timing
    public int spriteNum = 1; // Current sprite frame number
    
    public Rectangle solidArea; // Collision box for entity
    public int solidAreaDefaultX, solidAreaDefaultY; // Default offsets for collision box
    public boolean collisionOn = true; // Whether collision detection is active
    
    String[] dialogues = new String[20]; // Stores up to 20 dialogue lines
    private int dialogueIndex = 0; // Tracks which dialogue line to show next
    
    public entity(gamePanel gp) { // Constructor
        this.gp = gp; // Store reference to game panel
        
        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize); // Initialize collision box
        solidAreaDefaultX = solidArea.x; // Store default X offset
        solidAreaDefaultY = solidArea.y; // Store default Y offset
    }
    
    public void setAction() { // Placeholder for entity-specific behavior logic
        // Intentionally empty; subclasses override this
    }
    
    public void hearSound(int x, int y) { // Placeholder for reacting to sounds
        // Base entity does not react; overridden in guards/NPCs
    }
    
    public boolean hasLineOfSound(int sx, int sy) { // Checks if entity can "hear" a sound
        return false; // Base entity ignores sound
    }
    
    public void speak() { // Triggers entity dialogue with the player
        if (dialogues[getDialogueIndex()] == null) { // If no dialogue exists
            setDialogueIndex(0); // Reset to first dialogue
        }
        gp.ui.currentDialogue = dialogues[getDialogueIndex()]; // Set dialogue in UI
        setDialogueIndex(getDialogueIndex() + 1); // Move to next dialogue for next interaction
    }
    
    public void update() { // Per-frame update method
        setAction(); // Let subclass decide AI or movement actions
        gp.cChecker.checkItem(this, false); // Check collisions with items (false = do not pick up)
        
        if (!isMoving) { // Skip movement logic if not moving
            return;
        } else { // Movement is active
            int dx = 0; // Horizontal movement delta
            int dy = 0; // Vertical movement delta
            
            switch(direction) { // Determine movement direction
                case "up": dy = -1; break; // Move upward
                case "down": dy = 1; break; // Move downward
                case "left": dx = -1; break; // Move left
                case "right": dx = 1; break; // Move right
                default: dx = 0; dy = 0; break; // No movement if invalid
            }
            
            // ---------- MOVEMENT ----------
            if (dx != 0 || dy != 0) { // Only move if there is movement
                for (int step = 0; step < speed; step++) { // Move per pixel for collision accuracy
                    
                    // Horizontal movement
                    if (dx != 0 && collisionOn) {
                        boolean hitPlayerX = gp.cChecker.checkPlayer(this, dx, 0); // Check player collision
                        int collidedTileX = gp.cChecker.getCollidingTile(this, dx, 0); // Check tile collision
                        int collidedNpcX = gp.cChecker.checkEntity(this, gp.npc, dx, 0); // Check NPC collision
                        int collidedGuardX = gp.cChecker.checkEntity(this, gp.gaurds, dx, 0); // Check guard collision
                        
                        if (hitPlayerX) { // If hits player horizontally
                            System.out.println("Player killed by guard"); // Debug message
                            gp.eHandler.playerDied(); // Trigger death
                        } else if (collidedTileX == -1 && collidedNpcX == 999 && collidedGuardX == 999) { // No collisions
                            worldX += dx; // Apply horizontal movement
                        }
                    } else if (!collisionOn) { // Collision disabled
                        worldX += dx; // Move freely
                    }
                    
                    // Vertical movement
                    if (dy != 0 && collisionOn) {
                        boolean hitPlayerY = gp.cChecker.checkPlayer(this, 0, dy); // Check player collision
                        int collidedTileY = gp.cChecker.getCollidingTile(this, 0, dy); // Check tile collision
                        int collidedNpcY = gp.cChecker.checkEntity(this, gp.npc, 0, dy); // Check NPC collision
                        int collidedGuardY = gp.cChecker.checkEntity(this, gp.gaurds, 0, dy); // Check guard collision
                        
                        if (hitPlayerY) { // If hits player vertically
                            System.out.println("Player killed by guard"); // Debug message
                            gp.eHandler.playerDied(); // Trigger death
                        } else if (collidedTileY == -1 && collidedNpcY == 999 && collidedGuardY == 999) { // No collisions
                            worldY += dy; // Apply vertical movement
                        }
                    } else if (!collisionOn) { // Collision disabled
                        worldY += dy; // Move freely
                    }
                    
                } // End per-pixel movement loop
                
                // ---------- ANIMATION ----------
                spriteCounter++; // Increment animation counter
                if (spriteCounter > 19 - (1.5 * speed)) { // Check if sprite should change
                    spriteNum = (spriteNum == 1) ? 2 : 1; // Toggle sprite
                    spriteCounter = 0; // Reset counter
                }
            }
        }
    }
    
    public BufferedImage setup(String imagePath) { // Loads and scales an image
        UtilityTool uTool = new UtilityTool(gp); // Create utility instance
        BufferedImage image = null;
        
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png")); // Load image
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize); // Scale to tile size
        } catch (Exception e) {
            e.printStackTrace(); // Print error if fails
        }
        return image; // Return loaded image
    }
    
    // ---------- MOVEMENT MODES ----------
    public void sprint() { // Sprint logic
        if (stamina > 0f) {
            speed = sprintSpeed; // Increase speed
            stamina -= sprintStaminaCost / 60f; // Subtract stamina per frame
            if (stamina <= 0f) { // If stamina depleted
                stamina = 0f; // Clamp
                speed = walkSpeed; // Revert to walking
            }
        } else {
            speed = walkSpeed; // No stamina = walking
        }
    }

    public void walk() { // Walking logic
        speed = walkSpeed;
        if (stamina < maxStamina) { // Regenerate stamina per frame
            stamina += staminaRegen / 60f;
            if (stamina > maxStamina) stamina = maxStamina; // Clamp
        }
    }

    public void crouch() { // Crouch logic
        speed = crouchSpeed;
        if (stamina < maxStamina) { // Regenerate stamina per frame
            stamina += staminaRegen / 60f;
            if (stamina > maxStamina) stamina = maxStamina; // Clamp
        }
    }

    public int getWorldX() { // Getter for world X
        return worldX;
    }

    public int getWorldY() { // Getter for world Y
        return worldY;
    }

    public void draw(Graphics2D g2) { // Draw entity on screen
        int screenX = worldX - gp.player.worldX + gp.player.getScreenX(); // Convert world to screen X
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY(); // Convert world to screen Y
        
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) { // Check if on screen
            
            BufferedImage image = null; // Placeholder for current sprite
            switch(direction) { // Select sprite based on direction and animation frame
                case "up": image = (spriteNum == 1) ? up1 : up2; break;
                case "down": image = (spriteNum == 1) ? down1 : down2; break;
                case "left": image = (spriteNum == 1) ? left1 : left2; break;
                case "right": image = (spriteNum == 1) ? right1 : right2; break;
            }
            
            g2.drawImage(image, screenX, screenY, null); // Draw sprite
        }
    }

    public int getDialogueIndex() { // Getter for current dialogue index
        return dialogueIndex;
    }

    public void setDialogueIndex(int dialogueIndex) { // Setter for dialogue index
        this.dialogueIndex = dialogueIndex;
    }
}
