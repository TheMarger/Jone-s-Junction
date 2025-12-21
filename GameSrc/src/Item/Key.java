package Item;

public class Key extends Item {

	public Key() {
		name = "Key";
		sizeX = 24;
		sizeY = 24;
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/key.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
