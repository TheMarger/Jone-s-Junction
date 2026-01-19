/*
Names: Sukhmanpreet Gill
Course: ICS4U
Assignment: Jone's Junction
Due date: January 18, 2026
Program Description: This program is a prison escape game.
Throughout 4 levels, users must complete tasks to progress to the next level.
Within our game there are three types of guards, each with varying FOV mechanics. As you progress into higher levels, more types of guards appear.
The game has food items, which can be used to regain stamina lost during sprinting. These items can also be thrown to create a distraction for guards.
Similarly, the game has throwable items, which can not be used to regain stamina but can be used to create greater guard distractions.
The game has the features of saving game files, loading game files, character customization, and exiting.
If the user collides with a guard, they are told they lost and they must restart the level.
The player has the option to speedrun the game, where they must beat each level in an allocated time frame. If they do not, they must restart the level.
File & Class description:
This file and class are used to house the logic of all guard types. Here, everything about them is handled from using their fov calculations, guard states, chase logic, patrol routes, and more.
*/

package gaurd; // Places this class inside the 'gaurd' package so it groups with other guard classes

import entity.entity; // Imports the base entity class so gaurd can extend shared behavior
import main.gamePanel; // Imports gamePanel so the guard can access world and player data
import java.awt.Rectangle; // Imports Rectangle for field-of-view and collision checks
import java.awt.image.BufferedImage; // Imports BufferedImage for drawing guard sprites
import java.awt.Color; // Imports Color for drawing FOV overlays
import java.awt.Graphics2D; // Imports Graphics2D for rendering the guard

public class gaurd extends entity { // Declares the gaurd class which extends the generic entity class

    public enum GuardState { // Defines the possible AI states for the guard
        PATROL, // Guard follows its patrol route
        INVESTIGATE, // Guard moves to investigate a sound or disturbance
        CHASE, // Guard chases the player when seen
        RETURN // Guard returns to its patrol route after investigating
    }
    
    private int lastX, lastY; // Stores the guard's last world position to detect being stuck
    private int stuckCounter = 0; // Counts frames the guard hasn't moved to detect stuck state

    public GuardState state = GuardState.PATROL; // Current AI state, defaults to PATROL

    public int investigateX, investigateY; // Target coordinates to investigate when hearing a sound
    public long investigateEndTime; // Timestamp when investigation should end
    private int outsideFrames = 0; // Frames spent outside patrol bounds
    private int outsideThresholdFrames = 300; // Frames threshold before teleporting back

    public int patrolX, patrolY; // Current patrol target position in world pixels
    public int[][] patrolRoute = null; // Array of tile coordinates that form the patrol route
    public int patrolIndex = 0; // Index into patrolRoute for the current patrol target
    public int spawnTileX, spawnTileY; // Spawn tile coordinates used as fallback teleport location
    private int lastWorldX, lastWorldY; // Last recorded world position used by outside checks


    public gaurd(gamePanel gp) { // Constructor that receives the main game panel for context
        super(gp); // Calls the entity constructor to initialize shared fields
        walkSpeed = 2; // Default walking speed in pixels per update
        sprintSpeed = 3; // Default sprinting speed in pixels per update
        if (gp != null && gp.FPS > 0) { // If gamePanel is valid and FPS is known
            outsideThresholdFrames = 3 * gp.FPS; // Set outside threshold to 3 seconds worth of frames
        }
        direction = "down"; // Default facing direction

        solidArea.x = 14; // Collision box X offset inside the sprite
        solidArea.y = 16; // Collision box Y offset inside the sprite
        solidArea.width = 32; // Collision box width in pixels
        solidArea.height = 32; // Collision box height in pixels
        solidAreaDefaultX = solidArea.x; // Save default X offset for resets
        solidAreaDefaultY = solidArea.y; // Save default Y offset for resets

        isMoving = true; // Guards start in a moving state by default
        getImage(); // Load guard sprites
    }

   public void getImage() { // Loads sprite frames for each direction
        up1 = setup("/guards/Guard-1"); // First up-facing sprite
        up2 = setup("/guards/Guard-2"); // Second up-facing sprite
        down1 = setup("/guards/Guard-3"); // First down-facing sprite (reused)
        down2 = setup("/guards/Guard-4"); // Second down-facing sprite (reused)
        left1 = setup("/guards/Guard-7"); // First left-facing sprite (reused)
        left2 = setup("/guards/Guard-8"); // Second left-facing sprite (reused)
        right1 = setup("/guards/Guard-5"); // First right-facing sprite (reused)
        right2 = setup("/guards/Guard-6"); // Second right-facing sprite (reused)
    }

