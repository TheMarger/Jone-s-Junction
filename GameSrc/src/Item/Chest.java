package Item;

public class Chest extends Item {
	
	public Chest() {
		name = "Chest";
		collision = true;
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/chest.png"));
			collision = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
