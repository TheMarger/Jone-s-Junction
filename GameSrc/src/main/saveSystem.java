package main;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import Item.Item;
import task.Task;

/**
 * saveSystem
 *
 * Binary snapshot save/load of essential game state.
 * Usage: saveSystem.saveGame(gp);  // to save
 *        saveSystem.loadGame(gp);  // to load/apply
 */
public class saveSystem {

    private static final String SAVE_FILE = "savegame.dat";

    // Serializable snapshot of the game state
    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        // Level/map
        int level;
        // Player basic positioning + stamina
        int playerWorldX;
        int playerWorldY;
        int playerRow;
        int playerCol;
        float playerStamina;
        float playerMaxStamina;

        // Inventory saved as item names
        String[] inventoryItemNames; // length <= INVENTORY_SIZE

        // Selected slot index (0-based) - best-effort based on gp.ui.selectedItem
        int selectedSlot;

        // Item-related boolean flags (from player)
        boolean hasKey;
        boolean hasRedKey;
        boolean hasGreenKey;
        boolean hasBlueKey;
        boolean hasPebble;
        boolean hasCan;
        boolean hasTray;
        boolean hasApple;
        boolean hasBread;
        boolean hasProteinBar;
        boolean hasFlashlight;

        // Skins
        boolean[] unlockedSkins;
        int equippedSkinIndex;

        // Keybinds
        int[] keybinds;

