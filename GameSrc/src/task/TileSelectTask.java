package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class TileSelectTask extends Task {
		
	public TileSelectTask(gamePanel gp) {
		super(gp);
		name = "Tile Select Task";
		description = "Select the correct tile to complete the task!";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/tileSelectTask.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
