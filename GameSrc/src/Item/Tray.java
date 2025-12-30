package Item;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class Tray extends Throwable {

    public Tray(gamePanel gp) {
        super(gp);
        name = "Tray";
        allowedRadiusTiles = 3;     
        throwSoundIndex = 7;         
        sizeX = 32;
        sizeY = 32;
         try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/items/Plate Nachos.png")), sizeX, sizeY);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
