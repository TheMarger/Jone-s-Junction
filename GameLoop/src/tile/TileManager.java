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
	tile[] tile;
	int mapTileNum[][];
	
	public TileManager(gamePanel gp) {
		this.gp = gp;
		tile = new tile[232];
		mapTileNum = new int[gp.maxScreenCol][gp.maxScreenCol];
		getTileImage();
		loadMap("/maps/FinalLevel1Map.txt");
	}
	
	public void getTileImage() {
		try {
            
            InputStream is = getClass().getResourceAsStream("/FinalTileData.txt");

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
	
	public void loadMap(String filePath) {
		try {
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
				String line = br.readLine();
				while (col < gp.maxScreenCol) {
					String numbers[] = line.split(" ");
					int num = Integer.parseInt(numbers[col]);
					mapTileNum[col][row] = num;
					col++;
				}
				if (col == gp.maxScreenCol) {
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
		int col = 0;
		int row = 0;
		int x = 0;
		int y = 0;
		
		while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
			int tileNum = mapTileNum[col][row];
			if (tile[tileNum] != null && tile[tileNum].getImage() != null) {
			    g2.drawImage(tile[tileNum].getImage(), x, y, gp.tileSize, gp.tileSize, null);
			} else {
			    // draw a placeholder so you can see missing tiles
			    g2.setColor(Color.MAGENTA);
			    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
			}

			col++;
			x += gp.tileSize;
			if (col == gp.maxScreenCol) {
				col = 0;
				x = 0;
				row++;
				y += gp.tileSize;
			}
		}
	}
}
