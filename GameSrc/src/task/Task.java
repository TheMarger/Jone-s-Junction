package task;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.gamePanel;

public class Task {
	gamePanel gp;
	protected String description = "Generic Task Description";
	private boolean isCompleted;
	public String name = "Generic Task";
	BufferedImage image;
	public boolean collision = false;
	public int worldX, worldY;
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
	public int solidAreaDefaultX = 0;
	public int solidAreaDefaultY = 0;
	public int sizeX = 48;
	public int sizeY = 48;
	public int pickupDelay = 0;
	
	
	public Task(gamePanel gp) {
		this.gp = gp;
		isCompleted = false;
	}
	

	public void draw(Graphics2D g2) {
	    int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
	    int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
	    
	    if (worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
	        worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
	        worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
	        worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY()) {
	        
	        g2.drawImage(image, screenX, screenY, sizeX, sizeY, null);
	    }
	}

	public String getDescription() {
		return description;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return (isCompleted ? "[X] " : "[ ] ") + description;
	}

}
