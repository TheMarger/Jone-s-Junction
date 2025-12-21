package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.gamePanel;

public class TileManager {
	gamePanel gp;
	public static tile[] tile;
	public int mapTileNum[][];
	
	public TileManager(gamePanel gp) {
		this.gp = gp;
		tile = new tile[232];
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		getTileImage();
		loadMap("/maps/Level1Map.txt");
	}
	
	public void getTileImage() {
		try {
            
            InputStream is = getClass().getResourceAsStream("/tiles/Tiledata.txt");

            if (is == null) {
                System.out.println("errorr: Tiledata.txt not found on classpath");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String filename;
            while ((filename = reader.readLine()) != null) {
                filename = filename.trim();
                if (filename.isEmpty()) continue;

                String collisionLine = reader.readLine();
                if (collisionLine == null) break;
                boolean collision = Boolean.parseBoolean(collisionLine.trim());

                int index = Integer.parseInt(filename.substring(0, 3)); // "000.png" -> 0

                // load image from resources (use getResourceAsStream to avoid nulls)
                InputStream imgStream = getClass().getResourceAsStream("/tiles/" + filename);
                if (imgStream == null) {
                    System.out.println("warning: tile image not found: " + filename);
                    continue;
                }
                BufferedImage img = ImageIO.read(imgStream);

                // *** IMPORTANT: store into the tile array so draw() can use it ***
                tile[index] = new tile(img, collision);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void unlockTile(int tileIndex) {
	    if (tileIndex >= 0 && tileIndex < tile.length) {
	        try {
				tile[tileIndex].collision = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public void loadMap(String filePath) {
		try {
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
				String line = br.readLine();
				while (col < gp.maxWorldCol) {
					String numbers[] = line.split(" ");
					int num = Integer.parseInt(numbers[col]);
					mapTileNum[col][row] = num;
					col++;
				}
				if (col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2) {
	    int worldCol = 0;
	    int worldRow = 0;

	    while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

	        int tileNum = mapTileNum[worldCol][worldRow];

	        int worldX = worldCol * gp.tileSize;
	        int worldY = worldRow * gp.tileSize;

	        int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
	        int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
	        
	        if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
	            worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
	            worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
	            worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {
	        	// Draw the tile
	        	g2.drawImage(tile[tileNum].getImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);

	        	// --- DEBUG: draw tile number on top ---
	        	g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent background
	        	g2.fillRect(screenX, screenY, 24, 16);

	        	g2.setColor(Color.WHITE);
	        	g2.drawString(
	        	    String.valueOf(tileNum),
	        	    screenX + 4,
	        	    screenY + 12
	        	);

	        }


	        worldCol++;

	        if (worldCol == gp.maxWorldCol) {
	            worldCol = 0;
	            worldRow++;
	        }
	    }

	}

}
