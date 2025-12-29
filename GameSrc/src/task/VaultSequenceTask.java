package task;

import javax.imageio.ImageIO;

import main.gamePanel;

public class VaultSequenceTask extends Task {
		
	public VaultSequenceTask(gamePanel gp) {
		super(gp);
		name = "Vault Sequence Task";
		description = "Enter the correct sequence to unlock the vault!";
		try {
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/vaultSequence.png")), gp.tileSize, gp.tileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
