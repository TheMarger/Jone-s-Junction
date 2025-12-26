package entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Item.*;
import main.UtilityTool;
import main.gamePanel;
import main.keyHandler;
import tile.TileManager;

public class player extends entity {

    keyHandler keyH;

    private final int screenX;
    private final int screenY;
    int row;
    int col;

    public ArrayList<Item> inventory = new ArrayList<>();
    public final int INVENTORY_SIZE = 3;

    public static boolean hasKey;
    public static boolean hasBlueKey;
    public static boolean hasRedKey;
    public static boolean hasGreenKey;
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
        solidArea.x = 14;
        solidArea.y = 18;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 18;
        solidArea.height = 18;
        maxStamina = 100f;
        stamina = maxStamina;
        staminaRegen = 5f;       // e.g. 5 stamina per second
        sprintStaminaCost = 20f; // 10 stamina per second while sprinting
        
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
        walkSpeed = 4;
        sprintSpeed = 16;
        crouchSpeed = 2;
        speed = walkSpeed;
        direction = "down";
        hasKey = false;
        hasBlueKey = false;
        hasRedKey = false;
        hasGreenKey = false;
        hasFlashlight = false;
    }

    public void getPlayerImage() {
        up1 = setup("/Rabbit/boy_up_1");
        up2 = setup("/Rabbit/boy_up_2");
        down1 = setup("/Rabbit/boy_down_1");
        down2 = setup("/Rabbit/boy_down_2");
        left1 = setup("/Rabbit/boy_left_1");
        left2 = setup("/Rabbit/boy_left_2");
        right1 = setup("/Rabbit/boy_right_1");
        right2 = setup("/Rabbit/boy_right_2");
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
        if (keyH.interactPressed && hasFlashlight && gp.ui.selectedItem.equals("Flashlight")) {
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
                    // treat guards as non-blocking here (we'll check hitboxes separately)
                    if (tile == -1 && npcIndex == 999) {
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
                    } else {
                        break;
                    }
                }

                // try Y
                if (dy != 0) {
                    int tile = gp.cChecker.getCollidingTile(this, 0, dy);
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, 0, dy);
                    if (tile == -1 && npcIndex == 999) {
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

            if (npcIndex != 999) {
                gp.ui.showInteract();
                if (keyH.interactPressed) interactNPC(npcIndex);
            } else if (collidedTile == 211 || collidedTile == 212) {
                gp.ui.showInteract();
                if (keyH.interactPressed) interact("exitVan");
            }

            // pick up items while standing
            int itemIndex = gp.cChecker.checkItem(this, true);
            pickUpItem(itemIndex);

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
		}

    }
    
    public void interactNPC(int index) {
		if (index != 999) {
			gp.gameState = gp.dialogueState;
			gp.npc[index].speak();
		}
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

            default:
                return;
        }

        // ✅ MOVE the SAME object into inventory
        inventory.add(item);

        // ✅ REMOVE from world
        gp.items[index] = null;
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
     gp.ui.selectedItem = "";
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
