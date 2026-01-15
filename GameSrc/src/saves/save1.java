package saves;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import Item.Item;
import main.AssetSetter;
import main.gamePanel;
import task.*;

/**
 * save2 - Saves task types, positions, and completion status
 */
public class save1 {

    private static final String SAVE_FILE = "save1.dat";

    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L; // Changed version for new format

        int level;
        int playerWorldX;
        int playerWorldY;
        int playerRow;
        int playerCol;
        float playerStamina;
        float playerMaxStamina;

        String[] inventoryItemNames;
        int selectedSlot;

        boolean hasKey, hasRedKey, hasGreenKey, hasBlueKey;
        boolean hasPebble, hasCan, hasTray, hasApple, hasBread, hasProteinBar, hasFlashlight;

        boolean[] unlockedSkins;
        int equippedSkinIndex;

        int[] keybinds;

        // Enhanced task data
        String[] taskTypes;      // Class names (e.g., "MathTask", "VaultSequenceTask")
        int[] taskWorldX;        // X positions
        int[] taskWorldY;        // Y positions
        boolean[] taskCompleted; // Completion flags
    }

    // ---------------- SAVE ----------------
    public static void saveGame(gamePanel gp) {
        SaveData s = snapshotFromGame(gp);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(s);
            System.out.println("✓ Game saved to " + SAVE_FILE);
            System.out.println("  - Level: " + s.level);
            System.out.println("  - Tasks saved: " + (s.taskTypes != null ? s.taskTypes.length : 0));
            if (s.taskTypes != null && s.taskTypes.length > 0) {
                for (int i = 0; i < s.taskTypes.length; i++) {
                    System.out.println("    • " + s.taskTypes[i] + 
                        " at (" + s.taskWorldX[i] + "," + s.taskWorldY[i] + ") = " + 
                        (s.taskCompleted[i] ? "✓" : "○"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("✗ Failed to save game: " + e.getMessage());
        }
    }

    // ---------------- LOAD ----------------
    public static void loadGame(gamePanel gp) {
        File f = new File(SAVE_FILE);
        if (!f.exists()) {
            System.out.println("✗ No save file found (" + SAVE_FILE + ")");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Object raw = ois.readObject();
            if (!(raw instanceof SaveData)) {
                System.out.println("✗ Save file has unexpected format");
                return;
            }
            SaveData s = (SaveData) raw;

            System.out.println("✓ Loading save from " + SAVE_FILE);
            System.out.println("  - Level: " + s.level);
            System.out.println("  - Tasks to restore: " + (s.taskTypes != null ? s.taskTypes.length : 0));

            // Apply the snapshot
            applySnapshotToGame(s, gp);

            System.out.println("✓ Game loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("✗ Failed to load save: " + e.getMessage());
        }
    }

    // ---------------- SNAPSHOT ----------------
    private static SaveData snapshotFromGame(gamePanel gp) {
        SaveData s = new SaveData();
        s.level = (gp == null) ? 1 : gp.level;

        if (gp != null && gp.player != null) {
            s.playerWorldX = gp.player.worldX;
            s.playerWorldY = gp.player.worldY;
            s.playerRow = gp.player.row;
            s.playerCol = gp.player.col;
            
            try {
                s.playerStamina = gp.player.stamina;
                s.playerMaxStamina = gp.player.maxStamina;
            } catch (Throwable t) {
                s.playerStamina = 0;
                s.playerMaxStamina = 100;
            }

            // Inventory
            try {
                int invSize = Math.min(gp.player.inventory.size(), gp.player.INVENTORY_SIZE);
                s.inventoryItemNames = new String[invSize];
                for (int i = 0; i < invSize; i++) {
                    Item it = gp.player.inventory.get(i);
                    s.inventoryItemNames[i] = (it == null ? "" : it.getName());
                }
            } catch (Throwable t) {
                s.inventoryItemNames = new String[0];
            }

            // Selected slot
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

            // Flags
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
            s.playerWorldX = s.playerWorldY = s.playerRow = s.playerCol = 0;
            s.playerStamina = 0;
            s.playerMaxStamina = 100;
            s.inventoryItemNames = new String[0];
            s.selectedSlot = 0;
        }

        // Skins
        if (gp != null && gp.player != null && gp.player.unlockedSkins != null) {
            s.unlockedSkins = Arrays.copyOf(gp.player.unlockedSkins, gp.player.unlockedSkins.length);
        } else if (gp != null && gp.skins != null) {
            s.unlockedSkins = new boolean[gp.skins.length];
            for (int i = 0; i < gp.skins.length; i++) {
                try {
                    s.unlockedSkins[i] = gp.skins[i][1][0].equalsIgnoreCase("unlocked");
                } catch (Throwable ignored) {
                    s.unlockedSkins[i] = false;
                }
            }
        } else {
            s.unlockedSkins = new boolean[0];
        }
        s.equippedSkinIndex = (gp == null) ? 0 : gp.equippedSkinIndex;

        // Keybinds
        if (gp != null && gp.keybinds != null) {
            s.keybinds = Arrays.copyOf(gp.keybinds, gp.keybinds.length);
        } else {
            s.keybinds = new int[0];
        }

        // ---------------- ENHANCED TASK SAVING ----------------
        ArrayList<String> types = new ArrayList<>();
        ArrayList<Integer> xPositions = new ArrayList<>();
        ArrayList<Integer> yPositions = new ArrayList<>();
        ArrayList<Boolean> completed = new ArrayList<>();

        if (gp != null && gp.player != null && gp.player.tasksList != null) {
            for (Task t : gp.player.tasksList) {
                if (t == null) continue;
                
                // Save the class type (e.g., "MathTask")
                String className = t.getClass().getSimpleName();
                types.add(className);
                xPositions.add(t.worldX);
                yPositions.add(t.worldY);
                completed.add(t.isCompleted());
            }
        }

        s.taskTypes = types.toArray(new String[0]);
        s.taskWorldX = new int[xPositions.size()];
        s.taskWorldY = new int[yPositions.size()];
        s.taskCompleted = new boolean[completed.size()];
        
        for (int i = 0; i < xPositions.size(); i++) {
            s.taskWorldX[i] = xPositions.get(i);
            s.taskWorldY[i] = yPositions.get(i);
            s.taskCompleted[i] = completed.get(i);
        }

        return s;
    }

    // ---------------- APPLY SNAPSHOT ----------------
    private static void applySnapshotToGame(SaveData s, gamePanel gp) {
        if (s == null || gp == null) return;

        // Level/Map
        if (s.level >= 1 && s.level != gp.level) {
            gp.level = s.level;
            try {
                if (gp.tileM != null) {
                    gp.tileM.loadMap("/maps/Level" + s.level + "Map.txt");
                }
            } catch (Throwable ignored) {}
        }

        // Player fields
        if (gp.player != null) {
            try {
                gp.player.worldX = s.playerWorldX;
                gp.player.worldY = s.playerWorldY;
                gp.player.row = s.playerRow;
                gp.player.col = s.playerCol;
            } catch (Throwable ignored) {}
            
            try {
                gp.player.stamina = s.playerStamina;
                gp.player.maxStamina = s.playerMaxStamina;
            } catch (Throwable ignored) {}

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

        // Skins
        if (s.unlockedSkins != null && gp.player != null) {
            gp.player.unlockedSkins = Arrays.copyOf(s.unlockedSkins, s.unlockedSkins.length);
            gp.player.equippedSkinIndex = s.equippedSkinIndex;
            gp.equippedSkinIndex = s.equippedSkinIndex;
            try {
                gp.equippedSkin = gp.skins[s.equippedSkinIndex][0][0];
            } catch (Throwable ignored) {}
        }

        // Keybinds
        if (s.keybinds != null && s.keybinds.length > 0) {
            gp.keybinds = Arrays.copyOf(s.keybinds, s.keybinds.length);
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

        // Inventory restore
        if (s.inventoryItemNames != null && gp.player != null) {
            gp.player.clearInventory();
            for (String itemName : s.inventoryItemNames) {
                if (itemName == null || itemName.isEmpty()) continue;
                boolean found = false;
                for (int i = 0; i < gp.items.length; i++) {
                    Item worldItem = gp.items[i];
                    if (worldItem != null && itemName.equals(worldItem.getName())) {
                        gp.player.inventory.add(worldItem);
                        gp.items[i] = null;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("  ⚠ Saved item not found in world: " + itemName);
                }
            }
            
            try {
                int sel = Math.max(0, Math.min(s.selectedSlot, gp.player.inventory.size() - 1));
                if (!gp.player.inventory.isEmpty()) {
                    gp.ui.selectedItem = gp.player.inventory.get(sel);
                } else {
                    gp.ui.selectedItem = null;
                }
            } catch (Throwable ignored) {}
        }

        // ---------------- RESTORE TASKS EXACTLY ----------------
        if (s.taskTypes != null && s.taskTypes.length > 0) {
            System.out.println("  Restoring " + s.taskTypes.length + " tasks...");

            // Clear existing tasks
            gp.player.tasksList.clear();
            for (int i = 0; i < gp.tasks.length; i++) {
                gp.tasks[i] = null;
            }

            // Recreate each saved task
            for (int i = 0; i < s.taskTypes.length; i++) {
                String taskType = s.taskTypes[i];
                int worldX = s.taskWorldX[i];
                int worldY = s.taskWorldY[i];
                boolean isCompleted = s.taskCompleted[i];

                Task task = createTaskByType(taskType, gp);
                
                if (task != null) {
                    task.worldX = worldX;
                    task.worldY = worldY;
                    task.setCompleted(isCompleted);
                    
                    gp.player.tasksList.add(task);
                    gp.tasks[i] = task;
                    
                    System.out.println("    ✓ " + taskType + 
                        " at (" + worldX + "," + worldY + ") → " + 
                        (isCompleted ? "COMPLETED" : "incomplete"));
                } else {
                    System.out.println("    ✗ Failed to create task: " + taskType);
                }
            }
        }

        // Refresh visuals
        try {
            if (gp.player != null) {
                gp.player.getPlayerImage();
                gp.player.row = gp.player.worldY / gp.tileSize;
                gp.player.col = gp.player.worldX / gp.tileSize;
            }
        } catch (Throwable ignored) {}
    }

    // ---------------- CREATE TASK BY TYPE ----------------
    private static Task createTaskByType(String taskType, gamePanel gp) {
        try {
            switch (taskType) {
                case "MathTask":
                    return new MathTask(gp);
                case "VaultSequenceTask":
                    return new VaultSequenceTask(gp);
                case "CookingTask":
                    return new CookingTask(gp);
                case "ButtonTask":
                    return new ButtonTask(gp);
                case "LogicPanelTask":
                    return new LogicPanelTask(gp);
                case "RiddleTask":
                    return new RiddleTask(gp);
                case "TileSelectTask":
                    return new TileSelectTask(gp);
                case "FuseRepairTask":
                    return new FuseRepairTask(gp);
                default:
                    System.out.println("  ⚠ Unknown task type: " + taskType);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("  ✗ Error creating task " + taskType + ": " + e.getMessage());
            return null;
        }
    }

    // ---------------- FILE EXISTS ----------------
    public static boolean fileExists() {
        File f = new File(SAVE_FILE);
        if (!f.exists() || !f.isFile() || f.length() == 0) return false;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object raw = ois.readObject();
            return (raw instanceof SaveData);
        } catch (Throwable t) {
            return false;
        }
    }
}