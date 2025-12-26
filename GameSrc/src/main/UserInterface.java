package main; // package this class belongs to

import java.awt.Color; // color constants and creation
import java.awt.Font; // font handling
import java.awt.Graphics2D; // drawing surface type used in paint
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage; // image type for sprites/icons
import java.io.InputStream;
import java.text.DecimalFormat; // formatting numbers (unused currently but present)

import javax.imageio.ImageIO;

import entity.player; // reference to player's static flags (hasKey, etc.)
import Item.*; // import all Item classes (Key, Torch, etc.)

public class UserInterface { // UI class that draws HUD and title screens

    gamePanel gp; // reference to main game panel (game state, tileSize, etc.)
    Graphics2D g2; // temporary Graphics2D reference used during draw calls
    Font arial_40; // base font used for drawing UI text
    BufferedImage keyImage; // cached image for key icon
    BufferedImage greenKeyImage; // cached image for green key icon
    BufferedImage redKeyImage; // cached image for red key icon
    BufferedImage torchImage; // cached image for torch icon
    BufferedImage blueKeyImage; // cached image for blue key icon
    
    //////////____________________________________________________________________________
    BufferedImage skinPreview;//This is for skins
    ////////////////______________________________________________________________________
  
    public boolean messageOn = false; // whether a temporary message is visible
    public boolean interactOn = false; // whether the "[E] to interact" hint is visible
    public String interactMessage = ""; // text for the interact hint
    public String message = ""; // text for the temporary message
    int messageCounter = 0; // simple counter to time how long temporary message shows

    // Title/menu state -------------------------------------------------------
    public int commandNum = 0; // current selected item index in the main title menu
    public int titleScreenState = 0; // which title sub-screen is active: 0=main,1=load,2=chars,3=keybinds

    public boolean levelFinished = false; // flag used elsewhere to indicate level completion
    double playTime; // total play time (unused drawing-wise here)
    DecimalFormat df = new DecimalFormat("#0.00"); // formatter for playTime if needed

    public String currentDialogue = ""; // currently displayed dialogue text (multi-line)

    // ---- UI input flags (set by keyHandler) -------------------------------
    public boolean uiUp = false; // UI navigation: up was pressed (edge-triggered)
    public boolean uiDown = false; // UI navigation: down was pressed
    public boolean uiLeft = false; // UI navigation: left was pressed
    public boolean uiRight = false; // UI navigation: right was pressed
    public boolean uiConfirm = false; // UI confirm/accept (e.g., Enter)
    public boolean uiBack = false; // UI back/cancel (e.g., Backspace/Escape)

    // ---- keybind editing state --------------------------------------------
    public boolean awaitingKeybind = false; // waiting for user to press a key to bind
    public boolean capturedKeyPressed = false; // keyHandler sets this when a key arrives in bind mode
    public int capturedKey = -1; // stores the actual key code captured for binding
    public int keybindSelectedIndex = 0; // which action is selected in the keybind list
    public final String[] keybindActionNames = { // human-readable action names for the keybind screen
        "Move Forward","Move Backward","Move Left","Move Right",
        "Sprint","Crouch","Interact","Throw Item","Drop Item"
    };
    
    int messageX;
    int messageY;
    int messageDuration;
    String colorName;

    // constructor - caches images and fonts ---------------------------------
    public UserInterface(gamePanel gp) {
        this.gp = gp; // save reference to gamePanel so UI can read game state and resources
        arial_40 = new Font("Cambria", Font.PLAIN, 40); // create base font

        // create example item objects and cache their images for HUD icons
        Key key = new Key(gp); // instantiate Key item to access its image
        greenKey greenKey = new greenKey(gp); // instantiate greenKey item
        redKey redKey = new redKey(gp); // instantiate redKey item
        Flashlight torch = new Flashlight(gp); // instantiate Torch item
        blueKey blueKey = new blueKey(gp); // instantiate blueKey item
        keyImage = key.image; // store key image for quick draw
        greenKeyImage = greenKey.image; // store green key image
        redKeyImage = redKey.image; // store red key image
        torchImage = torch.image; // store torch image
        blueKeyImage = blueKey.image; // store blue key image
        messageX = gp.tileSize/2;
        messageY = gp.tileSize*5;
        messageDuration = 120; // default message duration (frames)
        colorName = "white";
    }

