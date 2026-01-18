package main; // package this class belongs to

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import Item.Flashlight;
import Item.Food;
import Item.Item;
import Item.Key;
import Item.Throwable;
import Item.blueKey;
import Item.greenKey;
import Item.redKey;
import saves.save1;
import saves.save2;
import saves.save3;
import task.Task;

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
    
 // Scroll state for the INSTRUCTIONS screen
    public int instrScrollOffset = 0;
    public int instrScrollSpeed = 28; // pixels per step (same as your line height)
    public int instrContentHeight = 0;      // computed each frame
    public int instrViewportHeight = 0;     // computed each frame
    
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
	
	 // Logic panel task state
	 private boolean logicGenerated = false;
	 private int logicStatementCount = 6;
	 private String[] logicStatements = new String[6];
	 private boolean[] logicCorrectAnswers = new boolean[6]; // true or false
	 private int[] logicPlayerAnswers = new int[6]; // -1 = unset, 0 = false, 1 = true
	 private int logicTimerFrames = 0;
	 private int logicTimeLimitFrames = 0;
	 private int logicFlashFrames = 0;

	 // Toggle switch hitboxes
	 private Rectangle[] logicTrueSwitches = new Rectangle[6];
	 private Rectangle[] logicFalseSwitches = new Rectangle[6];

	 // Logic statement pool (statement, correct answer)
	 private final String[][] LOGIC_STATEMENTS = {
	     // Simple facts - TRUE
	     {"All squares are rectangles.", "true"},
	     {"Water is composed of hydrogen and oxygen.", "true"},
	     {"The opposite of false is true.", "true"},
	     {"A triangle has three sides.", "true"},
	     {"All mammals are animals.", "true"},
	     {"Fire requires oxygen to burn.", "true"},
	     {"The Earth orbits the Sun.", "true"},
	     {"Ice is frozen water.", "true"},
	     {"A pentagon has five sides.", "true"},
	     {"Humans need oxygen to breathe.", "true"},
	     {"All birds have beaks.", "true"},
	     {"The sum of 2 + 2 equals 4.", "true"},
	     {"A circle has no corners.", "true"},
	     {"All roses are flowers.", "true"},
	     {"Sound travels through air.", "true"},
	     {"A week has seven days.", "true"},
	     {"All squares have four equal sides.", "true"},
	     {"Light travels faster than sound.", "true"},
	     {"A year has 365 days (non-leap).", "true"},
	     {"All diamonds are carbon.", "true"},
	     
	     // Simple facts - FALSE
	     {"All rectangles are squares.", "false"},
	     {"All animals are mammals.", "false"},
	     {"All birds can fly.", "false"},
	     {"The Sun orbits the Earth.", "false"},
	     {"All flowers are roses.", "false"},
	     {"A square has three sides.", "false"},
	     {"All fish live in freshwater.", "false"},
	     {"Ice is hotter than steam.", "false"},
	     {"A triangle has four corners.", "false"},
	     {"All metals are magnetic.", "false"},
	     {"Humans can breathe underwater without equipment.", "false"},
	     {"The opposite of true is maybe.", "false"},
	     {"All circles are squares.", "false"},
	     {"Sound travels faster than light.", "false"},
	     {"A pentagon has six sides.", "false"},
	     {"All water is saltwater.", "false"},
	     {"Fire can burn without oxygen.", "false"},
	     {"A week has eight days.", "false"},
	     {"All plants are trees.", "false"},
	     {"All birds are penguins.", "false"},
	     
	     // Logical deductions - TRUE
	     {"If all cats are animals, then some animals are cats.", "true"},
	     {"If X > Y and Y > Z, then X > Z.", "true"},
	     {"If it is raining, the ground is wet. It is raining. Therefore, the ground is wet.", "true"},
	     {"All A are B. X is A. Therefore, X is B.", "true"},
	     {"If today is Monday, tomorrow is Tuesday.", "true"},
	     {"Either the light is on or off. The light is not on. Therefore, it is off.", "true"},
	     {"If all students study, and John is a student, then John studies.", "true"},
	     {"No circles are squares. X is a circle. Therefore, X is not a square.", "true"},
	     {"All prime numbers greater than 2 are odd.", "true"},
	     {"If a shape has four equal sides and four right angles, it is a square.", "true"},
	     
	     // Logical deductions - FALSE
	     {"If some cats are animals, then all animals are cats.", "false"},
	     {"If all birds can fly, and penguins are birds, then penguins can fly.", "false"},
	     {"All A are B. Therefore, all B are A.", "false"},
	     {"Some dogs are brown. Therefore, all dogs are brown.", "false"},
	     {"If it is raining, the ground is wet. The ground is wet. Therefore, it is raining.", "false"},
	     {"All squares are rectangles. Therefore, all rectangles are squares.", "false"},
	     {"If X = Y, then X > Y.", "false"},
	     {"Some fish can swim. Therefore, all animals can swim.", "false"},
	     {"All cars have wheels. Some wheels are round. Therefore, all round things are cars.", "false"},
	     {"If today is Monday, yesterday was Wednesday.", "false"},
	     
	     // Paradoxes and tricky statements - TRUE
	     {"This statement has more than three words.", "true"},
	     {"The statement 'All generalizations are false' is itself a generalization.", "true"},
	     {"A set containing nothing is still a set.", "true"},
	     {"Zero is neither positive nor negative.", "true"},
	     {"A stopped clock is correct twice a day.", "true"},
	     {"An empty statement can still be a statement.", "true"},
	     
	     // Paradoxes and tricky statements - FALSE
	     {"This statement is false.", "false"}, // Classic liar paradox - treating as false
	     {"The following statement is true. The previous statement is false.", "false"},
	     {"Every rule has an exception, including this one.", "false"},
	     {"Nothing is impossible.", "false"},
	     {"All statements are true.", "false"},
	     {"This statement cannot be proven.", "false"}
	 };

    
 // VAULT SEQUENCE TASK VARIABLES
    private boolean vaultGenerated = false;

    private int vaultTimerFrames = 0;
    private int vaultTimeLimitFrames = 0;
    private int vaultWrongAnswers = 0;
    // 4 riddles
    private String[] vaultRiddleQ = new String[4];
    private String[] vaultRiddleA = new String[4];
    private String[] vaultInputs  = new String[]{"", "", "", ""};
    private boolean[] vaultSolved = new boolean[]{false, false, false, false};

    // digits awarded
    private int[] vaultDigits = new int[4];

    // which riddle is currently shown (0..3)
    private int vaultIndex = 0;

    // final code entry
    private boolean vaultEnteringCode = false;
    private String vaultFinalInput = "";

    // feedback
    private String vaultFeedback = "";
    private int vaultFeedbackFrames = 0;

    
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
    
    // Fuse repair task state
    private boolean fuseGenerated = false;
    private int fuseNodeCount = 9;
    private Color[] fuseLeftColors = new Color[9];
    private Color[] fuseRightColors = new Color[9];
    private int[] fuseRightOrder = new int[9]; // maps right index -> color index
    private boolean[] fuseConnected = new boolean[9];
    private int fuseSelectedLeft = -1; // -1 = none selected
    private int fuseConnectionsMade = 0;
    private int fuseTimerFrames = 0;
    private int fuseTimeLimitFrames = 0;

    // Available wire colors (9 distinct colors)
    private final Color[] FUSE_COLORS = {
        new Color(220, 60, 60),   // Red
        new Color(60, 120, 220),  // Blue
        new Color(80, 200, 80),   // Green
        new Color(240, 200, 60),  // Yellow
        new Color(200, 80, 200),  // Purple
        new Color(240, 140, 60),  // Orange
        new Color(100, 220, 220), // Cyan
        new Color(240, 120, 200), // Pink
        new Color(200, 200, 200)  // White/Gray
    };

    // Node hitboxes for click detection
    private Rectangle[] fuseLeftNodes = new Rectangle[9];
    private Rectangle[] fuseRightNodes = new Rectangle[9];

    // Flash effect for wrong connection
    private int fuseFlashFrames = 0;

    
 // Cooking task state
    private boolean cookingGenerated = false;
    private int cookingQuestionIndex = -1;
    private int cookingCorrectAnswer = -1; // 0-3 for A-D
    private int cookingSelectedAnswer = -1; // -1 = none selected
    private boolean cookingAnswerSubmitted = false;
    private boolean cookingAnswerCorrect = false;
    private int cookingTimerFrames = 0;
    private int cookingTimeLimitFrames = 0;

    // Cooking question pool (questions, options, correct answer index)
    private final String[][] COOKING_QUESTIONS = {
        {"What temperature does water boil at (at sea level)?", "90°C", "100°C", "110°C", "95°C", "1"},
        {"Which ingredient makes bread rise?", "Sugar", "Salt", "Yeast", "Flour", "2"},
        {"What is the main ingredient in guacamole?", "Tomato", "Onion", "Avocado", "Lime", "2"},
        {"At approximately what temperature does chocolate begin to melt?", "0–5°C", "15–18°C", "30–32°C", "40–45°C", "2"},
        {"Which cooking method uses dry heat and circulating air to cook food?", "Boiling", "Baking", "Steaming", "Poaching", "1"},
        {"Which knife is best for chopping vegetables?", "Bread knife", "Fillet knife", "Chef's knife", "Paring knife", "2"},
        {"What does 'al dente' refer to?", "Overcooked rice", "Soft vegetables", "Pasta cooked firm to bite", "Crispy fried food", "2"},
        {"Which of these is a dry-heat cooking technique?", "Steaming", "Grilling", "Poaching", "Boiling", "1"},
        {"Which fat is commonly used for sautéing because of its high smoke point?", "Butter", "Vegetable oil", "Extra virgin olive oil", "Margarine", "1"},
        {"What is the recommended minimum internal temperature for cooked chicken?", "55°C", "60°C", "74°C", "80°C", "2"},
        {"What is the purpose of blanching vegetables?", "To fry them", "Brief cook to stop enzymes", "To freeze immediately", "To add sweetness", "1"},
        {"Which of the following is a leavening agent?", "Salt", "Baking powder", "Water", "Cocoa", "1"},
        {"What does 'mise en place' mean?", "Cook quickly", "Everything in its place", "Taste as you go", "Finish with sauce", "1"},
        {"Which cut of meat is typically most tender?", "Shank", "Round", "Brisket", "Tenderloin", "3"},
        {"Which of these is best for making mayonnaise?", "Boiled egg", "Raw egg yolk with oil", "Bread crumbs", "Grated cheese", "1"},
        {"Which spice is the main ingredient in curry powder?", "Cumin", "Turmeric", "Cinnamon", "Oregano", "1"},
        {"Which method cooks food by surrounding it with steam?", "Frying", "Steaming", "Grilling", "Roasting", "1"},
        {"What ingredient is primarily responsible for browning via the Maillard reaction?", "Water", "Fat", "Proteins and sugars", "Acids", "2"},
        {"Which of the following is a dry, cured pork product?", "Ham", "Prosciutto", "Fresh bacon", "Uncooked sausage", "1"},
        {"Which of these is NOT a mother sauce in classical French cuisine?", "Béchamel", "Velouté", "Hollandaise", "Pesto", "3"},
        {"What is the culinary term for cutting food into long, thin strips?", "Brunoise", "Chiffonade", "Julienne", "Dice", "2"},
        {"Which cheese is traditionally used on pizza for its meltability?", "Cheddar", "Feta", "Mozzarella", "Parmesan", "2"},
        {"What does it mean to 'deglaze' a pan?", "Clean with soap", "Add liquid to dissolve bits", "Flip food quickly", "Add flour to thicken", "1"},
        {"Which flour has the highest protein content, typically used for bread?", "Cake flour", "Pastry flour", "Bread flour", "Rice flour", "2"},
        {"What is the safe refrigerator temperature to slow bacterial growth?", "10°C", "7°C", "4°C or below", "8°C", "2"},
        {"Which fruit is high in vitamin C and commonly used to prevent scurvy?", "Banana", "Apple", "Orange", "Pear", "2"},
        {"What does 'folding' mean in baking?", "Rapidly beat", "Cut finely", "Gently combine to preserve air", "Stretch dough", "2"},
        {"Which method uses water just below boiling to cook delicate foods?", "Boiling", "Frying", "Poaching", "Stewing", "2"},
        {"Which of these is a common emulsifier used in cooking?", "Water", "Sugar", "Lecithin (egg yolk)", "Salt", "2"},
        {"What kitchen tool measures dry ingredients most accurately?", "Tablespoon", "Cup", "Kitchen scale", "Measuring jug", "2"},
        {"Which grain is used to make risotto?", "Long-grain rice", "Jasmine rice", "Arborio rice", "Basmati rice", "2"},
        {"Which method is best for making stock from bones?", "Frying quickly", "Simmering for hours", "Freezing bones", "Microwaving", "1"},
        {"Which herb is the primary ingredient in pesto?", "Parsley", "Cilantro", "Basil", "Thyme", "2"},
        {"What's the common thickener in cream soups?", "Lemon juice", "Roux (butter + flour)", "Vinegar", "Soy sauce", "1"},
        {"Which oil is traditionally used in Japanese tempura for frying?", "Olive oil", "Butter", "Vegetable oil", "Coconut oil", "2"},
        {"Which of these indicates a cake is fully baked?", "Sinks in middle", "Top is wet", "Toothpick comes out clean", "Slips off pan", "2"},
        {"Which foodborne pathogen is commonly associated with undercooked eggs?", "E. coli", "Salmonella", "Listeria", "Botulism", "1"},
        {"What is 'umami'?", "A texture", "A bitter taste", "A savory taste", "A cooking method", "2"},
        {"Which of the following is a quick method for tenderizing meat?", "Roasting whole", "Pounding with mallet", "Freezing only", "Deep-frying dry", "1"},
        {"Which acid is commonly used to 'cook' fish in ceviche?", "Lactic acid", "Malic acid", "Citric acid (lime/lemon)", "Tartaric acid", "2"}
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
    

    
    void clampInstrScroll() {
        if (instrContentHeight <= instrViewportHeight) {
            instrScrollOffset = 0;
        } else {
            instrScrollOffset = Math.max(0, Math.min(instrScrollOffset, instrContentHeight - instrViewportHeight));
        }
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
        	gp.gameState = gp.playState;
            gp.keyH.escapePressed = false;
            resetAllTaskState();
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
                    case "Button Match Task" -> drawButtonMatchTask();
                    case "Vault Sequence Task" -> drawVaultSequenceTask();
                    case "Pattern Switches Task" -> drawPatternSwitchTask();
                    case "Cooking Task" -> drawCookingTask();
                    case "Fuse Repair Task" -> drawFuseRepairTask();
                    case "Logic Panel Task" -> drawLogicPanelTask();
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
                if (commandNum < 0) commandNum = 5; // wrap around top->bottom
                uiUp = false; // consume the input (edge-trigger)
            }
            if (uiDown) { // down navigation pressed
                commandNum++; // move selection down
                if (commandNum > 5) commandNum = 0; // wrap bottom->top
                uiDown = false; // consume
            }
            if (uiConfirm) { // user activated the currently selected menu entry
                switch (commandNum) {
                    case 0: // NEW GAME: switch to play state
                        titleScreenState = 5;
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
        
        if (titleScreenState == 5) {
        	if(!awaitingKeybind) {
        		if (uiRight) {
        			gp.speedRunState = !gp.speedRunState;
        			uiRight = false;
        		}
        		else if (uiLeft) {
        			gp.speedRunState = !gp.speedRunState;
        			uiLeft = false;
        		}
        		else if (uiConfirm) {
        			gp.level = 1;
        			gp.resetGame(false);
        			System.out.println(gp.level);
        			gp.gameState = gp.playState;
        			gp.speedRunTimerFrames = 0;
        			gp.playMusic(0);
        			uiConfirm = false;
        		}
        		else if (uiBack) {
        			titleScreenState = 0;
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

        // ===== FRAME POSITION (BOTTOM LEFT) =====
        int frameX = gp.tileSize / 2;
        int frameY = gp.screenHeight - frameHeight - gp.tileSize / 2;

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

        // ===== DRAW ITEMS IN SLOTS =====
        for (int i = 0; i < gp.player.inventory.size() && i < slots; i++) {
            Item item = gp.player.inventory.get(i);
            int slotX = slotXstart + i * (slotSize + slotGap);
            int slotY = slotYstart;

            g2.drawImage(
                item.image,
                slotX + 8,
                slotY + 8,
                slotSize - 16,
                slotSize - 16,
                null
            );
        }

        // ===== DRAW CURSOR / SELECTION =====
        if (slotRow > -1) {
            int cursorX = slotXstart + slotRow * (slotSize + slotGap);
            int cursorY = slotYstart;

            g2.setColor(Color.yellow);
            g2.drawRoundRect(
                cursorX - 4,
                cursorY - 4,
                slotSize + 8,
                slotSize + 8,
                12,
                12
            );

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

            // --- Title (fixed) ---
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            String title = "INSTRUCTIONS";
            int x = getXforCenteredText(title);
            int y = gp.tileSize;
            g2.setColor(Color.white);
            g2.drawString(title, x, y);

            // --- Layout for scrollable body ---
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18F));
            String[] instructions = {
                "Default Movement (Dynamic)\n" +
                "Use W / A / S / D to move\n" +
                "Hold SHIFT to sprint (uses stamina)\n" +
                "Hold CTRL to crouch (quieter and safer)",

                "Stamina\n" +
                "Sprinting drains stamina\n" +
                "If stamina reaches 0, you slow down\n" +
                "Eat food to restore stamina",

                "Inventory\n" +
                "You can hold up to 3 items\n" +
                "Switch items using 1 / 2 / 3\n" +
                "Selected item shows available actions",

                "Interact\n" +
                "Press E to interact\n" +
                "- Talk to NPCs\n" +
                "- Start tasks",

                "Throwables\n" +
                "Press Q to throw an item\n" +
                "Click a tile within range\n" +
                "Guards investigate the sound",

                "Stealth\n" +
                "Running near guards increases detection\n" +
                "Crouch and use cover to stay hidden\n" +
                "Use throwables to lure guards",

                "Failing\n" +
                "Failing tasks causes a cooldown\n" +
                "Getting caught may lead to death or reset"
            };

            // body region (below title, above footer)
            int bodyX = gp.tileSize;
            int bodyYStart = y + gp.tileSize; // start below title
            int bodyWidth = gp.screenWidth - gp.tileSize * 3; // leave space for scrollbar
            int bodyHeight = gp.screenHeight - bodyYStart - gp.tileSize * 2; // leave bottom space for ESCAPE text
            instrViewportHeight = bodyHeight; // remember for page up/down

            int lineHeight = 28;

            // Create a clipped graphics context so only the body area is affected by translate
            Graphics2D g2Body = (Graphics2D) g2.create();
            g2Body.setClip(bodyX, bodyYStart, bodyWidth, bodyHeight);
            g2Body.translate(0, -instrScrollOffset); // scroll the body content

            // draw the instructions inside clipped/translated context
            int textX = bodyX;
            int textY = bodyYStart;
            for (String block : instructions) {
                String[] lines = block.split("\n");
                for (String line : lines) {
                    g2Body.drawString(line, textX, textY);
                    textY += lineHeight;
                }
                textY += lineHeight / 2; // extra spacing between sections
            }

            // compute content height for clamping
            instrContentHeight = textY - bodyYStart;
            g2Body.dispose();

            // draw a simple scrollbar on the right of the body region (only if needed)
            if (instrContentHeight > bodyHeight) {
                int scrollbarX = bodyX + bodyWidth + gp.tileSize / 2;
                int scrollbarY = bodyYStart;
                int scrollbarW = gp.tileSize / 2;
                int scrollbarH = bodyHeight;

                // background track
                g2.setColor(new Color(0x333333));
                g2.fillRect(scrollbarX, scrollbarY, scrollbarW, scrollbarH);

                // thumb (proportional)
                float viewToContent = (float) bodyHeight / (float) instrContentHeight;
                int thumbH = Math.max(20, (int) (scrollbarH * viewToContent));
                float maxScroll = instrContentHeight - bodyHeight;
                int thumbY = scrollbarY;
                if (maxScroll > 0) {
                    thumbY = scrollbarY + (int) ((instrScrollOffset / maxScroll) * (scrollbarH - thumbH));
                }

                g2.setColor(new Color(0xBBBBBB));
                g2.fillRect(scrollbarX + 2, thumbY, scrollbarW - 4, thumbH);
            }

            // footer (fixed)
            g2.setColor(Color.white);
            g2.drawString("ESCAPE to go back", gp.tileSize, gp.screenHeight - gp.tileSize);
        }
        
        else if (titleScreenState == 5) {
        	g2.setColor(new Color(0,0,0,200));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            String text = "SPEED RUN MODE:";
            int x = gp.screenWidth / 2 - gp.tileSize * 3;
            int y = gp.tileSize*2;

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            
            text = "<     " + (gp.speedRunState ? "ON" : "False") + "     >";
            
            g2.drawString(text, x+gp.tileSize, y+gp.tileSize*2);
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

        // ===== CONFIG =====
        final int bars = 5;
        final int barHeight = 10;
        final int barGap = 6;
        final int padding = 6;

        // ===== HOTBAR DIMENSIONS (MATCH INVENTORY) =====
        final int slotSize = gp.tileSize;
        final int slots = 3;
        final int slotGap = 8;

        int hotbarWidth =
                padding * 2 +
                (slotSize * slots) +
                (slotGap * (slots - 1));

        int hotbarX = gp.tileSize / 2;
        int hotbarHeight = slotSize + padding * 2;
        int hotbarY = gp.screenHeight - hotbarHeight - gp.tileSize / 2;

        // ===== STAMINA BAR POSITION =====
        int x = hotbarX;
        int y = hotbarY - barHeight - 14; // spacing above hotbar

        // ===== BAR SIZE =====
        int barWidth = (hotbarWidth - (bars - 1) * barGap) / bars;

        // ===== BACKGROUND =====
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(
                x - padding,
                y - padding,
                hotbarWidth + padding * 2,
                barHeight + padding * 2,
                10, 10
        );

        g2.setColor(Color.white);
        g2.drawRoundRect(
                x - padding,
                y - padding,
                hotbarWidth + padding * 2,
                barHeight + padding * 2,
                10, 10
        );

        // ===== STAMINA → BAR COUNT =====
        float ratio = 0f;
        if (gp.player != null && gp.player.maxStamina > 0f) {
            ratio = gp.player.stamina / gp.player.maxStamina;
            ratio = Math.max(0f, Math.min(1f, ratio));
        }

        int filledBars = (int) Math.ceil(ratio * bars);

        // ===== DRAW BARS =====
        for (int i = 0; i < bars; i++) {

            int barX = x + i * (barWidth + barGap);

            if (i < filledBars) {
                // Color by sprint effectiveness
                if (filledBars >= 4) {
                    g2.setColor(new Color(80, 200, 120)); // green (80–100%)
                } else if (filledBars >= 2) {
                    g2.setColor(new Color(240, 200, 80)); // yellow (40–60%)
                } else {
                    g2.setColor(new Color(220, 80, 80)); // red (20%)
                }
            } else {
                g2.setColor(new Color(70, 70, 70, 180)); // empty
            }

            g2.fillRoundRect(barX, y, barWidth, barHeight, 6, 6);

            g2.setColor(Color.white);
            g2.drawRoundRect(barX, y, barWidth, barHeight, 6, 6);
        }
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
    
    public void drawSpeedRunTimer(Graphics2D g2) {

        int maxTimeSeconds = 0;

        switch (gp.level) {
            case 1 -> maxTimeSeconds = 300;
            case 2 -> maxTimeSeconds = 360;
            case 3 -> maxTimeSeconds = 420;
            case 4 -> maxTimeSeconds = 10;
        }

        int maxFrames = maxTimeSeconds * 60;
        int remainingFrames = Math.max(0, maxFrames - gp.speedRunTimerFrames);

        int totalSeconds = remainingFrames / 60;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Warning colors
        if (totalSeconds <= 10) {
            g2.setColor(Color.red);
        } else if (totalSeconds <= 30) {
            g2.setColor(Color.orange);
        } else {
            g2.setColor(Color.white);
        }
        
        if (totalSeconds == 0) {
        	gp.speedRunLost = true;
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));

        String timeText = String.format("TIME %02d:%02d", minutes, seconds);


        int x = gp.screenWidth - 220;
        int y = gp.screenHeight - 100;

        g2.drawString(timeText, x, y);
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

 // ------------------------ DRAW BUTTON MATCH TASK ------------------------
    public void drawButtonMatchTask() {

        // ==================== RENDERING SETUP ====================
        // Enable anti-aliasing for smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background overlay with subtle fade
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 10;
        int panelH = gp.tileSize * 7;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;
        int arc = 30;
        int pad = gp.tileSize / 3;

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);

        // Modern dark gradient background
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(35, 40, 50),
            panelX, panelY + panelH, new Color(25, 28, 35)
        );
        g2.setPaint(bgGradient);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // Accent border with glow effect
        g2.setColor(new Color(100, 150, 255, 120));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) {
            // Display centered cooldown message
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
            g2.setFont(cooldownFont);
            g2.setColor(new Color(240, 100, 100));
            
            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
            g2.drawString(locked, lx, ly);

            // Block all inputs during cooldown
            gp.mouseClicked = false;
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
            gp.keyH.escapePressed = false;
            return;
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return;
        }

        // ==================== TITLE SECTION ====================
        String title = "Button Match";
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.95f);
        g2.setFont(titleFont);
        
        int titleY = panelY + (int)(gp.tileSize * 0.9);
        
        // Title shadow for depth
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, panelX + pad + 2, titleY + 2);
        
        // Title with gradient effect
        g2.setColor(new Color(230, 240, 255));
        g2.drawString(title, panelX + pad, titleY);

        // ==================== DIVIDER LINE ====================
        int dividerY = titleY + (int)(gp.tileSize * 0.5);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY);

        // ==================== INITIALIZATION ====================
        if (!buttonMatchGenerated) {
            buttonMatchGenerated = true;
            buttonMatchResolved = false;
            buttonMatchStartNano = System.nanoTime();
            buttonMatchFeedback = "";
            buttonMatchFeedbackFrames = 0;
        }

        // ==================== TIMER CALCULATION ====================
        // Calculate elapsed time and remaining countdown
        double elapsed = (System.nanoTime() - buttonMatchStartNano) / 1_000_000_000.0;
        double remaining = 5.0 - elapsed;

        // Auto-fail if time expires without input
        if (!buttonMatchResolved && remaining <= 0) {
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                "Too slow. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
            return;
        }

        // ==================== INSTRUCTIONS ====================
        Font infoFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.38f);
        g2.setFont(infoFont);
        g2.setColor(new Color(200, 210, 230));

        String instruction = "Press ENTER to stop the timer at exactly 0.00s";
        String target = "Target Window: \u00B10.10 seconds";

        int instructY = dividerY + (int)(gp.tileSize * 0.7);
        int targetY = instructY + g2.getFontMetrics().getHeight() + 5;

        // Center-align instructions
        int instructX = panelX + (panelW - g2.getFontMetrics().stringWidth(instruction)) / 2;
        int targetX = panelX + (panelW - g2.getFontMetrics().stringWidth(target)) / 2;

        g2.drawString(instruction, instructX, instructY);
        
        g2.setColor(new Color(120, 220, 140));
        g2.drawString(target, targetX, targetY);

        // ==================== COUNTDOWN DISPLAY ====================
        Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 1.6f);
        g2.setFont(timerFont);
        
        // Dynamic color based on time remaining
        Color timerColor;
        if (remaining > 2.0) {
            timerColor = new Color(120, 220, 140); // Green
        } else if (remaining > 1.0) {
            timerColor = new Color(240, 200, 100); // Yellow
        } else {
            timerColor = new Color(240, 120, 120); // Red
        }
        
        String countdownText = String.format("%.2f", Math.max(0, remaining));
        int countdownX = panelX + (panelW - g2.getFontMetrics().stringWidth(countdownText)) / 2;
        int countdownY = targetY + (int)(gp.tileSize * 1.3);

        // Timer glow effect
        g2.setColor(new Color(timerColor.getRed(), timerColor.getGreen(), 
                             timerColor.getBlue(), 60));
        g2.drawString(countdownText, countdownX + 3, countdownY + 3);
        
        g2.setColor(timerColor);
        g2.drawString(countdownText, countdownX, countdownY);

        // ==================== PROGRESS BAR ====================
        int barW = gp.tileSize * 7;
        int barH = gp.tileSize / 5;
        int barX = panelX + (panelW - barW) / 2;
        int barY = countdownY + (int)(gp.tileSize * 0.5);

        // Calculate fill percentage
        float ratio = (float)Math.max(0.0, Math.min(1.0, remaining / 5.0));

        // Background track with inner shadow
        g2.setColor(new Color(20, 25, 30, 200));
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

        // Animated fill bar with gradient
        int fillW = Math.max(4, (int)(barW * ratio));
        GradientPaint barGradient = new GradientPaint(
            barX, barY, timerColor,
            barX + fillW, barY, new Color(timerColor.getRed(), 
                                          timerColor.getGreen(), 
                                          timerColor.getBlue(), 180)
        );
        g2.setPaint(barGradient);
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);

        // Subtle border highlight
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH);

        // ==================== INTERACTIVE BUTTON ====================
        int btnW = gp.tileSize * 6;
        int btnH = (int)(gp.tileSize * 1.3);
        int btnX = panelX + (panelW - btnW) / 2;
        int btnY = barY + (int)(gp.tileSize * 0.8);

        // Store button bounds for mouse interaction
        buttonMatchButtonRect.setBounds(btnX, btnY, btnW, btnH);

        // Button shadow
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(btnX + 3, btnY + 3, btnW, btnH, 22, 22);

        // Button gradient background
        GradientPaint btnGradient = new GradientPaint(
            btnX, btnY, new Color(50, 55, 65),
            btnX, btnY + btnH, new Color(35, 40, 50)
        );
        g2.setPaint(btnGradient);
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 22, 22);

        // Button accent border
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(new Color(100, 150, 255, 140));
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 22, 22);

        // Button text with icon
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.55f));
        g2.setColor(new Color(240, 245, 255));
        String btnText = "⏎ PRESS ENTER";
        int btx = btnX + (btnW - g2.getFontMetrics().stringWidth(btnText)) / 2;
        int bty = btnY + (btnH - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(btnText, btx, bty);

        // ==================== FEEDBACK DISPLAY ====================
        int feedbackY = btnY + btnH + (int)(gp.tileSize * 0.7);

        if (buttonMatchResolved) {
            // Display result with color-coded feedback
            Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.6f);
            g2.setFont(feedbackFont);
            
            Color feedbackColor = buttonMatchFeedback.startsWith("✔") ? 
                new Color(120, 220, 140) : new Color(240, 120, 120);
            
            // Feedback glow
            g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
                                 feedbackColor.getBlue(), 80));
            int fw = g2.getFontMetrics().stringWidth(buttonMatchFeedback);
            int fx = panelX + (panelW - fw) / 2;
            g2.drawString(buttonMatchFeedback, fx + 2, feedbackY + 2);
            
            g2.setColor(feedbackColor);
            g2.drawString(buttonMatchFeedback, fx, feedbackY);

            // Continue prompt
            g2.setFont(infoFont);
            g2.setColor(new Color(200, 210, 230, 200));
            String hint = "Press ENTER to continue";
            int hw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, panelX + (panelW - hw) / 2, feedbackY + (int)(gp.tileSize * 0.6));

            // Handle continuation input
            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;

                if (buttonMatchFeedback.startsWith("✔")) {
                    handleTaskSuccess("Task Completed!");
                } else {
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                        "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
                }
            }
            return;
        }

        // ==================== INPUT HANDLING ====================
        boolean pressed = false;

        // Mouse click detection on button area
        if (gp.mouseClicked) {
            gp.mouseClicked = false;
            if (buttonMatchButtonRect.contains(gp.mouseX, gp.mouseY)) {
                pressed = true;
            }
        }

        // Keyboard ENTER input (primary method)
        if (gp.keyH.enterPressed) {
            gp.keyH.enterPressed = false;
            pressed = true;
        }

        // ==================== RESULT CALCULATION ====================
        if (pressed) {
            // Calculate accuracy - target is 0.00 seconds
            double accuracy = Math.abs(remaining - 0.0);

            // Check if within acceptable window
            if (accuracy <= buttonMatchWindow) {
                buttonMatchFeedback = String.format("✔ PERFECT! (%.3fs precision)", accuracy);
            } else {
                buttonMatchFeedback = String.format("✖ MISSED (%.3fs off target)", accuracy);
            }

            buttonMatchResolved = true;
            buttonMatchFeedbackFrames = 60;
        }

        // ==================== BOTTOM HINT ====================
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f));
        g2.setColor(new Color(180, 190, 210, 180));
        String hint = "Stop the timer at 0.00s • Accuracy window: \u00B10.10s • ESC to exit";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 2);
    }

    
 // ------------------------ DRAW PATTERN SWITCHES TASK ------------------------
    public void drawPatternSwitchTask() {

        // ==================== RENDERING SETUP ====================
        // Enable anti-aliasing for smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background overlay with subtle fade
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 10;
        int panelH = gp.tileSize * 7;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;
        int arc = 30;
        int pad = gp.tileSize / 3;

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);

        // Modern dark gradient background
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(35, 40, 50),
            panelX, panelY + panelH, new Color(25, 28, 35)
        );
        g2.setPaint(bgGradient);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // Accent border with glow effect
        g2.setColor(new Color(150, 100, 255, 120));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // Inner content area
        int innerX = panelX + pad;
        int innerY = panelY + pad;
        int innerW = panelW - pad * 2;

        // ==================== TITLE SECTION ====================
        String title = "Pattern Switches";
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.95f);
        g2.setFont(titleFont);
        
        int titleY = innerY + (int)(gp.tileSize * 0.9f);
        
        // Title shadow for depth
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, innerX + 2, titleY + 2);
        
        // Title with gradient effect
        g2.setColor(new Color(230, 240, 255));
        g2.drawString(title, innerX, titleY);

        // ==================== LEVEL BADGE ====================
        String lvl = "Level " + gp.level;
        Font badgeFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.42f);
        FontMetrics badgeFM = g2.getFontMetrics(badgeFont);
        
        int badgeW = (int)(gp.tileSize * 2.8);
        int badgeH = (int)(gp.tileSize * 0.55);
        int badgeX = panelX + panelW - pad - badgeW;
        int badgeY = innerY - gp.tileSize / 8;

        // Badge shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(badgeX + 2, badgeY + 2, badgeW, badgeH, 14, 14);

        // Badge gradient background
        GradientPaint badgeGradient = new GradientPaint(
            badgeX, badgeY, new Color(255, 200, 60),
            badgeX, badgeY + badgeH, new Color(255, 170, 30)
        );
        g2.setPaint(badgeGradient);
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 14, 14);

        // Badge border
        g2.setColor(new Color(255, 220, 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 14, 14);

        // Badge text
        g2.setFont(badgeFont);
        g2.setColor(new Color(40, 35, 20));
        int bx = badgeX + (badgeW - badgeFM.stringWidth(lvl)) / 2;
        int by = badgeY + ((badgeH - badgeFM.getHeight()) / 2) + badgeFM.getAscent();
        g2.drawString(lvl, bx, by);

        // ==================== DYNAMIC INSTRUCTIONS ====================
        Font instrFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.36f);
        g2.setFont(instrFont);
        g2.setColor(new Color(200, 210, 230));

        String instr;
        if (patternShowing) {
            instr = "Memorize the pattern • Watch the flashing sequence";
        } else if (!patternChecked) {
            instr = "Repeat the sequence using keys 1, 2, 3, 4";
        } else {
            instr = "Press ENTER to continue";
        }
        
        int instrY = innerY + (int)(gp.tileSize * 1.55f);
        g2.drawString(instr, innerX, instrY);

        // ==================== DIVIDER LINE ====================
        int dividerY = instrY + (int)(gp.tileSize * 0.35f);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawLine(innerX, dividerY, innerX + innerW, dividerY);

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) {
            // Display centered cooldown message
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
            g2.setFont(cooldownFont);
            g2.setColor(new Color(240, 100, 100));
            
            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
            g2.drawString(locked, lx, ly);

            // Block all inputs during cooldown
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
            gp.keyH.escapePressed = false;
            return;
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return;
        }

        // ==================== INITIALIZATION ====================
        if (!patternGenerated) {
            
            // ==================== DIFFICULTY SCALING ====================
            // Adjust pattern length and time limit based on level
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

            // Generate random sequence (1-4 for each position)
            patternSequence = new int[patternLength];
            for (int i = 0; i < patternLength; i++) {
                patternSequence[i] = (int)(Math.random() * 4) + 1;
            }

            // Initialize phase states
            patternGenerated = true;
            patternShowing = true;
            patternIndex = 0;
            patternFlashTimer = 0;
            patternGapTimer = 0;
            patternInputIndex = 0;
            patternInputTimerFrames = 0;
            patternChecked = false;
            patternSuccess = false;

            // Clear any pending input
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
        }

        // ==================== GRID LAYOUT SETUP ====================
        int gridSize = gp.tileSize * 4;
        int gridX = panelX + (panelW - gridSize) / 2;
        int gridY = dividerY + (int)(gp.tileSize * 0.6);

        int btnSize = gp.tileSize * 2 - gp.tileSize / 4;
        int gap = gp.tileSize / 4;

        // Calculate button positions (1=top-left, 2=top-right, 3=bottom-left, 4=bottom-right)
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

        // ==================== PATTERN SHOWING PHASE ====================
        int highlight = -1; // Track which button should be highlighted

        if (patternShowing) {
            
            // Start flash if idle
            if (patternFlashTimer <= 0 && patternGapTimer <= 0) {
                patternFlashTimer = patternFlashFrames;
            }

            // Handle flashing animation
            if (patternFlashTimer > 0) {
                highlight = patternSequence[patternIndex];
                patternFlashTimer--;
                
                // Transition to gap after flash completes
                if (patternFlashTimer <= 0) {
                    patternGapTimer = patternGapFrames;
                }
            } 
            // Handle gap between flashes
            else if (patternGapTimer > 0) {
                patternGapTimer--;
                
                if (patternGapTimer <= 0) {
                    patternIndex++;
                    
                    // Move to input phase when sequence complete
                    if (patternIndex >= patternLength) {
                        patternShowing = false;
                        patternIndex = 0;
                        patternInputIndex = 0;
                        patternInputTimerFrames = 0;
                    }
                }
            }
        }

        // ==================== PATTERN INPUT PHASE ====================
        if (!patternShowing && !patternChecked) {
            
            // Increment input timer
            patternInputTimerFrames++;
            
            // Auto-fail if time limit exceeded
            if (patternInputTimerFrames > patternInputLimitFrames) {
                patternChecked = true;
                patternSuccess = false;
            }

            // Process keyboard input (1-4 keys only)
            char typed = gp.keyH.typedChar;
            if (typed != 0) {
                int pressed = -1;
                
                // Map character to button number
                if (typed == '1') pressed = 1;
                else if (typed == '2') pressed = 2;
                else if (typed == '3') pressed = 3;
                else if (typed == '4') pressed = 4;

                // Always consume the typed character
                gp.keyH.typedChar = 0;

                // Validate input if it's a valid button
                if (pressed != -1) {
                    if (pressed == patternSequence[patternInputIndex]) {
                        // Correct input - advance
                        patternInputIndex++;
                        
                        // Check if pattern completed successfully
                        if (patternInputIndex >= patternLength) {
                            patternChecked = true;
                            patternSuccess = true;
                        }
                    } else {
                        // Wrong input - fail immediately
                        patternChecked = true;
                        patternSuccess = false;
                    }
                }
            }
        }

        // ==================== DRAW INTERACTIVE BUTTONS ====================
        for (int i = 1; i <= 4; i++) {
            
            // Button shadow
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(bxPos[i] + 3, byPos[i] + 3, btnSize, btnSize, 18, 18);

            // Base button gradient
            GradientPaint btnGradient = new GradientPaint(
                bxPos[i], byPos[i], new Color(50, 55, 65),
                bxPos[i], byPos[i] + btnSize, new Color(35, 40, 50)
            );
            g2.setPaint(btnGradient);
            g2.fillRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 18, 18);

            // Highlight effect during pattern show phase
            if (patternShowing && highlight == i) {
                // Bright yellow glow overlay
                GradientPaint glowGradient = new GradientPaint(
                    bxPos[i], byPos[i], new Color(255, 230, 120, 150),
                    bxPos[i], byPos[i] + btnSize, new Color(255, 200, 80, 100)
                );
                g2.setPaint(glowGradient);
                g2.fillRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 18, 18);
            }

            // Standard border
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(100, 120, 150, 140));
            g2.drawRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 18, 18);

            // Enhanced border during highlight
            if (patternShowing && highlight == i) {
                g2.setStroke(new BasicStroke(4f));
                g2.setColor(new Color(255, 230, 120));
                g2.drawRoundRect(bxPos[i] - 2, byPos[i] - 2, btnSize + 4, btnSize + 4, 20, 20);
                
                // Outer glow ring
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(255, 230, 120, 80));
                g2.drawRoundRect(bxPos[i] - 5, byPos[i] - 5, btnSize + 10, btnSize + 10, 23, 23);
            }

            // Button number label
            Font numFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 1.0f);
            g2.setFont(numFont);
            
            Color numColor = (patternShowing && highlight == i) ? 
                new Color(40, 35, 20) : new Color(230, 240, 255);
            g2.setColor(numColor);
            
            String n = String.valueOf(i);
            FontMetrics nfm = g2.getFontMetrics();
            int nx = bxPos[i] + (btnSize - nfm.stringWidth(n)) / 2;
            int ny = byPos[i] + (btnSize - nfm.getHeight()) / 2 + nfm.getAscent();
            g2.drawString(n, nx, ny);
        }

        // ==================== TIMER DISPLAY (INPUT PHASE ONLY) ====================
        if (!patternShowing && !patternChecked) {
            Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f);
            g2.setFont(timerFont);
            
            // Calculate remaining time
            int secLeft = (patternInputLimitFrames - patternInputTimerFrames + 59) / 60;
            if (secLeft < 0) secLeft = 0;

            // Color-code timer based on remaining time
            Color timerColor;
            if (secLeft > 3) {
                timerColor = new Color(180, 220, 180);
            } else if (secLeft > 1) {
                timerColor = new Color(240, 200, 100);
            } else {
                timerColor = new Color(240, 120, 120);
            }
            
            g2.setColor(timerColor);
            String timeText = "⏱ " + secLeft + "s";
            FontMetrics tfm = g2.getFontMetrics();
            int tx = panelX + panelW - pad - tfm.stringWidth(timeText);
            int ty = titleY;
            g2.drawString(timeText, tx, ty);

            // Timer progress bar
            int barW = tfm.stringWidth(timeText);
            int barH = Math.max(6, tfm.getHeight() / 5);
            int barX = tx;
            int barY = ty + 6;

            // Calculate fill ratio
            float ratio = 1f;
            if (patternInputLimitFrames > 0) {
                ratio = 1f - ((float)patternInputTimerFrames / (float)patternInputLimitFrames);
            }
            ratio = Math.max(0f, Math.min(1f, ratio));

            // Bar background
            g2.setColor(new Color(20, 25, 30, 200));
            g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

            // Animated fill with gradient
            int fillW = Math.max(2, (int)(barW * ratio));
            GradientPaint barGradient = new GradientPaint(
                barX, barY, timerColor,
                barX + fillW, barY, new Color(timerColor.getRed(), 
                                              timerColor.getGreen(), 
                                              timerColor.getBlue(), 150)
            );
            g2.setPaint(barGradient);
            g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);

            // Bar border
            g2.setColor(new Color(255, 255, 255, 50));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(barX, barY, barW, barH, barH, barH);
        }

        // ==================== PROGRESS INDICATOR DOTS ====================
        int dotsY = gridY + gridSize + (int)(gp.tileSize * 0.4);
        int dotSize = gp.tileSize / 5;
        int dotGap = 8;
        int totalDotsW = patternLength * dotSize + (patternLength - 1) * dotGap;
        int dotsX = panelX + (panelW - totalDotsW) / 2;

        for (int i = 0; i < patternLength; i++) {
            int dx = dotsX + i * (dotSize + dotGap);
            
            // Determine dot state
            boolean filled = (!patternShowing && i < patternInputIndex);
            
            // Dot shadow
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(dx + 2, dotsY + 2, dotSize, dotSize);
            
            // Dot fill
            if (filled) {
                g2.setColor(new Color(120, 220, 140));
            } else {
                g2.setColor(new Color(80, 85, 95));
            }
            g2.fillOval(dx, dotsY, dotSize, dotSize);
            
            // Dot border
            g2.setColor(filled ? new Color(150, 240, 170) : new Color(100, 110, 125, 120));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(dx, dotsY, dotSize, dotSize);
        }

        // ==================== FEEDBACK DISPLAY ====================
        if (patternChecked) {
            
            Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.65f);
            g2.setFont(feedbackFont);

            String feedback = patternSuccess ? "✔ Perfect Match!" : "✖ Pattern Failed";
            Color feedbackColor = patternSuccess ? new Color(120, 220, 140) : new Color(240, 120, 120);
            
            int fbx = panelX + (panelW - g2.getFontMetrics().stringWidth(feedback)) / 2;
            int fby = panelY + panelH - (int)(gp.tileSize * 1.8);

            // Feedback glow
            g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
                                 feedbackColor.getBlue(), 80));
            g2.drawString(feedback, fbx + 2, fby + 2);
            
            g2.setColor(feedbackColor);
            g2.drawString(feedback, fbx, fby);

            // Continue prompt
            Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f);
            g2.setFont(hintFont);
            g2.setColor(new Color(200, 210, 230, 200));

            String hint = "Press ENTER to continue";
            int hx = panelX + (panelW - g2.getFontMetrics().stringWidth(hint)) / 2;
            g2.drawString(hint, hx, fby + (int)(gp.tileSize * 0.6));

            // Handle continuation
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
            // ==================== BOTTOM HELP TEXT ====================
            String hint = "Watch carefully • Press keys 1-4 in order • ESC to exit";
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f));
            g2.setColor(new Color(180, 190, 210, 180));
            FontMetrics hfm = g2.getFontMetrics();
            int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2;
            g2.drawString(hint, hx, panelY + panelH - pad / 2);
        }
    }
 // ------------------------ DRAW VAULT SEQUENCE TASK ------------------------
 	public void drawVaultSequenceTask() {

 	    // Cooldown block (MATCHES other tasks)
 	    if (taskCooldownFrames > 0) {

 	        // Rendering hints (keep consistent)
 	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

 	        // Dim background with smoother fade
 	        g2.setColor(new Color(0, 0, 0, 180));
 	        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

 	        // Panel size (same as Riddle/Vault panel)
 	        int panelW = gp.tileSize * 10;
 	        int panelH = gp.tileSize * 6;
 	        int panelX = (gp.screenWidth - panelW) / 2;
 	        int panelY = (gp.screenHeight - panelH) / 2;
 	        int arc = 30;

 	        // Enhanced shadow with gradient effect
 	        g2.setColor(new Color(0, 0, 0, 140));
 	        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
 	        g2.setColor(new Color(0, 0, 0, 80));
 	        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);
 	        
 	        // Modern dark background
 	        g2.setColor(new Color(25, 28, 35, 250));
 	        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

 	        // Centered cooldown text
 	        Font big = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
 	        g2.setFont(big);
 	        g2.setColor(new Color(220, 80, 80));

 	        String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
 	        int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
 	        int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
 	        g2.drawString(locked, lx, ly);

 	        // Eat input so nothing types during cooldown
 	        gp.keyH.typedChar = 0;
 	        gp.keyH.backspacePressed = false;
 	        gp.keyH.enterPressed = false;
 	        gp.keyH.escapePressed = false;

 	        return;
 	    }

 	    // Rendering hints
 	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

 	    // Dim background with smoother fade
 	    g2.setColor(new Color(0, 0, 0, 180));
 	    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

 	    // Panel dimensions
 	    int panelW = gp.tileSize * 10;
 	    int panelH = gp.tileSize * 6;
 	    int panelX = (gp.screenWidth - panelW) / 2;
 	    int panelY = (gp.screenHeight - panelH) / 2;
 	    int arc = 30;

 	    // Enhanced shadow with gradient effect
 	    g2.setColor(new Color(0, 0, 0, 140));
 	    g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
 	    g2.setColor(new Color(0, 0, 0, 80));
 	    g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);
 	    
 	    // Modern dark background
 	    g2.setColor(new Color(25, 28, 35, 250));
 	    g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

 	    // Accent border
 	    g2.setColor(new Color(100, 120, 200, 100));
 	    g2.setStroke(new BasicStroke(2f));
 	    g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc);

 	    // Title with glow effect
 	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.85f));
 	    String title = "Vault Sequence";
 	    int titleX = panelX + gp.tileSize/2;
 	    int titleY = panelY + (int)(gp.tileSize * 0.9);
 	    
 	    // Title glow
 	    g2.setColor(new Color(100, 120, 200, 80));
 	    g2.drawString(title, titleX + 1, titleY + 1);
 	    g2.setColor(new Color(230, 240, 255));
 	    g2.drawString(title, titleX, titleY);

 	    // Generate vault once
 	    if (!vaultGenerated) {
 	        vaultGenerated = true;

 	        // time limit
 	        vaultTimeLimitFrames = 45 * 60;
 	        vaultTimerFrames = vaultTimeLimitFrames;

 	        // Initialize wrong answer counter
 	        vaultWrongAnswers = 0;

 	        // pick 4 UNIQUE riddles from your pool
 	        java.util.HashSet<Integer> used = new java.util.HashSet<>();
 	        for (int i = 0; i < 4; i++) {
 	            int idx;
 	            do {
 	                idx = (int)(Math.random() * RIDDLE_QUESTIONS.length);
 	            } while (used.contains(idx));
 	            used.add(idx);

 	            vaultRiddleQ[i] = RIDDLE_QUESTIONS[idx];
 	            vaultRiddleA[i] = RIDDLE_ANSWERS[idx];

 	            // assign a random digit 0-9 for each riddle
 	            vaultDigits[i] = (int)(Math.random() * 10);

 	            vaultInputs[i] = "";
 	            vaultSolved[i] = false;
 	        }

 	        vaultIndex = 0;
 	        vaultEnteringCode = false;
 	        vaultFinalInput = "";

 	        vaultFeedback = "";
 	        vaultFeedbackFrames = 0;
 	    }

 	    // Escape abort
 	    if (gp.keyH.escapePressed) {
 	        gp.keyH.escapePressed = false;
 	        resetAllTaskState();
 	        gp.gameState = gp.playState;
 	        return;
 	    }

 	    // Timer tick
 	    if (vaultTimerFrames > 0) vaultTimerFrames--;
 	    if (vaultTimerFrames <= 0) {
 	        handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Vault failed. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
 	        return;
 	    }

 	    // Time display - top right with modern styling
 	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f));
 	    int timeSeconds = (vaultTimerFrames + 59) / 60;
 	    Color timeColor = timeSeconds <= 10 ? new Color(240, 100, 100) : new Color(180, 220, 180);
 	    g2.setColor(timeColor);
 	    String timeText = "⏱ " + timeSeconds + "s";
 	    int tW = g2.getFontMetrics().stringWidth(timeText);
 	    g2.drawString(timeText, panelX + panelW - gp.tileSize/2 - tW, panelY + (int)(gp.tileSize * 0.9));

 	    // Progress indicators with visual styling
 	    int solvedCount = 0;
 	    for (boolean b : vaultSolved) if (b) solvedCount++;

 	    int progressY = panelY + (int)(gp.tileSize * 1.4);
 	    
 	    // Riddle progress
 	    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f));
 	    g2.setColor(new Color(180, 200, 230));
 	    g2.drawString("Riddle: " + (vaultIndex + 1) + " / 4", panelX + gp.tileSize/2, progressY);
 	    
 	    // Solved count with color coding
 	    progressY += (int)(gp.tileSize * 0.4);
 	    Color solvedColor = solvedCount == 4 ? new Color(120, 220, 140) : new Color(200, 200, 200);
 	    g2.setColor(solvedColor);
 	    g2.drawString("Solved: " + solvedCount + " / 4", panelX + gp.tileSize/2, progressY);
 	    
 	    // Wrong answers indicator
 	    progressY += (int)(gp.tileSize * 0.4);
 	    Color wrongColor = vaultWrongAnswers == 1 ? new Color(240, 180, 100) : 
 	                       vaultWrongAnswers >= 2 ? new Color(240, 100, 100) : new Color(200, 200, 200);
 	    g2.setColor(wrongColor);
 	    g2.drawString("Mistakes: " + vaultWrongAnswers + " / 2", panelX + gp.tileSize/2, progressY);

 	    // If feedback is active, show it and freeze input briefly
 	    if (vaultFeedbackFrames > 0) {
 	        vaultFeedbackFrames--;

 	        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.42f));
 	        Color feedbackColor = vaultFeedback.startsWith("✔") ? new Color(120, 220, 140) : 
 	                             new Color(240, 120, 120);
 	        
 	        // Feedback glow effect
 	        g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
 	                             feedbackColor.getBlue(), 60));
 	        int fW = g2.getFontMetrics().stringWidth(vaultFeedback);
 	        int fX = panelX + (panelW - fW)/2;
 	        int fY = panelY + (int)(gp.tileSize * 3.0);
 	        g2.drawString(vaultFeedback, fX + 2, fY + 2);
 	        
 	        g2.setColor(feedbackColor);
 	        g2.drawString(vaultFeedback, fX, fY);

 	        // eat input while feedback is showing
 	        gp.keyH.typedChar = 0;
 	        gp.keyH.backspacePressed = false;
 	        gp.keyH.enterPressed = false;
 	        return;
 	    }

 	    // =========================
 	    // STAGE A: Solve 4 riddles
 	    // =========================
 	    if (!vaultEnteringCode) {

 	        // Draw current riddle question with better spacing
 	        int qX = panelX + gp.tileSize/2;
 	        int qY = panelY + (int)(gp.tileSize * 2.6);
 	        int qWArea = panelW - gp.tileSize;

 	        Font qFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.42f);
 	        g2.setFont(qFont);
 	        g2.setColor(new Color(240, 245, 255));

 	        java.util.List<String> qLines = wrapText(vaultRiddleQ[vaultIndex], g2.getFontMetrics(), qWArea);
 	        int lineH = g2.getFontMetrics().getHeight();
 	        int drawY = qY;
 	        for (String line : qLines) {
 	            g2.drawString(line, qX, drawY);
 	            drawY += lineH;
 	        }

 	        // Modern input box with gradient
 	        int boxW = panelW - gp.tileSize;
 	        int boxH = (int)(gp.tileSize * 1.1);
 	        int boxX = panelX + gp.tileSize/2;
 	        int boxY = panelY + panelH - (int)(gp.tileSize * 1.6);

 	        // Input box gradient background
 	        GradientPaint gradient = new GradientPaint(
 	            boxX, boxY, new Color(245, 248, 255),
 	            boxX, boxY + boxH, new Color(235, 240, 250)
 	        );
 	        g2.setPaint(gradient);
 	        g2.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16);
 	        
 	        // Input box border with accent
 	        g2.setColor(new Color(100, 120, 200, 120));
 	        g2.setStroke(new BasicStroke(2.5f));
 	        g2.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16);

 	        // Show typed input
 	        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.50f));
 	        g2.setColor(new Color(30, 35, 45));

 	        String curInput = vaultInputs[vaultIndex];
 	        if (curInput.isEmpty()) {
 	            g2.setColor(new Color(150, 160, 180));
 	            curInput = "Type your answer...";
 	        }
 	        
 	        FontMetrics ifm = g2.getFontMetrics();
 	        int itx = boxX + gp.tileSize/3;
 	        int ity = boxY + ((boxH - ifm.getHeight()) / 2) + ifm.getAscent();
 	        g2.drawString(curInput, itx, ity);

 	        // Handle typing
 	        char typed = gp.keyH.typedChar;
 	        if (typed != 0) {
 	            if (Character.isLetterOrDigit(typed) || Character.isWhitespace(typed) || isPunctuation(typed)) {
 	                vaultInputs[vaultIndex] += typed;
 	            }
 	            gp.keyH.typedChar = 0;
 	        }

 	        if (gp.keyH.backspacePressed) {
 	            if (vaultInputs[vaultIndex].length() > 0) {
 	                vaultInputs[vaultIndex] = vaultInputs[vaultIndex].substring(0, vaultInputs[vaultIndex].length() - 1);
 	            }
 	            gp.keyH.backspacePressed = false;
 	        }

 	        // Submit with ENTER
 	        if (gp.keyH.enterPressed) {
 	            gp.keyH.enterPressed = false;

 	            String user = normalizeAnswer(vaultInputs[vaultIndex]);
 	            String correct = normalizeAnswer(vaultRiddleA[vaultIndex]);

 	            if (user.equals(correct)) {
 	                vaultSolved[vaultIndex] = true;
 	                vaultFeedback = "✔ Correct! Digit: " + vaultDigits[vaultIndex];
 	                vaultFeedbackFrames = 90;

 	                // move to next unsolved riddle
 	                int next = -1;
 	                for (int i = 0; i < 4; i++) {
 	                    if (!vaultSolved[i]) { next = i; break; }
 	                }

 	                if (next == -1) {
 	                    // all solved -> move to code entry
 	                    vaultEnteringCode = true;
 	                } else {
 	                    vaultIndex = next;
 	                }
 	            } else {
 	                // Wrong answer - increment counter
 	                vaultWrongAnswers++;
 	                
 	                if (vaultWrongAnswers >= 2) {
 	                    // Failed - 2 wrong answers
 	                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Too many mistakes. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
 	                    return;
 	                } else {
 	                    // Show feedback and advance to next riddle
 	                    vaultFeedback = "✖ Wrong. Moving to next riddle...";
 	                    vaultFeedbackFrames = 90;
 	                    
 	                    // Find next unsolved riddle
 	                    int next = -1;
 	                    for (int i = 0; i < 4; i++) {
 	                        if (!vaultSolved[i] && i != vaultIndex) { 
 	                            next = i; 
 	                            break; 
 	                        }
 	                    }
 	                    
 	                    // If no other unsolved riddles, stay on current
 	                    if (next != -1) {
 	                        vaultIndex = next;
 	                    }
 	                }
 	            }
 	        }

 	        // Helper text with modern styling
 	        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f));
 	        g2.setColor(new Color(180, 190, 210));
 	        String helpText = "Type answer and press ENTER  •  ESC to exit";
 	        int helpW = g2.getFontMetrics().stringWidth(helpText);
 	        g2.drawString(helpText, panelX + (panelW - helpW)/2, panelY + panelH - gp.tileSize/5);

 	        return;
 	    }

 	    // =========================
 	    // STAGE B: Enter final code
 	    // =========================
 	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f));
 	    g2.setColor(new Color(230, 240, 255));

 	    String prompt = "Enter the 4-digit code:";
 	    int promptW = g2.getFontMetrics().stringWidth(prompt);
 	    g2.drawString(prompt, panelX + (panelW - promptW)/2, panelY + (int)(gp.tileSize * 2.7));

 	    // show earned digits with visual styling
 	    String digitsLine = vaultDigits[0] + "  " + vaultDigits[1] + "  " + vaultDigits[2] + "  " + vaultDigits[3];
 	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.55f));
 	    g2.setColor(new Color(120, 220, 140));
 	    int digitsW = g2.getFontMetrics().stringWidth(digitsLine);
 	    g2.drawString(digitsLine, panelX + (panelW - digitsW)/2, panelY + (int)(gp.tileSize * 3.4));

 	    // Final input box with enhanced styling
 	    int boxW = (int)(gp.tileSize * 4.5);
 	    int boxH = (int)(gp.tileSize * 1.2);
 	    int boxX = panelX + (panelW - boxW)/2;
 	    int boxY = panelY + panelH - (int)(gp.tileSize * 2.0);

 	    // Gradient background
 	    GradientPaint gradient = new GradientPaint(
 	        boxX, boxY, new Color(245, 248, 255),
 	        boxX, boxY + boxH, new Color(235, 240, 250)
 	    );
 	    g2.setPaint(gradient);
 	    g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);
 	    
 	    g2.setColor(new Color(100, 120, 200, 140));
 	    g2.setStroke(new BasicStroke(3f));
 	    g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);

 	    // input text - centered
 	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.75f));
 	    g2.setColor(new Color(30, 35, 45));
 	    FontMetrics fm = g2.getFontMetrics();

 	    String displayCode = vaultFinalInput.isEmpty() ? "____" : vaultFinalInput;
 	    int codeW = fm.stringWidth(displayCode);
 	    int itx = boxX + (boxW - codeW)/2;
 	    int ity = boxY + ((boxH - fm.getHeight()) / 2) + fm.getAscent();
 	    g2.drawString(displayCode, itx, ity);

 	    // typing: digits only, max 4
 	    char typed = gp.keyH.typedChar;
 	    if (typed != 0) {
 	        if (Character.isDigit(typed) && vaultFinalInput.length() < 4) {
 	            vaultFinalInput += typed;
 	        }
 	        gp.keyH.typedChar = 0;
 	    }

 	    if (gp.keyH.backspacePressed) {
 	        if (vaultFinalInput.length() > 0) {
 	            vaultFinalInput = vaultFinalInput.substring(0, vaultFinalInput.length() - 1);
 	        }
 	        gp.keyH.backspacePressed = false;
 	    }

 	    // submit final code
 	    if (gp.keyH.enterPressed) {
 	        gp.keyH.enterPressed = false;

 	        String correctCode = "" + vaultDigits[0] + vaultDigits[1] + vaultDigits[2] + vaultDigits[3];

 	        if (vaultFinalInput.equals(correctCode)) {
 	            handleTaskSuccess("Vault unlocked!");
 	        } else {
 	            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Wrong code. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
 	        }
 	        return;
 	    }

 	    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f));
 	    g2.setColor(new Color(180, 190, 210));
 	    String helpText = "Enter 4 digits then press ENTER  •  ESC to exit";
 	    int helpW = g2.getFontMetrics().stringWidth(helpText);
 	    g2.drawString(helpText, panelX + (panelW - helpW)/2, panelY + panelH - gp.tileSize/5);
 	}

 	public void drawLogicPanelTask() {

 	    // ==================== RENDERING SETUP ====================
 	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

 	    // Dim background overlay
 	    g2.setColor(new Color(0, 0, 0, 180));
 	    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

 	    // ==================== PANEL DIMENSIONS ====================
 	    int panelW = gp.tileSize * 12;
 	    int panelH = gp.tileSize * 9;
 	    int panelX = (gp.screenWidth - panelW) / 2;
 	    int panelY = (gp.screenHeight - panelH) / 2;
 	    int arc = 30;
 	    int pad = gp.tileSize / 3;

 	    // ==================== PANEL VISUAL EFFECTS ====================
 	    // Multi-layered shadow for depth
 	    g2.setColor(new Color(0, 0, 0, 140));
 	    g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
 	    g2.setColor(new Color(0, 0, 0, 80));
 	    g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);

 	    // Dark tech panel gradient
 	    GradientPaint bgGradient = new GradientPaint(
 	        panelX, panelY, new Color(30, 35, 45),
 	        panelX, panelY + panelH, new Color(20, 25, 35)
 	    );
 	    g2.setPaint(bgGradient);
 	    g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

 	    // Accent border (green tech theme)
 	    g2.setColor(new Color(100, 255, 150, 120));
 	    g2.setStroke(new BasicStroke(2.5f));
 	    g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc);

 	    // ==================== RED FLASH EFFECT ====================
 	    if (logicFlashFrames > 0) {
 	        logicFlashFrames--;
 	        int alpha = (int)(200 * (logicFlashFrames / 30.0));
 	        g2.setColor(new Color(255, 50, 50, Math.max(0, Math.min(255, alpha))));
 	        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);
 	    }

 	    // ==================== COOLDOWN STATE ====================
 	    if (taskCooldownFrames > 0) {
 	        Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
 	        g2.setFont(cooldownFont);
 	        g2.setColor(new Color(240, 100, 100));
 	        
 	        String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
 	        int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
 	        int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
 	        g2.drawString(locked, lx, ly);

 	        gp.mouseClicked = false;
 	        gp.keyH.typedChar = 0;
 	        gp.keyH.backspacePressed = false;
 	        gp.keyH.enterPressed = false;
 	        gp.keyH.escapePressed = false;
 	        return;
 	    }

 	    // ==================== ESCAPE TO EXIT ====================
 	    if (gp.keyH.escapePressed) {
 	        gp.keyH.escapePressed = false;
 	        resetAllTaskState();
 	        gp.gameState = gp.playState;
 	        return;
 	    }

 	    // ==================== TITLE SECTION ====================
 	    String title = "⚙ Logic Panel";
 	    Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.90f);
 	    g2.setFont(titleFont);
 	    
 	    int titleY = panelY + (int)(gp.tileSize * 0.85);
 	    
 	    // Title shadow
 	    g2.setColor(new Color(0, 0, 0, 100));
 	    g2.drawString(title, panelX + pad + 2, titleY + 2);
 	    
 	    // Title with tech green color
 	    g2.setColor(new Color(180, 255, 200));
 	    g2.drawString(title, panelX + pad, titleY);

 	    // ==================== DIVIDER LINE ====================
 	    int dividerY = titleY + (int)(gp.tileSize * 0.4);
 	    g2.setStroke(new BasicStroke(1.5f));
 	    g2.setColor(new Color(100, 255, 150, 60));
 	    g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY);

 	    // ==================== INITIALIZATION ====================
 	    if (!logicGenerated) {
 	        // Pick 6 random unique statements
 	        java.util.List<Integer> availableIndices = new java.util.ArrayList<>();
 	        for (int i = 0; i < LOGIC_STATEMENTS.length; i++) {
 	            availableIndices.add(i);
 	        }
 	        java.util.Collections.shuffle(availableIndices);
 	        
 	        for (int i = 0; i < logicStatementCount; i++) {
 	            int idx = availableIndices.get(i);
 	            logicStatements[i] = LOGIC_STATEMENTS[idx][0];
 	            logicCorrectAnswers[i] = LOGIC_STATEMENTS[idx][1].equals("true");
 	            logicPlayerAnswers[i] = -1; // Unset
 	            logicTrueSwitches[i] = new Rectangle();
 	            logicFalseSwitches[i] = new Rectangle();
 	        }

 	        // Set timer based on level
 	        if (gp.level <= 1) {
 	            logicTimeLimitFrames = 90 * 60; // 90s
 	        } else if (gp.level == 2) {
 	            logicTimeLimitFrames = 75 * 60; // 75s
 	        } else if (gp.level == 3) {
 	            logicTimeLimitFrames = 60 * 60; // 60s
 	        } else {
 	            logicTimeLimitFrames = 50 * 60; // 50s
 	        }
 	        
 	        logicTimerFrames = logicTimeLimitFrames;
 	        logicFlashFrames = 0;
 	        logicGenerated = true;
 	    }

 	    // ==================== TIMER TICK ====================
 	    if (logicTimerFrames > 0) {
 	        logicTimerFrames--;
 	    }
 	    
 	    if (logicTimerFrames <= 0) {
 	        handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, 
 	            "Time's up! Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
 	        return;
 	    }

 	    // ==================== TIMER DISPLAY ====================
 	    Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f);
 	    g2.setFont(timerFont);
 	    
 	    int timeSeconds = (logicTimerFrames + 59) / 60;
 	    Color timerColor = timeSeconds <= 15 ? new Color(240, 100, 100) : 
 	                       timeSeconds <= 30 ? new Color(240, 200, 100) :
 	                       new Color(180, 220, 180);
 	    
 	    g2.setColor(timerColor);
 	    String timeText = "⏱ " + timeSeconds + "s";
 	    int tW = g2.getFontMetrics().stringWidth(timeText);
 	    g2.drawString(timeText, panelX + panelW - pad - tW, titleY);

 	    // Timer bar
 	    int barW = tW;
 	    int barH = g2.getFontMetrics().getHeight() / 5;
 	    int barX = panelX + panelW - pad - tW;
 	    int barY = titleY + 6;

 	    float ratio = (float)logicTimerFrames / (float)logicTimeLimitFrames;
 	    ratio = Math.max(0f, Math.min(1f, ratio));

 	    g2.setColor(new Color(20, 25, 30, 200));
 	    g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

 	    int fillW = Math.max(2, (int)(barW * ratio));
 	    GradientPaint barGradient = new GradientPaint(
 	        barX, barY, timerColor,
 	        barX + fillW, barY, new Color(timerColor.getRed(), 
 	                                      timerColor.getGreen(), 
 	                                      timerColor.getBlue(), 150)
 	    );
 	    g2.setPaint(barGradient);
 	    g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);

 	    g2.setColor(new Color(255, 255, 255, 50));
 	    g2.setStroke(new BasicStroke(1f));
 	    g2.drawRoundRect(barX, barY, barW, barH, barH, barH);

 	    // ==================== PROGRESS DISPLAY ====================
 	    int answeredCount = 0;
 	    for (int i = 0; i < logicStatementCount; i++) {
 	        if (logicPlayerAnswers[i] != -1) answeredCount++;
 	    }
 	    
 	    Font progressFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.35f);
 	    g2.setFont(progressFont);
 	    
 	    Color progressColor = answeredCount == 6 ? new Color(120, 220, 140) : 
 	                         new Color(180, 200, 230);
 	    g2.setColor(progressColor);
 	    
 	    String progressText = "Answered: " + answeredCount + " / 6";
 	    g2.drawString(progressText, panelX + pad, titleY);

 	    // ==================== STATEMENT ROWS ====================
 	    int rowHeight = (int)(gp.tileSize * 1.1);
 	    int rowGap = (int)(gp.tileSize * 0.1);
 	    int startY = dividerY + (int)(gp.tileSize * 0.5);
 	    
 	    Font statementFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.30f);
 	    g2.setFont(statementFont);

 	    for (int i = 0; i < logicStatementCount; i++) {
 	        int rowY = startY + i * (rowHeight + rowGap);
 	        int rowX = panelX + pad;
 	        int rowW = panelW - pad * 2;

 	        // Row background
 	        g2.setColor(new Color(40, 45, 55, 200));
 	        g2.fillRoundRect(rowX, rowY, rowW, rowHeight, 12, 12);
 	        
 	        // Row border
 	        g2.setColor(new Color(100, 255, 150, 40));
 	        g2.setStroke(new BasicStroke(1.5f));
 	        g2.drawRoundRect(rowX, rowY, rowW, rowHeight, 12, 12);

 	        // Statement text
 	        g2.setColor(new Color(220, 230, 240));
 	        String statement = logicStatements[i];
 	        
 	        // Wrap text if too long
 	        int maxWidth = rowW - (int)(gp.tileSize * 3.5);
 	        java.util.List<String> lines = wrapText(statement, g2.getFontMetrics(), maxWidth);
 	        
 	        int textY = rowY + (rowHeight - lines.size() * g2.getFontMetrics().getHeight()) / 2 + 
 	                    g2.getFontMetrics().getAscent();
 	        int textX = rowX + (int)(gp.tileSize * 0.3);
 	        
 	        for (String line : lines) {
 	            g2.drawString(line, textX, textY);
 	            textY += g2.getFontMetrics().getHeight();
 	        }

 	        // ==================== TOGGLE SWITCHES ====================
 	        int switchW = (int)(gp.tileSize * 1.2);
 	        int switchH = (int)(gp.tileSize * 0.6);
 	        int switchGap = (int)(gp.tileSize * 0.2);
 	        
 	        int switchesX = rowX + rowW - (switchW * 2 + switchGap) - (int)(gp.tileSize * 0.3);
 	        int switchY = rowY + (rowHeight - switchH) / 2;

 	        // FALSE switch (left)
 	        logicFalseSwitches[i].setBounds(switchesX, switchY, switchW, switchH);
 	        
 	        boolean falseSelected = (logicPlayerAnswers[i] == 0);
 	        
 	        // Switch background
 	        Color falseBg = falseSelected ? new Color(240, 100, 100, 200) : 
 	                                       new Color(60, 65, 75, 200);
 	        g2.setColor(falseBg);
 	        g2.fillRoundRect(switchesX, switchY, switchW, switchH, 10, 10);
 	        
 	        // Switch border
 	        g2.setColor(falseSelected ? new Color(255, 150, 150) : new Color(100, 110, 120));
 	        g2.setStroke(new BasicStroke(falseSelected ? 2.5f : 1.5f));
 	        g2.drawRoundRect(switchesX, switchY, switchW, switchH, 10, 10);
 	        
 	        // Text
 	        Font switchFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.28f);
 	        g2.setFont(switchFont);
 	        g2.setColor(falseSelected ? Color.white : new Color(180, 185, 190));
 	        String falseText = "FALSE";
 	        int ftx = switchesX + (switchW - g2.getFontMetrics().stringWidth(falseText)) / 2;
 	        int fty = switchY + (switchH - g2.getFontMetrics().getHeight()) / 2 + 
 	                  g2.getFontMetrics().getAscent();
 	        g2.drawString(falseText, ftx, fty);

 	        // TRUE switch (right)
 	        int trueX = switchesX + switchW + switchGap;
 	        logicTrueSwitches[i].setBounds(trueX, switchY, switchW, switchH);
 	        
 	        boolean trueSelected = (logicPlayerAnswers[i] == 1);
 	        
 	        // Switch background
 	        Color trueBg = trueSelected ? new Color(100, 200, 120, 200) : 
 	                                     new Color(60, 65, 75, 200);
 	        g2.setColor(trueBg);
 	        g2.fillRoundRect(trueX, switchY, switchW, switchH, 10, 10);
 	        
 	        // Switch border
 	        g2.setColor(trueSelected ? new Color(150, 255, 180) : new Color(100, 110, 120));
 	        g2.setStroke(new BasicStroke(trueSelected ? 2.5f : 1.5f));
 	        g2.drawRoundRect(trueX, switchY, switchW, switchH, 10, 10);
 	        
 	        // Text
 	        g2.setFont(switchFont);
 	        g2.setColor(trueSelected ? Color.white : new Color(180, 185, 190));
 	        String trueText = "TRUE";
 	        int ttx = trueX + (switchW - g2.getFontMetrics().stringWidth(trueText)) / 2;
 	        int tty = switchY + (switchH - g2.getFontMetrics().getHeight()) / 2 + 
 	                  g2.getFontMetrics().getAscent();
 	        g2.drawString(trueText, ttx, tty);
 	    }

 	    // ==================== INPUT HANDLING ====================
 	    // NOTE: previously the code unconditionally cleared gp.mouseClicked when entering this block,
 	    // which prevented the submit button from ever seeing the click. We now only consume the
 	    // click if it actually hit one of the TRUE/FALSE switches. If it didn't, we leave the
 	    // click state intact so the submit button can handle it below.
 	    if (gp.mouseClicked) {
 	        boolean consumed = false;
 	        for (int i = 0; i < logicStatementCount; i++) {
 	            // Check TRUE switch
 	            if (logicTrueSwitches[i].contains(gp.mouseX, gp.mouseY)) {
 	                logicPlayerAnswers[i] = 1;
 	                consumed = true;
 	                break;
 	            }

 	            // Check FALSE switch
 	            if (logicFalseSwitches[i].contains(gp.mouseX, gp.mouseY)) {
 	                logicPlayerAnswers[i] = 0;
 	                consumed = true;
 	                break;
 	            }
 	        }

 	        if (consumed) {
 	            gp.mouseClicked = false; // consume click when it actually hit a switch
 	            return; // return early to avoid also triggering submit on the same frame
 	        }
 	        // if not consumed, leave gp.mouseClicked true so submit handling below can detect it
 	    }

 	    // ==================== SUBMIT BUTTON ====================
 	    int submitW = (int)(gp.tileSize * 4);
 	    int submitH = (int)(gp.tileSize * 0.8);
 	    int submitX = panelX + (panelW - submitW) / 2;
 	    int submitY = startY + 6 * (rowHeight + rowGap) + (int)(gp.tileSize * 0.3);

 	    Rectangle submitButton = new Rectangle(submitX, submitY, submitW, submitH);

 	    // Check if all answered
 	    boolean allAnswered = (answeredCount == 6);

 	    // Button background
 	    Color submitBg = allAnswered ? new Color(100, 200, 120, 220) : 
 	                                  new Color(80, 85, 95, 180);
 	    g2.setColor(submitBg);
 	    g2.fillRoundRect(submitX, submitY, submitW, submitH, 14, 14);

 	    // Button border
 	    g2.setColor(allAnswered ? new Color(150, 255, 180) : new Color(100, 110, 120));
 	    g2.setStroke(new BasicStroke(2f));
 	    g2.drawRoundRect(submitX, submitY, submitW, submitH, 14, 14);

 	    // Button text
 	    Font submitFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f);
 	    g2.setFont(submitFont);
 	    g2.setColor(allAnswered ? Color.white : new Color(140, 145, 150));
 	    String submitText = "SUBMIT";
 	    int stx = submitX + (submitW - g2.getFontMetrics().stringWidth(submitText)) / 2;
 	    int sty = submitY + (submitH - g2.getFontMetrics().getHeight()) / 2 + 
 	              g2.getFontMetrics().getAscent();
 	    g2.drawString(submitText, stx, sty);

 	    // ==================== SUBMIT HANDLING ====================
 	    // Handle submit - note gp.mouseClicked may still be true here if the click didn't hit switches
 	    if (allAnswered && gp.mouseClicked) {
 	        if (submitButton.contains(gp.mouseX, gp.mouseY)) {
 	            gp.mouseClicked = false; // Consume click
 	            
 	            // Check if all answers are correct
 	            boolean allCorrect = true;
 	            for (int i = 0; i < logicStatementCount; i++) {
 	                boolean playerSaysTrue = (logicPlayerAnswers[i] == 1);
 	                if (playerSaysTrue != logicCorrectAnswers[i]) {
 	                    allCorrect = false;
 	                    break;
 	                }
 	            }

 	            if (allCorrect) {
 	                handleTaskSuccess("Logic panel verified!");
 	            } else {
 	                // Show red flash and fail
 	                logicFlashFrames = 30;
 	                handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
 	                    "One or more answers are incorrect. Try again in " + 
 	                    DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
 	            }
 	            return; // Exit to prevent further processing
 	        }
 	    }

 	    // ==================== INSTRUCTIONS ====================
 	    Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.28f);
 	    g2.setFont(hintFont);
 	    g2.setColor(new Color(180, 190, 210, 180));
 	    
 	    String hint = allAnswered ? "Click SUBMIT to verify all answers" :
 	                               "Click TRUE or FALSE for each statement";
 	    
 	    int hw = g2.getFontMetrics().stringWidth(hint);
 	    g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 3);
 	}

	 // ==================== DRAW FUSE REPAIR TASK ====================
	 public void drawFuseRepairTask() {
	
	     // ==================== RENDERING SETUP ====================
	     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	     g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
	     // Dim background overlay
	     g2.setColor(new Color(0, 0, 0, 180));
	     g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
	
	     // ==================== PANEL DIMENSIONS ====================
	     int panelW = gp.tileSize * 11;
	     int panelH = gp.tileSize * 8;
	     int panelX = (gp.screenWidth - panelW) / 2;
	     int panelY = (gp.screenHeight - panelH) / 2;
	     int arc = 30;
	     int pad = gp.tileSize / 3;
	
	     // ==================== PANEL VISUAL EFFECTS ====================
	     // Multi-layered shadow for depth
	     g2.setColor(new Color(0, 0, 0, 140));
	     g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
	     g2.setColor(new Color(0, 0, 0, 80));
	     g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);
	
	     // Dark electrical-themed gradient background
	     GradientPaint bgGradient = new GradientPaint(
	         panelX, panelY, new Color(25, 30, 40),
	         panelX, panelY + panelH, new Color(15, 20, 28)
	     );
	     g2.setPaint(bgGradient);
	     g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);
	
	     // Accent border (electric blue theme)
	     g2.setColor(new Color(60, 180, 255, 120));
	     g2.setStroke(new BasicStroke(2.5f));
	     g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc);
	
	     // ==================== RED FLASH EFFECT ====================
	     if (fuseFlashFrames > 0) {
	         fuseFlashFrames--;
	         int alpha = (int)(200 * (fuseFlashFrames / 30.0)); // Fade out over 30 frames
	         g2.setColor(new Color(255, 50, 50, Math.max(0, Math.min(255, alpha))));
	         g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);
	     }
	
	     // ==================== COOLDOWN STATE ====================
	     if (taskCooldownFrames > 0) {
	         Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
	         g2.setFont(cooldownFont);
	         g2.setColor(new Color(240, 100, 100));
	         
	         String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
	         int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
	         int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
	         g2.drawString(locked, lx, ly);
	
	         gp.mouseClicked = false;
	         gp.keyH.typedChar = 0;
	         gp.keyH.backspacePressed = false;
	         gp.keyH.enterPressed = false;
	         gp.keyH.escapePressed = false;
	         return;
	     }
	
	     // ==================== ESCAPE TO EXIT ====================
	     if (gp.keyH.escapePressed) {
	         gp.keyH.escapePressed = false;
	         resetAllTaskState();
	         gp.gameState = gp.playState;
	         return;
	     }
	
	     // ==================== TITLE SECTION ====================
	     String title = "⚡ Fuse Repair";
	     Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.90f);
	     g2.setFont(titleFont);
	     
	     int titleY = panelY + (int)(gp.tileSize * 0.85);
	     
	     // Title shadow
	     g2.setColor(new Color(0, 0, 0, 100));
	     g2.drawString(title, panelX + pad + 2, titleY + 2);
	     
	     // Title with electric blue color
	     g2.setColor(new Color(160, 220, 255));
	     g2.drawString(title, panelX + pad, titleY);
	
	     // ==================== DIVIDER LINE ====================
	     int dividerY = titleY + (int)(gp.tileSize * 0.4);
	     g2.setStroke(new BasicStroke(1.5f));
	     g2.setColor(new Color(60, 180, 255, 60));
	     g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY);
	
	     // ==================== INITIALIZATION ====================
	     if (!fuseGenerated) {
	         // Reset state
	         fuseSelectedLeft = -1;
	         fuseConnectionsMade = 0;
	         fuseFlashFrames = 0;
	         
	         // Initialize arrays
	         for (int i = 0; i < fuseNodeCount; i++) {
	             fuseLeftColors[i] = FUSE_COLORS[i];
	             fuseConnected[i] = false;
	             fuseLeftNodes[i] = new Rectangle();
	             fuseRightNodes[i] = new Rectangle();
	         }
	
	         // Shuffle right side
	         java.util.List<Integer> indices = new java.util.ArrayList<>();
	         for (int i = 0; i < fuseNodeCount; i++) indices.add(i);
	         java.util.Collections.shuffle(indices);
	         
	         for (int i = 0; i < fuseNodeCount; i++) {
	             fuseRightOrder[i] = indices.get(i);
	             fuseRightColors[i] = FUSE_COLORS[fuseRightOrder[i]];
	         }
	
	         // Set timer based on level
	         if (gp.level <= 1) {
	             fuseTimeLimitFrames = 60 * 60; // 60s
	         } else if (gp.level == 2) {
	             fuseTimeLimitFrames = 50 * 60; // 50s
	         } else if (gp.level == 3) {
	             fuseTimeLimitFrames = 40 * 60; // 40s
	         } else {
	             fuseTimeLimitFrames = 35 * 60; // 35s
	         }
	         
	         fuseTimerFrames = fuseTimeLimitFrames;
	         fuseGenerated = true;
	     }
	
	     // ==================== TIMER TICK ====================
	     if (fuseTimerFrames > 0) {
	         fuseTimerFrames--;
	     }
	     
	     if (fuseTimerFrames <= 0) {
	         handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, 
	             "Time's up! Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
	         return;
	     }
	
	     // ==================== TIMER DISPLAY ====================
	     Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f);
	     g2.setFont(timerFont);
	     
	     int timeSeconds = (fuseTimerFrames + 59) / 60;
	     Color timerColor = timeSeconds <= 10 ? new Color(240, 100, 100) : 
	                        timeSeconds <= 20 ? new Color(240, 200, 100) :
	                        new Color(180, 220, 180);
	     
	     g2.setColor(timerColor);
	     String timeText = "⏱ " + timeSeconds + "s";
	     int tW = g2.getFontMetrics().stringWidth(timeText);
	     g2.drawString(timeText, panelX + panelW - pad - tW, titleY);
	
	     // Timer bar
	     int barW = tW;
	     int barH = g2.getFontMetrics().getHeight() / 5;
	     int barX = panelX + panelW - pad - tW;
	     int barY = titleY + 6;
	
	     float ratio = (float)fuseTimerFrames / (float)fuseTimeLimitFrames;
	     ratio = Math.max(0f, Math.min(1f, ratio));
	
	     g2.setColor(new Color(20, 25, 30, 200));
	     g2.fillRoundRect(barX, barY, barW, barH, barH, barH);
	
	     int fillW = Math.max(2, (int)(barW * ratio));
	     GradientPaint barGradient = new GradientPaint(
	         barX, barY, timerColor,
	         barX + fillW, barY, new Color(timerColor.getRed(), 
	                                       timerColor.getGreen(), 
	                                       timerColor.getBlue(), 150)
	     );
	     g2.setPaint(barGradient);
	     g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);
	
	     g2.setColor(new Color(255, 255, 255, 50));
	     g2.setStroke(new BasicStroke(1f));
	     g2.drawRoundRect(barX, barY, barW, barH, barH, barH);
	
	     // ==================== PROGRESS DISPLAY ====================
	     Font progressFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f);
	     g2.setFont(progressFont);
	     
	     Color progressColor = fuseConnectionsMade == 9 ? new Color(120, 220, 140) : 
	                          new Color(180, 200, 230);
	     g2.setColor(progressColor);
	     
	     String progressText = "Connections: " + fuseConnectionsMade + " / 9";
	     g2.drawString(progressText, panelX + pad, titleY);
	
	     // ==================== NODE LAYOUT ====================
	     int nodeSize = (int)(gp.tileSize * 0.6);
	     int nodeGap = (int)(gp.tileSize * 0.15);
	     
	     int leftColX = panelX + (int)(gp.tileSize * 1.5);
	     int rightColX = panelX + panelW - (int)(gp.tileSize * 1.5) - nodeSize;
	     
	     int startY = dividerY + (int)(gp.tileSize * 0.8);
	     int totalNodesHeight = (nodeSize * fuseNodeCount) + (nodeGap * (fuseNodeCount - 1));
	     int availableHeight = panelH - (startY - panelY) - (int)(gp.tileSize * 1.2);
	     int centerOffset = (availableHeight - totalNodesHeight) / 2;
	     int firstNodeY = startY + centerOffset;
	
	     // ==================== DRAW WIRES (COMPLETED CONNECTIONS) ====================
	     g2.setStroke(new BasicStroke(3f));
	     
	     for (int i = 0; i < fuseNodeCount; i++) {
	         if (fuseConnected[i]) {
	             // Find matching right node
	             int rightIdx = -1;
	             for (int j = 0; j < fuseNodeCount; j++) {
	                 if (fuseRightOrder[j] == i) {
	                     rightIdx = j;
	                     break;
	                 }
	             }
	             
	             if (rightIdx != -1) {
	                 int leftY = firstNodeY + i * (nodeSize + nodeGap) + nodeSize / 2;
	                 int rightY = firstNodeY + rightIdx * (nodeSize + nodeGap) + nodeSize / 2;
	                 
	                 int leftX = leftColX + nodeSize;
	                 int rightX = rightColX;
	                 
	                 // Draw wire with glow effect
	                 Color wireColor = fuseLeftColors[i];
	                 
	                 // Outer glow
	                 g2.setColor(new Color(wireColor.getRed(), wireColor.getGreen(), 
	                                      wireColor.getBlue(), 60));
	                 g2.setStroke(new BasicStroke(7f));
	                 g2.drawLine(leftX, leftY, rightX, rightY);
	                 
	                 // Inner wire
	                 g2.setColor(wireColor);
	                 g2.setStroke(new BasicStroke(3f));
	                 g2.drawLine(leftX, leftY, rightX, rightY);
	             }
	         }
	     }
	
	     // ==================== DRAW PREVIEW WIRE (WHEN LEFT NODE SELECTED) ====================
	     if (fuseSelectedLeft != -1 && !fuseConnected[fuseSelectedLeft]) {
	         int leftY = firstNodeY + fuseSelectedLeft * (nodeSize + nodeGap) + nodeSize / 2;
	         int leftX = leftColX + nodeSize;
	         
	         // Draw dotted line to mouse
	         g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, 
	                                      BasicStroke.JOIN_ROUND, 1.0f, 
	                                      new float[]{8f, 8f}, 0f));
	         
	         Color previewColor = fuseLeftColors[fuseSelectedLeft];
	         g2.setColor(new Color(previewColor.getRed(), previewColor.getGreen(), 
	                              previewColor.getBlue(), 180));
	         g2.drawLine(leftX, leftY, gp.mouseX, gp.mouseY);
	     }
	
	     // ==================== DRAW NODES ====================
	     for (int i = 0; i < fuseNodeCount; i++) {
	         int nodeY = firstNodeY + i * (nodeSize + nodeGap);
	         
	         // LEFT NODE
	         fuseLeftNodes[i].setBounds(leftColX, nodeY, nodeSize, nodeSize);
	         
	         boolean leftConnected = fuseConnected[i];
	         boolean leftSelected = (fuseSelectedLeft == i);
	         
	         // Node shadow
	         g2.setColor(new Color(0, 0, 0, 120));
	         g2.fillOval(leftColX + 3, nodeY + 3, nodeSize, nodeSize);
	         
	         // Node background
	         if (leftConnected) {
	             g2.setColor(new Color(40, 45, 50));
	         } else if (leftSelected) {
	             g2.setColor(new Color(60, 70, 85));
	         } else {
	             g2.setColor(new Color(50, 55, 60));
	         }
	         g2.fillOval(leftColX, nodeY, nodeSize, nodeSize);
	         
	         // Node color ring
	         g2.setStroke(new BasicStroke(leftSelected ? 5f : 3f));
	         g2.setColor(fuseLeftColors[i]);
	         g2.drawOval(leftColX + 3, nodeY + 3, nodeSize - 6, nodeSize - 6);
	         
	         // Inner glow for selected
	         if (leftSelected && !leftConnected) {
	             g2.setColor(new Color(fuseLeftColors[i].getRed(), 
	                                  fuseLeftColors[i].getGreen(), 
	                                  fuseLeftColors[i].getBlue(), 100));
	             g2.fillOval(leftColX + 8, nodeY + 8, nodeSize - 16, nodeSize - 16);
	         }
	         
	         // RIGHT NODE
	         fuseRightNodes[i].setBounds(rightColX, nodeY, nodeSize, nodeSize);
	         
	         int colorIdx = fuseRightOrder[i];
	         boolean rightConnected = fuseConnected[colorIdx];
	         
	         // Node shadow
	         g2.setColor(new Color(0, 0, 0, 120));
	         g2.fillOval(rightColX + 3, nodeY + 3, nodeSize, nodeSize);
	         
	         // Node background
	         g2.setColor(rightConnected ? new Color(40, 45, 50) : new Color(50, 55, 60));
	         g2.fillOval(rightColX, nodeY, nodeSize, nodeSize);
	         
	         // Node color ring
	         g2.setStroke(new BasicStroke(3f));
	         g2.setColor(fuseRightColors[i]);
	         g2.drawOval(rightColX + 3, nodeY + 3, nodeSize - 6, nodeSize - 6);
	     }
	
	     // ==================== INPUT HANDLING ====================
	     if (gp.mouseClicked) {
	         gp.mouseClicked = false;
	         
	         // Check left nodes
	         for (int i = 0; i < fuseNodeCount; i++) {
	             if (fuseLeftNodes[i].contains(gp.mouseX, gp.mouseY) && !fuseConnected[i]) {
	                 if (fuseSelectedLeft == i) {
	                     fuseSelectedLeft = -1; // Deselect if clicking same node
	                 } else {
	                     fuseSelectedLeft = i;
	                 }
	                 return;
	             }
	         }
	         
	         // Check right nodes (only if left is selected)
	         if (fuseSelectedLeft != -1) {
	             for (int i = 0; i < fuseNodeCount; i++) {
	                 if (fuseRightNodes[i].contains(gp.mouseX, gp.mouseY)) {
	                     int rightColorIdx = fuseRightOrder[i];
	                     
	                     // Check if this is the correct match
	                     if (rightColorIdx == fuseSelectedLeft) {
	                         // CORRECT CONNECTION
	                         fuseConnected[fuseSelectedLeft] = true;
	                         fuseConnectionsMade++;
	                         fuseSelectedLeft = -1;
	                         
	                         // Check for completion
	                         if (fuseConnectionsMade == 9) {
	                             handleTaskSuccess("Fuse repaired!");
	                         }
	                     } else {
	                         // WRONG CONNECTION - IMMEDIATE FAIL
	                         fuseFlashFrames = 30;
	                         
	                         // Small delay before fail to show flash
	                         new Thread(() -> {
	                             try {
	                                 Thread.sleep(500);
	                                 handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
	                                     "Incorrect connection. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
	                             } catch (InterruptedException e) {
	                                 e.printStackTrace();
	                             }
	                         }).start();
	                     }
	                     return;
	                 }
	             }
	         }
	     }
	
	     // ==================== INSTRUCTIONS ====================
	     Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.30f);
	     g2.setFont(hintFont);
	     g2.setColor(new Color(180, 190, 210, 180));
	     
	     String hint = fuseSelectedLeft == -1 ? 
	         "Click a node on the left, then its matching color on the right • ESC to exit" :
	         "Click the matching color on the right • Click same node to deselect";
	     
	     int hw = g2.getFontMetrics().stringWidth(hint);
	     g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 2);
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
    
 // ==================== DRAW COOKING TASK = ====================
    public void drawCookingTask() {

        // ==================== RENDERING SETUP ====================
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 10;
        int panelH = gp.tileSize * 7;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;
        int arc = 30;
        int pad = gp.tileSize / 3;

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc);
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc);

        // Warm cooking-themed gradient background
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(45, 35, 30),
            panelX, panelY + panelH, new Color(30, 25, 22)
        );
        g2.setPaint(bgGradient);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // Accent border (warm orange/red theme)
        g2.setColor(new Color(255, 140, 60, 120));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc);

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) {
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f);
            g2.setFont(cooldownFont);
            g2.setColor(new Color(240, 100, 100));
            
            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2;
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2;
            g2.drawString(locked, lx, ly);

            gp.mouseClicked = false;
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
            gp.keyH.escapePressed = false;
            return;
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            resetAllTaskState();
            gp.gameState = gp.playState;
            return;
        }

        // ==================== TITLE SECTION ====================
        String title = "🍳 Cooking Quiz";
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.90f);
        g2.setFont(titleFont);
        
        int titleY = panelY + (int)(gp.tileSize * 0.85);
        
        // Title shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, panelX + pad + 2, titleY + 2);
        
        // Title with warm color
        g2.setColor(new Color(255, 220, 180));
        g2.drawString(title, panelX + pad, titleY);

        // ==================== DIVIDER LINE ====================
        int dividerY = titleY + (int)(gp.tileSize * 0.4);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 140, 60, 60));
        g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY);

        // ==================== INITIALIZATION ====================
        if (!cookingGenerated) {
            // Pick random question
            cookingQuestionIndex = (int)(Math.random() * COOKING_QUESTIONS.length);
            cookingCorrectAnswer = Integer.parseInt(COOKING_QUESTIONS[cookingQuestionIndex][5]);
            cookingSelectedAnswer = -1;
            cookingAnswerSubmitted = false;
            cookingAnswerCorrect = false;
            
            // Set timer based on level
            if (gp.level <= 1) {
                cookingTimeLimitFrames = 30 * 60; // 30s
            } else if (gp.level == 2) {
                cookingTimeLimitFrames = 25 * 60; // 25s
            } else if (gp.level == 3) {
                cookingTimeLimitFrames = 20 * 60; // 20s
            } else {
                cookingTimeLimitFrames = 15 * 60; // 15s
            }
            
            cookingTimerFrames = cookingTimeLimitFrames;
            cookingGenerated = true;
        }

        // ==================== TIMER TICK ====================
        if (cookingTimerFrames > 0 && !cookingAnswerSubmitted) {
            cookingTimerFrames--;
        }
        
        if (cookingTimerFrames <= 0 && !cookingAnswerSubmitted) {
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, 
                "Time's up! Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
            return;
        }

        // ==================== TIMER DISPLAY ====================
        Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f);
        g2.setFont(timerFont);
        
        int timeSeconds = (cookingTimerFrames + 59) / 60;
        Color timerColor = timeSeconds <= 5 ? new Color(240, 100, 100) : 
                           timeSeconds <= 10 ? new Color(240, 200, 100) :
                           new Color(180, 220, 180);
        
        g2.setColor(timerColor);
        String timeText = "⏱ " + timeSeconds + "s";
        int tW = g2.getFontMetrics().stringWidth(timeText);
        g2.drawString(timeText, panelX + panelW - pad - tW, titleY);

        // Timer bar
        int barW = tW;
        int barH = g2.getFontMetrics().getHeight() / 5;
        int barX = panelX + panelW - pad - tW;
        int barY = titleY + 6;

        float ratio = (float)cookingTimerFrames / (float)cookingTimeLimitFrames;
        ratio = Math.max(0f, Math.min(1f, ratio));

        g2.setColor(new Color(20, 25, 30, 200));
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

        int fillW = Math.max(2, (int)(barW * ratio));
        GradientPaint barGradient = new GradientPaint(
            barX, barY, timerColor,
            barX + fillW, barY, new Color(timerColor.getRed(), 
                                          timerColor.getGreen(), 
                                          timerColor.getBlue(), 150)
        );
        g2.setPaint(barGradient);
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);

        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH);

        // ==================== QUESTION DISPLAY ====================
        int questionY = dividerY + (int)(gp.tileSize * 0.6);
        int questionW = panelW - pad * 2;

        Font questionFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.42f);
        g2.setFont(questionFont);
        g2.setColor(new Color(255, 235, 210));

        String question = COOKING_QUESTIONS[cookingQuestionIndex][0];
        java.util.List<String> questionLines = wrapText(question, g2.getFontMetrics(), questionW);
        
        int qY = questionY;
        for (String line : questionLines) {
            int lineW = g2.getFontMetrics().stringWidth(line);
            g2.drawString(line, panelX + (panelW - lineW) / 2, qY);
            qY += g2.getFontMetrics().getHeight();
        }

        // ==================== ANSWER OPTIONS ====================
        int optionsY = qY + (int)(gp.tileSize * 0.3);
        int optionH = (int)(gp.tileSize * 0.8);
        int optionGap = (int)(gp.tileSize * 0.2);

        Font optionFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.38f);
        g2.setFont(optionFont);

        String[] optionLabels = {"A", "B", "C", "D"};
        
        for (int i = 0; i < 4; i++) {
            int optY = optionsY + i * (optionH + optionGap);
            int optX = panelX + pad;
            int optW = panelW - pad * 2;

            // Determine option state
            boolean isSelected = (cookingSelectedAnswer == i);
            boolean isCorrect = (cookingCorrectAnswer == i);
            boolean showResult = cookingAnswerSubmitted;

            // Option background color
            Color bgColor;
            if (showResult && isCorrect) {
                bgColor = new Color(60, 120, 60, 200); // Green for correct
            } else if (showResult && isSelected && !isCorrect) {
                bgColor = new Color(120, 40, 40, 200); // Red for wrong selection
            } else if (isSelected && !showResult) {
                bgColor = new Color(80, 100, 150, 200); // Blue for current selection
            } else {
                bgColor = new Color(50, 50, 55, 200); // Default gray
            }

            // Draw option box with shadow
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(optX + 3, optY + 3, optW, optionH, 16, 16);

            g2.setColor(bgColor);
            g2.fillRoundRect(optX, optY, optW, optionH, 16, 16);

            // Border
            Color borderColor;
            if (showResult && isCorrect) {
                borderColor = new Color(120, 220, 140);
            } else if (showResult && isSelected && !isCorrect) {
                borderColor = new Color(240, 120, 120);
            } else if (isSelected && !showResult) {
                borderColor = new Color(150, 180, 255);
            } else {
                borderColor = new Color(100, 100, 110, 140);
            }

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(isSelected ? 3f : 2f));
            g2.drawRoundRect(optX, optY, optW, optionH, 16, 16);

            // Option text
            g2.setColor(new Color(240, 240, 250));
            String optionText = optionLabels[i] + ") " + COOKING_QUESTIONS[cookingQuestionIndex][i + 1];
            
            FontMetrics ofm = g2.getFontMetrics();
            int textX = optX + (int)(gp.tileSize * 0.3);
            int textY = optY + (optionH - ofm.getHeight()) / 2 + ofm.getAscent();
            g2.drawString(optionText, textX, textY);

            // Checkmark for correct answer (after submission)
            if (showResult && isCorrect) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.5f));
                g2.setColor(new Color(120, 220, 140));
                g2.drawString("✓", optX + optW - (int)(gp.tileSize * 0.6), textY);
            }
        }

        // ==================== INPUT HANDLING ====================
        if (!cookingAnswerSubmitted) {
            // Keyboard input (1-4 or A-D)
            char typed = gp.keyH.typedChar;
            if (typed != 0) {
                gp.keyH.typedChar = 0;
                
                if (typed == '1' || typed == 'a' || typed == 'A') cookingSelectedAnswer = 0;
                else if (typed == '2' || typed == 'b' || typed == 'B') cookingSelectedAnswer = 1;
                else if (typed == '3' || typed == 'c' || typed == 'C') cookingSelectedAnswer = 2;
                else if (typed == '4' || typed == 'd' || typed == 'D') cookingSelectedAnswer = 3;
            }

            // Mouse click on options
            if (gp.mouseClicked) {
                gp.mouseClicked = false;
                
                for (int i = 0; i < 4; i++) {
                    int optY = optionsY + i * (optionH + optionGap);
                    int optX = panelX + pad;
                    int optW = panelW - pad * 2;
                    
                    if (gp.mouseX >= optX && gp.mouseX <= optX + optW &&
                        gp.mouseY >= optY && gp.mouseY <= optY + optionH) {
                        cookingSelectedAnswer = i;
                        break;
                    }
                }
            }

            // Submit with ENTER
            if (gp.keyH.enterPressed && cookingSelectedAnswer != -1) {
                gp.keyH.enterPressed = false;
                cookingAnswerSubmitted = true;
                cookingAnswerCorrect = (cookingSelectedAnswer == cookingCorrectAnswer);
            }
        }

        // ==================== FEEDBACK DISPLAY ====================
        if (cookingAnswerSubmitted) {
            int feedbackY = optionsY + 4 * (optionH + optionGap) + (int)(gp.tileSize * 0.3);
            
            Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.55f);
            g2.setFont(feedbackFont);
            
            String feedback = cookingAnswerCorrect ? "✓ Correct!" : "✗ Incorrect";
            Color feedbackColor = cookingAnswerCorrect ? 
                new Color(120, 220, 140) : new Color(240, 120, 120);
            
            // Feedback glow
            g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
                                 feedbackColor.getBlue(), 80));
            int fw = g2.getFontMetrics().stringWidth(feedback);
            int fx = panelX + (panelW - fw) / 2;
            g2.drawString(feedback, fx + 2, feedbackY + 2);
            
            g2.setColor(feedbackColor);
            g2.drawString(feedback, fx, feedbackY);

            // Continue prompt
            Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f);
            g2.setFont(hintFont);
            g2.setColor(new Color(200, 210, 230, 200));
            
            String hint = "Press ENTER to continue";
            int hw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, panelX + (panelW - hw) / 2, feedbackY + (int)(gp.tileSize * 0.5));

            // Handle continuation
            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;
                
                if (cookingAnswerCorrect) {
                    handleTaskSuccess("Task Completed!");
                } else {
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                        "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
                }
                return;
            }
        } else {
            // ==================== BOTTOM HELP TEXT ====================
            String hint = cookingSelectedAnswer == -1 ? 
                "Click an option or press 1-4 • ESC to exit" :
                "Press ENTER to submit • ESC to exit";
            
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.30f));
            g2.setColor(new Color(180, 190, 210, 180));
            int hw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 2);
        }
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
        // Logic Panel Task
        logicGenerated = false;
        logicTimerFrames = 0;
        logicTimeLimitFrames = 0;
        logicFlashFrames = 0;
        for (int i = 0; i < 6; i++) {
            logicPlayerAnswers[i] = -1;
        }
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
        
        // Fuse Repair Task
        fuseGenerated = false;
        fuseSelectedLeft = -1;
        fuseConnectionsMade = 0;
        fuseTimerFrames = 0;
        fuseTimeLimitFrames = 0;
        fuseFlashFrames = 0;
        for (int i = 0; i < 9; i++) {
            fuseConnected[i] = false;
        }
        
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
    	
    	// Cooking Task
    	cookingGenerated = false;
    	cookingQuestionIndex = -1;
    	cookingCorrectAnswer = -1;
    	cookingSelectedAnswer = -1;
    	cookingAnswerSubmitted = false;
    	cookingAnswerCorrect = false;
    	cookingTimerFrames = 0;
    	cookingTimeLimitFrames = 0;
    	
        
        
	 // VAULT SEQUENCE (4 RIDDLES -> 4 DIGITS -> FINAL CODE)
	   vaultGenerated = false;

	    vaultTimerFrames = 0;
	    vaultTimeLimitFrames = 0;

	    // 4 riddles
	    vaultRiddleQ = new String[4];
	    vaultRiddleA = new String[4];
	    vaultInputs  = new String[]{"", "", "", ""};
	    vaultSolved = new boolean[]{false, false, false, false};

	    // digits awarded
	    vaultDigits = new int[4];

	    // which riddle is currently shown (0..3)
	    vaultIndex = 0;

	    // final code entry
	    vaultEnteringCode = false;
	    vaultFinalInput = "";

	    // feedback
	    vaultFeedback = "";
	    vaultFeedbackFrames = 0;

        

        
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
        showBoxMessage(
            popupMessage,
            gp.screenWidth / 2 - gp.tileSize * 2,
            gp.screenHeight / 2 - gp.tileSize
        );

        boxMessageOn = true;
        messageCounter = 0;
        messageDuration = 120;

        try {
            if (gp.player != null) {
                int idx = gp.player.curTaskIndex;

                // Mark current task complete
                if (idx >= 0 && idx < gp.player.tasksList.size()) {
                    gp.player.tasksList.get(idx).setCompleted(true);
                    if (idx < gp.tasks.length) {
                        gp.tasks[idx] = null;
                    }
                }

                // ==================== CHECK ALL TASKS ====================
                boolean allCompleted = true;
                for (Task task : gp.player.tasksList) {
                    if (!task.isCompleted()) {
                        allCompleted = false;
                        break;
                    }
                }

                gp.player.tasksComplete = allCompleted;
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
    
    private String normalizeAnswer(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();

        // remove leading articles to make answers forgiving (optional but helps a lot)
        if (s.startsWith("a ")) s = s.substring(2);
        else if (s.startsWith("an ")) s = s.substring(3);
        else if (s.startsWith("the ")) s = s.substring(4);

        // collapse multiple spaces
        s = s.replaceAll("\\s+", " ");

        return s;
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
    
    public int getTaskCooldownFrames() {
        return taskCooldownFrames;
    }




}