    @Override
    public void setAction() { // Decides the guard's action each AI tick

        if (canSeePlayer()) { // If the guard can see the player right now
            state = GuardState.CHASE; // Switch to chase state
            outsideFrames = 0; // Reset outside counter while chasing
        }

        switch (state) { // Execute behavior based on current state

            case PATROL:
                doPatrol(); // Perform patrol behavior
                break; // Breaks out of the switch case

            case INVESTIGATE:
                doInvestigate(); // Perform investigation behavior
                speed = walkSpeed; // Use walking speed while investigating
                break; // Breaks out of the switch case

            case CHASE:
                doChase(); // Perform chase behavior
                speed = sprintSpeed; // Use sprinting speed while chasing
                break; // Breaks out of the switch case

            case RETURN:
                doReturn(); // Return to patrol route
                speed = walkSpeed; // Use walking speed while returning
                break; // Breaks out of the switch case
        }
        
        if (state == GuardState.CHASE) { // Extra checks while in chase state

            if (worldX == lastX && worldY == lastY) { // If guard hasn't moved since last tick
                stuckCounter++; // Increment stuck counter
            } else { // If the above if statement wasn't met, this code runs
                stuckCounter = 0; // Reset stuck counter when movement occurs
            }

            if (stuckCounter > 60) { // If stuck for too long (60 frames)
                stuckCounter = 0; // Reset counter
                state = GuardState.PATROL; // Revert to patrol to recover
                outsideFrames = 0; // Reset outside counter
                direction = "down"; // Reset facing direction as a fallback
            }
        }

        lastX = worldX; // Save current X for next tick's stuck detection
        lastY = worldY; // Save current Y for next tick's stuck detection
        
    }

    public void doPatrol() { // Patrol behavior: face toward current patrol target and advance index when reached
        if (patrolRoute == null || patrolRoute.length == 0) return; // Nothing to do if no route

        int targetTileX = patrolRoute[patrolIndex][0]; // Patrol target tile X
        int targetTileY = patrolRoute[patrolIndex][1]; // Patrol target tile Y

        int targetX = targetTileX * gp.tileSize; // Convert target tile X to world pixels
        int targetY = targetTileY * gp.tileSize; // Convert target tile Y to world pixels

        if (Math.abs(worldX - targetX) > Math.abs(worldY - targetY)) { // Prefer horizontal movement if farther horizontally
            if (worldX < targetX) direction = "right"; // Face right if target is to the right
            else if (worldX > targetX) direction = "left"; // Face left if target is to the left
        } else {
            if (worldY < targetY) direction = "down"; // Face down if target is below
            else if (worldY > targetY) direction = "up"; // Face up if target is above
        }

        if (Math.abs(worldX - targetX) < 4 && Math.abs(worldY - targetY) < 4) { // If close enough to target
            patrolIndex++; // Advance to next patrol point
            if (patrolIndex >= patrolRoute.length) patrolIndex = 0; // Wrap patrol index if at end
        }
    }

    public void doInvestigate() { // Investigation behavior: move toward the investigation point

        moveToward(investigateX, investigateY); // Set direction toward investigate coordinates

        if (System.currentTimeMillis() > investigateEndTime) { // If investigation time expired
            state = GuardState.RETURN; // Switch to return state
            outsideFrames = 0; // Reset outside counter
        }
    }

    public void doChase() { // Chase behavior: move toward the player's current position
        moveToward(gp.player.worldX, gp.player.worldY); // Face toward player
        speed = sprintSpeed; // Ensure sprint speed while chasing
    }

    public void doReturn() { // Return behavior: move back to saved patrol position

        moveToward(patrolX, patrolY); // Face toward stored patrol position

        if (Math.abs(worldX - patrolX) < 4 && Math.abs(worldY - patrolY) < 4) { // If close to patrol position
            state = GuardState.PATROL; // Resume patrolling
            outsideFrames = 0; // Reset outside counter
        }
    }

    public void moveToward(int targetX, int targetY) { // Sets facing direction toward a target point

        int dx = targetX - worldX; // Delta X to target
        int dy = targetY - worldY; // Delta Y to target

        if (Math.abs(dx) > Math.abs(dy)) { // Prefer horizontal facing if horizontal distance is greater
            direction = dx > 0 ? "right" : "left"; // Choose left or right
        } else { // If the above if statement wasn't met, this code runs
            direction = dy > 0 ? "down" : "up"; // Otherwise choose up or down
        }
    }