    // showMessage: set a temporary message that will auto-hide after a short time
    public void showMessage(String text) {
        message = text; // set message text
        messageOn = true; // enable message rendering
        messageCounter = 0; // reset timer/counter
    }
    
    public void showMessage(String text, String colorName) {
			message = text; // set message text
			messageOn = true; // enable message rendering
			messageCounter = 0; // reset timer/counter
			this.colorName = colorName;
	}
    
    public void showMessage(String text, int durationFrames) {
		message = text; // set message text
		messageOn = true; // enable message rendering
		messageCounter = 0; // reset timer/counter
		messageDuration = durationFrames;
	}
    
    public void showMessage(String text, String colorName, int durationFrames) {
    				message = text; // set message text
    				messageOn = true; // enable message rendering
    				messageCounter = 0; // reset timer/counter
    				this.colorName = colorName;
    				messageDuration = durationFrames;
    }
    
    public void showMessage(String text, int x, int y) {
    		message = text; // set message text
    		messageOn = true; // enable message rendering
    		messageCounter = 0; // reset timer/counter
    		messageX = x;
    		messageY = y;
    }
    
    public void showMessage(String text, int x, int y, int durationFrames) {
    			message = text; // set message text
    			messageOn = true; // enable message rendering
    			messageCounter = 0; // reset timer/counter
    			messageX = x;
    			messageY = y;
    			messageDuration = durationFrames;
    }
    
    public void showMessage(String text, int x, int y, String colorName) {
    			message = text; // set message text
    			messageOn = true; // enable message rendering
    			messageCounter = 0; // reset timer/counter
    			messageX = x;
    			messageY = y;
    			this.colorName = colorName;
    }

    // showInteract: enable the interact hint text
    public void showInteract() {
    	String interactKey = KeyEvent.getKeyText(gp.keybinds[6]); // get key name for "interact" action
        interactMessage = "[" + interactKey + "] to interact"; // format hint text
    	interactOn = true; // enable rendering
    }

    // hideInteract: disable the interact hint
    public void hideInteract() {
        interactOn = false;
    }

    // main draw entry — called every frame by gamePanel.paintComponent(...)
    // handleInput() is called first so UI state (menu navigation, keybind capture) is processed
    public void draw(Graphics2D g2) {
        this.g2 = g2; // store the Graphics2D instance for helper methods below
        g2.setFont(arial_40); // set base font
        g2.setColor(Color.white); // default draw color

        // Process UI input first (consumes uiUp/uiDown/uiConfirm etc.)
        handleInput();

        // If the game is at the title screen, draw the title/menu
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        // If the game is in play state you could draw HUD elements here (not used)
        if (gp.gameState == gp.playState) {
            // (game HUD logic could go here)
        }
        // If the game is paused, draw pause screen
        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        // If the game is showing dialogue, draw the dialogue box
        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }
        if (gp.gameState == gp.deathState) {
			drawDeathScreen();
		}

        // KEYS: draw small inventory icons in the HUD area if player has them
        if (player.hasKey) {
            g2.drawImage(keyImage, gp.tileSize/2, gp.tileSize/2, gp.tileSize, gp.tileSize, null); // draw main key
        }
        if (player.hasGreenKey) {
            g2.drawImage(greenKeyImage, gp.tileSize/2 + 40, gp.tileSize/2, gp.tileSize, gp.tileSize, null); // green key
        }
        if (player.hasRedKey) {
            g2.drawImage(redKeyImage, gp.tileSize/2 + 80, gp.tileSize/2, gp.tileSize, gp.tileSize, null); // red key
        }
        if (player.hasTorch) {
            g2.drawImage(torchImage, gp.tileSize/2 + 160, gp.tileSize/2, gp.tileSize, gp.tileSize, null); // torch icon
        }
        if (player.hasBlueKey) {
            g2.drawImage(blueKeyImage, gp.tileSize/2 + 120, gp.tileSize/2, gp.tileSize, gp.tileSize, null); // blue key
        }

