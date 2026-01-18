/*
 * Name: Christina Heaven
 * Course Code: ICS4U0
 * Date: 1/19/2026
 * Description: Tile class that represents a single tile in the game.
 *              Each tile has an image, position, and collision property.
 *              Used for building maps and handling collision logic.
 * 
 */

package tile; // Declares the package name for this class

import java.awt.image.BufferedImage; // Imports BufferedImage for storing tile graphics

public class tile { // Defines the tile class for map tiles

    private int x;  // Stores the tile's column position in the grid
    private int y;  // Stores the tile's row position in the grid

    public boolean collision = false; // Indicates if the tile blocks movement (true = solid)
    public BufferedImage image; // Stores the visual image for this tile
    private int num; // Stores the tile type/ID number

    // Constructor used by TileManager to create tile types with appearance and solidity
    public tile(int num, BufferedImage image, boolean collision) {
        this.image = image; // Assigns the tile's image
        this.collision = collision; // Sets whether the tile is solid
        this.num = num; // Stores the tile type number
        this.x = -1; // Sets x to -1 (indicates no specific grid position yet)
        this.y = -1; // Sets y to -1 (indicates no specific grid position yet)
    }

    // Constructor used for placing tiles at specific positions on the map
    public tile(int x, int y, BufferedImage image, boolean collision) {
        this.x = x; // Sets the tile's column position
        this.y = y; // Sets the tile's row position
        this.image = image; // Assigns the tile's image
        this.collision = collision; // Sets whether the tile is solid
    }

    public int getX() { // Returns the tile's column position
		return x; // Returns the x coordinate
	}
    
    public int getY() { // Returns the tile's row position
    	return y; // Returns the y coordinate
    }

    public boolean isSolid() { // Checks if the tile is solid (blocks movement)
    	return collision; // Returns true if tile has collision, false otherwise
    }

    public BufferedImage getImage() { // Returns the tile's image
    	return image; // Returns the BufferedImage for this tile
    }
    
    public int getNum() { // Returns the tile's type number/ID
		return num; // Returns the tile number
	}

}