package Item;

import main.gamePanel;

public class greenKey extends Key {
	public greenKey(gamePanel gp) {
		super(gp);
		name = "Green Key";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/greenKey.png"));
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}
