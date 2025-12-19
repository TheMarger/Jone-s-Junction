package Item;

public class Door extends Item {
	
	public Door() {
		name = "Door";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/door.png"));
			collision = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
