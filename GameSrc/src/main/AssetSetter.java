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
import task.TileSelectTask;
import task.VaultSequenceTask;
import entity.*;

public class AssetSetter {

	gamePanel gp;
	
	public AssetSetter(gamePanel gp) {
		this.gp = gp;
	}
	
	
	int[][][] TaskLocations = new int[][][] {
		// Level 1 Tasks
		{ {62,2550}, {1563,1715}, {2434,1015}, {1921,893} },
		// Level 2 Tasks
		{ {2948,1019}, {2431,124}, {311,1905}, {930,2765}, {2811,3049}, {958,825} },
		// Level 3 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26} },
		// Level 4 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26}, {22,28}, {24,30} }
	};
	
	int[][][] ItemLocations = new int[][][] {
		// yellow key, red key, green key, flashlight, blue key, pebble, can, tray, apple, bread, protein bar
		
		// Level 1 Items
		{ {2879,2138}, {1535,1714}, {67,2234}, {2691,2598}, {1455,2826}, {2560,1968}, {1404,1836}, {1404,1836}, {2304,2524}, {1220,1700}, {1728,1112} },
		// Level 2 Tasks
		{ {2948,1019}, {2431,124}, {311,1905}, {930,2765}, {2811,3049}, {958,825} },
		// Level 3 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26} },
		// Level 4 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26}, {22,28}, {24,30} }
		
	};
	
	int[][][] NPCLocations = new int[][][] {	
		// Billy Goat, Old Man Jone
		// Level 1 NPCS
		{ {384, 960}, {1792,704}},
		// Level 2 NPCS
		{ {1665,1023}, {1733,187}},
		// Level 3 Tasks
		{ {790,123}, {2563,2795}},
		// Level 4 Tasks
		{ {2166,1911}, {187,1963}}
		
	};
	
	int[][][] GaurdLocations = new int[][][] {		
		// Level 1 Items
		{ {62,2550}, {1563,1715}, {2434,1015}, {1921,893} },
		// Level 2 Tasks
		{ {2948,1019}, {2431,124}, {311,1905}, {930,2765}, {2811,3049}, {958,825} },
		// Level 3 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26} },
		// Level 4 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26}, {22,28}, {24,30} }
		
	};
	
	
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
		if (gp.level == 1) {
			gp.npc[1] = new BillyGoat(gp);
			gp.npc[1].worldX = NPCLocations[0][0][0];
			gp.npc[1].worldY = NPCLocations[0][0][1];
			
			gp.npc[0] = new OldManJone(gp);
			gp.npc[0].worldX = NPCLocations[0][1][0];
			gp.npc[0].worldY = NPCLocations[0][1][1];
		}
		if (gp.level == 2) {
			gp.npc[1] = new BillyGoat(gp);
			gp.npc[1].worldX = NPCLocations[1][0][0];
			gp.npc[1].worldY = NPCLocations[1][0][1];
			
			gp.npc[0] = new OldManJone(gp);
			gp.npc[0].worldX = NPCLocations[1][1][0];
			gp.npc[0].worldY = NPCLocations[1][1][1];
		}
		if (gp.level == 3) {
			gp.npc[1] = new BillyGoat(gp);
			gp.npc[1].worldX = NPCLocations[2][0][0];
			gp.npc[1].worldY = NPCLocations[2][0][1];
			
			gp.npc[0] = new OldManJone(gp);
			gp.npc[0].worldX = NPCLocations[2][1][0];
			gp.npc[0].worldY = NPCLocations[2][1][1];
		}
		if (gp.level == 4) {
			gp.npc[1] = new BillyGoat(gp);
			gp.npc[1].worldX = NPCLocations[3][0][0];
			gp.npc[1].worldY = NPCLocations[3][0][1];
			
			gp.npc[0] = new OldManJone(gp);
			gp.npc[0].worldX = NPCLocations[3][1][0];
			gp.npc[0].worldY = NPCLocations[3][1][1];
		}
		
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

            int choice = random.nextInt(9); // number of task types
            
            while (true) {
				boolean alreadyAssigned = false;
				for (Task t : gp.player.tasksList) {
					if ((choice == 0 && t instanceof MathTask) ||
						(choice == 1 && t instanceof VaultSequenceTask) ||
						(choice == 2 && t instanceof CookingTask) ||
						(choice == 3 && t instanceof ButtonTask) ||
						(choice == 4 && t instanceof LogicPanelTask) ||
						(choice == 5 && t instanceof RiddleTask) ||
						(choice == 6 && t instanceof FuseRepairTask) ||
						(choice == 7 && t instanceof TileSelectTask)) {
						alreadyAssigned = true;
						break;
					}
				}
				if (!alreadyAssigned) {
					break; // unique task type found
				}
				choice = random.nextInt(9); // pick another
			}
            

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
				task = new TileSelectTask(gp);
			}
			else if (choice == 7) {
				task = new LogicPanelTask(gp);
			} 
			else if (choice == 8) {
				task = new FuseRepairTask(gp);
			}
            choice = random.nextInt(TaskLocations[gp.level - 1].length);
            
            // check if location is already taken
            while (true) {
            	boolean locationTaken = false;
            	int x = TaskLocations[gp.level - 1][choice][0];
            	int y = TaskLocations[gp.level - 1][choice][1];
            	for (Task t : gp.player.tasksList) {
					if (t.worldX == x && t.worldY == y) {
						locationTaken = true;
						break;
					}
				}
            	if (!locationTaken) {
					break; // unique location found
				}
				choice = random.nextInt(TaskLocations[gp.level - 1].length);
            }
            
            int x = TaskLocations[gp.level - 1][choice][0];
            int y = TaskLocations[gp.level - 1][choice][1];

            if (task != null) {
                gp.player.tasksList.add(task);
                gp.tasks[i] = task; 
                gp.tasks[i].worldX = x; // example positioning
                gp.tasks[i].worldY = y;
                System.out.println("Added task: " + task.getName() + " at X: " + gp.tasks[i].worldX + " Y: " + gp.tasks[i].worldY);
                
            }
        }
    }
}
