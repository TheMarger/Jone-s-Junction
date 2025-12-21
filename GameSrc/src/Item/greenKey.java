package Item;

public class greenKey extends Key {
	public greenKey() {
		name = "Green Key";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/greenKey.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}
