package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class MathTask extends Task {
		
	public MathTask(gamePanel gp) {
		super(gp);
		name = "Math Task";
		description = "Solve the math problem to complete the task!";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/mathQuestionTask.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
