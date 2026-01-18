package main;

import java.util.ArrayList;

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
import task.PatternSwitchesTask;
import entity.*;

public class AssetSetter {

	gamePanel gp;
	java.util.Random random = new java.util.Random();
	
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
		//     0          1            2             3            4          5        6      7        8        9          10
		// red key,   yellow key,   green key,   flashlight,   blue key,   pebble,   can,   tray,   apple,   bread,   protein bar
		
		// Level 1 Items
        {{1815,1869}, {2899,2175},   {67,2234},  {2699,2620},  {1455,2826}, {2560,1968}, {1425,2170}, {1415,1871}, {2325,2557}, {1230,1745}, {1747,1155} },
        // Level 2 Items
        { {340,1974}, {725,959}, {114,2055}, {2803,1400}, {2500,3078}, {1359,1860}, {2780,2436}, {2379,2819}, {2769,450}, {2568,2819}, {1992,1160}},
        // Level 3 Items
		{ {2399,151}, {2900,2050}, {2950,1350}, {2600,3075}, {1900,1114}, {1950, 781}, {340, 1465}, {1670,1294}, {2630, 2765}, {1280,1260}, {335, 1980}, },
		// Level 4 Items
		{ {2835,2070}, {432,2679}, {1950,510}, {1790,640}, {996,2070}, {1610,2640}, {1987,2690}, {972,1360}, {920,2765}, {1239,1524}, {2178,1220} }
		
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
		{ {1860,672}, {1834,883}, {762,2523}, {66,2251}, {1222,1703}, {2298,2535}, {2690,2619}, {2874,2143}, {2314,1711}, {450,2039}, {1726,2147} },
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
	
	public void setThrowable() {

	    // ==================== HOW MANY TO SPAWN ====================
	    int amount = 0;
	    switch (gp.level) {
	        case 1 -> amount = random.nextInt(2, 4); // 2–3
	        case 2 -> amount = random.nextInt(3, 5); // 3–4
	        case 3 -> amount = random.nextInt(4, 6); // 4–5
	        case 4 -> amount = random.nextInt(5, 7); // 5–6
	    }

	    int levelIndex = gp.level - 1;
	    int[][] levelLocations = ItemLocations[levelIndex];

	    // ==================== AVAILABLE THROWABLE INDICES ====================
	    ArrayList<Integer> availableIndices = new ArrayList<>();
	    if (levelLocations.length > 5) availableIndices.add(5); // Pebble
	    if (levelLocations.length > 6) availableIndices.add(6); // Can
	    if (levelLocations.length > 7) availableIndices.add(7); // Tray

	    // ==================== SPAWN THROWABLES ====================
	    for (int i = 0; i < amount && !availableIndices.isEmpty(); i++) {

	        int slot = gp.getEmptyItemSlot();
	        if (slot == -1) break;

	        // Pick random throwable index (no reuse)
	        int idx = random.nextInt(availableIndices.size());
	        int itemIndex = availableIndices.remove(idx);

	        Item throwable;
	        switch (itemIndex) {
	            case 5 -> throwable = new Pebble(gp);
	            case 6 -> throwable = new Can(gp);
	            default -> throwable = new Tray(gp);
	        }

	        // Set position from predefined list
	        throwable.worldX = levelLocations[itemIndex][0];
	        throwable.worldY = levelLocations[itemIndex][1];

	        gp.items[slot] = throwable;
	    }
	}

	public void setItem() {

	    // ==================== THROWABLES FIRST ====================
	    setThrowable();

	    int levelIndex = gp.level - 1;
	    int[][] levelLocations = ItemLocations[levelIndex];
	    int slot;

	    // ==================== ITEM KIT (NON-THROWABLES) ====================

	    // 0 → Yellow Key
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 0) {
	        Item it = new redKey(gp);
	        it.worldX = levelLocations[0][0];
	        it.worldY = levelLocations[0][1];
	        gp.items[slot] = it;
	    }