        // Tasks: save task names and completion flags
        String[] taskNames;
        boolean[] taskDone;
    }

    /**
     * Create a SaveData snapshot from the live gamePanel state.
     */
    private static SaveData snapshotFromGame(gamePanel gp) {
        SaveData s = new SaveData();

        // Level
        s.level = gp.level;

        // Player pos & stamina (best-effort fields)
        if (gp.player != null) {
            s.playerWorldX = gp.player.worldX;
            s.playerWorldY = gp.player.worldY;
            s.playerRow = gp.player.row;
            s.playerCol = gp.player.col;

            // stamina fields exist in player
            try {
                s.playerStamina = gp.player.stamina;
                s.playerMaxStamina = gp.player.maxStamina;
            } catch (Throwable t) {
                s.playerStamina = 0;
                s.playerMaxStamina = 100;
            }

            // Inventory -> names
            int invSize = Math.min(gp.player.inventory.size(), gp.player.INVENTORY_SIZE);
            s.inventoryItemNames = new String[invSize];
            for (int i = 0; i < invSize; i++) {
                Item it = gp.player.inventory.get(i);
                s.inventoryItemNames[i] = (it == null ? "" : it.getName());
            }

            // selected slot: guess by matching ui.selectedItem to inventory index
            s.selectedSlot = 0;
            try {
                Item sel = (gp.ui == null ? null : gp.ui.selectedItem);
                if (sel != null) {
                    for (int i = 0; i < gp.player.inventory.size(); i++) {
                        if (gp.player.inventory.get(i) == sel) {
                            s.selectedSlot = i;
                            break;
                        }
                    }
                }
            } catch (Throwable ignored) {}

            // flags
            s.hasKey = gp.player.hasKey;
            s.hasRedKey = gp.player.hasRedKey;
            s.hasGreenKey = gp.player.hasGreenKey;
            s.hasBlueKey = gp.player.hasBlueKey;
            s.hasPebble = gp.player.hasPebble;
            s.hasCan = gp.player.hasCan;
            s.hasTray = gp.player.hasTray;
            s.hasApple = gp.player.hasApple;
            s.hasBread = gp.player.hasBread;
            s.hasProteinBar = gp.player.hasProteinBar;
            s.hasFlashlight = gp.player.hasFlashlight;
        } else {
            // defaults
            s.playerWorldX = 0;
            s.playerWorldY = 0;
            s.playerRow = 0;
            s.playerCol = 0;
            s.playerStamina = 0;
            s.playerMaxStamina = 100;
            s.inventoryItemNames = new String[0];
            s.selectedSlot = 0;
        }

        // skins
        if (gp.player != null && gp.player.unlockedSkins != null) {
            s.unlockedSkins = Arrays.copyOf(gp.player.unlockedSkins, gp.player.unlockedSkins.length);
        } else {
            // fallback: try to read from gamePanel.skins array (locked/unlocked string)
            if (gp.skins != null) {
                s.unlockedSkins = new boolean[gp.skins.length];
                for (int i = 0; i < gp.skins.length; i++) {
                    s.unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked");
                }
            } else {
                s.unlockedSkins = new boolean[0];
            }
        }
        s.equippedSkinIndex = gp.equippedSkinIndex;

        // keybinds (copy)
        if (gp.keybinds != null) {
            s.keybinds = Arrays.copyOf(gp.keybinds, gp.keybinds.length);
        } else {
            s.keybinds = new int[0];
        }

        // tasks: store names + completion flags (best-effort)
        if (gp.tasks != null) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Boolean> dones = new ArrayList<>();
            for (Task t : gp.tasks) {
                if (t == null) continue;
                try {
                    String name = t.getName();
                    names.add(name);
                    // try common getter names for completion
                    boolean done = tryGetTaskCompleted(t);
                    dones.add(done);
                } catch (Throwable ex) {
                    // if getName missing just skip
                }
            }
            s.taskNames = names.toArray(new String[0]);
            s.taskDone = new boolean[dones.size()];
            for (int i = 0; i < dones.size(); i++) s.taskDone[i] = dones.get(i);
        } else {
            s.taskNames = new String[0];
            s.taskDone = new boolean[0];
        }

        return s;
    }

    /**
     * Save the current game (gp) to disk.
     * Call from gamePanel.saveGame() or anywhere with 'this' gp reference:
     *    saveSystem.saveGame(this);
     */
    public static void saveGame(gamePanel gp) {
        SaveData s = snapshotFromGame(gp);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(s);
            System.out.println("Game saved to " + SAVE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save game: " + e.getMessage());
        }
    }

    /**
     * Load the saved file and apply the snapshot to the provided gamePanel.
     * Call early after setupGame() finished creating gp.player, gp.items, gp.tasks, etc:
     *    saveSystem.loadGame(this);
     */
    public static void loadGame(gamePanel gp) {
        File f = new File(SAVE_FILE);
        if (!f.exists()) {
            System.out.println("No save file found (" + SAVE_FILE + ").");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            SaveData s = (SaveData) ois.readObject();
            applySnapshotToGame(s, gp);
            System.out.println("Game loaded from " + SAVE_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load save: " + e.getMessage());
        }
    }

    /**
     * Apply the SaveData snapshot into live game objects (gamePanel + player).
     * This is best-effort and will skip fields it can't sensibly restore.
     */
    private static void applySnapshotToGame(SaveData s, gamePanel gp) {
        if (s == null || gp == null) return;

        // Level: change current level and reload map if necessary
        if (s.level >= 1 && s.level != gp.level) {
            gp.level = s.level;
            try {
                // load appropriate map file (your project uses tileM.loadMap)
                if (gp.tileM != null) {
                    String path = "/maps/Level" + s.level + "Map.txt";
                    gp.tileM.loadMap(path);
                }
            } catch (Throwable ignored) {}
        }

        // Player basic fields
        if (gp.player != null) {
            gp.player.worldX = s.playerWorldX;
            gp.player.worldY = s.playerWorldY;
            gp.player.row = s.playerRow;
            gp.player.col = s.playerCol;

            try {
                gp.player.stamina = s.playerStamina;
                gp.player.maxStamina = s.playerMaxStamina;
            } catch (Throwable ignored) {}

            // flags
            gp.player.hasKey = s.hasKey;
            gp.player.hasRedKey = s.hasRedKey;
            gp.player.hasGreenKey = s.hasGreenKey;
            gp.player.hasBlueKey = s.hasBlueKey;
            gp.player.hasPebble = s.hasPebble;
            gp.player.hasCan = s.hasCan;
            gp.player.hasTray = s.hasTray;
            gp.player.hasApple = s.hasApple;
            gp.player.hasBread = s.hasBread;
            gp.player.hasProteinBar = s.hasProteinBar;
            gp.player.hasFlashlight = s.hasFlashlight;
        }

        // skins
        if (s.unlockedSkins != null && gp.player != null) {
            gp.player.unlockedSkins = Arrays.copyOf(s.unlockedSkins, s.unlockedSkins.length);
            gp.player.equippedSkinIndex = s.equippedSkinIndex;
            gp.equippedSkinIndex = s.equippedSkinIndex;
            try {
                gp.equippedSkin = gp.skins[s.equippedSkinIndex][0][0];
            } catch (Throwable ignored) {}
        }

        // keybinds - copy then update human-readable key names
        if (s.keybinds != null && s.keybinds.length > 0) {
            gp.keybinds = Arrays.copyOf(s.keybinds, s.keybinds.length);
            // update display names used by UI
            try {
                gp.forwardKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[0]);
                gp.backKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[1]);
                gp.leftKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[2]);
                gp.rightKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[3]);
                gp.sprintKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[4]);
                gp.crouchKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[5]);
                gp.interactKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[6]);
                gp.throwKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[7]);
                gp.dropKey = java.awt.event.KeyEvent.getKeyText(gp.keybinds[8]);
            } catch (Throwable ignored) {}
        }

        // Inventory: best-effort: look for matching world item by name and move it to player.inventory
        if (s.inventoryItemNames != null && gp.player != null) {
            gp.player.clearInventory();
            for (String itemName : s.inventoryItemNames) {
                if (itemName == null || itemName.isEmpty()) continue;
                boolean found = false;
                for (int i = 0; i < gp.items.length; i++) {
                    Item worldItem = gp.items[i];
                    if (worldItem != null && itemName.equals(worldItem.getName())) {
                        gp.player.inventory.add(worldItem);
                        gp.items[i] = null; // remove from world
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Saved item not found in world: " + itemName + " (skipped).");
                    // Optionally: you could attempt to construct an Item by name if you have a factory
                }
            }

            // set UI selectedItem according to saved selectedSlot (best-effort)
            try {
                int sel = Math.max(0, Math.min(s.selectedSlot, gp.player.inventory.size() - 1));
                if (!gp.player.inventory.isEmpty()) {
                    gp.ui.selectedItem = gp.player.inventory.get(sel);
                } else {
                    gp.ui.selectedItem = null;
                }
            } catch (Throwable ignored) {}
        }

        // Tasks: find matching task by name in gp.tasks[] and try to set completion state
        if (s.taskNames != null && gp.tasks != null) {
            for (int i = 0; i < s.taskNames.length; i++) {
                String tName = s.taskNames[i];
                boolean done = (s.taskDone != null && i < s.taskDone.length) ? s.taskDone[i] : false;
                // find in gp.tasks
                for (Task t : gp.tasks) {
                    if (t == null) continue;
                    try {
                        String name = t.getName();
                        if (tName.equals(name)) {
                            trySetTaskCompleted(t, done);
                            break;
                        }
                    } catch (Throwable ignored) {}
                }
            }
        }

        // After applying, refresh images / player frames if needed
        try {
            gp.player.getPlayerImage();
        } catch (Throwable ignored) {}

        // Recompute player's row/col if needed
        try {
            gp.player.row = gp.player.worldY / gp.tileSize;
            gp.player.col = gp.player.worldX / gp.tileSize;
        } catch (Throwable ignored) {}

    
    }

    // ---------- reflection helpers for Task completion ----------

    private static boolean tryGetTaskCompleted(Task t) {
        String[] candidates = new String[] { "isCompleted", "isDone", "getCompleted", "isComplete", "getIsComplete" };
        for (String name : candidates) {
            try {
                Method m = t.getClass().getMethod(name);
                Object res = m.invoke(t);
                if (res instanceof Boolean) return (Boolean) res;
            } catch (NoSuchMethodException nsme) {
                // try next
            } catch (Throwable e) {
                // ignore any other reflection issues
            }
        }
        return false;
    }

    private static void trySetTaskCompleted(Task t, boolean value) {
        String[] setterCandidates = new String[] { "setCompleted", "setDone", "markCompleted", "setIsComplete" };
        for (String name : setterCandidates) {
            try {
                Method m = t.getClass().getMethod(name, boolean.class);
                m.invoke(t, value);
                return;
            } catch (NoSuchMethodException nsme) {
                // try next
            } catch (Throwable e) {
                // ignore issues
            }
        }
        // if no setter found we skip (best-effort)
    }
    
    public static boolean saveFileExists() {
		File f = new File(SAVE_FILE);
		return f.exists();
	}
    
}
