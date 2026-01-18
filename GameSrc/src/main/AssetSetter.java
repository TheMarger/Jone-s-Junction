/*
 * Name: Rafay
 * Date: 1/19/2026
 * Course Code: ICS4U0
 * Description: AssetSetter class manages the placement and respawning of all game assets
 *              including items, NPCs, guards, and tasks across different levels. It handles
 *              the initialization of game entities at their designated world coordinates,
 *              manages food item respawn timers, and ensures proper distribution of throwable
 *              items and tasks throughout each level.
 */

package main; // Declares this class belongs to the main package

import java.util.ArrayList; // Imports ArrayList class for dynamic array functionality

import Item.*; // Imports all classes from the Item package
import gaurd.*; // Imports all classes from the gaurd package
import task.ButtonTask; // Imports the ButtonTask class
import task.CookingTask; // Imports the CookingTask class
import task.FuseRepairTask; // Imports the FuseRepairTask class
import task.LogicPanelTask; // Imports the LogicPanelTask class
import task.MathTask; // Imports the MathTask class
import task.RiddleTask; // Imports the RiddleTask class
import task.Task; // Imports the base Task class
import task.TileSelectTask; // Imports the TileSelectTask class
import task.VaultSequenceTask; // Imports the VaultSequenceTask class
import task.PatternSwitchesTask; // Imports the PatternSwitchesTask class
import entity.*; // Imports all classes from the entity package

public class AssetSetter { // Declares the public AssetSetter class
	
	// Respawn timers for food items (60 FPS * 60 seconds = 3600 frames)
    public int breadRespawnTimer = -1;  // Timer for bread respawn, -1 means not waiting to respawn
    public int appleRespawnTimer = -1; // Timer for apple respawn, -1 means not waiting to respawn
    public int breadRespawnX, breadRespawnY;  // Stores the X and Y coordinates where bread was picked up
    public int appleRespawnX, appleRespawnY; // Stores the X and Y coordinates where apple was picked up
    public static final int RESPAWN_TIME = 600; // Constant defining respawn time (600 frames = 10 seconds at 60 FPS)

	gamePanel gp; // Reference to the main gamePanel object
	java.util.Random random = new java.util.Random(); // Creates a Random object for generating random numbers
	
	public AssetSetter(gamePanel gp) { // Constructor that takes a gamePanel parameter
		this.gp = gp; // Assigns the gamePanel parameter to the class's gp field
	}
	
	
	int[][][] TaskLocations = new int[][][] { // Three-dimensional array storing task locations for each level
		// Level 1 Tasks
		{ {62,2550}, {1563,1715}, {2434,1015}, {1921,893} }, // Four task locations for level 1 with X,Y coordinates
		// Level 2 Tasks
		{ {2948,1019}, {2431,124}, {311,1905}, {930,2765}, {2811,3049}, {958,825} }, // Six task locations for level 2
		// Level 3 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26} }, // Six task locations for level 3
		// Level 4 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26}, {22,28}, {24,30} } // Eight task locations for level 4
	};
	
