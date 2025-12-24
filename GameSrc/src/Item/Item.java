package Item;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.UtilityTool;
import main.gamePanel;

public class Item {
	
	public BufferedImage image;
	UtilityTool uTool = new UtilityTool();
	public String name;
	public boolean collision = false;
	public int worldX, worldY;
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
	public int solidAreaDefaultX = 0;
	public int solidAreaDefaultY = 0;
	public int sizeX = 48;
	public int sizeY = 48;
	
	public void draw(Graphics2D g2, gamePanel gp) {
	    int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
	    int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
	    
	    if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
	        worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
	        worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
	        worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {
	        
	        g2.drawImage(image, screenX, screenY, sizeX, sizeY, null);
	    }
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
