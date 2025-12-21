package Item;

public class Flashlight extends Item {
	public Flashlight() {
		name = "Flashlight";
		sizeX = 24;
		sizeY = 24;
		try {
			image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/items/Torch.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}