	int[][][] ItemLocations = new int[][][] { // Three-dimensional array storing item locations for each level
		//     0          1            2             3            4          5        6      7        8        9          10
		// red key,   yellow key,   green key,   flashlight,   blue key,   pebble,   can,   tray,   apple,   bread,   protein bar
		
		// Level 1 Items
        {{1815,1869}, {2899,2175},   {67,2234},  {2699,2620},  {1455,2826}, {2560,1968}, {1425,2170}, {1415,1871}, {2325,2557}, {1230,1745}, {1747,1155} }, // Eleven item locations for level 1
        // Level 2 Items
        { {340,1974}, {725,959}, {114,2055}, {2803,1400}, {2500,3078}, {1359,1860}, {2780,2436}, {2379,2819}, {2769,450}, {2568,2819}, {1992,1160}}, // Eleven item locations for level 2
        // Level 3 Items
		{ {2399,151}, {2900,2050}, {2950,1350}, {2600,3075}, {1900,1114}, {1950, 781}, {340, 1465}, {1670,1294}, {2630, 2765}, {1280,1260}, {335, 1980}, }, // Eleven item locations for level 3
		// Level 4 Items
		{ {2835,2070}, {432,2679}, {1950,510}, {1790,640}, {996,2070}, {1610,2640}, {1987,2690}, {972,1360}, {920,2765}, {1239,1524}, {2178,1220} } // Eleven item locations for level 4
		
	};
	
	
	int[][][] NPCLocations = new int[][][] {	// Three-dimensional array storing NPC locations for each level
		// Billy Goat, Old Man Jone
		// Level 1 NPCS
		{ {384, 960}, {1792,704}}, // Two NPC locations for level 1
		// Level 2 NPCS
		{ {1665,1023}, {1733,187}}, // Two NPC locations for level 2
		// Level 3 Tasks
		{ {790,123}, {2563,2795}}, // Two NPC locations for level 3
		// Level 4 Tasks
		{ {2166,1911}, {187,1963}} // Two NPC locations for level 4
		
	};
	
	int[][][] GaurdLocations = new int[][][] {	// Three-dimensional array storing guard locations for each level (currently unused)
		// Level 1 Items
		{ {1860,672}, {1834,883}, {762,2523}, {66,2251}, {1222,1703}, {2298,2535}, {2690,2619}, {2874,2143}, {2314,1711}, {450,2039}, {1726,2147} }, // Eleven guard locations for level 1
		// Level 2 Tasks
		{ {2948,1019}, {2431,124}, {311,1905}, {930,2765}, {2811,3049}, {958,825} }, // Six guard locations for level 2
		// Level 3 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26} }, // Six guard locations for level 3
		// Level 4 Tasks
		{ {10,15}, {12,18}, {14,20}, {16,22}, {18,24}, {20,26}, {22,28}, {24,30} } // Eight guard locations for level 4
		
	};

	
	
	public void setAll() { // Method to initialize all game assets at once
		setItem(); // Calls method to place all items in the world
		setNPC(); // Calls method to place all NPCs in the world
		setGaurds(); // Calls method to place all guards in the world
		setTasks(); // Calls method to place all tasks in the world
	}
	
	// in class AssetSetter
	public void placeItem(Item item, int worldX, int worldY) { // Method to place a single item at specified world coordinates
	    int slot = gp.getEmptyItemSlot(); // Gets the next available slot in the world item array
	    if (slot == -1) { // Checks if no empty slot was found
	        // no room in world item array
	        System.out.println("No empty item slot in world to place item: " + item.getName()); // Prints error message to console
	        return; // Exits the method early
	    }

	    // set the item's world position and put it into the world slot
	    item.worldX = worldX; // Sets the item's X coordinate in the world
	    item.worldY = worldY; // Sets the item's Y coordinate in the world
	    gp.items[slot] = item; // Assigns the item to the available slot in the game panel's item array
	}
	
