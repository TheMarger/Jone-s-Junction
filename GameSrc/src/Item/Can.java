package Item;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class Can extends Throwable {

    public Can(gamePanel gp) {
        super(gp);
        name = "Can";
        allowedRadiusTiles = 4;     
        throwSoundIndex = 5;         
        sizeX = 28;
        sizeY = 28;
         try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/items/Soda Can.png")), sizeX, sizeY);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
