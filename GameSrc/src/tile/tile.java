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

    public boolean collision = false; //tile solidity
    public BufferedImage image; //tile image
    private int num; 

    // Constructor used by TileManager, for the looks and solidity
    public tile(int num, BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
        this.num = num;
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
    public int getNum() {
		return num;
	}

}