    public void hearSound(int x, int y) { // Called when a sound is heard; sets investigation target and timer
        investigateX = x; // Save sound X coordinate
        investigateY = y; // Save sound Y coordinate

        patrolX = worldX; // Save current world X as return point
        patrolY = worldY; // Save current world Y as return point

        investigateEndTime = System.currentTimeMillis() + 5000; // Investigate for 5 seconds

        state = GuardState.INVESTIGATE; // Switch to investigate state
        outsideFrames = 0; // Reset outside counter while investigating
    }

    public boolean canSeePlayer() { // Checks if player is inside FOV rectangle and line of sight is clear

        Rectangle playerBox = gp.player.getHitbox(); // Get the player's collision box for intersection testing
        int ts = gp.tileSize; // Cache tile size to convert tiles to pixels

        Rectangle fov = new Rectangle(); // Allocate a rectangle to represent the guard's field of view

        switch (direction) { // Choose FOV shape based on the guard's facing direction

            case "up": // Facing up: build an FOV above the guard
                fov.setBounds( // Set FOV bounds for upward facing
                    worldX - ts, // left edge: one tile left of guard center
                    worldY - (3 * ts), // top edge: three tiles above guard center
                    ts * 3, // width: three tiles wide
                    ts * 3 // height: three tiles tall
                ); // FOV above the guard
                break; // End "up" case

            case "down": // Facing down: build an FOV below the guard
                fov.setBounds( // Set FOV bounds for downward facing
                    worldX - ts, // left edge: one tile left of guard center
                    worldY + ts, // top edge: one tile below guard center
                    ts * 3, // width: three tiles wide
                    ts * 3 // height: three tiles tall
                ); // FOV below the guard
                break; // End "down" case

            case "left": // Facing left: build an FOV to the left of the guard
                fov.setBounds( // Set FOV bounds for leftward facing
                    worldX - (3 * ts), // left edge: three tiles left of guard center
                    worldY - ts, // top edge: one tile above guard center
                    ts * 3, // width: three tiles wide
                    ts * 3 // height: three tiles tall
                ); // FOV to the left of the guard
                break; // End "left" case

            case "right": // Facing right: build an FOV to the right of the guard
                fov.setBounds( // Set FOV bounds for rightward facing
                    worldX + ts, // left edge: one tile right of guard center
                    worldY - ts, // top edge: one tile above guard center
                    ts * 3, // width: three tiles wide
                    ts * 3 // height: three tiles tall
                ); // FOV to the right of the guard
                break; // End "right" case
        } // End switch on direction

        if (!fov.intersects(playerBox)) { // If the player's hitbox does not intersect the FOV rectangle
            return false; // Player is outside FOV, so not visible
        } // End intersection check

        return hasLineOfSightToPlayer(); // Player is inside FOV; return true only if line-of-sight is clear
    } // End canSeePlayer
    
    public Rectangle getFOV() { // Default FOV getter used by drawing; overridden by specific guard types
    	return new Rectangle(worldX, worldY, 0, 0); // Returns an empty rectangle by default
    }
    
