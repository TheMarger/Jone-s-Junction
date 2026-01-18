/*
 * Name: Rafay
 * Date: 1/19/2026
 * Course Code: ICS4U0
 * Description: gamePanel class is the main game engine and rendering panel that extends JPanel
 *              and implements Runnable for the game loop. It manages all game states, coordinates
 *              updates for all entities (player, NPCs, guards, items, tasks), handles rendering,
 *              processes mouse and keyboard input, manages save/load functionality, and controls
 *              the game's core loop running at 60 FPS. This class acts as the central hub that
 *              connects all game systems including collision detection, tile management, sound,
 *              UI, and event handling.
 */

package main; // Declares this class belongs to the main package

import java.awt.Color; // Imports Color class for setting background color
import java.awt.Dimension; // Imports Dimension class for setting panel size
import java.awt.Graphics; // Imports Graphics class for basic rendering
import java.awt.Graphics2D; // Imports Graphics2D class for advanced 2D rendering
import java.awt.event.KeyEvent; // Imports KeyEvent class for keyboard input handling

import entity.entity; // Imports the base entity class
import entity.player; // Imports the player class
import gaurd.gaurd; // Imports the guard base class
import task.Task; // Imports the Task class
import tile.TileManager; // Imports TileManager for map rendering

import javax.swing.JPanel; // Imports JPanel class to extend for custom rendering panel
import saves.*; // Imports all classes from the saves package
import Item.Item; // Imports the Item class
import tile.TileManager; // Imports TileManager again (duplicate import)

public class gamePanel extends JPanel implements Runnable { // Declares gamePanel class that extends JPanel for rendering and implements Runnable for threading

	// Screen settings
	public final int originalTileSize = 16; // Original size of tiles before scaling (16x16 pixels)
	public final int scale = 4; // Multiplier to scale tiles up by 4x
	public final int tileSize = originalTileSize * scale; // Final rendered tile size (64x64 pixels)
	public final int maxScreenCol = 16; // Number of tile columns visible on screen
	public final int maxScreenRow = 12; // Number of tile rows visible on screen
	public final int screenWidth = tileSize * maxScreenCol; // Total screen width in pixels (1024 pixels)
	public final int screenHeight = tileSize * maxScreenRow; // Total screen height in pixels (768 pixels)
	public boolean playingMusic = false; // Flag to track if background music is currently playing
	
	public final int maxWorldCol = 50; // Total number of tile columns in the game world
	public final int maxWorldRow = 50; // Total number of tile rows in the game world
	public final int worldWidth = tileSize * maxWorldCol; // Total world width in pixels
	public final int worldHeight = tileSize * maxWorldRow; // Total world height in pixels
	public int currentItemIndex = 0; // Index of currently selected item in inventory
	public int equippedSkinIndex = 0; // Index of currently equipped player skin (0 = default)
	public String equippedSkin; // String name of equipped skin for readability
	public int level = 1; // Current game level the player is on
	public int startLevel = 1; // The level the player started the game at
	// mouse / selection state 
	public int mouseX = 0, mouseY = 0;                  // Last recorded mouse X and Y coordinates on screen
	public int hoveredTileCol = -1, hoveredTileRow = -1; // Column and row of tile currently under mouse cursor (-1 means none)
	public int selectedThrowCol = -1, selectedThrowRow = -1; // Column and row of tile selected as throw target (-1 means none)
	public boolean mouseClicked = false;               // Flag that becomes true when mouse is clicked
	
	// saves
	public save1 save1 = new save1(); // Creates save slot 1 object
	public save2 save2 = new save2(); // Creates save slot 2 object
	public save3 save3 = new save3(); // Creates save slot 3 object
	
	// FPS
	public final int FPS = 60; // Target frames per second for game loop
	
	public Task tasks[] = new Task[10]; // Array to hold up to 10 active tasks in the world
		