	public void setThrowable() { // Method to spawn throwable items (pebbles, cans, trays) across the level

	    // ==================== HOW MANY TO SPAWN ====================
	    int amount = 0; // Variable to store the number of throwables to spawn
	    switch (gp.level) { // Switches based on current game level
	        case 1 -> amount = random.nextInt(2, 4); // Level 1: spawns 2-3 throwables
	        case 2 -> amount = random.nextInt(3, 5); // Level 2: spawns 3-4 throwables
	        case 3 -> amount = random.nextInt(4, 6); // Level 3: spawns 4-5 throwables
	        case 4 -> amount = random.nextInt(5, 7); // Level 4: spawns 5-6 throwables
	    }
	    
	    if (gp.level > 4) return; // Exits method if level is greater than 4

	    int levelIndex = gp.level - 1; // Converts level number to array index (0-based)
	    int[][] levelLocations = ItemLocations[levelIndex]; // Gets the item location array for the current level

	    // ==================== AVAILABLE THROWABLE INDICES ====================
	    ArrayList<Integer> availableIndices = new ArrayList<>(); // Creates list to store indices of available throwable types
	    if (levelLocations.length > 5) availableIndices.add(5); // Adds Pebble index if location exists
	    if (levelLocations.length > 6) availableIndices.add(6); // Adds Can index if location exists
	    if (levelLocations.length > 7) availableIndices.add(7); // Adds Tray index if location exists

	    // ==================== SPAWN THROWABLES ====================
	    for (int i = 0; i < amount && !availableIndices.isEmpty(); i++) { // Loops to spawn the determined amount of throwables

	        int slot = gp.getEmptyItemSlot(); // Gets next available item slot in world
	        if (slot == -1) break; // Exits loop if no empty slots remain

	        // Pick random throwable index (no reuse)
	        int idx = random.nextInt(availableIndices.size()); // Generates random index from available throwables
	        int itemIndex = availableIndices.remove(idx); // Removes and retrieves the selected throwable index to prevent duplicates

	        Item throwable; // Declares variable to hold the throwable item
	        switch (itemIndex) { // Switches based on the selected item index
	            case 5 -> throwable = new Pebble(gp); // Creates a Pebble object
	            case 6 -> throwable = new Can(gp); // Creates a Can object
	            default -> throwable = new Tray(gp); // Creates a Tray object for any other case
	        }

	        // Set position from predefined list
	        throwable.worldX = levelLocations[itemIndex][0]; // Sets the throwable's X coordinate from the location array
	        throwable.worldY = levelLocations[itemIndex][1]; // Sets the throwable's Y coordinate from the location array

	        gp.items[slot] = throwable; // Places the throwable in the world item array
	    }
	}

	public void setItem() { // Method to place all items in the game world

	    // ==================== THROWABLES FIRST ====================
	    setThrowable(); // Calls method to spawn throwable items first
	    if (gp.level > 4) return; // Exits if level exceeds 4
	    int levelIndex = gp.level - 1; // Converts level to array index
	    int[][] levelLocations = ItemLocations[levelIndex]; // Gets item locations for current level
	    int slot; // Variable to store available item slots

	    // ==================== ITEM KIT (NON-THROWABLES) ====================

	    // 0 → Yellow Key
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 0) { // Checks if slot exists and location array has data
	        Item it = new redKey(gp); // Creates a red key object (comment says Yellow Key but creates redKey)
	        it.worldX = levelLocations[0][0]; // Sets X coordinate from location array
	        it.worldY = levelLocations[0][1]; // Sets Y coordinate from location array
	        gp.items[slot] = it; // Places the key in the world
	    }

