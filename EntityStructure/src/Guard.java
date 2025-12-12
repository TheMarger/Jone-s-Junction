public class Guard extends Entity {

    private Tile currentSoundSource;

    private int fovRange;
    private boolean active;
    private PatrolRoute patrolRoute;
    private GuardType guardType;
    private Tile[][] grid;

    private static final float SPEED_REGULAR = 1.0f;
    private static final float SPEED_RECT = 1.5f;
    private static final float SPEED_NARROW = 2.0f;

    public Guard(int tileX, int tileY, Direction facing, GuardType type, Tile[][] grid, PatrolRoute patrolRoute) {

        super(tileX, tileY, facing);

        this.guardType = type;
        this.grid = grid;
        this.patrolRoute = patrolRoute;
        this.type = "Guard";
        this.active = true;

        switch (guardType) {
            case REGULAR:
                this.speed = SPEED_REGULAR;
                break;
            case RECTANGULAR:
                this.speed = SPEED_RECT;
                break;
            case NARROW:
                this.speed = SPEED_NARROW;
                break;
        }
    }

	 public boolean Fov(Player p) {

	        int dx = p.tileX - tileX;
	        int dy = p.tileY - tileY;

	        switch (guardType) {
	            case REGULAR:
	                return Math.abs(dx) <= 1 && Math.abs(dy) <= 1;
	            case RECTANGULAR:
	                return isInFront(dx, dy, 3, 1);
	            case NARROW:
	                return isInFront(dx, dy, 4, 0);
	        }

	        return false;
	    }

	    private boolean isInFront(int dx, int dy, int length, int halfWidth) {

	        switch (facing) {
	            case NORTH:
	                return Math.abs(dx) <= halfWidth && dy < 0 && -dy <= length;
	            case SOUTH:
	                return Math.abs(dx) <= halfWidth && dy > 0 && dy <= length;
	            case EAST:
	                return Math.abs(dy) <= halfWidth && dx > 0 && dx <= length;
	            case WEST:
	                return Math.abs(dy) <= halfWidth && dx < 0 && -dx <= length;
	        }

	        return false;
	    }
	    public boolean detectSound(Tile soundSource, int radius) {
	        return false;
	    }

	    public boolean reactToThrownItem(Tile itemTile, item thrown) {
	        return false;
	    }

	    public boolean getActive() {
	        return active;
	    }

	    public void setActive(boolean active) {
	        this.active = active;
	    }
	}
