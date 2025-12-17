/*
Name: Samir Bhagat
Course: ICS4U0
Date Completed: 12/8/2025
Assignment Title: Jone's Junction Final Project
File: saveSystem.java
Program Description:
The saveSystem class stores and restores data for one Jone's Junction save file.

Stores:
 - level number, level name, and checkpoint index
 - task list and which tasks are completed
 - stamina, inventory items, and selected slot
 - flashlight unlock state and on/off
 - unlocked character skins and equipped skin
 - keyboard keybinds for core actions
*/

import java.io.*; // for file reading and writing

public class saveSystem { // handles saving and loading game data

	private int levelNum; // current level 1->4
	private String levelName; // level name (Cell Block, Workshop, etc.)
	private int checkpointIndex; // which checkpoint inside the level

	private String[] tasks; // text descriptions of tasks in this save
	private boolean[] taskDone; // parallel array for completed tasks

	private double staminaBars; // current stamina (0.0 to 5.0)

	private String[] inventory; // item names in 3 inventory slots ("" if empty)
	private int selectedSlot; // selected inventory slot index 0-2

	private boolean flashlightUnlocked; // true if flashlight has been unlocked
	private boolean flashlightOn; // true if flashlight is currently on

	private boolean[] unlockedSkins; // unlocked character skins
	private int equippedSkinIndex; // which skin is currently equipped

	private String keyMoveForward; // key for moving forward
	private String keyMoveBackward; // key for moving backward
	private String keyMoveLeft; // key for moving left
	private String keyMoveRight; // key for moving right
	private String keySprint; // key for sprinting
	private String keyCrouch; // key for crouching
	private String keyInteract; // key for interact/use
	private String keyThrow; // key for throwing item
	private String keyDrop; // key for dropping item

	public saveSystem() { // no arg constructor sets default values
		levelNum = 1; // default start on level 1
		levelName = ""; // empty until game sets this
		checkpointIndex = 0; // start checkpoint

		tasks = new String[0]; // no tasks in save yet
		taskDone = new boolean[0]; // no completion flags yet

		staminaBars = 5.0; // full stamina by default

		inventory = new String[3]; // 3 inventory slots
		for (int i = 0; i < inventory.length; i++) { // loop slots
			inventory[i] = ""; // empty means no item
		}
		selectedSlot = 0; // first slot selected

		flashlightUnlocked = false; // flashlight not unlocked
		flashlightOn = false; // flashlight starts off

		unlockedSkins = new boolean[6]; // 6 skins total
		unlockedSkins[0] = true; // default skin unlocked
		for (int i = 1; i < unlockedSkins.length; i++) { // loop others
			unlockedSkins[i] = false; // locked until earned
		}
		equippedSkinIndex = 0; // default skin equipped

		keyMoveForward = "W"; // default keybinds from doc
		keyMoveBackward = "S";
		keyMoveLeft = "A";
		keyMoveRight = "D";
		keySprint = "Shift";
		keyCrouch = "C";
		keyInteract = "E";
		keyThrow = "Q";
		keyDrop = "R";
	}

	// setters

	public void setLevelInfo(int levelNum, String levelName, int checkpointIndex) { // store level data
		this.levelNum = levelNum;
		this.levelName = levelName;
		this.checkpointIndex = checkpointIndex;
	}

	public void setTasks(String[] tasks, boolean[] taskDone) { // store tasks and completion flags
		if (tasks == null || taskDone == null) { // if null arrays passed
			this.tasks = new String[0]; // use empty arrays
			this.taskDone = new boolean[0];
		} else {
			this.tasks = tasks; // store reference to tasks
			this.taskDone = taskDone; // store reference to flags
		}
	}


	public void setStamina(double staminaBars) { // store stamina value
		this.staminaBars = staminaBars;
	}

	public void setInventory(String[] inv, int selectedSlot) { // store inventory and selected slot
		if (inv != null && inv.length == 3) { // make sure size is 3
			for (int i = 0; i < 3; i++) { // loop slots
				if (inv[i] == null) {
					inventory[i] = "";
				} else {
					inventory[i] = inv[i];
				}
			}
		}
		this.selectedSlot = selectedSlot;
	}

