package main;

public class AssetSetter {

	gamePanel gp;
	
	public AssetSetter(gamePanel gp) {
		this.gp = gp;
	}
	
	public void setItem() {
		gp.items[0] = new Item.redKey();
		gp.items[0].worldX = gp.tileSize * 6;
		gp.items[0].worldY = gp.tileSize * 17;
		
		gp.items[1] = new Item.Key();
		gp.items[1].worldX = gp.tileSize * 7;
		gp.items[1].worldY = gp.tileSize * 21;
		
		gp.items[2] = new Item.greenKey();
		gp.items[2].worldX = gp.tileSize * 12;
		gp.items[2].worldY = gp.tileSize * 19;
		
		gp.items[3] = new Item.Door();
		gp.items[3].worldX = gp.tileSize * 10;
		gp.items[3].worldY = gp.tileSize * 21;
		
		gp.items[4] = new Item.Flashlight();
		gp.items[4].worldX = gp.tileSize * 8;
		gp.items[4].worldY = gp.tileSize * 20;
	}
}
