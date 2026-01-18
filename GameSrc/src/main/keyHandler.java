/*
 * Name: Rafay
 * Date: 1/19/2026
 * Course Code: ICS4U0
 * Description: keyHandler class implements KeyListener to process all keyboard input for the game.
 *              It manages boolean flags for gameplay actions (movement, sprint, crouch, interact,
 *              throw, drop), handles menu navigation in different game states (title, pause, dialogue,
 *              death, task), processes custom keybind assignments, and supports instruction screen
 *              scrolling. The class differentiates between UI navigation and gameplay controls based
 *              on the current game state, and includes special handling for key capture during
 *              keybind customization.
 */

package main; // Declares this class belongs to the main package

import java.awt.event.KeyEvent; // Imports KeyEvent class for keyboard event constants
import java.awt.event.KeyListener; // Imports KeyListener interface for keyboard input handling

public class keyHandler implements KeyListener { // Declares keyHandler class that implements KeyListener interface
    // gameplay flags (consumed by game code)
    public boolean upPressed, downPressed, leftPressed, rightPressed; // Boolean flags for directional movement keys
    public boolean sprintPressed, crouchPressed, interactPressed, escapePressed, dropPressed, refreshPressed, enterPressed, backspacePressed, throwPressed; // Boolean flags for action keys
    public boolean onePressed, twoPressed, threePressed; // Boolean flags for number keys (inventory slots)
    public char typedChar; // Stores the last character typed during task state
    public boolean throwJustPressed = false; // Flag that's true only on the first frame throw key is pressed

    gamePanel gp; // Reference to the main gamePanel object

    public keyHandler(gamePanel gp) { // Constructor that takes a gamePanel parameter
        this.gp = gp; // Assigns the gamePanel parameter to the class's gp field
    }