	    // 1 → Key
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 1) {
	        Item it = new Key(gp);
	        it.worldX = levelLocations[1][0];
	        it.worldY = levelLocations[1][1];
	        gp.items[slot] = it;
	    }

	    // 2 → Green Key
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 2) {
	        Item it = new greenKey(gp);
	        it.worldX = levelLocations[2][0];
	        it.worldY = levelLocations[2][1];
	        gp.items[slot] = it;
	    }

	    // 3 → Flashlight
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 3) {
	        Item it = new Flashlight(gp);
	        it.worldX = levelLocations[3][0];
	        it.worldY = levelLocations[3][1];
	        gp.items[slot] = it;
	    }

	    // 4 → Blue Key
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 4) {
	        Item it = new blueKey(gp);
	        it.worldX = levelLocations[4][0];
	        it.worldY = levelLocations[4][1];
	        gp.items[slot] = it;
	    }

	    // ==================== CONSUMABLES ====================

	    // 8 → Apple
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 8) {
	        Item it = new Apple(gp);
	        it.worldX = levelLocations[8][0];
	        it.worldY = levelLocations[8][1];
	        gp.items[slot] = it;
	    }

	    // 9 → Bread
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 9) {
	        Item it = new Bread(gp);
	        it.worldX = levelLocations[9][0];
	        it.worldY = levelLocations[9][1];
	        gp.items[slot] = it;
	    }

	    // 10 → Protein Bar
	    slot = gp.getEmptyItemSlot();
	    if (slot != -1 && levelLocations.length > 10) {
	        Item it = new ProteinBar(gp);
	        it.worldX = levelLocations[10][0];
	        it.worldY = levelLocations[10][1];
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
	
	public void setGaurds() { // Declares the setGaurds method, allows it to take no parameters, and makes it return nothing (void)

	    if (gp.player.level == 1) // If the player is currently on level 1, this code runs
	    {
	        gp.gaurds[0] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 0
	        gp.gaurds[0].worldX = gp.tileSize * 8; // Sets guard 0's world X coordinate to tile 8 (in pixels)
	        gp.gaurds[0].worldY = gp.tileSize * 19; // Sets guard 0's world Y coordinate to tile 19 (in pixels)
	        gp.gaurds[0].spawnTileX = gp.gaurds[0].worldX / gp.tileSize; // Records guard 0's spawn tile X based on its world X
	        gp.gaurds[0].spawnTileY = gp.gaurds[0].worldY / gp.tileSize; // Records guard 0's spawn tile Y based on its world Y
	        gp.gaurds[0].setPatrolRouteFromSpawn(true, 4, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 0's patrol route from its spawn with given parameters

	        gp.gaurds[1] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 1
	        gp.gaurds[1].worldX = gp.tileSize * 30; // Sets guard 1's world X coordinate to tile 30 (in pixels)
	        gp.gaurds[1].worldY = gp.tileSize * 20; // Sets guard 1's world Y coordinate to tile 20 (in pixels)
	        gp.gaurds[1].spawnTileX = gp.gaurds[1].worldX / gp.tileSize; // Records guard 1's spawn tile X based on its world X
	        gp.gaurds[1].spawnTileY = gp.gaurds[1].worldY / gp.tileSize; // Records guard 1's spawn tile Y based on its world Y
	        gp.gaurds[1].setPatrolRouteFromSpawn(false, 3, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 1's patrol route from its spawn with given parameters

	        gp.gaurds[2] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 2
	        gp.gaurds[2].worldX = gp.tileSize * 15; // Sets guard 2's world X coordinate to tile 15 (in pixels)
	        gp.gaurds[2].worldY = gp.tileSize * 33; // Sets guard 2's world Y coordinate to tile 33 (in pixels)
	        gp.gaurds[2].spawnTileX = gp.gaurds[2].worldX / gp.tileSize; // Records guard 2's spawn tile X based on its world X
	        gp.gaurds[2].spawnTileY = gp.gaurds[2].worldY / gp.tileSize; // Records guard 2's spawn tile Y based on its world Y
	        gp.gaurds[2].setPatrolRouteFromSpawn(false, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 2's patrol route from its spawn with given parameters

	        gp.gaurds[3] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 3
	        gp.gaurds[3].worldX = gp.tileSize * 31; // Sets guard 3's world X coordinate to tile 31 (in pixels)
	        gp.gaurds[3].worldY = gp.tileSize * 34; // Sets guard 3's world Y coordinate to tile 34 (in pixels)
	        gp.gaurds[3].spawnTileX = gp.gaurds[3].worldX / gp.tileSize; // Records guard 3's spawn tile X based on its world X
	        gp.gaurds[3].spawnTileY = gp.gaurds[3].worldY / gp.tileSize; // Records guard 3's spawn tile Y based on its world Y
	        gp.gaurds[3].setPatrolRouteFromSpawn(false, 7, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 3's patrol route from its spawn with given parameters

	    }
	    else if (gp.player.level == 2) // If the player is currently on level 2, this code runs
	    {
	        gp.gaurds[0] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 0
	        gp.gaurds[0].worldX = gp.tileSize * 19; // Sets guard 0's world X coordinate to tile 19 (in pixels)
	        gp.gaurds[0].worldY = gp.tileSize * 35; // Sets guard 0's world Y coordinate to tile 35 (in pixels)
	        gp.gaurds[0].spawnTileX = gp.gaurds[0].worldX / gp.tileSize; // Records guard 0's spawn tile X based on its world X
	        gp.gaurds[0].spawnTileY = gp.gaurds[0].worldY / gp.tileSize; // Records guard 0's spawn tile Y based on its world Y
	        gp.gaurds[0].setPatrolRouteFromSpawn(true, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 0's patrol route from its spawn with given parameters

	        gp.gaurds[1] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 1
	        gp.gaurds[1].worldX = gp.tileSize * 39; // Sets guard 1's world X coordinate to tile 39 (in pixels)
	        gp.gaurds[1].worldY = gp.tileSize * 10; // Sets guard 1's world Y coordinate to tile 10 (in pixels)
	        gp.gaurds[1].spawnTileX = gp.gaurds[1].worldX / gp.tileSize; // Records guard 1's spawn tile X based on its world X
	        gp.gaurds[1].spawnTileY = gp.gaurds[1].worldY / gp.tileSize; // Records guard 1's spawn tile Y based on its world Y
	        gp.gaurds[1].setPatrolRouteFromSpawn(false, 4, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 1's patrol route from its spawn with given parameters

	        gp.gaurds[2] = new RectangularGuard(gp); // Creates a RectangularGuard instance and stores it in the guards array at index 2
	        gp.gaurds[2].worldX = gp.tileSize * 15; // Sets guard 2's world X coordinate to tile 15 (in pixels)
	        gp.gaurds[2].worldY = gp.tileSize * 26; // Sets guard 2's world Y coordinate to tile 26 (in pixels)
	        gp.gaurds[2].spawnTileX = gp.gaurds[2].worldX / gp.tileSize; // Records guard 2's spawn tile X based on its world X
	        gp.gaurds[2].spawnTileY = gp.gaurds[2].worldY / gp.tileSize; // Records guard 2's spawn tile Y based on its world Y
	        gp.gaurds[2].setPatrolRouteFromSpawn(false, 8, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 2's patrol route from its spawn with given parameters

	        gp.gaurds[3] = new RectangularGuard(gp); // Creates a RectangularGuard instance and stores it in the guards array at index 3
	        gp.gaurds[3].worldX = gp.tileSize * 35; // Sets guard 3's world X coordinate to tile 29 (in pixels)
	        gp.gaurds[3].worldY = gp.tileSize * 27; // Sets guard 3's world Y coordinate to tile 35 (in pixels)
	        gp.gaurds[3].spawnTileX = gp.gaurds[3].worldX / gp.tileSize; // Records guard 3's spawn tile X based on its world X
	        gp.gaurds[3].spawnTileY = gp.gaurds[3].worldY / gp.tileSize; // Records guard 3's spawn tile Y based on its world Y
	        gp.gaurds[3].setPatrolRouteFromSpawn(false, 3, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 3's patrol route from its spawn with given parameters
	    }
	    else if (gp.player.level == 3) // If the player is currently on level 3, this code runs
	    {
	        gp.gaurds[0] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 0
	        gp.gaurds[0].worldX = gp.tileSize * 11; // Sets guard 0's world X coordinate to tile 11 (in pixels)
	        gp.gaurds[0].worldY = gp.tileSize * 26; // Sets guard 0's world Y coordinate to tile 26 (in pixels)
	        gp.gaurds[0].spawnTileX = gp.gaurds[0].worldX / gp.tileSize; // Records guard 0's spawn tile X based on its world X
	        gp.gaurds[0].spawnTileY = gp.gaurds[0].worldY / gp.tileSize; // Records guard 0's spawn tile Y based on its world Y
	        gp.gaurds[0].setPatrolRouteFromSpawn(false, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 0's patrol route from its spawn with given parameters

	        gp.gaurds[1] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 1
	        gp.gaurds[1].worldX = gp.tileSize * 20; // Sets guard 1's world X coordinate to tile 20 (in pixels)
	        gp.gaurds[1].worldY = gp.tileSize * 10; // Sets guard 1's world Y coordinate to tile 10 (in pixels)
	        gp.gaurds[1].spawnTileX = gp.gaurds[1].worldX / gp.tileSize; // Records guard 1's spawn tile X based on its world X
	        gp.gaurds[1].spawnTileY = gp.gaurds[1].worldY / gp.tileSize; // Records guard 1's spawn tile Y based on its world Y
	        gp.gaurds[1].setPatrolRouteFromSpawn(true, 4, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 1's patrol route from its spawn with given parameters

	        gp.gaurds[2] = new RectangularGuard(gp); // Creates a RectangularGuard instance and stores it in the guards array at index 2
	        gp.gaurds[2].worldX = gp.tileSize * 35; // Sets guard 2's world X coordinate to tile 35 (in pixels)
	        gp.gaurds[2].worldY = gp.tileSize * 28; // Sets guard 2's world Y coordinate to tile 28 (in pixels)
	        gp.gaurds[2].spawnTileX = gp.gaurds[2].worldX / gp.tileSize; // Records guard 2's spawn tile X based on its world X
	        gp.gaurds[2].spawnTileY = gp.gaurds[2].worldY / gp.tileSize; // Records guard 2's spawn tile Y based on its world Y
	        gp.gaurds[2].setPatrolRouteFromSpawn(false, 4, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 2's patrol route from its spawn with given parameters

	        gp.gaurds[3] = new RectangularGuard(gp); // Creates a RectangularGuard instance and stores it in the guards array at index 3
	        gp.gaurds[3].worldX = gp.tileSize * 35; // Sets guard 3's world X coordinate to tile 35 (in pixels)
	        gp.gaurds[3].worldY = gp.tileSize * 38; // Sets guard 3's world Y coordinate to tile 38 (in pixels)
	        gp.gaurds[3].spawnTileX = gp.gaurds[3].worldX / gp.tileSize; // Records guard 3's spawn tile X based on its world X
	        gp.gaurds[3].spawnTileY = gp.gaurds[3].worldY / gp.tileSize; // Records guard 3's spawn tile Y based on its world Y
	        gp.gaurds[3].setPatrolRouteFromSpawn(false, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 3's patrol route from its spawn with given parameters

	        gp.gaurds[4] = new NarrowGuard(gp); // Creates a NarrowGuard instance and stores it in the guards array at index 4
	        gp.gaurds[4].worldX = gp.tileSize * 23; // Sets guard 4's world X coordinate to tile 23 (in pixels)
	        gp.gaurds[4].worldY = gp.tileSize * 35; // Sets guard 4's world Y coordinate to tile 35 (in pixels)
	        gp.gaurds[4].spawnTileX = gp.gaurds[4].worldX / gp.tileSize; // Records guard 4's spawn tile X based on its world X
	        gp.gaurds[4].spawnTileY = gp.gaurds[4].worldY / gp.tileSize; // Records guard 4's spawn tile Y based on its world Y
	        gp.gaurds[4].setPatrolRouteFromSpawn(false, 3, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 4's patrol route from its spawn with given parameters

	        gp.gaurds[5] = new NarrowGuard(gp); // Creates a NarrowGuard instance and stores it in the guards array at index 5
	        gp.gaurds[5].worldX = gp.tileSize * 30; // Sets guard 5's world X coordinate to tile 30 (in pixels)
	        gp.gaurds[5].worldY = gp.tileSize * 13; // Sets guard 5's world Y coordinate to tile 13 (in pixels)
	        gp.gaurds[5].spawnTileX = gp.gaurds[5].worldX / gp.tileSize; // Records guard 5's spawn tile X based on its world X
	        gp.gaurds[5].spawnTileY = gp.gaurds[5].worldY / gp.tileSize; // Records guard 5's spawn tile Y based on its world Y
	        gp.gaurds[5].setPatrolRouteFromSpawn(true, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 5's patrol route from its spawn with given parameters
	    }
	    else // For any other player level not explicitly handled above, this default configuration runs
	    {
	        gp.gaurds[0] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 0
	        gp.gaurds[0].worldX = gp.tileSize * 3; // Sets guard 0's world X coordinate to tile 3 (in pixels)
	        gp.gaurds[0].worldY = gp.tileSize * 3; // Sets guard 0's world Y coordinate to tile 3 (in pixels)
	        gp.gaurds[0].spawnTileX = gp.gaurds[0].worldX / gp.tileSize; // Records guard 0's spawn tile X based on its world X
	        gp.gaurds[0].spawnTileY = gp.gaurds[0].worldY / gp.tileSize; // Records guard 0's spawn tile Y based on its world Y
	        gp.gaurds[0].setPatrolRouteFromSpawn(false, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 0's patrol route from its spawn with given parameters

	        gp.gaurds[1] = new RegularGuard(gp); // Creates a RegularGuard instance and stores it in the guards array at index 1
	        gp.gaurds[1].worldX = gp.tileSize * 17; // Sets guard 1's world X coordinate to tile 17 (in pixels)
	        gp.gaurds[1].worldY = gp.tileSize * 15; // Sets guard 1's world Y coordinate to tile 15 (in pixels)
	        gp.gaurds[1].spawnTileX = gp.gaurds[1].worldX / gp.tileSize; // Records guard 1's spawn tile X based on its world X
	        gp.gaurds[1].spawnTileY = gp.gaurds[1].worldY / gp.tileSize; // Records guard 1's spawn tile Y based on its world Y
	        gp.gaurds[1].setPatrolRouteFromSpawn(true, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 1's patrol route from its spawn with given parameters

	        gp.gaurds[2] = new RectangularGuard(gp); // Creates a RectangularGuard instance and stores it in the guards array at index 2
	        gp.gaurds[2].worldX = gp.tileSize * 26; // Sets guard 2's world X coordinate to tile 26 (in pixels)
	        gp.gaurds[2].worldY = gp.tileSize * 18; // Sets guard 2's world Y coordinate to tile 18 (in pixels)
	        gp.gaurds[2].spawnTileX = gp.gaurds[2].worldX / gp.tileSize; // Records guard 2's spawn tile X based on its world X
	        gp.gaurds[2].spawnTileY = gp.gaurds[2].worldY / gp.tileSize; // Records guard 2's spawn tile Y based on its world Y
	        gp.gaurds[2].setPatrolRouteFromSpawn(false, 10, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 2's patrol route from its spawn with given parameters

	        gp.gaurds[3] = new RectangularGuard(gp); // Creates a RectangularGuard instance and stores it in the guards array at index 3
	        gp.gaurds[3].worldX = gp.tileSize * 12; // Sets guard 3's world X coordinate to tile 12 (in pixels)
	        gp.gaurds[3].worldY = gp.tileSize * 28; // Sets guard 3's world Y coordinate to tile 28 (in pixels)
	        gp.gaurds[3].spawnTileX = gp.gaurds[3].worldX / gp.tileSize; // Records guard 3's spawn tile X based on its world X
	        gp.gaurds[3].spawnTileY = gp.gaurds[3].worldY / gp.tileSize; // Records guard 3's spawn tile Y based on its world Y
	        gp.gaurds[3].setPatrolRouteFromSpawn(false, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 3's patrol route from its spawn with given parameters

	        gp.gaurds[4] = new NarrowGuard(gp); // Creates a NarrowGuard instance and stores it in the guards array at index 4
	        gp.gaurds[4].worldX = gp.tileSize * 39; // Sets guard 4's world X coordinate to tile 39 (in pixels)
	        gp.gaurds[4].worldY = gp.tileSize * 32; // Sets guard 4's world Y coordinate to tile 32 (in pixels)
	        gp.gaurds[4].spawnTileX = gp.gaurds[4].worldX / gp.tileSize; // Records guard 4's spawn tile X based on its world X
	        gp.gaurds[4].spawnTileY = gp.gaurds[4].worldY / gp.tileSize; // Records guard 4's spawn tile Y based on its world Y
	        gp.gaurds[4].setPatrolRouteFromSpawn(false, 7, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 4's patrol route from its spawn with given parameters

	        gp.gaurds[5] = new NarrowGuard(gp); // Creates a NarrowGuard instance and stores it in the guards array at index 5
	        gp.gaurds[5].worldX = gp.tileSize * 24; // Sets guard 5's world X coordinate to tile 24 (in pixels)
	        gp.gaurds[5].worldY = gp.tileSize * 12; // Sets guard 5's world Y coordinate to tile 12 (in pixels)
	        gp.gaurds[5].spawnTileX = gp.gaurds[5].worldX / gp.tileSize; // Records guard 5's spawn tile X based on its world X
	        gp.gaurds[5].spawnTileY = gp.gaurds[5].worldY / gp.tileSize; // Records guard 5's spawn tile Y based on its world Y
	        gp.gaurds[5].setPatrolRouteFromSpawn(true, 5, gp.maxWorldCol, gp.maxWorldRow); // Initializes guard 5's patrol route from its spawn with given parameters
	    }
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
						(choice == 7 && t instanceof TileSelectTask) ||
						(choice == 8 && t instanceof PatternSwitchesTask)) {
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
				task = new PatternSwitchesTask(gp);
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