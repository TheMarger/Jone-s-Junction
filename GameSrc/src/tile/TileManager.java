/*
Names: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment: Jones Junction
File Name: TileManager.java

Description:
This class is responsible for managing all tiles in the game.
It loads tile images and their collision properties,
reads map files to place tiles in the world,
and draws only the visible tiles to the screen based on the player’s position.

The TileManager helps control how the game world looks and
which areas the player can or cannot walk through.
*/

package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.UtilityTool;
import main.gamePanel;

public class TileManager {
	
	//Link to access player, tile size, world size, level, etc.)
	gamePanel gp;
	
	//Stores tile images and collision info
	public static tile[] tile;
	
	//Stores the map layout, using tile IDs for each position in the world (mapTileNum[col][row] = 5, means "tile type #5 goes here")
	public int mapTileNum[][];
	
	public TileManager(gamePanel gp) {
		
		this.gp = gp;//Saves the gamePanel so this class can use it
		
		//Creates space for up to 600 different tile types
		tile = new tile[600];
		
		//Create the map grid using the world's max columns and rows
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		
		//Load tile images and collision info into the tile[] array
		getTileImage();
	}
	
	//Resets tile data and reloads the map again, to change levels
	public void resetMap () {
		
		tile = new tile[600]; //Re-creates tile array and clears old tile data
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; //Re-create map layout array and clears old map layout
		getTileImage(); //Reload tile images and collision info
		
		//Load the correct map file depending on the current level
		if (gp.level == 1) {
			loadMap("/maps/Level1Map.txt");
		} else if (gp.level == 2) {
			loadMap("/maps/Level2Map.txt");
		} else if (gp.level == 3) {
			loadMap("/maps/Level3Map.txt");
		} else if (gp.level == 4) {
			loadMap("/maps/Level4Map.txt");
		}

	}
	
	//Makes a tile solid
	public static void lockTile(int tileIndex) {
		
		//Safety check to make sure the index is inside the array
	    if (tileIndex >= 0 && tileIndex < tile.length) {
	        try {
				tile[tileIndex].collision = true; //Turn collision on (solid tile)
			} catch (Exception e) {
			
				e.printStackTrace();
			}
	    }
	}
	
	//Loads tile images and collision settings from Tiledata.txt
	public void getTileImage() {
		try {
            
			//Opens the text file
            InputStream is = getClass().getResourceAsStream("/tiles/Tiledata.txt");

            //If file can't be found print error
            if (is == null) { 
                System.out.println("errorr: Tiledata.txt not found on classpath");
                return;
            }
            
            //Read the file line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            
            //Holds each filename line we read from the text file
            String filename; 
            
            //Keep reading until there are no more lines
            while ((filename = reader.readLine()) != null) {
            	
                filename = filename.trim(); //Remove extra spaces
                if (filename.isEmpty()) continue; //Skip empty lines
                
                String collisionLine = reader.readLine(); //Next line in the file is collision value (true/false)
                if (collisionLine == null) break; //If no next line, stop reading 
                boolean collision = Boolean.parseBoolean(collisionLine.trim()); //Converts text to boolean
                
                //Get the tile index from the first 3 numbers of the filename
                int index = Integer.parseInt(filename.substring(0, 3)); 

                //Load image from res folder
                InputStream imgStream = getClass().getResourceAsStream("/tiles/" + filename);
                
                //If the image file can not be found, warn and skip it
                if (imgStream == null) {
                    System.out.println("warning: tile image not found: " + filename);
                    continue;
                }
                BufferedImage img = ImageIO.read(imgStream); //Read the image into a BufferedImage object
                int num = index; //Save the index as the tile's ID number

               //Create a new tile object and store it in the tile array
                tile[index] = new tile(num, img, collision);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//Make a tile not solid
	public static void unlockTile(int tileIndex) {
		
		//Makes sure the index is valid
	    if (tileIndex >= 0 && tileIndex < tile.length) {
	        try {
				tile[tileIndex].collision = false; //Turn collision off
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	    }
	}
	
	//Load a map text file and fills mapTileNum[][] with the correct tile numbers
	public void loadMap(String filePath) {
		
		try {
			//Open the map file from res
			InputStream is = getClass().getResourceAsStream(filePath);
			
			//Read file line by line
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			//Start at top left of the map grid
			int col = 0;
			int row = 0;
			
			//Keep reading until the whole grid is filled out
			while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
				
				//Read one row of numbers from the file
				String line = br.readLine();
				
				//
				while (col < gp.maxWorldCol) {
					
					//Split the line into seperate numbers using spaces
					String numbers[] = line.split(" ");
					
					//Convert the current number into an int
					int num = Integer.parseInt(numbers[col]);
					
					//Store the tile number in the map array
					mapTileNum[col][row] = num;
					col++;//Move to the next column
				}
				
				//If we finish all columns, move to the next row
				if (col == gp.maxWorldCol) {
					col = 0; //Reset column back to 0
					row++;  //Go down one row
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Draws the visible tiles onto screen
	public void draw(Graphics2D g2) {
		
		//Start from top left tile of the map
	    int worldCol = 0;
	    int worldRow = 0;

	   //Loop through every tile in the world grid
	    while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

	    	//Get the tile type number at this world position
	        int tileNum = mapTileNum[worldCol][worldRow];

	        //Convert tile grid position into pixel position in the world
	        int worldX = worldCol * gp.tileSize;
	        int worldY = worldRow * gp.tileSize;

	        //Convert world position into screen position, based on players camera
	        int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
	        int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
	        
	        //Only draw tiles that are inside the camera view
	        if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
	            worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
	            worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
	            worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {
	        	
	        	//Draw the tile image on screen
	        	g2.drawImage(tile[tileNum].getImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);
	        }
	        
	        //Move to next column tile
	        worldCol++;

	        //If we reach the end of the row, go to next row
	        if (worldCol == gp.maxWorldCol) {
	            worldCol = 0;
	            worldRow++;
	        }
	    }
	    
	}
	
	
	//Returns the whole tile[] array
	public static tile[] getTileArray() {
	    return tile;
	}
	
	//Returns one tile type at a specific index
	public static tile getTile(int index) {
	    return tile[index];
	}
	
	//Returns how many tile slots exist (not how many are actually used)
	public static int getTileCount() {
	    return tile.length;
	}
	
	

}
