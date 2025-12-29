package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class LogicPanelTask extends Task {

	public LogicPanelTask(gamePanel gp) {
		super(gp);
		name = "Logic Panel Task";
		description = "Solve the logic puzzle to unlock the panel!";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/logicPanel.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
