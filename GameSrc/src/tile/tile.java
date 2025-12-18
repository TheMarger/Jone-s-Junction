package tile;

/*
 Christina Heaven
 Tile class
 Description: This class represents a single tile in the game.
 Each tile has an image and can be solid/not solid.
 
 Used for: Building maps and handling collision logic
 */
import java.awt.image.BufferedImage;

public class tile {

    private int x;  // tile column
    private int y;  // tile row

    private boolean collision = false; //tile solidity
    private BufferedImage image; //tile image

    // Constructor used by TileManager, for the looks and solidity
    public tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
        this.x = -1;   
        this.y = -1;  
    }

    // Constructor used, for the placing of the tiles
    public tile(int x, int y, BufferedImage image, boolean collision) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.collision = collision;
    }
    
    public int getX() {
		return x;
	}
    public int getY() {
    	return y;
    }

    public boolean isSolid() { 
    	return collision; 
    	}
    
    public BufferedImage getImage() { 
    	return image; 
    	}

}

