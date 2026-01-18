/*
Name: rafay
Course: ICS4U0
Assignment: Jones Junction
File Name: player.java
Date: 1/19/2026
Description:
This class controls the main player character.
It handles movement, stamina (walk/sprint/crouch), collision checks,
inventory (pick up/drop/use items), interacting with NPCs and tasks,
and drawing the player to the screen.
*/

package entity; // Package declaration: this class lives in the 'entity' package

import java.awt.Color; // Import Color class (unused here but kept for drawing/possible UI use)
import java.awt.Rectangle; // Import Rectangle for hitboxes and collision areas
import java.awt.image.BufferedImage; // Import BufferedImage for sprite images
import java.io.IOException; // Import IOException for potential image load exceptions
import java.io.InputStream; // Import InputStream (not used directly here but commonly used for resources)
import java.util.ArrayList; // Import ArrayList for inventory and task lists

import javax.imageio.ImageIO; // Import ImageIO for loading images

import Item.*; // Import all classes from Item package (Item, Food, Key, etc.)
import Item.Throwable; // Import Throwable specifically to avoid ambiguity with java.lang.Throwable
import main.UtilityTool; // Import UtilityTool for image scaling and helper functions
import main.gamePanel; // Import gamePanel to access global game state and systems
import main.keyHandler; // Import keyHandler to read keyboard input
import task.*; // Import task-related classes
import tile.TileManager; // Import TileManager for unlocking/locking tiles

public class player extends entity { // Class declaration: player extends base class entity

    keyHandler keyH; // Reads keyboard input (reference to the game's key handler)

    // Where the player is drawn on the screen (center of the screen)
    private final int screenX; // X coordinate on screen where player is rendered
    private final int screenY; // Y coordinate on screen where player is rendered
    
    // Player position in the world grid
    public int row; // Player's current row in the tile grid
    public int col; // Player's current column in the tile grid
    
    // Inventory. Stores items the player is carrying
    public ArrayList<Item> inventory = new ArrayList<>(); // Inventory list initialized empty
    
    // Tasks List. Store tasks for the specific level
	public ArrayList<Task> tasksList = new ArrayList<>(); // List of tasks for the player/level
	
	// Max number of items the player can hold
    public final int INVENTORY_SIZE = 3; // Inventory capacity constant
    

    public String equppedSkin; // (typo preserved) field for equipped skin path/name
    
    // Skin/character selection storage used by the title character screen
    public boolean[] unlockedSkins; // Tracks which skins the player owns (true = unlocked)
    public int equippedSkinIndex = 0; // Index of the skin the player currently has equipped
    public int currentSkinIndex = 0; // Index used while browsing the skin selection menu
    public String equippedSkin; // Path or name of the equipped skin
    
    // Current task info when standing near a task
    public int curTaskIndex; // Index of the current nearby task (if any)
    public String curTaskName; // Name of the current nearby task (if any)
    
    // Used to decide if player can leave the level
    public boolean tasksComplete; // True when required tasks are finished
    
    private long lastFootstepTime = 0; // Timestamp (ms) of the last footstep sound played

    private long footstepInterval = 200; // Minimum interval between footsteps in milliseconds

    public int noiseValue = 0; // Loudness value of the player's current action (used to rank sounds)

    public int noiseRadiusTiles = 0; // Radius in tiles that the current noise can be heard by guards

    // Item Checks (quick booleans to check if the player has something)
    public boolean hasKey; // Generic key flag
    public boolean hasBlueKey; // Blue key flag
    public boolean hasRedKey; // Red key flag
    public boolean hasGreenKey; // Green key flag
    public boolean hasPebble; // Pebble flag
    public boolean hasCan; // Can flag
    public boolean hasTray; // Tray flag
    public boolean hasApple; // Apple flag
    public boolean hasBread; // Bread flag
    public boolean hasProteinBar; // Protein bar flag
    
    // Used to reset the animation when standing still
    int standCounter = 0; // Counter to reset sprite when idle
    
    // Flashlight system
    public static boolean hasFlashlight = false; // True when player has picked up a Flashlight/Torch item
    int lastPlayerCol = -1; // Column of the tile that was last lit by flashlight
    int lastPlayerRow = -1; // Row of the tile that was last lit by flashlight
    
    // Player life state (used for dying or game over logic)
    public boolean isAlive; // True when player is alive

    // Getters for screen position
    public int getScreenX() { // Returns screen X draw coordinate
        return screenX; // Return the final computed screenX
    }

    public int getScreenY() { // Returns screen Y draw coordinate
        return screenY; // Return the final computed screenY
    }

