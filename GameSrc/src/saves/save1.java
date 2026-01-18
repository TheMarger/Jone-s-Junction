/*
Name: Rafay, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: save1.java
Date: 1/19/2026
Program Description:
Save slot 1 class. Handles saving and loading game state to save1.dat file.
Stores player position, level, stamina, inventory items, key flags,
unlocked skins, keybinds, and task data including type, position, and
completion status. Allows the player to resume from a previously saved game.
*/

package saves; // package: groups save-related classes together

import java.io.*; // file input/output and serialization utilities
import java.util.ArrayList; // dynamic list for building task data
import java.util.Arrays; // array helper methods (copying, etc.)

import Item.Item; // Item class used to match inventory items by name
import main.AssetSetter; // asset helper (kept for compatibility; not modified)
import main.gamePanel; // main game panel class — source of the current game state
import task.*; // import all task subclasses so they can be recreated on load

public class save1 { // save1: handles save slot 1 (save1.dat)

    private static final String SAVE_FILE = "save1.dat"; // filename used for this save slot

    private static class SaveData implements Serializable { // container class that gets serialized
        private static final long serialVersionUID = 1L; // version ID for serialization compatibility

        int level; // current level number
        int playerWorldX; // player's world X coordinate
        int playerWorldY; // player's world Y coordinate
        int playerRow; // player's tile row
        int playerCol; // player's tile column
        float playerStamina; // current player stamina
        float playerMaxStamina; // player's maximum stamina

        String[] inventoryItemNames; // snapshot of inventory items by name
        int selectedSlot; // currently selected inventory slot index

        boolean hasKey, hasRedKey, hasGreenKey, hasBlueKey; // boolean key flags
        boolean hasPebble, hasCan, hasTray, hasApple, hasBread, hasProteinBar, hasFlashlight; // other item flags

        boolean[] unlockedSkins; // which skins are unlocked
        int equippedSkinIndex; // index of the equipped skin

        int[] keybinds; // custom keybinds saved as key codes

        String[] taskTypes; // saved task class names
        int[] taskWorldX; // saved task world X positions
        int[] taskWorldY; // saved task world Y positions
        boolean[] taskCompleted; // saved task completion flags
    } // end SaveData

