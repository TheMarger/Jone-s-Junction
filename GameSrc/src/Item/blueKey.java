package Item;

public class blueKey extends Key {
	public blueKey() {
		name = "Blue Key";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/blueKey.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
