package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class CookingTask extends Task {

	public CookingTask(gamePanel gp) {
		super(gp);
		name = "Cooking Task";
		description = "Prepare a delicious meal by following the recipe!";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/cookingTask.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
