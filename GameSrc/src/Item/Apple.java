package Item;

import main.gamePanel;

public class Apple extends Food {
	
	public Apple(gamePanel gp) {
		super(gp);
		name = "Apple";
		allowedRadiusTiles = 5;     
		throwSoundIndex = 3;         
		sizeX = 24;
		sizeY = 24;
		restoreValue = 0.25f;
		 try {
			image = gp.uTool.scaleImage(javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/Apple.png")), sizeX, sizeY);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