    @Override
    public void keyTyped(java.awt.event.KeyEvent e) { // Method called when a key is typed (pressed and released)
        // not used
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) { // Method called when a key is pressed down
        int code = e.getKeyCode(); // Gets the virtual key code of the pressed key

        // ----- If we're on the title screen, set UI flags in gp.ui and capture keys for binding -----
        if (gp.gameState == gp.titleState || gp.gameState == gp.pauseState) { // Checks if game is on title screen or paused
            // If UI is awaiting a keybind, capture the raw key for UI to validate/assign
            if (gp.ui.titleScreenState == 3 && gp.ui.awaitingKeybind) { // Checks if UI is waiting for keybind customization input
                gp.ui.capturedKey = code; // Stores the pressed key code for UI to process
                gp.ui.capturedKeyPressed = true; // Sets flag indicating a key was captured
                return; // Exits method to prevent further processing (consumed by UI binding flow)
            }

            // Navigation keys for titles/menus (UI will consume these flags)
            if (code == KeyEvent.VK_W)    { // Checks if W key was pressed
            	gp.ui.uiUp = true; // Sets UI navigation up flag
            	gp.playSoundEffect(5); // Plays menu navigation sound effect
            }
            if (code == KeyEvent.VK_S)  { // Checks if S key was pressed
            	gp.ui.uiDown = true; // Sets UI navigation down flag
            	gp.playSoundEffect(5); // Plays menu navigation sound effect
            }
            if (code == KeyEvent.VK_A)  { // Checks if A key was pressed
            	gp.ui.uiLeft = true; // Sets UI navigation left flag
            	gp.playSoundEffect(5); // Plays menu navigation sound effect
            }
            if (code == KeyEvent.VK_D) { // Checks if D key was pressed
            	gp.ui.uiRight = true; // Sets UI navigation right flag
            	gp.playSoundEffect(5); // Plays menu navigation sound effect
            }
            if (code == KeyEvent.VK_ENTER) { // Checks if Enter key was pressed
            	gp.ui.uiConfirm = true; // Sets UI confirm action flag
            	gp.playSoundEffect(5); // Plays menu navigation sound effect
            }
            if (code == KeyEvent.VK_ESCAPE) { // Checks if Escape key was pressed
            	gp.ui.uiBack = true; // Sets UI back/cancel action flag
            	gp.playSoundEffect(5); // Plays menu navigation sound effect
            }
            // do not set gameplay flags when on title screen
            return; // Exits method to prevent gameplay input during menus
        }
        
        if (gp.gameState == gp.taskState) { // Checks if game is in task completion state
			if (code == KeyEvent.VK_ENTER) { // Checks if Enter key was pressed
				enterPressed = true; // Sets enter pressed flag for task submission
			}
			if (code == KeyEvent.VK_BACK_SPACE) { // Checks if Backspace key was pressed
				backspacePressed = true; // Sets backspace pressed flag for text deletion
			}
			if (code != KeyEvent.VK_ENTER && code != KeyEvent.VK_BACK_SPACE) { // Checks if key is not Enter or Backspace
				typedChar = e.getKeyChar(); // Stores the character for task text input
			}
			return; // Exits method to prevent other input processing during tasks
		}

        // ----- PLAY STATE: set gameplay flags (mapped to gp.keybinds) -----
        if (gp.gameState == gp.playState) { // Checks if game is in active gameplay state
            if (code == gp.keybinds[0]) { // Checks if pressed key matches forward keybind
            	upPressed = true; // Sets upward movement flag
            }
            if (code == gp.keybinds[1]) { // Checks if pressed key matches backward keybind
            	downPressed = true; // Sets downward movement flag
            }
            if (code == gp.keybinds[2]) { // Checks if pressed key matches left keybind
            	leftPressed = true; // Sets leftward movement flag
            }
            if (code == gp.keybinds[3]) { // Checks if pressed key matches right keybind
            	rightPressed = true; // Sets rightward movement flag
            }
            if (code == gp.keybinds[4]) { // Checks if pressed key matches sprint keybind
            	sprintPressed = true; // Sets sprint action flag
            }
            if (code == gp.keybinds[5]) { // Checks if pressed key matches crouch keybind
            	crouchPressed = true; // Sets crouch action flag
            }
            if (code == gp.keybinds[6]) { // Checks if pressed key matches interact keybind
            	interactPressed = true; // Sets interact action flag
            }
            if (code == gp.keybinds[7]) { // Checks if pressed key matches throw keybind
                if (!throwPressed) {       // Checks if throw key wasn't already held down (only mark just pressed on first frame)
                    throwJustPressed = true; // Sets flag for single-frame throw detection
                }
                throwPressed = true; // Sets continuous throw pressed flag
            }


            if (code == gp.keybinds[10]) { // Checks if pressed key matches slot 1 keybind
				onePressed = true; // Sets number 1 pressed flag
				if (gp.ui.slotRow == 0) { // Checks if slot 0 is currently selected
					gp.ui.slotRow = -1; // Deselects the slot
				} else { // If slot 0 is not selected
					gp.ui.slotRow = 0; // Selects inventory slot 0
				}
				gp.playSoundEffect(5); // Plays inventory selection sound
			}
            if (code == gp.keybinds[11]) { // Checks if pressed key matches slot 2 keybind
            	twoPressed = true; // Sets number 2 pressed flag
            	if (gp.ui.slotRow == 1) { // Checks if slot 1 is currently selected
					gp.ui.slotRow = -1; // Deselects the slot
				} else { // If slot 1 is not selected
					gp.ui.slotRow = 1; // Selects inventory slot 1
				}
            	gp.playSoundEffect(5); // Plays inventory selection sound
            	
            }
            if (code == gp.keybinds[12]) { // Checks if pressed key matches slot 3 keybind
				threePressed = true; // Sets number 3 pressed flag
				if (gp.ui.slotRow == 2) { // Checks if slot 2 is currently selected
					gp.ui.slotRow = -1; // Deselects the slot
				} else { // If slot 2 is not selected
					gp.ui.slotRow = 2; // Selects inventory slot 2
				}
				gp.playSoundEffect(5); // Plays inventory selection sound
			}
            
            if (code == gp.keybinds[8]) { // Checks if pressed key matches drop keybind
				dropPressed = true; // Sets drop action flag
				System.out.println("Dropping item in slot " + gp.ui.slotRow); // Prints debug message to console
				gp.player.dropItem(gp.ui.slotRow); // Calls player method to drop item from selected slot
			}
            
            if (code == KeyEvent.VK_M) { // Checks if M key was pressed (debug key)
				refreshPressed = true; // Sets refresh pressed flag
				//gp.uTool.refreshMap(); // Commented code that would refresh the map
				gp.player.collisionOn = !gp.player.collisionOn; // Toggles player collision on/off for debugging
			}
            
            if (code ==KeyEvent.VK_T) { // Checks if T key was pressed (debug key)
            	gp.uTool.refreshTasks(); // Refreshes/regenerates all tasks
            }

            // allow ESC -> pause (game logic still happens in UI or gamePanel)
            if (code == KeyEvent.VK_ESCAPE) { // Checks if Escape key was pressed
            	gp.gameState = gp.pauseState; // Changes game state to paused
            }
            return; // Exits method after processing play state input
        }


        if (gp.gameState == gp.dialogueState) { // Checks if game is in dialogue state
            if (code == KeyEvent.VK_ENTER) { // Checks if Enter key was pressed
            	if (gp.ui.levelFinished) { // Checks if level completion dialogue is showing
            		if (gp.level != 5) { // Checks if not on final level
            			gp.resetGame(false); // Resets game and continues to next level
            		} else { // If on final level
            			gp.level = gp.startLevel; // Resets level to starting level
            			gp.resetGame(true); // Resets game and returns to title screen
            			gp.gameState = gp.completionState;
            		}
            	} else { // If not level completion dialogue
	                enterPressed = true;   // Sets enter pressed flag (simple boolean — draw() will handle the logic)
	                upPressed = true;  // Sets up pressed flag (prevent stuck key issue)
	                interactPressed = true; // Sets interact pressed flag
	                gp.gameState = gp.playState; // Returns to gameplay state
            	}
                
            }
            if (code == KeyEvent.VK_ESCAPE) { // Checks if Escape key was pressed
            	if (gp.ui.levelFinished) { // Checks if level completion dialogue is showing
            		return; // Does nothing if level is finished
            	} else { // If normal dialogue
	                escapePressed = true;  // Sets escape pressed flag (draw() will handle cancel behavior)
	                upPressed = false; // Clears up pressed flag
	                interactPressed = false; // Clears interact pressed flag
	                gp.gameState = gp.playState; // Returns to gameplay state
            	}
      
                
            }
        }
        if (gp.gameState == gp.deathState) { // Checks if game is in death state
			if (code == KeyEvent.VK_ENTER) { // Checks if Enter key was pressed
				gp.resetGame(false); // Resets game and continues at current level
			}
			if (code == KeyEvent.VK_ESCAPE) { // Checks if Escape key was pressed
				gp.resetGame(true); // Resets game and returns to title screen
			}
			
		}
        
     // inside your keyPressed(KeyEvent e) handler:
        if (gp.ui.titleScreenState == 4) { // Checks if instruction screen is active
            int kc = e.getKeyCode(); // Gets the key code
            if (kc == java.awt.event.KeyEvent.VK_UP) { // Checks if Up arrow key was pressed
            	gp.ui.instrScrollOffset -= gp.ui.instrScrollSpeed; // Scrolls instructions upward
            	gp.ui.clampInstrScroll(); // Ensures scroll stays within valid bounds
            } else if (kc == java.awt.event.KeyEvent.VK_DOWN) { // Checks if Down arrow key was pressed
            	gp.ui.instrScrollOffset += gp.ui.instrScrollSpeed; // Scrolls instructions downward
            	gp.ui.clampInstrScroll(); // Ensures scroll stays within valid bounds
            } else if (kc == java.awt.event.KeyEvent.VK_PAGE_UP) { // Checks if Page Up key was pressed
            	gp.ui.instrScrollOffset -= gp.ui.instrViewportHeight; // Scrolls up by one full page
            	gp.ui.clampInstrScroll(); // Ensures scroll stays within valid bounds
            } else if (kc == java.awt.event.KeyEvent.VK_PAGE_DOWN) { // Checks if Page Down key was pressed
            	gp.ui.instrScrollOffset += gp.ui.instrViewportHeight; // Scrolls down by one full page
            	gp.ui.clampInstrScroll(); // Ensures scroll stays within valid bounds
            } else if (kc == java.awt.event.KeyEvent.VK_HOME) { // Checks if Home key was pressed
            	gp.ui.instrScrollOffset = 0; // Jumps to top of instructions
            } else if (kc == java.awt.event.KeyEvent.VK_END) { // Checks if End key was pressed
            	gp.ui.instrScrollOffset = Math.max(0, gp.ui.instrContentHeight - gp.ui.instrViewportHeight); // Jumps to bottom of instructions
            }
        }

    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) { // Method called when a key is released
        int code = e.getKeyCode(); // Gets the virtual key code of the released key

        // release gameplay flags when matching keys are released
        if (code == gp.keybinds[0]) { // Checks if released key matches forward keybind
        	upPressed = false; // Clears upward movement flag
        }
        if (code == gp.keybinds[1]) { // Checks if released key matches backward keybind
        	downPressed = false; // Clears downward movement flag
        }
        if (code == gp.keybinds[2]) { // Checks if released key matches left keybind
        	leftPressed = false; // Clears leftward movement flag
        }
        if (code == gp.keybinds[3]) { // Checks if released key matches right keybind
        	rightPressed = false; // Clears rightward movement flag
        }
        if (code == gp.keybinds[4]) { // Checks if released key matches sprint keybind
        	sprintPressed = false; // Clears sprint action flag
        }
        if (code == gp.keybinds[5]) { // Checks if released key matches crouch keybind
        	crouchPressed = false; // Clears crouch action flag
        }
        if (code == gp.keybinds[6]) { // Checks if released key matches interact keybind
        	interactPressed = false; // Clears interact action flag
        }
        if (code == gp.keybinds[7]) { // Checks if released key matches throw keybind
            throwPressed = false; // Clears continuous throw pressed flag
            throwJustPressed = false; // Clears single-frame throw detection flag (reset)
        }

		if (code == gp.keybinds[10]) { // Checks if released key matches slot 1 keybind
			onePressed = false; // Clears number 1 pressed flag
		}
		if (code == gp.keybinds[11]) { // Checks if released key matches slot 2 keybind
			twoPressed = false; // Clears number 2 pressed flag
		}
		if (code == gp.keybinds[12]) { // Checks if released key matches slot 3 keybind
			threePressed = false; // Clears number 3 pressed flag
		}
		if (code == gp.keybinds[8]) { // Checks if released key matches drop keybind
			dropPressed = false; // Clears drop action flag
		}
    }
}