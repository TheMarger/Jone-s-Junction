package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class keyHandler implements KeyListener {
    // gameplay flags (consumed by game code)
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean sprintPressed, crouchPressed, interactPressed, escapePressed, dropPressed, refreshPressed;
    public boolean onePressed, twoPressed, threePressed;

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
        if (gp.gameState == gp.titleState) {
            // If UI is awaiting a keybind, capture the raw key for UI to validate/assign
            if (gp.ui.titleScreenState == 3 && gp.ui.awaitingKeybind) {
                gp.ui.capturedKey = code;
                gp.ui.capturedKeyPressed = true; // UI will consume this
                return; // consumed by UI binding flow
            }

            // Navigation keys for titles/menus (UI will consume these flags)
            if (code == KeyEvent.VK_W)    {
            	gp.ui.uiUp = true;
            }
            if (code == KeyEvent.VK_S)  {
            	gp.ui.uiDown = true;
            }
            if (code == KeyEvent.VK_A)  {
            	gp.ui.uiLeft = true;
            }
            if (code == KeyEvent.VK_D) {
            	gp.ui.uiRight = true;
            }
            if (code == KeyEvent.VK_ENTER) {
            	gp.ui.uiConfirm = true;
            }
            if (code == KeyEvent.VK_ESCAPE) {
            	gp.ui.uiBack = true;
            }
            // do not set gameplay flags when on title screen
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
            
            if (code == KeyEvent.VK_B) {
				refreshPressed = true;
				gp.uTool.refreshMap();
			}

            // allow ESC -> pause (game logic still happens in UI or gamePanel)
            if (code == KeyEvent.VK_ESCAPE) {
            	gp.gameState = gp.pauseState;
            }
            return;
        }

        // ----- PAUSE / DIALOGUE state: keep simple (game handles) -----
        if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_ESCAPE) {
            	gp.gameState = gp.playState;
            }
            return;
        }

        if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.gameState = gp.playState;
                upPressed = true;      // small convenience to advance player
                interactPressed = true;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                upPressed = false;
                interactPressed = false;
                gp.gameState = gp.playState;
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
			escapePressed = false;
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