    // ---------------- SAVE ----------------
    public static void saveGame(gamePanel gp) { // create and write a snapshot to SAVE_FILE
        SaveData s = snapshotFromGame(gp); // build a SaveData object from the current game state
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) { // open serialized output
            oos.writeObject(s); // write the SaveData to disk
            System.out.println("✓ Game saved to " + SAVE_FILE); // log success
            System.out.println("  - Level: " + s.level); // log saved level
            System.out.println("  - Equipped skin: " + s.equippedSkinIndex); // log equipped skin index
            System.out.println("  - Unlocked skins:"); // header for unlocked skins listing
            if (s.unlockedSkins != null) { // check for skin data
                for (int i = 0; i < s.unlockedSkins.length; i++) { // iterate through skins
                    if (s.unlockedSkins[i]) { // if skin unlocked
                        System.out.println("    ✓ Skin " + i); // print unlocked skin index
                    }
                }
            }
            System.out.println("  - Tasks saved: " + (s.taskTypes != null ? s.taskTypes.length : 0)); // log number of saved tasks
        } catch (IOException e) { // handle file I/O errors during save
            e.printStackTrace(); // print stack trace for debugging
            System.out.println("✗ Failed to save game: " + e.getMessage()); // friendly error message
        }
    } // end saveGame

    // ---------------- LOAD ----------------
    public static void loadGame(gamePanel gp) { // read SAVE_FILE and apply it to the provided gamePanel
        File f = new File(SAVE_FILE); // reference to the save file
        if (!f.exists()) { // if the file does not exist
            System.out.println("✗ No save file found (" + SAVE_FILE + ")"); // inform the user
            return; // abort load
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) { // open serialized input
            Object raw = ois.readObject(); // read object from file
            if (!(raw instanceof SaveData)) { // verify the object type
                System.out.println("✗ Save file has unexpected format"); // format mismatch
                return; // abort load
            }
            SaveData s = (SaveData) raw; // safe cast to SaveData

            System.out.println("✓ Loading save from " + SAVE_FILE); // log start of load
            System.out.println("  - Level: " + s.level); // log saved level
            System.out.println("  - Equipped skin: " + s.equippedSkinIndex); // log equipped skin

            applySnapshotToGame(s, gp); // apply values from the snapshot to the live game

            System.out.println("✓ Game loaded successfully"); // finished loading
        } catch (Exception e) { // catch any exception during load
            e.printStackTrace(); // print stack trace for debugging
            System.out.println("✗ Failed to load save: " + e.getMessage()); // friendly error message
        }
    } // end loadGame

    // ---------------- SNAPSHOT ----------------
    private static SaveData snapshotFromGame(gamePanel gp) { // build a SaveData object from the current game state
        SaveData s = new SaveData(); // new container instance
        s.level = (gp == null) ? 1 : gp.level; // store level (default to 1 if gp is null)

        if (gp != null && gp.player != null) { // if game panel and player exist
            s.playerWorldX = gp.player.worldX; // store player world X
            s.playerWorldY = gp.player.worldY; // store player world Y
            s.playerRow = gp.player.row; // store player tile row
            s.playerCol = gp.player.col; // store player tile column
            
            try { // try to capture stamina values
                s.playerStamina = gp.player.stamina; // current stamina
                s.playerMaxStamina = gp.player.maxStamina; // max stamina
            } catch (Throwable t) { // defensive fallback if fields are missing
                s.playerStamina = 0; // fallback stamina
                s.playerMaxStamina = 100; // fallback max stamina
            }

            // Inventory
            try { // attempt to snapshot inventory names
                int invSize = Math.min(gp.player.inventory.size(), gp.player.INVENTORY_SIZE); // ensure within inventory bounds
                s.inventoryItemNames = new String[invSize]; // allocate array for names
                for (int i = 0; i < invSize; i++) { // loop inventory slots
                    Item it = gp.player.inventory.get(i); // get item reference
                    s.inventoryItemNames[i] = (it == null ? "" : it.getName()); // store item name or empty string
                }
            } catch (Throwable t) { // if any error occurs
                s.inventoryItemNames = new String[0]; // fallback to empty array
            }

            // Selected slot
            s.selectedSlot = 0; // default selected slot
            try { // try to determine selected slot index
                Item sel = (gp.ui == null ? null : gp.ui.selectedItem); // safely get selected item
                if (sel != null) { // if there's a selected item
                    for (int i = 0; i < gp.player.inventory.size(); i++) { // find it in the inventory
                        if (gp.player.inventory.get(i) == sel) { // match by reference
                            s.selectedSlot = i; // store index
                            break; // done searching
                        }
                    }
                }
            } catch (Throwable ignored) {} // ignore any errors here

            // Flags (key items and consumables)
            s.hasKey = gp.player.hasKey; // store hasKey
            s.hasRedKey = gp.player.hasRedKey; // store hasRedKey
            s.hasGreenKey = gp.player.hasGreenKey; // store hasGreenKey
            s.hasBlueKey = gp.player.hasBlueKey; // store hasBlueKey
            s.hasPebble = gp.player.hasPebble; // store hasPebble
            s.hasCan = gp.player.hasCan; // store hasCan
            s.hasTray = gp.player.hasTray; // store hasTray
            s.hasApple = gp.player.hasApple; // store hasApple
            s.hasBread = gp.player.hasBread; // store hasBread
            s.hasProteinBar = gp.player.hasProteinBar; // store hasProteinBar
            s.hasFlashlight = gp.player.hasFlashlight; // store hasFlashlight
        } else { // if gp or player is null, set safe defaults
            s.playerWorldX = s.playerWorldY = s.playerRow = s.playerCol = 0; // default positions
            s.playerStamina = 0; // default stamina
            s.playerMaxStamina = 100; // default max stamina
            s.inventoryItemNames = new String[0]; // empty inventory
            s.selectedSlot = 0; // default selection
        }

        // Skins - READ FROM gp.skins (the source of truth)
        if (gp != null && gp.skins != null) { // if skins table exists
            s.unlockedSkins = new boolean[gp.skins.length]; // allocate unlocked array
            for (int i = 0; i < gp.skins.length; i++) { // iterate skins
                try { // try to read the unlocked marker
                    s.unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked"); // true if unlocked
                } catch (Throwable ignored) { // fallback if structure unexpected
                    s.unlockedSkins[i] = false; // default locked
                }
            }
        } else { // no skins available
            s.unlockedSkins = new boolean[0]; // empty array
        }
        s.equippedSkinIndex = (gp == null) ? 0 : gp.equippedSkinIndex; // store equipped skin index (safe default 0)

        // Keybinds
        if (gp != null && gp.keybinds != null) { // if keybinds exist
            s.keybinds = Arrays.copyOf(gp.keybinds, gp.keybinds.length); // copy keybind array
        } else { // otherwise
            s.keybinds = new int[0]; // empty array
        }

        // Tasks
        ArrayList<String> types = new ArrayList<>(); // temp list for task class names
        ArrayList<Integer> xPositions = new ArrayList<>(); // temp list for X positions
        ArrayList<Integer> yPositions = new ArrayList<>(); // temp list for Y positions
        ArrayList<Boolean> completed = new ArrayList<>(); // temp list for completion flags

        if (gp != null && gp.player != null && gp.player.tasksList != null) { // if tasks exist on the player
            for (Task t : gp.player.tasksList) { // iterate player's tasks
                if (t == null) continue; // skip null entries
                
                String className = t.getClass().getSimpleName(); // get simple class name of the task
                types.add(className); // record the type
                xPositions.add(t.worldX); // record world X
                yPositions.add(t.worldY); // record world Y
                completed.add(t.isCompleted()); // record completion state
            }
        }

        s.taskTypes = types.toArray(new String[0]); // convert types list to array
        s.taskWorldX = new int[xPositions.size()]; // allocate X array
        s.taskWorldY = new int[yPositions.size()]; // allocate Y array
        s.taskCompleted = new boolean[completed.size()]; // allocate completed array
        
        for (int i = 0; i < xPositions.size(); i++) { // copy lists into arrays
            s.taskWorldX[i] = xPositions.get(i); // set X
            s.taskWorldY[i] = yPositions.get(i); // set Y
            s.taskCompleted[i] = completed.get(i); // set completion
        }

        return s; // return the fully populated snapshot
    } // end snapshotFromGame

    // ---------------- APPLY SNAPSHOT ----------------
    private static void applySnapshotToGame(SaveData s, gamePanel gp) { // apply data from SaveData into the live game
        if (s == null || gp == null) return; // safety: nothing to apply

        // ===== SKINS FIRST - UPDATE gp.skins BEFORE ANYTHING ELSE =====
        if (s.unlockedSkins != null && s.unlockedSkins.length > 0 && gp.skins != null) { // if saved skins available and game has skins
            System.out.println("  Restoring skin unlock states:"); // log header
            for (int i = 0; i < s.unlockedSkins.length && i < gp.skins.length; i++) { // iterate clipped to both lengths
                String oldState = gp.skins[i][1][0]; // remember old state string
                gp.skins[i][1][0] = s.unlockedSkins[i] ? "unlocked" : "locked"; // set new locked/unlocked string
                System.out.println("    " + gp.skins[i][0][0] + ": " + oldState + " → " + gp.skins[i][1][0]); // log change
            }
            
            gp.equippedSkinIndex = s.equippedSkinIndex; // update equipped index on gp
            try { // try to set equippedSkin name if available
                gp.equippedSkin = gp.skins[s.equippedSkinIndex][0][0]; // set equipped skin string
            } catch (Throwable ignored) {} // ignore errors if index out of range
        }

        // Level/Map
        if (s.level >= 1 && s.level != gp.level) { // if saved level valid and different from current
            gp.level = s.level; // update level on game panel
            try { // attempt to reload the map for the new level
                if (gp.tileM != null) {
                    gp.tileM.loadMap("/maps/Level" + s.level + "Map.txt"); // load corresponding map file
                }
            } catch (Throwable ignored) {} // ignore map loading errors
        }

        // Player fields
        if (gp.player != null) { // if player exists in game panel
            try { // restore world position and tile coordinates
                gp.player.worldX = s.playerWorldX; // apply saved X
                gp.player.worldY = s.playerWorldY; // apply saved Y
                gp.player.row = s.playerRow; // apply saved row
                gp.player.col = s.playerCol; // apply saved col
            } catch (Throwable ignored) {} // ignore errors

            try { // restore stamina values
                gp.player.stamina = s.playerStamina; // apply saved stamina
                gp.player.maxStamina = s.playerMaxStamina; // apply saved max stamina
            } catch (Throwable ignored) {} // ignore errors

            gp.player.hasKey = s.hasKey; // restore key flag
            gp.player.hasRedKey = s.hasRedKey; // restore red key flag
            gp.player.hasGreenKey = s.hasGreenKey; // restore green key flag
            gp.player.hasBlueKey = s.hasBlueKey; // restore blue key flag
            gp.player.hasPebble = s.hasPebble; // restore pebble flag
            gp.player.hasCan = s.hasCan; // restore can flag
            gp.player.hasTray = s.hasTray; // restore tray flag
            gp.player.hasApple = s.hasApple; // restore apple flag
            gp.player.hasBread = s.hasBread; // restore bread flag
            gp.player.hasProteinBar = s.hasProteinBar; // restore protein bar flag
            gp.player.hasFlashlight = s.hasFlashlight; // restore flashlight flag
            
            // UPDATE PLAYER SKINS (gp.skins is already updated above)
            if (s.unlockedSkins != null && s.unlockedSkins.length > 0) { // if skin data present
                gp.player.unlockedSkins = Arrays.copyOf(s.unlockedSkins, s.unlockedSkins.length); // copy to player
                gp.player.equippedSkinIndex = s.equippedSkinIndex; // set player's equipped index
                gp.player.equippedSkin = gp.equippedSkin; // sync player's equippedSkin string with gp
                gp.player.getPlayerImage(); // refresh player sprites for the equipped skin
            }
        }

        // Keybinds
        if (s.keybinds != null && s.keybinds.length > 0) { // if saved keybinds present
            gp.keybinds = Arrays.copyOf(s.keybinds, s.keybinds.length); // copy into game panel
            try { // try to set human-readable key name strings
                gp.forwardKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[0]); // forward key name
                gp.backKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[1]); // back key name
                gp.leftKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[2]); // left key name
                gp.rightKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[3]); // right key name
                gp.sprintKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[4]); // sprint key name
                gp.crouchKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[5]); // crouch key name
                gp.interactKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[6]); // interact key name
                gp.throwKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[7]); // throw key name
                gp.dropKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[8]); // drop key name
            } catch (Throwable ignored) {} // ignore errors if array shorter than expected
        }

        // Inventory restore
        if (s.inventoryItemNames != null && gp.player != null) { // if saved inventory names exist
            gp.player.clearInventory(); // clear current inventory before restoring
            for (String itemName : s.inventoryItemNames) { // iterate saved item names
                if (itemName == null || itemName.isEmpty()) continue; // skip empty entries
                boolean found = false; // flag to check if item found in world
                for (int i = 0; i < gp.items.length; i++) { // search gp.items for a matching world item
                    Item worldItem = gp.items[i]; // reference to world item
                    if (worldItem != null && itemName.equals(worldItem.getName())) { // match by name
                        gp.player.inventory.add(worldItem); // add item to player's inventory
                        gp.items[i] = null; // remove item from world so it isn't duplicated
                        found = true; // mark as found
                        break; // stop searching
                    }
                }
                if (!found) { // if not found in world items
                    System.out.println("  ⚠ Saved item not found in world: " + itemName); // warn in console
                }
            }
            
            try { // attempt to restore selected slot reference
                int sel = Math.max(0, Math.min(s.selectedSlot, gp.player.inventory.size() - 1)); // clamp index into range
                if (!gp.player.inventory.isEmpty()) { // if inventory has items
                    gp.ui.selectedItem = gp.player.inventory.get(sel); // set UI selected item
                } else { // if inventory empty
                    gp.ui.selectedItem = null; // clear UI selection
                }
            } catch (Throwable ignored) {} // ignore errors while restoring selection
        }

        // Tasks restore
        if (s.taskTypes != null && s.taskTypes.length > 0) { // if there are saved tasks
            System.out.println("  Restoring " + s.taskTypes.length + " tasks..."); // log restoration start

            gp.player.tasksList.clear(); // clear existing player tasks list
            for (int i = 0; i < gp.tasks.length; i++) { // clear global gp.tasks array
                gp.tasks[i] = null; // set each slot to null
            }

            for (int i = 0; i < s.taskTypes.length; i++) { // iterate saved tasks
                String taskType = s.taskTypes[i]; // saved task class name
                int worldX = s.taskWorldX[i]; // saved X
                int worldY = s.taskWorldY[i]; // saved Y
                boolean isCompleted = s.taskCompleted[i]; // saved completion flag

                Task task = createTaskByType(taskType, gp); // instantiate the correct task subclass
                
                if (task != null) { // if creation succeeded
                    task.worldX = worldX; // set task world X
                    task.worldY = worldY; // set task world Y
                    task.setCompleted(isCompleted); // apply completion state
                    
                    gp.player.tasksList.add(task); // add to player's task list
                    gp.tasks[i] = task; // add to global tasks array
                    
                    System.out.println("    ✓ " + taskType + 
                        " at (" + worldX + "," + worldY + ") → " + 
                        (isCompleted ? "COMPLETED" : "incomplete")); // log restoration details
                } else { // if task creation failed
                    System.out.println("    ✗ Failed to create task: " + taskType); // warn
                }
            }
        }

        // Refresh visuals
        try { // attempt visual/coordinate refresh after loading
            if (gp.player != null) { // if player exists
                gp.player.row = gp.player.worldY / gp.tileSize; // recalc player's tile row
                gp.player.col = gp.player.worldX / gp.tileSize; // recalc player's tile column
            }
        } catch (Throwable ignored) {} // ignore any errors during refresh
    } // end applySnapshotToGame

    // ---------------- CREATE TASK BY TYPE ----------------
    private static Task createTaskByType(String taskType, gamePanel gp) { // instantiate task subclass by saved name
        try { // attempt instantiation
            switch (taskType) { // choose which Task subclass to create
                case "MathTask":
                    return new MathTask(gp); // create MathTask
                case "VaultSequenceTask":
                    return new VaultSequenceTask(gp); // create VaultSequenceTask
                case "CookingTask":
                    return new CookingTask(gp); // create CookingTask
                case "ButtonTask":
                    return new ButtonTask(gp); // create ButtonTask
                case "LogicPanelTask":
                    return new LogicPanelTask(gp); // create LogicPanelTask
                case "RiddleTask":
                    return new RiddleTask(gp); // create RiddleTask
                case "TileSelectTask":
                    return new TileSelectTask(gp); // create TileSelectTask
                case "FuseRepairTask":
                    return new FuseRepairTask(gp); // create FuseRepairTask
                case "PatternSwitchesTask":
                    return new PatternSwitchesTask(gp); // create PatternSwitchesTask
                default: // unknown or new task type not in switch
                    System.out.println("  ⚠ Unknown task type: " + taskType); // warn about unknown type
                    return null; // return null to indicate failure
            }
        } catch (Exception e) { // catch errors during instantiation
            System.out.println("  ✗ Error creating task " + taskType + ": " + e.getMessage()); // log error
            return null; // return null on exception
        }
    } // end createTaskByType

    // ---------------- FILE EXISTS ----------------
    public static boolean fileExists() { // checks whether the save file exists and looks valid
        File f = new File(SAVE_FILE); // reference to the save file
        if (!f.exists() || !f.isFile() || f.length() == 0) return false; // basic checks: exists, is a file, non-empty
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) { // try to read and verify content
            Object raw = ois.readObject(); // read saved object
            return (raw instanceof SaveData); // true only if it is a SaveData instance
        } catch (Throwable t) { // any error reading or parsing means file is not a valid save
            return false; // invalid or corrupted
        }
    } // end fileExists
} // end class save1