	    // 1 → Key
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 1) { // Checks if slot and location exist
	        Item it = new Key(gp); // Creates a yellow key object
	        it.worldX = levelLocations[1][0]; // Sets X coordinate
	        it.worldY = levelLocations[1][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the key in the world
	    }

	    // 2 → Green Key
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 2) { // Checks if slot and location exist
	        Item it = new greenKey(gp); // Creates a green key object
	        it.worldX = levelLocations[2][0]; // Sets X coordinate
	        it.worldY = levelLocations[2][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the key in the world
	    }

	    // 3 → Flashlight
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 3) { // Checks if slot and location exist
	        Item it = new Flashlight(gp); // Creates a flashlight object
	        it.worldX = levelLocations[3][0]; // Sets X coordinate
	        it.worldY = levelLocations[3][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the flashlight in the world
	    }

	    // 4 → Blue Key
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 4) { // Checks if slot and location exist
	        Item it = new blueKey(gp); // Creates a blue key object
	        it.worldX = levelLocations[4][0]; // Sets X coordinate
	        it.worldY = levelLocations[4][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the key in the world
	    }

	    // ==================== CONSUMABLES ====================

	    // 8 → Apple
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 8) { // Checks if slot and location exist
	        Item it = new Apple(gp); // Creates an apple object
	        it.worldX = levelLocations[8][0]; // Sets X coordinate
	        it.worldY = levelLocations[8][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the apple in the world
	    }

	    // 9 → Bread
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 9) { // Checks if slot and location exist
	        Item it = new Bread(gp); // Creates a bread object
	        it.worldX = levelLocations[9][0]; // Sets X coordinate
	        it.worldY = levelLocations[9][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the bread in the world
	    }

	    // 10 → Protein Bar
	    slot = gp.getEmptyItemSlot(); // Gets next empty slot
	    if (slot != -1 && levelLocations.length > 10) { // Checks if slot and location exist
	        Item it = new ProteinBar(gp); // Creates a protein bar object
	        it.worldX = levelLocations[10][0]; // Sets X coordinate
	        it.worldY = levelLocations[10][1]; // Sets Y coordinate
	        gp.items[slot] = it; // Places the protein bar in the world
	    }
	}


	
	public void setNPC() { // Method to place NPCs in the world based on current level
		if (gp.level == 1) { // Checks if current level is 1
			gp.npc[1] = new BillyGoat(gp); // Creates Billy Goat NPC at index 1
			gp.npc[1].worldX = NPCLocations[0][0][0]; // Sets Billy Goat's X coordinate from level 1 locations
			gp.npc[1].worldY = NPCLocations[0][0][1]; // Sets Billy Goat's Y coordinate from level 1 locations
			
			gp.npc[0] = new OldManJone(gp); // Creates Old Man Jone NPC at index 0
			gp.npc[0].worldX = NPCLocations[0][1][0]; // Sets Old Man Jone's X coordinate from level 1 locations
			gp.npc[0].worldY = NPCLocations[0][1][1]; // Sets Old Man Jone's Y coordinate from level 1 locations
		}
		if (gp.level == 2) { // Checks if current level is 2
			gp.npc[1] = new BillyGoat(gp); // Creates Billy Goat NPC at index 1
			gp.npc[1].worldX = NPCLocations[1][0][0]; // Sets Billy Goat's X coordinate from level 2 locations
			gp.npc[1].worldY = NPCLocations[1][0][1]; // Sets Billy Goat's Y coordinate from level 2 locations
			
			gp.npc[0] = new OldManJone(gp); // Creates Old Man Jone NPC at index 0
			gp.npc[0].worldX = NPCLocations[1][1][0]; // Sets Old Man Jone's X coordinate from level 2 locations
			gp.npc[0].worldY = NPCLocations[1][1][1]; // Sets Old Man Jone's Y coordinate from level 2 locations
		}
		if (gp.level == 3) { // Checks if current level is 3
			gp.npc[1] = new BillyGoat(gp); // Creates Billy Goat NPC at index 1
			gp.npc[1].worldX = NPCLocations[2][0][0]; // Sets Billy Goat's X coordinate from level 3 locations
			gp.npc[1].worldY = NPCLocations[2][0][1]; // Sets Billy Goat's Y coordinate from level 3 locations
			
			gp.npc[0] = new OldManJone(gp); // Creates Old Man Jone NPC at index 0
			gp.npc[0].worldX = NPCLocations[2][1][0]; // Sets Old Man Jone's X coordinate from level 3 locations
			gp.npc[0].worldY = NPCLocations[2][1][1]; // Sets Old Man Jone's Y coordinate from level 3 locations
		}
		if (gp.level == 4) { // Checks if current level is 4
			gp.npc[1] = new BillyGoat(gp); // Creates Billy Goat NPC at index 1
			gp.npc[1].worldX = NPCLocations[3][0][0]; // Sets Billy Goat's X coordinate from level 4 locations
			gp.npc[1].worldY = NPCLocations[3][0][1]; // Sets Billy Goat's Y coordinate from level 4 locations
			
			gp.npc[0] = new OldManJone(gp); // Creates Old Man Jone NPC at index 0
			gp.npc[0].worldX = NPCLocations[3][1][0]; // Sets Old Man Jone's X coordinate from level 4 locations
			gp.npc[0].worldY = NPCLocations[3][1][1]; // Sets Old Man Jone's Y coordinate from level 4 locations
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
	
	public void setTasks() { // Method to randomly assign and place tasks for the current level
		
		if (gp.level > 4) return; // Exits method if level is greater than 4

        gp.player.tasksList.clear(); // Clears all tasks from the player's task list
        for (int i = 0; i < gp.tasks.length; i++) { // Loops through all task slots in the game panel's task array
			gp.tasks[i] = null; // Sets each task slot to null, clearing existing tasks
		}

        int tasksToAdd = 2; // Variable to store how many tasks to add for this level

        if (gp.level == 1) tasksToAdd = 2; // Level 1 gets 2 tasks
        else if (gp.level == 2) tasksToAdd = 4; // Level 2 gets 4 tasks
        else if (gp.level == 3) tasksToAdd = 6; // Level 3 gets 6 tasks
        else if (gp.level >= 4) tasksToAdd = 8; // Level 4 and above get 8 tasks

    

        for (int i = 0; i < tasksToAdd; i++) { // Loops to add the specified number of tasks

            int choice = random.nextInt(9); // Randomly selects a task type (0-8)
            
            while (true) { // Loops until a unique task type is found
				boolean alreadyAssigned = false; // Flag to track if this task type is already assigned
				for (Task t : gp.player.tasksList) { // Loops through existing tasks in player's list
					if ((choice == 0 && t instanceof MathTask) || // Checks if choice 0 and MathTask already exists
						(choice == 1 && t instanceof VaultSequenceTask) || // Checks if choice 1 and VaultSequenceTask already exists
						(choice == 2 && t instanceof CookingTask) || // Checks if choice 2 and CookingTask already exists
						(choice == 3 && t instanceof ButtonTask) || // Checks if choice 3 and ButtonTask already exists
						(choice == 4 && t instanceof LogicPanelTask) || // Checks if choice 4 and LogicPanelTask already exists
						(choice == 5 && t instanceof RiddleTask) || // Checks if choice 5 and RiddleTask already exists
						(choice == 6 && t instanceof FuseRepairTask) || // Checks if choice 6 and FuseRepairTask already exists
						(choice == 7 && t instanceof TileSelectTask) || // Checks if choice 7 and TileSelectTask already exists
						(choice == 8 && t instanceof PatternSwitchesTask)) { // Checks if choice 8 and PatternSwitchesTask already exists
						alreadyAssigned = true; // Sets flag to true if task type already exists
						break; // Exits the for loop early
					}
				}
				if (!alreadyAssigned) { // If this task type is not already assigned
					break; // Exits the while loop, unique task type found
				}
				choice = random.nextInt(9); // Generates a new random choice to try again
			}
            

            Task task = null; // Declares variable to hold the task object

            if (choice == 0) { // If choice is 0
                task = new MathTask(gp); // Creates a MathTask
            }
            else if (choice == 1) { // If choice is 1
                task = new VaultSequenceTask(gp); // Creates a VaultSequenceTask
            }
            else if (choice == 2) { // If choice is 2
                task = new CookingTask(gp); // Creates a CookingTask
            }
            else if (choice == 3) { // If choice is 3
				task = new ButtonTask(gp); // Creates a ButtonTask
			}
			else if (choice == 4) { // If choice is 4
				task = new LogicPanelTask(gp); // Creates a LogicPanelTask
			}
			else if (choice == 5) { // If choice is 5
				task = new RiddleTask(gp); // Creates a RiddleTask
			}
			else if (choice == 6) { // If choice is 6
				task = new TileSelectTask(gp); // Creates a TileSelectTask
			}
			else if (choice == 7) { // If choice is 7
				task = new PatternSwitchesTask(gp); // Creates a PatternSwitchesTask
			} 
			else if (choice == 8) { // If choice is 8
				task = new FuseRepairTask(gp); // Creates a FuseRepairTask
			}
            choice = random.nextInt(TaskLocations[gp.level - 1].length); // Randomly selects a location index for this task
            
            // check if location is already taken
            while (true) { // Loops until a unique location is found
            	boolean locationTaken = false; // Flag to track if this location is already occupied
            	int x = TaskLocations[gp.level - 1][choice][0]; // Gets the X coordinate from the selected location
            	int y = TaskLocations[gp.level - 1][choice][1]; // Gets the Y coordinate from the selected location
            	for (Task t : gp.player.tasksList) { // Loops through existing tasks
					if (t.worldX == x && t.worldY == y) { // Checks if a task already exists at this location
						locationTaken = true; // Sets flag to true if location is occupied
						break; // Exits the for loop early
					}
				}
            	if (!locationTaken) { // If location is not occupied
					break; // Exits the while loop, unique location found
				}
				choice = random.nextInt(TaskLocations[gp.level - 1].length); // Generates a new random location to try again
            }
            
            int x = TaskLocations[gp.level - 1][choice][0]; // Retrieves final X coordinate for the task
            int y = TaskLocations[gp.level - 1][choice][1]; // Retrieves final Y coordinate for the task

            if (task != null) { // Checks if a task was successfully created
                gp.player.tasksList.add(task); // Adds the task to the player's task list
                gp.tasks[i] = task; // Assigns the task to the game panel's task array at index i
                gp.tasks[i].worldX = x; // Sets the task's world X coordinate
                gp.tasks[i].worldY = y; // Sets the task's world Y coordinate
                System.out.println("Added task: " + task.getName() + " at X: " + gp.tasks[i].worldX + " Y: " + gp.tasks[i].worldY); // Prints task placement info to console
                
            }
        }
    }
	
	 // Call this when bread is picked up - pass the item's location
    public void startBreadRespawn(int x, int y) { // Method to initialize bread respawn timer when bread is picked up
        breadRespawnX = x; // Stores the X coordinate where bread was picked up
        breadRespawnY = y; // Stores the Y coordinate where bread was picked up
        breadRespawnTimer = RESPAWN_TIME; // Sets the respawn timer to the defined respawn time
    }

    // Call this when apple is picked up - pass the item's location
    public void startAppleRespawn(int x, int y) { // Method to initialize apple respawn timer when apple is picked up
        appleRespawnX = x; // Stores the X coordinate where apple was picked up
        appleRespawnY = y; // Stores the Y coordinate where apple was picked up
        appleRespawnTimer = RESPAWN_TIME; // Sets the respawn timer to the defined respawn time
    }

	
	// Called every frame from gamePanel.update() to handle food respawns
    public void updateRespawns() { // Method that updates respawn timers and spawns food items when timers reach zero
        // Bread respawn timer
        if (breadRespawnTimer > 0) { // Checks if bread respawn timer is counting down
            breadRespawnTimer--; // Decrements bread respawn timer by 1 frame
        } else if (breadRespawnTimer == 0) { // Checks if bread respawn timer just reached zero
            int slot = gp.getEmptyItemSlot(); // Gets next available item slot
            if (slot != -1) { // Checks if an empty slot was found
                Item it = new Bread(gp); // Creates a new bread object
                it.worldX = breadRespawnX; // Sets bread's X coordinate to stored respawn location
                it.worldY = breadRespawnY; // Sets bread's Y coordinate to stored respawn location
                gp.items[slot] = it; // Places the bread in the world
            }
            breadRespawnTimer = -1; // Resets timer to -1 indicating not currently respawning
        }

        // Apple respawn timer
        if (appleRespawnTimer > 0) { // Checks if apple respawn timer is counting down
            appleRespawnTimer--; // Decrements apple respawn timer by 1 frame
        } else if (appleRespawnTimer == 0) { // Checks if apple respawn timer just reached zero
            int slot = gp.getEmptyItemSlot(); // Gets next available item slot
            if (slot != -1) { // Checks if an empty slot was found
                Item it = new Apple(gp); // Creates a new apple object
                it.worldX = appleRespawnX; // Sets apple's X coordinate to stored respawn location
                it.worldY = appleRespawnY; // Sets apple's Y coordinate to stored respawn location
                gp.items[slot] = it; // Places the apple in the world
            }
            appleRespawnTimer = -1; // Resets timer to -1 indicating not currently respawning
        }
    }
	
	public void update() { // Main update method called each frame
		updateRespawns(); // Calls the method to handle food item respawning logic
	}
}