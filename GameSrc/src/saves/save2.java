/*
Name: Rafay, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: save2.java
Date: 1/19/2026
Program Description:
Save slot 2 class. Handles saving and loading game state to save2.dat file.
Stores player position, level, stamina, inventory items, key flags,
unlocked skins, keybinds, and task data including type, position, and
completion status. Allows the player to resume from a previously saved game.
*/

package saves; // package: groups save-related classes together

import java.io.*; // import file I/O and serialization classes (File, FileInputStream, Object streams, etc.)
import java.util.ArrayList; // import ArrayList for building task lists dynamically
import java.util.Arrays; // import Arrays utility for copying arrays

import Item.Item; // import the Item class to match saved item names with world items
import main.AssetSetter; // import AssetSetter (kept for compatibility with gamePanel)
import main.gamePanel; // import the main gamePanel class as source/target of save/load
import task.*; // import all task subclasses so they can be recreated when loading

public class save2 { // save2 class: handles the second save slot (save2.dat)

    private static final String SAVE_FILE = "save2.dat"; // file name used for this save slot

    private static class SaveData implements Serializable { // inner class used as the serializable snapshot
        private static final long serialVersionUID = 1L; // serialization version UID for compatibility

        int level; // saved level number
        int playerWorldX; // saved player world X coordinate
        int playerWorldY; // saved player world Y coordinate
        int playerRow; // saved player tile row
        int playerCol; // saved player tile column
        float playerStamina; // saved current stamina
        float playerMaxStamina; // saved maximum stamina

        String[] inventoryItemNames; // saved inventory items by name
        int selectedSlot; // saved selected inventory slot index

        boolean hasKey, hasRedKey, hasGreenKey, hasBlueKey; // saved key flags
        boolean hasPebble, hasCan, hasTray, hasApple, hasBread, hasProteinBar, hasFlashlight; // saved item flags

        boolean[] unlockedSkins; // saved unlocked skins array
        int equippedSkinIndex; // saved equipped skin index

        int[] keybinds; // saved custom keybinds as key codes

        String[] taskTypes; // saved task class names
        int[] taskWorldX; // saved task world X positions
        int[] taskWorldY; // saved task world Y positions
        boolean[] taskCompleted; // saved task completion flags
    } // end SaveData

