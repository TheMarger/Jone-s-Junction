package main;

import Item.*;
import gaurd.*;
import entity.*;

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
	
	// in class AssetSetter
	public void placeItem(Item item, int worldX, int worldY) {
	    int slot = gp.getEmptyItemSlot();
	    if (slot == -1) {
	        // no room in world item array
	        System.out.println("No empty item slot in world to place item: " + item.getName());
	        return;
	    }

	    // set the item's world position and put it into the world slot
	    item.worldX = worldX;
	    item.worldY = worldY;
	    gp.items[slot] = item;
	}

	
	public void setItem() {
	    int slot;

	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new redKey(gp);
	        it.worldX = gp.tileSize * 6;
	        it.worldY = gp.tileSize * 17;
	        gp.items[slot] = it;
	    }

	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Key(gp);
	        it.worldX = gp.tileSize * 7;
	        it.worldY = gp.tileSize * 21;
	        gp.items[slot] = it;
	    }

	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new greenKey(gp);
	        it.worldX = gp.tileSize * 12;
	        it.worldY = gp.tileSize * 19;
	        gp.items[slot] = it;
	    }

	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Flashlight(gp);
	        it.worldX = gp.tileSize * 8;
	        it.worldY = gp.tileSize * 20;
	        gp.items[slot] = it;
	    }

	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new blueKey(gp);
	        it.worldX = gp.tileSize * 14;
	        it.worldY = gp.tileSize * 16;
	        gp.items[slot] = it;
	    }
	}

	
	public void setNPC() {
		gp.npc[0] = new OldManJone(gp);
		gp.npc[0].worldX = gp.tileSize * 28;
		gp.npc[0].worldY = gp.tileSize * 11;
		
		gp.npc[1] = new BillyGoat(gp);
		gp.npc[1].worldX = gp.tileSize * 6;
		gp.npc[1].worldY = gp.tileSize * 15;
		
		gp.npc[2] = new OldManJone(gp);
		gp.npc[2].worldX = gp.tileSize * 1;
		gp.npc[2].worldY = gp.tileSize * 40;
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
