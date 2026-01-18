/*
 * Name: Christina Heaven
 * Course Code: ICS4U0
 * Date: 1/19/2026
 * Description: TileManager class that manages all tiles in the game.
 *              Handles loading tile images, map data, rendering tiles on screen,
 *              and managing tile collision properties.
 */

package tile; // Declares the package name for this class

import java.awt.Color; // Imports Color class for color operations
import java.awt.Graphics2D; // Imports Graphics2D for 2D drawing operations
import java.awt.image.BufferedImage; // Imports BufferedImage for image handling
import java.io.BufferedReader; // Imports BufferedReader for reading text files
import java.io.InputStream; // Imports InputStream for reading resource files
import java.io.InputStreamReader; // Imports InputStreamReader for converting streams to readable text

import javax.imageio.ImageIO; // Imports ImageIO for loading images

import main.UtilityTool; // Imports UtilityTool class for helper functions
import main.gamePanel; // Imports gamePanel class reference

public class TileManager { // Defines the TileManager class for tile management
	
	gamePanel gp; // Reference to the main game panel
	public static tile[] tile; // Static array storing all available tile types
	public int mapTileNum[][]; // 2D array storing which tile type is at each map position
	
	public TileManager(gamePanel gp) { // Constructor that initializes the tile manager
		this.gp = gp; // Stores the game panel reference
		UtilityTool uTool = new UtilityTool(gp); // Creates utility tool instance
		tile = new tile[600]; // Initializes array to hold up to 600 different tile types
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; // Creates 2D array matching world map dimensions
		getTileImage(); // Loads all tile images from resources
	}
	
	public void resetMap () { // Resets and reloads the map data
		tile = new tile[600]; // Reinitializes the tile array
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; // Reinitializes the map tile number array
		getTileImage(); // Reloads all tile images
		if (gp.level == 1) { // Checks if current level is 1
			loadMap("/maps/Level1Map.txt"); // Loads Level 1 map file
		} else if (gp.level == 2) { // Checks if current level is 2
			loadMap("/maps/Level2Map.txt"); // Loads Level 2 map file
		} else if (gp.level == 3) { // Checks if current level is 3
			loadMap("/maps/Level3Map.txt"); // Loads Level 3 map file
		} else if (gp.level == 4) { // Checks if current level is 4
			loadMap("/maps/Level4Map.txt"); // Loads Level 4 map file
		}
	}
	
	public static void lockTile(int tileIndex) { // Sets a tile to be solid (blocks movement)
	    if (tileIndex >= 0 && tileIndex < tile.length) { // Validates the tile index is within valid range
	        try { // Begins exception handling block
				tile[tileIndex].collision = true; // Sets the tile's collision property to true
			} catch (Exception e) { // Catches any exceptions during the operation
				e.printStackTrace(); // Prints error details to console
			}
	    }
	}
	
	public void getTileImage() { // Loads all tile images and their properties from data file
		try { // Begins exception handling block
            
            InputStream is = getClass().getResourceAsStream("/tiles/Tiledata.txt"); // Opens the tile data file as a stream

            if (is == null) { // Checks if the file was not found
                System.out.println("errorr: Tiledata.txt not found on classpath"); // Prints error message
                return; // Exits the method early
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); // Creates reader for the data file
            String filename; // Variable to store each filename read from file
            while ((filename = reader.readLine()) != null) { // Reads each line (filename) until end of file
                filename = filename.trim(); // Removes whitespace from filename
                if (filename.isEmpty()) continue; // Skips empty lines

                String collisionLine = reader.readLine(); // Reads the next line containing collision data
                if (collisionLine == null) break; // Exits loop if no collision data found
                boolean collision = Boolean.parseBoolean(collisionLine.trim()); // Converts collision string to boolean

                int index = Integer.parseInt(filename.substring(0, 3)); // Extracts tile index from first 3 characters of filename

                // Loads image from resources using getResourceAsStream to avoid null errors
                InputStream imgStream = getClass().getResourceAsStream("/tiles/" + filename); // Opens image file as stream
                if (imgStream == null) { // Checks if image file was not found
                    System.out.println("warning: tile image not found: " + filename); // Prints warning message
                    continue; // Skips to next tile
                }
                BufferedImage img = ImageIO.read(imgStream); // Reads the image into a BufferedImage
                int num = index; // Stores the tile number

                tile[index] = new tile(num, img, collision); // Creates new tile object and stores it in array
            }
            reader.close(); // Closes the file reader
        } catch (Exception e) { // Catches any exceptions during file reading or image loading
            e.printStackTrace(); // Prints error details to console
        }
	}
	
	
	public static void unlockTile(int tileIndex) { // Sets a tile to be passable (no collision)
	    if (tileIndex >= 0 && tileIndex < tile.length) { // Validates the tile index is within valid range
	        try { // Begins exception handling block
				tile[tileIndex].collision = false; // Sets the tile's collision property to false
			} catch (Exception e) { // Catches any exceptions during the operation
				e.printStackTrace(); // Prints error details to console
			}
	    }
	}
	
