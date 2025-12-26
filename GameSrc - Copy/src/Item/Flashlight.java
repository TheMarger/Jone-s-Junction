package Item;

import main.gamePanel;

public class Flashlight extends Item {
	gamePanel gp;
	public Flashlight(gamePanel gp) {
		super(gp);
		name = "Flashlight";
		sizeX = 24;
		sizeY = 24;
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/flashlight.png"));
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}
