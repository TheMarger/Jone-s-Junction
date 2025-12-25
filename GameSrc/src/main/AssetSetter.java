package main;

import gaurd.gaurd;

public class AssetSetter {

	gamePanel gp;
	
	public AssetSetter(gamePanel gp) {
		this.gp = gp;
	}
	
	public void setAll() {
		setItem();
		setNPC();
		setGaurds();
	}
	
	public void setItem() {
		gp.items[0] = new Item.redKey(gp);
		gp.items[0].worldX = gp.tileSize * 6;
		gp.items[0].worldY = gp.tileSize * 17;
		
		gp.items[1] = new Item.Key(gp);
		gp.items[1].worldX = gp.tileSize * 7;
		gp.items[1].worldY = gp.tileSize * 21;
		
		gp.items[2] = new Item.greenKey(gp);
		gp.items[2].worldX = gp.tileSize * 12;
		gp.items[2].worldY = gp.tileSize * 19;
		
		gp.items[3] = new Item.Door(gp);
		gp.items[3].worldX = gp.tileSize * 10;
		gp.items[3].worldY = gp.tileSize * 21;
		
		gp.items[4] = new Item.Flashlight(gp);
		gp.items[4].worldX = gp.tileSize * 8;
		gp.items[4].worldY = gp.tileSize * 20;
		
		gp.items[5] = new Item.blueKey(gp);
		gp.items[5].worldX = gp.tileSize * 14;
		gp.items[5].worldY = gp.tileSize * 16;
	}
	
	public void setNPC() {
		gp.npc[0] = new entity.OldManJone(gp);
		gp.npc[0].worldX = gp.tileSize * 28;
		gp.npc[0].worldY = gp.tileSize * 11;
		
		gp.npc[1] = new entity.BillyGoat(gp);
		gp.npc[1].worldX = gp.tileSize * 6;
		gp.npc[1].worldY = gp.tileSize * 15;
	}
	
	public void setGaurds() {
		gp.gaurds[0] = new gaurd(gp);
		gp.gaurds[0].worldX = gp.tileSize * 8;
		gp.gaurds[0].worldY = gp.tileSize * 19;
		
		gp.gaurds[1] = new gaurd(gp);
		gp.gaurds[1].worldX = gp.tileSize * 8;
		gp.gaurds[1].worldY = gp.tileSize * 21;
		
	}
}