	public void loadMap(String filePath) { // Loads map layout from a text file
		try { // Begins exception handling block
			InputStream is = getClass().getResourceAsStream(filePath); // Opens the map file as a stream
			BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Creates reader for the map file
			
			int col = 0; // Current column being processed
			int row = 0; // Current row being processed
			
			while (col < gp.maxWorldCol && row < gp.maxWorldRow) { // Loops through all rows and columns
				String line = br.readLine(); // Reads one line (one row of tiles)
				while (col < gp.maxWorldCol) { // Loops through all columns in current row
					String numbers[] = line.split(" "); // Splits line by spaces to get individual tile numbers
					int num = Integer.parseInt(numbers[col]); // Converts tile number string to integer
					mapTileNum[col][row] = num; // Stores tile number in the map array
					col++; // Moves to next column
				}
				if (col == gp.maxWorldCol) { // Checks if finished processing all columns
					col = 0; // Resets column to start
					row++; // Moves to next row
				}
			}
			br.close(); // Closes the file reader
		} catch (Exception e) { // Catches any exceptions during file reading
			e.printStackTrace(); // Prints error details to console
		}
	}
	
	public void draw(Graphics2D g2) { // Draws all visible tiles on the screen
	    int worldCol = 0; // Current world column being drawn
	    int worldRow = 0; // Current world row being drawn

	    while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) { // Loops through all tiles in the world

	        int tileNum = mapTileNum[worldCol][worldRow]; // Gets the tile type number at this position

	        int worldX = worldCol * gp.tileSize; // Calculates world X coordinate of this tile
	        int worldY = worldRow * gp.tileSize; // Calculates world Y coordinate of this tile

	        int screenX = worldX - gp.player.worldX + gp.player.getScreenX(); // Converts world X to screen X relative to player
	        int screenY = worldY - gp.player.worldY + gp.player.getScreenY(); // Converts world Y to screen Y relative to player
	        
	        // Checks if tile is within visible screen area (optimization to only draw visible tiles)
	        if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() && // Right edge visible
	            worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() && // Left edge visible
	            worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() && // Bottom edge visible
	            worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) { // Top edge visible
	        	
	        	// Draws the tile image at the calculated screen position
	        	g2.drawImage(tile[tileNum].getImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);

	        }
	        
	        worldCol++; // Moves to next column

	        if (worldCol == gp.maxWorldCol) { // Checks if finished all columns in current row
	            worldCol = 0; // Resets column to start
	            worldRow++; // Moves to next row
	        }
	    }
	}
	
	public static tile[] getTileArray() { // Returns the array of all tile types
	    return tile; // Returns the static tile array
	}
	
	public static tile getTile(int index) { // Returns a specific tile by index
	    return tile[index]; // Returns the tile at the given index
	}
	
	public static int getTileCount() { // Returns the total number of tile slots
	    return tile.length; // Returns the length of the tile array
	}
}