/*
 Christina Heaven
 Tile class
 Description: This class represents a single tile in the game.
 Each tile has an image and can be solid/not solid.
 
 Used for: Building maps and handling collision logic
 */
import java.awt.image.BufferedImage;

public class Tile {

    private int x;  // tile column
    private int y;  // tile row

    private boolean solid; //tile solidity
    private BufferedImage image; //tile image

    // Constructor used by TileManager, for the looks and solidity
    public Tile(BufferedImage image, boolean solid) {
        this.image = image;
        this.solid = solid;
        this.x = -1;   
        this.y = -1;  
    }

    // Constructor used, for the placing of the tiles
    public Tile(int x, int y, BufferedImage image, boolean solid) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.solid = solid;
    }


    public boolean isSolid() { 
    	return solid; 
    	}
    
    public BufferedImage getImage() { 
    	return image; 
    	}

}