    // ---------------- SAVE ----------------
    public static void saveGame(gamePanel gp) { // saveGame: captures current state and writes it to SAVE_FILE
        SaveData s = snapshotFromGame(gp); // create a SaveData snapshot from the gamePanel
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) { // open ObjectOutputStream
            oos.writeObject(s); // serialize and write the SaveData object to disk
            System.out.println("✓ Game saved to " + SAVE_FILE); // log success
            System.out.println("  - Level: " + s.level); // log saved level
            System.out.println("  - Equipped skin: " + s.equippedSkinIndex); // log equipped skin index
            System.out.println("  - Unlocked skins:"); // header for unlocked skins
            if (s.unlockedSkins != null) { // if skin data exists
                for (int i = 0; i < s.unlockedSkins.length; i++) { // iterate through saved skin flags
                    if (s.unlockedSkins[i]) { // if skin is unlocked
                        System.out.println("    ✓ Skin " + i); // print unlocked skin index
                    }
                }
            }
            System.out.println("  - Tasks saved: " + (s.taskTypes != null ? s.taskTypes.length : 0)); // log number of saved tasks
        } catch (IOException e) { // handle I/O errors during save
            e.printStackTrace(); // print stack trace for debugging
            System.out.println("✗ Failed to save game: " + e.getMessage()); // friendly error message
        }
    } // end saveGame

    // ---------------- LOAD ----------------
    public static void loadGame(gamePanel gp) { // loadGame: reads SAVE_FILE and applies the snapshot to gamePanel
        File f = new File(SAVE_FILE); // create a File reference to the save file
        if (!f.exists()) { // if the file doesn't exist
            System.out.println("✗ No save file found (" + SAVE_FILE + ")"); // notify and abort
            return; // early return
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) { // open ObjectInputStream
            Object raw = ois.readObject(); // read object from file
            if (!(raw instanceof SaveData)) { // verify that the object is the expected type
                System.out.println("✗ Save file has unexpected format"); // error if not
                return; // abort load
            }
            SaveData s = (SaveData) raw; // cast to SaveData

            System.out.println("✓ Loading save from " + SAVE_FILE); // log beginning of load
            System.out.println("  - Level: " + s.level); // log saved level
            System.out.println("  - Equipped skin: " + s.equippedSkinIndex); // log saved equipped skin

            applySnapshotToGame(s, gp); // apply snapshot values to the provided gamePanel

            System.out.println("✓ Game loaded successfully"); // log success
        } catch (Exception e) { // catch any exception during loading (I/O, ClassNotFound, etc.)
            e.printStackTrace(); // print stack trace for debugging
            System.out.println("✗ Failed to load save: " + e.getMessage()); // friendly error message
        }
    } // end loadGame

    // ---------------- SNAPSHOT ----------------
    private static SaveData snapshotFromGame(gamePanel gp) { // snapshotFromGame: build SaveData from current game state
        SaveData s = new SaveData(); // instantiate SaveData
        s.level = (gp == null) ? 1 : gp.level; // store current level, default to 1 if gp is null

        if (gp != null && gp.player != null) { // if gamePanel and player are available
            s.playerWorldX = gp.player.worldX; // save player's world X
            s.playerWorldY = gp.player.worldY; // save player's world Y
            s.playerRow = gp.player.row; // save player's tile row
            s.playerCol = gp.player.col; // save player's tile column
            
            try { // attempt to capture stamina values
                s.playerStamina = gp.player.stamina; // save current stamina
                s.playerMaxStamina = gp.player.maxStamina; // save max stamina
            } catch (Throwable t) { // defensive fallback if fields missing/unavailable
                s.playerStamina = 0; // default stamina
                s.playerMaxStamina = 100; // default max stamina
            }

            // Inventory
            try { // snapshot inventory item names
                int invSize = Math.min(gp.player.inventory.size(), gp.player.INVENTORY_SIZE); // ensure within inventory limit
                s.inventoryItemNames = new String[invSize]; // allocate array for names
                for (int i = 0; i < invSize; i++) { // iterate inventory slots
                    Item it = gp.player.inventory.get(i); // get item reference
                    s.inventoryItemNames[i] = (it == null ? "" : it.getName()); // store item name or empty string
                }
            } catch (Throwable t) { // on error, fallback to empty array
                s.inventoryItemNames = new String[0];
            }

            // Selected slot
            s.selectedSlot = 0; // default selected slot
            try { // attempt to find selected item index in inventory
                Item sel = (gp.ui == null ? null : gp.ui.selectedItem); // safely get selected item from UI
                if (sel != null) { // if a selected item exists
                    for (int i = 0; i < gp.player.inventory.size(); i++) { // search inventory
                        if (gp.player.inventory.get(i) == sel) { // match by reference
                            s.selectedSlot = i; // store index
                            break; // stop searching
                        }
                    }
                }
            } catch (Throwable ignored) {} // ignore errors finding selected slot

            // Flags
            s.hasKey = gp.player.hasKey; // save hasKey flag
            s.hasRedKey = gp.player.hasRedKey; // save hasRedKey flag
            s.hasGreenKey = gp.player.hasGreenKey; // save hasGreenKey flag
            s.hasBlueKey = gp.player.hasBlueKey; // save hasBlueKey flag
            s.hasPebble = gp.player.hasPebble; // save hasPebble flag
            s.hasCan = gp.player.hasCan; // save hasCan flag
            s.hasTray = gp.player.hasTray; // save hasTray flag
            s.hasApple = gp.player.hasApple; // save hasApple flag
            s.hasBread = gp.player.hasBread; // save hasBread flag
            s.hasProteinBar = gp.player.hasProteinBar; // save hasProteinBar flag
            s.hasFlashlight = gp.player.hasFlashlight; // save hasFlashlight flag
        } else { // if gp or player is unavailable, use safe defaults
            s.playerWorldX = s.playerWorldY = s.playerRow = s.playerCol = 0; // default coordinates
            s.playerStamina = 0; // default stamina
            s.playerMaxStamina = 100; // default max stamina
            s.inventoryItemNames = new String[0]; // empty inventory
            s.selectedSlot = 0; // default selected slot
        }

        // Skins - READ FROM gp.skins (the source of truth)
        if (gp != null && gp.skins != null) { // if skins data exists on gamePanel
            s.unlockedSkins = new boolean[gp.skins.length]; // allocate boolean array for unlocked state
            for (int i = 0; i < gp.skins.length; i++) { // iterate skins
                try { // try to read unlock marker from the skins table
                    s.unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked"); // true if unlocked text found
                } catch (Throwable ignored) { // fallback on unexpected structure
                    s.unlockedSkins[i] = false; // default locked
                }
            }
        } else { // no skins available
            s.unlockedSkins = new boolean[0]; // empty array
        }
        s.equippedSkinIndex = (gp == null) ? 0 : gp.equippedSkinIndex; // save equipped skin index (safe default 0)

        // Keybinds
        if (gp != null && gp.keybinds != null) { // if keybinds exist on gp
            s.keybinds = Arrays.copyOf(gp.keybinds, gp.keybinds.length); // copy keybinds array
        } else { // otherwise use empty array
            s.keybinds = new int[0];
        }

        // Tasks
        ArrayList<String> types = new ArrayList<>(); // temporary list for task class names
        ArrayList<Integer> xPositions = new ArrayList<>(); // temporary list for task X positions
        ArrayList<Integer> yPositions = new ArrayList<>(); // temporary list for task Y positions
        ArrayList<Boolean> completed = new ArrayList<>(); // temporary list for task completion flags

        if (gp != null && gp.player != null && gp.player.tasksList != null) { // if player's tasks are available
            for (Task t : gp.player.tasksList) { // iterate player's tasks
                if (t == null) continue; // skip null entries
                
                String className = t.getClass().getSimpleName(); // get simple class name for reconstruction
                types.add(className); // add type to list
                xPositions.add(t.worldX); // add world X position
                yPositions.add(t.worldY); // add world Y position
                completed.add(t.isCompleted()); // add completion state
            }
        }

        s.taskTypes = types.toArray(new String[0]); // convert task types list to array
        s.taskWorldX = new int[xPositions.size()]; // allocate taskWorldX array
        s.taskWorldY = new int[yPositions.size()]; // allocate taskWorldY array
        s.taskCompleted = new boolean[completed.size()]; // allocate taskCompleted array
        
        for (int i = 0; i < xPositions.size(); i++) { // copy collected lists into arrays
            s.taskWorldX[i] = xPositions.get(i); // set X value
            s.taskWorldY[i] = yPositions.get(i); // set Y value
            s.taskCompleted[i] = completed.get(i); // set completion flag
        }

        return s; // return the populated SaveData snapshot
    } // end snapshotFromGame

    // ---------------- APPLY SNAPSHOT ----------------
    private static void applySnapshotToGame(SaveData s, gamePanel gp) { // apply saved snapshot to live gamePanel
        if (s == null || gp == null) return; // safety: nothing to apply

        // ===== SKINS FIRST - UPDATE gp.skins BEFORE ANYTHING ELSE =====
        if (s.unlockedSkins != null && s.unlockedSkins.length > 0 && gp.skins != null) { // if saved skins and gamePanel skins present
            System.out.println("  Restoring skin unlock states:"); // log header
            for (int i = 0; i < s.unlockedSkins.length && i < gp.skins.length; i++) { // iterate up to the smaller length
                String oldState = gp.skins[i][1][0]; // remember old state
                gp.skins[i][1][0] = s.unlockedSkins[i] ? "unlocked" : "locked"; // write new state string
                System.out.println("    " + gp.skins[i][0][0] + ": " + oldState + " → " + gp.skins[i][1][0]); // log change
            }
            
            gp.equippedSkinIndex = s.equippedSkinIndex; // set equipped skin index on gamePanel
            try { // try to set equippedSkin string if index valid
                gp.equippedSkin = gp.skins[s.equippedSkinIndex][0][0]; // assign equipped skin name
            } catch (Throwable ignored) {} // ignore errors (e.g., index out of range)
        }

        // Level/Map
        if (s.level >= 1 && s.level != gp.level) { // if saved level is valid and different
            gp.level = s.level; // update gamePanel level
            try { // try to load the corresponding map file
                if (gp.tileM != null) {
                    gp.tileM.loadMap("/maps/Level" + s.level + "Map.txt"); // load map for saved level
                }
            } catch (Throwable ignored) {} // ignore map loading errors
        }

        // Player fields
        if (gp.player != null) { // if player exists on gamePanel
            try { // apply position and tile coordinates
                gp.player.worldX = s.playerWorldX; // restore world X
                gp.player.worldY = s.playerWorldY; // restore world Y
                gp.player.row = s.playerRow; // restore tile row
                gp.player.col = s.playerCol; // restore tile column
            } catch (Throwable ignored) {} // ignore errors

            try { // apply stamina values
                gp.player.stamina = s.playerStamina; // restore current stamina
                gp.player.maxStamina = s.playerMaxStamina; // restore max stamina
            } catch (Throwable ignored) {} // ignore errors

            gp.player.hasKey = s.hasKey; // restore hasKey flag
            gp.player.hasRedKey = s.hasRedKey; // restore hasRedKey flag
            gp.player.hasGreenKey = s.hasGreenKey; // restore hasGreenKey flag
            gp.player.hasBlueKey = s.hasBlueKey; // restore hasBlueKey flag
            gp.player.hasPebble = s.hasPebble; // restore hasPebble flag
            gp.player.hasCan = s.hasCan; // restore hasCan flag
            gp.player.hasTray = s.hasTray; // restore hasTray flag
            gp.player.hasApple = s.hasApple; // restore hasApple flag
            gp.player.hasBread = s.hasBread; // restore hasBread flag
            gp.player.hasProteinBar = s.hasProteinBar; // restore hasProteinBar flag
            gp.player.hasFlashlight = s.hasFlashlight; // restore hasFlashlight flag
            
            // UPDATE PLAYER SKINS (gp.skins is already updated above)
            if (s.unlockedSkins != null && s.unlockedSkins.length > 0) { // if skin data present
                gp.player.unlockedSkins = Arrays.copyOf(s.unlockedSkins, s.unlockedSkins.length); // copy unlocked array to player
                gp.player.equippedSkinIndex = s.equippedSkinIndex; // set player's equipped index
                gp.player.equippedSkin = gp.equippedSkin; // sync player's equippedSkin string with gamePanel
                gp.player.getPlayerImage(); // refresh player's sprites for equipped skin
            }
        }

        // Keybinds
        if (s.keybinds != null && s.keybinds.length > 0) { // if saved keybinds exist
            gp.keybinds = Arrays.copyOf(s.keybinds, s.keybinds.length); // copy into gamePanel
            try { // attempt to set readable key names on gp
                gp.forwardKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[0]); // set forwardKey name
                gp.backKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[1]); // set backKey name
                gp.leftKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[2]); // set leftKey name
                gp.rightKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[3]); // set rightKey name
                gp.sprintKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[4]); // set sprintKey name
                gp.crouchKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[5]); // set crouchKey name
                gp.interactKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[6]); // set interactKey name
                gp.throwKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[7]); // set throwKey name
                gp.dropKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[8]); // set dropKey name
            } catch (Throwable ignored) {} // ignore errors if array shorter than expected
        }

        // Inventory restore
        if (s.inventoryItemNames != null && gp.player != null) { // if saved inventory names exist and player is present
            gp.player.clearInventory(); // clear current inventory before restoring
            for (String itemName : s.inventoryItemNames) { // iterate saved item names
                if (itemName == null || itemName.isEmpty()) continue; // skip empty entries
                boolean found = false; // flag if the world item was found
                for (int i = 0; i < gp.items.length; i++) { // search gp.items for matching world item
                    Item worldItem = gp.items[i]; // reference world item
                    if (worldItem != null && itemName.equals(worldItem.getName())) { // if name matches
                        gp.player.inventory.add(worldItem); // add world item to player's inventory
                        gp.items[i] = null; // remove it from the world to avoid duplication
                        found = true; // mark as found
                        break; // stop searching
                    }
                }
                if (!found) { // if not found in world items
                    System.out.println("  ⚠ Saved item not found in world: " + itemName); // warn
                }
            }
            
            try { // attempt to restore selected slot reference safely
                int sel = Math.max(0, Math.min(s.selectedSlot, gp.player.inventory.size() - 1)); // clamp index into range
                if (!gp.player.inventory.isEmpty()) { // if inventory has items
                    gp.ui.selectedItem = gp.player.inventory.get(sel); // set UI selected item
                } else { // if inventory empty
                    gp.ui.selectedItem = null; // clear selection
                }
            } catch (Throwable ignored) {} // ignore selection restoration errors
        }

        // Tasks restore
        if (s.taskTypes != null && s.taskTypes.length > 0) { // if there are saved tasks to restore
            System.out.println("  Restoring " + s.taskTypes.length + " tasks..."); // log number of tasks
            gp.player.tasksList.clear(); // clear existing player's tasks
            for (int i = 0; i < gp.tasks.length; i++) { // clear global task array
                gp.tasks[i] = null; // set each slot to null
            }

            for (int i = 0; i < s.taskTypes.length; i++) { // iterate saved tasks
                String taskType = s.taskTypes[i]; // get saved class name
                int worldX = s.taskWorldX[i]; // get saved world X
                int worldY = s.taskWorldY[i]; // get saved world Y
                boolean isCompleted = s.taskCompleted[i]; // get saved completion flag

                Task task = createTaskByType(taskType, gp); // create a new instance of the appropriate Task subclass
                
                if (task != null) { // if instantiation succeeded
                    task.worldX = worldX; // set task world X
                    task.worldY = worldY; // set task world Y
                    task.setCompleted(isCompleted); // set completion state
                    
                    gp.player.tasksList.add(task); // add task to player's list
                    gp.tasks[i] = task; // store task in global tasks array
                    
                    System.out.println("    ✓ " + taskType + 
                        " at (" + worldX + "," + worldY + ") → " + 
                        (isCompleted ? "COMPLETED" : "incomplete")); // log restored task info
                } else { // if creation failed
                    System.out.println("    ✗ Failed to create task: " + taskType); // warn
                }
            }
        }

        // Refresh visuals
        try { // attempt to refresh player's row/col after loading
            if (gp.player != null) { // if player exists
                gp.player.row = gp.player.worldY / gp.tileSize; // recalculate player's tile row
                gp.player.col = gp.player.worldX / gp.tileSize; // recalculate player's tile column
            }
        } catch (Throwable ignored) {} // ignore visual refresh errors
    } // end applySnapshotToGame

    // ---------------- CREATE TASK BY TYPE ----------------
    private static Task createTaskByType(String taskType, gamePanel gp) { // factory that returns a Task subclass instance for a given type name
        try { // try to create a task instance
            switch (taskType) { // match the saved class name
                case "MathTask":
                    return new MathTask(gp); // return MathTask instance
                case "VaultSequenceTask":
                    return new VaultSequenceTask(gp); // return VaultSequenceTask instance
                case "CookingTask":
                    return new CookingTask(gp); // return CookingTask instance
                case "ButtonTask":
                    return new ButtonTask(gp); // return ButtonTask instance
                case "LogicPanelTask":
                    return new LogicPanelTask(gp); // return LogicPanelTask instance
                case "RiddleTask":
                    return new RiddleTask(gp); // return RiddleTask instance
                case "TileSelectTask":
                    return new TileSelectTask(gp); // return TileSelectTask instance
                case "FuseRepairTask":
                    return new FuseRepairTask(gp); // return FuseRepairTask instance
                case "PatternSwitchesTask":
                    return new PatternSwitchesTask(gp); // return PatternSwitchesTask instance
                default: // unknown task type
                    System.out.println("  ⚠ Unknown task type: " + taskType); // warn about unknown type
                    return null; // return null to indicate failure
            }
        } catch (Exception e) { // catch instantiation exceptions
            System.out.println("  ✗ Error creating task " + taskType + ": " + e.getMessage()); // log error
            return null; // return null on exception
        }
    } // end createTaskByType

    // ---------------- FILE EXISTS ----------------
    public static boolean fileExists() { // fileExists: checks whether SAVE_FILE exists and appears to contain SaveData
        File f = new File(SAVE_FILE); // reference to the save file
        if (!f.exists() || !f.isFile() || f.length() == 0) return false; // basic checks: exists, is a file, non-empty
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) { // try to read the file's object
            Object raw = ois.readObject(); // read the serialized object
            return (raw instanceof SaveData); // return true only if it is a SaveData instance
        } catch (Throwable t) { // any exception reading/parsing -> not a valid save
            return false; // indicate file doesn't exist or is invalid
        }
    } // end fileExists
} // end class save2
