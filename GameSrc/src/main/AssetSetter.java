package main;

import Item.*;
import gaurd.*;
import task.ButtonTask;
import task.CookingTask;
import task.FuseRepairTask;
import task.LogicPanelTask;
import task.MathTask;
import task.RiddleTask;
import task.Task;
import task.VaultSequenceTask;
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
		setTasks();
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
	    
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Pebble(gp);
	        it.worldX = gp.tileSize * 14;
	        it.worldY = gp.tileSize * 19;
	        gp.items[slot] = it;
	    }
	    
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Can(gp);
	        it.worldX = gp.tileSize * 10;
	        it.worldY = gp.tileSize * 20;
	        gp.items[slot] = it;
	    }
	    
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Tray(gp);
	        it.worldX = gp.tileSize * 11;
	        it.worldY = gp.tileSize * 20;
	        gp.items[slot] = it;
	    }
	    
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Apple(gp);
	        it.worldX = gp.tileSize * 12;
	        it.worldY = gp.tileSize * 20;
	        gp.items[slot] = it;
	    }
	    
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new Bread(gp);
	        it.worldX = gp.tileSize * 13;
	        it.worldY = gp.tileSize * 20;
	        gp.items[slot] = it;
	    }
	    
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1) {
	        Item it = new ProteinBar(gp);
	        it.worldX = gp.tileSize * 14;
	        it.worldY = gp.tileSize * 20;
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
	
	public void setTasks() {

        gp.player.tasksList.clear(); // clear existing tasks
        for (int i = 0; i < gp.tasks.length; i++) {
			gp.tasks[i] = null; // clear gamePanel's task array
		}

        int tasksToAdd = 2;

        if (gp.level == 1) tasksToAdd = 2;
        else if (gp.level == 2) tasksToAdd = 4;
        else if (gp.level == 3) tasksToAdd = 6;
        else if (gp.level >= 4) tasksToAdd = 8;

        java.util.Random random = new java.util.Random();

        for (int i = 0; i < tasksToAdd; i++) {

            int choice = random.nextInt(8); // number of task types

            Task task = null;

            if (choice == 0) {
                task = new MathTask(gp);
            }
            else if (choice == 1) {
                task = new VaultSequenceTask(gp);
            }
            else if (choice == 2) {
                task = new CookingTask(gp);
            }
            else if (choice == 3) {
				task = new ButtonTask(gp);
			}
			else if (choice == 4) {
				task = new LogicPanelTask(gp);
			}
			else if (choice == 5) {
				task = new RiddleTask(gp);
			}
			else if (choice == 6) {
				task = new FuseRepairTask(gp);
			}
			else if (choice == 7) {
				task = new LogicPanelTask(gp);
			}

            if (task != null) {
                gp.player.tasksList.add(task);
                gp.tasks[i] = task; // also add to gamePanel's array for reference
                gp.tasks[i].worldX = gp.tileSize * (10 + i); // example positioning
                gp.tasks[i].worldY = gp.tileSize * (15 + i);
                System.out.println("Added task: " + task.getName() + " at X: " + gp.tasks[i].worldX + " Y: " + gp.tasks[i].worldY);
                
            }
        }
    }
}