		// keybinds: 0=forward,1=back,2=left,3=right,4=sprint,5=crouch,6=interact,7=throw,8=drop
		public int[] keybinds = new int[] { // Array storing virtual key codes for all game controls
		    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, // Movement keys (W, S, A, D)
		    KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_E, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_ESCAPE, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3 // Action keys (Shift, Ctrl, E, Q, R, Esc, 1, 2, 3)
		};
		
		public String forwardKey = KeyEvent.getKeyText(keybinds[0]); // Converts forward keybind to readable text (e.g., "W")
		public String backKey = KeyEvent.getKeyText(keybinds[1]); // Converts back keybind to readable text
		public String leftKey = KeyEvent.getKeyText(keybinds[2]); // Converts left keybind to readable text
		public String rightKey = KeyEvent.getKeyText(keybinds[3]); // Converts right keybind to readable text
		public String sprintKey = KeyEvent.getKeyText(keybinds[4]); // Converts sprint keybind to readable text
		public String crouchKey = KeyEvent.getKeyText(keybinds[5]); // Converts crouch keybind to readable text
		public String interactKey = KeyEvent.getKeyText(keybinds[6]); // Converts interact keybind to readable text
		public String throwKey = KeyEvent.getKeyText(keybinds[7]); // Converts throw keybind to readable text
		public String dropKey = KeyEvent.getKeyText(keybinds[8]); // Converts drop keybind to readable text
		public String escapeKey = KeyEvent.getKeyText(keybinds[9]); // Converts escape keybind to readable text
		
		
		public final String[][][] skins = { // 3D array storing all available player skins with their properties
		    {   // Rabbit
		    	{"Rabbit"}, // Skin name
		    	{"unlocked"}, // Lock status
		        {"/assets/Rabbit.png", "/Rabbit/boy_up_1.png", "/Rabbit/boy_up_2.png", "/Rabbit/boy_down_1.png", "/Rabbit/boy_down_2.png", "/Rabbit/boy_right_1.png", "/Rabbit/boy_right_2.png", "/Rabbit/boy_left_1.png", "/Rabbit/boy_left_2.png"} // Sprite paths for all directions and animation frames
		    },
		    
		    {
		    	//Boy
		    	{"Boy"}, // Skin name
		    	{"unlocked"}, // Lock status
		        {"/player/boy_down_1.png", "/player/boy_up_1.png", "/player/boy_up_2.png", "/player/boy_down_1.png", "/player/boy_down_2.png", "/player/boy_right_1.png", "/player/boy_right_2.png", "/player/boy_left_1.png", "/player/boy_left_2.png"} // Sprite paths
		    },
		    
		    {  
		    	// Old Timer
		    	{"Old Timer"}, // Skin name
		    	{"locked"}, // Lock status
		        {"/assets/OldTimer.png"}, // Sprite path
		    },
		    {   // Froseph
		    	{"Froseph"}, // Skin name
		    	{"locked"}, // Lock status
		        {"/assets/Froseph.png"}, // Sprite path
		    },
		    {   // Lifer
		    	{"Lifer"}, // Skin name
		    	{"locked"}, // Lock status
		        {"/assets/Lifer.png"}, // Sprite path
		    },
		    {   // BillyGoat
		    	{"Billy Goat"}, // Skin name
		    	{"locked"}, // Lock status
		        {"/assets/BillyGoat.png"}, // Sprite path
		    },
		    {   // Marv
		    	{"Marv"}, // Skin name
		    	{"locked"}, // Lock status
		        {"/assets/Marv.png"}, // Sprite path
		    }
		};
	
	public Item items[] = new Item[20]; // Array to hold up to 20 items in the game world
	public entity npc[] = new entity[10]; // Array to hold up to 10 NPCs in the game world
	public gaurd gaurds[] = new gaurd[20]; // Array to hold up to 20 guards in the game world
	Sound music = new Sound(); // Sound object for background music
	Sound soundEffect = new Sound(); // Sound object for sound effects

