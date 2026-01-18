/*
Names: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment: Jones Junction
File Name: player.java

Description:
This class controls the main player character.
It handles movement, stamina (walk/sprint/crouch), collision checks,
inventory (pick up/drop/use items), interacting with NPCs and tasks,
and drawing the player to the screen.
*/

package entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Item.*;
import Item.Throwable;
import main.UtilityTool;
import main.gamePanel;
import main.keyHandler;
import task.*;
import tile.TileManager;

public class player extends entity {

    keyHandler keyH; //Reads keyboard input

    //Where the player is drawn on the screen ( Center of the screen)
    private final int screenX;
    private final int screenY;
    
    //Player position in the world grid 
    public int row;
    public int col;
    
    //Inventory. Stores items the player is carrying 
    public ArrayList<Item> inventory = new ArrayList<>();
    
    //Tasks List. Store tasks for the specific level
	public ArrayList<Task> tasksList = new ArrayList<>(); 
	
	//Max number of items the player can hold
    public final int INVENTORY_SIZE = 3;
    

    public String equppedSkin; 
    
    //Skin/character selection storage used by the title character screen
    public boolean[] unlockedSkins; //Tracks which skins the player owns
    public int equippedSkinIndex = 0; //Which skin is currently used
    public int currentSkinIndex = 0; //Used for selecting skin menu
    public String equippedSkin; //Stores skin names/path
    
    //Current task info when standing near a task
    public int curTaskIndex; 
    public String curTaskName;
    
    //Used to decide if player can leave the level
    public boolean tasksComplete;
    
    private long lastFootstepTime = 0; // Timestamp (ms) of the last footstep sound played

    private long footstepInterval = 200; // Minimum interval between footsteps in milliseconds

    public int noiseValue = 0; // Loudness value of the player's current action (used to rank sounds)

    public int noiseRadiusTiles = 0; // Radius in tiles that the current noise can be heard by guards

 //Item Checks (quick booleans to check if the player has something)
    public boolean hasKey;
    public boolean hasBlueKey;
    public boolean hasRedKey;
    public boolean hasGreenKey;
    public boolean hasPebble;
    public boolean hasCan;
    public boolean hasTray;
    public boolean hasApple;
    public boolean hasBread;
    public boolean hasProteinBar;
    
    //Used to reset the animation when standing still
    int standCounter = 0; 
    
    //Flashlight system
    public static boolean hasFlashlight = false;          // set true when player picks up a Torch item
    int lastPlayerCol = -1;            // column of the tile that was last used to light
    int lastPlayerRow = -1;            // row of the tile that was last used to light
    
    //Player life state (used for dying or game over logic)
    public boolean isAlive;

    //Getters for screen position
    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    //Sets up the player when the game starts
    public player(gamePanel gp, keyHandler keyH) {
    	super(gp); //Call the parent class (entity) constructor first
    	
        this.keyH = keyH; //Save the key handler for input checks

        //Center the player on the screen (camera view)
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

     //Create a collision box inside the player sprite
        solidArea = new Rectangle();
        solidArea.x = gp.tileSize / 3;
        solidArea.y = gp.tileSize / 3;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 22;
        solidArea.height = 22;
        
        //Stamina settings
        maxStamina = 100f;
        stamina = maxStamina;
        staminaRegen = 5f;       //Stamina regained per second
        sprintStaminaCost = 20f; // Stamina drained per second while sprinting
        
        //Load the equipped skin from gamePanel
        equippedSkinIndex = gp.equippedSkinIndex;
        equippedSkin = gp.equippedSkin;
        
        isAlive = true; //Player starts alive

     //Set starting values and load images and default content
        setDefaultValues();
        getPlayerImage();
        setItems();
        setDialogues();
    }
    
    //Gives the player starting items (for testing)
    public void setItems() {
    	inventory.add(new blueKey(gp));
    	updateInventory();
    }

    //Clears all items from inventory 
    public void clearInventory() {
    	inventory.clear();
    			
    }
    
