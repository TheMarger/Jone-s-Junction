package Item;

public class redKey extends Key {
	public redKey() {
		name = "Red Key";
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/redKey.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
