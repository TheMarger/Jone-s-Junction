package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
	
	public void draw(Graphics2D g2) {
		/*// Drawing utility method can be implemented here if needed
		// For debugging: draw event rectangles
	    for (int col = 0; col < gp.maxWorldCol; col++) {
    for (int row = 0; row < gp.maxWorldRow; row++) {
        int x = col * gp.tileSize + gp.eHandler.eventRect[col][row].x;
        int y = row * gp.tileSize + gp.eHandler.eventRect[col][row].y;

        // Only draw if within the visible screen area
        if (x + gp.eHandler.eventRect[col][row].width > gp.player.worldX - gp.player.getScreenX() &&
            x - gp.eHandler.eventRect[col][row].width < gp.player.worldX + gp.player.getScreenX() &&
            y + gp.eHandler.eventRect[col][row].height > gp.player.worldY - gp.player.getScreenY() &&
            y - gp.eHandler.eventRect[col][row].height < gp.player.worldY + gp.player.getScreenY()) {

            g2.setColor(new java.awt.Color(255, 0, 0, 150)); // semi-transparent red
            g2.fillRect(
                x - gp.player.worldX + gp.player.getScreenX(),
                y - gp.player.worldY + gp.player.getScreenY(),
                gp.eHandler.eventRect[col][row].width,
                gp.eHandler.eventRect[col][row].height
            );
        }
    }
   }*/
	}
}
