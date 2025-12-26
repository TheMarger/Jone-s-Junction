package entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import main.UtilityTool;
import main.gamePanel;
import main.keyHandler;
import tile.TileManager;

public class player extends entity {

    keyHandler keyH;
    int speed;

    private final int screenX;
    private final int screenY;
    int row;
    int col;

    public static boolean hasKey;
    public static boolean hasBlueKey;
    public static boolean hasRedKey;
    public static boolean hasGreenKey;
    int standCounter = 0;
    // torch ownership + last lit tile position
    public static boolean hasTorch = false;          // set true when player picks up a Torch item
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
        
        isAlive = true;

        setDefaultValues();
        getPlayerImage();
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
        hasTorch = false;
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

        // INTERACT KEY -> only while holding E AND player owns a torch
        if (keyH.interactPressed && hasTorch) {
            interact("torch");
        } else {
            // if interact released or player doesn't have torch, turn off old lit tiles (if any)
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

        // Always clear the interact prompt at the start of the frame;
        // we'll show it again if we detect something to interact with.
        gp.ui.hideInteract();

        // MOVEMENT
        if (dx != 0 || dy != 0) {

            for (int step = 0; step < speed; step++) {

                // X movement
                if (dx != 0) {
                    int collidedTile = gp.cChecker.getCollidingTile(this, dx, 0);
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, dx, 0);
                    int gaurdIndex = gp.cChecker.checkEntity(this, gp.gaurds, dx, 0);

                    if (collidedTile == -1 && npcIndex == 999 && gaurdIndex == 999) {
                        worldX += dx;
                        col = worldX / gp.tileSize;
                    }
                    else if (npcIndex != 999) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) {
                            interactNPC(npcIndex);
                        }
                        break; // stop further movement this frame
                    }
                    else if (collidedTile == 212 || collidedTile == 211) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) {
                            interact("exitVan");
                        }
                        break;
                    }
                    else if (gaurdIndex != 999) {
                        interactGaurd();
                        break;
                    }
                }

                // Y movement
                if (dy != 0) {
                    int collidedTile = gp.cChecker.getCollidingTile(this, 0, dy);
                    int npcIndex = gp.cChecker.checkEntity(this, gp.npc, 0, dy);
                    int gaurdIndex = gp.cChecker.checkEntity(this, gp.gaurds, 0, dy);

                    if (collidedTile == -1 && npcIndex == 999 && gaurdIndex == 999) {
                        worldY += dy;
                        row = worldY / gp.tileSize;
                    }
                    else if (npcIndex != 999) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) {
                            interactNPC(npcIndex);
                        }
                        break;
                    }
                    else if (collidedTile == 212 || collidedTile == 211) {
                        gp.ui.showInteract();
                        if (keyH.interactPressed) {
                            interact("exitVan");
                        }
                        break;
                    }
                    else if (gaurdIndex != 999) {
                        interactGaurd();
                        break;
                    }
                }
            }

            int itemIndex = gp.cChecker.checkItem(this, true);
            pickUpItem(itemIndex);

            // Check events
            gp.eHandler.checkEvent();

            spriteCounter++;
            if (spriteCounter > 19 - (1.5 * speed)) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            // --- STATIONARY: check one tile in facing direction for NPCs / guards / exit tile ---
            int checkX = 0;
            int checkY = 0;

            switch (direction) {
                case "up":    checkY = -gp.tileSize; break;
                case "down":  checkY = gp.tileSize;  break;
                case "left":  checkX = -gp.tileSize; break;
                case "right": checkX = gp.tileSize;  break;
            }

            int collidedTile = gp.cChecker.getCollidingTile(this, checkX, checkY);
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc, checkX, checkY);
            int gaurdIndex = gp.cChecker.checkEntity(this, gp.gaurds, checkX, checkY);

            if (npcIndex != 999) {
                gp.ui.showInteract();
                if (keyH.interactPressed) {
                    interactNPC(npcIndex);
                }
            } else if (collidedTile == 212 || collidedTile == 211) {
                gp.ui.showInteract();
                if (keyH.interactPressed) {
                    interact("exitVan");
                }
            } else if (gaurdIndex != 999) {
                // Optionally show an interact prompt for guards or directly handle.
                gp.ui.showInteract();
                if (keyH.interactPressed) {
                    interactGaurd();
                }
            }

            // allow picking up items while standing (optional; mirrors movement behavior)
            int itemIndex = gp.cChecker.checkItem(this, true);
            pickUpItem(itemIndex);

            standCounter++;
            if (standCounter == 20) {
                spriteNum = 1;
                standCounter = 0;
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

        if (index != 999) {

            String itemName = gp.items[index].getName();

            if (itemName.equals("Key")) {
            	gp.playSoundEffect(1);
                gp.items[index] = null;
                hasKey = true;
                TileManager.unlockTile(227);            
            }

            if (itemName.equals("Red Key")) {
            	gp.playSoundEffect(1);
                gp.items[index] = null;
                hasRedKey = true;
                TileManager.unlockTile(193);
            }

            if (itemName.equals("Green Key")) {
            	gp.playSoundEffect(1);
                gp.items[index] = null;
                hasGreenKey = true;
                TileManager.unlockTile(219);
            }
            
            if (itemName.equals("Blue Key")) {
				gp.playSoundEffect(1);
				gp.items[index] = null;
				hasBlueKey = true;
				TileManager.unlockTile(204);
				TileManager.unlockTile(205);
			}

            if (itemName.equals("Torch")) {
            	gp.playSoundEffect(3);
                gp.items[index] = null;
                hasTorch = true;   // player now owns a torch (must hold E to light)
            }
        }
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
