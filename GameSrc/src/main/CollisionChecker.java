/*
 * Name: Rafay
 * Date: 1/19/2026
 * Course Code: ICS4U0
 * Description: CollisionChecker class handles all collision detection in the game including
 *              tile collisions, entity-to-entity collisions, item pickups, task interactions,
 *              and player detection. It uses rectangle-based hitbox intersection to determine
 *              if entities would collide when moving by specified offsets, and provides
 *              methods to check collisions with world boundaries, solid tiles, items, NPCs,
 *              guards, and tasks.
 */

package main; // Declares this class belongs to the main package

import entity.entity; // Imports the base entity class
import task.Task; // Imports the Task class

import java.awt.Rectangle; // Imports Rectangle class for hitbox calculations

public class CollisionChecker { // Declares the public CollisionChecker class
    gamePanel gp; // Reference to the main gamePanel object

    public CollisionChecker(gamePanel gp) { // Constructor that takes a gamePanel parameter
        this.gp = gp; // Assigns the gamePanel parameter to the class's gp field
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
    public int getCollidingTile(entity entity, int dx, int dy) { // Method to check if entity would collide with a tile when moving by dx and dy

        if (entity.solidArea == null) return -1; // Returns -1 if entity has no collision hitbox defined

        int leftX   = entity.worldX + entity.solidArea.x + dx; // Calculates the leftmost X coordinate of entity's hitbox after movement
        int rightX  = leftX + entity.solidArea.width - 1; // Calculates the rightmost X coordinate of entity's hitbox after movement
        int topY    = entity.worldY + entity.solidArea.y + dy; // Calculates the topmost Y coordinate of entity's hitbox after movement
        int bottomY = topY + entity.solidArea.height - 1; // Calculates the bottommost Y coordinate of entity's hitbox after movement

        int leftCol   = leftX / gp.tileSize; // Converts left X coordinate to tile column index
        int rightCol  = rightX / gp.tileSize; // Converts right X coordinate to tile column index
        int topRow    = topY / gp.tileSize; // Converts top Y coordinate to tile row index
        int bottomRow = bottomY / gp.tileSize; // Converts bottom Y coordinate to tile row index

        // world bounds check
        if (leftCol < 0 || rightCol >= gp.maxWorldCol || topRow < 0 || bottomRow >= gp.maxWorldRow) { // Checks if entity would move outside the world boundaries
            return -2; // Returns -2 indicating out of bounds collision
        }

        for (int col = leftCol; col <= rightCol; col++) { // Loops through all tile columns the entity would occupy
            for (int row = topRow; row <= bottomRow; row++) { // Loops through all tile rows the entity would occupy
                int tileNum = gp.tileM.mapTileNum[col][row]; // Gets the tile type at this position from the map

                if (gp.tileM.tile == null) continue; // Skips if tile array is not initialized
                if (tileNum < 0 || tileNum >= gp.tileM.tile.length) continue; // Skips if tile number is invalid

                tile.tile t = gp.tileM.tile[tileNum]; // Gets the tile object for this tile type
                if (t != null && t.collision) { // Checks if tile exists and has collision enabled
                    return tileNum; // Returns the index of the colliding tile
                }
            }
        }

        return -1; // Returns -1 indicating no collision detected
    }

    
    public int checkItem(entity entity, boolean player) { // Method to check collision between an entity and items in the world
		int index = 999; // Initializes index to 999 (indicates no item collision)
		
		for (int i = 0; i < gp.items.length; i++) { // Loops through all items in the game world
			if (gp.items[i] != null) { // Checks if an item exists at this index
				// Get entity's solid area position
				entity.solidArea.x = entity.worldX + entity.solidArea.x; // Calculates entity's absolute hitbox X position in world
				entity.solidArea.y = entity.worldY + entity.solidArea.y; // Calculates entity's absolute hitbox Y position in world
				// Get item's solid area position
				gp.items[i].solidArea.x = gp.items[i].worldX + gp.items[i].solidArea.x; // Calculates item's absolute hitbox X position in world
				gp.items[i].solidArea.y = gp.items[i].worldY + gp.items[i].solidArea.y; // Calculates item's absolute hitbox Y position in world
				
				switch(entity.direction) { // Switches based on entity's movement direction
					case "up": // If entity is moving up
						entity.solidArea.y -= entity.speed; // Moves entity's hitbox upward by its speed
						if (entity.solidArea.intersects(gp.items[i].solidArea)) { // Checks if entity's hitbox intersects with item's hitbox
							if (gp.items[i].collision == true) { // Checks if item has collision enabled
								entity.collisionOn = true; // Sets entity's collision flag to true
							}
							if (player == true) { // Checks if the entity is the player
								index = i; // Stores the index of the colliding item
							}
						}
						break; // Exits the switch statement
					case "down": // If entity is moving down
						entity.solidArea.y += entity.speed; // Moves entity's hitbox downward by its speed
						if (entity.solidArea.intersects(gp.items[i].solidArea)) { // Checks if entity's hitbox intersects with item's hitbox
							if (gp.items[i].collision == true) { // Checks if item has collision enabled
								entity.collisionOn = true; // Sets entity's collision flag to true
							}
							if (player == true) { // Checks if the entity is the player
								index = i; // Stores the index of the colliding item
							}
						}
						break; // Exits the switch statement
					case "left": // If entity is moving left
						entity.solidArea.x -= entity.speed; // Moves entity's hitbox left by its speed
						if (entity.solidArea.intersects(gp.items[i].solidArea)) { // Checks if entity's hitbox intersects with item's hitbox
							if (gp.items[i].collision == true) { // Checks if item has collision enabled
								entity.collisionOn = true; // Sets entity's collision flag to true
							}
							if (player == true) { // Checks if the entity is the player
								index = i; // Stores the index of the colliding item
							}
						}
						break; // Exits the switch statement
					case "right": // If entity is moving right
						entity.solidArea.x += entity.speed; // Moves entity's hitbox right by its speed
						if (entity.solidArea.intersects(gp.items[i].solidArea)) { // Checks if entity's hitbox intersects with item's hitbox
							if (gp.items[i].collision == true) { // Checks if item has collision enabled
								entity.collisionOn = true; // Sets entity's collision flag to true
							}
							if (player == true) { // Checks if the entity is the player
								index = i; // Stores the index of the colliding item
							}
						}
						break; // Exits the switch statement
				
				}
				
				// Reset solid area position
				entity.solidArea.x = entity.solidAreaDefaultX; // Resets entity's hitbox X to its default offset
				entity.solidArea.y = entity.solidAreaDefaultY; // Resets entity's hitbox Y to its default offset
				gp.items[i].solidArea.x =gp.items[i].solidAreaDefaultX; // Resets item's hitbox X to its default offset
				gp.items[i].solidArea.y = gp.items[i].solidAreaDefaultY; // Resets item's hitbox Y to its default offset
			}
		}
		
		return index; // Returns the index of the colliding item or 999 if no collision
	}
    
    public int checkTask(entity entity, Task[] target, int dx, int dy) { // Method to check collision between an entity and tasks

		if (entity.solidArea == null) return 999; // Returns 999 if entity has no collision hitbox defined

		// Future hitbox of the moving entity
		Rectangle futureArea = new Rectangle( // Creates a Rectangle representing entity's hitbox after movement
			entity.worldX + entity.solidArea.x + dx, // X position of hitbox after moving by dx
			entity.worldY + entity.solidArea.y + dy, // Y position of hitbox after moving by dy
			entity.solidArea.width, // Width of the hitbox
			entity.solidArea.height // Height of the hitbox
		);

		for (int i = 0; i < target.length; i++) { // Loops through all tasks in the target array
			if (target[i] == null || target[i].solidArea == null) continue; // Skips if task doesn't exist or has no hitbox

			// Target task hitbox
			Rectangle targetArea = new Rectangle( // Creates a Rectangle representing the task's hitbox
				target[i].worldX + target[i].solidArea.x, // X position of task's hitbox
				target[i].worldY + target[i].solidArea.y, // Y position of task's hitbox
				target[i].solidArea.width, // Width of task's hitbox
				target[i].solidArea.height // Height of task's hitbox
			);

			if (futureArea.intersects(targetArea)) { // Checks if entity's future hitbox intersects with task's hitbox
				return i; // Returns the index of the colliding task
			}
		}

		return 999; // Returns 999 indicating no collision detected
		
    }
    
 // Check collision between an entity and a list of target entities
    public int checkEntity(entity entity, entity[] target, int dx, int dy) { // Method to check collision between an entity and other entities

        if (entity.solidArea == null) return 999; // Returns 999 if entity has no collision hitbox defined

        // Future hitbox of the moving entity
        Rectangle futureArea = new Rectangle( // Creates a Rectangle representing entity's hitbox after movement
            entity.worldX + entity.solidArea.x + dx, // X position of hitbox after moving by dx
            entity.worldY + entity.solidArea.y + dy, // Y position of hitbox after moving by dy
            entity.solidArea.width, // Width of the hitbox
            entity.solidArea.height // Height of the hitbox
        );

        for (int i = 0; i < target.length; i++) { // Loops through all entities in the target array
            if (target[i] == null || target[i].solidArea == null || target[i] == entity) continue; // Skips if target doesn't exist, has no hitbox, or is the same entity

            // Target entity hitbox
            Rectangle targetArea = new Rectangle( // Creates a Rectangle representing the target entity's hitbox
                target[i].worldX + target[i].solidArea.x, // X position of target's hitbox
                target[i].worldY + target[i].solidArea.y, // Y position of target's hitbox
                target[i].solidArea.width, // Width of target's hitbox
                target[i].solidArea.height // Height of target's hitbox
            );

            if (futureArea.intersects(targetArea)) { // Checks if entity's future hitbox intersects with target's hitbox
                return i; // Returns the index of the colliding entity
            }
        }

        return 999; // Returns 999 indicating no collision detected
    }
    
    public boolean checkPlayer(entity entity, int dx, int dy) { // Method to check if an entity would collide with the player

        if (entity.solidArea == null) return false; // Returns false if entity has no collision hitbox defined

        // Future hitbox of entity
        Rectangle futureArea = new Rectangle( // Creates a Rectangle representing entity's hitbox after movement
            entity.worldX + entity.solidArea.x + dx, // X position of hitbox after moving by dx
            entity.worldY + entity.solidArea.y + dy, // Y position of hitbox after moving by dy
            entity.solidArea.width, // Width of the hitbox
            entity.solidArea.height // Height of the hitbox
        );

        // Player hitbox
        Rectangle playerArea = new Rectangle( // Creates a Rectangle representing the player's current hitbox
            gp.player.worldX + gp.player.solidArea.x, // X position of player's hitbox
            gp.player.worldY + gp.player.solidArea.y, // Y position of player's hitbox
            gp.player.solidArea.width, // Width of player's hitbox
            gp.player.solidArea.height // Height of player's hitbox
        );

        return futureArea.intersects(playerArea); // Returns true if entity's future hitbox intersects with player's hitbox, false otherwise
    }


    
}