	public void setFlashlightState(boolean unlocked, boolean on) { // store flashlight info
		flashlightUnlocked = unlocked;
		flashlightOn = on;
	}

	public void setSkins(boolean[] unlocked, int equippedSkinIndex) { // store skin data
		if (unlocked != null && unlocked.length == unlockedSkins.length) { // size match
			for (int i = 0; i < unlockedSkins.length; i++) { // copy flags
				unlockedSkins[i] = unlocked[i];
			}
		}
		this.equippedSkinIndex = equippedSkinIndex;
	}

	public void setKeybinds(String forward, String backward, String left, String right, String sprint, String crouch,
			String interact, String throwKey, String drop) { // store keybinds
		keyMoveForward = forward;
		keyMoveBackward = backward;
		keyMoveLeft = left;
		keyMoveRight = right;
		keySprint = sprint;
		keyCrouch = crouch;
		keyInteract = interact;
		keyThrow = throwKey;
		keyDrop = drop;
	}

	// getters

	public int getLevelNum() { // get saved level number
		return levelNum;
	}

	public String getLevelName() { // get saved level name
		return levelName;
	}

	public int getCheckpointIndex() { // get saved checkpoint
		return checkpointIndex;
	}

	public String[] getTasksCopy() { // get copy of tasks
		String[] copy = new String[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			copy[i] = tasks[i];
		}
		return copy;
	}

	public boolean[] getTaskDoneCopy() { // get copy of task flags
		boolean[] copy = new boolean[taskDone.length];
		for (int i = 0; i < taskDone.length; i++) {
			copy[i] = taskDone[i];
		}
		return copy;
	}


	public double getStaminaBars() { // get saved stamina
		return staminaBars;
	}

	public String[] getInventoryCopy() { // get copy of inventory
		String[] copy = new String[inventory.length];
		for (int i = 0; i < inventory.length; i++) {
			copy[i] = inventory[i];
		}
		return copy;
	}

	public int getSelectedSlot() { // get selected inventory slot
		return selectedSlot;
	}

	public boolean isFlashlightUnlocked() { // check if flashlight unlocked
		return flashlightUnlocked;
	}

	public boolean isFlashlightOn() { // check if flashlight is on
		return flashlightOn;
	}

	public boolean[] getUnlockedSkinsCopy() { // get copy of unlocked skins
		boolean[] copy = new boolean[unlockedSkins.length];
		for (int i = 0; i < unlockedSkins.length; i++) {
			copy[i] = unlockedSkins[i];
		}
		return copy;
	}

	public int getEquippedSkinIndex() { // get equipped skin
		return equippedSkinIndex;
	}

	public String getKeyMoveForward() { //get binds
		return keyMoveForward;
	}

	public String getKeyMoveBackward() {
		return keyMoveBackward;
	}

	public String getKeyMoveLeft() {
		return keyMoveLeft;
	}

	public String getKeyMoveRight() {
		return keyMoveRight;
	}

	public String getKeySprint() {
		return keySprint;
	}

	public String getKeyCrouch() {
		return keyCrouch;
	}

	public String getKeyInteract() {
		return keyInteract;
	}

	public String getKeyThrow() {
		return keyThrow;
	}

	public String getKeyDrop() {
		return keyDrop;
	}

	// save to text file

