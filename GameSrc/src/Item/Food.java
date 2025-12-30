package Item;

import main.gamePanel;

public class Food extends Pebble {
	float restoreValue;
	
	public Food(gamePanel gp) {
		super(gp);
	}
	
	public float getRestoreValue() {
		return restoreValue;
	}
	
}