    //Equip a skin if it exists and is unlocked
    public void equipSkin(int index) {
        if (index < 0 || index >= gp.skins.length) return; //Invalid index
        if (!unlockedSkins[index]) return; //Skin not unlocked

        //Save equipped skin into gamePanel so other screens know it
        gp.equippedSkinIndex = index;
        gp.equippedSkin = gp.skins[index][0][0]; //Stores name

        //Update player fields 
        equippedSkinIndex = index;
        equippedSkin = gp.equippedSkin;

        getPlayerImage();  //Reload player images using the new skin paths
    }

    //Unlock a skin, after finishing a level
    public void unlockSkin(int index) {
        if (index < 0 || index >= unlockedSkins.length) return;
        System.out.println("trying to unlock skin: " + gp.skins[index][0][0]);
        unlockedSkins[index] = true; //Mark as unlocked
        gp.skins[index][1][0] = "unlocked"; //Update skins array
    }


    //Add an item if inventory has space
    public void addItem(Item item) {
		if (inventory.size() < INVENTORY_SIZE) {
			inventory.add(item);
		}
	}
    
    //Remove item by index (only if index is valid)
    public void removeItem(int index) {
		if (index >= 0 && index < inventory.size()) {
			inventory.remove(index);
		}
    }

    //Sets starting position, stats, and skin info
    public void setDefaultValues() {
    	
    	//Starting world position (in pixels)
        worldX = gp.tileSize * 6;
        worldY = gp.tileSize * 16;
        
        //Converts to row/col on tile grid
        row = worldY / gp.tileSize;
        col = worldX / gp.tileSize;

        //Unlock skins
        unlockedSkins = new boolean[gp.skins.length];
        for (int i = 0; i < gp.skins.length; i++) {
            unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked");
        }

        //Instead of resetting to 0, use the skin stored in gamePanel
        equippedSkinIndex = gp.equippedSkinIndex;
        equippedSkin = gp.equippedSkin;

        //Speeds for different movement types
        walkSpeed = 4;
        sprintSpeed = 16;
        crouchSpeed = 2;
        
        speed = walkSpeed; //Current speed starts as walking
        
        direction = "down";  //Starting direction
        level = gp.level; //Sync with current level
        stamina = maxStamina;
        
        tasksComplete = true; //...
        collisionOn = true; //Player collision is active
        
        getPlayerImage(); //Load images for current skin
    }

    //Loads all player sprites based on which skin is equipped
    public void getPlayerImage() {

    	//This makes sure gp and skins exist
        if (gp == null || gp.skins == null) return;

        //Each skin list of image paths
        String[] paths = gp.skins[equippedSkinIndex][2];

        //Index 0 is assumed to be a safe idle / fallback frame
        BufferedImage fallback = loadAndScale(paths[0]);

        // Up
        up1 = loadAndScaleSafe(paths, 1, fallback);
        up2 = loadAndScaleSafe(paths, 2, fallback);

        // Down
        down1 = loadAndScaleSafe(paths, 3, fallback);
        down2 = loadAndScaleSafe(paths, 4, fallback);

        // Right
        right1 = loadAndScaleSafe(paths, 5, fallback);
        right2 = loadAndScaleSafe(paths, 6, fallback);

        // Left
        left1 = loadAndScaleSafe(paths, 7, fallback);
        left2 = loadAndScaleSafe(paths, 8, fallback);
    }
    
