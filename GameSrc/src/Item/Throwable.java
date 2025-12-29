package Item;

import main.gamePanel;


public class Throwable extends Item {

    // How far the player may choose to throw, measured in tiles.
    public int allowedRadiusTiles = 8;

    // Sound index to play when thrown 
    protected int throwSoundIndex = -1;
    
    public int throwDelay = 0;

    public Throwable(gamePanel gp) {
        super(gp);
    }

    public void setAllowedRadiusTiles(int tiles) {
        this.allowedRadiusTiles = tiles;
    }

    public int getAllowedRadiusTiles() {
        return allowedRadiusTiles;
    }

    public void setThrowSoundIndex(int index) {
        this.throwSoundIndex = index;
    }

    public int getThrowSoundIndex() {
        return throwSoundIndex;
    }
    
    public void update() {
		if (pickupDelay > 0) {
			pickupDelay--;
		}
		if (throwDelay > 0) {
			throwDelay--;
		}
	}



}
