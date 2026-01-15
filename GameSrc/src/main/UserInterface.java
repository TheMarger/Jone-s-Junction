package main; // package this class belongs to

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import entity.player;
import saves.save1;
import saves.save2;
import saves.save3;
import task.Task;
import Item.*;
import Item.Throwable;

public class UserInterface {

    gamePanel gp;
    Graphics2D g2;
    Font arial_40;
    BufferedImage keyImage, greenKeyImage, redKeyImage, torchImage, blueKeyImage;
    BufferedImage skinPreview;

    // messages / UI
    public boolean messageOn = false;
    public boolean boxMessageOn = false;
    public boolean interactOn = false;
    public String interactMessage = "";
    public String message = "";
    public String boxMessage = "";
    int messageCounter = 0;
    int messageDuration = 80;
    int messageX;
    int messageY;
    String colorName = "white";
    public boolean showThrowRadius = false;
    public Throwable activeThrowable;
    
    // Tile select task variables
    private boolean tileSelectGenerated = false;

    private static final int TS_GRID = 6; // 6x6
    private static final int TS_FLASH_COUNT = 6;

    private int tsCellSize;
    private int tsGridX, tsGridY;

    private boolean[][] tsPattern = new boolean[TS_GRID][TS_GRID];
    private boolean[][] tsSelected = new boolean[TS_GRID][TS_GRID];

    private int tsPhase = 0; // 0=flash, 1=blank pause, 2=input, 3=feedback
    private int tsFlashFrames = (int) (3.0 * 60);
    private int tsBlankFrames = (int) (2.0 * 60); // 120 frames
    private int tsTimer = 0;

    private boolean tsResult = false;


    // task / math variables
    private boolean taskGenerated = false;
    private String question = "";
    private int correctAnswer = 0;
    private String playerInput = "";
    private boolean answerChecked = false;
    private boolean answerCorrect = false;

    // task counters / timers (math)
    private int taskTimerFrames = 0;
    private int taskTimeLimitFrames = 0;
    private int questionsAsked = 0;
    private int correctCount = 0;
    private int wrongCount = 0;

    // riddle task state
    private boolean riddleGenerated = false;
    private String riddleQuestion = "";
    private String riddleAnswer = "";
    private String riddlePlayerInput = "";
    private boolean riddleAnswerChecked = false;
    private boolean riddleAnswerCorrect = false;
    private int riddleTimerFrames = 0;
    private int riddleTimeLimitFrames = 45 * 60;
    
    
 // BUTTON MATCH TASK VARIABLES
    private boolean buttonMatchGenerated = false;
    private boolean buttonMatchResolved = false;

    private long buttonMatchStartNano = 0;
    private double buttonMatchTargetSeconds = 0;   
    private final double buttonMatchWindow = 0.10; // +/- 0.10 seconds window

    private Rectangle buttonMatchButtonRect = new Rectangle();
    private String buttonMatchFeedback = "";
    private int buttonMatchFeedbackFrames = 0;
    
	// pattern switches task
	private boolean patternGenerated = false;
	private int[] patternSequence = new int[0];
	private int patternLength = 0;
	private boolean patternShowing = true;

	// timing (frames @ 60fps)
	private int patternFlashFrames = 30; // 0.5s
	private int patternGapFrames = 6; // small gap between flashes (optional)
	private int patternIndex = 0;
	private int patternFlashTimer = 0; // counts down within flash
	private int patternGapTimer = 0; // counts down between flashes
	private int patternInputIndex = 0;
	private int patternInputTimerFrames = 0;
	private int patternInputLimitFrames = 5 * 60; // 5 seconds
	private boolean patternChecked = false;
	private boolean patternSuccess = false; {

	// Pattern Switches
	patternGenerated = false;
	patternSequence = new int[0];
	patternLength = 0;
	patternShowing = true;
	patternIndex = 0;
	patternFlashTimer = 0;
	patternGapTimer = 0;
	patternInputIndex = 0;
	patternInputTimerFrames = 0;
	patternInputLimitFrames = 5 * 60;
	patternChecked = false;
	patternSuccess = false; }

    
 // VAULT SEQUENCE TASK VARIABLES
    private boolean vaultGenerated = false;
    private boolean vaultResolved = false;

    private int[] vaultSequence;          // the generated sequence (like 2,4,1,3)
    private int vaultSeqLen = 0;          // length of sequence
    private int vaultProgressIndex = 0;   // how many correct inputs so far

    private int vaultStrikes = 0;         // mistakes
    private int vaultMaxStrikes = 2;      // fail after 2 mistakes

    private int vaultTimerFrames = 0;
    private int vaultTimeLimitFrames = 0;

    private Rectangle[] vaultButtonRects = new Rectangle[4];  // clickable tiles/buttons
    private String vaultFeedback = "";
    private int vaultFeedbackFrames = 0;

    // optional: reuse riddle pool as flavor text (not required but matches your "use same riddles" idea)
    private String vaultFlavorRiddle = "";
    
    // riddle pool
    private final String[] RIDDLE_QUESTIONS = {
        "What has to be broken before you can use it?",
        "I speak without a mouth and hear without ears. I have no body, but I come alive with the wind.",
        "What can travel around the world while staying in a corner?",
        "I have keys but no locks. I have space but no room. You can enter but can't go outside. What am I?",
        "What gets wetter as it dries?",
        "What has a heart that doesn’t beat?",
        "What begins with T, ends with T, and has T in it?",
        "What has one eye, but cannot see?",
        "What can you catch, but not throw?",
        "The more you take, the more you leave behind. What are they?",
        "What has many teeth but cannot bite?",
        "What is so fragile that saying its name breaks it?",
        "What goes up but never comes down?",
        "I’m light as a feather, yet the strongest man can’t hold me for more than five minutes. What am I?",
        "What has hands but cannot clap?",
        "What building has the most stories?",
        "What can fill a room but takes up no space?",
        "I’m tall when I’m young and short when I’m old. What am I?",
        "What runs, but never walks; has a bed but never sleeps?",
        "What tastes better than it smells?",
        "What can you keep after giving it to someone?",
        "What has a neck but no head?",
        "What has an eye but cannot see?",
        "What has an endless supply of letters but starts empty?",
        "If you drop me I'm sure to crack, but smile at me and I'll smile back. What am I?",
        "I have branches, but no fruit, trunk or leaves. What am I?",
        "I follow you all the time and copy your every move, but you can’t touch me or catch me. What am I?",
        "What is always in front of you but can’t be seen?",
        "I shave every day, but my beard stays the same. Who am I?",
        "What begins with an E but only has one letter?"
    };

    private final String[] RIDDLE_ANSWERS = {
        "an egg",
        "an echo",
        "a stamp",
        "a keyboard",
        "a towel",
        "an artichoke",
        "a teapot",
        "a needle",
        "a cold",
        "footsteps",
        "a comb",
        "silence",
        "your age",
        "breath",
        "a clock",
        "a library",
        "light",
        "a candle",
        "a river",
        "your tongue",
        "a promise",
        "a bottle",
        "a needle",
        "a mailbox",
        "an egg",
        "a tree",
        "your shadow",
        "the future",
        "a barber",
        "an envelope"
    };

    // Generic cooldown used across tasks (frames @ 60fps)
    private int taskCooldownFrames = 0;
    private final int DEFAULT_TASK_COOLDOWN_SECONDS = 10;

    // Title/menu state
    public int commandNum = 0;
    public int titleScreenState = 0;

    public boolean levelFinished = false;
    double playTime;
    DecimalFormat df = new DecimalFormat("#0.00");

    public String currentDialogue = "";
    public String currentDialogueSpeaker = "";

    // UI input flags (set by keyHandler)
    public boolean uiUp = false;
    public boolean uiDown = false;
    public boolean uiLeft = false;
    public boolean uiRight = false;
    public boolean uiConfirm = false;
    public boolean uiBack = false;

    // keybind editing
    public boolean awaitingKeybind = false;
    public boolean capturedKeyPressed = false;
    public int capturedKey = -1;
    public int keybindSelectedIndex = 0;
    public final String[] keybindActionNames = {"Move Forward","Move Backward","Move Left","Move Right","Sprint","Crouch","Interact","Throw Item","Drop Item"};

    public int slotRow = -1;
    public Item selectedItem;
    

    public UserInterface(gamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Cambria", Font.PLAIN, 40);

        // sample item image caching - keep as in original
        try {
            Key key = new Key(gp);
            greenKey greenK = new greenKey(gp);
            redKey redK = new redKey(gp);
            Flashlight torch = new Flashlight(gp);
            blueKey blueK = new blueKey(gp);
            keyImage = key.image;
            greenKeyImage = greenK.image;
            redKeyImage = redK.image;
            torchImage = torch.image;
            blueKeyImage = blueK.image;
        } catch (Exception e) {
            // ignore if images not present during compile / quick tests
        }

        messageX = gp.tileSize/2;
        messageY = gp.tileSize*5;
        messageDuration = 80;
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
    
    public void showBoxMessage(String text) {
    			boxMessage = text; // set box message text
    			boxMessageOn = true; // enable box message rendering
    			messageCounter = 0; // reset timer/counter
    }
    
    public void showBoxMessage(String text, int x, int y) {
    				boxMessage = text; // set box message text
    				boxMessageOn = true; // enable box message rendering
    				messageCounter = 0; // reset timer/counter
    				messageX = x;
    				messageY = y;
    				
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
    public void draw(Graphics2D g2) {
        this.g2 = g2; // store the Graphics2D instance for helper methods below
        g2.setFont(arial_40); // set base font
        g2.setColor(Color.white); // default draw color

        // Tick global cooldown every frame (so it begins immediately when a task fails)
        if (taskCooldownFrames > 0) {
            taskCooldownFrames--;
            if (taskCooldownFrames < 0) taskCooldownFrames = 0;
        }

        // Global Escape handler: if the player presses Escape while in a task, abort and reset
        if (gp.gameState == gp.taskState && gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return; // stop drawing task UI this frame
        }

        // Process UI input first (consumes uiUp/uiDown/uiConfirm etc.)
        handleInput();

        // Title & main states drawing (preserve your existing structure)
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        if (gp.gameState == gp.playState) {
            drawInventory();
            drawStaminaBar();
            drawTasksList();
        }
        if (gp.gameState == gp.pauseState) drawPauseScreen();
        if (gp.gameState == gp.dialogueState) drawDialogueScreen();
        if (gp.gameState == gp.deathState) drawDeathScreen();

        // Task state: pick task by player's current task name
        if (gp.gameState == gp.taskState) {
            if (gp.player != null && gp.player.curTaskName != null) {
                switch (gp.player.curTaskName) {
                    case "Math Task" -> drawMathTask();
                    case "Riddle Task" -> drawRiddleTask();
                    case "Tile Select Task" -> drawTileSelectTask();
                    case "Button Task" -> drawButtonMatchTask();
                    case "Vault Sequence Task" -> drawVaultSequenceTask();
                    case "Fuse Repair Task" -> drawPatternSwitchTask();
                    // other task types fall back to math or can be added
                    default -> drawMathTask();
                }
            } else {
                drawMathTask();
            }
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
            messageCounter++;
            if (messageCounter > messageDuration) {
                messageCounter = 0;
                messageOn = false;
            }
        }
        if (boxMessageOn) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // smaller font for box message
            int padding = 8;
            int textWidth = g2.getFontMetrics().stringWidth(boxMessage);
            int textHeight = g2.getFontMetrics().getHeight();
            int frameX = messageX - padding;
            int frameY = messageY - textHeight + 8; // adjust baseline
            int frameWidth = textWidth + padding * 2;
            int frameHeight = textHeight + padding / 2;
            // draw semi-transparent background
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10);
            // draw border
            g2.setColor(Color.white);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10);
            // draw the box message text (center baseline)
            g2.setColor(Color.white);
            g2.drawString(boxMessage, messageX, messageY);

            messageCounter++;
            if (messageCounter > messageDuration) {
                messageCounter = 0;
                boxMessageOn = false;
            }
        }

