package tile;

/*
 Christina Heaven
 Tile class
 Description: This class represents a single tile in the game.
 Each tile has an image and can be solid/not solid.
 
 Used for: Building maps and handling collision logic
 */
import java.awt.image.BufferedImage; //Stores & draws images

public class tile { 

	//Tiles position
    private int x;  //Tile column
    private int y;  //Tile row

    public boolean collision = false; //Tile solidity. true = solid, false = walkable
    
    public BufferedImage image; //Tile image
    private int num; //Tile ID number

    //Constructor used by TileManager, for image and solidity
    public tile(int num, BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
        this.num = num;
        this.x = -1;   
        this.y = -1;  
    }

    //Constructor used for the placing the tiles on the map
    public tile(int x, int y, BufferedImage image, boolean collision) {
        this.x = x; //Set tile X position
        this.y = y; //Set tile Y position
        this.image = image; //Assign tile image
        this.collision = collision; //Set solidity
    }
    
    //Returns the tile's X position
    public int getX() {
		return x;
	}
    
    //Returns the tile's Y position
    public int getY() {
    	return y;
    }

    //Returns whether the tile is solid or not
    public boolean isSolid() { 
    	return collision; 
    	}
    
  //Returns the tile's image
    public BufferedImage getImage() { 
    	return image; 
    	}
    
    //Returns the tile's ID number
    public int getNum() {
		return num;
	}

}

