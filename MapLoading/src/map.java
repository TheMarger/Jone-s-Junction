/*
 Christina Heaven
 Map class
 Description: Stores the level layout using time numbers 
 & draws the map using tiles from Tile Manager
 Used for: Managing & rendering the game level layout
 */
import java.awt.Graphics2D;

public class map {

	//Map size in tiles
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;

    private int[][] tiles = new int[HEIGHT][WIDTH]; // Stores numbers that tell the game which tile to use from TileManager

    public map(String mapData) {
        loadMap(mapData);
    }
    
// Converts map text data to 2D tile array
    private void loadMap(String data) {
    	
        String[] numbers = data.split("\\s+");
        int index = 0;

        for (int row = 0; row < HEIGHT; row++) {
        	
            for (int col = 0; col < WIDTH; col++) {
            	
            	//Converts string num into tile index
                tiles[row][col] = Integer.parseInt(numbers[index]);
                index++;
            }
        }
    }
    //Draws the map onto the screen
    public void draw(Graphics2D g2, TileManager tileManager) {
    	
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {

                int tileIndex = tiles[row][col];
                Tile baseTile = tileManager.tiles[tileIndex];

                //Draw tile image
                g2.drawImage(baseTile.getImage(), col * 16, row * 16, null);
            }
        }
    }

}