    // Sets up the player when the game starts
    public player(gamePanel gp, keyHandler keyH) { // Constructor takes gamePanel and keyHandler
    	super(gp); // Call the parent class (entity) constructor first
    	
        this.keyH = keyH; // Save the key handler reference for input checks

        // Center the player on the screen (camera view)
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2); // Compute center X offset for rendering
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2); // Compute center Y offset for rendering

     // Create a collision box inside the player sprite
        solidArea = new Rectangle(); // Initialize the solidArea Rectangle instance
        solidArea.x = gp.tileSize / 3; // X offset inside sprite for collision box
        solidArea.y = gp.tileSize / 3; // Y offset inside sprite for collision box
        solidAreaDefaultX = solidArea.x; // Store default X offset for resets
        solidAreaDefaultY = solidArea.y; // Store default Y offset for resets
        solidArea.width = 22; // Collision box width in pixels
        solidArea.height = 22; // Collision box height in pixels
        
        // Stamina settings
        maxStamina = 100f; // Maximum stamina value
        stamina = maxStamina; // Start with full stamina
        staminaRegen = 5f;       // Stamina regained per second
        sprintStaminaCost = 20f; // Stamina drained per second while sprinting
        
        // Load the equipped skin from gamePanel
        equippedSkinIndex = gp.equippedSkinIndex; // Sync equipped skin index from gp
        equippedSkin = gp.equippedSkin; // Sync equipped skin path/name from gp
        
        isAlive = true; // Player starts alive

     // Set starting values and load images and default content
        setDefaultValues(); // Initialize world position, speeds, etc.
        getPlayerImage(); // Load player sprites for the equipped skin
        setItems(); // Give starting items (for testing or default loadout)
        setDialogues(); // Prepare dialogue lines for level completion/etc.
    }
    
    // Gives the player starting items (for testing)
    public void setItems() { // Adds default items to inventory
    	inventory.add(new blueKey(gp)); // Add a blue key to inventory
    	updateInventory(); // Update item flags and tile unlocking based on inventory
    }

    // Clears all items from inventory
    public void clearInventory() { // Remove everything from inventory
    	inventory.clear(); // Clear the ArrayList of items
    			
    }
    
    // Equip a skin if it exists and is unlocked
    public void equipSkin(int index) { // Switch equipped skin if valid and unlocked
        if (index < 0 || index >= gp.skins.length) return; // Guard: invalid index
        if (!unlockedSkins[index]) return; // Guard: skin not unlocked

        // Save equipped skin into gamePanel so other screens know it
        gp.equippedSkinIndex = index; // Persist index to gp
        gp.equippedSkin = gp.skins[index][0][0]; // Save skin name/path in gp

        // Update player fields
        equippedSkinIndex = index; // Update local equipped index
        equippedSkin = gp.equippedSkin; // Update local equipped skin path/name

        getPlayerImage();  // Reload player images using the new skin paths
    }

    // Unlock a skin, after finishing a level
    public void unlockSkin(int index) { // Mark skin as unlocked
        if (index < 0 || index >= unlockedSkins.length) return; // Guard invalid index
        System.out.println("trying to unlock skin: " + gp.skins[index][0][0]); // Debug print
        unlockedSkins[index] = true; // Mark unlocked
        gp.skins[index][1][0] = "unlocked"; // Update skins array to reflect unlocked state
    }


    // Add an item if inventory has space
    public void addItem(Item item) { // Adds item to inventory if capacity allows
		if (inventory.size() < INVENTORY_SIZE) {
			inventory.add(item); // Add the item
		}
    }
    
    // Remove item by index (only if index is valid)
    public void removeItem(int index) { // Remove item at given index safely
		if (index >= 0 && index < inventory.size()) {
			inventory.remove(index); // Remove the element
		}
    }

    // Sets starting position, stats, and skin info
    public void setDefaultValues() { // Initialize player default values for new level/load
        
        if (gp.level != 4) { // If level is not 4, use tile-based spawn
        	// Starting world position (in pixels)
            worldX = gp.tileSize * 6; // X pixel spawn based on tile size
            worldY = gp.tileSize * 16; // Y pixel spawn based on tile size
        } else { // Special hardcoded spawn for level 4
        	worldX = 571; // Hardcoded X for level 4
            worldY = 1093; // Hardcoded Y for level 4
        }
        	
        // Converts to row/col on tile grid
        row = worldY / gp.tileSize; // Convert worldY to tile row
        col = worldX / gp.tileSize; // Convert worldX to tile column

        // Unlock skins
        unlockedSkins = new boolean[gp.skins.length]; // Create boolean array sized to skins
        for (int i = 0; i < gp.skins.length; i++) { // Populate unlocked flags from gp data
            unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked"); // True if gp marks it unlocked
        }

        // Instead of resetting to 0, use the skin stored in gamePanel
        equippedSkinIndex = gp.equippedSkinIndex; // Sync equipped index again
        equippedSkin = gp.equippedSkin; // Sync equipped skin path/name again

        // Speeds for different movement types
        walkSpeed = 4; // Walk speed in pixels per frame
        sprintSpeed = 8; // Sprint speed in pixels per frame
        crouchSpeed = 2; // Crouch speed in pixels per frame
        
        speed = walkSpeed; // Current speed starts as walking
        
        direction = "down";  // Starting facing direction
        level = gp.level; // Sync with current level from gp
        stamina = maxStamina; // Reset stamina to max
        
        tasksComplete = false; // Initialize tasksComplete (may be adjusted later)
        collisionOn = true; // Player collisions are enabled by default
        
        getPlayerImage(); // Load images for current skin
    }

    // Loads all player sprites based on which skin is equipped
    public void getPlayerImage() { // Load and assign directional sprites
    	// This makes sure gp and skins exist
        if (gp == null || gp.skins == null) return; // Guard if gp or skins are missing

        // Each skin list of image paths
        String[] paths = gp.skins[equippedSkinIndex][2]; // Paths array for equipped skin

        // Index 0 is assumed to be a safe idle / fallback frame
        BufferedImage fallback = loadAndScale(paths[0]); // Load fallback image

        // Up
        up1 = loadAndScaleSafe(paths, 1, fallback); // Load up1 or fallback
        up2 = loadAndScaleSafe(paths, 2, fallback); // Load up2 or fallback

        // Down
        down1 = loadAndScaleSafe(paths, 3, fallback); // Load down1 or fallback
        down2 = loadAndScaleSafe(paths, 4, fallback); // Load down2 or fallback

        // Right
        right1 = loadAndScaleSafe(paths, 5, fallback); // Load right1 or fallback
        right2 = loadAndScaleSafe(paths, 6, fallback); // Load right2 or fallback

        // Left
        left1 = loadAndScaleSafe(paths, 7, fallback); // Load left1 or fallback
        left2 = loadAndScaleSafe(paths, 8, fallback); // Load left2 or fallback
    }
    
    // Loads one image and scales it to tile size
    private BufferedImage loadAndScale(String path) { // Helper to load and scale a single image
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path)); // Read resource
            return gp.uTool.scaleImage(img, gp.tileSize, gp.tileSize); // Scale to tile size and return
        } catch (Exception e) {
            return null; // If image fails to load, return null so callers can use fallback
        }
    }

    // If index is invalid or image missing, return fallback
    private BufferedImage loadAndScaleSafe(String[] paths, int index, BufferedImage fallback) { // Safe loader with fallback
        if (index < 0 || index >= paths.length || paths[index] == null) { // Validate index and path
            return fallback; // Return fallback if invalid
        }

        BufferedImage img = loadAndScale(paths[index]); // Try to load requested image
        return (img != null) ? img : fallback; // If load failed, return fallback
    }
   
    // Handles input, movement, collisions, interactions, sounds, and guard overlap.
    public void update() { // Main per-frame update for the player
        
        int dx = 0; // Movement in x direction for this frame (pixel delta per step)
        int dy = 0; // Movement in y direction for this frame (pixel delta per step)
        
        // ---------- input ----------
        if (keyH.upPressed)    { dy = -1; direction = "up"; } // Move/up press sets dy and direction
        if (keyH.downPressed)  { dy =  1; direction = "down"; } // Move/down press sets dy and direction
        if (keyH.leftPressed)  { dx = -1; direction = "left"; } // Move/left press sets dx and direction
        if (keyH.rightPressed) { dx =  1; direction = "right"; } // Move/right press sets dx and direction

        // ---------- speed / stamina ----------
        if (keyH.sprintPressed && stamina > 0) { // If sprint key held and stamina available
            sprint(); // Switch to sprint movement mode
            noiseValue = 3; // Louder noise value for sprinting
            noiseRadiusTiles = 6; // Bigger radius for guard hearing
        } else if (keyH.crouchPressed) { // If crouch key held
            crouch(); // Switch to crouch movement mode
            noiseValue = 0; // No noise while crouching
            noiseRadiusTiles = 0; // No hearing radius while crouching
        } else { // Default: walking
            walk(); // Normal walking movement and regen
            noiseValue = 1; // Normal footstep noise
            noiseRadiusTiles = 2; // Normal hearing radius
        }

        // ---------- Flashlight lighting ----------
        if (keyH.interactPressed && hasFlashlight && gp.ui.selectedItem instanceof Flashlight) { // If interacting with flashlight
            interact("torch"); // Handle torch interaction (lighting)
        } else if (lastPlayerCol != -1) { // If not interacting and there was a previously lit area
            // Turn off previously lit tiles when not holding interact
            for (int c = lastPlayerCol - 1; c <= lastPlayerCol + 1; c++) { // Loop columns in 3x3 region
                for (int r = lastPlayerRow - 1; r <= lastPlayerRow + 1; r++) { // Loop rows in 3x3 region
                    if (c >= 0 && c < gp.maxWorldCol && r >= 0 && r < gp.maxWorldRow) { // Bounds check
                        if (gp.tileM.mapTileNum[c][r] == 167) gp.tileM.mapTileNum[c][r] = 0; // Turn lit tile back to dark (167 -> 0)
                    }
                }
            }
            lastPlayerCol = -1; // Reset last lit column marker
            lastPlayerRow = -1; // Reset last lit row marker
        }
        
        // ---------- Throwing items ----------
        if (gp.keyH.throwJustPressed && gp.ui.selectedItem instanceof Throwable) { // If throw key was pressed and selected item is throwable
            interactThrow((Throwable) gp.ui.selectedItem); // Toggle throw UI / prepare throw
            gp.keyH.throwJustPressed = false; // Consume the throw press
        }
        
        // ---------- Eating food ----------
        if (gp.keyH.interactPressed && gp.ui.selectedItem instanceof Food) { // If interact pressed on a Food item
            // Find index of selected food in inventory
            int foodIndex = -1; // Default -1 meaning not found
            // Search inventory for the selected food object
            for (int i = 0; i < inventory.size(); i++) { // Iterate inventory
                if (inventory.get(i) == gp.ui.selectedItem) { // Compare by reference
                    foodIndex = i; // Save found index
                    break; // Exit loop
                }
            }
            consumeItem(foodIndex); // Consume the food by index (if valid)
            gp.keyH.interactPressed = false; // Consume the interact press
        }
        
        // ---------- update inventory items ----------
        for (Item item : inventory) { // Update each item (pickupDelay, throwDelay, etc.)
            item.update(); // Call item-specific update
        }

        // ---------- clear interact prompt for this frame ----------
        gp.ui.hideInteract(); // Hide the UI interact hint by default

        boolean moved = false; // Flag to know if the player moved this frame

        // ---------- movement + collision checks ----------
        if (dx != 0 || dy != 0) { // If player attempted to move this frame
            for (int step = 0; step < speed; step++) { // Move one pixel at a time, speed times
                // Try X
                if (dx != 0 && collisionOn) { // Horizontal move with collision enabled
                    int tile = gp.cChecker.getCollidingTile(this, dx, 0); // Check tile collision on X
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, dx, 0); // Check NPC collision on X
                    int taskIndex = gp.cChecker.checkTask(this, gp.tasks, dx, 0); // Check Task collision on X
                    // Treat guards as non-blocking here (we'll check hitboxes separately)
                    if (tile == -1 && npcIndex == 999 && taskIndex == 999) { // No collisions detected
                        worldX += dx; // Apply horizontal movement
                        col = worldX / gp.tileSize; // Update column
                        moved = true; // Mark as moved
                    } else if (npcIndex != 999) { // NPC is in the way
                        gp.ui.showInteract(); // Show interact hint
                        if (keyH.interactPressed) interactNPC(npcIndex); // Interact if button pressed
                        break; // Stop movement this frame
                    } else if (((tile == 211 || tile == 212) && (level == 1 || level == 3)) || ((tile == 204 || tile == 205) && level == 2)) { // Special exit tiles per level
                        gp.ui.showInteract(); // Show interact hint
                        if (keyH.interactPressed) interact("exit"); // Interact with exit if pressed
                        break; // Stop movement this frame
                    } else if (taskIndex != 999) { // Task is in the way
                        gp.ui.showInteract(); // Show interact hint
                        curTaskIndex = taskIndex; // Save current task index
                        curTaskName = gp.tasks[taskIndex].getName(); // Save current task name
                        if (keyH.interactPressed) interactTask(curTaskName); // Open task UI if pressed
                        break; // Stop movement this frame
                    } else {
                        break; // Default: stop movement if blocked
                    }
                } else if (!collisionOn) { // If collision disabled, move freely on X
                    worldX += dx; // Apply horizontal move
                    moved = true; // Mark as moved
                }

                // try Y
                if (dy != 0 && collisionOn) { // Vertical move with collision enabled
                    int tile = gp.cChecker.getCollidingTile(this, 0, dy); // Check tile collision on Y
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, 0, dy); // Check NPC collision on Y
                    int taskIndex = gp.cChecker.checkTask(this, gp.tasks, 0, dy); // Check Task collision on Y
                    if (tile == -1 && npcIndex == 999 && taskIndex == 999) { // No collisions
                        worldY += dy; // Apply vertical movement
                        row = worldY / gp.tileSize; // Update row
                        moved = true; // Mark as moved
                    } else if (npcIndex != 999) { // NPC in front
                        gp.ui.showInteract(); // Show interact hint
                        if (keyH.interactPressed) interactNPC(npcIndex); // Interact if pressed
                        break; // Stop movement this frame
                    } else if (
                    	    (level == 1 || level == 3) && (tile == 211 || tile == 212) || // Level-specific exit tiles
                    	    level == 2 && (tile == 204 || tile == 205) ||
                    	    level == 4 && (tile == 472 || tile == 473 || tile == 474 || tile == 475)
                    	) {
                    	    gp.ui.showInteract(); // Show interact hint for exit
                    	    if (keyH.interactPressed) {
                    	        interact("exit"); // Interact with exit
                    	    }
                    	    break; // Stop movement this frame
                    } else if (taskIndex != 999) { // Task near
                        gp.ui.showInteract(); // Show interact hint
                        curTaskIndex = taskIndex; // Save task index
                        curTaskName = gp.tasks[taskIndex].getName(); // Save task name
                        if (keyH.interactPressed) interactTask(curTaskName); // Interact if pressed
                        break; // Stop movement
                    } else {
                        break; // Default: stop movement if blocked
                    }
                } else if (!collisionOn) { // If collision disabled, move freely on Y
                    worldY += dy; // Apply vertical movement
                    moved = true; // Mark as moved
                }
            }

            // picking up items + events
            int itemIndex = gp.cChecker.checkItem(this, true); // Check for items at player position (and pickable)
            pickUpItem(itemIndex); // Attempt to pick up item if present
            gp.eHandler.checkEvent(); // Check for events after movement

            // animate
            spriteCounter++; // Increment animation counter
            if (spriteCounter > 19 - (1.5 * speed)) { // Flip sprite faster with higher speed
                spriteNum = (spriteNum == 1) ? 2 : 1; // Toggle sprite frame
                spriteCounter = 0; // Reset counter
            }
        } else { // If no movement input this frame
            // ---------- standing checks ----------
            int checkX = 0, checkY = 0; // Variables for checking tile/NPC/task in front of player
            switch (direction) { // Determine the tile in front based on facing
                case "up":    checkY = -gp.tileSize; break; // Tile above player
                case "down":  checkY = gp.tileSize;  break; // Tile below player
                case "left":  checkX = -gp.tileSize; break; // Tile left of player
                case "right": checkX = gp.tileSize;  break; // Tile right of player
            }

            int collidedTile = gp.cChecker.getCollidingTile(this, checkX, checkY); // Check tile number in front
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc, checkX, checkY); // Check NPC index in front
            int taskIndex = gp.cChecker.checkTask(this, gp.tasks, checkX, checkY); // Check Task index in front

            if (npcIndex != 999) { // If NPC is in front
                gp.ui.showInteract(); // Show 'press to interact' UI
                if (keyH.interactPressed) interactNPC(npcIndex); // Interact if pressed
            } else if (collidedTile == 211 || collidedTile == 212) { // Specific tile numbers for van exit
                gp.ui.showInteract(); // Show interact hint
                if (keyH.interactPressed) interact("exitVan"); // Interact with van exit
            } else if (taskIndex != 999) { // If a task is in front
                gp.ui.showInteract(); // Show interact hint
                curTaskIndex = taskIndex; // Save task index
                curTaskName = gp.tasks[taskIndex].getName(); // Save task name
                if (keyH.interactPressed) interactTask(curTaskName); // Open task UI if pressed
            }

            // pick up items while standing
            int itemIndex = gp.cChecker.checkItem(this, true); // Check for nearby items
            if (itemIndex != 999) { // If an item found
                pickUpItem(itemIndex); // Pick it up
            }
            
            standCounter++; // Increment idle counter
            if (standCounter >= 20) { // If stood still for certain frames
                spriteNum = 1; // Reset to idle sprite
                standCounter = 0; // Reset counter
            }
        }

        // ---------- sound trigger for guards ----------
        if (moved && noiseValue > 0) { // If moved and action produced noise
            long now = System.currentTimeMillis(); // Current time
            if (now - lastFootstepTime >= footstepInterval) { // Ensure minimum interval between triggering guards
                gp.triggerSoundForGuards(worldX, worldY, noiseRadiusTiles); // Notify guards about sound event
                lastFootstepTime = now; // Update last footstep time
            }
        }

        // ---------- GUARD HITBOX CHECK (precise overlap only) ----------
        // build player hitbox at current world position (do NOT mutate solidArea)
        Rectangle playerBox = new Rectangle( // Construct a new Rectangle that represents player's world hitbox
            worldX + solidArea.x, // X coordinate of hitbox in world space
            worldY + solidArea.y, // Y coordinate of hitbox in world space
            solidArea.width, // Width of hitbox
            solidArea.height // Height of hitbox
        );

        for (int i = 0; i < gp.gaurds.length; i++) { // Iterate all guards in the level
            if (gp.gaurds[i] == null) continue; // Skip null guard slots
            // guard's current hitbox
            Rectangle guardBox = new Rectangle( // Construct guard hitbox in world coordinates
                gp.gaurds[i].worldX + gp.gaurds[i].solidArea.x, // Guard hitbox X
                gp.gaurds[i].worldY + gp.gaurds[i].solidArea.y, // Guard hitbox Y
                gp.gaurds[i].solidArea.width, // Guard hitbox width
                gp.gaurds[i].solidArea.height // Guard hitbox height
            );
            if (playerBox.intersects(guardBox)) { // If player's hitbox intersects guard's hitbox
                // true overlap -> die
                interactGaurd(); // Trigger guard interaction (player death)
                break; // Stop checking further guards
            }
        }
    }


    // modular interact method - only handles "torch" here
    public void interact(String item) { // Generalized interact method handling item-based interactions
        if ("torch".equals(item)) { // If the interaction is the torch/flashlight
	        // current player tile
	        int playerCol = worldX / gp.tileSize; // Compute player's column tile
	        int playerRow = worldY / gp.tileSize; // Compute player's row tile
	
	        // CHECK: only allow torch if the tile the player stands on is DARK (0)
	        int currentTileNum = gp.tileM.mapTileNum[playerCol][playerRow]; // Get current tile number
	        if (currentTileNum != 0) { // If tile is not dark
	            // Player on a regular (non-dark) tile: ensure any previous lit area is turned off
	            if (lastPlayerCol != -1) { // If there was a previously lit tile region
	                for (int col = lastPlayerCol - 1; col <= lastPlayerCol + 1; col++) { // Loop previous 3x3 region columns
	                    for (int row = lastPlayerRow - 1; row <= lastPlayerRow + 1; row++) { // Loop previous 3x3 region rows
	                        if (col >= 0 && col < gp.maxWorldCol &&
	                            row >= 0 && row < gp.maxWorldRow) { // Bounds check
	                            if (gp.tileM.mapTileNum[col][row] == 167) { // If tile was lit marker
	                                gp.tileM.mapTileNum[col][row] = 0; // Turn it back to dark
	                            }
	                        }
	                    }
	                }
	                lastPlayerCol = -1; // Reset last lit col
	                lastPlayerRow = -1; // Reset last lit row
	            }
	            return; // Exit because torch only works on dark tiles
	        }
	
	        // only do work when player enters a new tile (prevents re-setting same tiles every frame)
	        if (playerCol == lastPlayerCol && playerRow == lastPlayerRow) { // If still on same tile as last lit
	            return; // Nothing to do
	        }
	
	        // TURN OFF old lit tiles (if any)
	        if (lastPlayerCol != -1) { // If previously lit region exists
	            for (int col = lastPlayerCol - 1; col <= lastPlayerCol + 1; col++) { // Loop previous 3x3 cols
	                for (int row = lastPlayerRow - 1; row <= lastPlayerRow + 1; row++) { // Loop previous 3x3 rows
	                    if (col >= 0 && col < gp.maxWorldCol &&
	                        row >= 0 && row < gp.maxWorldRow) { // Bounds check
	                        if (gp.tileM.mapTileNum[col][row] == 167) { // If previously lit marker
	                            gp.tileM.mapTileNum[col][row] = 0; // Turn back to dark
	                        }
	                    }
	                }
	            }
	        }
	
	        // LIGHT new surrounding tiles (3x3). Only replace dark tiles (0) with lit (167)
	        for (int col = playerCol - 1; col <= playerCol + 1; col++) { // Loop target 3x3 region columns
	            for (int row = playerRow - 1; row <= playerRow + 1; row++) { // Loop target 3x3 region rows
	                if (col >= 0 && col < gp.maxWorldCol &&
	                    row >= 0 && row < gp.maxWorldRow) { // Bounds check
	                    if (gp.tileM.mapTileNum[col][row] == 0) { // If tile is dark
	                        gp.tileM.mapTileNum[col][row] = 167; // Mark as lit
	                    }
	                }
	            }
	        }
	
	        // save current tile so we can turn it off when they move or release E
	        lastPlayerCol = playerCol; // Store last lit column
	        lastPlayerRow = playerRow; // Store last lit row
	    }
        if ("exit".equals(item)) { // If interaction is an exit
        	if (tasksComplete) { // Allow exit only if tasks are complete
        		gp.stopMusic(); // Stop background music
    			gp.playSoundEffect(2); // Play exit sound
    			gp.gameState = gp.dialogueState; // Switch to dialogue state to show completion dialogue
    			levelUp(); // Handle level completion and rewards
    			speak(); // Trigger completion dialogue
        	} else { // If tasks incomplete
        		gp.ui.showBoxMessage("Tasks Incomplete!"); // Inform player they cannot leave
        	}
		}

    }
    
    public void levelUp() { // Handle level completion: unlock skins, reset player, advance level
        switch (level) {
            case 1 -> unlockSkin(5); // unlock BillyGoat skin when finishing level 1
            case 2 -> unlockSkin(6); // unlock skin for level 2
            case 3 -> unlockSkin(2); // unlock skin for level 3
            case 4 -> unlockSkin(3); // unlock skin for level 4
        }
        
        // reset position/stats manually
        worldX = gp.tileSize * 6; // Reset world X to default spawn
        worldY = gp.tileSize * 16; // Reset world Y to default spawn
        row = worldY / gp.tileSize; // Update row
        col = worldX / gp.tileSize; // Update col
        stamina = maxStamina; // Refill stamina
        speed = walkSpeed; // Reset speed to walk
        direction = "down"; // Reset facing direction
        gp.ui.levelFinished = true; // Notify UI that level finished
		gp.level++; // Advance gp level
		level = gp.level; // Sync local level with gp
    }
    
    public void setDialogues() { // Initialize dialogues used when finishing levels and credits
    	switch (gp.level) { // Dialogue varies by level
    	case 1:
			dialogues[0] = "Gaurd: Payload delivery to the warehouse \n Attendant: All clear, you may proceed.\n\n\nCompleted Level 1!\n You have unlocked the BillyGoat skin.";
			dialogues[1] = "Gaurd: Anyone seen my blue keys? \n Billy Goat: Idk man, jone's gonna be mad if he finds out though.\n\n\nCompleted Level 2!\n You have unlocked the Marv skin.";
			dialogues[2] = "Gaurd: It's too hot for this job man \n Marv: Whatever man, just drive.\n\n\nCompleted Level 3!\n You have unlocked the Old Timer skin.";
			dialogues[3] = "Jone: HEY GET BACK HERE! \nyou still have to serve 5 more decades of code commenting\n\n\nCompleted Level 4!\n You have unlocked the Froseph skin.";
			dialogues[4] = "YOUVE ESCAPED! GAME OVER.\n\n\n Credits: \nRafay, Christina, Sukhmanpreet, Jeevan, Samir";
			break;    		
    	}

	}
    
    public void speak() { // Trigger speak using base entity behavior to advance dialogues
		super.speak(); // Call parent speak to show the current dialogue line and advance index
    }
    
    public void interactNPC(int index) { // Interact with NPC at given index
		if (index != 999) { // If valid NPC index
			gp.ui.currentDialogueSpeaker = gp.npc[index].name; // Set speaker name in UI
			gp.gameState = gp.dialogueState; // Switch game state to dialogue
			gp.npc[index].speak(); // Call the NPC's speak method
		}
	}
    
    public void interactTask(String taskName) { // Interact with a task by name
    	System.out.println("Interacting with task: " + taskName); // Debug print
    	gp.gameState = gp.taskState; // Switch to task UI/state
    }
    
    
    public void interactGaurd() { // Called when touching a guard hitbox
    	gp.eHandler.playerDied(); // Trigger the player-death handler
    }
    
    public void updateInventory() { // Refresh item flags based on inventory contents
		for (Item item : inventory) { // Iterate each item
			refreshItem(item.getName()); // Update flags/tiles for each item name
		}
	}
    
    public void refreshItem(String name) { // Update player state based on item name present in inventory

        switch (name) { // Decide behavior using item name

            case "Key":
                hasKey = true; // Set generic key flag
                TileManager.unlockTile(227); // Unlock tile 227 in TileManager
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Red Key":
                hasRedKey = true; // Set red key flag
                TileManager.unlockTile(193); // Unlock tile 193
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Green Key":
                hasGreenKey = true; // Set green key flag
                TileManager.unlockTile(219); // Unlock tile 219
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Blue Key":
                hasBlueKey = true; // Set blue key flag
                TileManager.unlockTile(204); // Unlock relevant tiles for blue key
                TileManager.unlockTile(205);
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Flashlight":
                hasFlashlight = true; // Set flashlight flag
                gp.playSoundEffect(3); // Play flashlight pickup sound
                break;
                
            case "Pebble":
            	hasPebble = true; // Set pebble flag
            	gp.playSoundEffect(3); // Play pickup sound
            	break;
            	
            case "Can":
            	hasCan = true; // Set can flag
				gp.playSoundEffect(3); // Play pickup sound
				break;
            case "Tray":
            	hasTray = true; // Set tray flag
            	gp.playSoundEffect(3); // Play pickup sound
            	break;
            case "Apple":
            	hasApple = true; // Set apple flag
				gp.playSoundEffect(3); // Play pickup sound
				break;
			case "Bread":
				hasBread = true; // Set bread flag
				gp.playSoundEffect(3); // Play pickup sound
				break;
			case "Protein Bar":
				hasProteinBar = true;	// Set protein bar flag
				gp.playSoundEffect(3); // Play pickup sound
				break;
				
            default:
                return; // No action for unknown names
        }
    }

    public void pickUpItem(int index) { // Attempt to pick up an item at the provided world slot index

        if (index == 999) return; // 999 means no item present
        Item item = gp.items[index]; // Get item reference from global items array
        if (item == null) return; // Guard: nothing to pick up

        // block instant re-pickup after drop
        if (item.pickupDelay > 0) return; // Respect pickup delay

        if (inventory.size() >= INVENTORY_SIZE) { // If inventory full
            gp.ui.showBoxMessage("Inventory full!"); // Show UI message
            return; // Abort pickup
        }

        switch (item.getName()) { // Handle pickup effects by item name

            case "Key":
                hasKey = true; // Update flag
                TileManager.unlockTile(227); // Unlock associated tile
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Red Key":
                hasRedKey = true; // Update flag
                TileManager.unlockTile(193); // Unlock tile
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Green Key":
                hasGreenKey = true; // Update flag
                TileManager.unlockTile(219); // Unlock tile
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Blue Key":
                hasBlueKey = true; // Update flag
                TileManager.unlockTile(204); // Unlock tiles for blue key
                TileManager.unlockTile(205);
                gp.playSoundEffect(1); // Play pickup sound
                break;

            case "Flashlight":
                hasFlashlight = true; // Update flag
                gp.playSoundEffect(3); // Play pickup sound
                break;
                
            case "Pebble":
            	hasPebble = true; // Update flag
            	gp.playSoundEffect(3); // Play pickup sound
            	break;
            	
            case "Can":
            	hasCan = true; // Update flag
				gp.playSoundEffect(3); // Play pickup sound
				break;
            case "Tray":
            	hasTray = true; // Update flag
            	gp.playSoundEffect(3); // Play pickup sound
            	break;
            case "Apple":
            	hasApple = true; // Update flag
                gp.aSetter.startAppleRespawn(item.worldX, item.worldY); // Start respawn timer for apple
				gp.playSoundEffect(3); // Play pickup sound
				break;
			case "Bread":
				hasBread = true; // Update flag
				gp.aSetter.startBreadRespawn(item.worldX, item.worldY); // Start respawn for bread
				gp.playSoundEffect(3); // Play pickup sound
				break;
			case "Protein Bar":
				hasProteinBar = true;	// Update flag
				gp.playSoundEffect(3); // Play pickup sound
				break;
				
            default:
                return; // Unknown items: do nothing
        }

        // ✅ MOVE the SAME object into inventory
        inventory.add(item); // Add item instance reference to player's inventory

        // ✅ REMOVE from world
        gp.items[index] = null; // Remove reference from world items array
    }
    
    public void interactThrow(Throwable throwable) { // Toggle throw UI or set throw cooldown
        // block if still cooling down
        if (throwable.throwDelay > 0) return; // Respect throwable's cooldown
        
        if (gp.ui.showThrowRadius && gp.ui.activeThrowable == throwable) { // If already showing and same throwable selected
            // Toggle OFF if clicking same throwable again
            gp.ui.showThrowRadius = false; // Hide the throw radius UI
            gp.ui.activeThrowable = null; // Clear active throwable reference

            gp.selectedThrowCol = -1; // Reset selected column
            gp.selectedThrowRow = -1; // Reset selected row
        } else {
            // Toggle ON
            gp.ui.activeThrowable = throwable; // Mark throwable as active
            gp.ui.showThrowRadius = true; // Show throw radius UI
            throwable.throwDelay = 60; // Set cooldown on throwable (frames)
        }
    }
    
    public void throwItem(Throwable throwable, int targetCol, int targetRow) { // Throws a throwable item to a target tile if valid

        if (throwable == null) return; // Nothing to throw, exit early
        if (targetCol < 0 || targetRow < 0) return; // Invalid negative target coordinates, exit early

        if (targetCol >= gp.maxWorldCol || targetRow >= gp.maxWorldRow) { // Target outside map bounds
            gp.ui.showMessage("Invalid target"); // Inform player
            return; // Abort throw
        }

        int playerCol = worldX / gp.tileSize; // Player's current column on the tile grid
        int playerRow = worldY / gp.tileSize; // Player's current row on the tile grid
        int dx = targetCol - playerCol; // Column difference to target
        int dy = targetRow - playerRow; // Row difference to target
        double dist = Math.sqrt(dx*dx + dy*dy); // Euclidean distance in tiles to target
        if (dist > throwable.getAllowedRadiusTiles() + 0.0001) { // If target is beyond allowed throw radius
            gp.ui.showMessage("Target out of range"); // Inform player
            return; // Abort throw
        }

        int invIndex = -1; // Inventory index of the throwable to remove
        for (int i = 0; i < inventory.size(); i++) { // Search inventory for the matching item
            Item it = inventory.get(i); // Current inventory item
            if (it == throwable || (it.getName() != null && it.getName().equals(throwable.getName()))) { // Match by reference or name
                invIndex = i; // Save index
                break; // Stop searching
            }
        }

        if (invIndex == -1) { // If no matching item found in inventory
            gp.ui.showMessage("No throwable in inventory"); // Inform player
            return; // Abort throw
        }

        int worldSlot = gp.getEmptyItemSlot(); // Find an empty slot in the world item array
        if (worldSlot == -1) { // If world has no free slot
            gp.ui.showMessage("World is full, can't throw"); // Inform player
            return; // Abort throw
        }

        Item toPlace = inventory.get(invIndex); // The actual item instance to place in the world

        toPlace.worldX = targetCol * gp.tileSize; // Convert target column to world X and assign
        toPlace.worldY = targetRow * gp.tileSize; // Convert target row to world Y and assign

        toPlace.pickupDelay = 60; // Prevent immediate pickup for a short time (frames)

        try { // Best-effort: set throwDelay on the throwable instance(s)
            throwable.throwDelay = 60; // Set throw delay on the passed throwable reference
            if (toPlace instanceof Throwable) ((Throwable) toPlace).throwDelay = 60; // Also set on the placed item if it implements Throwable
        } catch (Exception ignored) {} // Ignore any reflection/type errors silently

        gp.items[worldSlot] = toPlace; // Place the item into the world's item array
        inventory.remove(invIndex); // Remove the item from the player's inventory

        gp.triggerSoundForGuards(toPlace.worldX, toPlace.worldY, throwable.getAllowedRadiusTiles()); // Notify guards of the sound at the throw location

        String name = toPlace.getName(); // Get the placed item's name
        if (name != null) { // If name exists
            if (name.equalsIgnoreCase("Pebble")) hasPebble = false; // Clear player's pebble flag if a pebble was thrown
        }

        gp.ui.showThrowRadius = false; // Hide throw radius UI
        gp.ui.activeThrowable = null; // Clear active throwable UI reference
        gp.selectedThrowCol = -1; // Reset selected throw column
        gp.selectedThrowRow = -1; // Reset selected throw row

        try { gp.playSoundEffect(6); } catch (Exception ignored) {} // Play throw sound effect (best-effort)
    }
    
    public void consumeItem(int index) { // Consume a food item from inventory by index
		if (index < 0 || index >= inventory.size()) return; // Guard: invalid index
		Item item = inventory.get(index); // Get the item at index
		if (item == null) return; // Guard: null check

		String name = item.getName(); // Get item's name
		if (name == null) return; // Guard: no name means can't handle

		switch (name) { // Handle consumables by name
			case "Apple":
				hasApple = false; // Clear apple flag
				gp.playSoundEffect(5); // Play eat sound
				break;
			case "Bread":
				hasBread = false; // Clear bread flag
				gp.playSoundEffect(5); // Play eat sound
				break;
			case "Protein Bar":
				hasProteinBar = false; // Clear protein bar flag
				gp.playSoundEffect(5); // Play eat sound
				break;
			default:
				return; // not consumable
		}
		if (stamina >= maxStamina) { // If stamina already full
			gp.ui.showBoxMessage("Stamina full!"); // Show message and abort
			return;
		}
		stamina += maxStamina * (((Food) item).getRestoreValue()); // Restore a fraction of max stamina

		// remove from inventory
		inventory.remove(index); // Remove consumed item
		gp.ui.selectedItem = null; // Clear selected item in UI
	}

    public void dropItem(int index) { // Drop an item from inventory into the world

        System.out.println("Attempting to drop item at index: " + index); // Debug log for drops

        if (inventory.isEmpty()) return; // Nothing to drop
        if (index < 0 || index >= inventory.size()) return; // Invalid index

        Item original = inventory.get(index); // Reference to the item being dropped

        // ---------- UPDATE PLAYER FLAGS FIRST ----------
        if (original instanceof Flashlight) { // If dropping flashlight, turn off lit tiles and clear flag
            hasFlashlight = false; // Player no longer has flashlight

            // turn off lit tiles
            if (lastPlayerCol != -1) { // If there was an area lit
                for (int col = lastPlayerCol - 1; col <= lastPlayerCol + 1; col++) {
                    for (int row = lastPlayerRow - 1; row <= lastPlayerRow + 1; row++) {
                        if (col >= 0 && col < gp.maxWorldCol &&
                            row >= 0 && row < gp.maxWorldRow) {
                            if (gp.tileM.mapTileNum[col][row] == 167) {
                                gp.tileM.mapTileNum[col][row] = 0; // Turn lit marker back to dark
                            }
                        }
                    }
                }
                lastPlayerCol = -1; // Reset last lit col
                lastPlayerRow = -1; // Reset last lit row
            }
        }

        if (original instanceof Key) { // If dropping generic Key
            hasKey = false; // Clear hasKey flag
            TileManager.lockTile(227); // Lock tile associated with the key
        }
        if (original instanceof redKey) { // Dropping redKey
            hasRedKey = false; // Clear flag
            TileManager.lockTile(193); // Lock tile 193
        }
        if (original instanceof greenKey) { // Dropping greenKey
            hasGreenKey = false; // Clear flag
            TileManager.lockTile(219); // Lock tile 219
        }
        if (original instanceof blueKey) { // Dropping blueKey
            hasBlueKey = false; // Clear flag
            TileManager.lockTile(204); // Lock its tiles
            TileManager.lockTile(205);
        }
        
        if (original instanceof Pebble) { // Dropping pebble
			hasPebble = false; // Clear pebble flag
		}
        
        if (original instanceof Can) { // Dropping can
			hasCan = false; // Clear can flag
		}
        
        if (original instanceof Tray) { // Dropping tray
        	hasTray = false; // Clear tray flag
        }
        
        if (original instanceof Throwable) { // If dropped item is throwable, reset throw UI state
        	 gp.ui.showThrowRadius = false; // Hide throw radius UI
             gp.ui.activeThrowable = null; // Clear active throwable
             gp.selectedThrowCol = -1; // Reset selected column
             gp.selectedThrowRow = -1; // Reset selected row
		}

        Item toDrop = original; // Use same instance for dropping

     // position
     toDrop.worldX = worldX; // Place dropped item at player's world X
     toDrop.worldY = worldY; // Place dropped item at player's world Y

     // prevent instant pickup
     toDrop.pickupDelay = 60; // Set brief delay to avoid immediate re-pickup

     // place in world
     gp.aSetter.placeItem(toDrop, worldX, worldY); // Use aSetter helper to place into world items
     gp.ui.selectedItem = null; // Clear UI selection
     gp.ui.slotRow = -1; // Reset UI slot row selection

     // remove from inventory
     inventory.remove(index); // Remove the item from player's inventory

     gp.playSoundEffect(6); // Play drop/throw sound
    }
    
    public Rectangle getHitbox() { // Returns the entity's collision box positioned in world coordinates
        return new Rectangle( // Create and return a new Rectangle instance (safe copy for callers)
            worldX + solidArea.x, // X position: entity world X plus local solidArea X offset
            worldY + solidArea.y, // Y position: entity world Y plus local solidArea Y offset
            solidArea.width,      // Width: use the configured solidArea width
            solidArea.height      // Height: use the configured solidArea height
        ); // End Rectangle construction
    } // End getHitbox



    public void draw(java.awt.Graphics2D g2) { // Draw player sprite and debug UI

        BufferedImage image = null; // Placeholder for selected sprite image

        switch (direction) { // Choose sprite based on facing direction and animation frame
            case "up":
                image = (spriteNum == 1) ? up1 : up2; // Pick up1 or up2
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : down2; // Pick down1 or down2
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : left2; // Pick left1 or left2
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : right2; // Pick right1 or right2
                break;
        }

        g2.drawImage(image, screenX, screenY, null); // Draw the chosen sprite at screen center
        
        gp.uTool.showPlayerPosition(g2, worldX, worldY, row, col); // Optional debug/UI helper showing player position on map
    }
}
