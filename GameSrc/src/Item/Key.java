package Item;

import main.gamePanel;

public class Key extends Item {

	gamePanel gp;
	
	public Key(gamePanel gp) {
		name = "Key";
		sizeX = 24;
		sizeY = 24;
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/key.png"));
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
