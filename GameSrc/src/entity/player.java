package entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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

    keyHandler keyH;

    private final int screenX;
    private final int screenY;
    int row;
    int col;

    public ArrayList<Item> inventory = new ArrayList<>();
	public ArrayList<Task> tasksList = new ArrayList<>(); 
    public final int INVENTORY_SIZE = 3;
    public String equppedSkin;
 // basic character/skin storage used by the title character screen
    public boolean[] unlockedSkins;
    public int equippedSkinIndex = 0;
    public int currentSkinIndex = 0;
    public String equippedSkin;
    public int curTaskIndex;
    public String curTaskName;

 	
    public boolean hasKey;
    public boolean hasBlueKey;
    public boolean hasRedKey;
    public boolean hasGreenKey;
    public boolean hasPebble;
    int standCounter = 0;
    // torch ownership + last lit tile position
    public static boolean hasFlashlight = false;          // set true when player picks up a Torch item
    int lastPlayerCol = -1;            // column of the tile that was last used to light
    int lastPlayerRow = -1;            // row of the tile that was last used to light
    
    public boolean isAlive;

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public player(gamePanel gp, keyHandler keyH) {
    	super(gp);
    	
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = gp.tileSize / 3;
        solidArea.y = gp.tileSize / 3;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 22;
        solidArea.height = 22;
        maxStamina = 100f;
        stamina = maxStamina;
        staminaRegen = 5f;       // e.g. 5 stamina per second
        sprintStaminaCost = 20f; // 10 stamina per second while sprinting
        equippedSkinIndex = gp.equippedSkinIndex;
        equippedSkin = gp.equippedSkin;

        
        isAlive = true;

        setDefaultValues();
        getPlayerImage();
        setItems();
    }
    
    public void setItems() {
    	//inventory.add(new Flashlight(gp));
    }

    
    public void clearInventory() {
    	inventory.clear();
    			
    }
    
    public void equipSkin(int index) {
        if (index < 0 || index >= gp.skins.length) return;
        if (!unlockedSkins[index]) return;

        gp.equippedSkinIndex = index;
        gp.equippedSkin = gp.skins[index][0][0]; // optional, just store name

        equippedSkinIndex = index;
        equippedSkin = gp.equippedSkin;

        getPlayerImage();
    }


    public void unlockSkin(int index) {
        if (index < 0 || index >= unlockedSkins.length) return;
        unlockedSkins[index] = true;
    }


    
    public void addItem(Item item) {
		if (inventory.size() < INVENTORY_SIZE) {
			inventory.add(item);
		}
	}
    
    public void removeItem(int index) {
		if (index >= 0 && index < inventory.size()) {
			inventory.remove(index);
		}
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 6;
        worldY = gp.tileSize * 16;
        row = worldY / gp.tileSize;
        col = worldX / gp.tileSize;

        // unlock skins
        unlockedSkins = new boolean[gp.skins.length];
        for (int i = 0; i < gp.skins.length; i++) {
            unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked");
        }

        // instead of resetting to 0, use the skin stored in gamePanel
        equippedSkinIndex = gp.equippedSkinIndex;
        equippedSkin = gp.equippedSkin;

        walkSpeed = 4;
        sprintSpeed = 16;
        crouchSpeed = 2;
        speed = walkSpeed;
        direction = "down";
        level = gp.level;

        getPlayerImage(); // load images for current skin
    }




    public void getPlayerImage() {
        if (gp == null || gp.skins == null) return;

        String[] paths = gp.skins[equippedSkinIndex][2]; // paths for all frames

        try {
            // Up frames
            up1 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[1])), gp.tileSize, gp.tileSize);
            up2 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[2])), gp.tileSize, gp.tileSize);

            // Down frames
            down1 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[3])), gp.tileSize, gp.tileSize);
            down2 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[4])), gp.tileSize, gp.tileSize);

            // right frames
            right1 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[5])), gp.tileSize, gp.tileSize);
            right2 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[6])), gp.tileSize, gp.tileSize);

            // left frames
            left1 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[7])), gp.tileSize, gp.tileSize);
            left2 = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream(paths[8])), gp.tileSize, gp.tileSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
  
    public void update() {
        int dx = 0;
        int dy = 0;
        
        // ---------- input ----------
        if (keyH.upPressed)    { dy = -1; direction = "up"; }
        if (keyH.downPressed)  { dy =  1; direction = "down"; }
        if (keyH.leftPressed)  { dx = -1; direction = "left"; }
        if (keyH.rightPressed) { dx =  1; direction = "right"; }

        // ---------- speed / stamina ----------
        if (keyH.sprintPressed && stamina > 0) sprint();
        else if (keyH.crouchPressed) crouch();
        else walk();

        // ---------- torch (hold-to-light) ----------
        if (keyH.interactPressed && hasFlashlight && gp.ui.selectedItem instanceof Flashlight) {
            interact("torch");
        } else if (lastPlayerCol != -1) {
            // turn off previously lit tiles when not holding interact
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
        if (gp.keyH.throwJustPressed && gp.ui.selectedItem.getName().equals("Pebble") && hasPebble) {
            interactThrow((Throwable) gp.ui.selectedItem);
            gp.keyH.throwJustPressed = false; // consume the press
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
                // try X
                if (dx != 0) {
                    int tile = gp.cChecker.getCollidingTile(this, dx, 0);
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, dx, 0);
                    int taskIndex = gp.cChecker.checkTask(this, gp.tasks, dx, 0);
                    // treat guards as non-blocking here (we'll check hitboxes separately)
                    if (tile == -1 && npcIndex == 999 && taskIndex == 999) {
                        worldX += dx;
                        col = worldX / gp.tileSize;
                        moved = true;
                    } else if (npcIndex != 999) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) interactNPC(npcIndex);
                        break; // stop movement this frame
                    } else if (tile == 211 || tile == 212) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) interact("exitVan");
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
                }

                // try Y
                if (dy != 0) {
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
                    } else if (tile == 211 || tile == 212) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) interact("exitVan");
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

        // ---------- GUARd HITBOX CHECK (precise overlap only) ----------
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
        if ("exitVan".equals(item)) {
			gp.ui.levelFinished = true;
			gp.stopMusic();
			gp.playSoundEffect(2);
			gp.level++;
			gp.resetGame(false);
		}

    }
    
    public void interactNPC(int index) {
		if (index != 999) {
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

        // DEBUG HITBOX
        g2.setColor(new Color(0, 255, 0, 120));
        g2.fillRect(
            screenX + solidArea.x,
            screenY + solidArea.y,
            solidArea.width,
            solidArea.height
        );

        g2.setColor(Color.GREEN);
        g2.drawRect(
            screenX + solidArea.x,
            screenY + solidArea.y,
            solidArea.width,
            solidArea.height
        );
        
        gp.uTool.showPlayerPosition(g2, worldX, worldY, row, col);
    }
}
