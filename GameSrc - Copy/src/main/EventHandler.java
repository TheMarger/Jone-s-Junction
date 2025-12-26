package main;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import entity.entity;

public class EventHandler {
	
	gamePanel gp;
	EventRect eventRect[][];
	
	public EventHandler(gamePanel gp) {
	    this.gp = gp;
	    eventRect = new EventRect[gp.maxWorldCol][gp.maxWorldRow];

	    int col = 0;
	    int row = 0;

	    while (col < gp.maxWorldCol && row < gp.maxWorldRow) {

	        eventRect[col][row] = new EventRect();

	        int size = gp.tileSize / 2;
	        int offset = (gp.tileSize - size) / 2; // tileSize / 4

	        eventRect[col][row].x = offset;
	        eventRect[col][row].y = offset;
	        eventRect[col][row].width = size;
	        eventRect[col][row].height = size;

	        col++;
	        if (col == gp.maxWorldCol) {
	            col = 0;
	            row++;
	        }
	    }
	}

	
	public void checkEvent() {

	    // Tile event
	    /*if (hitTile(6, 19, "any")) {
	        playerDied();
	    }*/
	}

	
	public boolean hitTile(int col, int row, String reqDirection) {

	    // Player world hitbox (temporary)
	    Rectangle playerArea = new Rectangle(
	        gp.player.worldX + gp.player.solidArea.x,
	        gp.player.worldY + gp.player.solidArea.y,
	        gp.player.solidArea.width,
	        gp.player.solidArea.height
	    );

	    // Event world hitbox (temporary)
	    Rectangle eventArea = new Rectangle(
	        col * gp.tileSize + eventRect[col][row].x,
	        row * gp.tileSize + eventRect[col][row].y,
	        eventRect[col][row].width,
	        eventRect[col][row].height
	    );

	    if (playerArea.intersects(eventArea)) {
	        return reqDirection.equals("any") ||
	               gp.player.direction.equals(reqDirection);
	    }

	    return false;
	}

	public void playerDied() {
		gp.gameState = gp.deathState;
		System.out.println("You Died!");
		gp.playSoundEffect(4);
		gp.stopMusic();
	}
	


}
