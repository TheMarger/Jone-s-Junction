package Item;

import main.gamePanel;

public class redKey extends Key {
	public redKey(gamePanel gp) {
		super(gp);
		name = "Red Key";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/redKey.png"));
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