	public int gameState; // Variable storing the current state of the game
	public final int titleState = 0; // Constant representing title screen state
	public final int playState = 1; // Constant representing active gameplay state
	public final int pauseState = 2; // Constant representing paused state
	public final int dialogueState = 3; // Constant representing dialogue/conversation state
	public final int deathState = 4; // Constant representing death screen state
	public final int taskState = 5; // Constant representing task UI state
	public boolean speedRunState = false; // Flag indicating if speedrun mode is active
	
	public int speedRunTimerFrames = 0; // Counter tracking elapsed frames in speedrun mode
	public boolean speedRunLost = false; // Flag indicating if speedrun timer has expired
		
	public TileManager tileM; // Manager object for loading and rendering map tiles
	public UtilityTool uTool; // Utility object for helper functions like hitbox drawing
	public keyHandler keyH; // Handler object for processing keyboard input
	public CollisionChecker cChecker; // Checker object for detecting collisions
	public AssetSetter aSetter; // Setter object for placing items, NPCs, guards, and tasks
	public player player; // The player character object
	public UserInterface ui; // UI object for rendering HUD, menus, and dialogs
	public EventHandler eHandler; // Handler object for tile-based events
	
	Thread gameThread; // Thread object for running the game loop
	
	
	public gamePanel() { // Constructor for gamePanel

	    setPreferredSize(new Dimension(screenWidth, screenHeight)); // Sets the preferred size of this panel
	    setBackground(Color.black); // Sets background color to black
	    setDoubleBuffered(true); // Enables double buffering to reduce screen flickering
	    setFocusable(true); // Allows this panel to receive keyboard input
	    
		 // track mouse movement to update hovered tile
		 addMouseMotionListener(new java.awt.event.MouseMotionAdapter() { // Adds a listener for mouse movement events
		     @Override
		     public void mouseMoved(java.awt.event.MouseEvent e) { // Called when mouse moves without buttons pressed
		         mouseX = e.getX(); // Updates mouse X coordinate
		         mouseY = e.getY(); // Updates mouse Y coordinate
		         updateHoveredTile(); // Calculates which tile is under the mouse
		     }
	
		     @Override
		     public void mouseDragged(java.awt.event.MouseEvent e) { // Called when mouse moves with buttons pressed
		         mouseX = e.getX(); // Updates mouse X coordinate
		         mouseY = e.getY(); // Updates mouse Y coordinate
		         updateHoveredTile(); // Calculates which tile is under the mouse
		     }
		 });
	
		 // handle clicks to set the selected throw tile
		 addMouseListener(new java.awt.event.MouseAdapter() { // Adds a listener for mouse click events
		     @Override
		     public void mouseClicked(java.awt.event.MouseEvent e) { // Called when mouse is clicked
		         // only set selection while playing (optional tweak)
		         if (gameState == playState || gameState == taskState) { // Checks if game is in a state where clicking is valid
		             // set selected target to the hovered tile (or clear if none)
		             selectedThrowCol = hoveredTileCol; // Sets throw target column to currently hovered tile
		             selectedThrowRow = hoveredTileRow; // Sets throw target row to currently hovered tile
		             mouseClicked = true; // Sets flag indicating a click occurred
		         }
		     }
		 });
		 
		// allow mouse wheel to scroll instructions
		 // this assumes 'this' is a Component (e.g., JPanel) or you have access to the component to add the listener
		 this.addMouseWheelListener(new java.awt.event.MouseWheelListener() { // Adds a listener for mouse wheel events
		     @Override
		     public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) { // Called when mouse wheel is scrolled
		         // only scroll when instruction screen is active
		         if (ui.titleScreenState == 4) { // Checks if instruction screen is currently displayed
		             ui.instrScrollOffset += e.getWheelRotation() * ui.instrScrollSpeed; // Adjusts scroll offset based on wheel rotation amount
		             ui.clampInstrScroll(); // Ensures scroll offset stays within valid bounds
		         }
		     }
		 });

