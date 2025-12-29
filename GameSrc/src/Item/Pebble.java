package Item;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class Pebble extends Throwable {

    public Pebble(gamePanel gp) {
        super(gp);
        name = "Pebble";
        allowedRadiusTiles = 5;     // per your spec
        throwSoundIndex = 3;         // <-- map this to the correct sound index in your Sound system
        sizeX = 28;
        sizeY = 28;
        // image assignment if you have a sprite:
         try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/items/Sphere.png")), sizeX, sizeY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