        // INTERACT hint
        if (interactOn && gp.gameState == gp.playState) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
            int padding = 8;
            int textWidth = g2.getFontMetrics().stringWidth(interactMessage);
            int textHeight = g2.getFontMetrics().getHeight();
            int frameX = gp.tileSize / 2 - padding;
            int frameY = gp.tileSize * 6 + 20 - textHeight + 8;
            int frameWidth = textWidth + padding * 2;
            int frameHeight = textHeight + padding / 2;
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10);
            g2.setColor(Color.white);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10);
            g2.setColor(Color.white);
            g2.drawString(interactMessage, gp.tileSize / 2, gp.tileSize * 6 + 20);
        }
        if (selectedItem != null) {
        	if (!selectedItem.getName().equals("") && gp.gameState == gp.playState) {
                if (selectedItem.getName().equals("Flashlight")) {
                    String[] options = { "[" + gp.interactKey + "] Use", "["+ gp.dropKey +"] Drop" };
                    drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28);
                    
                    String[] description = {"Type: Tool", "Illuminate dark areas."};
                    drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20);
                    
                } else if (selectedItem instanceof Food) {
					String[] options = { "[" + gp.interactKey + "] Eat", "[" + gp.throwKey + "] Throw" , "[" + gp.dropKey + "] Drop" };
					drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28);
					Food food = (Food) selectedItem;
					String[] description = {"Type: Food", "Restores " + ((int) (food.restoreValue*100)) +"% stamina when eaten", "Sound value if thrown: Low"};
					drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20);
				} 
                else if (selectedItem instanceof Throwable) {
                	String[] options = {"["+gp.throwKey+"] Throw", "["+ gp.dropKey +"] Drop" };
                	drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28);
                	
                	Throwable throwable = (Throwable) selectedItem;
                	String[] description = {
                		    "Type: Throwable",
                		    "Can be thrown to distract guards",
                		    "Sound value if thrown: " +
                		        (throwable.throwSoundIndex == 3 ? "Low" :
                		         throwable.throwSoundIndex == 5 ? "Med" :
                		         throwable.throwSoundIndex == 7 ? "High" : "Unknown"),
                		    "Allowed Throw Radius: " + throwable.getAllowedRadiusTiles() + " tiles."
                		};
                	drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20);
                } else if (selectedItem instanceof Key) {
					String[] options = { "[" + gp.dropKey + "] Drop" };
					drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28);
					String[] description = {"Type: Key", "Unlock doors of matching color."};
					drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20);
				}
                else {
                	String[] options = { "[" + gp.dropKey + "] Drop" };
    				drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28);
                }
                
            }
		}
        
        if (showThrowRadius) {
            drawThrowRadius(activeThrowable);
        }

    }
    
    public void drawThrowRadius(Throwable item) {
        if (item == null) return;

        // allowed radius in tiles (centered on player tile)
        int radiusTiles = item.getAllowedRadiusTiles();

        // player tile
        int playerCol = gp.player.worldX / gp.tileSize;
        int playerRow = gp.player.worldY / gp.tileSize;

        // Save old composite/stroke so we can restore later
        java.awt.Composite oldComp = g2.getComposite();
        java.awt.Stroke oldStroke = g2.getStroke();

        // translucent fill for in-range tiles
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.15f));
        g2.setColor(new java.awt.Color(50, 120, 220)); // translucent fill (alpha via composite)

        // iterate bounding box (clamped to world)
        int fromCol = Math.max(0, playerCol - radiusTiles);
        int toCol   = Math.min(gp.maxWorldCol - 1, playerCol + radiusTiles);
        int fromRow = Math.max(0, playerRow - radiusTiles);
        int toRow   = Math.min(gp.maxWorldRow - 1, playerRow + radiusTiles);

        for (int c = fromCol; c <= toCol; c++) {
            for (int r = fromRow; r <= toRow; r++) {
                int dx = c - playerCol;
                int dy = r - playerRow;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= radiusTiles + 0.0001) { // inside circle
                    // compute screen coords for this tile
                    int screenX = c * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
                    int screenY = r * gp.tileSize - gp.player.worldY + gp.player.getScreenY();

                    // only draw tiles that are on-screen (simple frustum cull)
                    if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth || screenY + gp.tileSize < 0 || screenY > gp.screenHeight) {
                        continue;
                    }

                    g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                }
            }
        }

        // outline the hovered tile with a highlighted border
        if (gp.hoveredTileCol >= 0 && gp.hoveredTileRow >= 0) {
            int dxH = gp.hoveredTileCol - playerCol;
            int dyH = gp.hoveredTileRow - playerRow;
            double distH = Math.sqrt(dxH * dxH + dyH * dyH);

            if (distH <= radiusTiles + 0.0001) {
                // strong highlight for hovered tile inside range
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
                g2.setStroke(new java.awt.BasicStroke(3f));
                g2.setColor(new java.awt.Color(220, 220, 50)); // yellow-ish outline
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY();
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2);
            } else {
                // hovered out-of-range: dim outline (optional)
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.9f));
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.setColor(new java.awt.Color(140, 140, 140));
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY();
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2);
            }
        }

        // outline the selected tile (from mouse click) — only if inside range draw as "selectable"
        if (gp.selectedThrowCol >= 0 && gp.selectedThrowRow >= 0) {
            int dxS = gp.selectedThrowCol - playerCol;
            int dyS = gp.selectedThrowRow - playerRow;
            double distS = Math.sqrt(dxS * dxS + dyS * dyS);

            int sX = gp.selectedThrowCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
            int sY = gp.selectedThrowRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY();

            // only draw selection if it's on-screen
            if (!(sX + gp.tileSize < 0 || sX > gp.screenWidth || sY + gp.tileSize < 0 || sY > gp.screenHeight)) {
                if (distS <= radiusTiles + 0.0001) {
                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
                    g2.setStroke(new java.awt.BasicStroke(3f));
                    g2.setColor(new java.awt.Color(80, 200, 100)); // green outline for selected target
                    g2.drawRect(sX + 1, sY + 1, gp.tileSize - 2, gp.tileSize - 2);

                    if (gp.mouseClicked) {
                        gp.player.throwItem(activeThrowable, gp.selectedThrowCol, gp.selectedThrowRow);

                        // consume click and close the throw UI
                        gp.mouseClicked = false;
                        showThrowRadius = false;
                        activeThrowable = null;
                        gp.selectedThrowCol = -1;
                        gp.selectedThrowRow = -1;
                        selectedItem = null;
                    }
                } else {
                    // selected out-of-range: show a dim/red outline to indicate invalid target
                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
                    g2.setStroke(new java.awt.BasicStroke(3f));
                    g2.setColor(new java.awt.Color(200, 80, 80)); // red outline for invalid selection
                    g2.drawRect(sX + 1, sY + 1, gp.tileSize - 2, gp.tileSize - 2);
                }
            }
        }

        // restore
        g2.setComposite(oldComp);
        g2.setStroke(oldStroke);
    
	

        // outline the hovered tile with a highlighted border
        if (gp.hoveredTileCol >= 0 && gp.hoveredTileRow >= 0) {
            // check if hovered is inside radius — if not, draw it but in a dim color (optional)
            int dxH = gp.hoveredTileCol - playerCol;
            int dyH = gp.hoveredTileRow - playerRow;
            double distH = Math.sqrt(dxH * dxH + dyH * dyH);

            if (distH <= radiusTiles + 0.0001) {
                // strong highlight for hovered tile inside range
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
                g2.setStroke(new java.awt.BasicStroke(3f));
                g2.setColor(new java.awt.Color(220, 220, 50)); // yellow-ish outline
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY();
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2);
            } else {
                // hovered out-of-range: dim outline (optional)
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.9f));
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.setColor(new java.awt.Color(140, 140, 140));
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY();
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2);
            }
        }

        // outline the selected tile (from mouse click) with a distinct color
        if (gp.selectedThrowCol >= 0 && gp.selectedThrowRow >= 0) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
            g2.setStroke(new java.awt.BasicStroke(3f));
            g2.setColor(new java.awt.Color(80, 200, 100)); // green outline for selected target
            int sX = gp.selectedThrowCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX();
            int sY = gp.selectedThrowRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY();
            g2.drawRect(sX + 1, sY + 1, gp.tileSize - 2, gp.tileSize - 2);
        }

        // restore
        g2.setComposite(oldComp);
        g2.setStroke(oldStroke);
        
        // Show text hint
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        String hint = "Click within radius to throw";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect((gp.screenWidth - hintWidth) / 2 - 10, gp.screenHeight - gp.tileSize - 40, hintWidth + 20, 40, 10, 10);
        g2.setColor(Color.white);
        g2.drawString(hint, (gp.screenWidth - hintWidth) / 2, gp.screenHeight - gp.tileSize - 15);
        
    }



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
        if (gp.gameState != gp.titleState && gp.gameState != gp.pauseState) {
            // Clear any stray UI flags so they don't trigger when you return to the menu
            uiUp = uiDown = uiLeft = uiRight = uiConfirm = uiBack = false;
            return; // no UI handling this frame
        }
        
        if (gp.gameState == gp.pauseState) {
			if (uiConfirm) {
				titleScreenState = 6; // save 
				gp.gameState = gp.titleState;
				uiConfirm = false;
			}
			if (uiBack) {
				gp.gameState = gp.playState;
				uiBack = false;
			}
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
                        gp.player.currentSkinIndex = gp.player.equippedSkinIndex; // start on equipped skin
                        break;
                    case 3: // KEYBINDINGS: show keybinds sub-screen
                        titleScreenState = 3;
                        commandNum = 0;
                        keybindSelectedIndex = 0; // select first action
                        awaitingKeybind = false; // ensure not in awaiting mode
                        break;
                    case 4: // INSTRUCTIONS: show instructions screen
                    	titleScreenState = 4;
                    	commandNum = 0;
                    	keybindSelectedIndex = 0;
                    	awaitingKeybind = false;
                        break;
                    case 5: // EXIT: close the game
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
                if (commandNum < 0) commandNum = 3; // wrap for 3 items
                uiUp = false; // consume
            }
            if (uiDown) {
                commandNum++;
                if (commandNum > 3) commandNum = 0;
                uiDown = false;
            }
            if (uiConfirm) {
                if (commandNum == 3) { // Back entry
                    titleScreenState = 0; // return to main title
                    commandNum = 0;
                } else { // selecting a save slot (not implemented)
					gp.loadGame(commandNum+1);
					titleScreenState = 0;
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
                SkinPrev(gp.player.currentSkinIndex);
        	}
        	
        	if (uiLeft) {
        	    gp.player.currentSkinIndex--;
        	    if (gp.player.currentSkinIndex < 0) gp.player.currentSkinIndex = gp.skins.length - 1;
        	    SkinPrev(gp.player.currentSkinIndex);
        	    uiLeft = false;
        	}
        	if (uiRight) {
        	    gp.player.currentSkinIndex++;
        	    if (gp.player.currentSkinIndex >= gp.skins.length) gp.player.currentSkinIndex = 0;
        	    SkinPrev(gp.player.currentSkinIndex);
        	    uiRight = false;
        	}

        	if (uiConfirm) {
        	    if (gp.player.unlockedSkins[gp.player.currentSkinIndex]) {
        	        gp.player.equipSkin(gp.player.currentSkinIndex); // centralised equip logic
        	        String skinName = gp.skins[gp.player.currentSkinIndex][0][0];
        	        showMessage("Equipped " + skinName);
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
        
        if (titleScreenState == 4) { // INSTRUCTIONS SCREEN 
            if (!awaitingKeybind) { // normal navigation when not capturing a key      
                if (uiBack) { // return to main menu
                    titleScreenState = 0;
                    commandNum = 0;
                    uiBack = false;
                }
            }
            }
            if (titleScreenState == 6) {

                if (!awaitingKeybind) {

                    if (uiUp) {
                        commandNum--;
                        if (commandNum < 0) {
							commandNum = 3;
						}
                        uiUp = false;
                    }

                    if (uiDown) {
                        commandNum++;
                       if (commandNum > 3) {
                    	   commandNum = 0;
                       }
                        uiDown = false;
                    }

                    if (uiConfirm) {
                        if (commandNum == 3) { // Back
                            gp.gameState = gp.pauseState;
                        } else {
                            // SAVE SLOT SELECTED
                            showMessage("Saved to Slot " + (commandNum + 1));
                            gp.saveGame(commandNum+1);
                            gp.resetGame(true);
                        }
                        uiConfirm = false;
                    }

                    if (uiBack) {
                        titleScreenState = 0;
                        commandNum = 0;
                        uiBack = false;
                    }
                }

                return;
            }

   
    }
    
    public void drawInventory() {

        // ===== CONFIG =====
        final int slotSize = gp.tileSize; // 48x48
        final int slots = 3;
        final int padding = 12;
        final int slotGap = 8;

        // ===== FRAME SIZE =====
        int frameWidth = padding * 2 + (slotSize * slots) + (slotGap * (slots - 1));

        int frameHeight = padding * 2 + slotSize;

        // ===== FRAME POSITION (TOP RIGHT) =====
        int frameX = gp.screenWidth - frameWidth - gp.tileSize / 2;
        int frameY = gp.tileSize / 2;

        // ===== DRAW FRAME =====
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 25, 25);

        g2.setColor(Color.white);
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 25, 25);

        // ===== SLOT START POSITION =====
        int slotXstart = frameX + padding;
        int slotYstart = frameY + padding;

        // ===== DRAW SLOTS =====
        for (int i = 0; i < slots; i++) {
            int slotX = slotXstart + i * (slotSize + slotGap);
            int slotY = slotYstart;

            g2.setColor(new Color(60, 60, 60, 200));
            g2.fillRoundRect(slotX, slotY, slotSize, slotSize, 10, 10);

            g2.setColor(Color.white);
            g2.drawRoundRect(slotX, slotY, slotSize, slotSize, 10, 10);
        }
        
        // draw items in slots
        for (int i = 0; i < gp.player.inventory.size() && i < slots; i++) {
			Item item = gp.player.inventory.get(i);
			int slotX = slotXstart + i * (slotSize + slotGap);
			int slotY = slotYstart;

			g2.drawImage(item.image, slotX + 8, slotY + 8, slotSize - 16, slotSize - 16, null);
		}

        // draw cursor 
        if (slotRow > -1) {
	        int cursorX = slotXstart + slotRow * (slotSize + slotGap);
	        int cursorY = slotYstart;
	        g2.setColor(Color.yellow);
	        g2.drawRoundRect(cursorX - 4, cursorY - 4, slotSize + 8, slotSize + 8, 12, 12);
	        if (gp.player.inventory.size() > slotRow) {
	        	selectedItem = gp.player.inventory.get(slotRow);
	        }
        } else {
			selectedItem = null;
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
            String[] menu = { "NEW GAME", "LOAD GAME", "CHARACTERS", "KEYBINDINGS", "INSTRUCTIONS", "EXIT" }; // menu labels
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
            String[] saves = new String[4];

            saves[0] = "Save Slot 1 " + (save1.fileExists() ? "(exists)" : "(empty)");
            saves[1] = "Save Slot 2 " + (save2.fileExists() ? "(exists)" : "(empty)");
            saves[2] = "Save Slot 3 " + (save3.fileExists() ? "(exists)" : "(empty)");
            saves[3] = "Back";

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

            // --- Title ---
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            String title = "CHARACTERS";
            int x = getXforCenteredText(title);
            int y = gp.tileSize * 2;
            g2.setColor(Color.white);
            g2.drawString(title, x, y);
            
            String status;
            if (!gp.player.unlockedSkins[gp.player.currentSkinIndex]) {
                status = "Locked";
            } else if (gp.player.equippedSkinIndex == gp.player.currentSkinIndex) {
                status = "Equipped";
            } else {
                status = "Unlocked";
            }



            // --- Status (above image) ---
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
            int statusX = getXforCenteredText(status);
            int statusY = gp.tileSize * 4; // vertical position above image
            g2.drawString(status, statusX, statusY);

            // --- Skin Preview Image ---
            if (skinPreview != null) {
                int imgWidth = gp.tileSize * 4;
                int imgHeight = gp.tileSize * 4;
                int imgX = gp.screenWidth / 2 - imgWidth / 2;
                int imgY = statusY + gp.tileSize; // place image below status
                g2.drawImage(skinPreview, imgX, imgY, imgWidth, imgHeight, null);

                // --- Arrows (fixed beside image) ---
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
                g2.drawString("<", imgX - gp.tileSize, imgY + imgHeight / 2 + 16); // left
                g2.drawString(">", imgX + imgWidth + 10, imgY + imgHeight / 2 + 16); // right

                // --- Skin Name (below image) ---
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
                String skinName = gp.skins[gp.player.currentSkinIndex][0][0];
                int nameX = getXforCenteredText(skinName);
                int nameY = imgY + imgHeight + gp.tileSize / 2;
                g2.drawString(skinName, nameX, nameY);
            }

            // --- Instructions ---
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
            g2.drawString("ESCAPE to go back.", gp.tileSize / 2, gp.screenHeight - gp.tileSize);
        }

        else if (titleScreenState == 3) { // KEYBINDINGS screen
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
        else if (titleScreenState == 4) { // INSTRUCTIONS screen
			g2.setColor(Color.black);
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
			String title = "INSTRUCTIONS";
			int x = getXforCenteredText(title);
			int y = gp.tileSize;
			g2.setColor(Color.white);
			g2.drawString(title, x, y);

			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
			String[] instructions = {
				"Use WASD or Arrow keys to move.",
				"Press SPACE to interact with objects/NPCs.",
				"Open inventory with I key.",
				"Access this instructions screen from the main menu.",
				"Complete tasks to progress in the game."
			};
			int localY = y + gp.tileSize;
			for (String line : instructions) {
				int sx = gp.tileSize; // left margin
				g2.drawString(line, sx, localY);
				localY += gp.tileSize / 1.5; // vertical spacing
			}

			g2.drawString("ESCAPE to go back.", gp.tileSize, gp.screenHeight - gp.tileSize);
		}
        
        else if (titleScreenState == 6) { // SAVE screen

            g2.setColor(new Color(0,0,0,200));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            String text = "Save Game - Select Slot";
            int x = gp.screenWidth / 2 - gp.tileSize * 2;
            int y = gp.tileSize;

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
            
            String[] saves = new String[3];

            saves[0] = "Save Slot 1 " + (save1.fileExists() ? "(exists)" : "(empty)");
            saves[1] = "Save Slot 2 " + (save2.fileExists() ? "(exists)" : "(empty)");
            saves[2] = "Save Slot 3 " + (save3.fileExists() ? "(exists)" : "(empty)");
 
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

            // Back (3)
            int backY = y + gp.tileSize + 3 * gp.tileSize;
            if (commandNum == 3) g2.setColor(Color.yellow);
            else g2.setColor(Color.white);

            g2.drawString("Back", x, backY);
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
    
    public void drawStaminaBar() {
        int x =  gp.screenWidth - gp.tileSize * 3 - 40;
        int y =  gp.tileSize + gp.tileSize + 15;
        int width =  gp.tileSize * 3;
        int height = 10;

        // background
        g2.setColor(new Color(0,0,0,160));
        g2.fillRoundRect(x-4, y-4, width+8, height+8, 8, 8);

        // border
        g2.setColor(Color.white);
        g2.drawRoundRect(x-4, y-4, width+8, height+8, 8, 8);

        // fill
        float ratio = 0f;
        if (gp != null && gp.player != null && gp.player.maxStamina > 0f) {
            ratio = gp.player.stamina / gp.player.maxStamina;
            if (ratio < 0f) ratio = 0f;
            if (ratio > 1f) ratio = 1f;
        }
        int innerWidth = (int) (width * ratio);

        // color: green -> yellow -> red by ratio
        if (ratio > 0.6f) {
            g2.setColor(new Color(80,200,120));
        } else if (ratio > 0.25f) {
            g2.setColor(new Color(240,200,80));
        } else {
            g2.setColor(new Color(220,80,80));
        }

        g2.fillRoundRect(x, y, innerWidth, height, 6, 6);
    }
    
    public void drawTasksList() {
        if (gp == null || gp.player == null) return;
        java.util.List<Task> list = gp.player.tasksList;
        if (list == null || list.isEmpty()) return;

        int panelX = gp.tileSize / 2;
        int panelY = gp.tileSize / 2;
        int padding = 10;          // inside padding
        int gap = 12;              // space between tasks
        int headerGap = 22;        // extra space after "Tasks"
        int bulletSize = 12;
        int titleGap = 16;         // space between bullet and title
        int maxPanelWidth = Math.min(gp.screenWidth / 2, gp.tileSize * 5); // bigger box

        // Fonts
        Font headerFont = g2.getFont().deriveFont(Font.BOLD, 18f);
        Font titleFont  = g2.getFont().deriveFont(Font.BOLD, 14f);
        Font descFont   = g2.getFont().deriveFont(Font.PLAIN, 12f);

        // compute inner width used for wrapping (reserve space for bullet+title offset)
        int innerWidth = maxPanelWidth - padding * 2;

        // Prepare wrapped desc for each task and compute panel height
        java.util.List<java.util.List<String>> wrapped = new java.util.ArrayList<>();
        int panelHeight = padding; // start with top padding

        g2.setFont(headerFont);
        FontMetrics fmHeader = g2.getFontMetrics();
        panelHeight += fmHeader.getHeight() + headerGap; // header height + gap

        for (Task t : list) {
            String title = t.getName();
            g2.setFont(titleFont);
            FontMetrics fmTitle = g2.getFontMetrics();

            g2.setFont(descFont);
            FontMetrics fmDesc = g2.getFontMetrics();

            // give available width for description lines (title area uses some width but desc can start under title)
            int descMaxWidth = innerWidth - (bulletSize + titleGap);
            if (descMaxWidth < 40) descMaxWidth = innerWidth; // fallback

            java.util.List<String> lines = wrapText(t.getDescription(), fmDesc, descMaxWidth);
            wrapped.add(lines);

            panelHeight += fmTitle.getHeight();            // title
            panelHeight += lines.size() * fmDesc.getHeight(); // desc lines
            panelHeight += gap;                            // gap after each task
        }

        panelHeight += padding; // bottom padding

        // DRAW PANEL
        int panelWidth = maxPanelWidth;
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 12, 12);
        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 12, 12);

        // DRAW HEADER
        int cursorX = panelX + padding;
        int cursorY = panelY + padding;
        g2.setFont(headerFont);
        g2.setColor(Color.white);
        cursorY += g2.getFontMetrics().getAscent();
        g2.drawString("Tasks", cursorX, cursorY);

        // move down for tasks
        cursorY += headerGap;

        // DRAW TASKS
        g2.setFont(titleFont);
        for (int i = 0; i < list.size(); i++) {
            Task t = list.get(i);
            boolean complete = t.isCompleted();
            java.util.List<String> lines = wrapped.get(i);

            // bullet
            int bulletX = cursorX;
            int bulletY = cursorY - g2.getFontMetrics(titleFont).getAscent()/2;
            g2.setColor(complete ? Color.green : Color.darkGray);
            g2.fillOval(bulletX, bulletY, bulletSize, bulletSize);

            // title
            int titleX = bulletX + bulletSize + titleGap;
            g2.setFont(titleFont);
            g2.setColor(complete ? Color.green : Color.white);
            g2.drawString(t.getName(), titleX, cursorY);

            // description lines (start under title)
            g2.setFont(descFont);
            g2.setColor(new Color(200,200,200));
            int descY = cursorY + g2.getFontMetrics(descFont).getAscent() + 4;
            for (String line : lines) {
                g2.drawString(line, titleX, descY);
                descY += g2.getFontMetrics(descFont).getHeight();
            }

            // advance cursor for next task
            cursorY = descY + gap;
            g2.setFont(titleFont);
        }
    }

    // very small wrap function that fits text into maxWidth using FontMetrics
    private java.util.List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) return out;
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String candidate = (line.length() == 0) ? w : line + " " + w;
            if (fm.stringWidth(candidate) <= maxWidth) {
                if (line.length() == 0) line.append(w); else line.append(" ").append(w);
            } else {
                if (line.length() > 0) { out.add(line.toString()); line.setLength(0); }
                if (fm.stringWidth(w) > maxWidth) out.add(w); else line.append(w);
            }
        }
        if (line.length() > 0) out.add(line.toString());
        return out;
    }
    

    public void drawTaskScreen() {
		int x = gp.tileSize * 1; // left padding
		int y = gp.tileSize * 1; // top padding
		int width = gp.screenWidth - (gp.tileSize * 2); // width of the task box
		int height = gp.screenHeight - (gp.tileSize * 2); // height of the task box

		// Draw panel background and border
		Color c = new Color(0, 0, 0); // semi-transparent black
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 35, 35); // filled rounded rect
		c = new Color(255, 255, 255); // white for border
		g2.setColor(c);
		g2.setStroke(new java.awt.BasicStroke(5)); // thicker stroke for border
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25); // draw border inside
		

		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // task font
		x += gp.tileSize; // inner padding
		y += gp.tileSize; // inner padding
		
	}
    
 // ------------------------------------- TILE SELECT TASK SCREEN -------------------------------------
    public void drawTileSelectTask() {

		// overlay
		g2.setColor(new Color(0, 0, 0, 160));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

		// panel
		int panelW = gp.tileSize * 10;
		int panelH = gp.tileSize * 8;
		int panelX = (gp.screenWidth - panelW) / 2;
		int panelY = (gp.screenHeight - panelH) / 2;

		// shadow
		int shadowOffset = gp.tileSize / 10;
		g2.setColor(new Color(0, 0, 0, 120));
		g2.fillRoundRect(panelX + shadowOffset, panelY + shadowOffset, panelW, panelH, 24, 24);

		// main panel
		g2.setColor(new Color(30, 30, 30, 220));
		g2.fillRoundRect(panelX, panelY, panelW, panelH, 24, 24);
		g2.setColor(new Color(255, 255, 255, 70));
		g2.setStroke(new BasicStroke(2f));
		g2.drawRoundRect(panelX, panelY, panelW, panelH, 24, 24);

		// title
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 42f));
		g2.setColor(Color.white);
		g2.drawString("Tile Select", panelX + gp.tileSize / 2, panelY + gp.tileSize);

		// grid placement
		tsCellSize = gp.tileSize;
		int gridPixel = TS_GRID * tsCellSize;
		tsGridX = gp.screenWidth / 2 - gridPixel / 2;
		tsGridY = panelY + gp.tileSize * 2;

		// cooldow
		if (taskCooldownFrames > 0) {

			int s = (taskCooldownFrames + 59) / 60;

			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f));
			String t = "Tasks locked. Try again in " + s + "s";
			int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(t) / 2;
			int y = gp.screenHeight / 2;

			g2.setColor(Color.lightGray);
			g2.drawString(t, x, y);

			gp.mouseClicked = false;
			gp.keyH.enterPressed = false;
			gp.keyH.typedChar = 0;
			gp.keyH.backspacePressed = false;

			//escape handler already handles escape
			return;
		}
		if (!tileSelectGenerated) {

			// clear grids
			for (int r = 0; r < TS_GRID; r++) {
				for (int c = 0; c < TS_GRID; c++) {
					tsPattern[r][c] = false;
					tsSelected[r][c] = false;
				}
			}

			// pick 6 random tiles
			int placed = 0;
			while (placed < TS_FLASH_COUNT) {
				int r = (int) (Math.random() * TS_GRID);
				int c = (int) (Math.random() * TS_GRID);
				if (!tsPattern[r][c]) {
					tsPattern[r][c] = true;
					placed++;
				}
			}

			tsPhase = 0; // 0=flash, 1=blank, 2=input, 3=feedback
			tsTimer = tsFlashFrames;
			tsResult = false;

			tileSelectGenerated = true;
		}

		//  phase timing
		if (tsPhase == 0) { // flash
			tsTimer--;
			if (tsTimer <= 0) {
				tsPhase = 1;
				tsTimer = tsBlankFrames;
			}
		} else if (tsPhase == 1) { // blank pause
			tsTimer--;
			if (tsTimer <= 0) {
				tsPhase = 2; // input
			}
		}

		//instructions
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
		g2.setColor(new Color(220, 220, 220));

		String instr = "";
		if (tsPhase == 0) instr = "Memorize the 6 tiles...";
		else if (tsPhase == 1) instr = "Wait...";
		else if (tsPhase == 2) instr = "Click the 6 tiles, then press ENTER to submit.";
		else instr = "Press ENTER to continue";

		g2.drawString(instr, panelX + gp.tileSize / 2, panelY + gp.tileSize + 40);

		// draw grid
		for (int r = 0; r < TS_GRID; r++) {
			for (int c = 0; c < TS_GRID; c++) {

				int x = tsGridX + c * tsCellSize;
				int y = tsGridY + r * tsCellSize;

				// base tile
				g2.setColor(new Color(60, 60, 60, 220));
				g2.fillRoundRect(x, y, tsCellSize, tsCellSize, 8, 8);

				// show flash tiles only during phase 0
				if (tsPhase == 0 && tsPattern[r][c]) {
					g2.setColor(new Color(240, 220, 80, 230));
					g2.fillRoundRect(x, y, tsCellSize, tsCellSize, 8, 8);
				}

				// show selected tiles during input
				if (tsPhase == 2 && tsSelected[r][c]) {
					g2.setColor(new Color(120, 220, 140, 220));
					g2.fillRoundRect(x, y, tsCellSize, tsCellSize, 8, 8);
				}

				// outline
				g2.setColor(new Color(255, 255, 255, 90));
				g2.drawRoundRect(x, y, tsCellSize, tsCellSize, 8, 8);
			}
		}

		// input phase
		if (tsPhase == 2) {

			// handle mouse 
			if (gp.mouseClicked) {
				gp.mouseClicked = false; // consume click

				int mx = gp.mouseX;
				int my = gp.mouseY;

				// inside grid
				if (mx >= tsGridX && mx < tsGridX + TS_GRID * tsCellSize &&
					my >= tsGridY && my < tsGridY + TS_GRID * tsCellSize) {

					int col = (mx - tsGridX) / tsCellSize;
					int row = (my - tsGridY) / tsCellSize;

					// toggle selection
					tsSelected[row][col] = !tsSelected[row][col];}
			}

			// count selected
			int selectedCount = 0;
			for (int r = 0; r < TS_GRID; r++) {
				for (int c = 0; c < TS_GRID; c++) {
					if (tsSelected[r][c]) selectedCount++;
				}
			}

			// show selected count
			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16f));
			g2.setColor(new Color(200, 200, 200, 170));
			String countText = "Selected: " + selectedCount + " / " + TS_FLASH_COUNT;
			int cx = panelX + gp.tileSize / 2;
			int cy = panelY + panelH - gp.tileSize / 2;
			g2.drawString(countText, cx, cy);

			// ENTER submits only when 6 selected 
			if (selectedCount == TS_FLASH_COUNT && gp.keyH.enterPressed) {
				gp.keyH.enterPressed = false;

				boolean ok = true;
				for (int r = 0; r < TS_GRID; r++) {
					for (int c = 0; c < TS_GRID; c++) {
						if (tsSelected[r][c] != tsPattern[r][c]) {
							ok = false;
							break;
						}
					}
					if (!ok) break;
				}

				tsResult = ok;
				tsPhase = 3; // feedback
			}

			// eat enter if pressed early 
			if (gp.keyH.enterPressed && selectedCount != TS_FLASH_COUNT) {
				gp.keyH.enterPressed = false;}
		}

		//feedback pase
		if (tsPhase == 3) {

			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36f));
			String msg = tsResult ? "Correct!" : "Incorrect!";
			g2.setColor(tsResult ? new Color(120, 220, 140) : new Color(240, 120, 120));

			int mx = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(msg) / 2;
			int my = panelY + panelH - gp.tileSize;
			g2.drawString(msg, mx, my);

			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
			g2.setColor(new Color(220, 220, 220));
			String hint = "Press ENTER to continue";
			int hx = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(hint) / 2;
			g2.drawString(hint, hx, my + 30);

			if (gp.keyH.enterPressed) {
				gp.keyH.enterPressed = false;

				// reset generator so next entry generates a new pattern)
				tileSelectGenerated = false;

				if (tsResult) {
					handleTaskSuccess("Task Completed!");
				} else {
					handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
							"Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
				}
			}
		}
	}
	
    
    // ------------------------ DRAW MATH TASK ------------------------
    public void drawMathTask() {

        // Rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background
        Color overlay = new Color(0, 0, 0, 160);
        g2.setColor(overlay);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Panel layout
        int panelW = gp.tileSize * 10;
        int panelH = gp.tileSize * 7;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;
        int arc = 28;

        // drop shadow
        int shadowOffset = gp.tileSize / 8;
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(panelX + shadowOffset, panelY + shadowOffset, panelW, panelH, arc, arc);

        // panel gradient background
        GradientPaint gpBack = new GradientPaint(panelX, panelY,
                new Color(60, 63, 65), panelX, panelY + panelH,
                new Color(42, 45, 48));
        g2.setPaint(gpBack);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // inner padding and separator line
        int pad = gp.tileSize / 3;
        int innerX = panelX + pad;
        int innerY = panelY + pad;
        int innerW = panelW - pad * 2;

        // Title with subtle shadow
        String title = "Math Task";
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.9f);
        g2.setFont(titleFont);
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawString(title, innerX + 3, innerY + (int)(gp.tileSize * 0.9f) + 3); // shadow
        g2.setColor(new Color(230, 230, 230));
        g2.drawString(title, innerX, innerY + (int)(gp.tileSize * 0.9f));

        // Level badge (top-right)
        String lvl = "Level " + gp.level;
        Font badgeFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f);
        int badgeW = gp.tileSize * 3;
        int badgeH = gp.tileSize / 2;
        int badgeX = panelX + panelW - pad - badgeW;
        int badgeY = innerY - gp.tileSize/6;
        g2.setColor(new Color(255, 200, 60));
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 12, 12);
        g2.setColor(Color.BLACK);
        g2.setFont(badgeFont);
        FontMetrics fmBadge = g2.getFontMetrics();
        int bx = badgeX + (badgeW - fmBadge.stringWidth(lvl)) / 2;
        int by = badgeY + ((badgeH - fmBadge.getHeight()) / 2) + fmBadge.getAscent();
        g2.drawString(lvl, bx, by);

        // instruction under title
        Font instrFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
        g2.setFont(instrFont);
        g2.setColor(new Color(200, 200, 200));
        String instr = "Solve the equation below (give INTEGER answer)";
        g2.drawString(instr, innerX, innerY + (int)(gp.tileSize * 1.6f));

        // divider
        int dividerY = innerY + (int)(gp.tileSize * 1.9f);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawLine(innerX, dividerY, innerX + innerW, dividerY);

        // ----- GLOBAL COOLDOWN: block input if active -----
        if (taskCooldownFrames > 0) {
            Font big = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
            g2.setFont(big);
            g2.setColor(new Color(180, 180, 180));
            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
            g2.drawString(locked, lx, ly);

            // clear any input flags while locked so nothing sneaks through
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
            gp.keyH.escapePressed = false;
            return;
        }

        // Initialize per-task timer / counters on first entry
        if (questionsAsked == 0 && taskTimerFrames == 0 && !taskGenerated) {
            switch (gp.level) {
                case 1 -> taskTimeLimitFrames = 30 * 60; // 30s
                case 2 -> taskTimeLimitFrames = 45 * 60; // 45s
                case 3 -> taskTimeLimitFrames = 60 * 60; // 60s
                default  -> taskTimeLimitFrames = 75 * 60; // 75s
            }
            taskTimerFrames = taskTimeLimitFrames;
            correctCount = 0;
            wrongCount = 0;
            questionsAsked = 0;
        }

        // Escape to exit immediately
        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return;
        }

        // Countdown the timer (only while not viewing feedback)
        if (taskTimerFrames > 0 && !answerChecked) taskTimerFrames--;
        if (taskTimerFrames <= 0 && !answerChecked) {
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
            return;
        }

        // GENERATE QUESTION (once)
        if (!taskGenerated) {
            String[] ops = {"+", "-", "*", "/"};
            int n1 = (int)(Math.random()*10)+1;
            int n2 = (int)(Math.random()*10)+1;
            int n3 = (int)(Math.random()*10)+1;
            int n4 = (int)(Math.random()*10)+1;
            int n5 = (int)(Math.random()*10)+1;
            String op1 = ops[(int)(Math.random()*ops.length)];
            String op2 = ops[(int)(Math.random()*ops.length)];
            int temp = 0, result = 0;

            // Level-specific logic (ensures integer division)
            if (gp.level == 1) {
                if (op1.equals("/")) {
                    int q = (int)(Math.random()*9)+1;
                    n2 = (int)(Math.random()*9)+1;
                    n1 = n2 * q;
                }
                question = n1 + " " + op1 + " " + n2;
                switch (op1) {
                    case "+" -> result = n1 + n2;
                    case "-" -> result = n1 - n2;
                    case "*" -> result = n1 * n2;
                    default  -> result = n1 / n2;
                }
            } else if (gp.level == 2) {
                if (op1.equals("/")) {
                    int q = (int)(Math.random()*9)+1;
                    n2 = (int)(Math.random()*9)+1;
                    n1 = n2 * q;
                }
                switch (op1) {
                    case "+" -> temp = n1 + n2;
                    case "-" -> temp = n1 - n2;
                    case "*" -> temp = n1 * n2;
                    default  -> temp = n1 / n2;
                }
                if (op2.equals("/")) {
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n3 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n3 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                question = n1 + " " + op1 + " " + n2 + " " + op2 + " " + n3;
                switch (op2) {
                    case "+" -> result = temp + n3;
                    case "-" -> result = temp - n3;
                    case "*" -> result = temp * n3;
                    default  -> result = temp / n3;
                }
            } else if (gp.level == 3) {
                if (op1.equals("/")) {
                    int q = (int)(Math.random()*9)+1;
                    n2 = (int)(Math.random()*9)+1;
                    n1 = n2 * q;
                }
                switch (op1) {
                    case "+" -> temp = n1 + n2;
                    case "-" -> temp = n1 - n2;
                    case "*" -> temp = n1 * n2;
                    default  -> temp = n1 / n2;
                }
                if (op2.equals("/")) {
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n3 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n3 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                switch (op2) {
                    case "+" -> temp = temp + n3;
                    case "-" -> temp = temp - n3;
                    case "*" -> temp = temp * n3;
                    default  -> temp = temp / n3;
                }
                if (op1.equals("/")) {
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n4 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n4 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                question = n1 + " " + op1 + " " + n2 + " " + op2 + " " + n3 + " " + op1 + " " + n4;
                switch (op1) {
                    case "+" -> result = temp + n4;
                    case "-" -> result = temp - n4;
                    case "*" -> result = temp * n4;
                    default  -> result = temp / n4;
                }
            } else {
                if (op1.equals("/")) {
                    int q = (int)(Math.random()*9)+1;
                    n2 = (int)(Math.random()*9)+1;
                    n1 = n2 * q;
                }
                switch (op1) {
                    case "+" -> temp = n1 + n2;
                    case "-" -> temp = n1 - n2;
                    case "*" -> temp = n1 * n2;
                    default  -> temp = n1 / n2;
                }
                if (op2.equals("/")) {
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n3 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n3 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                switch (op2) {
                    case "+" -> temp = temp + n3;
                    case "-" -> temp = temp - n3;
                    case "*" -> temp = temp * n3;
                    default  -> temp = temp / n3;
                }
                if (op1.equals("/")) {
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n4 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n4 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                switch (op1) {
                    case "+" -> temp = temp + n4;
                    case "-" -> temp = temp - n4;
                    case "*" -> temp = temp * n4;
                    default  -> temp = temp / n4;
                }
                if (op2.equals("/")) {
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n5 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n5 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                question = n1 + " " + op1 + " " + n2 + " " + op2 + " " + n3 + " " + op1 + " " + n4 + " " + op2 + " " + n5;
                switch (op2) {
                    case "+" -> result = temp + n5;
                    case "-" -> result = temp - n5;
                    case "*" -> result = temp * n5;
                    default  -> result = temp / n5;
                }
            }

            correctAnswer = result;
            taskGenerated = true;
            playerInput = "";
            answerChecked = false;
        }

        // HANDLE INPUT (blocked earlier if taskCooldownFrames > 0)
        if (!answerChecked) {

            // typed char for numbers / minus
            if (Character.isDigit(gp.keyH.typedChar) || gp.keyH.typedChar == '-') {
                playerInput += gp.keyH.typedChar;
                gp.keyH.typedChar = 0;
            }

            // backspace
            if (gp.keyH.backspacePressed && playerInput.length() > 0) {
                playerInput = playerInput.substring(0, playerInput.length() - 1);
                gp.keyH.backspacePressed = false;
            }

            // ENTER submits
            if (gp.keyH.enterPressed) {
                try {
                    int answer = Integer.parseInt(playerInput);
                    answerCorrect = (answer == correctAnswer);
                } catch (Exception e) {
                    answerCorrect = false;
                }
                answerChecked = true;
                gp.keyH.enterPressed = false;
            }
        } else {
            // after feedback, ENTER continues
            if (gp.keyH.enterPressed) {
                // register the result into counters
                questionsAsked++;
                if (answerCorrect) correctCount++;
                else wrongCount++;

                gp.keyH.enterPressed = false;

                // fail if 2+ wrong
                if (wrongCount >= 2) {
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
                    return;
                }

                // success if 3 correct
                if (correctCount >= 3) {
                    handleTaskSuccess("Task Completed!");
                    return;
                }

                // if 3 questions asked -> evaluate
                if (questionsAsked >= 3) {
                    if (correctCount >= 3) handleTaskSuccess("Task Completed!");
                    else handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
                    return;
                }

                // else prepare next question
                taskGenerated = false;
                playerInput = "";
                answerChecked = false;
                answerCorrect = false;
            }
        }

        // DRAW TIMER (top-right)
        int secondsLeft = (taskTimerFrames + 59) / 60;
        String timeText = String.format("Time: %d s", secondsLeft);
        g2.setFont(instrFont);
        g2.setColor(new Color(220,220,220));
        FontMetrics tfm = g2.getFontMetrics();
        int tx = panelX + panelW - pad - tfm.stringWidth(timeText);
        int ty = innerY + (int)(gp.tileSize * 0.9f);
        g2.drawString(timeText, tx, ty);

        // TIMER BAR under the time text (match text width)
        int textWidth  = tfm.stringWidth(timeText);
        int textHeight = tfm.getHeight();
        int barW = textWidth;
        int barH = Math.max(6, textHeight / 5);
        int barX = tx;
        int barY = ty + 6; // small gap
        float ratio = (float) taskTimerFrames / (float) taskTimeLimitFrames;
        ratio = Math.max(0f, Math.min(1f, ratio));
        g2.setColor(new Color(0, 0, 0, 130));
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);
        Color col =
                ratio > 0.6f ? new Color(120, 220, 140) :
                ratio > 0.25f ? new Color(240, 200, 80) :
                                new Color(240, 120, 120);
        int fillW = Math.max(2, (int)(barW * ratio));
        g2.setColor(col);
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);
        g2.setColor(new Color(255, 255, 255, 70));
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH);

        // QUESTION DISPLAY (centered)
        Font qFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 1.0f);
        g2.setFont(qFont);
        g2.setColor(new Color(245, 245, 245));
        FontMetrics qfm = g2.getFontMetrics();
        String qText = "What is " + question + " ?";
        int qx = panelX + (panelW - qfm.stringWidth(qText)) / 2;
        int qy = dividerY + (int)(gp.tileSize * 1.4f);
        g2.drawString(qText, qx, qy);

        // INPUT BOX (rounded, centered)
        int boxW = innerW - gp.tileSize;
        int boxH = gp.tileSize;
        int boxX = panelX + (panelW - boxW) / 2;
        int boxY = qy + gp.tileSize / 2;

        // box background
        g2.setColor(new Color(30, 33, 36, 200));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 14, 14);

        // box border
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(120, 120, 120, 120));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 14, 14);

        // input text
        Font inputFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.7f);
        g2.setFont(inputFont);
        g2.setColor(new Color(230, 230, 230));
        FontMetrics ifm = g2.getFontMetrics();
        String displayInput = playerInput.length() > 0 ? playerInput : "";
        int ttx = boxX + gp.tileSize / 3;
        int tty = boxY + ((boxH - ifm.getHeight()) / 2) + ifm.getAscent();
        g2.drawString(displayInput, ttx, tty);

        // blinking caret
        if (!answerChecked) {
            boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
            if (blink) {
                int caretX = ttx + ifm.stringWidth(displayInput);
                int caretY1 = boxY + (boxH / 6);
                int caretY2 = boxY + boxH - (boxH / 6);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(caretX, caretY1, caretX, caretY2);
            }
        }

        // RESULT / FEEDBACK
        int feedbackY = boxY + boxH + gp.tileSize / 2;
        Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.6f);
        g2.setFont(feedbackFont);

        if (answerChecked) {
            String msg = answerCorrect ? "Correct!" : "Wrong! Answer: " + correctAnswer;
            g2.setColor(answerCorrect ? new Color(120, 220, 140) : new Color(240, 120, 120));
            FontMetrics ffm = g2.getFontMetrics();
            int fx = panelX + (panelW - ffm.stringWidth(msg)) / 2;
            int fy = feedbackY + ffm.getAscent();
            g2.drawString(msg, fx, fy);

            // continue hint
            String hint = "Press ENTER to continue";
            g2.setFont(instrFont);
            g2.setColor(new Color(200, 200, 200, 180));
            FontMetrics hfm = g2.getFontMetrics();
            int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2;
            g2.drawString(hint, hx, fy + gp.tileSize / 2);

        } else {
            // small helper hint
            String hint = "Type numbers, use BACKSPACE, press ENTER to submit";
            g2.setFont(instrFont);
            g2.setColor(new Color(200, 200, 200, 160));
            FontMetrics hfm = g2.getFontMetrics();
            int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2;
            g2.drawString(hint, hx, feedbackY + hfm.getAscent());
        }

        // draw progress (x/3 and wrongs)
        String prog = String.format("Progress: %d / 3   Wrong: %d", correctCount + wrongCount, wrongCount);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.20f));
        g2.setColor(new Color(200,200,200,180));
        g2.drawString(prog, innerX, panelY + panelH - pad - gp.tileSize/6);
    }
    
    // ------------------------ DRAW Button Match  TASK ------------------------
    public void drawButtonMatchTask() {

        // If task is on cooldown, show message and block attempts
        if (taskCooldownFrames > 0) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f));
            g2.setColor(Color.white);
            g2.drawString("Task is cooling down...", gp.tileSize * 4, gp.tileSize * 4);
            return;
        }

        // Generate the task once
        if (!buttonMatchGenerated) {
            buttonMatchGenerated = true;
            buttonMatchResolved = false;

            // random target time between 1.0s and 3.0s
            buttonMatchTargetSeconds = 1.0 + (Math.random() * 2.0);
            buttonMatchStartNano = System.nanoTime();

            buttonMatchFeedback = "";
            buttonMatchFeedbackFrames = 0;
        }

        // Draw background panel (simple)
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize * 2;
        int frameW = gp.screenWidth - gp.tileSize * 4;
        int frameH = gp.screenHeight - gp.tileSize * 4;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 20, 20);

        // Title + instructions
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 34f));
        g2.setColor(Color.white);
        g2.drawString("Button Match Task", frameX + gp.tileSize, frameY + gp.tileSize);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
        g2.drawString("Click the button when the timer hits the target window.", frameX + gp.tileSize, frameY + gp.tileSize * 2);

        // Compute elapsed time
        double elapsed = (System.nanoTime() - buttonMatchStartNano) / 1_000_000_000.0;

        // Draw timer info
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
        g2.drawString("Time: " + String.format("%.2f", elapsed) + "s", frameX + gp.tileSize, frameY + gp.tileSize * 3);
        g2.drawString("Target: " + String.format("%.2f", buttonMatchTargetSeconds) + "s", frameX + gp.tileSize, frameY + gp.tileSize * 4);
        g2.drawString("Window: +/- " + String.format("%.2f", buttonMatchWindow) + "s", frameX + gp.tileSize, frameY + gp.tileSize * 5);

        // Button rectangle (centered)
        int btnW = gp.tileSize * 6;
        int btnH = gp.tileSize * 2;
        int btnX = frameX + (frameW / 2) - (btnW / 2);
        int btnY = frameY + (frameH / 2) - (btnH / 2);

        buttonMatchButtonRect.setBounds(btnX, btnY, btnW, btnH);

        // Draw button
        g2.setColor(Color.darkGray);
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 20, 20);

        g2.setColor(Color.white);
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 20, 20);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26f));
        g2.drawString("CLICK", btnX + gp.tileSize * 2, btnY + gp.tileSize + 10);

        // If already resolved, just show feedback for a bit then exit task
        if (buttonMatchResolved) {
            if (buttonMatchFeedbackFrames > 0) buttonMatchFeedbackFrames--;

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f));
            g2.setColor(Color.white);
            g2.drawString(buttonMatchFeedback, frameX + gp.tileSize, frameY + frameH - gp.tileSize * 2);

            // After feedback ends, leave task screen
            if (buttonMatchFeedbackFrames == 0) {
                resetAllTaskState();
                gp.gameState = gp.playState;
            }
            return;
        }

        // Detect click (same style as your other "click-based" UI features)
        // IMPORTANT: this assumes you have gp.mouseClicked + gp.mouseX + gp.mouseY
        if (gp.mouseClicked) {
            gp.mouseClicked = false;

            boolean clickedButton = buttonMatchButtonRect.contains(gp.mouseX, gp.mouseY);

            if (clickedButton) {
                double diff = Math.abs(elapsed - buttonMatchTargetSeconds);

                if (diff <= buttonMatchWindow) {
                    buttonMatchFeedback = "SUCCESS!";
                    
                } else {
                    buttonMatchFeedback = "FAILED (too early/late)";
                    taskCooldownFrames = DEFAULT_TASK_COOLDOWN_SECONDS * 60;
                }

                buttonMatchResolved = true;
                buttonMatchFeedbackFrames = 60; // 1 second of feedback
            }
        }
    }
    
 // ------------------------ DRAW PATTERN SWITCHES TASK ------------------------
 	public void drawPatternSwitchTask() {

 		// Rendering
 		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

 		// Dim background
 		g2.setColor(new Color(0, 0, 0, 160));
 		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

 		// Panel layout
 		int panelW = gp.tileSize * 10;
 		int panelH = gp.tileSize * 7;
 		int panelX = (gp.screenWidth - panelW) / 2;
 		int panelY = (gp.screenHeight - panelH) / 2;
 		int arc = 28;

 		// drop shadow
 		int shadowOffset = gp.tileSize / 8;
 		g2.setColor(new Color(0, 0, 0, 120));
 		g2.fillRoundRect(panelX + shadowOffset, panelY + shadowOffset, panelW, panelH, arc, arc);

 		// panel gradient background
 		GradientPaint back = new GradientPaint(panelX, panelY, new Color(60, 63, 65), panelX, panelY + panelH,
 				new Color(42, 45, 48));
 		g2.setPaint(back);
 		g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

 		// inner padding
 		int pad = gp.tileSize / 3;
 		int innerX = panelX + pad;
 		int innerY = panelY + pad;
 		int innerW = panelW - pad * 2;

 		// Title with subtle shadow
 		String title = "Pattern Switches";
 		Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.9f);
 		g2.setFont(titleFont);
 		g2.setColor(new Color(0, 0, 0, 120));
 		g2.drawString(title, innerX + 3, innerY + (int) (gp.tileSize * 0.9f) + 3);
 		g2.setColor(new Color(230, 230, 230));
 		g2.drawString(title, innerX, innerY + (int) (gp.tileSize * 0.9f));

 		// Level badge
 		String lvl = "Level " + gp.level;
 		Font badgeFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f);
 		int badgeW = gp.tileSize * 3;
 		int badgeH = gp.tileSize / 2;
 		int badgeX = panelX + panelW - pad - badgeW;
 		int badgeY = innerY - gp.tileSize / 6;
 		g2.setColor(new Color(255, 200, 60));
 		g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 12, 12);
 		g2.setColor(Color.BLACK);
 		g2.setFont(badgeFont);
 		FontMetrics fmBadge = g2.getFontMetrics();
 		int bx = badgeX + (badgeW - fmBadge.stringWidth(lvl)) / 2;
 		int by = badgeY + ((badgeH - fmBadge.getHeight()) / 2) + fmBadge.getAscent();
 		g2.drawString(lvl, bx, by);

 		// instruction under title
 		Font instrFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
 		g2.setFont(instrFont);
 		g2.setColor(new Color(200, 200, 200));

 		String instr;
 		if (patternShowing) {
 			instr = "Memorize the flashes. Then repeat using keys 1, 2, 3, 4";
 		} else if (!patternChecked) {
 			instr = "Repeat the sequence: press 1, 2, 3, 4";
 		} else {
 			instr = "Press ENTER to continue";
 		}
 		g2.drawString(instr, innerX, innerY + (int) (gp.tileSize * 1.6f));

 		// divider
 		int dividerY = innerY + (int) (gp.tileSize * 1.9f);
 		g2.setStroke(new BasicStroke(1f));
 		g2.setColor(new Color(255, 255, 255, 30));
 		g2.drawLine(innerX, dividerY, innerX + innerW, dividerY);

 		// block input if active
 		if (taskCooldownFrames > 0) {
 			Font big = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
 			g2.setFont(big);
 			g2.setColor(new Color(180, 180, 180));
 			String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
 			int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
 			int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
 			g2.drawString(locked, lx, ly);

 			// clear any input while locked
 			gp.keyH.typedChar = 0;
 			gp.keyH.backspacePressed = false;
 			gp.keyH.enterPressed = false;
 			gp.keyH.escapePressed = false;
 			return;
 		}

 		// Escape to exit immediately
 		if (gp.keyH.escapePressed) {
 			gp.keyH.escapePressed = false;
 			resetAllTaskState();
 			gp.gameState = gp.playState;
 			return;
 		}

 		// Generate pattern once on entry
 		if (!patternGenerated) {

 			// difficulty scaling by level
 			if (gp.level <= 1) {
 				patternLength = 4;
 				patternInputLimitFrames = 5 * 60;
 			} else if (gp.level == 2) {
 				patternLength = 5;
 				patternInputLimitFrames = 5 * 60;
 			} else if (gp.level == 3) {
 				patternLength = 6;
 				patternInputLimitFrames = 6 * 60;
 			} else {
 				patternLength = 7;
 				patternInputLimitFrames = 6 * 60;
 			}

 			patternSequence = new int[patternLength];

 			// random 1..4
 			for (int i = 0; i < patternLength; i++) {
 				patternSequence[i] = (int) (Math.random() * 4) + 1;
 			}

 			// reset phase state
 			patternGenerated = true;
 			patternShowing = true;

 			patternIndex = 0;
 			patternFlashTimer = 0;
 			patternGapTimer = 0;

 			patternInputIndex = 0;
 			patternInputTimerFrames = 0;

 			patternChecked = false;
 			patternSuccess = false;

 			// clear any olinput
 			gp.keyH.typedChar = 0;
 			gp.keyH.backspacePressed = false;
 			gp.keyH.enterPressed = false;
 		}

 		// GRID LAYOUT
 		int gridSize = gp.tileSize * 4;
 		int gridX = panelX + (panelW - gridSize) / 2;
 		int gridY = dividerY + gp.tileSize / 2;

 		int btnSize = gp.tileSize * 2 - gp.tileSize / 4;
 		int gap = gp.tileSize / 4;

 		// 1=top-left, 2=top-right, 3=bottom-left, 4=bottom-right
 		int[] bxPos = new int[5];
 		int[] byPos = new int[5];
 		bxPos[1] = gridX;
 		byPos[1] = gridY;
 		bxPos[2] = gridX + btnSize + gap;
 		byPos[2] = gridY;
 		bxPos[3] = gridX;
 		byPos[3] = gridY + btnSize + gap;
 		bxPos[4] = gridX + btnSize + gap;
 		byPos[4] = gridY + btnSize + gap;

 		// Determine which button should be highlighted during show phase
 		int highlight = -1;

 		// SHOW PHASE
 		if (patternShowing) {

 			// start flash if idle
 			if (patternFlashTimer <= 0 && patternGapTimer <= 0) {
 				patternFlashTimer = patternFlashFrames;
 			}

 			// flashing
 			if (patternFlashTimer > 0) {
 				highlight = patternSequence[patternIndex];
 				patternFlashTimer--;
 				if (patternFlashTimer <= 0) {
 					patternGapTimer = patternGapFrames;
 				}
 			} else if (patternGapTimer > 0) {
 				patternGapTimer--;
 				if (patternGapTimer <= 0) {
 					patternIndex++;
 					if (patternIndex >= patternLength) {
 						// move to input phase
 						patternShowing = false;
 						patternIndex = 0;
 						patternInputIndex = 0;
 						patternInputTimerFrames = 0;
 					}
 				}
 			}
 		}

 		// INPUT PHASE
 		if (!patternShowing && !patternChecked) {

 			// input timer
 			patternInputTimerFrames++;
 			if (patternInputTimerFrames > patternInputLimitFrames) {
 				patternChecked = true;
 				patternSuccess = false;
 			}

 			// accept only 1 -> 4
 			char typed = gp.keyH.typedChar;
 			if (typed != 0) {

 				int pressed = -1;
 				if (typed == '1')
 					pressed = 1;
 				if (typed == '2')
 					pressed = 2;
 				if (typed == '3')
 					pressed = 3;
 				if (typed == '4')
 					pressed = 4;

 				// consume typed char no matter what
 				gp.keyH.typedChar = 0;

 				if (pressed != -1) {
 					if (pressed == patternSequence[patternInputIndex]) {
 						patternInputIndex++;
 						if (patternInputIndex >= patternLength) {
 							patternChecked = true;
 							patternSuccess = true;
 						}
 					} else {
 						patternChecked = true;
 						patternSuccess = false;
 					}
 				}
 			}
 		}

 		// DRAW BUTTONS
 		for (int i = 1; i <= 4; i++) {

 			// base
 			g2.setColor(new Color(30, 33, 36, 200));
 			g2.fillRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 16, 16);

 			// highlight fill (only during show phase)
 			if (patternShowing && highlight == i) {
 				g2.setColor(new Color(255, 220, 90, 80));
 				g2.fillRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 16, 16);
 			}

 			// border
 			g2.setStroke(new BasicStroke(2f));
 			g2.setColor(new Color(120, 120, 120, 120));
 			g2.drawRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 16, 16);

 			// strong border while highlighted
 			if (patternShowing && highlight == i) {
 				g2.setStroke(new BasicStroke(4f));
 				g2.setColor(new Color(255, 220, 90));
 				g2.drawRoundRect(bxPos[i] - 2, byPos[i] - 2, btnSize + 4, btnSize + 4, 18, 18);
 			}

 			// number label
 			Font numF = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.9f);
 			g2.setFont(numF);
 			g2.setColor(new Color(230, 230, 230, 220));
 			String n = String.valueOf(i);
 			FontMetrics nfm = g2.getFontMetrics();
 			int nx = bxPos[i] + (btnSize - nfm.stringWidth(n)) / 2;
 			int ny = byPos[i] + (btnSize - nfm.getHeight()) / 2 + nfm.getAscent();
 			g2.drawString(n, nx, ny);
 		}

 		// Timeduring input
 		if (!patternShowing && !patternChecked) {
 			Font smallF = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
 			g2.setFont(smallF);
 			g2.setColor(new Color(220, 220, 220));

 			int secLeft = (patternInputLimitFrames - patternInputTimerFrames + 59) / 60;
 			if (secLeft < 0)
 				secLeft = 0;

 			String timeText = "Time: " + secLeft + " s";
 			FontMetrics tfm = g2.getFontMetrics();
 			int tx = panelX + panelW - pad - tfm.stringWidth(timeText);
 			int ty = innerY + (int) (gp.tileSize * 0.9f);
 			g2.drawString(timeText, tx, ty);

 			// timer bar
 			int barW = tfm.stringWidth(timeText);
 			int barH = Math.max(6, tfm.getHeight() / 5);
 			int barX = tx;
 			int barY = ty + 6;

 			float ratio = 1f;
 			if (patternInputLimitFrames > 0)
 				ratio = 1f - ((float) patternInputTimerFrames / (float) patternInputLimitFrames);
 			if (ratio < 0f)
 				ratio = 0f;
 			if (ratio > 1f)
 				ratio = 1f;

 			g2.setColor(new Color(0, 0, 0, 130));
 			g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

 			Color col = ratio > 0.6f ? new Color(120, 220, 140)
 					: ratio > 0.25f ? new Color(240, 200, 80) : new Color(240, 120, 120);

 			int fillW = Math.max(2, (int) (barW * ratio));
 			g2.setColor(col);
 			g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);
 			g2.setColor(new Color(255, 255, 255, 70));
 			g2.drawRoundRect(barX, barY, barW, barH, barH, barH);
 		}

 		// PROGRESS 
 		int dotsY = gridY + gridSize + gp.tileSize / 4;
 		int dotSize = gp.tileSize / 5;
 		int dotGap = 6;
 		int totalDotsW = patternLength * dotSize + (patternLength - 1) * dotGap;
 		int dotsX = panelX + (panelW - totalDotsW) / 2;

 		for (int i = 0; i < patternLength; i++) {
 			boolean on = (!patternShowing && i < patternInputIndex);
 			g2.setColor(on ? new Color(120, 220, 140) : new Color(120, 120, 120, 140));
 			int dx = dotsX + i * (dotSize + dotGap);
 			g2.fillOval(dx, dotsY, dotSize, dotSize);
 		}
 		// FEEDBACK (when checked)
 		if (patternChecked) {

 			Font fbF = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
 			g2.setFont(fbF);

 			String fb = patternSuccess ? "✔ Correct" : "Incorrect. The task failed. Re-entry required.";
 			g2.setColor(patternSuccess ? new Color(120, 220, 140) : new Color(240, 120, 120));

 			int fbx = panelX + (panelW - g2.getFontMetrics().stringWidth(fb)) / 2;
 			int fby = panelY + panelH - gp.tileSize * 2;
 			g2.drawString(fb, fbx, fby);

 			Font hintF = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
 			g2.setFont(hintF);
 			g2.setColor(new Color(200, 200, 200, 180));

 			String hint = "Press ENTER to continue";
 			int hx = panelX + (panelW - g2.getFontMetrics().stringWidth(hint)) / 2;
 			g2.drawString(hint, hx, fby + gp.tileSize / 2);

 			// ENTER 
 			if (gp.keyH.enterPressed) {
 				gp.keyH.enterPressed = false;
 				if (patternSuccess) {
 					handleTaskSuccess("Task Completed!");
 				} else {
 					handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
 							"Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
 				}
 				return;
 			}

 		} else {
 			// helper hint
 			String hint = "Press 1,2,3,4 in order";
 			g2.setFont(instrFont);
 			g2.setColor(new Color(200, 200, 200, 160));
 			FontMetrics hfm = g2.getFontMetrics();
 			int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2;
 			g2.drawString(hint, hx, panelY + panelH - pad);
 		}
 	}
    
 // ------------------------ DRAW VAULT SEQUENCE TASK ------------------------
    public void drawVaultSequenceTask() {

        // Cooldown block
        if (taskCooldownFrames > 0) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f));
            g2.setColor(Color.white);
            g2.drawString("Task is cooling down...", gp.tileSize * 4, gp.tileSize * 4);

            // eat clicks so player can’t spam
            gp.mouseClicked = false;
            return;
        }

        // Rendering hints (same style as your other tasks)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Panel layout
        int panelW = gp.tileSize * 11;
        int panelH = gp.tileSize * 7;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;
        int arc = 26;

        // Shadow + background
        g2.setColor(new Color(0,0,0,120));
        g2.fillRoundRect(panelX+6, panelY+6, panelW, panelH, arc, arc);
        g2.setColor(new Color(30, 30, 36, 240));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // Title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f));
        g2.setColor(Color.white);
        g2.drawString("Vault Sequence", panelX + gp.tileSize, panelY + gp.tileSize);

        // Initialize task once
        if (!vaultGenerated) {
            vaultGenerated = true;
            vaultResolved = false;

            // sequence length by level (adjust if you want)
            vaultSeqLen = (gp.level <= 1) ? 3 :
                          (gp.level == 2) ? 4 :
                          (gp.level == 3) ? 5 : 6;

            vaultSequence = new int[vaultSeqLen];

            // generate random values 1..4
            for (int i = 0; i < vaultSeqLen; i++) {
                vaultSequence[i] = 1 + (int)(Math.random() * 4);
            }

            vaultProgressIndex = 0;
            vaultStrikes = 0;
            vaultFeedback = "";
            vaultFeedbackFrames = 0;

            // time limit (you can tweak)
            vaultTimeLimitFrames = (gp.level <= 1) ? 30 * 60 : 45 * 60;
            vaultTimerFrames = vaultTimeLimitFrames;

            // optional: pick a flavor riddle from your pool (reuses your existing arrays)
            int idx = (int)(Math.random() * RIDDLE_QUESTIONS.length);
            vaultFlavorRiddle = RIDDLE_QUESTIONS[idx];

            // setup rectangles array
            for (int i = 0; i < vaultButtonRects.length; i++) {
                if (vaultButtonRects[i] == null) vaultButtonRects[i] = new Rectangle();
            }
        }

        // Escape abort (your global escape handler already exists, but this keeps it consistent)
        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return;
        }

        // Timer tick
        if (vaultTimerFrames > 0 && !vaultResolved) vaultTimerFrames--;
        if (vaultTimerFrames <= 0 && !vaultResolved) {
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Vault failed. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
            return;
        }

        // Show small flavor line (uses your riddle pool)
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.28f));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("Hint: " + vaultFlavorRiddle, panelX + gp.tileSize, panelY + (int)(gp.tileSize * 1.6));

        // Show progress + strikes
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f));
        g2.setColor(Color.white);
        g2.drawString("Progress: " + vaultProgressIndex + " / " + vaultSeqLen, panelX + gp.tileSize, panelY + (int)(gp.tileSize * 2.4));
        g2.setColor(new Color(240, 120, 120));
        g2.drawString("Strikes: " + vaultStrikes + " / " + vaultMaxStrikes, panelX + gp.tileSize, panelY + (int)(gp.tileSize * 2.9));

        // Time top-right
        g2.setColor(new Color(220,220,220));
        String timeText = "Time: " + ((vaultTimerFrames + 59) / 60) + " s";
        int tW = g2.getFontMetrics().stringWidth(timeText);
        g2.drawString(timeText, panelX + panelW - gp.tileSize - tW, panelY + (int)(gp.tileSize * 1.0));

        // Draw 4 buttons (2x2 grid)
        int btnSize = gp.tileSize * 2;
        int gap = gp.tileSize / 2;

        int gridW = btnSize * 2 + gap;
        int gridH = btnSize * 2 + gap;

        int gridX = panelX + (panelW - gridW) / 2;
        int gridY = panelY + (int)(gp.tileSize * 3.2);

        // positions: 1 top-left, 2 top-right, 3 bottom-left, 4 bottom-right
        Rectangle r1 = vaultButtonRects[0];
        Rectangle r2 = vaultButtonRects[1];
        Rectangle r3 = vaultButtonRects[2];
        Rectangle r4 = vaultButtonRects[3];

        r1.setBounds(gridX, gridY, btnSize, btnSize);
        r2.setBounds(gridX + btnSize + gap, gridY, btnSize, btnSize);
        r3.setBounds(gridX, gridY + btnSize + gap, btnSize, btnSize);
        r4.setBounds(gridX + btnSize + gap, gridY + btnSize + gap, btnSize, btnSize);

        // draw helper
        drawVaultButton(r1, "1");
        drawVaultButton(r2, "2");
        drawVaultButton(r3, "3");
        drawVaultButton(r4, "4");

        // instruction
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f));
        g2.setColor(new Color(200,200,200));
        g2.drawString("Click the buttons in the correct order.", panelX + gp.tileSize, panelY + panelH - gp.tileSize);

        // If resolved, show feedback then finish
        if (vaultResolved) {
            if (vaultFeedbackFrames > 0) vaultFeedbackFrames--;

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.55f));
            g2.setColor(vaultStrikes >= vaultMaxStrikes ? new Color(240,120,120) : new Color(120,220,140));

            int fW = g2.getFontMetrics().stringWidth(vaultFeedback);
            g2.drawString(vaultFeedback, panelX + (panelW - fW)/2, panelY + (int)(gp.tileSize * 3.0));

            if (vaultFeedbackFrames == 0) {
                if (vaultStrikes >= vaultMaxStrikes) {
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Vault failed. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
                } else {
                    handleTaskSuccess("Vault unlocked!");
                }
            }
            return;
        }

        // Handle click input
        if (gp.mouseClicked) {
            gp.mouseClicked = false;

            int clicked = 0;
            if (r1.contains(gp.mouseX, gp.mouseY)) clicked = 1;
            else if (r2.contains(gp.mouseX, gp.mouseY)) clicked = 2;
            else if (r3.contains(gp.mouseX, gp.mouseY)) clicked = 3;
            else if (r4.contains(gp.mouseX, gp.mouseY)) clicked = 4;

            if (clicked != 0) {
                int expected = vaultSequence[vaultProgressIndex];

                if (clicked == expected) {
                    vaultProgressIndex++;

                    // completed whole sequence
                    if (vaultProgressIndex >= vaultSeqLen) {
                        vaultFeedback = "SUCCESS!";
                        vaultResolved = true;
                        vaultFeedbackFrames = 60;
                    }
                } else {
                    vaultStrikes++;

                    if (vaultStrikes >= vaultMaxStrikes) {
                        vaultFeedback = "FAILED!";
                        vaultResolved = true;
                        vaultFeedbackFrames = 60;
                    }
                }
            }
        }
    }

    // helper for drawing the vault buttons
    private void drawVaultButton(Rectangle r, String label) {
        g2.setColor(new Color(60, 60, 70));
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 18, 18);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 18, 18);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.6f));
        FontMetrics fm = g2.getFontMetrics();
        int lx = r.x + (r.width - fm.stringWidth(label)) / 2;
        int ly = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(label, lx, ly);
    }


    // ------------------------ DRAW RIDDLE TASK ------------------------
    public void drawRiddleTask() {

        // Rendering setup
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Panel layout
        int panelW = gp.tileSize * 10;
        int panelH = gp.tileSize * 6;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;
        int arc = 26;

        // Shadow + background
        g2.setColor(new Color(0,0,0,120));
        g2.fillRoundRect(panelX+6, panelY+6, panelW, panelH, arc, arc);
        GradientPaint back = new GradientPaint(panelX, panelY, new Color(245,235,210), panelX, panelY+panelH, new Color(230,210,170));
        g2.setPaint(back);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // parchment lines
        g2.setColor(new Color(0,0,0,10));
        for (int yy = panelY + 12; yy < panelY + panelH - 12; yy += 12) {
            g2.drawLine(panelX + 12, yy, panelX + panelW - 12, yy);
        }

        // emblem + title
        int emblemSize = gp.tileSize;
        int emblemX = panelX + gp.tileSize/2;
        int emblemY = panelY + gp.tileSize/2;
        g2.setColor(new Color(80,30,110));
        g2.fillOval(emblemX, emblemY, emblemSize, emblemSize);
        g2.setColor(new Color(255,235,200));
        Font emblemFont = g2.getFont().deriveFont(Font.BOLD, emblemSize * 0.75f);
        g2.setFont(emblemFont);
        FontMetrics efm = g2.getFontMetrics();
        String qMark = "?";
        int qmx = emblemX + (emblemSize - efm.stringWidth(qMark)) / 2;
        int qmy = emblemY + (emblemSize - efm.getHeight()) / 2 + efm.getAscent();
        g2.drawString(qMark, qmx, qmy);

        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.8f);
        g2.setFont(titleFont);
        g2.setColor(new Color(40,20,60));
        String title = "Riddle";
        int tx = emblemX + emblemSize + gp.tileSize/3;
        int ty = emblemY + emblemSize/2 + g2.getFontMetrics().getAscent()/2;
        g2.drawString(title, tx, ty);

        // cooldown block if active
        if (taskCooldownFrames > 0) {
            Font big = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
            g2.setFont(big);
            g2.setColor(new Color(120,120,120));
            String wait = "Riddle locked. Try again in " + ( (taskCooldownFrames + 59) / 60 ) + "s";
            int wx = panelX + (panelW - g2.getFontMetrics().stringWidth(wait)) / 2;
            int wy = panelY + panelH/2 + g2.getFontMetrics().getAscent()/2;
            g2.drawString(wait, wx, wy);

            riddleGenerated = false;
            // eat input
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
            gp.keyH.escapePressed = false;
            return;
        }

        // Initialize riddle if needed
        if (!riddleGenerated) {
            int idx = (int)(Math.random() * RIDDLE_QUESTIONS.length);
            riddleQuestion = RIDDLE_QUESTIONS[idx];
            riddleAnswer = RIDDLE_ANSWERS[idx];
            riddleGenerated = true;
            riddlePlayerInput = "";
            riddleAnswerChecked = false;
            riddleAnswerCorrect = false;
            riddleTimeLimitFrames = 45 * 60;
            riddleTimerFrames = riddleTimeLimitFrames;
        }

        // Escape abort
        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return;
        }

        // timer tick while not viewing feedback
        if (riddleTimerFrames > 0 && !riddleAnswerChecked) riddleTimerFrames--;
        if (riddleTimerFrames <= 0 && !riddleAnswerChecked) {
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Incorrect. The task failed. Re-entry required.");
            return;
        }

        // -----------------------------
        // QUESTION RENDERING (ROBUST)
        // -----------------------------
        // Define question area (top portion of panel)
        int questionAreaX = panelX + gp.tileSize / 2;
        int questionAreaY = panelY + gp.tileSize / 2;
        int questionAreaW = panelW - gp.tileSize;
        int questionAreaH = panelH / 2; // use top half for question

        // Attempt single-line with shrinking font first
        int maxQW = questionAreaW - gp.tileSize; // inner padding
        float fsize = gp.tileSize * 0.9f;
        float minSize = gp.tileSize * 0.35f;
        Font qF = g2.getFont().deriveFont(Font.PLAIN, fsize);
        FontMetrics qfm = g2.getFontMetrics(qF);
        int qW = qfm.stringWidth(riddleQuestion);

        while (qW > maxQW && fsize > minSize) {
            fsize -= 1.5f;
            qF = g2.getFont().deriveFont(Font.PLAIN, fsize);
            qfm = g2.getFontMetrics(qF);
            qW = qfm.stringWidth(riddleQuestion);
        }

        g2.setFont(qF);
        g2.setColor(new Color(30,20,50));

        if (qW <= maxQW) {
            // single-line fits: center it inside questionArea
            int drawX = questionAreaX + (questionAreaW - qW) / 2;
            int drawY = questionAreaY + (questionAreaH / 2) + (qfm.getAscent() / 2);
            g2.drawString(riddleQuestion, drawX, drawY);
        } else {
            // still too wide even at minSize -> wrap into multiple lines
            Font wrapFont = g2.getFont().deriveFont(Font.PLAIN, minSize);
            FontMetrics wfm = g2.getFontMetrics(wrapFont);
            java.util.List<String> wrapped = wrapText(riddleQuestion, wfm, maxQW);

            // compute total height and start Y to center vertically in questionArea
            int lineH = wfm.getHeight();
            int totalH = wrapped.size() * lineH;
            int startY = questionAreaY + (questionAreaH - totalH) / 2 + wfm.getAscent();

            g2.setFont(wrapFont);
            for (String line : wrapped) {
                int lw = wfm.stringWidth(line);
                int lx = questionAreaX + (questionAreaW - lw) / 2;
                g2.drawString(line, lx, startY);
                startY += lineH;
            }
        }

        // -----------------------------
        // label + input box
        // -----------------------------
        Font labelFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
        g2.setFont(labelFont);
        g2.setColor(new Color(70,60,40));
        String label = "Type your answer";
        int labelX = panelX + gp.tileSize;
        int labelY = panelY + panelH - gp.tileSize*3 + g2.getFontMetrics().getAscent();
        g2.drawString(label, labelX, labelY);

        int boxW = panelW - gp.tileSize*2;
        int boxH = gp.tileSize;
        int boxX = panelX + gp.tileSize;
        int boxY = panelY + panelH - gp.tileSize*2 - 8;
        g2.setColor(new Color(255,255,255,200));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(200,170,120));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // input display & caret
        Font inputFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.6f);
        g2.setFont(inputFont);
        g2.setColor(new Color(40,30,20));
        FontMetrics ifm = g2.getFontMetrics();
        String display = riddlePlayerInput.length() > 0 ? riddlePlayerInput : "";
        int itx = boxX + gp.tileSize/3;
        int ity = boxY + ((boxH - ifm.getHeight()) / 2) + ifm.getAscent();
        g2.drawString(display, itx, ity);

        if (!riddleAnswerChecked) {
            boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
            if (blink) {
                int caretX = itx + ifm.stringWidth(display);
                int cTop = boxY + boxH/6;
                int cBot = boxY + boxH - boxH/6;
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(caretX, cTop, caretX, cBot);
            }
        }

        // Input handling only when not checked
        if (!riddleAnswerChecked) {
            char typed = gp.keyH.typedChar;
            if (typed != 0) {
                if (Character.isLetterOrDigit(typed) || Character.isWhitespace(typed) || isPunctuation(typed)) {
                    riddlePlayerInput += typed;
                }
                gp.keyH.typedChar = 0;
            }
            if (gp.keyH.backspacePressed) {
                if (riddlePlayerInput.length() > 0) riddlePlayerInput = riddlePlayerInput.substring(0, riddlePlayerInput.length() - 1);
                gp.keyH.backspacePressed = false;
            }
            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;
                String user = riddlePlayerInput.trim();
                String correct = riddleAnswer.trim();
                riddleAnswerCorrect = user.equalsIgnoreCase(correct);
                riddleAnswerChecked = true;
            }
        } else {
            Font fbFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
            g2.setFont(fbFont);
            String fb = riddleAnswerCorrect ? "✔ Correct" : "Incorrect. The task failed. Re-entry required.";
            g2.setColor(riddleAnswerCorrect ? new Color(40,150,60) : new Color(200,60,60));
            int fbx = panelX + (panelW - g2.getFontMetrics().stringWidth(fb)) / 2;
            int fby = boxY - gp.tileSize/2;
            g2.drawString(fb, fbx, fby);

            if (riddleAnswerCorrect) {
                int alpha = 180;
                g2.setColor(new Color(120,220,140,alpha));
                int cx = boxX + boxW - gp.tileSize;
                int cy = boxY - gp.tileSize/2;
                g2.fillOval(cx, cy, gp.tileSize/2, gp.tileSize/2);
            }

            Font hintF = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
            g2.setFont(hintF);
            g2.setColor(new Color(60,50,40));
            String hint = riddleAnswerCorrect ? "Press ENTER to finish" : "Press ENTER to continue";
            int hx = panelX + (panelW - g2.getFontMetrics().stringWidth(hint)) / 2;
            g2.drawString(hint, hx, boxY + boxH + gp.tileSize/3);

            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;
                if (riddleAnswerCorrect) handleTaskSuccess("Riddle solved!");
                else handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Incorrect. The task failed. Re-entry required.");
                return;
            }
        }

        // Timer top-right + bar
        Font smallF = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
        g2.setFont(smallF);
        g2.setColor(new Color(60,50,40));
        String timeText = "Time: " + ((riddleTimerFrames + 59)/60) + " s";
        FontMetrics tfm = g2.getFontMetrics();
        int timeTX = panelX + panelW - gp.tileSize - tfm.stringWidth(timeText);
        int timeTY = panelY + gp.tileSize/2 + tfm.getAscent()/2;
        g2.drawString(timeText, timeTX, timeTY);

        int textWidth = tfm.stringWidth(timeText);
        int barH = Math.max(6, tfm.getHeight()/5);
        int barX = timeTX;
        int barY = timeTY + 6;
        float ratio = (float) riddleTimerFrames / (float) riddleTimeLimitFrames;
        ratio = Math.max(0f, Math.min(1f, ratio));
        g2.setColor(new Color(0,0,0,80));
        g2.fillRoundRect(barX, barY, textWidth, barH, barH, barH);
        Color ccol = ratio > 0.6f ? new Color(120,220,140) : ratio > 0.25f ? new Color(240,200,80) : new Color(240,120,120);
        int fillW = Math.max(2, (int)(textWidth * ratio));
        g2.setColor(ccol);
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);
        g2.setColor(new Color(255,255,255,80));
        g2.drawRoundRect(barX, barY, textWidth, barH, barH, barH);
    }


    // ------------------------ HELPERS ------------------------

    private boolean isPunctuation(char c) {
        return "!@#$%^&*()_+-={}[]|:;\"'<>,.?/`~".indexOf(c) >= 0;
    }

    private void resetAllTaskState() {
        // Math
        taskGenerated = false;
        playerInput = "";
        answerChecked = false;
        answerCorrect = false;
        taskTimerFrames = 0;
        taskTimeLimitFrames = 0;
        questionsAsked = 0;
        correctCount = 0;
        wrongCount = 0;
        // Riddle
        riddleGenerated = false;
        riddlePlayerInput = "";
        riddleAnswerChecked = false;
        riddleAnswerCorrect = false;
        riddleTimerFrames = 0;
        riddleTimeLimitFrames = 0;
        // tile select
        tileSelectGenerated = false;
        tsPhase = 0;
        tsTimer = 0;
        tsResult = false;
        for (int r = 0; r < TS_GRID; r++)
            for (int c = 0; c < TS_GRID; c++) {
                tsPattern[r][c] = false;
                tsSelected[r][c] = false;
            }
     // Button Match
        buttonMatchGenerated = false;
        buttonMatchResolved = false;
        buttonMatchStartNano = 0;
        buttonMatchTargetSeconds = 0;
        buttonMatchFeedback = "";
        buttonMatchFeedbackFrames = 0;
        buttonMatchButtonRect.setBounds(0, 0, 0, 0);
        
     // Pattern Switched
     // pattern switches task
    	patternGenerated = false;
    	patternSequence = new int[0];
    	patternLength = 0;
    	patternShowing = true;

    	// timing (frames @ 60fps)
    	patternFlashFrames = 30; // 0.5s
    	patternGapFrames = 6; // small gap between flashes (optional)
    	patternIndex = 0;
    	patternFlashTimer = 0; // counts down within flash
    	patternGapTimer = 0; // counts down between flashes
    	patternInputIndex = 0;
    	patternInputTimerFrames = 0;
    	patternInputLimitFrames = 5 * 60; // 5 seconds
    	patternChecked = false;
    	patternSuccess = false; 

    	// Pattern Switches
    	patternGenerated = false;
    	patternSequence = new int[0];
    	patternLength = 0;
    	patternShowing = true;
    	patternIndex = 0;
    	patternFlashTimer = 0;
    	patternGapTimer = 0;
    	patternInputIndex = 0;
    	patternInputTimerFrames = 0;
    	patternInputLimitFrames = 5 * 60;
    	patternChecked = false;
    	patternSuccess = false;
    	
        
     // Vault Sequence
        vaultGenerated = false;
        vaultResolved = false;
        vaultSequence = null;
        vaultSeqLen = 0;
        vaultProgressIndex = 0;
        vaultStrikes = 0;
        vaultTimerFrames = 0;
        vaultTimeLimitFrames = 0;
        vaultFeedback = "";
        vaultFeedbackFrames = 0;
        vaultFlavorRiddle = "";
        if (vaultButtonRects != null) {
            for (int i = 0; i < vaultButtonRects.length; i++) {
                if (vaultButtonRects[i] != null) vaultButtonRects[i].setBounds(0,0,0,0);
            }
        }
        

        
    }

    public void handleTaskFailed(int cooldownSeconds, String popupMessage) {
        resetAllTaskState();
        taskCooldownFrames = Math.max(1, cooldownSeconds) * 60;
        showBoxMessage(popupMessage, gp.screenWidth / 2 - gp.tileSize * 2, gp.screenHeight / 2 - gp.tileSize);
        boxMessageOn = true;
        messageCounter = 0;
        messageDuration = Math.max(60, cooldownSeconds * 60 / 2);
        gp.gameState = gp.playState;
    }

    public void handleTaskSuccess(String popupMessage) {
        resetAllTaskState();
        showBoxMessage(popupMessage, gp.screenWidth / 2 - gp.tileSize * 2, gp.screenHeight / 2 - gp.tileSize);
        boxMessageOn = true;
        messageCounter = 0;
        messageDuration = 120;
        try {
            if (gp.player != null) {
                int idx = gp.player.curTaskIndex;
                if (idx >= 0 && idx < gp.player.tasksList.size()) {
                    gp.player.tasksList.get(idx).setCompleted(true);
                    if (idx >= 0 && idx < gp.tasks.length) gp.tasks[idx] = null;
                }
            }
        } catch (Exception ignored) {}
        gp.gameState = gp.playState;
    }


    
    // Draw the large "PAUSED" screen in the center
    public void drawPauseScreen() {
        // draw frame
    	g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent black
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // fill entire screen

		String text = "PAUSED"; // paused text
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F)); // large bold font
		int x = getXforCenteredText(text); // center text
		int y = gp.screenHeight / 2; // vertical center
		g2.setColor(Color.white); // white color
		g2.drawString(text, x, y); // draw paused text
		
		// Draw "ESCAPE to Resume" prompt below
		String prompt = "ESCAPE to Resume";
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F)); // smaller font for prompt
		x = getXforCenteredText(prompt);
		y += gp.tileSize * 1;
		g2.drawString(prompt, x, y); // draw prompt
		
		// Draw "SAVE and EXIT" option below
		String saveExit = "ENTER to Save & Exit";
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F)); // smaller font for prompt
		x = getXforCenteredText(saveExit);
		y += gp.tileSize * 1;
		g2.drawString(saveExit, x, y); // draw prompt
    	
    }

    public void drawDialogueScreen() {

        // ===========================
        // LEVEL COMPLETE – CINEMATIC
        // ===========================
        if (levelFinished) {

            g2.setColor(Color.black);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));
            g2.setColor(Color.white);
            FontMetrics fm = g2.getFontMetrics();

            String title = "LEVEL COMPLETE";
            int titleX = gp.screenWidth / 2 - fm.stringWidth(title) / 2;
            int titleY = gp.screenHeight / 2 - fm.getHeight();
            g2.drawString(title, titleX, titleY);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24f));
            fm = g2.getFontMetrics();

            String text = currentDialogue != null ? currentDialogue : "";
            String[] lines = text.split("\n");

            int y = gp.screenHeight / 2 + fm.getAscent();
            for (String line : lines) {
                int x = gp.screenWidth / 2 - fm.stringWidth(line) / 2;
                g2.drawString(line, x, y);
                y += fm.getHeight();
            }

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));
            g2.setColor(new Color(200, 200, 200));
            String hint = "Press ENTER to continue";
            int hx = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(hint) / 2;
            g2.drawString(hint, hx, gp.screenHeight - gp.tileSize);

            return;
        }

        // ===========================
        // NORMAL DIALOGUE PANEL
        // ===========================

        int x = gp.tileSize * 2;
        int y = gp.tileSize * 6;
        int width = gp.screenWidth - gp.tileSize * 4;
        int height = gp.tileSize * 4;
        int radius = 22;
        int padding = gp.tileSize / 2;

        // shadow
        for (int i = 6; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + i, y + i, width, height, radius, radius);
        }

        // background
        GradientPaint bg = new GradientPaint(
                x, y, new Color(20, 26, 32, 235),
                x, y + height, new Color(36, 44, 52, 235)
        );
        Paint oldPaint = g2.getPaint();
        g2.setPaint(bg);
        g2.fillRoundRect(x, y, width, height, radius, radius);

        // border
        g2.setPaint(oldPaint);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, width, height, radius, radius);

        // header
        int headerH = gp.tileSize;
        GradientPaint header = new GradientPaint(
                x, y, new Color(90, 140, 255, 220),
                x + width, y, new Color(90, 220, 200, 220)
        );
        g2.setPaint(header);
        g2.fillRoundRect(x + 4, y + 4, width - 8, headerH, radius / 2, radius / 2);

        // speaker name
        g2.setPaint(oldPaint);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        g2.setColor(Color.white);

        String speaker = (currentDialogueSpeaker != null && !currentDialogueSpeaker.isEmpty())
                ? currentDialogueSpeaker
                : "Dialogue";

        g2.drawString(
                speaker,
                x + padding,
                y + headerH / 2 + g2.getFontMetrics().getAscent() / 2
        );

        // ===========================
        // PORTRAIT (NPC SPRITE)
        // ===========================
        int portraitSize = gp.tileSize;
        int portraitX = x + padding;
        int portraitY = y + headerH + padding / 2;

        BufferedImage portrait = null;

        if (gp.npc != null) {
            for (int i = 0; i < gp.npc.length; i++) {
                if (gp.npc[i] == null) continue;

                if (gp.npc[i].name != null &&
                    gp.npc[i].name.equalsIgnoreCase(speaker)) {

                    if (gp.npc[i].down1 != null) portrait = gp.npc[i].down1;
                    else if (gp.npc[i].down1 != null) portrait = gp.npc[i].down1;
                    break;
                }
            }
        }

        if (portrait != null) {
            Shape oldClip = g2.getClip();
            Ellipse2D circle = new Ellipse2D.Float(
                    portraitX, portraitY, portraitSize, portraitSize
            );
            g2.setClip(circle);

            int iw = portrait.getWidth();
            int ih = portrait.getHeight();
            double scale = portraitSize / (double)Math.max(iw, ih);
            int dw = (int)(iw * scale);
            int dh = (int)(ih * scale);

            int dx = portraitX + (portraitSize - dw) / 2;
            int dy = portraitY + (portraitSize - dh) / 2;

            g2.drawImage(portrait, dx, dy, dw, dh, null);
            g2.setClip(oldClip);

            g2.setColor(new Color(255, 255, 255, 80));
            g2.drawOval(portraitX, portraitY, portraitSize, portraitSize);
        } else {
            g2.setColor(new Color(45, 52, 60, 220));
            g2.fillOval(portraitX, portraitY, portraitSize, portraitSize);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawOval(portraitX, portraitY, portraitSize, portraitSize);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22f));
            FontMetrics pfm = g2.getFontMetrics();
            String init = speaker.substring(0, 1);
            int ix = portraitX + (portraitSize - pfm.stringWidth(init)) / 2;
            int iy = portraitY + (portraitSize + pfm.getAscent()) / 2 - 4;
            g2.drawString(init, ix, iy);
        }

        // ===========================
        // DIALOGUE TEXT
        // ===========================
        int textX = portraitX + portraitSize + 18;
        int textY = portraitY + 6;
        int textW = width - (textX - x) - padding;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20f));
        g2.setColor(new Color(230, 230, 230));

        String dialogue = currentDialogue != null ? currentDialogue : "";
        java.util.List<String> lines = wrapText(dialogue, g2.getFontMetrics(), textW);

        int lh = g2.getFontMetrics().getHeight();
        for (String line : lines) {
            g2.drawString(line, textX, textY + g2.getFontMetrics().getAscent());
            textY += lh;
        }

        // continue arrow
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            int ax = x + width - padding - 14;
            int ay = y + height - padding - 10;
            g2.setColor(new Color(200, 220, 255));
            g2.fillPolygon(
                    new int[]{ax, ax + 10, ax},
                    new int[]{ay - 6, ay, ay + 6},
                    3
            );
        }

        g2.setStroke(new BasicStroke(1f));
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
    
 // Draw a rounded black translucent panel with white border and multiple lines of text
    public void drawSubWindow(int x, int y, String[] lines, int fontSize) {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontSize));
        FontMetrics fm = g2.getFontMetrics();

        int padding = 10;

        // Calculate width and height based on text
        int textWidth = 0;
        for (String line : lines) {
            textWidth = Math.max(textWidth, fm.stringWidth(line));
        }
        int textHeight = fm.getHeight() * lines.length;

        int boxWidth = textWidth + padding * 2;
        int boxHeight = textHeight + padding * 2;

        // Draw background
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 10, 10);

        // Draw border
        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(x, y, boxWidth, boxHeight, 10, 10);

        // Draw text
        g2.setColor(Color.white);
        int textX = x + padding;
        int textY = y + padding + fm.getAscent();
        for (String line : lines) {
            g2.drawString(line, textX, textY);
            textY += fm.getHeight();
        }
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
            // Use the main image path from the skin array: skins[skinIndex][2][0]
            String imagePath = gp.skins[skinIndex][2][0]; 
            InputStream skin = getClass().getResourceAsStream(imagePath);

            if (skin == null) {
                System.out.println("Skin preview not found: " + imagePath);
                skinPreview = null;
                return;
            }

            skinPreview = ImageIO.read(skin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
