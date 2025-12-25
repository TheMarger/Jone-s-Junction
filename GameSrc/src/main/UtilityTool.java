package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import entity.entity;
import main.*;

public class UtilityTool {
	
	gamePanel gp;
	
	public UtilityTool(gamePanel gp) {
		this.gp = gp;
		
	}
	
	public BufferedImage scaleImage(BufferedImage original, int width, int height) {
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.drawImage(original, 0, 0, width, height, null);
		g2d.dispose();
		return scaledImage;
	}
	
	public void drawEntityHitbox(Graphics2D g2, entity e) {

	    // Convert world position to screen position
	    int screenX = e.worldX - gp.player.worldX + gp.player.getScreenX();
	    int screenY = e.worldY - gp.player.worldY + gp.player.getScreenY();

	    // Hitbox position (solidArea is relative to entity)
	    int hitboxX = screenX + e.solidArea.x;
	    int hitboxY = screenY + e.solidArea.y;

	    g2.setColor(java.awt.Color.RED);
	    g2.drawRect(hitboxX, hitboxY, e.solidArea.width, e.solidArea.height);
	}
	
	public void highlightTile(Graphics2D g2, int col, int row) {
		// highlight tile at certain position for testing
	    int testCol = col;
	    int testRow = row;
	    int testWorldX = testCol * gp.tileSize;
	    int testWorldY = testRow * gp.tileSize;
	    int testScreenX = testWorldX - gp.player.worldX + gp.player.getScreenX();
	    int testScreenY = testWorldY - gp.player.worldY + gp.player.getScreenY();
	    g2.setColor(new Color(255, 0, 0, 100));
	    g2.fillRect(testScreenX, testScreenY, gp.tileSize, gp.tileSize);
	}

	public void showTileNumber(Graphics2D g2, int screenX, int screenY, int tileNum) {
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
	
	public void showPlayerPosition(Graphics2D g2, int worldX, int worldY, int row, int col) {
		String positionText = "WorldX: " + worldX + " WorldY: " + worldY + " Row: " + row + " Col: " + col;
	    g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent background
	    g2.fillRect(10, 10, 300, 20);

	    g2.setColor(Color.WHITE);
	    g2.drawString(positionText, 15, 25);
	}
}