	    keyH = new keyHandler(this); // Creates keyboard handler and passes reference to this gamePanel
	    addKeyListener(keyH); // Registers keyboard handler to receive key events

	    System.out.println("gamePanel constructed"); // Prints confirmation message to console
	}

	
	public void setupGame() { // Method to initialize all game systems and objects

	    System.out.println("setupGame started"); // Prints status message to console

	    uTool = new UtilityTool(this); // Creates utility tool and passes reference to this gamePanel
	    tileM = new TileManager(this); // Creates tile manager and passes reference to this gamePanel
	    cChecker = new CollisionChecker(this); // Creates collision checker and passes reference to this gamePanel
	    aSetter = new AssetSetter(this); // Creates asset setter and passes reference to this gamePanel

	    player = new player(this, keyH); // Creates player object with references to gamePanel and keyboard handler
	    ui = new UserInterface(this); // Creates UI object and passes reference to this gamePanel
	    eHandler = new EventHandler(this); // Creates event handler and passes reference to this gamePanel

	    aSetter.setItem(); // Places all items in the world
	    aSetter.setNPC(); // Places all NPCs in the world
	    aSetter.setGaurds(); // Places all guards in the world
	    aSetter.setTasks(); // Places all tasks in the world

	    if (player.level == 1) { // Checks if player is on level 1
	        tileM.loadMap("/maps/Level1Map.txt"); // Loads level 1 map file
	    } else if (player.level == 2) { // Checks if player is on level 2
	        tileM.loadMap("/maps/Level2Map.txt"); // Loads level 2 map file
	    } else if (player.level == 3) { // Checks if player is on level 3
	        tileM.loadMap("/maps/Level3Map.txt"); // Loads level 3 map file
	    } else if (player.level == 4) { // Checks if player is on level 4
	        tileM.loadMap("/maps/Level4Map.txt"); // Loads level 4 map file
	    }

	    gameState = titleState; // Sets initial game state to title screen

	    System.out.println("setupGame finished"); // Prints completion message to console
	}
	
	public void saveGame(int slot) { // Method to save current game state to specified slot
		if (slot == 1) { // Checks if saving to slot 1
			saves.save1.saveGame(this); // Saves game to slot 1
		} else if (slot == 2) { // Checks if saving to slot 2
			saves.save2.saveGame(this); // Saves game to slot 2

		}
		else if (slot == 3) { // Checks if saving to slot 3
			saves.save3.saveGame(this); // Saves game to slot 3
		}
	    
	}
	
	public void loadGame(int slot) { // Method to load saved game from specified slot
	    if (slot == 1) { // Checks if loading from slot 1
	        if (saves.save1.fileExists()) { // Checks if save file exists for slot 1
	            saves.save1.loadGame(this); // Loads game data from slot 1
	            player.updateInventory(); // Updates player inventory display
	            // DON'T call player.setDefaultValues() here!
	            ui.showBoxMessage("Game Loaded from Slot 1!"); // Displays success message to player
	            gameState = playState; // Changes game state to active gameplay
	        } else { // If save file doesn't exist
	            ui.showBoxMessage("No save file found in Slot 1!"); // Displays error message to player
	        }
	    } else if (slot == 2) { // Checks if loading from slot 2
	        if (saves.save2.fileExists()) { // Checks if save file exists for slot 2
	            saves.save2.loadGame(this); // Loads game data from slot 2
	            player.updateInventory(); // Updates player inventory display
	            // DON'T call player.setDefaultValues() here!
	            ui.showBoxMessage("Game Loaded from Slot 2!"); // Displays success message to player
	            gameState = playState; // Changes game state to active gameplay
	        } else { // If save file doesn't exist
	            ui.showBoxMessage("No save file found in Slot 2!"); // Displays error message to player
	        }
	    } else if (slot == 3) { // Checks if loading from slot 3
	        if (saves.save3.fileExists()) { // Checks if save file exists for slot 3
	            saves.save3.loadGame(this); // Loads game data from slot 3
	            player.updateInventory(); // Updates player inventory display
	            // DON'T call player.setDefaultValues() here!
	            ui.showBoxMessage("Game Loaded from Slot 3!"); // Displays success message to player
	            gameState = playState; // Changes game state to active gameplay
	        } else { // If save file doesn't exist
	            ui.showBoxMessage("No save file found in Slot 3!"); // Displays error message to player
	        }
	    }
	}


	public void startGameThread() { // Method to create and start the game loop thread
		gameThread = new Thread(this); // Creates new thread with this gamePanel as the runnable
		gameThread.start(); // Starts the thread, which calls the run() method
	}

	@Override
	public void run() { // Main game loop that runs at 60 FPS
		double drawInterval = 1000000000 / FPS; // Calculates time in nanoseconds between frames (16.67ms for 60 FPS)
		double delta = 0; // Accumulator for tracking when to update/draw next frame
		long lastTime = System.nanoTime(); // Records current time in nanoseconds
		long currentTime; // Variable to hold current time in each loop iteration
		long timer = 0; // Accumulator for tracking FPS measurement period
		int drawCount = 0; // Counts number of frames drawn in current second
		
		// TODO Auto-generated method stub
		while (gameThread != null) { // Loops continuously while game thread exists
			currentTime = System.nanoTime(); // Gets current time
			delta += (currentTime - lastTime) / drawInterval; // Adds elapsed time to delta
			timer += (currentTime - lastTime); // Adds elapsed time to timer
			lastTime = currentTime; // Updates last time for next iteration
			if (delta >= 1) { // Checks if enough time has passed for next frame
				// Update: update information such as character positions
				update(); // Updates all game logic
				// Draw: draw the screen with the updated information
				repaint(); // Triggers paintComponent to redraw screen
				delta--; // Decrements delta by 1 frame
				drawCount++; // Increments frame counter
			}
			if (timer >= 1000000000) { // Checks if one second has passed
				drawCount = 0; // Resets frame counter
				timer = 0; // Resets timer
			}
		}
	}
	
	
	
	public void update() { // Main game loop update; runs once per frame

	    if (gameState == playState) { // Only update gameplay entities when the game is in active play mode

	        // Player
	        player.update(); // Update the player first so all other systems react to the player's new state
	        aSetter.update(); // Updates respawn timers for consumable items

	        // NPCs
	        for (int i = 0; i < npc.length; i++) { // Loop through all NPC slots
	            if (npc[i] != null) { // Skip empty NPC slots
	                npc[i].update(); // Update this NPC's behavior and movement
	            }
	        }

	        // Guards
	        for (int i = 0; i < gaurds.length; i++) { // Loop through all guard slots
	            if (gaurds[i] != null) { // Skip empty guard slots
	                gaurds[i].update(); // Update guard AI, movement, LOS, sound detection, etc.
	            }
	        }

	        // Items
	        for (int i = 0; i < items.length; i++) { // Loop through all world item slots
	            if (items[i] != null) { // Skip empty item slots
	                items[i].update(); // Update item timers (pickupDelay, throwDelay, animations)
	            }
	        }
	    }

	    if (gameState == pauseState) { // Checks if game is paused
	        // Game is paused; no updates needed
	    }

	    if (gameState == dialogueState) { // Checks if game is in dialogue mode
	        // Dialogue mode; gameplay entities are frozen
	    }

	    if (gameState == deathState) { // Checks if game is in death state
	        // Player death screen; no gameplay updates
	    }

	    if (gameState == taskState) { // Checks if game is in task UI state
	        // Task UI open; gameplay paused except UI
	    }
	}
	
	public void triggerSoundForGuards(int worldX, int worldY, int radiusTiles) { // Notify guards about a sound event at a specific world position

	    int radiusPixels = radiusTiles * tileSize; // Convert the sound radius from tiles to pixels for distance comparison

	    for (int i = 0; i < gaurds.length; i++) { // Loop through every guard in the game
	        if (gaurds[i] == null) continue; // Skip empty guard slots

	        int dx = gaurds[i].worldX - worldX; // Horizontal distance between guard and sound source
	        int dy = gaurds[i].worldY - worldY; // Vertical distance between guard and sound source
	        int dist = (int)Math.sqrt(dx + dy); // Euclidean distance from guard to sound source

	        if (dist <= radiusPixels) { // Guard is within hearing range

	            if (gaurds[i].hasLineOfSound(worldX, worldY)) { // Check if nothing blocks the sound between guard and source
	                gaurds[i].hearSound(worldX, worldY); // Alert the guard so it can react to the sound
	            }
	        }
	    }
	} 

	
	public void resetGame(boolean restartFromTitle) { // Method to reset game state and reload level
	    // Clear world objects, reset player, map, etc.
	    for (int i = 0; i < items.length; i++) items[i] = null; // Clears all items from world
	    player.clearInventory(); // Removes all items from player inventory
	    player.setDefaultValues(); // Resets player stats, position, and state to defaults
	    tileM.resetMap(); // Clears current map data
	    
	    if (player.level == 1) { // Checks if resetting level 1
	        tileM.loadMap("/maps/Level1Map.txt"); // Reloads level 1 map
	    } else if (player.level == 2) { // Checks if resetting level 2
	        tileM.loadMap("/maps/Level2Map.txt"); // Reloads level 2 map
	    } else if (player.level == 3) { // Checks if resetting level 3
	        tileM.loadMap("/maps/Level3Map.txt"); // Reloads level 3 map
	    } else if (player.level == 4) { // Checks if resetting level 4
	        tileM.loadMap("/maps/Level4Map.txt"); // Reloads level 4 map
	    }

	    // Respawn items / NPCs but **do not create a new gamePanel**
	    aSetter.setAll(); // Respawns all items, NPCs, guards, and tasks
	    
	    ui.levelFinished = false; // Resets level completion flag
	    ui.slotRow = -1; // Resets selected inventory slot
	    ui.selectedItem = null; // Clears selected item reference
	    ui.showThrowRadius = false; // Hides throw radius indicator
        ui.activeThrowable = null; // Clears active throwable item
        selectedThrowCol = -1; // Clears selected throw column
        selectedThrowRow = -1; // Clears selected throw row
        
        speedRunTimerFrames = 0; // Resets speedrun timer to zero
        speedRunLost = false; // Resets speedrun failure flag
        
	    if (restartFromTitle) { // Checks if should return to title screen
	    	gameState = titleState; // Sets game state to title screen
	    	ui.titleScreenState = 0; // Resets title screen to main menu
	    }
	    else gameState = playState; // Sets game state to active gameplay
	}

	
	public void paintComponent(Graphics g) { // Method called to render all graphics to screen
		super.paintComponent(g); // Calls parent class paintComponent to clear screen
		 
		Graphics2D g2 = (Graphics2D) g; // Casts Graphics to Graphics2D for advanced rendering features
		
		//Title screen
		if (gameState == titleState) { // Checks if game is on title screen
			ui.draw(g2); // Draws title screen UI only
		} else { // If not on title screen
			// Draw tiles
			tileM.draw(g2); // Draws all map tiles
			
			// Draw Time
			if (speedRunState) { // Checks if speedrun mode is active
				ui.drawSpeedRunTimer(g2); // Draws speedrun timer on screen
				if (speedRunLost) { // Checks if speedrun time limit exceeded
					eHandler.playerDied(); // Triggers player death event
				}
				speedRunTimerFrames++; // Increments speedrun frame counter
			}
			
			
			// Draw items
			for (int i = 0; i < items.length; i++) { // Loops through all item slots
				if (items[i] != null) { // Checks if item exists at this slot
					items[i].draw(g2); // Draws this item to screen
				}
			}
			
			//Draw tasks
			for (int i = 0; i < tasks.length; i++) { // Loops through all task slots
				if (tasks[i] != null) { // Checks if task exists at this slot
					tasks[i].draw(g2); // Draws this task to screen
				}
			}
			
			// Draw NPCs
			for (int i = 0; i < npc.length; i++) { // Loops through all NPC slots
				if (npc[i] != null) { // Checks if NPC exists at this slot
					npc[i].draw(g2); // Draws this NPC to screen
				}
			}
			// Draw Gaurds
			for (int i = 0; i < gaurds.length; i++) { // Loops through all guard slots
				if (gaurds[i] != null) { // Checks if guard exists at this slot
					gaurds[i].draw(g2); // Draws this guard to screen
				}
			}
			
			// Draw player
			player.draw(g2); // Draws player character to screen
			
			// Draw gaurds
			for (int i = 0; i < gaurds.length; i++) { // Loops through all guard slots again (duplicate rendering)
			    if (gaurds[i] != null) { // Checks if guard exists at this slot
			    	gaurds[i].draw(g2); // Draws this guard to screen
			    	//uTool.drawEntityHitbox(g2, gaurds[i]); // Commented code that would draw guard hitboxes for debugging
			    }
			}
			
			//UI
			ui.draw(g2); // Draws UI elements (HUD, inventory, menus, etc.)
			
		}
		g2.dispose(); // Disposes of graphics context to free resources
	}
	
	public void playMusic(int i) { // Method to start playing background music track
		if (!playingMusic) { // Checks if music is not already playing
			music.setFile(i); // Loads music file at index i
			music.setVolume(0.6f); // Sets music volume to 60%
			music.play(); // Starts playing music
			music.loop(); // Sets music to loop continuously
			playingMusic = true; // Sets flag indicating music is now playing
		} else { // If music is already playing
			return; // Does nothing and exits method
		}
	}
	
	public int getEmptyItemSlot() { // Method to find first available slot in world items array
		for (int i = 0; i < items.length; i++) { // Loops through all item slots
			if (items[i] == null) { // Checks if this slot is empty
				return i; // Returns the index of this empty slot
			}
		}
		return -1; // Returns -1 if no empty slots found
	}
	
	public void stopMusic() { // Method to stop background music
		music.stop(); // Stops the currently playing music
	}
	public void playSoundEffect(int i) { // Method to play a one-time sound effect
		soundEffect.setFile(i); // Loads sound effect file at index i
		soundEffect.setVolume(0.65f); // Sets sound effect volume to 65%
		soundEffect.play(); // Plays the sound effect once
	}
	
	public void updateHoveredTile() { // Method to calculate which tile is currently under mouse cursor
	    // Convert mouse screen coords to world coords (invert your screenX calculation)
	    if (player == null) return; // Exits if player doesn't exist yet
	    int worldXAtMouse = mouseX + player.worldX - player.getScreenX(); // Converts mouse screen X to world X coordinate
	    int worldYAtMouse = mouseY + player.worldY - player.getScreenY(); // Converts mouse screen Y to world Y coordinate

	    // tile col/row under mouse
	    int col = worldXAtMouse / tileSize; // Calculates tile column from world X coordinate
	    int row = worldYAtMouse / tileSize; // Calculates tile row from world Y coordinate

	    // sanity check bounds
	    if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) { // Checks if calculated tile is outside world bounds
	        hoveredTileCol = -1; // Sets hovered column to -1 (invalid)
	        hoveredTileRow = -1; // Sets hovered row to -1 (invalid)
	    } else { // If tile is within world bounds
	        hoveredTileCol = col; // Stores hovered tile column
	        hoveredTileRow = row; // Stores hovered tile row
	    }
	}

	
}