    //Loads one image and scales it to tile size
    private BufferedImage loadAndScale(String path) {
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path));
            return gp.uTool.scaleImage(img, gp.tileSize, gp.tileSize);
        } catch (Exception e) {
            return null; //If image fails to load, return null
        }
    }

    //If index is invalid or image missing, return fallback
    private BufferedImage loadAndScaleSafe(String[] paths, int index, BufferedImage fallback) {
        if (index < 0 || index >= paths.length || paths[index] == null) {
            return fallback;
        }

        BufferedImage img = loadAndScale(paths[index]);
        return (img != null) ? img : fallback;
    }
   
    //Handles input, movement, collisions, interactions, sounds, and guard overlap.
    public void update() {
    	
        int dx = 0; //Movement in x direction for this frame
        int dy = 0; //Movement in y direction for this frame
        
        // ---------- input ----------
        if (keyH.upPressed)    { dy = -1; direction = "up"; }
        if (keyH.downPressed)  { dy =  1; direction = "down"; }
        if (keyH.leftPressed)  { dx = -1; direction = "left"; }
        if (keyH.rightPressed) { dx =  1; direction = "right"; }

        // ---------- speed / stamina ----------
        if (keyH.sprintPressed && stamina > 0) {
            sprint();
            noiseValue = 3;
            noiseRadiusTiles = 6;
        } else if (keyH.crouchPressed) {
            crouch();
            noiseValue = 0;
            noiseRadiusTiles = 0;
        } else {
            walk();
            noiseValue = 1;
            noiseRadiusTiles = 2;
        }

        // ---------- Flashlight lighting ----------
        if (keyH.interactPressed && hasFlashlight && gp.ui.selectedItem instanceof Flashlight) {
            interact("torch");
        } else if (lastPlayerCol != -1) {
            //Turn off previously lit tiles when not holding interact
            for (int c = lastPlayerCol - 1; c <= lastPlayerCol + 1; c++) {
                for (int r = lastPlayerRow - 1; r <= lastPlayerRow + 1; r++) {
                    if (c >= 0 && c < gp.maxWorldCol && r >= 0 && r < gp.maxWorldRow) {
                        if (gp.tileM.mapTileNum[c][r] == 167) gp.tileM.mapTileNum[c][r] = 0;
                    }
                }
            }
            lastPlayerCol = -1;
            lastPlayerRow = -1;
        }
        
        // ---------- Throwing items ----------
        if (gp.keyH.throwJustPressed && gp.ui.selectedItem instanceof Throwable) {
            interactThrow((Throwable) gp.ui.selectedItem);
            gp.keyH.throwJustPressed = false; // consume the press
        }
        
        // ---------- Eating food ----------
        if (gp.keyH.interactPressed && gp.ui.selectedItem instanceof Food) {
            //Find index of selected food in inventory
            int foodIndex = -1;
            //Search inventory for the selected food object
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i) == gp.ui.selectedItem) {
                    foodIndex = i;
                    break;
                }
            }
            consumeItem(foodIndex);
            gp.keyH.interactPressed = false; // consume the press
        }
        
        // ---------- update inventory items ----------
        for (Item item : inventory) {
            item.update();
        }

        // ---------- clear interact prompt for this frame ----------
        gp.ui.hideInteract();

        boolean moved = false;

        // ---------- movement + collision checks ----------
        if (dx != 0 || dy != 0) {
            for (int step = 0; step < speed; step++) {
                //Try X
                if (dx != 0 && collisionOn) {
                    int tile = gp.cChecker.getCollidingTile(this, dx, 0);
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, dx, 0);
                    int taskIndex = gp.cChecker.checkTask(this, gp.tasks, dx, 0);
                    //Treat guards as non-blocking here (we'll check hitboxes separately)
                    if (tile == -1 && npcIndex == 999 && taskIndex == 999) {
                        worldX += dx;
                        col = worldX / gp.tileSize;
                        moved = true;
                    } else if (npcIndex != 999) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) interactNPC(npcIndex);
                        break; //Stop movement this frame
                    } else if (((tile == 211 || tile == 212) && (level == 1 || level == 3)) || ((tile == 204 || tile == 205) && level == 2)) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) interact("exit");
                        break;
                    } else if (taskIndex != 999) {
                        gp.ui.showInteract();
                        curTaskIndex = taskIndex;
                        curTaskName = gp.tasks[taskIndex].getName();
                        if (keyH.interactPressed) interactTask(curTaskName);
                        break;
                    } else {
                        break;
                    }
                } else if (!collisionOn) {
                    worldX += dx;
                    moved = true;
                }

                // try Y
                if (dy != 0 && collisionOn) {
                    int tile = gp.cChecker.getCollidingTile(this, 0, dy);
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, 0, dy);
                    int taskIndex = gp.cChecker.checkTask(this, gp.tasks, 0, dy);
                    if (tile == -1 && npcIndex == 999 && taskIndex == 999) {
                        worldY += dy;
                        row = worldY / gp.tileSize;
                        moved = true;
                    } else if (npcIndex != 999) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) interactNPC(npcIndex);
                        break;
                    } else if (
                    	    (level == 1 || level == 3) && (tile == 211 || tile == 212) ||
                    	    level == 2 && (tile == 204 || tile == 205) ||
                    	    level == 4 && (tile == 472 || tile == 473 || tile == 474 || tile == 475)
                    	) {
                    	    gp.ui.showInteract();
                    	    if (keyH.interactPressed) {
                    	        interact("exit");
                    	    }
                    	    break;
                    } else if (taskIndex != 999) {
                        gp.ui.showInteract();
                        curTaskIndex = taskIndex;
                        curTaskName = gp.tasks[taskIndex].getName();
                        if (keyH.interactPressed) interactTask(curTaskName);
                        break;
                    } else {
                        break;
                    }
                } else if (!collisionOn) {
                    worldY += dy;
                    moved = true;
                }
            }

            // picking up items + events
            int itemIndex = gp.cChecker.checkItem(this, true);
            pickUpItem(itemIndex);
            gp.eHandler.checkEvent();

            // animate
            spriteCounter++;
            if (spriteCounter > 19 - (1.5 * speed)) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            // ---------- standing checks ----------
            int checkX = 0, checkY = 0;
            switch (direction) {
                case "up":    checkY = -gp.tileSize; break;
                case "down":  checkY = gp.tileSize;  break;
                case "left":  checkX = -gp.tileSize; break;
                case "right": checkX = gp.tileSize;  break;
            }

            int collidedTile = gp.cChecker.getCollidingTile(this, checkX, checkY);
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc, checkX, checkY);
            int taskIndex = gp.cChecker.checkTask(this, gp.tasks, checkX, checkY);

            if (npcIndex != 999) {
                gp.ui.showInteract();
                if (keyH.interactPressed) interactNPC(npcIndex);
            } else if (collidedTile == 211 || collidedTile == 212) {
                gp.ui.showInteract();
                if (keyH.interactPressed) interact("exitVan");
            } else if (taskIndex != 999) {
                gp.ui.showInteract();
                curTaskIndex = taskIndex;
                curTaskName = gp.tasks[taskIndex].getName();
                if (keyH.interactPressed) interactTask(curTaskName);
            }

            // pick up items while standing
            int itemIndex = gp.cChecker.checkItem(this, true);
            if (itemIndex != 999) {
                pickUpItem(itemIndex);
            }
            
            standCounter++;
            if (standCounter >= 20) {
                spriteNum = 1;
                standCounter = 0;
            }
        }

        // ---------- sound trigger for guards ----------
        if (moved && noiseValue > 0) {
            long now = System.currentTimeMillis();
            if (now - lastFootstepTime >= footstepInterval) {
                gp.triggerSoundForGuards(worldX, worldY, noiseRadiusTiles);
                lastFootstepTime = now;
            }
        }

        // ---------- GUARD HITBOX CHECK (precise overlap only) ----------
        // build player hitbox at current world position (do NOT mutate solidArea)
        Rectangle playerBox = new Rectangle(
            worldX + solidArea.x,
            worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );

        for (int i = 0; i < gp.gaurds.length; i++) {
            if (gp.gaurds[i] == null) continue;
            // guard's current hitbox
            Rectangle guardBox = new Rectangle(
                gp.gaurds[i].worldX + gp.gaurds[i].solidArea.x,
                gp.gaurds[i].worldY + gp.gaurds[i].solidArea.y,
                gp.gaurds[i].solidArea.width,
                gp.gaurds[i].solidArea.height
            );
            if (playerBox.intersects(guardBox)) {
                // true overlap -> die
                interactGaurd();
                break;
            }
        }
    }


    // modular interact method - only handles "torch" here
    public void interact(String item) {
        if ("torch".equals(item)) {
	        // current player tile
	        int playerCol = worldX / gp.tileSize;
	        int playerRow = worldY / gp.tileSize;
	
	        // CHECK: only allow torch if the tile the player stands on is DARK (0)
	        int currentTileNum = gp.tileM.mapTileNum[playerCol][playerRow];
	        if (currentTileNum != 0) {
	            // Player on a regular (non-dark) tile: ensure any previous lit area is turned off
	            if (lastPlayerCol != -1) {
	                for (int col = lastPlayerCol - 1; col <= lastPlayerCol + 1; col++) {
	                    for (int row = lastPlayerRow - 1; row <= lastPlayerRow + 1; row++) {
	                        if (col >= 0 && col < gp.maxWorldCol &&
	                            row >= 0 && row < gp.maxWorldRow) {
	                            if (gp.tileM.mapTileNum[col][row] == 167) {
	                                gp.tileM.mapTileNum[col][row] = 0;
	                            }
	                        }
	                    }
	                }
	                lastPlayerCol = -1;
	                lastPlayerRow = -1;
	            }
	            return;
	        }
	
	        // only do work when player enters a new tile (prevents re-setting same tiles every frame)
	        if (playerCol == lastPlayerCol && playerRow == lastPlayerRow) {
	            return;
	        }
	
	        // TURN OFF old lit tiles (if any)
	        if (lastPlayerCol != -1) {
	            for (int col = lastPlayerCol - 1; col <= lastPlayerCol + 1; col++) {
	                for (int row = lastPlayerRow - 1; row <= lastPlayerRow + 1; row++) {
	                    if (col >= 0 && col < gp.maxWorldCol &&
	                        row >= 0 && row < gp.maxWorldRow) {
	                        if (gp.tileM.mapTileNum[col][row] == 167) {
	                            gp.tileM.mapTileNum[col][row] = 0;
	                        }
	                    }
	                }
	            }
	        }
	
	        // LIGHT new surrounding tiles (3x3). Only replace dark tiles (0) with lit (167)
	        for (int col = playerCol - 1; col <= playerCol + 1; col++) {
	            for (int row = playerRow - 1; row <= playerRow + 1; row++) {
	                if (col >= 0 && col < gp.maxWorldCol &&
	                    row >= 0 && row < gp.maxWorldRow) {
	                    if (gp.tileM.mapTileNum[col][row] == 0) {
	                        gp.tileM.mapTileNum[col][row] = 167;
	                    }
	                }
	            }
	        }
	
	        // save current tile so we can turn it off when they move or release E
	        lastPlayerCol = playerCol;
	        lastPlayerRow = playerRow;
	    }
        if ("exit".equals(item)) {
        	if (tasksComplete) {
        		gp.stopMusic();
    			gp.playSoundEffect(2);
    			gp.gameState = gp.dialogueState;
    			levelUp();
    			speak();
        	} else {
        		gp.ui.showBoxMessage("Tasks Incomplete!");
        	}
        	
		}

    }
    
    public void levelUp() {
        switch (level) {
            case 1 -> unlockSkin(5); // unlock BillyGoat skin
            case 2 -> unlockSkin(6);
            case 3 -> unlockSkin(2);
            case 4 -> unlockSkin(3);
        }
        
        // reset position/stats manually
        worldX = gp.tileSize * 6;
        worldY = gp.tileSize * 16;
        row = worldY / gp.tileSize;
        col = worldX / gp.tileSize;
        stamina = maxStamina;
        speed = walkSpeed;
        direction = "down";
        gp.ui.levelFinished = true;
		gp.level++;
		level = gp.level;
        
    }
    
    public void setDialogues() {
    	switch (gp.level) {
    	case 1:
			dialogues[0] = "Gaurd: Payload delivery to the warehouse \n Attendant: All clear, you may proceed.\n\n\nCompleted Level 1!\n You have unlocked the BillyGoat skin.";
			dialogues[1] = "Gaurd: Anyone seen my blue keys? \n Billy Goat: Idk man, jone's gonna be mad if he finds out though.\n\n\nCompleted Level 2!\n You have unlocked the Marv skin.";
			dialogues[2] = "Gaurd: It's too hot for this job man \n Marv: Whatever man, just drive.\n\n\nCompleted Level 3!\n You have unlocked the Old Timer skin.";
			dialogues[3] = "Jone: HEY GET BACK HERE! \nyou still have to serve 5 more decades of code commenting\n\n\nCompleted Level 4!\n You have unlocked the Froseph skin.";
			dialogues[4] = "YOUVE ESCAPED! GAME OVER.\n\n\n Credits: \nRafay, Christina, Sukhmanpreet, Jeevan, Samir";
			break;    		
    	}

	}
    
    public void speak() {
		super.speak();
    }
    
    public void interactNPC(int index) {
		if (index != 999) {
			gp.ui.currentDialogueSpeaker = gp.npc[index].name;
			gp.gameState = gp.dialogueState;
			gp.npc[index].speak();
			
		}
	}
    
    public void interactTask(String taskName) {
    	System.out.println("Interacting with task: " + taskName);
    	gp.gameState = gp.taskState;
    }
    
    
    public void interactGaurd() {
    	gp.eHandler.playerDied();
    }
    
    public void updateInventory() {
		for (Item item : inventory) {
			refreshItem(item.getName());
		}
	}
    
    public void refreshItem(String name) {

        switch (name) {

            case "Key":
                hasKey = true;
                TileManager.unlockTile(227);
                gp.playSoundEffect(1);
                break;

            case "Red Key":
                hasRedKey = true;
                TileManager.unlockTile(193);
                gp.playSoundEffect(1);
                break;

            case "Green Key":
                hasGreenKey = true;
                TileManager.unlockTile(219);
                gp.playSoundEffect(1);
                break;

            case "Blue Key":
                hasBlueKey = true;
                TileManager.unlockTile(204);
                TileManager.unlockTile(205);
                gp.playSoundEffect(1);
                break;

            case "Flashlight":
                hasFlashlight = true;
                gp.playSoundEffect(3);
                break;
                
            case "Pebble":
            	hasPebble = true;
            	gp.playSoundEffect(3);
            	break;
            	
            case "Can":
            	hasCan = true;
				gp.playSoundEffect(3);
				break;
            case "Tray":
            	hasTray = true;
            	gp.playSoundEffect(3);
            	break;
            case "Apple":
            	hasApple = true;
				gp.playSoundEffect(3);
				break;
			case "Bread":
				hasBread = true;
				gp.playSoundEffect(3);
				break;
			case "Protein Bar":
				hasProteinBar = true;	
				gp.playSoundEffect(3);
				break;
				
            default:
                return;
        }
    }

    public void pickUpItem(int index) {

        if (index == 999) return;
        Item item = gp.items[index];
        if (item == null) return;

        // block instant re-pickup after drop
        if (item.pickupDelay > 0) return;

        if (inventory.size() >= INVENTORY_SIZE) {
            gp.ui.showBoxMessage("Inventory full!");
            return;
        }

        switch (item.getName()) {

            case "Key":
                hasKey = true;
                TileManager.unlockTile(227);
                gp.playSoundEffect(1);
                break;

            case "Red Key":
                hasRedKey = true;
                TileManager.unlockTile(193);
                gp.playSoundEffect(1);
                break;

            case "Green Key":
                hasGreenKey = true;
                TileManager.unlockTile(219);
                gp.playSoundEffect(1);
                break;

            case "Blue Key":
                hasBlueKey = true;
                TileManager.unlockTile(204);
                TileManager.unlockTile(205);
                gp.playSoundEffect(1);
                break;

            case "Flashlight":
                hasFlashlight = true;
                gp.playSoundEffect(3);
                break;
                
            case "Pebble":
            	hasPebble = true;
            	gp.playSoundEffect(3);
            	break;
            	
            case "Can":
            	hasCan = true;
				gp.playSoundEffect(3);
				break;
            case "Tray":
            	hasTray = true;
            	gp.playSoundEffect(3);
            	break;
            case "Apple":
            	hasApple = true;
                gp.aSetter.startAppleRespawn(item.worldX, item.worldY);
				gp.playSoundEffect(3);
				break;
			case "Bread":
				hasBread = true;
				gp.aSetter.startBreadRespawn(item.worldX, item.worldY);
				gp.playSoundEffect(3);
				break;
			case "Protein Bar":
				hasProteinBar = true;	
				gp.playSoundEffect(3);
				break;
				
            default:
                return;
        }

        // ✅ MOVE the SAME object into inventory
        inventory.add(item);

        // ✅ REMOVE from world
        gp.items[index] = null;
    }
    
    public void interactThrow(Throwable throwable) {
        // block if still cooling down
        if (throwable.throwDelay > 0) return;
        
        if (gp.ui.showThrowRadius && gp.ui.activeThrowable == throwable) {
            // Toggle OFF if clicking same throwable again
            gp.ui.showThrowRadius = false;
            gp.ui.activeThrowable = null;

            gp.selectedThrowCol = -1;
            gp.selectedThrowRow = -1;
        } else {
            // Toggle ON
            gp.ui.activeThrowable = throwable;
            gp.ui.showThrowRadius = true;
            throwable.throwDelay = 60; // still useful for cooldown
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
    
    public void consumeItem(int index) {
		if (index < 0 || index >= inventory.size()) return;
		Item item = inventory.get(index);
		if (item == null) return;

		String name = item.getName();
		if (name == null) return;

		switch (name) {
			case "Apple":
				hasApple = false;
				gp.playSoundEffect(5); 
				break;
			case "Bread":
				hasBread = false;
				gp.playSoundEffect(5); // eating sound
				break;
			case "Protein Bar":
				hasProteinBar = false;
				gp.playSoundEffect(5); // eating sound
				break;
			default:
				return; // not consumable
		}
		if (stamina >= maxStamina) {
			gp.ui.showBoxMessage("Stamina full!");
			return;
		}
		stamina += maxStamina * (((Food) item).getRestoreValue());

		// remove from inventory
		inventory.remove(index);
		gp.ui.selectedItem = null;
	}

    public void dropItem(int index) {

        System.out.println("Attempting to drop item at index: " + index);

        if (inventory.isEmpty()) return;
        if (index < 0 || index >= inventory.size()) return;

        Item original = inventory.get(index);

        // ---------- UPDATE PLAYER FLAGS FIRST ----------
        if (original instanceof Flashlight) {
            hasFlashlight = false;

            // turn off lit tiles
            if (lastPlayerCol != -1) {
                for (int col = lastPlayerCol - 1; col <= lastPlayerCol + 1; col++) {
                    for (int row = lastPlayerRow - 1; row <= lastPlayerRow + 1; row++) {
                        if (col >= 0 && col < gp.maxWorldCol &&
                            row >= 0 && row < gp.maxWorldRow) {
                            if (gp.tileM.mapTileNum[col][row] == 167) {
                                gp.tileM.mapTileNum[col][row] = 0;
                            }
                        }
                    }
                }
                lastPlayerCol = -1;
                lastPlayerRow = -1;
            }
        }

        if (original instanceof Key) {
            hasKey = false;
            TileManager.lockTile(227);
        }
        if (original instanceof redKey) {
            hasRedKey = false;
            TileManager.lockTile(193);
        }
        if (original instanceof greenKey) {
            hasGreenKey = false;
            TileManager.lockTile(219);
        }
        if (original instanceof blueKey) {
            hasBlueKey = false;
            TileManager.lockTile(204);
            TileManager.lockTile(205);
        }
        
        if (original instanceof Pebble) {
			hasPebble = false;
		}
        
        if (original instanceof Can) {
			hasCan = false;
		}
        
        if (original instanceof Tray) {
        	hasTray = false;
        }
        
        if (original instanceof Throwable) {
        	 gp.ui.showThrowRadius = false;
             gp.ui.activeThrowable = null;
             gp.selectedThrowCol = -1;
             gp.selectedThrowRow = -1;
		}

        Item toDrop = original;

     // position
     toDrop.worldX = worldX;
     toDrop.worldY = worldY;

     // prevent instant pickup
     toDrop.pickupDelay = 60;

     // place in world
     gp.aSetter.placeItem(toDrop, worldX, worldY);
     gp.ui.selectedItem = null;
     gp.ui.slotRow = -1;

     // remove from inventory
     inventory.remove(index);

     gp.playSoundEffect(6);
    }
    
    public Rectangle getHitbox() { // Returns the entity's collision box positioned in world coordinates
        return new Rectangle( // Create and return a new Rectangle instance (safe copy for callers)
            worldX + solidArea.x, // X position: entity world X plus local solidArea X offset
            worldY + solidArea.y, // Y position: entity world Y plus local solidArea Y offset
            solidArea.width,      // Width: use the configured solidArea width
            solidArea.height      // Height: use the configured solidArea height
        ); // End Rectangle construction
    } // End getHitbox



    public void draw(java.awt.Graphics2D g2) {

        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : right2;
                break;
        }

        g2.drawImage(image, screenX, screenY, null);
        
        gp.uTool.showPlayerPosition(g2, worldX, worldY, row, col);
    }
}