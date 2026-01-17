package task;

public class PatternSwitchesTask extends Task {
		
	public PatternSwitchesTask(main.gamePanel gp) {
		super(gp);
		name = "Pattern Switches Task";
		description = "Activate the switches in the correct pattern to complete the task!";
		try {
			image = gp.uTool.scaleImage(javax.imageio.ImageIO.read(getClass().getResourceAsStream("/tasks/patternSwitchesTask.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
