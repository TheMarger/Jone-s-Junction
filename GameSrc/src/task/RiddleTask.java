package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class RiddleTask extends Task {
	public RiddleTask(gamePanel gp) {
		super(gp);
		name = "Riddle Task";
		description = "Solve the riddle to unlock the secret!";
		try {
			image = gp.uTool.scaleImage(
				ImageIO.read(getClass().getResourceAsStream("/tasks/singleRiddleTask.png")),
				gp.tileSize,
				gp.tileSize
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
