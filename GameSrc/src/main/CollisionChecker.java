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
    /**
     * Returns the tile index of the first solid tile the entity would collide with
     * if it moves by (dx, dy), or -1 if no collision.
     * -2 if the movement goes outside the world bounds.
     */
    public int getCollidingTile(entity entity, int dx, int dy) {

        if (entity.solidArea == null) return -1;

        int leftX   = entity.worldX + entity.solidArea.x + dx;
        int rightX  = leftX + entity.solidArea.width - 1;
        int topY    = entity.worldY + entity.solidArea.y + dy;
        int bottomY = topY + entity.solidArea.height - 1;

        int leftCol   = leftX / gp.tileSize;
        int rightCol  = rightX / gp.tileSize;
        int topRow    = topY / gp.tileSize;
        int bottomRow = bottomY / gp.tileSize;

        // world bounds check
        if (leftCol < 0 || rightCol >= gp.maxWorldCol || topRow < 0 || bottomRow >= gp.maxWorldRow) {
            return -2; // out of bounds
        }

        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];

                if (gp.tileM.tile == null) continue;
                if (tileNum < 0 || tileNum >= gp.tileM.tile.length) continue;

                tile.tile t = gp.tileM.tile[tileNum];
                if (t != null && t.collision) {
                    return tileNum; // return the index of the tile collided with
                }
            }
        }

        return -1; // no collision
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
    
 // Check collision between an entity and a list of target entities
    public int checkEntity(entity entity, entity[] target, int dx, int dy) {

        if (entity.solidArea == null) return 999;

        // Future hitbox of the moving entity
        Rectangle futureArea = new Rectangle(
            entity.worldX + entity.solidArea.x + dx,
            entity.worldY + entity.solidArea.y + dy,
            entity.solidArea.width,
            entity.solidArea.height
        );

        for (int i = 0; i < target.length; i++) {
            if (target[i] == null || target[i].solidArea == null || target[i] == entity) continue;

            // Target entity hitbox
            Rectangle targetArea = new Rectangle(
                target[i].worldX + target[i].solidArea.x,
                target[i].worldY + target[i].solidArea.y,
                target[i].solidArea.width,
                target[i].solidArea.height
            );

            if (futureArea.intersects(targetArea)) {
                return i; // collided with this entity
            }
        }

        return 999; // no collision
    }
    
    public boolean checkPlayer(entity entity, int dx, int dy) {

        if (entity.solidArea == null) return false;

        // Future hitbox of entity
        Rectangle futureArea = new Rectangle(
            entity.worldX + entity.solidArea.x + dx,
            entity.worldY + entity.solidArea.y + dy,
            entity.solidArea.width,
            entity.solidArea.height
        );

        // Player hitbox
        Rectangle playerArea = new Rectangle(
            gp.player.worldX + gp.player.solidArea.x,
            gp.player.worldY + gp.player.solidArea.y,
            gp.player.solidArea.width,
            gp.player.solidArea.height
        );

        return futureArea.intersects(playerArea);
    }


    
}
