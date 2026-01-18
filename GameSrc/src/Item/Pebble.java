package Item;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class Pebble extends Throwable {

    public Pebble(gamePanel gp) {
        super(gp);
        name = "Pebble";
        allowedRadiusTiles = 6;     
        throwSoundIndex = 3;         
        sizeX = 28;
        sizeY = 28;
         try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/items/pebble.png")), sizeX, sizeY);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
