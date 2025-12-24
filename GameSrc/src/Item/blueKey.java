package Item;

import main.gamePanel;

public class blueKey extends Key {
		
	public blueKey(gamePanel gp) {
		super(gp);
		name = "Blue Key";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/blueKey.png"));
			uTool.scaleImage(image, sizeX, sizeY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
