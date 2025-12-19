package main;

import entity.entity;
import java.awt.Rectangle;

public class CollisionChecker {
    gamePanel gp;

    public CollisionChecker(gamePanel gp) {
        this.gp = gp;
    }

    /**
     * Pure collision check. Returns true if moving the entity by (dx, dy)
     * would collide with a solid tile (or world bounds).
     *
     * dx, dy are in WORLD PIXELS (for our usage they will be -1, 0 or 1 per step).
     */
    public boolean willCollide(entity entity, int dx, int dy) {

        // guard: if no solidArea defined, assume no collision
        if (entity.solidArea == null) return false;

        // compute hitbox in world coords after the proposed move
        int leftX   = entity.worldX + entity.solidArea.x + dx;
        int rightX  = leftX + entity.solidArea.width - 1;
        int topY    = entity.worldY + entity.solidArea.y + dy;
        int bottomY = topY + entity.solidArea.height - 1;

        // tile indices
        int leftCol   = leftX / gp.tileSize;
        int rightCol  = rightX / gp.tileSize;
        int topRow    = topY / gp.tileSize;
        int bottomRow = bottomY / gp.tileSize;

        // if any of those indices are outside the world, treat as collision
        if (leftCol < 0 || rightCol >= gp.maxWorldCol || topRow < 0 || bottomRow >= gp.maxWorldRow) {
            return true;
        }

        // Check every tile the hitbox would overlap (covers wide/thin hitboxes)
        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];

                // guard: if tile array not set or tile is null -> treat as non-solid
                if (gp.tileM.tile == null) continue;
                if (tileNum < 0 || tileNum >= gp.tileM.tile.length) continue;

                tile.tile t = gp.tileM.tile[tileNum];
                if (t != null && t.collision) {
                    return true;
                }
            }
        }

        // no solid tile overlapped
        return false;
    }
    
    public int checkItem(entity entity, boolean player) {
		int index = 999;
		
		for (int i = 0; i < gp.items.length; i++) {
			if (gp.items[i] != null) {
				// Get entity's solid area position
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				// Get item's solid area position
				gp.items[i].solidArea.x = gp.items[i].worldX + gp.items[i].solidArea.x;
				gp.items[i].solidArea.y = gp.items[i].worldY + gp.items[i].solidArea.y;
				
				switch(entity.direction) {
					case "up":
						entity.solidArea.y -= entity.speed;
						if (entity.solidArea.intersects(gp.items[i].solidArea)) {
							if (gp.items[i].collision == true) {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						if (entity.solidArea.intersects(gp.items[i].solidArea)) {
							if (gp.items[i].collision == true) {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						if (entity.solidArea.intersects(gp.items[i].solidArea)) {
							if (gp.items[i].collision == true) {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						if (entity.solidArea.intersects(gp.items[i].solidArea)) {
							if (gp.items[i].collision == true) {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
				
				}
				
				// Reset solid area position
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				gp.items[i].solidArea.x =gp.items[i].solidAreaDefaultX;
				gp.items[i].solidArea.y = gp.items[i].solidAreaDefaultY;
			}
		}
		
		return index;
	}
}
