
public class Entity {

	protected String type;
	protected int tileX;     
	protected int tileY;    
	protected Direction facing; 
	protected String name;
	protected float speed;
	protected boolean isCrouching;
	protected boolean isSprinting;
	     
	public Entity(int tileX, int tileY, Direction facing) {
		 this.tileX = tileX;
	     this.tileY = tileY;
	     this.facing = facing;
    }
	 public int getTileX() { 
		 return tileX; 
	}
	 
	 public int getTileY() { 
		 return tileY; 
	}

    public void setTilePosition(int x, int y) {
        this.tileX = x;
        this.tileY = y;
    }

    public Direction getFacing() { 
    	return facing; 
    	}
    
    public void setFacing(Direction facing) { 
    	this.facing = facing; 
    	}
    
    public void moveNorth() {
        tileY--;
        facing = Direction.NORTH;
    }

    public void moveSouth() {
        tileY++;
        facing = Direction.SOUTH;
    }

    public void moveEast() {
        tileX++;
        facing = Direction.EAST;
    }

    public void moveWest() {
        tileX--;
        facing = Direction.WEST;
    }
    

    public float getSpeed() { 
    	return speed;
    	} 
    
    public void setSpeed(float speed) { 
    	this.speed = speed; 
    	}
    

    public boolean isCrouching() {
    	return isCrouching; 
    	}
    
    public void setCrouching(boolean crouch) { 
    	isCrouching = crouch; 
    	}
    

    public boolean isSprinting() {
    	return isSprinting; 
    	}
    
    public void setSprinting(boolean sprint) { 
    	isSprinting = sprint; 
    	}
}

