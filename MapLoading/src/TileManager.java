/*
 Christina Heaven
 TileManager class
 Description: Loads all the tile images & their solidity
 from a text file & stores them for use in the game.
 
 Used for: Managing tile data & providing tiles to the map
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TileManager {
    public Tile[] tiles = new Tile[232]; // Tile max amount used

    public TileManager() {
    	loadTilesFromFile();
    }

    //Reads Tiledata.txt & loads each tile
    private void loadTilesFromFile() {
        try {
            
            InputStream is = getClass().getResourceAsStream("/Tiledata.txt");

            if (is == null) {
                System.out.println("errorr: Tiledata.txt not found on classpath");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while (true) {
               
            	//Read tile name
                String filename = reader.readLine();
                if (filename == null) break; 

                filename = filename.trim();
                if (filename.isEmpty()) continue;


                boolean solid = Boolean.parseBoolean(reader.readLine().trim());
                
                //Take index form name (016.png to 16)
                int index = Integer.parseInt(filename.substring(0, 3)); 

               //Declare img
                BufferedImage img = ImageIO.read(
                        getClass().getResource("/Tiles/" + filename)
                );
                
                //creates new tile using image & solid value, then stores it in tiles array at index
                tiles[index] = new Tile(img, solid);
            }

            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}