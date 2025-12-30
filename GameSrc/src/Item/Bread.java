package Item;

import main.gamePanel;

public class Bread extends Food {
	
	public Bread(gamePanel gp) {
		super(gp);
		name = "Bread";
		allowedRadiusTiles = 5;     
		throwSoundIndex = 3;         
		sizeX = 32;
		sizeY = 32;
		restoreValue = 0.5f;
		 try {
			image = gp.uTool.scaleImage(javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/Bread.png")), sizeX, sizeY);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