        // MESSAGES: draw temporary message if set, and auto-hide it after a counter
        if (messageOn) {
        	g2.setColor(switch(colorName) {
        	case "red" -> Color.red;
			case "yellow" -> Color.yellow;
			case "green" -> Color.green;
			default -> Color.white;
        	});
        	
            g2.drawString(message, messageX, messageY); // position message
            messageCounter++; // increment message timer
            if (messageCounter > messageDuration) { // if shown for enough frames
                messageCounter = 0; // reset
                messageOn = false; // hide message
            }
        }
        // INTERACT hint: draw "[E] to interact" if active
        if (interactOn) {
            g2.drawString(interactMessage, gp.tileSize/2, gp.tileSize*6 + 20);
        }
    }

    // This method contains all UI logic and consumes UI flags from keyHandler.
    // It handles menu navigation, sub-screens, and keybind assignment.
    public void handleInput() {
        // If we are waiting for a key to bind and a key has been captured, handle it first
        if (awaitingKeybind && capturedKeyPressed) {
            int code = capturedKey; // the key code captured by keyHandler
            capturedKeyPressed = false; // consume the captured event

            // Reject certain keys which would interfere with the UI or be confusing
            if (code == java.awt.event.KeyEvent.VK_ESCAPE || code == java.awt.event.KeyEvent.VK_BACK_SPACE || code == java.awt.event.KeyEvent.VK_ENTER) {
                showMessage("Can't bind Escape/Backspace/Enter. Try another key.", gp.tileSize*1, 504); // inform player
                return; // keep awaitingKeybind true so they can try again
            }

            // check duplicates: don't allow binding a key that's already used by another action
            for (int i = 0; i < gp.keybinds.length; i++) {
                if (i == keybindSelectedIndex) continue; // ignore the action we're re-assigning
                if (gp.keybinds[i] == code) { // found a duplicate
                    String alreadyFor = (i < keybindActionNames.length) ? keybindActionNames[i] : ("action " + i);
                    showMessage("Key already used for: " + alreadyFor, gp.tileSize*1, 504); // inform player
                    return; // keep awaitingKeybind true so they can pick another key
                }
            }

            // valid bind: assign the captured keycode to the selected action
            gp.keybinds[keybindSelectedIndex] = code;
            awaitingKeybind = false; // exit capture mode
            return; // done handling input this frame
        }

        // If we're not on the title screen, we ignore the UI navigation flags and clear them
        if (gp.gameState != gp.titleState) {
            // Clear any stray UI flags so they don't trigger when you return to the menu
            uiUp = uiDown = uiLeft = uiRight = uiConfirm = uiBack = false;
            return; // no UI handling this frame
        }

        // UI Behavior when on main title menu (titleScreenState == 0)
        if (titleScreenState == 0) {
            if (uiUp) { // up navigation pressed
                commandNum--; // move selection up
                if (commandNum < 0) commandNum = 4; // wrap around top->bottom
                uiUp = false; // consume the input (edge-trigger)
            }
            if (uiDown) { // down navigation pressed
                commandNum++; // move selection down
                if (commandNum > 4) commandNum = 0; // wrap bottom->top
                uiDown = false; // consume
            }
            if (uiConfirm) { // user activated the currently selected menu entry
                switch (commandNum) {
                    case 0: // NEW GAME: switch to play state
                        gp.gameState = gp.playState;
                        gp.playMusic(0);
                        break;
                    case 1: // LOAD GAME: show load sub-screen
                        titleScreenState = 1;
                        commandNum = 0; // reset local command index for sub-screen
                        break;
                    case 2: // CHARACTERS: show character selection sub-screen
                        titleScreenState = 2;
                        commandNum = 0;
                        gp.currentSkinIndex = gp.equippedSkinIndex; // start on equipped skin
                        break;
                    case 3: // KEYBINDINGS: show keybinds sub-screen
                        titleScreenState = 3;
                        commandNum = 0;
                        keybindSelectedIndex = 0; // select first action
                        awaitingKeybind = false; // ensure not in awaiting mode
                        break;
                    case 4: // EXIT: quit the application
                        System.exit(0);
                        break;
                }
                uiConfirm = false; // consume confirm
            }
            if (uiBack) { // back/cancel pressed on main screen — nothing to go back to
                uiBack = false; // just consume it
            }
            return; // done handling main menu input
        }

        // LOAD SCREEN (titleScreenState == 1): simple list of save slots + Back
        if (titleScreenState == 1) {
            if (uiUp) {
                commandNum--;
                if (commandNum < 0) commandNum = 2; // wrap for 3 items
                uiUp = false; // consume
            }
            if (uiDown) {
                commandNum++;
                if (commandNum > 2) commandNum = 0;
                uiDown = false;
            }
            if (uiConfirm) {
                if (commandNum == 2) { // Back entry
                    titleScreenState = 0; // return to main title
                    commandNum = 0;
                } else { // selecting a save slot (not implemented)
                    showMessage("Load slot " + (commandNum + 1) + " selected (not implemented)");
                }
                uiConfirm = false;
            }
            if (uiBack) { // back behavior
                titleScreenState = 0;
                commandNum = 0;
                uiBack = false;
            }
            return;
        }

        // CHARACTER SCREEN (titleScreenState == 2): cycle skins and equip
        if (titleScreenState == 2) {
        	
 
        	if (skinPreview == null) { //This loads the skin once
                SkinPrev(gp.currentSkinIndex);
        	}
        	
            if (uiLeft) { // previous skin
                gp.currentSkinIndex--;
                if (gp.currentSkinIndex < 0) { gp.currentSkinIndex = gp.skinNames.length - 1; } // wrap
                SkinPrev(gp.currentSkinIndex);
                uiLeft = false;
            }
            if (uiRight) { // next skin
                gp.currentSkinIndex++;
                if (gp.currentSkinIndex >= gp.skinNames.length) { gp.currentSkinIndex = 0;}
                SkinPrev(gp.currentSkinIndex);
                uiRight = false;
            }
            
            if (uiConfirm) { // equip current skin if unlocked
                if (gp.unlockedSkins[gp.currentSkinIndex]) {
                    gp.equippedSkinIndex = gp.currentSkinIndex;
                    showMessage("Equipped " + gp.skinNames[gp.currentSkinIndex]); // feedback
                } else {
                    showMessage("Skin is locked");
                }
                uiConfirm = false;
            }
            
            if (uiBack) { // return to main title
                titleScreenState = 0;
                commandNum = 0;
                uiBack = false;
                skinPreview = null; //Resets so it can load the next time
            }
            
            return;
        }

        // KEYBINDINGS SCREEN (titleScreenState == 3): select action, start capture, go back
        if (titleScreenState == 3) {
            if (!awaitingKeybind) { // normal navigation when not capturing a key
                if (uiUp) {
                    keybindSelectedIndex--;
                    if (keybindSelectedIndex < 0) keybindSelectedIndex = keybindActionNames.length - 1;
                    uiUp = false;
                }
                if (uiDown) {
                    keybindSelectedIndex++;
                    if (keybindSelectedIndex >= keybindActionNames.length) keybindSelectedIndex = 0;
                    uiDown = false;
                }
                if (uiConfirm) { // enter capture mode to bind a key
                    awaitingKeybind = true; // now UI will wait for capturedKeyPressed to be set
                    capturedKeyPressed = false; // ensure no stale key
                    capturedKey = -1; // reset captured code
                    uiConfirm = false; // consume confirm
                }
                if (uiBack) { // return to main menu
                    titleScreenState = 0;
                    commandNum = 0;
                    uiBack = false;
                }
            }
            // if awaitingKeybind == true, the capturedKeyPressed branch at the top handles assignment
            return;
        }
    }

    // drawTitleScreen and other drawing methods (these render the visual UI)
    public void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(0, 0, 0)); // black background
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // fill entire screen
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F)); // large bold title font
            String text = "Jone's Junction"; // title text
            int x = getXforCenteredText(text); // compute X so text is centered
            int y = gp.tileSize * 3; // baseline Y for title
            g2.setColor(Color.gray); // shadow color
            g2.drawString(text, x + 5, y + 5); // draw shadow slightly offset
            g2.setColor(Color.white); // main title color
            g2.drawString(text, x, y); // draw title

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F)); // menu font
            String[] menu = { "NEW GAME", "LOAD GAME", "CHARACTERS", "KEYBINDINGS", "EXIT" }; // menu labels
            int localY = y; // local Y cursor for menu
            for (int i = 0; i < menu.length; i++) {
                text = menu[i]; // menu item text
                x = getXforCenteredText(text); // center the item
                localY += gp.tileSize * (i == 0 ? 3 : 1); // spacing (first item sits lower)
                if (commandNum == i) { // highlighted item
                    g2.setColor(Color.gray); // dim text color for selected item (style choice)
                    g2.drawString(text, x, localY); // draw text
                    g2.setColor(Color.white); // arrow color
                    g2.drawString(">", x - gp.tileSize, localY); // draw selector arrow on left
                } else {
                    g2.setColor(Color.white); // normal color
                    g2.drawString(text, x, localY); // draw non-selected item
                }
            }
            g2.setColor(Color.white); // reset color
        } else if (titleScreenState == 1) { // LOAD screen
            g2.setColor(Color.black); // background
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            String title = "LOAD GAME"; // header text
            int x = getXforCenteredText(title);
            int y = gp.tileSize * 2;
            g2.setColor(Color.white);
            g2.drawString(title, x, y); // draw header

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // smaller font for list
            String[] saves = { "Save slot 1 - (empty)", "Save slot 2 - (empty)", "Back" }; // placeholder entries
            int localY = y;
            for (int i = 0; i < saves.length; i++) {
                String s = saves[i];
                int sx = getXforCenteredText(s); // center each entry
                localY += gp.tileSize; // vertical spacing
                if (commandNum == i) { // highlighted row
                    g2.setColor(Color.gray);
                    g2.drawString(s, sx, localY);
                    g2.setColor(Color.white);
                    g2.drawString(">", sx - gp.tileSize, localY); // arrow
                } else {
                    g2.setColor(Color.white);
                    g2.drawString(s, sx, localY);
                }
            }
        } else if (titleScreenState == 2) { // CHARACTER screen
            g2.setColor(Color.black);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            String title = "CHARACTERS";
            int x = getXforCenteredText(title);
            int y = gp.tileSize * 2;
            g2.setColor(Color.white);
            g2.drawString(title, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
            String skinName = gp.skinNames[gp.currentSkinIndex]; // name of currently previewed skin
            y += gp.tileSize*4;
            x = getXforCenteredText(skinName);
            g2.drawString("<", 234, y); // left arrow hint (visual only)
            g2.drawString(">", 522, y); // right arrow hint
            g2.drawString(skinName, x, y); // draw skin name

            y += gp.tileSize+(gp.tileSize/1.5);
            // compute status: Equipped / Unlocked / Locked
            String status = gp.unlockedSkins[gp.currentSkinIndex] ?
                    (gp.equippedSkinIndex == gp.currentSkinIndex ? "Equipped" : "Unlocked") : "Locked";
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F)); // smaller font for status
            g2.drawString(status, 345, y);

            y += gp.tileSize*4;
            // helper instructions
            g2.drawString("ESCAPE to go back.", gp.tileSize/2, y);
            
            ///////////////// Draws & centers the skin
            if (skinPreview != null) {
                g2.drawImage(
                    skinPreview,
                    gp.screenWidth / 2 - gp.tileSize * 2,//x-axis, with centering cause I can't estimate
                    gp.tileSize * 8,//y-axis
                    gp.tileSize * 4,//width
                    gp.tileSize * 4,//height
                    null
                ); }
            ////////////////////

        } else if (titleScreenState == 3) { // KEYBINDINGS screen
            g2.setColor(Color.black);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            String title = "KEYBINDINGS";
            int x = getXforCenteredText(title);
            int y = gp.tileSize;
            g2.setColor(Color.white);
            g2.drawString(title, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
            int localY = y;
            for (int i = 0; i < keybindActionNames.length; i++) {
                String action = keybindActionNames[i]; // action label
                String keyName = java.awt.event.KeyEvent.getKeyText(gp.keybinds[i]); // readable key name for current binding
                String line = String.format("%-20s { %s }", action, keyName); // format display line
                localY += gp.tileSize / 1.05; // vertical spacing
                int sx = gp.tileSize; // X offset for list
                if (keybindSelectedIndex == i) { // highlighted row
                    g2.setColor(Color.gray);
                    g2.drawString(line, sx, localY);
                    g2.setColor(Color.white);
                    g2.drawString(">", sx - gp.tileSize, localY); // selection arrow
                } else {
                    g2.setColor(Color.white);
                    g2.drawString(line, sx, localY);
                }
            }

            localY += gp.tileSize/0.5;
            if (awaitingKeybind) { // if in key-capture mode, show instruction
                g2.setColor(Color.yellow);
                g2.drawString("Press any key to bind for: " + keybindActionNames[keybindSelectedIndex], gp.tileSize, localY);
                g2.setColor(Color.white);
            } else { // otherwise show navigation hint
                g2.drawString("ESCAPE to go back.", gp.tileSize, localY);
            }
        }
    }
    
    public void drawDeathScreen() {
    	// create a semi-transparent black overlay
		Color c = new Color(0, 0, 0, 200); // RGBA
		g2.setColor(c);
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // fill entire screen

		// Draw "YOU DIED" text in the center
		String text = "YOU DIED";
		int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text);
		int y = gp.screenHeight / 2;

		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F)); // large bold font
		g2.setColor(Color.red); // red color for death message
		g2.drawString(text, x, y); // draw death text
		
		// Draw "ENTER to Restart  ESCAPE to exit" prompt below 
		
		String prompt = "ENTER to Restart  ESCAPE to exit";
		x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(prompt) / 5 + 15;
		y += gp.tileSize * 1;
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F)); // smaller font for prompt
		g2.setColor(Color.white); // white color for prompt
		g2.drawString(prompt, x, y); // draw prompt
		
		
		
    }

    // Draw the large "PAUSED" screen in the center
    public void drawPauseScreen() {
        String text = "PAUSED";
        int x = getXforCenteredText(text); // center X
        int y = gp.screenHeight / 2; // center Y

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F)); // big font
        g2.drawString(text, x, y); // draw paused text
    }

    // Draw a dialogue window with multi-line text
    public void drawDialogueScreen() {
        int x = gp.tileSize * 2; // left padding
        int y = gp.tileSize * 6; // top padding
        int width = gp.screenWidth - (gp.tileSize * 4); // width of the dialogue box
        int height = gp.tileSize * 4; // height of the dialogue box

        drawSubWindow(x, y, width, height); // draw panel background and border

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // smaller dialogue font
        x += gp.tileSize; // inner padding
        y += gp.tileSize; // inner padding

        for (String line : currentDialogue.split("\n")) { // split by newline and draw each line
            g2.drawString(line, x, y);
            y += 40; // line spacing
        }
    }
    
    // drawSubWindow: helper that renders a rounded black translucent panel with white border
    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210); // semi-transparent black
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35); // filled rounded rect

        c = new Color(255, 255, 255); // white for border
        g2.setColor(c);
        g2.setStroke(new java.awt.BasicStroke(5)); // thicker stroke for border
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25); // draw border inside
    }

    // getXforCenteredText: compute X so given text is horizontally centered on screen
    public int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth(); // pixel width of text
        int x = gp.screenWidth / 2 - length / 2; // center formula
        return x;
    }
    /////////////////////////////Uses
    public void SkinPrev(int skinIndex) {
        try {
            InputStream skin = getClass().getResourceAsStream(gp.skinPaths[skinIndex]); //gets the image (skin)

            if (skin == null) {
                System.out.println("The skin preview has not been found I guess: " + gp.skinPaths[skinIndex]); //security
                skinPreview = null;
                return;
            }

            skinPreview = ImageIO.read(skin); //load image into character menu

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
