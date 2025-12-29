package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class ButtonTask extends Task {
	
	public ButtonTask(gamePanel gp) {
		super(gp);
		name = "Button Task";
		description = "press the button to complete the task.";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/buttonMatch.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
