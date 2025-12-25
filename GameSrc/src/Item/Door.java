package Item;

import entity.entity;
import main.gamePanel;

public class Door extends Item {
	gamePanel gp;
	public Door(gamePanel gp) {
		super(gp);
		name = "Door";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/door.png"));
			collision = true;
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
