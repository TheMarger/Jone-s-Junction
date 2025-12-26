package Item;

import main.gamePanel;

public class Chest extends Item {
	gamePanel gp;
	public Chest(gamePanel gp) {
		super(gp);
		name = "Chest";
		collision = true;
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/chest.png"));
			collision = true;
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
