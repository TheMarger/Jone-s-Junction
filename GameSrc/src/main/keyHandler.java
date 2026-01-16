package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class keyHandler implements KeyListener {
    // gameplay flags (consumed by game code)
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean sprintPressed, crouchPressed, interactPressed, escapePressed, dropPressed, refreshPressed, enterPressed, backspacePressed, throwPressed; 
    public boolean onePressed, twoPressed, threePressed;
    public char typedChar;
    public boolean throwJustPressed = false;

    gamePanel gp;

    public keyHandler(gamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        // not used
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();

        // ----- If we're on the title screen, set UI flags in gp.ui and capture keys for binding -----
        if (gp.gameState == gp.titleState || gp.gameState == gp.pauseState) {
            // If UI is awaiting a keybind, capture the raw key for UI to validate/assign
            if (gp.ui.titleScreenState == 3 && gp.ui.awaitingKeybind) {
                gp.ui.capturedKey = code;
                gp.ui.capturedKeyPressed = true; // UI will consume this
                return; // consumed by UI binding flow
            }

            // Navigation keys for titles/menus (UI will consume these flags)
            if (code == KeyEvent.VK_W)    {
            	gp.ui.uiUp = true;
            	gp.playSoundEffect(5);
            }
            if (code == KeyEvent.VK_S)  {
            	gp.ui.uiDown = true;
            	gp.playSoundEffect(5);
            }
            if (code == KeyEvent.VK_A)  {
            	gp.ui.uiLeft = true;
            	gp.playSoundEffect(5);
            }
            if (code == KeyEvent.VK_D) {
            	gp.ui.uiRight = true;
            	gp.playSoundEffect(5);
            }
            if (code == KeyEvent.VK_ENTER) {
            	gp.ui.uiConfirm = true;
            	gp.playSoundEffect(5);
            }
            if (code == KeyEvent.VK_ESCAPE) {
            	gp.ui.uiBack = true;
            	gp.playSoundEffect(5);
            }
            // do not set gameplay flags when on title screen
            return;
        }
        
        if (gp.gameState == gp.taskState) {
			if (code == KeyEvent.VK_ENTER) {
				enterPressed = true;
			}
			if (code == KeyEvent.VK_BACK_SPACE) {
				backspacePressed = true;
			}
			if (code != KeyEvent.VK_ENTER && code != KeyEvent.VK_BACK_SPACE) {
				typedChar = e.getKeyChar();
			}
			return;
		}

        // ----- PLAY STATE: set gameplay flags (mapped to gp.keybinds) -----
        if (gp.gameState == gp.playState) {
            if (code == gp.keybinds[0]) {
            	upPressed = true;
            }
            if (code == gp.keybinds[1]) {
            	downPressed = true;
            }
            if (code == gp.keybinds[2]) {
            	leftPressed = true;
            }
            if (code == gp.keybinds[3]) {
            	rightPressed = true;
            }
            if (code == gp.keybinds[4]) {
            	sprintPressed = true;
            }
            if (code == gp.keybinds[5]) {
            	crouchPressed = true;
            }
            if (code == gp.keybinds[6]) {
            	interactPressed = true;
            }
            if (code == gp.keybinds[7]) { // throw key
                if (!throwPressed) {       // only mark just pressed on first frame
                    throwJustPressed = true;
                }
                throwPressed = true;
            }


            if (code == gp.keybinds[10]) {
				onePressed = true;
				if (gp.ui.slotRow == 0) {
					gp.ui.slotRow = -1;
				} else {
					gp.ui.slotRow = 0;
				}
				gp.playSoundEffect(5);
			}
            if (code == gp.keybinds[11]) {
            	twoPressed = true;
            	if (gp.ui.slotRow == 1) {
					gp.ui.slotRow = -1;
				} else {
					gp.ui.slotRow = 1;
				}
            	gp.playSoundEffect(5);
            	
            }
            if (code == gp.keybinds[12]) {
				threePressed = true;
				if (gp.ui.slotRow == 2) {
					gp.ui.slotRow = -1;
				} else {
					gp.ui.slotRow = 2;
				}
				gp.playSoundEffect(5);
			}
            
            if (code == gp.keybinds[8]) {
				dropPressed = true;
				System.out.println("Dropping item in slot " + gp.ui.slotRow);
				gp.player.dropItem(gp.ui.slotRow);
			}
            
            if (code == KeyEvent.VK_M) {
				refreshPressed = true;
				gp.uTool.refreshMap();
			}
            
            if (code ==KeyEvent.VK_T) {
            	gp.uTool.refreshTasks();
            }

            // allow ESC -> pause (game logic still happens in UI or gamePanel)
            if (code == KeyEvent.VK_ESCAPE) {
            	gp.gameState = gp.pauseState;
            }
            return;
        }


        if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
            	if (gp.ui.levelFinished) {
            		gp.resetGame(false);
            	} else {
	                enterPressed = true;   // simple boolean — draw() will handle the logic
	                upPressed = true;  // prevent stuck key issue
	                interactPressed = true;
	                gp.gameState = gp.playState;
            	}
                
            }
            if (code == KeyEvent.VK_ESCAPE) {
            	if (gp.ui.levelFinished) {
            		return; // ignore during level complete
            	} else {
	                escapePressed = true;  // draw() will handle cancel behavior
	                upPressed = false;
	                interactPressed = false;
	                gp.gameState = gp.playState;
            	}
      
                
            }
        }
        if (gp.gameState == gp.deathState) {
			if (code == KeyEvent.VK_ENTER) {
				gp.resetGame(false);
			}
			if (code == KeyEvent.VK_ESCAPE) {
				gp.resetGame(true);
			}
			
		}
        
     // inside your keyPressed(KeyEvent e) handler:
        if (gp.ui.titleScreenState == 4) {
            int kc = e.getKeyCode();
            if (kc == java.awt.event.KeyEvent.VK_UP) {
            	gp.ui.instrScrollOffset -= gp.ui.instrScrollSpeed;
            	gp.ui.clampInstrScroll();
            } else if (kc == java.awt.event.KeyEvent.VK_DOWN) {
            	gp.ui.instrScrollOffset += gp.ui.instrScrollSpeed;
            	gp.ui.clampInstrScroll();
            } else if (kc == java.awt.event.KeyEvent.VK_PAGE_UP) {
            	gp.ui.instrScrollOffset -= gp.ui.instrViewportHeight; // page up
            	gp.ui.clampInstrScroll();
            } else if (kc == java.awt.event.KeyEvent.VK_PAGE_DOWN) {
            	gp.ui.instrScrollOffset += gp.ui.instrViewportHeight; // page down
            	gp.ui.clampInstrScroll();
            } else if (kc == java.awt.event.KeyEvent.VK_HOME) {
            	gp.ui.instrScrollOffset = 0;
            } else if (kc == java.awt.event.KeyEvent.VK_END) {
            	gp.ui.instrScrollOffset = Math.max(0, gp.ui.instrContentHeight - gp.ui.instrViewportHeight);
            }
        }

    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();

        // release gameplay flags when matching keys are released
        if (code == gp.keybinds[0]) {
        	upPressed = false;
        }
        if (code == gp.keybinds[1]) {
        	downPressed = false;
        }
        if (code == gp.keybinds[2]) {
        	leftPressed = false;
        }
        if (code == gp.keybinds[3]) {
        	rightPressed = false;
        }
        if (code == gp.keybinds[4]) {
        	sprintPressed = false;
        }
        if (code == gp.keybinds[5]) {
        	crouchPressed = false;
        }
        if (code == gp.keybinds[6]) {
        	interactPressed = false;
        }
        if (code == gp.keybinds[7]) {
            throwPressed = false;
            throwJustPressed = false; // reset
        }

		if (code == gp.keybinds[10]) {
			onePressed = false;
		}
		if (code == gp.keybinds[11]) {
			twoPressed = false;
		}
		if (code == gp.keybinds[12]) {
			threePressed = false;
		}
		if (code == gp.keybinds[8]) {
			dropPressed = false;
		}
    }
}
