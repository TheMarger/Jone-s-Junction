package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class FuseRepairTask extends Task {
	
	public FuseRepairTask(gamePanel gp) {
		super(gp);
		name = "Fuse Repair Task";
		description = "Connect the wires to repair the fuse.";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/fuseRepairTask.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
