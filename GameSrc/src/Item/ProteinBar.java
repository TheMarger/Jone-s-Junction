package Item;

import main.gamePanel;

public class ProteinBar extends Food {
	
	public ProteinBar(gamePanel gp) {
		super(gp);
		name = "Protein Bar";
		allowedRadiusTiles = 5;     
		throwSoundIndex = 3;         
		sizeX = 32;
		sizeY = 32;
		restoreValue = 1f;
		 try {
			image = gp.uTool.scaleImage(javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/Chocolate Bar.png")), sizeX, sizeY);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
