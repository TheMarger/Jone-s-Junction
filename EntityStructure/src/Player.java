
public class Player extends Entity {

	GamePanel gp;
	
    private int stamina;
    private int maxStamina = 5;   

    private item[] inventory;
    private int selectedInvIndex;

	private int worldX;

	private int worldY;

	//Constructor
    public Player(int tileX, int tileY, Direction facing) {
        super(tileX, tileY, facing);
        this.type = "Player";
    }
    
    //Sound 
    public int getCurrentNoiseValue() {
        return 0;
    }
    
    //Stamina usage
    public void consumeStaminaPerSecond() {
        
    }

    public void regenStaminaTick(boolean isCrouchingOrStanding) {
        
    }

  
    public void setDefaultValues() {

        
    }
    
    public int getStamina() {
        return stamina;
    }

    public void setStamina(int s) {
        stamina = s;
    }

    public item[] getInventory() {
        return inventory;
    }

    public void setInventory(item[] inventory) {
        this.inventory = inventory;
    }

    public int getSelectedInvIndex() {
        return selectedInvIndex;
    }

    public void setSelectedInvIndex(int index) {
        this.selectedInvIndex = index;
    }
}