    @Override
    public void draw(Graphics2D g2) { // Draws the guard and its FOV overlay for debugging

        int screenX = worldX - gp.player.worldX + gp.player.getScreenX(); // Convert world X to screen X
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY(); // Convert world Y to screen Y

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) { // If guard is inside the camera view

            BufferedImage image = null; // Placeholder for selected sprite
            switch(direction) { // Choose sprite based on facing direction and animation frame
                case "up":    image = (spriteNum == 1) ? up1 : up2; break; // Up sprite
                case "down":  image = (spriteNum == 1) ? down1 : down2; break; // Down sprite
                case "left":  image = (spriteNum == 1) ? left1 : left2; break; // Left sprite
                case "right": image = (spriteNum == 1) ? right1 : right2; break; // Right sprite
            }
            g2.drawImage(image, screenX, screenY, null); // Draw the guard sprite

            Rectangle fov = getFOV(); // Get the guard's FOV rectangle

            int fovScreenX = fov.x - gp.player.worldX + gp.player.getScreenX(); // Convert FOV X to screen X
            int fovScreenY = fov.y - gp.player.worldY + gp.player.getScreenY(); // Convert FOV Y to screen Y

            g2.setColor(new Color(255, 0, 0, 15)); // Semi-transparent red for FOV fill
            g2.fillRect(fovScreenX, fovScreenY, fov.width, fov.height); // Fill the FOV area

            g2.setColor(Color.RED); // Solid red for FOV outline
            g2.drawRect(fovScreenX, fovScreenY, fov.width, fov.height); // Draw the FOV rectangle outline
        }
    }
    private boolean tileBlocksVision(int col, int row) { // Checks whether a tile at (col,row) blocks vision
        int tileNum = gp.tileM.mapTileNum[col][row]; // Get tile index from map
        return gp.tileM.tile[tileNum].collision;  // Return whether that tile is marked as collidable
    }
    private boolean hasLineOfSightToPlayer() { // Steps from guard to player tile-by-tile to check for blocking tiles

        int guardCol = worldX / gp.tileSize; // Guard's tile column
        int guardRow = worldY / gp.tileSize; // Guard's tile row

        int playerCol = gp.player.worldX / gp.tileSize; // Player's tile column
        int playerRow = gp.player.worldY / gp.tileSize; // Player's tile row

        int dx = playerCol - guardCol; // Delta columns to player
        int dy = playerRow - guardRow; // Delta rows to player

        int steps = Math.max(Math.abs(dx), Math.abs(dy)); // Number of steps for sampling along the line

        float stepX = dx / (float) steps; // Fractional column step per iteration
        float stepY = dy / (float) steps; // Fractional row step per iteration

        float currentX = guardCol; // Start sampling at guard column
        float currentY = guardRow; // Start sampling at guard row

        for (int i = 0; i < steps; i++) { // Iterate along the line toward the player
            currentX += stepX; // Advance sample X
            currentY += stepY; // Advance sample Y

            int checkCol = Math.round(currentX); // Round to nearest column
            int checkRow = Math.round(currentY); // Round to nearest row

            if (checkCol == guardCol && checkRow == guardRow) continue; // Skip the guard's own tile

            if (tileBlocksVision(checkCol, checkRow)) { // If any sampled tile blocks vision
                return false; // Line of sight is blocked
            }
        }

        return true; // No blocking tiles found; player is visible
    }
    @Override
    public void update() { // Per-frame update called by the game loop
        int oldSpeed = speed; // Save current speed to restore after base update

        super.update(); // Call entity update to handle movement and animation

        speed = oldSpeed; // Restore speed in case base update modified it
        
        checkOutsideAndTeleport(); // Ensure guard hasn't wandered outside its patrol bounds
    }
    
    public boolean hasLineOfSound(int sx, int sy) { // Checks if sound at (sx,sy) can reach the guard without blocked tiles

        int guardCol = worldX / gp.tileSize; // Guard's column on the tile grid
        int guardRow = worldY / gp.tileSize; // Guard's row on the tile grid

        int soundCol = sx / gp.tileSize; // Sound source column on the tile grid
        int soundRow = sy / gp.tileSize; // Sound source row on the tile grid

        int dx = soundCol - guardCol; // Column difference from guard to sound
        int dy = soundRow - guardRow; // Row difference from guard to sound

        int steps = Math.max(Math.abs(dx), Math.abs(dy)); // Number of sampling steps along the line

        float stepX = dx / (float) steps; // Fractional column increment per step
        float stepY = dy / (float) steps; // Fractional row increment per step

        float cx = guardCol; // Current sample column (float)
        float cy = guardRow; // Current sample row (float)

        for (int i = 0; i < steps; i++) { // Step along the line toward the sound
            cx += stepX; // Advance sample X
            cy += stepY; // Advance sample Y

            int col = Math.round(cx); // Round sample X to nearest column
            int row = Math.round(cy); // Round sample Y to nearest row

            if (col == guardCol && row == guardRow) continue; // Skip the guard's own tile
            if (tileBlocksSound(col, row)) return false; // If any tile blocks sound, line is blocked
        }

        return true; // No blocking tiles found; sound can reach the guard
    }

    private boolean tileBlocksSound(int col, int row) { // Returns whether the tile at (col,row) blocks sound
        int tileNum = gp.tileM.mapTileNum[col][row]; // Look up the tile index from the map
        return gp.tileM.tile[tileNum].collision; // Treat collision tiles as blocking sound
    }

    public void setPatrolRouteFromSpawn(boolean horizontal, int lengthTiles, int mapCols, int mapRows) { // Build a simple two-point patrol route from spawn
        int startCol = spawnTileX; // Start column defaults to spawn column
        int startRow = spawnTileY; // Start row defaults to spawn row

        int endCol = startCol; // Initialize end column
        int endRow = startRow; // Initialize end row

        if (horizontal) { // If route should be horizontal
            endCol = startCol + (lengthTiles - 1); // Compute end column based on length
            if (endCol >= mapCols) { // Clamp if it would go off the right edge
                endCol = mapCols - 1; // Set to last valid column
                startCol = Math.max(0, endCol - (lengthTiles - 1)); // Shift start left so length fits
            }
        } else { // Vertical route
            endRow = startRow + (lengthTiles - 1); // Compute end row based on length
            if (endRow >= mapRows) { // Clamp if it would go off the bottom edge
                endRow = mapRows - 1; // Set to last valid row
                startRow = Math.max(0, endRow - (lengthTiles - 1)); // Shift start up so length fits
            }
        }

        this.patrolRoute = new int[][] { // Create a two-point patrol route: start and end
            { startCol, startRow },
            { endCol, endRow }
        };
        this.patrolIndex = 0; // Reset patrol index to start
    }

    public void teleportBackToRoute() { // Teleport guard to the nearest patrol point or spawn if no route
        if (patrolRoute == null || patrolRoute.length == 0) { // If no route defined
            worldX = spawnTileX * gp.tileSize; // Teleport to spawn X in pixels
            worldY = spawnTileY * gp.tileSize; // Teleport to spawn Y in pixels
            return; // Done
        }

        int bestX = patrolRoute[0][0] * gp.tileSize; // Initialize best X to first patrol point (pixels)
        int bestY = patrolRoute[0][1] * gp.tileSize; // Initialize best Y to first patrol point (pixels)
        double bestDist = Double.MAX_VALUE; // Track smallest distance found

        for (int i = 0; i < patrolRoute.length; i++) { // Find nearest patrol point to current position
            int tx = patrolRoute[i][0] * gp.tileSize; // Patrol point X in pixels
            int ty = patrolRoute[i][1] * gp.tileSize; // Patrol point Y in pixels
            double d = Math.hypot(worldX - tx, worldY - ty); // Euclidean distance to this patrol point
            if (d < bestDist) { // If closer than previous best
                bestDist = d; // Update best distance
                bestX = tx; // Update best X
                bestY = ty; // Update best Y
            }
        }

        worldX = bestX; // Teleport guard X to nearest patrol point
        worldY = bestY; // Teleport guard Y to nearest patrol point

        for (int i = 0; i < patrolRoute.length; i++) { // Find which patrol index matches the teleport location
            if (Math.abs(worldX - patrolRoute[i][0] * gp.tileSize) < 2 &&
                Math.abs(worldY - patrolRoute[i][1] * gp.tileSize) < 2) { // If within 2 pixels of a patrol point
                patrolIndex = i; // Set patrolIndex to that point
                break; // Stop searching
            }
        }

        outsideFrames = 0; // Reset outside counter after teleport
        lastWorldX = worldX; // Update last known world X
        lastWorldY = worldY; // Update last known world Y

    }

    public boolean isInsidePatrolBounds() { // Returns true if guard's current tile is within the bounding box of the patrol route
        if (patrolRoute == null || patrolRoute.length == 0) return true; // If no route, consider guard inside

        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE; // Track min/max columns of route
        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE; // Track min/max rows of route

        for (int i = 0; i < patrolRoute.length; i++) { // Compute bounding box over all patrol points
            int c = patrolRoute[i][0]; // Column of route point
            int r = patrolRoute[i][1]; // Row of route point
            if (c < minCol) minCol = c; // Update min column
            if (c > maxCol) maxCol = c; // Update max column
            if (r < minRow) minRow = r; // Update min row
            if (r > maxRow) maxRow = r; // Update max row
        }

        int currentCol = worldX / gp.tileSize; // Guard's current column
        int currentRow = worldY / gp.tileSize; // Guard's current row

        return (currentCol >= minCol && currentCol <= maxCol && currentRow >= minRow && currentRow <= maxRow); // True if inside bounds
    }

    public void checkOutsideAndTeleport() { // Tracks whether guard left patrol bounds and teleports back after threshold
        boolean inChase = (state == GuardState.CHASE); // True if currently chasing the player
        boolean inInvestigation = (state == GuardState.INVESTIGATE); // True if currently investigating a sound

        if (inChase || inInvestigation) { // If busy chasing or investigating
            outsideFrames = 0; // Reset outside counter
            lastWorldX = worldX; // Update last known world X
            lastWorldY = worldY; // Update last known world Y
            return; // Do not teleport while engaged
        }

        if (isInsidePatrolBounds()) { // If still inside patrol bounds
            outsideFrames = 0; // Reset outside counter
            lastWorldX = worldX; // Update last known world X
            lastWorldY = worldY; // Update last known world Y
            return; // Nothing to do
        }

        outsideFrames++; // Increment frames spent outside patrol bounds

        if (outsideFrames >= outsideThresholdFrames) { // If exceeded allowed time outside
            teleportBackToRoute(); // Teleport back to nearest patrol point or spawn
        }
    }
}