	public void saveToFile() { // write data to text file
		try { // try to connect to file
			PrintWriter output; // writer for file
			output = new PrintWriter(new FileWriter("savegame.txt")); // open file

			output.println(levelNum); // line 1: level number
			output.println(levelName); // line 2: level name
			output.println(checkpointIndex); // line 3: checkpoint index

			output.println(tasks.length); // line 4: how many tasks
			for (int i = 0; i < tasks.length; i++) { // loop tasks
				if (tasks[i] == null) {
					output.println(""); // blank task line
				} else {
					output.println(tasks[i]); // task text
				}
				output.println(taskDone[i]); // true/false for this task
			}

			output.println(staminaBars); // stamina amount

			output.println(inventory.length); // inventory size (3)
			for (int i = 0; i < inventory.length; i++) { // loop slots
				if (inventory[i] == null) {
					output.println(""); // empty item
				} else {
					output.println(inventory[i]); // item name
				}
			}
			output.println(selectedSlot); // selected slot index

			output.println(flashlightUnlocked); // flashlight unlocked
			output.println(flashlightOn); // flashlight on/off

			output.println(unlockedSkins.length); // number of skins
			for (int i = 0; i < unlockedSkins.length; i++) { // loop skins
				output.println(unlockedSkins[i]); // true/false per skin
			}
			output.println(equippedSkinIndex); // equipped skin index

			output.println(keyMoveForward); // forward key
			output.println(keyMoveBackward); // backward key
			output.println(keyMoveLeft); // left key
			output.println(keyMoveRight); // right key
			output.println(keySprint); // sprint key
			output.println(keyCrouch); // crouch key
			output.println(keyInteract); // interact key
			output.println(keyThrow); // throw key
			output.println(keyDrop); // drop key

			output.close(); // close file when done

		} catch (IOException e) { // if save fails
			System.out.println("Error saving game file."); // simple error
		}
	}

	// load from text file

	public void loadFromFile() { // read data from text file
		try { // try to open file
			BufferedReader input; // reader for file
			input = new BufferedReader(new FileReader("savegame.txt")); // open

			String line; // temp storage for each line

			line = input.readLine(); // level number
			levelNum = Integer.parseInt(line); // parse int

			levelName = input.readLine(); // level name

			line = input.readLine(); // checkpoint index
			checkpointIndex = Integer.parseInt(line); // parse int

			line = input.readLine(); // task count
			int taskCount = Integer.parseInt(line); // parse int

			tasks = new String[taskCount]; // create task array
			taskDone = new boolean[taskCount]; // create flags array

			for (int i = 0; i < taskCount; i++) { // loop tasks
				tasks[i] = input.readLine(); // task text
				line = input.readLine(); // true/false
				taskDone[i] = Boolean.parseBoolean(line); // parse bool
			}


			line = input.readLine(); // stamina
			staminaBars = Double.parseDouble(line);

			line = input.readLine(); // inventory size
			int invSize = Integer.parseInt(line);

			inventory = new String[invSize]; // create inventory
			for (int i = 0; i < invSize; i++) { // loop slots
				inventory[i] = input.readLine(); // item name (may be "")
			}

			line = input.readLine(); // selected slot
			selectedSlot = Integer.parseInt(line);

			line = input.readLine(); // flashlight unlocked
			flashlightUnlocked = Boolean.parseBoolean(line);

			line = input.readLine(); // flashlight on
			flashlightOn = Boolean.parseBoolean(line);

			line = input.readLine(); // skin count
			int skinCount = Integer.parseInt(line);

			unlockedSkins = new boolean[skinCount]; // create skins array
			for (int i = 0; i < skinCount; i++) { // loop skins
				line = input.readLine(); // true/false per skin
				unlockedSkins[i] = Boolean.parseBoolean(line);
			}

			line = input.readLine(); // equipped skin index
			equippedSkinIndex = Integer.parseInt(line);

			keyMoveForward = input.readLine(); // forward key
			keyMoveBackward = input.readLine(); // backward key
			keyMoveLeft = input.readLine(); // left key
			keyMoveRight = input.readLine(); // right key
			keySprint = input.readLine(); // sprint key
			keyCrouch = input.readLine(); // crouch key
			keyInteract = input.readLine(); // interact key
			keyThrow = input.readLine(); // throw key
			keyDrop = input.readLine(); // drop key

			input.close(); // close file
			// System.out.println("Game loaded."); // optional

		} catch (IOException e) { // if file missing or read fails
			System.out.println("Error loading game file."); // simple error
		} catch (NumberFormatException e) { // if parse fails
			System.out.println("Save file is invalid or corrupted."); // simple error
		}
	}

}