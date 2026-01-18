/*

Name: Rafay 

Date: 1/19/2026

Course Code: ICS4U0

Description:
This class handles all User interface components including interface and engine code. Methods are called from either main game loop or triggered by player activity.

*/
package main; // package this class belongs to

import java.awt.BasicStroke; // stroke for drawing lines
import java.awt.Color; // color constants and RGB colors
import java.awt.Font; // font handling
import java.awt.FontMetrics; // for measuring text
import java.awt.GradientPaint; // gradient painting utility
import java.awt.Graphics2D; // primary 2D rendering class
import java.awt.Paint; // paint abstraction
import java.awt.Rectangle; // rectangular shape/hitbox
import java.awt.RenderingHints; // rendering quality hints
import java.awt.Shape; // geometric shape abstraction
import java.awt.event.KeyEvent; // key event helpers (getKeyText etc.)
import java.awt.geom.Ellipse2D; // ellipse shape
import java.awt.image.BufferedImage; // in-memory image representation
import java.io.InputStream; // input stream (not used here but imported)
import java.text.DecimalFormat; // decimal formatting utility

import javax.imageio.ImageIO; // image IO for reading images

import Item.Flashlight; // game item: flashlight
import Item.Food; // game item: food
import Item.Item; // base item class
import Item.Key; // base key item
import Item.Throwable; // throwable item
import Item.blueKey; // blue key item
import Item.greenKey; // green key item
import Item.redKey; // red key item
import saves.save1; // save slot 1
import saves.save2; // save slot 2
import saves.save3; // save slot 3
import task.Task; // task abstraction

public class UserInterface { // main UI class for in-game overlays and tasks

    gamePanel gp; // reference to main game panel / context
    Graphics2D g2; // graphics context used for drawing
    Font arial_40; // base font used by UI (declared but set to Cambria later)
    BufferedImage keyImage, greenKeyImage, redKeyImage, torchImage, blueKeyImage; // cached item images
    BufferedImage skinPreview; // preview image for skin (unused here but reserved)
    
    // messages / UI
    public boolean messageOn = false; // whether a simple transient message should be drawn
    public boolean boxMessageOn = false; // whether a boxed message should be drawn
    public boolean interactOn = false; // whether the interact hint should be shown
    public String interactMessage = ""; // text for interact hint
    public String message = ""; // text for transient message
    public String boxMessage = ""; // text for boxed message
    int messageCounter = 0; // frame counter for message lifetime
    int messageDuration = 80; // default duration in frames for messages
    int messageX; // x position for message drawing
    int messageY; // y position for message drawing
    String colorName = "white"; // color hint for message rendering (string mapped to Color)
    public boolean showThrowRadius = false; // whether to display allowed throw radius overlay
    public Throwable activeThrowable; // currently active throwable item being targeted
    
    // Tile select task variables
    private boolean tileSelectGenerated = false; // whether the tile-select task has been initialized

    private static final int TS_GRID = 6; // 6x6 grid size constant for tile-select
    private static final int TS_FLASH_COUNT = 6; // number of flashes in tile-select sequence

    private int tsCellSize; // pixel size of each cell in tile-select UI
    private int tsGridX, tsGridY; // top-left screen position of tile-select grid

    private boolean[][] tsPattern = new boolean[TS_GRID][TS_GRID]; // pattern to flash
    private boolean[][] tsSelected = new boolean[TS_GRID][TS_GRID]; // player's selected tiles

    private int tsPhase = 0; // 0=flash, 1=blank pause, 2=input, 3=feedback
    private int tsFlashFrames = (int) (3.0 * 60); // flash duration in frames (3s * 60fps)
    private int tsBlankFrames = (int) (2.0 * 60); // 120 frames blank pause
    private int tsTimer = 0; // general purpose timer for tile-select

    private boolean tsResult = false; // result of tile-select (success/fail)

    // task / math variables
    private boolean taskGenerated = false; // whether a math task has been generated
    private String question = ""; // math question text
    private int correctAnswer = 0; // correct answer for math question
    private String playerInput = ""; // player's typed answer
    private boolean answerChecked = false; // whether player's input has been checked
    private boolean answerCorrect = false; // whether player's answer was correct

    // task counters / timers (math)
    private int taskTimerFrames = 0; // elapsed frames for current math task
    private int taskTimeLimitFrames = 0; // time limit for math task in frames
    private int questionsAsked = 0; // number of math questions asked this session
    private int correctCount = 0; // tally of correct answers
    private int wrongCount = 0; // tally of wrong answers

    // riddle task state
    private boolean riddleGenerated = false; // whether a riddle task has been generated
    private String riddleQuestion = ""; // riddle question text
    private String riddleAnswer = ""; // expected riddle answer
    private String riddlePlayerInput = ""; // player's typed riddle answer
    private boolean riddleAnswerChecked = false; // whether riddle answer has been validated
    private boolean riddleAnswerCorrect = false; // riddle answer correctness flag
    private int riddleTimerFrames = 0; // elapsed frames for riddle timer
    private int riddleTimeLimitFrames = 45 * 60; // default riddle time limit (45s * 60fps)
    
 // Scroll state for the INSTRUCTIONS screen
    public int instrScrollOffset = 0; // current vertical scroll offset in pixels
    public int instrScrollSpeed = 28; // pixels per step (same as your line height)
    public int instrContentHeight = 0;      // computed each frame: full content height
    public int instrViewportHeight = 0;     // computed each frame: visible viewport height
    
 // BUTTON MATCH TASK VARIABLES
    private boolean buttonMatchGenerated = false; // whether button-match task is created
    private boolean buttonMatchResolved = false; // whether button-match has been resolved by player

    private long buttonMatchStartNano = 0; // start time in nanoseconds for timing precision
    private double buttonMatchTargetSeconds = 0;   // target timestamp in seconds for press
    private final double buttonMatchWindow = 0.10; // +/- 0.10 seconds window allowed for success

    private Rectangle buttonMatchButtonRect = new Rectangle(); // hitbox for the clickable button
    private String buttonMatchFeedback = ""; // text feedback for button-match result
    private int buttonMatchFeedbackFrames = 0; // frames to display feedback
    
	// pattern switches task
	private boolean patternGenerated = false; // whether pattern switches task is generated
	private int[] patternSequence = new int[0]; // generated sequence of switches to replicate
	private int patternLength = 0; // length of patternSequence
	private boolean patternShowing = true; // whether pattern is currently being shown to player

	// timing (frames @ 60fps)
	private int patternFlashFrames = 30; // 0.5s flash duration at 60fps
	private int patternGapFrames = 6; // small gap between flashes (optional)
	private int patternIndex = 0; // current index in the sequence while showing
	private int patternFlashTimer = 0; // counts down within flash period
	private int patternGapTimer = 0; // counts down between flashes
	private int patternInputIndex = 0; // index of player's input while replicating
	private int patternInputTimerFrames = 0; // player's input timer
	private int patternInputLimitFrames = 5 * 60; // 5 seconds to input pattern
	private boolean patternChecked = false; // whether player's input was checked
	private boolean patternSuccess = false; // whether player succeeded at pattern task { }

	{

	// Pattern Switches
	patternGenerated = false; // initialize pattern flags (redundant but explicit)
	patternSequence = new int[0]; // reset sequence
	patternLength = 0; // reset length
	patternShowing = true; // set showing initially true
	patternIndex = 0; // reset index
	patternFlashTimer = 0; // reset flash timer
	patternGapTimer = 0; // reset gap timer
	patternInputIndex = 0; // reset input index
	patternInputTimerFrames = 0; // reset input timer
	patternInputLimitFrames = 5 * 60; // reassign default input limit
	patternChecked = false; // reset checked flag
	patternSuccess = false; } // close initialization block
	
	 // Logic panel task state
	 private boolean logicGenerated = false; // whether logic panel is generated
	 private int logicStatementCount = 6; // number of statements to present
	 private String[] logicStatements = new String[6]; // array holding statement strings
	 private boolean[] logicCorrectAnswers = new boolean[6]; // true or false expected answers
	 private int[] logicPlayerAnswers = new int[6]; // -1 = unset, 0 = false, 1 = true
	 private int logicTimerFrames = 0; // elapsed frames for logic task
	 private int logicTimeLimitFrames = 0; // time limit for logic task in frames
	 private int logicFlashFrames = 0; // frames used for flash feedback

	 // Toggle switch hitboxes
	 private Rectangle[] logicTrueSwitches = new Rectangle[6]; // hitboxes for "true" toggles
	 private Rectangle[] logicFalseSwitches = new Rectangle[6]; // hitboxes for "false" toggles

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
    private boolean vaultGenerated = false; // whether vault task has been initialized

    private int vaultTimerFrames = 0; // elapsed frames for vault timer
    private int vaultTimeLimitFrames = 0; // time limit for vault task
    private int vaultWrongAnswers = 0; // tally of wrong answers in vault
    // 4 riddles
    private String[] vaultRiddleQ = new String[4]; // questions for vault riddles
    private String[] vaultRiddleA = new String[4]; // answers for vault riddles
    private String[] vaultInputs  = new String[]{"", "", "", ""}; // player inputs for each riddle
    private boolean[] vaultSolved = new boolean[]{false, false, false, false}; // solved flags

    // digits awarded
    private int[] vaultDigits = new int[4]; // digits that form final code

    // which riddle is currently shown (0..3)
    private int vaultIndex = 0; // index of current riddle shown

    // final code entry
    private boolean vaultEnteringCode = false; // whether player is entering final code
    private String vaultFinalInput = ""; // player's typed final code

    // feedback
    private String vaultFeedback = ""; // feedback text for vault
    private int vaultFeedbackFrames = 0; // frames to show feedback

    
    // riddle pool
    private final String[] RIDDLE_QUESTIONS = {
        "What has to be broken before you can use it?", // classic riddle question
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
        "an egg", // answer for first riddle
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
    private boolean fuseGenerated = false; // whether fuse task is initialized
    private int fuseNodeCount = 9; // number of nodes on each side
    private Color[] fuseLeftColors = new Color[9]; // colors for left side nodes
    private Color[] fuseRightColors = new Color[9]; // colors for right side nodes
    private int[] fuseRightOrder = new int[9]; // maps right index -> color index
    private boolean[] fuseConnected = new boolean[9]; // whether each fuse is connected
    private int fuseSelectedLeft = -1; // -1 = none selected; selected left node index
    private int fuseConnectionsMade = 0; // number of successful connections made
    private int fuseTimerFrames = 0; // elapsed frames for fuse task
    private int fuseTimeLimitFrames = 0; // time limit for fuse task

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
    private Rectangle[] fuseLeftNodes = new Rectangle[9]; // left side node rectangles
    private Rectangle[] fuseRightNodes = new Rectangle[9]; // right side node rectangles

    // Flash effect for wrong connection
    private int fuseFlashFrames = 0; // frames to flash on wrong connect

    
 // Cooking task state
    private boolean cookingGenerated = false; // whether cooking Q/A is generated
    private int cookingQuestionIndex = -1; // index of current cooking question
    private int cookingCorrectAnswer = -1; // 0-3 index for correct multiple choice answer
    private int cookingSelectedAnswer = -1; // player's selected option index (-1 none)
    private boolean cookingAnswerSubmitted = false; // whether player submitted their answer
    private boolean cookingAnswerCorrect = false; // whether submitted answer was correct
    private int cookingTimerFrames = 0; // elapsed frames for cooking task
    private int cookingTimeLimitFrames = 0; // time limit in frames for cooking task

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
    private int taskCooldownFrames = 0; // cooldown frames before next task attempt
    private final int DEFAULT_TASK_COOLDOWN_SECONDS = 10; // default cooldown in seconds

    // Title/menu state
    public int commandNum = 0; // menu selection index
    public int titleScreenState = 0; // state of title screen

    public boolean levelFinished = false; // whether current level is finished
    double playTime; // accumulated play time in seconds (or units used elsewhere)
    DecimalFormat df = new DecimalFormat("#0.00"); // formatter for playTime display

    public String currentDialogue = ""; // active dialogue text
    public String currentDialogueSpeaker = ""; // speaker name for dialogue text

    // UI input flags (set by keyHandler)
    public boolean uiUp = false; // navigate UI up
    public boolean uiDown = false; // navigate UI down
    public boolean uiLeft = false; // navigate UI left
    public boolean uiRight = false; // navigate UI right
    public boolean uiConfirm = false; // confirm selection
    public boolean uiBack = false; // go back/cancel

    // keybind editing
    public boolean awaitingKeybind = false; // whether awaiting input for a new keybind
    public boolean capturedKeyPressed = false; // whether a key press has been captured
    public int capturedKey = -1; // captured key code
    public int keybindSelectedIndex = 0; // which action is being rebound
    public final String[] keybindActionNames = {"Move Forward","Move Backward","Move Left","Move Right","Sprint","Crouch","Interact","Throw Item","Drop Item"}; // action labels

    public int slotRow = -1; // selected inventory slot row
    public Item selectedItem; // reference to currently selected item in UI
    

    public UserInterface(gamePanel gp) { // constructor expecting game panel context
        this.gp = gp; // store reference to gamePanel
        arial_40 = new Font("Cambria", Font.PLAIN, 40); // initialize base font (Cambria, 40px)

        // sample item image caching - keep as in original
        try {
            Key key = new Key(gp); // instantiate key to pull its image
            greenKey greenK = new greenKey(gp); // instantiate green key
            redKey redK = new redKey(gp); // instantiate red key
            Flashlight torch = new Flashlight(gp); // instantiate flashlight
            blueKey blueK = new blueKey(gp); // instantiate blue key
            keyImage = key.image; // cache key image
            greenKeyImage = greenK.image; // cache green key image
            redKeyImage = redK.image; // cache red key image
            torchImage = torch.image; // cache flashlight image
            blueKeyImage = blueK.image; // cache blue key image
        } catch (Exception e) {
            // ignore if images not present during compile / quick tests
        }
        
        

        messageX = gp.tileSize/2; // default message X position (half tile)
        messageY = gp.tileSize*5; // default message Y position (5 tiles down)
        messageDuration = 80; // re-assert default duration
        colorName = "white"; // default color for messages
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
			this.colorName = colorName; // set message color name
	}
    
    public void showMessage(String text, int durationFrames) {
		message = text; // set message text
		messageOn = true; // enable message rendering
		messageCounter = 0; // reset timer/counter
		messageDuration = durationFrames; // override duration
	}
    
    public void showMessage(String text, String colorName, int durationFrames) {
					message = text; // set message text
					messageOn = true; // enable message rendering
					messageCounter = 0; // reset timer/counter
					this.colorName = colorName; // set message color
					messageDuration = durationFrames; // override duration
    }
    
    public void showMessage(String text, int x, int y) {
			message = text; // set message text
			messageOn = true; // enable message rendering
			messageCounter = 0; // reset timer/counter
			messageX = x; // set custom X
			messageY = y; // set custom Y
    }
    
    public void showMessage(String text, int x, int y, int durationFrames) {
				message = text; // set message text
				messageOn = true; // enable message rendering
				messageCounter = 0; // reset timer/counter
				messageX = x; // set custom X
				messageY = y; // set custom Y
				messageDuration = durationFrames; // custom duration
    }
    
    public void showMessage(String text, int x, int y, String colorName) {
				message = text; // set message text
				messageOn = true; // enable message rendering
				messageCounter = 0; // reset timer/counter
				messageX = x; // set custom X
				messageY = y; // set custom Y
				this.colorName = colorName; // set color
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
					messageX = x; // custom X for box
					messageY = y; // custom Y for box
					
    }
    

    
    void clampInstrScroll() {
        if (instrContentHeight <= instrViewportHeight) {
            instrScrollOffset = 0; // nothing to scroll if content fits
        } else {
            instrScrollOffset = Math.max(0, Math.min(instrScrollOffset, instrContentHeight - instrViewportHeight)); // clamp between 0 and max
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
        interactOn = false; // turn off interact hint
    }

 // main draw entry — called every frame by gamePanel.paintComponent(...)
    public void draw(Graphics2D g2) {
        this.g2 = g2; // store the Graphics2D instance for helper methods below
        g2.setFont(arial_40); // set base font
        g2.setColor(Color.white); // default draw color

        // Tick global cooldown every frame (so it begins immediately when a task fails)
        if (taskCooldownFrames > 0) {
            taskCooldownFrames--; // decrement cooldown
            if (taskCooldownFrames < 0) taskCooldownFrames = 0; // ensure non-negative
        }

        // Global Escape handler: if the player presses Escape while in a task, abort and reset
        if (gp.gameState == gp.taskState && gp.keyH.escapePressed) {
        	gp.gameState = gp.playState; // return to play state
            gp.keyH.escapePressed = false; // consume escape press
            resetAllTaskState(); // reset all task-related state
            return; // stop drawing task UI this frame
        }

        // Process UI input first (consumes uiUp/uiDown/uiConfirm etc.)
        handleInput(); // update internal UI navigation flags

        // Title & main states drawing (preserve your existing structure)
        if (gp.gameState == gp.titleState) {
            drawTitleScreen(); // draw title screen
        }
        if (gp.gameState == gp.playState) {
            drawInventory(); // draw inventory overlay
            drawStaminaBar(); // draw stamina bar
            drawTasksList(); // draw available tasks list
        }
        if (gp.gameState == gp.pauseState) drawPauseScreen(); // draw pause UI if paused
        if (gp.gameState == gp.dialogueState) drawDialogueScreen(); // draw dialogue UI
        if (gp.gameState == gp.deathState) drawDeathScreen(); // draw death screen UI

        // Task state: pick task by player's current task name
        if (gp.gameState == gp.taskState) {
            if (gp.player != null && gp.player.curTaskName != null) {
                switch (gp.player.curTaskName) {
                    case "Math Task" -> drawMathTask(); // math task renderer
                    case "Riddle Task" -> drawRiddleTask(); // riddle task renderer
                    case "Tile Select Task" -> drawTileSelectTask(); // tile-select renderer
                    case "Button Match Task" -> drawButtonMatchTask(); // button-match renderer
                    case "Vault Sequence Task" -> drawVaultSequenceTask(); // vault task renderer
                    case "Pattern Switches Task" -> drawPatternSwitchTask(); // pattern switches renderer
                    case "Cooking Task" -> drawCookingTask(); // cooking task renderer
                    case "Fuse Repair Task" -> drawFuseRepairTask(); // fuse repair renderer
                    case "Logic Panel Task" -> drawLogicPanelTask(); // logic panel renderer
                    default -> drawMathTask(); // fallback to math task
                }
            } else {
                drawMathTask(); // fallback when no player or task specified
            }
        }

        // MESSAGES: draw temporary message if set, and auto-hide it after a counter
        if (messageOn) {
            g2.setColor(switch(colorName) {
                case "red" -> Color.red; // map colorName to Color
                case "yellow" -> Color.yellow;
                case "green" -> Color.green;
                default -> Color.white;
            });
            g2.drawString(message, messageX, messageY); // position message
            messageCounter++; // advance message timer
            if (messageCounter > messageDuration) {
                messageCounter = 0; // reset counter
                messageOn = false; // hide message after duration
            }
        }
        if (boxMessageOn) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // smaller font for box message
            int padding = 8; // padding around text
            int textWidth = g2.getFontMetrics().stringWidth(boxMessage); // measure text width
            int textHeight = g2.getFontMetrics().getHeight(); // measure text height
            int frameX = messageX - padding; // compute box X
            int frameY = messageY - textHeight + 8; // adjust baseline
            int frameWidth = textWidth + padding * 2; // box width
            int frameHeight = textHeight + padding / 2; // box height
            // draw semi-transparent background
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10); // filled rounded rect
            // draw border
            g2.setColor(Color.white);
            g2.setStroke(new java.awt.BasicStroke(2)); // 2px border
            g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10); // border round rect
            // draw the box message text (center baseline)
            g2.setColor(Color.white);
            g2.drawString(boxMessage, messageX, messageY); // draw text

            messageCounter++; // advance box message timer
            if (messageCounter > messageDuration) {
                messageCounter = 0; // reset counter
                boxMessageOn = false; // hide box message after duration
            }
        }

        // INTERACT hint
        if (interactOn && gp.gameState == gp.playState) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // set font for hint
            int padding = 8; // padding for hint box
            int textWidth = g2.getFontMetrics().stringWidth(interactMessage); // measure hint width
            int textHeight = g2.getFontMetrics().getHeight(); // get font height
            int frameX = gp.tileSize / 2 - padding; // compute frame X
            int frameY = gp.tileSize * 6 + 20 - textHeight + 8; // compute frame Y
            int frameWidth = textWidth + padding * 2; // width of hint box
            int frameHeight = textHeight + padding / 2; // height of hint box
            g2.setColor(new Color(0, 0, 0, 160)); // semi-transparent bg
            g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10); // fill
            g2.setColor(Color.white); // white border/text
            g2.setStroke(new java.awt.BasicStroke(2)); // 2px stroke
            g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 10, 10); // border
            g2.setColor(Color.white); // text color
            g2.drawString(interactMessage, gp.tileSize / 2, gp.tileSize * 6 + 20); // draw hint text
        }
        if (selectedItem != null) {
        	if (!selectedItem.getName().equals("") && gp.gameState == gp.playState) {
                if (selectedItem.getName().equals("Flashlight")) {
                    String[] options = { "[" + gp.interactKey + "] Use", "["+ gp.dropKey +"] Drop" }; // flashlight options
                    drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28); // draw options window
                    
                    String[] description = {"Type: Tool", "Illuminate dark areas."}; // flashlight description
                    drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20); // draw description
                    
                } else if (selectedItem instanceof Food) {
					String[] options = { "[" + gp.interactKey + "] Eat", "[" + gp.throwKey + "] Throw" , "[" + gp.dropKey + "] Drop" }; // food options
					drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28); // draw options
					Food food = (Food) selectedItem; // cast to Food
					String[] description = {"Type: Food", "Restores " + ((int) (food.restoreValue*100)) +"% stamina when eaten", "Sound value if thrown: Low"}; // food description
					drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20); // draw description
				} 
                else if (selectedItem instanceof Throwable) {
                	String[] options = {"["+gp.throwKey+"] Throw", "["+ gp.dropKey +"] Drop" }; // throwable options
                	drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28); // draw options
                	
                	Throwable throwable = (Throwable) selectedItem; // cast to Throwable
                	String[] description = {
                		    "Type: Throwable",
                		    "Can be thrown to distract guards",
                		    "Sound value if thrown: " +
                		        (throwable.throwSoundIndex == 3 ? "Low" :
                		         throwable.throwSoundIndex == 5 ? "Med" :
                		         throwable.throwSoundIndex == 7 ? "High" : "Unknown"),
                		    "Allowed Throw Radius: " + throwable.getAllowedRadiusTiles() + " tiles."
                		}; // dynamic description for throwable
                	drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20); // draw description
                } else if (selectedItem instanceof Key) {
				String[] options = { "[" + gp.dropKey + "] Drop" }; // key options
				drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28); // draw options
				String[] description = {"Type: Key", "Unlock doors of matching color."}; // key description
				drawSubWindow(gp.tileSize * 11, gp.tileSize * 3, description, 20); // draw description
			}
                else {
                	String[] options = { "[" + gp.dropKey + "] Drop" }; // default single option
				drawSubWindow(gp.tileSize / 2, gp.tileSize * 6, options, 28); // draw options
                }
                
            }
		}
        
        if (showThrowRadius) {
            drawThrowRadius(activeThrowable); // render throw radius overlay
        }

    }
    
    public void drawThrowRadius(Throwable item) {
        if (item == null) return; // nothing to draw if no item

        // allowed radius in tiles (centered on player tile)
        int radiusTiles = item.getAllowedRadiusTiles(); // radius from throwable

        // player tile
        int playerCol = gp.player.worldX / gp.tileSize; // player's tile column
        int playerRow = gp.player.worldY / gp.tileSize; // player's tile row

        // Save old composite/stroke so we can restore later
        java.awt.Composite oldComp = g2.getComposite(); // save composite
        java.awt.Stroke oldStroke = g2.getStroke(); // save stroke

        // translucent fill for in-range tiles
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.15f)); // low-alpha composite
        g2.setColor(new java.awt.Color(50, 120, 220)); // translucent fill (alpha via composite)

        // iterate bounding box (clamped to world)
        int fromCol = Math.max(0, playerCol - radiusTiles); // left bound
        int toCol   = Math.min(gp.maxWorldCol - 1, playerCol + radiusTiles); // right bound
        int fromRow = Math.max(0, playerRow - radiusTiles); // top bound
        int toRow   = Math.min(gp.maxWorldRow - 1, playerRow + radiusTiles); // bottom bound

        for (int c = fromCol; c <= toCol; c++) {
            for (int r = fromRow; r <= toRow; r++) {
                int dx = c - playerCol; // delta x tiles
                int dy = r - playerRow; // delta y tiles
                double dist = Math.sqrt(dx * dx + dy * dy); // euclidean distance in tiles
                if (dist <= radiusTiles + 0.0001) { // inside circle (allow small epsilon)
                    // compute screen coords for this tile
                    int screenX = c * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // tile screen X
                    int screenY = r * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // tile screen Y

                    // only draw tiles that are on-screen (simple frustum cull)
                    if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth || screenY + gp.tileSize < 0 || screenY > gp.screenHeight) {
                        continue; // skip off-screen tiles
                    }

                    g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize); // fill tile rectangle
                }
            }
        }

        // outline the hovered tile with a highlighted border
        if (gp.hoveredTileCol >= 0 && gp.hoveredTileRow >= 0) {
            int dxH = gp.hoveredTileCol - playerCol; // hovered delta x
            int dyH = gp.hoveredTileRow - playerRow; // hovered delta y
            double distH = Math.sqrt(dxH * dxH + dyH * dyH); // hovered distance

            if (distH <= radiusTiles + 0.0001) {
                // strong highlight for hovered tile inside range
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f)); // full alpha
                g2.setStroke(new java.awt.BasicStroke(3f)); // thicker stroke
                g2.setColor(new java.awt.Color(220, 220, 50)); // yellow-ish outline
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // hovered screen X
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // hovered screen Y
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw highlight
            } else {
                // hovered out-of-range: dim outline (optional)
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.9f)); // near full alpha
                g2.setStroke(new java.awt.BasicStroke(2f)); // thinner stroke
                g2.setColor(new java.awt.Color(140, 140, 140)); // gray outline
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // hovered screen X
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // hovered screen Y
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw dim outline
            }
        }

        // outline the selected tile (from mouse click) — only if inside range draw as "selectable"
        if (gp.selectedThrowCol >= 0 && gp.selectedThrowRow >= 0) {
            int dxS = gp.selectedThrowCol - playerCol; // selected delta x
            int dyS = gp.selectedThrowRow - playerRow; // selected delta y
            double distS = Math.sqrt(dxS * dxS + dyS * dyS); // selected distance

            int sX = gp.selectedThrowCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // selected screen X
            int sY = gp.selectedThrowRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // selected screen Y

            // only draw selection if it's on-screen
            if (!(sX + gp.tileSize < 0 || sX > gp.screenWidth || sY + gp.tileSize < 0 || sY > gp.screenHeight)) {
                if (distS <= radiusTiles + 0.0001) {
                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f)); // full alpha
                    g2.setStroke(new java.awt.BasicStroke(3f)); // 3px stroke
                    g2.setColor(new java.awt.Color(80, 200, 100)); // green outline for selected target
                    g2.drawRect(sX + 1, sY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw selection

                    if (gp.mouseClicked) {
                        gp.player.throwItem(activeThrowable, gp.selectedThrowCol, gp.selectedThrowRow); // perform throw action

                        // consume click and close the throw UI
                        gp.mouseClicked = false; // clear click state
                        showThrowRadius = false; // hide radius UI
                        activeThrowable = null; // clear active throwable
                        gp.selectedThrowCol = -1; // reset selection col
                        gp.selectedThrowRow = -1; // reset selection row
                        selectedItem = null; // clear selectedItem
                    }
                } else {
                    // selected out-of-range: show a dim/red outline to indicate invalid target
                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f)); // full alpha
                    g2.setStroke(new java.awt.BasicStroke(3f)); // 3px stroke
                    g2.setColor(new java.awt.Color(200, 80, 80)); // red outline for invalid selection
                    g2.drawRect(sX + 1, sY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw invalid outline
                }
            }
        }

        // restore
        g2.setComposite(oldComp); // restore original composite
        g2.setStroke(oldStroke); // restore original stroke
    
	
	

        // outline the hovered tile with a highlighted border
        if (gp.hoveredTileCol >= 0 && gp.hoveredTileRow >= 0) {
            // check if hovered is inside radius — if not, draw it but in a dim color (optional)
            int dxH = gp.hoveredTileCol - playerCol; // hovered delta x (repeat section; harmless duplication)
            int dyH = gp.hoveredTileRow - playerRow; // hovered delta y
            double distH = Math.sqrt(dxH * dxH + dyH * dyH); // hovered distance

            if (distH <= radiusTiles + 0.0001) {
                // strong highlight for hovered tile inside range
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f)); // full alpha
                g2.setStroke(new java.awt.BasicStroke(3f)); // thicker stroke
                g2.setColor(new java.awt.Color(220, 220, 50)); // yellow-ish outline
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // hovered screen X
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // hovered screen Y
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw highlight
            } else {
                // hovered out-of-range: dim outline (optional)
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.9f)); // near full alpha
                g2.setStroke(new java.awt.BasicStroke(2f)); // thinner stroke
                g2.setColor(new java.awt.Color(140, 140, 140)); // gray outline
                int screenX = gp.hoveredTileCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // hovered screen X
                int screenY = gp.hoveredTileRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // hovered screen Y
                g2.drawRect(screenX + 1, screenY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw dim outline
            }
        }

        // outline the selected tile (from mouse click) with a distinct color
        if (gp.selectedThrowCol >= 0 && gp.selectedThrowRow >= 0) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f)); // full alpha
            g2.setStroke(new java.awt.BasicStroke(3f)); // 3px stroke
            g2.setColor(new java.awt.Color(80, 200, 100)); // green outline for selected target
            int sX = gp.selectedThrowCol * gp.tileSize - gp.player.worldX + gp.player.getScreenX(); // selected X
            int sY = gp.selectedThrowRow * gp.tileSize - gp.player.worldY + gp.player.getScreenY(); // selected Y
            g2.drawRect(sX + 1, sY + 1, gp.tileSize - 2, gp.tileSize - 2); // draw selection
        }

        // restore
        g2.setComposite(oldComp); // restore composite again (safe idempotent restore)
        g2.setStroke(oldStroke); // restore stroke again
        
        // Show text hint
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // set font for hint
        String hint = "Click within radius to throw"; // hint text
        int hintWidth = g2.getFontMetrics().stringWidth(hint); // measure hint width
        g2.setColor(new Color(0, 0, 0, 160)); // background color for hint
        g2.fillRoundRect((gp.screenWidth - hintWidth) / 2 - 10, gp.screenHeight - gp.tileSize - 40, hintWidth + 20, 40, 10, 10); // draw hint background
        g2.setColor(Color.white); // text color
        g2.drawString(hint, (gp.screenWidth - hintWidth) / 2, gp.screenHeight - gp.tileSize - 15); // draw hint text
        
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
                    case 0: // LOAD GAME: show load sub-screen
                        titleScreenState = 7;
                        commandNum = 0; // reset local command index for sub-screen
                        break;
                    case 1: // CHARACTERS: show character selection sub-screen
                        titleScreenState = 2;
                        commandNum = 0;
                        gp.player.currentSkinIndex = gp.player.equippedSkinIndex; // start on equipped skin
                        break;
                    case 2: // KEYBINDINGS: show keybinds sub-screen
                        titleScreenState = 3;
                        commandNum = 0;
                        keybindSelectedIndex = 0; // select first action
                        awaitingKeybind = false; // ensure not in awaiting mode
                        break;
                    case 3: // INSTRUCTIONS: show instructions screen
                    	titleScreenState = 4;
                    	commandNum = 0;
                    	keybindSelectedIndex = 0;
                    	awaitingKeybind = false;
                        break;
                    case 4: // EXIT: close the game
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
        
        if (titleScreenState == 7) {
        	if (uiUp) { // up navigation pressed
                commandNum--; // move selection up
                if (commandNum < 0) commandNum = 3; // wrap around top->bottom
                uiUp = false; // consume the input (edge-trigger)
            }
            if (uiDown) { // down navigation pressed
                commandNum++; // move selection down
                if (commandNum > 3) commandNum = 0; // wrap bottom->top
                uiDown = false; // consume
            }
            if (uiConfirm) { // user activated the currently selected menu entry
                switch (commandNum) {
                    case 0: // LOAD GAME: show load sub-screen
                        titleScreenState = 5;
                        commandNum = 0; // reset local command index for sub-screen
                        break;
                    case 1: // CHARACTERS: show character selection sub-screen
                        titleScreenState = 1;
                        commandNum = 0;
                        break;
                    case 2: // KEYBINDINGS: show keybinds sub-screen
                        titleScreenState = 0;
                        commandNum = 0;
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
            String[] menu = { "PLAY", "CHARACTERS", "KEYBINDINGS", "INSTRUCTIONS", "EXIT" }; // menu labels
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
        } else if (titleScreenState == 7) {
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
             String[] menu = { "NEW GAME", "LOAD GAME", "BACK" }; // menu labels
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
        }
        
        else if (titleScreenState == 1) { // LOAD screen
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

    

    public void drawTaskScreen() { // start of drawTaskScreen method
		int x = gp.tileSize * 1; // left padding
		int y = gp.tileSize * 1; // top padding
		int width = gp.screenWidth - (gp.tileSize * 2); // width of the task box
		int height = gp.screenHeight - (gp.tileSize * 2); // height of the task box

		// Draw panel background and border
		Color c = new Color(0, 0, 0); // semi-transparent black (actually fully opaque here)
		g2.setColor(c); // set current paint color to c
		g2.fillRoundRect(x, y, width, height, 35, 35); // filled rounded rect for panel background
		c = new Color(255, 255, 255); // white for border
		g2.setColor(c); // set color to white for border
		g2.setStroke(new java.awt.BasicStroke(5)); // thicker stroke for border
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25); // draw border inside the panel
		

		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F)); // set font for task text
		x += gp.tileSize; // apply inner padding to x
		y += gp.tileSize; // apply inner padding to y
		
	} // end of drawTaskScreen
    
    public void drawSpeedRunTimer(Graphics2D g2) { // start of drawSpeedRunTimer

        int maxTimeSeconds = 0; // initialize max time in seconds

        switch (gp.level) // choose max time by level
        {
            case 1 -> maxTimeSeconds = 300; // level 1 -> 300s
            case 2 -> maxTimeSeconds = 360; // level 2 -> 360s
            case 3 -> maxTimeSeconds = 420; // level 3 -> 420s
            case 4 -> maxTimeSeconds = 480; // level 4 -> 480s
        }

        int maxFrames = maxTimeSeconds * 60; // convert seconds to frames assuming 60fps
        int remainingFrames = Math.max(0, maxFrames - gp.speedRunTimerFrames); // remaining frames clamp >=0

        int totalSeconds = remainingFrames / 60; // integer seconds remaining
        int minutes = totalSeconds / 60; // minutes part
        int seconds = totalSeconds % 60; // seconds part

        // Warning colors
        if (totalSeconds <= 10) { // critical threshold
            g2.setColor(Color.red); // red color for low time
        } else if (totalSeconds <= 30) { // warning threshold
            g2.setColor(Color.orange); // orange color
        } else {
            g2.setColor(Color.white); // default color
        }
        
        if (totalSeconds == 0) { // if no time left
        	gp.speedRunLost = true; // mark speedrun lost
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F)); // set timer font

        String timeText = String.format("TIME %02d:%02d", minutes, seconds); // formatted time text


        int x = gp.screenWidth - 220; // X position for timer
        int y = gp.screenHeight - 100; // Y position for timer

        g2.drawString(timeText, x, y); // draw the time string
    } // end of drawSpeedRunTimer

    
 // ------------------------------------- TILE SELECT TASK SCREEN -------------------------------------
    public void drawTileSelectTask() { // start of tile select drawing

		// overlay
		g2.setColor(new Color(0, 0, 0, 160)); // translucent black overlay
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // cover full screen

		// panel
		int panelW = gp.tileSize * 10; // panel width in pixels
		int panelH = gp.tileSize * 8; // panel height in pixels
		int panelX = (gp.screenWidth - panelW) / 2; // center horizontally
		int panelY = (gp.screenHeight - panelH) / 2; // center vertically

		// shadow
		int shadowOffset = gp.tileSize / 10; // small shadow offset
		g2.setColor(new Color(0, 0, 0, 120)); // shadow color
		g2.fillRoundRect(panelX + shadowOffset, panelY + shadowOffset, panelW, panelH, 24, 24); // draw shadow

		// main panel
		g2.setColor(new Color(30, 30, 30, 220)); // panel background color
		g2.fillRoundRect(panelX, panelY, panelW, panelH, 24, 24); // draw main panel
		g2.setColor(new Color(255, 255, 255, 70)); // light border color
		g2.setStroke(new BasicStroke(2f)); // thin stroke for border
		g2.drawRoundRect(panelX, panelY, panelW, panelH, 24, 24); // draw panel border

		// title
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 42f)); // title font
		g2.setColor(Color.white); // title color
		g2.drawString("Tile Select", panelX + gp.tileSize / 2, panelY + gp.tileSize); // draw title

		// grid placement
		tsCellSize = gp.tileSize; // cell size equals tile size
		int gridPixel = TS_GRID * tsCellSize; // total grid pixel size
		tsGridX = gp.screenWidth / 2 - gridPixel / 2; // center grid X
		tsGridY = panelY + gp.tileSize * 2; // grid top Y below title

		// cooldow
		if (taskCooldownFrames > 0) { // show cooldown message when tasks locked

			int s = (taskCooldownFrames + 59) / 60; // seconds remaining rounded up

			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f)); // cooldown font
			String t = "Tasks locked. Try again in " + s + "s"; // message text
			int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(t) / 2; // center X
			int y = gp.screenHeight / 2; // center Y

			g2.setColor(Color.lightGray); // draw in light gray
			g2.drawString(t, x, y); // draw cooldown text

			gp.mouseClicked = false; // consume mouse clicks while locked
			gp.keyH.enterPressed = false; // consume enter
			gp.keyH.typedChar = 0; // clear typed char
			gp.keyH.backspacePressed = false; // clear backspace

			//escape handler already handles escape
			return; // exit early while locked
		}
		if (!tileSelectGenerated) { // lazy-generate the pattern when first entering

			// clear grids
			for (int r = 0; r < TS_GRID; r++) { // iterate rows
				for (int c = 0; c < TS_GRID; c++) { // iterate cols
					tsPattern[r][c] = false; // clear pattern flag
					tsSelected[r][c] = false; // clear selected flag
				}
			}

			// pick 6 random tiles
			int placed = 0; // count placed flashes
			while (placed < TS_FLASH_COUNT) { // until 6 placed
				int r = (int) (Math.random() * TS_GRID); // random row
				int c = (int) (Math.random() * TS_GRID); // random col
				if (!tsPattern[r][c]) { // if not already chosen
					tsPattern[r][c] = true; // mark as flash tile
					placed++; // increment count
				}
			}

			tsPhase = 0; // set to flash phase
			tsTimer = tsFlashFrames; // set flash timer
			tsResult = false; // clear result

			tileSelectGenerated = true; // mark generated
		}

		//  phase timing
		if (tsPhase == 0) { // flash phase logic
			tsTimer--; // decrement timer
			if (tsTimer <= 0) { // finished flash
				tsPhase = 1; // switch to blank pause
				tsTimer = tsBlankFrames; // set blank timer
			}
		} else if (tsPhase == 1) { // blank pause phase logic
			tsTimer--; // decrement blank timer
			if (tsTimer <= 0) { // finished blank
				tsPhase = 2; // go to input phase
			}
		}

		//instructions
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f)); // small instruction font
		g2.setColor(new Color(220, 220, 220)); // instruction color

		String instr = ""; // instruction text holder
		if (tsPhase == 0) instr = "Memorize the 6 tiles..."; // during flash
		else if (tsPhase == 1) instr = "Wait..."; // during blank
		else if (tsPhase == 2) instr = "Click the 6 tiles, then press ENTER to submit."; // during input
		else instr = "Press ENTER to continue"; // during feedback

		g2.drawString(instr, panelX + gp.tileSize / 2, panelY + gp.tileSize + 40); // render instruction text

		// draw grid
		for (int r = 0; r < TS_GRID; r++) { // for each row
			for (int c = 0; c < TS_GRID; c++) { // for each col

				int x = tsGridX + c * tsCellSize; // cell X pixel
				int y = tsGridY + r * tsCellSize; // cell Y pixel

				// base tile
				g2.setColor(new Color(60, 60, 60, 220)); // base tile color
				g2.fillRoundRect(x, y, tsCellSize, tsCellSize, 8, 8); // draw base tile

				// show flash tiles only during phase 0
				if (tsPhase == 0 && tsPattern[r][c]) { // if flashing and this tile is in pattern
					g2.setColor(new Color(240, 220, 80, 230)); // highlight color
					g2.fillRoundRect(x, y, tsCellSize, tsCellSize, 8, 8); // draw highlighted tile
				}

				// show selected tiles during input
				if (tsPhase == 2 && tsSelected[r][c]) { // if input phase and tile selected
					g2.setColor(new Color(120, 220, 140, 220)); // selected color
					g2.fillRoundRect(x, y, tsCellSize, tsCellSize, 8, 8); // draw selected tile
				}

				// outline
				g2.setColor(new Color(255, 255, 255, 90)); // outline color
				g2.drawRoundRect(x, y, tsCellSize, tsCellSize, 8, 8); // draw outline
			}
		}

		// input phase
		if (tsPhase == 2) { // only handle input when in phase 2

			// handle mouse 
			if (gp.mouseClicked) { // if mouse was clicked
				gp.mouseClicked = false; // consume click

				int mx = gp.mouseX; // mouse X
				int my = gp.mouseY; // mouse Y

				// inside grid
				if (mx >= tsGridX && mx < tsGridX + TS_GRID * tsCellSize &&
					my >= tsGridY && my < tsGridY + TS_GRID * tsCellSize) { // if click inside grid

					int col = (mx - tsGridX) / tsCellSize; // compute clicked column
					int row = (my - tsGridY) / tsCellSize; // compute clicked row

					// toggle selection
					tsSelected[row][col] = !tsSelected[row][col];} // flip selected flag
			}

			// count selected
			int selectedCount = 0; // number of tiles currently selected
			for (int r = 0; r < TS_GRID; r++) { // iterate rows
				for (int c = 0; c < TS_GRID; c++) { // iterate cols
					if (tsSelected[r][c]) selectedCount++; // increment if selected
				}
			}

			// show selected count
			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16f)); // small font for count
			g2.setColor(new Color(200, 200, 200, 170)); // dim color
			String countText = "Selected: " + selectedCount + " / " + TS_FLASH_COUNT; // count text
			int cx = panelX + gp.tileSize / 2; // count X location
			int cy = panelY + panelH - gp.tileSize / 2; // count Y location
			g2.drawString(countText, cx, cy); // draw count

			// ENTER submits only when 6 selected 
			if (selectedCount == TS_FLASH_COUNT && gp.keyH.enterPressed) { // if exact number selected and enter pressed
				gp.keyH.enterPressed = false; // consume enter

				boolean ok = true; // assume ok until mismatch found
				for (int r = 0; r < TS_GRID; r++) { // check each cell
					for (int c = 0; c < TS_GRID; c++) {
						if (tsSelected[r][c] != tsPattern[r][c]) { // mismatch between selection and pattern
							ok = false; // set ok false
							break; // break inner loop
						}
					}
					if (!ok) break; // break outer loop if not ok
				}

				tsResult = ok; // store result
				tsPhase = 3; // move to feedback phase
			}

			// eat enter if pressed early 
			if (gp.keyH.enterPressed && selectedCount != TS_FLASH_COUNT) { // if enter but not enough selected
				gp.keyH.enterPressed = false;} // consume enter
		}

		//feedback pase
		if (tsPhase == 3) { // feedback phase logic

			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36f)); // feedback font
			String msg = tsResult ? "Correct!" : "Incorrect!"; // result message
			g2.setColor(tsResult ? new Color(120, 220, 140) : new Color(240, 120, 120)); // result color

			int mx = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(msg) / 2; // center msg X
			int my = panelY + panelH - gp.tileSize; // message Y near bottom of panel
			g2.drawString(msg, mx, my); // draw result message

			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f)); // hint font
			g2.setColor(new Color(220, 220, 220)); // hint color
			String hint = "Press ENTER to continue"; // hint text
			int hx = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(hint) / 2; // center hint X
			g2.drawString(hint, hx, my + 30); // draw hint below result

			if (gp.keyH.enterPressed) { // if enter pressed to continue
				gp.keyH.enterPressed = false; // consume enter

				// reset generator so next entry generates a new pattern)
				tileSelectGenerated = false; // allow new pattern next time

				if (tsResult) { // if correct
					handleTaskSuccess("Task Completed!"); // success handler
				} else {
					handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
							"Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // failure handler
				}
			}
		}
	} // end of drawTileSelectTask
	
    
    // ------------------------ DRAW MATH TASK ------------------------
    public void drawMathTask() { // start of math task drawing

        // Rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable AA
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // enable text AA

        // Dim background
        Color overlay = new Color(0, 0, 0, 160); // translucent overlay color
        g2.setColor(overlay); // set overlay color
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // draw overlay

        // Panel layout
        int panelW = gp.tileSize * 10; // panel width
        int panelH = gp.tileSize * 7; // panel height
        int panelX = (gp.screenWidth - panelW) / 2; // center X
        int panelY = (gp.screenHeight - panelH) / 2; // center Y
        int arc = 28; // corner radius for rounded rects

        // drop shadow
        int shadowOffset = gp.tileSize / 8; // small drop shadow offset
        g2.setColor(new Color(0, 0, 0, 120)); // shadow color
        g2.fillRoundRect(panelX + shadowOffset, panelY + shadowOffset, panelW, panelH, arc, arc); // draw shadow

        // panel gradient background
        GradientPaint gpBack = new GradientPaint(panelX, panelY,
                new Color(60, 63, 65), panelX, panelY + panelH,
                new Color(42, 45, 48)); // vertical gradient colors
        g2.setPaint(gpBack); // set paint to gradient
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw gradient panel

        // inner padding and separator line
        int pad = gp.tileSize / 3; // inner padding
        int innerX = panelX + pad; // inner left
        int innerY = panelY + pad; // inner top
        int innerW = panelW - pad * 2; // inner width

        // Title with subtle shadow
        String title = "Math Task"; // title text
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.9f); // title font size
        g2.setFont(titleFont); // apply font
        g2.setColor(new Color(0, 0, 0, 120)); // shadow color
        g2.drawString(title, innerX + 3, innerY + (int)(gp.tileSize * 0.9f) + 3); // draw shadow slightly offset
        g2.setColor(new Color(230, 230, 230)); // title color
        g2.drawString(title, innerX, innerY + (int)(gp.tileSize * 0.9f)); // draw title

        // Level badge (top-right)
        String lvl = "Level " + gp.level; // level label
        Font badgeFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f); // badge font
        int badgeW = gp.tileSize * 3; // badge width
        int badgeH = gp.tileSize / 2; // badge height
        int badgeX = panelX + panelW - pad - badgeW; // badge X pos
        int badgeY = innerY - gp.tileSize/6; // badge Y pos slightly above innerY
        g2.setColor(new Color(255, 200, 60)); // badge background color
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 12, 12); // draw badge background
        g2.setColor(Color.BLACK); // badge text color
        g2.setFont(badgeFont); // set badge font
        FontMetrics fmBadge = g2.getFontMetrics(); // metrics for badge text
        int bx = badgeX + (badgeW - fmBadge.stringWidth(lvl)) / 2; // center text in badge X
        int by = badgeY + ((badgeH - fmBadge.getHeight()) / 2) + fmBadge.getAscent(); // center text in badge Y
        g2.drawString(lvl, bx, by); // draw level string in badge

        // instruction under title
        Font instrFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f); // instruction font
        g2.setFont(instrFont); // apply instruction font
        g2.setColor(new Color(200, 200, 200)); // instruction color
        String instr = "Solve the equation below (NO BEDMAS)"; // instruction text
        g2.drawString(instr, innerX, innerY + (int)(gp.tileSize * 1.6f)); // draw instruction

        // divider
        int dividerY = innerY + (int)(gp.tileSize * 1.9f); // divider Y position
        g2.setStroke(new BasicStroke(1f)); // thin stroke for divider
        g2.setColor(new Color(255, 255, 255, 30)); // divider color
        g2.drawLine(innerX, dividerY, innerX + innerW, dividerY); // draw divider across inner width

        // ----- GLOBAL COOLDOWN: block input if active -----
        if (taskCooldownFrames > 0) { // if global cooldown active
            Font big = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f); // big font
            g2.setFont(big); // set font
            g2.setColor(new Color(180, 180, 180)); // gray color
            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s"; // message
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2; // center X for locked
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2; // center Y
            g2.drawString(locked, lx, ly); // draw locked message

            // clear any input flags while locked so nothing sneaks through
            gp.keyH.typedChar = 0; // clear typed char
            gp.keyH.backspacePressed = false; // clear backspace flag
            gp.keyH.enterPressed = false; // clear enter flag
            gp.keyH.escapePressed = false; // clear escape flag
            return; // exit early while locked
        }

        // Initialize per-task timer / counters on first entry
        if (questionsAsked == 0 && taskTimerFrames == 0 && !taskGenerated) { // first-time initialization
            switch (gp.level) {
                case 1 -> taskTimeLimitFrames = 30 * 60; // 30s for level 1
                case 2 -> taskTimeLimitFrames = 45 * 60; // 45s for level 2
                case 3 -> taskTimeLimitFrames = 60 * 60; // 60s for level 3
                default  -> taskTimeLimitFrames = 75 * 60; // 75s for higher levels
            }
            taskTimerFrames = taskTimeLimitFrames; // set countdown frames
            correctCount = 0; // reset correct counter
            wrongCount = 0; // reset wrong counter
            questionsAsked = 0; // reset asked counter
        }

        // Escape to exit immediately
        if (gp.keyH.escapePressed) { // if escape pressed
            gp.keyH.escapePressed = false; // consume escape
            resetAllTaskState(); // reset task state
            gp.gameState = gp.playState; // return to play state
            return; // exit method
        }

        // Countdown the timer (only while not viewing feedback)
        if (taskTimerFrames > 0 && !answerChecked) taskTimerFrames--; // decrement timer if running and not in feedback
        if (taskTimerFrames <= 0 && !answerChecked) { // if time ran out and no feedback shown
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
            return; // exit
        }

        // GENERATE QUESTION (once)
        if (!taskGenerated) { // if question not yet generated
            String[] ops = {"+", "-", "*", "/"}; // operator choices
            int n1 = (int)(Math.random()*10)+1; // random number 1..10
            int n2 = (int)(Math.random()*10)+1; // random number 1..10
            int n3 = (int)(Math.random()*10)+1; // random number 1..10
            int n4 = (int)(Math.random()*10)+1; // random number 1..10
            int n5 = (int)(Math.random()*10)+1; // random number 1..10
            String op1 = ops[(int)(Math.random()*ops.length)]; // random operator 1
            String op2 = ops[(int)(Math.random()*ops.length)]; // random operator 2
            int temp = 0, result = 0; // temporaries for calculation

            // Level-specific logic (ensures integer division)
            if (gp.level == 1) { // single operation questions
                if (op1.equals("/")) { // force divisible pair for division
                    int q = (int)(Math.random()*9)+1; // random multiplier 1..9
                    n2 = (int)(Math.random()*9)+1; // divisor 1..9
                    n1 = n2 * q; // make n1 divisible by n2
                }
                question = n1 + " " + op1 + " " + n2; // build question string
                switch (op1) {
                    case "+" -> result = n1 + n2; // addition
                    case "-" -> result = n1 - n2; // subtraction
                    case "*" -> result = n1 * n2; // multiplication
                    default  -> result = n1 / n2; // division
                }
            } else if (gp.level == 2) { // two-operand expression
                if (op1.equals("/")) { // ensure integer division for the first operator
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
                if (op2.equals("/")) { // ensure integer division for second operator using temp
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n3 = 1; // avoid zero/div by small values
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>(); // collect divisors
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n3 = divs.get((int)(Math.random()*divs.size())); // pick random divisor
                    }
                }
                question = n1 + " " + op1 + " " + n2 + " " + op2 + " " + n3; // build question string
                switch (op2) {
                    case "+" -> result = temp + n3;
                    case "-" -> result = temp - n3;
                    case "*" -> result = temp * n3;
                    default  -> result = temp / n3;
                }
            } else if (gp.level == 3) { // three-operand expression, more complex chaining
                if (op1.equals("/")) { // ensure integer for op1
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
                if (op2.equals("/")) { // ensure integer for op2
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
                if (op1.equals("/")) { // additional check for next division operand
                    int absTemp = Math.abs(temp);
                    if (absTemp <= 1) n4 = 1;
                    else {
                        java.util.List<Integer> divs = new java.util.ArrayList<>();
                        for (int d = 1; d <= absTemp; d++) if (absTemp % d == 0) divs.add(d);
                        n4 = divs.get((int)(Math.random()*divs.size()));
                    }
                }
                question = n1 + " " + op1 + " " + n2 + " " + op2 + " " + n3 + " " + op1 + " " + n4; // build question
                switch (op1) {
                    case "+" -> result = temp + n4;
                    case "-" -> result = temp - n4;
                    case "*" -> result = temp * n4;
                    default  -> result = temp / n4;
                }
            } else { // higher complexity (4 operators / operands)
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

            correctAnswer = result; // store the computed correct integer result
            taskGenerated = true; // mark task as generated
            playerInput = ""; // clear player input
            answerChecked = false; // clear checked flag
        }

        // HANDLE INPUT (blocked earlier if taskCooldownFrames > 0)
        if (!answerChecked) { // only accept input if not already checked

            // typed char for numbers / minus
            if (Character.isDigit(gp.keyH.typedChar) || gp.keyH.typedChar == '-') { // allow digits and minus sign
                playerInput += gp.keyH.typedChar; // append typed char to input
                gp.keyH.typedChar = 0; // clear typed char
            }

            // backspace
            if (gp.keyH.backspacePressed && playerInput.length() > 0) { // if backspace and input non-empty
                playerInput = playerInput.substring(0, playerInput.length() - 1); // remove last char
                gp.keyH.backspacePressed = false; // consume backspace
            }

            // ENTER submits
            if (gp.keyH.enterPressed) { // if enter pressed
                try {
                    int answer = Integer.parseInt(playerInput); // parse integer from input
                    answerCorrect = (answer == correctAnswer); // compare with correct answer
                } catch (Exception e) {
                    answerCorrect = false; // parsing failed -> incorrect
                }
                answerChecked = true; // mark that answer was checked
                gp.keyH.enterPressed = false; // consume enter
            }
        } else {
            // after feedback, ENTER continues
            if (gp.keyH.enterPressed) { // if enter pressed to continue after feedback
                // register the result into counters
                questionsAsked++; // increment questions asked
                if (answerCorrect) correctCount++; // increment correct if correct
                else wrongCount++; // increment wrong if incorrect

                gp.keyH.enterPressed = false; // consume enter

                // fail if 2+ wrong
                if (wrongCount >= 2) {
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
                    return; // exit
                }

                // success if 3 correct
                if (correctCount >= 3) {
                    handleTaskSuccess("Task Completed!"); // success handler
                    return; // exit
                }

                // if 3 questions asked -> evaluate
                if (questionsAsked >= 3) {
                    if (correctCount >= 3) handleTaskSuccess("Task Completed!"); // success if all correct
                    else handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // otherwise fail
                    return; // exit
                }

                // else prepare next question
                taskGenerated = false; // mark to generate next question
                playerInput = ""; // clear input
                answerChecked = false; // clear checked flag
                answerCorrect = false; // clear correctness
            }
        }

        // DRAW TIMER (top-right)
        int secondsLeft = (taskTimerFrames + 59) / 60; // seconds left rounded up
        String timeText = String.format("Time: %d s", secondsLeft); // timer text
        g2.setFont(instrFont); // use instruction font
        g2.setColor(new Color(220,220,220)); // color for the timer text
        FontMetrics tfm = g2.getFontMetrics(); // get font metrics
        int tx = panelX + panelW - pad - tfm.stringWidth(timeText); // compute X position to right align
        int ty = innerY + (int)(gp.tileSize * 0.9f); // compute Y position near top
        g2.drawString(timeText, tx, ty); // draw time text

        // TIMER BAR under the time text (match text width)
        int textWidth  = tfm.stringWidth(timeText); // measure text width
        int textHeight = tfm.getHeight(); // measure text height
        int barW = textWidth; // bar width equals text width
        int barH = Math.max(6, textHeight / 5); // bar height relative to text
        int barX = tx; // bar X same as text X
        int barY = ty + 6; // small gap below text
        float ratio = (float) taskTimerFrames / (float) taskTimeLimitFrames; // fraction of time remaining
        ratio = Math.max(0f, Math.min(1f, ratio)); // clamp ratio to [0,1]
        g2.setColor(new Color(0, 0, 0, 130)); // background track color
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH); // draw track
        Color col =
                ratio > 0.6f ? new Color(120, 220, 140) : // green if plenty of time
                ratio > 0.25f ? new Color(240, 200, 80) : // yellow if moderate
                                new Color(240, 120, 120); // red if low
        int fillW = Math.max(2, (int)(barW * ratio)); // compute fill width
        g2.setColor(col); // set fill color
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH); // draw fill
        g2.setColor(new Color(255, 255, 255, 70)); // subtle border color
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH); // draw border

        // QUESTION DISPLAY (centered)
        Font qFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 1.0f); // question font
        g2.setFont(qFont); // set font
        g2.setColor(new Color(245, 245, 245)); // question color
        FontMetrics qfm = g2.getFontMetrics(); // metrics for qFont
        String qText = "What is " + question + " ?"; // full question string
        int qx = panelX + (panelW - qfm.stringWidth(qText)) / 2; // centered X for question
        int qy = dividerY + (int)(gp.tileSize * 1.4f); // Y position below divider
        g2.drawString(qText, qx, qy); // draw question

        // INPUT BOX (rounded, centered)
        int boxW = innerW - gp.tileSize; // input box width
        int boxH = gp.tileSize; // input box height
        int boxX = panelX + (panelW - boxW) / 2; // center box X
        int boxY = qy + gp.tileSize / 2; // box Y below question

        // box background
        g2.setColor(new Color(30, 33, 36, 200)); // box bg color
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 14, 14); // draw box bg

        // box border
        g2.setStroke(new BasicStroke(2f)); // border stroke
        g2.setColor(new Color(120, 120, 120, 120)); // border color
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 14, 14); // draw border

        // input text
        Font inputFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.7f); // input font
        g2.setFont(inputFont); // set input font
        g2.setColor(new Color(230, 230, 230)); // input text color
        FontMetrics ifm = g2.getFontMetrics(); // metrics for input font
        String displayInput = playerInput.length() > 0 ? playerInput : ""; // show content or blank
        int ttx = boxX + gp.tileSize / 3; // text X inside box with padding
        int tty = boxY + ((boxH - ifm.getHeight()) / 2) + ifm.getAscent(); // vertical centering for text
        g2.drawString(displayInput, ttx, tty); // draw the player's input

        // blinking caret
        if (!answerChecked) { // only blink caret when input is active (not in feedback)
            boolean blink = (System.currentTimeMillis() / 500) % 2 == 0; // simple blink toggle
            if (blink) { // if blink phase visible
                int caretX = ttx + ifm.stringWidth(displayInput); // caret X after input
                int caretY1 = boxY + (boxH / 6); // caret top Y
                int caretY2 = boxY + boxH - (boxH / 6); // caret bottom Y
                g2.setStroke(new BasicStroke(2f)); // caret stroke
                g2.drawLine(caretX, caretY1, caretX, caretY2); // draw caret line
            }
        }

        // RESULT / FEEDBACK
        int feedbackY = boxY + boxH + gp.tileSize / 2; // feedback start Y
        Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.6f); // feedback font
        g2.setFont(feedbackFont); // set feedback font

        if (answerChecked) { // if answer has been evaluated
            String msg = answerCorrect ? "Correct!" : "Wrong! Answer: " + correctAnswer; // message text
            g2.setColor(answerCorrect ? new Color(120, 220, 140) : new Color(240, 120, 120)); // color by correctness
            FontMetrics ffm = g2.getFontMetrics(); // metrics for feedback font
            int fx = panelX + (panelW - ffm.stringWidth(msg)) / 2; // center feedback X
            int fy = feedbackY + ffm.getAscent(); // compute Y baseline for feedback
            g2.drawString(msg, fx, fy); // draw feedback message

            // continue hint
            String hint = "Press ENTER to continue"; // continuation hint
            g2.setFont(instrFont); // use instruction font for hint
            g2.setColor(new Color(200, 200, 200, 180)); // hint color
            FontMetrics hfm = g2.getFontMetrics(); // metrics for hint
            int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2; // center hint X
            g2.drawString(hint, hx, fy + gp.tileSize / 2); // draw hint below feedback

        } else {
            // small helper hint
            String hint = "Type numbers, use BACKSPACE, press ENTER to submit"; // helper text
            g2.setFont(instrFont); // use instruction font
            g2.setColor(new Color(200, 200, 200, 160)); // dim helper color
            FontMetrics hfm = g2.getFontMetrics(); // metrics for helper
            int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2; // center helper X
            g2.drawString(hint, hx, feedbackY + hfm.getAscent()); // draw helper text
        }

        // draw progress (x/3 and wrongs)
        String prog = String.format("Progress: %d / 3   Wrong: %d", correctCount + wrongCount, wrongCount); // progress string
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.20f)); // small font for progress
        g2.setColor(new Color(200,200,200,180)); // progress color
        g2.drawString(prog, innerX, panelY + panelH - pad - gp.tileSize/6); // draw progress at bottom-left of panel
    } // end of drawMathTask
    
    // ------------------------ DRAW Button Match  TASK ------------------------

 // ------------------------ DRAW BUTTON MATCH TASK ------------------------
    public void drawButtonMatchTask() { // start drawButtonMatchTask

        // ==================== RENDERING SETUP ====================
        // Enable anti-aliasing for smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable AA
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // enable text AA

        // Dim background overlay with subtle fade
        g2.setColor(new Color(0, 0, 0, 180)); // translucent overlay color
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // fill entire screen

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 10; // panel width
        int panelH = gp.tileSize * 7; // panel height
        int panelX = (gp.screenWidth - panelW) / 2; // center X
        int panelY = (gp.screenHeight - panelH) / 2; // center Y
        int arc = 30; // corner radius
        int pad = gp.tileSize / 3; // padding

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140)); // darker shadow
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc); // outer shadow
        g2.setColor(new Color(0, 0, 0, 80)); // lighter shadow
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc); // inner shadow

        // Modern dark gradient background
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(35, 40, 50),
            panelX, panelY + panelH, new Color(25, 28, 35)
        ); // gradient from top to bottom
        g2.setPaint(bgGradient); // set paint to gradient
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // fill panel with gradient

        // Accent border with glow effect
        g2.setColor(new Color(100, 150, 255, 120)); // accent color
        g2.setStroke(new BasicStroke(2.5f)); // accent stroke
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw accent border

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) { // if tasks locked globally
            // Display centered cooldown message
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f); // cooldown font
            g2.setFont(cooldownFont); // set font
            g2.setColor(new Color(240, 100, 100)); // red tint

            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s"; // message text
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2; // center X
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2; // center Y
            g2.drawString(locked, lx, ly); // draw locked message

            // Block all inputs during cooldown
            gp.mouseClicked = false; // consume mouse clicks
            gp.keyH.typedChar = 0; // clear typed char
            gp.keyH.backspacePressed = false; // clear backspace
            gp.keyH.enterPressed = false; // clear enter
            gp.keyH.escapePressed = false; // clear escape
            return; // exit early
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) { // if escape pressed
            gp.keyH.escapePressed = false; // consume escape
            resetAllTaskState(); // reset state
            gp.gameState = gp.playState; // go back to play state
            return; // exit
        }

        // ==================== TITLE SECTION ====================
        String title = "Button Match"; // title string
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.95f); // title font
        g2.setFont(titleFont); // set font
        
        int titleY = panelY + (int)(gp.tileSize * 0.9); // Y position for title
        
        // Title shadow for depth
        g2.setColor(new Color(0, 0, 0, 100)); // shadow color
        g2.drawString(title, panelX + pad + 2, titleY + 2); // draw shadow slightly offset
        
        // Title with gradient effect
        g2.setColor(new Color(230, 240, 255)); // main title color
        g2.drawString(title, panelX + pad, titleY); // draw title

        // ==================== DIVIDER LINE ====================
        int dividerY = titleY + (int)(gp.tileSize * 0.5); // compute divider Y
        g2.setStroke(new BasicStroke(1.5f)); // divider stroke
        g2.setColor(new Color(255, 255, 255, 40)); // divider color
        g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY); // draw divider

        // ==================== INITIALIZATION ====================
        if (!buttonMatchGenerated) { // initialize button match state once
            buttonMatchGenerated = true; // mark generated
            buttonMatchResolved = false; // not yet resolved
            buttonMatchStartNano = System.nanoTime(); // record start time in nanoseconds
            buttonMatchFeedback = ""; // clear feedback text
            buttonMatchFeedbackFrames = 0; // reset feedback frame counter
        }

        // ==================== TIMER CALCULATION ====================
        // Calculate elapsed time and remaining countdown
        double elapsed = (System.nanoTime() - buttonMatchStartNano) / 1_000_000_000.0; // seconds elapsed
        double remaining = 5.0 - elapsed; // remaining seconds from 5.0 target

        // Auto-fail if time expires without input
        if (!buttonMatchResolved && remaining <= 0) { // if unresolved and time expired
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                "Too slow. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
            return; // exit
        }

        // ==================== INSTRUCTIONS ====================
        Font infoFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.38f); // info font
        g2.setFont(infoFont); // set font
        g2.setColor(new Color(200, 210, 230)); // info color

        String instruction = "Press ENTER to stop the timer at exactly 0.00s"; // instruction text
        String target = "Target Window: \u00B10.10 seconds"; // secondary instruction showing target window

        int instructY = dividerY + (int)(gp.tileSize * 0.7); // instruction Y
        int targetY = instructY + g2.getFontMetrics().getHeight() + 5; // target Y slightly below instruction

        // Center-align instructions
        int instructX = panelX + (panelW - g2.getFontMetrics().stringWidth(instruction)) / 2; // center X
        int targetX = panelX + (panelW - g2.getFontMetrics().stringWidth(target)) / 2; // center X for target

        g2.drawString(instruction, instructX, instructY); // draw instruction
        
        g2.setColor(new Color(120, 220, 140)); // greenish color for target
        g2.drawString(target, targetX, targetY); // draw target

        // ==================== COUNTDOWN DISPLAY ====================
        Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 1.6f); // large timer font
        g2.setFont(timerFont); // set font
        
        // Dynamic color based on time remaining
        Color timerColor; // compute timer color based on remaining time
        if (remaining > 2.0) {
            timerColor = new Color(120, 220, 140); // Green
        } else if (remaining > 1.0) {
            timerColor = new Color(240, 200, 100); // Yellow
        } else {
            timerColor = new Color(240, 120, 120); // Red
        }
        
        String countdownText = String.format("%.2f", Math.max(0, remaining)); // formatted remaining to 2 decimals
        int countdownX = panelX + (panelW - g2.getFontMetrics().stringWidth(countdownText)) / 2; // center X
        int countdownY = targetY + (int)(gp.tileSize * 1.3); // Y below target

        // Timer glow effect
        g2.setColor(new Color(timerColor.getRed(), timerColor.getGreen(), 
                             timerColor.getBlue(), 60)); // translucent glow
        g2.drawString(countdownText, countdownX + 3, countdownY + 3); // draw glow slightly offset
        
        g2.setColor(timerColor); // main timer color
        g2.drawString(countdownText, countdownX, countdownY); // draw timer text

        // ==================== PROGRESS BAR ====================
        int barW = gp.tileSize * 7; // progress bar width
        int barH = gp.tileSize / 5; // progress bar height
        int barX = panelX + (panelW - barW) / 2; // center bar X
        int barY = countdownY + (int)(gp.tileSize * 0.5); // bar Y below countdown

        // Calculate fill percentage
        float ratio = (float)Math.max(0.0, Math.min(1.0, remaining / 5.0)); // fill ratio clamped to [0,1]

        // Background track with inner shadow
        g2.setColor(new Color(20, 25, 30, 200)); // track color
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH); // draw track

        // Animated fill bar with gradient
        int fillW = Math.max(4, (int)(barW * ratio)); // fill width based on ratio
        GradientPaint barGradient = new GradientPaint(
            barX, barY, timerColor,
            barX + fillW, barY, new Color(timerColor.getRed(), 
                                          timerColor.getGreen(), 
                                          timerColor.getBlue(), 180)
        ); // gradient from timerColor to more translucent variant
        g2.setPaint(barGradient); // set gradient paint
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH); // draw fill

        // Subtle border highlight
        g2.setColor(new Color(255, 255, 255, 50)); // highlight color
        g2.setStroke(new BasicStroke(1.5f)); // stroke for highlight
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH); // draw border highlight

        // ==================== INTERACTIVE BUTTON ====================
        int btnW = gp.tileSize * 6; // button width
        int btnH = (int)(gp.tileSize * 1.3); // button height
        int btnX = panelX + (panelW - btnW) / 2; // center button X
        int btnY = barY + (int)(gp.tileSize * 0.8); // button Y below bar

        // Store button bounds for mouse interaction
        buttonMatchButtonRect.setBounds(btnX, btnY, btnW, btnH); // save button rectangle

        // Button shadow
        g2.setColor(new Color(0, 0, 0, 120)); // shadow color for button
        g2.fillRoundRect(btnX + 3, btnY + 3, btnW, btnH, 22, 22); // draw button shadow

        // Button gradient background
        GradientPaint btnGradient = new GradientPaint(
            btnX, btnY, new Color(50, 55, 65),
            btnX, btnY + btnH, new Color(35, 40, 50)
        ); // button gradient top->bottom
        g2.setPaint(btnGradient); // set paint
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 22, 22); // draw button background

        // Button accent border
        g2.setStroke(new BasicStroke(2.5f)); // border stroke
        g2.setColor(new Color(100, 150, 255, 140)); // accent color
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 22, 22); // draw border

        // Button text with icon
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.55f)); // button text font
        g2.setColor(new Color(240, 245, 255)); // text color
        String btnText = "⏎ PRESS ENTER"; // button label with icon
        int btx = btnX + (btnW - g2.getFontMetrics().stringWidth(btnText)) / 2; // center text X
        int bty = btnY + (btnH - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent(); // center text Y
        g2.drawString(btnText, btx, bty); // draw button text

        // ==================== FEEDBACK DISPLAY ====================
        int feedbackY = btnY + btnH + (int)(gp.tileSize * 0.7); // feedback start Y

        if (buttonMatchResolved) { // if button press already resolved
            // Display result with color-coded feedback
            Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.6f); // feedback font
            g2.setFont(feedbackFont); // set font
            
            Color feedbackColor = buttonMatchFeedback.startsWith("✔") ? 
                new Color(120, 220, 140) : new Color(240, 120, 120); // green for success, red for fail
            
            // Feedback glow
            g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
                                 feedbackColor.getBlue(), 80)); // translucent glow
            int fw = g2.getFontMetrics().stringWidth(buttonMatchFeedback); // feedback width
            int fx = panelX + (panelW - fw) / 2; // center feedback X
            g2.drawString(buttonMatchFeedback, fx + 2, feedbackY + 2); // draw glow offset
            
            g2.setColor(feedbackColor); // solid feedback color
            g2.drawString(buttonMatchFeedback, fx, feedbackY); // draw feedback text

            // Continue prompt
            g2.setFont(infoFont); // use info font for hint
            g2.setColor(new Color(200, 210, 230, 200)); // hint color
            String hint = "Press ENTER to continue"; // hint text
            int hw = g2.getFontMetrics().stringWidth(hint); // hint width
            g2.drawString(hint, panelX + (panelW - hw) / 2, feedbackY + (int)(gp.tileSize * 0.6)); // draw hint

            // Handle continuation input
            if (gp.keyH.enterPressed) { // if enter pressed after feedback
                gp.keyH.enterPressed = false; // consume enter

                if (buttonMatchFeedback.startsWith("✔")) { // check if success
                    handleTaskSuccess("Task Completed!"); // success handler
                } else {
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                        "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // failure handler
                }
            }
            return; // exit after handling resolved feedback
        }

        // ==================== INPUT HANDLING ====================
        boolean pressed = false; // flag for whether the player pressed the button

        // Mouse click detection on button area
        if (gp.mouseClicked) { // if mouse click happened
            gp.mouseClicked = false; // consume click
            if (buttonMatchButtonRect.contains(gp.mouseX, gp.mouseY)) { // check if click inside button
                pressed = true; // mark pressed
            }
        }

        // Keyboard ENTER input (primary method)
        if (gp.keyH.enterPressed) { // if enter pressed
            gp.keyH.enterPressed = false; // consume enter
            pressed = true; // mark pressed
        }

        // ==================== RESULT CALCULATION ====================
        if (pressed) { // if user pressed either via mouse or keyboard
            // Calculate accuracy - target is 0.00 seconds
            double accuracy = Math.abs(remaining - 0.0); // absolute difference from 0.00

            // Check if within acceptable window
            if (accuracy <= buttonMatchWindow) { // inside window tolerance
                buttonMatchFeedback = String.format("✔ PERFECT! (%.3fs precision)", accuracy); // success message
            } else {
                buttonMatchFeedback = String.format("✖ MISSED (%.3fs off target)", accuracy); // miss message
            }

            buttonMatchResolved = true; // mark resolved
            buttonMatchFeedbackFrames = 60; // set feedback frames (unused visually here but stored)
        }

        // ==================== BOTTOM HINT ====================
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f)); // small bottom hint font
        g2.setColor(new Color(180, 190, 210, 180)); // hint color
        String hint = "Stop the timer at 0.00s • Accuracy window: \u00B10.10s • ESC to exit"; // bottom hint text
        int hw = g2.getFontMetrics().stringWidth(hint); // width of hint
        g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 2); // draw bottom hint centered
    } // end drawButtonMatchTask


    
 // ------------------------ DRAW PATTERN SWITCHES TASK ------------------------
    public void drawPatternSwitchTask() { // start of drawPatternSwitchTask method

        // ==================== RENDERING SETUP ====================
        // Enable anti-aliasing for smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // turn on shape anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // turn on text anti-aliasing

        // Dim background overlay with subtle fade
        g2.setColor(new Color(0, 0, 0, 180)); // set overlay color (black, semi-transparent)
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // fill entire screen with overlay

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 10; // panel width in pixels (10 tiles)
        int panelH = gp.tileSize * 7; // panel height in pixels (7 tiles)
        int panelX = (gp.screenWidth - panelW) / 2; // panel X to center horizontally
        int panelY = (gp.screenHeight - panelH) / 2; // panel Y to center vertically
        int arc = 30; // corner radius for rounded rectangles
        int pad = gp.tileSize / 3; // inner padding

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140)); // darker shadow color
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc); // outer shadow layer
        g2.setColor(new Color(0, 0, 0, 80)); // lighter shadow color
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc); // inner shadow layer

        // Modern dark gradient background
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(35, 40, 50), // top color
            panelX, panelY + panelH, new Color(25, 28, 35) // bottom color
        ); // create vertical gradient for panel
        g2.setPaint(bgGradient); // apply gradient paint
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw panel background with gradient

        // Accent border with glow effect
        g2.setColor(new Color(150, 100, 255, 120)); // accent color (purple-ish, translucent)
        g2.setStroke(new BasicStroke(2.5f)); // stroke width for accent border
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw accent border

        // Inner content area
        int innerX = panelX + pad; // left edge of inner content
        int innerY = panelY + pad; // top edge of inner content
        int innerW = panelW - pad * 2; // inner content width

        // ==================== TITLE SECTION ====================
        String title = "Pattern Switches"; // title text
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.95f); // title font sized relative to tile
        g2.setFont(titleFont); // set title font
        
        int titleY = innerY + (int)(gp.tileSize * 0.9f); // Y position for title (slightly below innerY)
        
        // Title shadow for depth
        g2.setColor(new Color(0, 0, 0, 100)); // shadow color for title
        g2.drawString(title, innerX + 2, titleY + 2); // draw title shadow offset by (2,2)
        
        // Title with gradient effect
        g2.setColor(new Color(230, 240, 255)); // main title color
        g2.drawString(title, innerX, titleY); // draw title text

        // ==================== LEVEL BADGE ====================
        String lvl = "Level " + gp.level; // badge text showing current level
        Font badgeFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.42f); // badge font
        FontMetrics badgeFM = g2.getFontMetrics(badgeFont); // metrics for badge font
        
        int badgeW = (int)(gp.tileSize * 2.8); // badge width
        int badgeH = (int)(gp.tileSize * 0.55); // badge height
        int badgeX = panelX + panelW - pad - badgeW; // badge X positioned at top-right inside panel
        int badgeY = innerY - gp.tileSize / 8; // badge Y slightly above inner top

        // Badge shadow
        g2.setColor(new Color(0, 0, 0, 100)); // badge shadow color
        g2.fillRoundRect(badgeX + 2, badgeY + 2, badgeW, badgeH, 14, 14); // draw badge shadow

        // Badge gradient background
        GradientPaint badgeGradient = new GradientPaint(
            badgeX, badgeY, new Color(255, 200, 60), // top color (yellow)
            badgeX, badgeY + badgeH, new Color(255, 170, 30) // bottom color (orange)
        ); // gradient for badge
        g2.setPaint(badgeGradient); // set paint to badge gradient
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 14, 14); // draw badge background

        // Badge border
        g2.setColor(new Color(255, 220, 100)); // light border color for badge
        g2.setStroke(new BasicStroke(1.5f)); // thin border stroke
        g2.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 14, 14); // draw badge border

        // Badge text
        g2.setFont(badgeFont); // set badge font for text
        g2.setColor(new Color(40, 35, 20)); // text color (dark)
        int bx = badgeX + (badgeW - badgeFM.stringWidth(lvl)) / 2; // center text horizontally in badge
        int by = badgeY + ((badgeH - badgeFM.getHeight()) / 2) + badgeFM.getAscent(); // center text vertically in badge
        g2.drawString(lvl, bx, by); // draw level text inside badge

        // ==================== DYNAMIC INSTRUCTIONS ====================
        Font instrFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.36f); // instruction font
        g2.setFont(instrFont); // apply instruction font
        g2.setColor(new Color(200, 210, 230)); // instruction color

        String instr; // holder for dynamic instruction text
        if (patternShowing) { // if currently showing the pattern
            instr = "Memorize the pattern • Watch the flashing sequence"; // instruction for show phase
        } else if (!patternChecked) { // if in input phase and not yet checked
            instr = "Repeat the sequence using keys 1, 2, 3, 4"; // instruction for input phase
        } else { // after checking
            instr = "Press ENTER to continue"; // instruction for feedback/continue
        }
        
        int instrY = innerY + (int)(gp.tileSize * 1.55f); // Y position for instruction text
        g2.drawString(instr, innerX, instrY); // draw instruction text

        // ==================== DIVIDER LINE ====================
        int dividerY = instrY + (int)(gp.tileSize * 0.35f); // Y position for divider line below instruction
        g2.setStroke(new BasicStroke(1.5f)); // stroke for divider
        g2.setColor(new Color(255, 255, 255, 40)); // faint divider color
        g2.drawLine(innerX, dividerY, innerX + innerW, dividerY); // draw divider across inner width

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) { // if global task cooldown active
            // Display centered cooldown message
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f); // big cooldown font
            g2.setFont(cooldownFont); // set font
            g2.setColor(new Color(240, 100, 100)); // redish color
            
            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s"; // message text
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2; // center X for locked text
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2; // center Y for locked text
            g2.drawString(locked, lx, ly); // draw locked text

            // Block all inputs during cooldown
            gp.keyH.typedChar = 0; // clear typed char
            gp.keyH.backspacePressed = false; // clear backspace flag
            gp.keyH.enterPressed = false; // clear enter flag
            gp.keyH.escapePressed = false; // clear escape flag
            return; // exit method while cooldown active
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) { // if escape pressed by user
            gp.keyH.escapePressed = false; // consume escape
            resetAllTaskState(); // reset task-related state
            gp.gameState = gp.playState; // return to play state
            return; // exit method
        }

        // ==================== INITIALIZATION ====================
        if (!patternGenerated) { // if pattern not yet generated for this task
            
            // ==================== DIFFICULTY SCALING ====================
            // Adjust pattern length and time limit based on level
            if (gp.level <= 1) { // easiest levels
                patternLength = 4; // 4 steps
                patternInputLimitFrames = 5 * 60; // 5 seconds input window at 60fps
            } else if (gp.level == 2) { // level 2
                patternLength = 5; // 5 steps
                patternInputLimitFrames = 5 * 60; // 5 seconds
            } else if (gp.level == 3) { // level 3
                patternLength = 6; // 6 steps
                patternInputLimitFrames = 6 * 60; // 6 seconds
            } else { // level 4+
                patternLength = 7; // 7 steps
                patternInputLimitFrames = 6 * 60; // 6 seconds
            }

            // Generate random sequence (1-4 for each position)
            patternSequence = new int[patternLength]; // allocate sequence array
            for (int i = 0; i < patternLength; i++) { // fill array
                patternSequence[i] = (int)(Math.random() * 4) + 1; // random int 1..4
            }

            // Initialize phase states
            patternGenerated = true; // mark generated
            patternShowing = true; // start in showing phase
            patternIndex = 0; // index into sequence for show phase
            patternFlashTimer = 0; // timer for individual flash
            patternGapTimer = 0; // timer for gap between flashes
            patternInputIndex = 0; // index of player's input progress
            patternInputTimerFrames = 0; // frames elapsed during input phase
            patternChecked = false; // not yet checked
            patternSuccess = false; // result not yet determined

            // Clear any pending input
            gp.keyH.typedChar = 0; // clear typed char so previous characters don't leak in
            gp.keyH.backspacePressed = false; // clear backspace flag
            gp.keyH.enterPressed = false; // clear enter flag
        }

        // ==================== GRID LAYOUT SETUP ====================
        int gridSize = gp.tileSize * 4; // total pixel size for the 2x2 button grid
        int gridX = panelX + (panelW - gridSize) / 2; // center grid horizontally inside panel
        int gridY = dividerY + (int)(gp.tileSize * 0.6); // Y position for grid below divider

        int btnSize = gp.tileSize * 2 - gp.tileSize / 4; // individual button size (slightly less than 2 tiles)
        int gap = gp.tileSize / 4; // gap between buttons

        // Calculate button positions (1=top-left, 2=top-right, 3=bottom-left, 4=bottom-right)
        int[] bxPos = new int[5]; // x positions indexed 1..4
        int[] byPos = new int[5]; // y positions indexed 1..4
        bxPos[1] = gridX; // top-left x
        byPos[1] = gridY; // top-left y
        bxPos[2] = gridX + btnSize + gap; // top-right x
        byPos[2] = gridY; // top-right y
        bxPos[3] = gridX; // bottom-left x
        byPos[3] = gridY + btnSize + gap; // bottom-left y
        bxPos[4] = gridX + btnSize + gap; // bottom-right x
        byPos[4] = gridY + btnSize + gap; // bottom-right y

        // ==================== PATTERN SHOWING PHASE ====================
        int highlight = -1; // Track which button should be highlighted (-1 = none)

        if (patternShowing) { // if currently showing the sequence
            
            // Start flash if idle
            if (patternFlashTimer <= 0 && patternGapTimer <= 0) {
                patternFlashTimer = patternFlashFrames; // begin a flash period for the current index
            }

            // Handle flashing animation
            if (patternFlashTimer > 0) { // during flash
                highlight = patternSequence[patternIndex]; // highlight the current sequence button
                patternFlashTimer--; // count down flash timer
                
                // Transition to gap after flash completes
                if (patternFlashTimer <= 0) { // flash finished
                    patternGapTimer = patternGapFrames; // start gap timer
                }
            } 
            // Handle gap between flashes
            else if (patternGapTimer > 0) { // during gap between flashes
                patternGapTimer--; // decrement gap timer
                
                if (patternGapTimer <= 0) { // gap finished -> advance sequence index
                    patternIndex++; // advance to next sequence entry
                    
                    // Move to input phase when sequence complete
                    if (patternIndex >= patternLength) { // completed entire sequence
                        patternShowing = false; // switch to input phase
                        patternIndex = 0; // reset index for potential reuse
                        patternInputIndex = 0; // reset input progress
                        patternInputTimerFrames = 0; // reset input timer
                    }
                }
            }
        }

        // ==================== PATTERN INPUT PHASE ====================
        if (!patternShowing && !patternChecked) { // only process input when not showing and not yet checked
            
            // Increment input timer
            patternInputTimerFrames++; // count frames while player inputs
            
            // Auto-fail if time limit exceeded
            if (patternInputTimerFrames > patternInputLimitFrames) { // if time ran out
                patternChecked = true; // mark as checked
                patternSuccess = false; // player failed
            }

            // Process keyboard input (1-4 keys only)
            char typed = gp.keyH.typedChar; // capture typed char
            if (typed != 0) { // if a character was typed
                int pressed = -1; // default invalid

                // Map character to button number
                if (typed == '1') pressed = 1; // map '1'
                else if (typed == '2') pressed = 2; // map '2'
                else if (typed == '3') pressed = 3; // map '3'
                else if (typed == '4') pressed = 4; // map '4'

                // Always consume the typed character
                gp.keyH.typedChar = 0; // clear typed char

                // Validate input if it's a valid button
                if (pressed != -1) { // if mapped to a valid button
                    if (pressed == patternSequence[patternInputIndex]) { // correct button for current index
                        // Correct input - advance
                        patternInputIndex++; // move to next input slot
                        
                        // Check if pattern completed successfully
                        if (patternInputIndex >= patternLength) { // if entire sequence entered correctly
                            patternChecked = true; // mark as checked
                            patternSuccess = true; // success
                        }
                    } else { // incorrect input
                        // Wrong input - fail immediately
                        patternChecked = true; // mark as checked
                        patternSuccess = false; // failure
                    }
                }
            }
        }

        // ==================== DRAW INTERACTIVE BUTTONS ====================
        for (int i = 1; i <= 4; i++) { // draw four buttons (1..4)
            
            // Button shadow
            g2.setColor(new Color(0, 0, 0, 120)); // shadow color for button
            g2.fillRoundRect(bxPos[i] + 3, byPos[i] + 3, btnSize, btnSize, 18, 18); // draw button shadow

            // Base button gradient
            GradientPaint btnGradient = new GradientPaint(
                bxPos[i], byPos[i], new Color(50, 55, 65), // top color for button
                bxPos[i], byPos[i] + btnSize, new Color(35, 40, 50) // bottom color
            ); // create gradient for base button
            g2.setPaint(btnGradient); // apply gradient
            g2.fillRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 18, 18); // draw base button

            // Highlight effect during pattern show phase
            if (patternShowing && highlight == i) { // if this button should be highlighted during show
                // Bright yellow glow overlay
                GradientPaint glowGradient = new GradientPaint(
                    bxPos[i], byPos[i], new Color(255, 230, 120, 150), // bright top color (semi-translucent)
                    bxPos[i], byPos[i] + btnSize, new Color(255, 200, 80, 100) // slightly darker bottom
                ); // gradient for glow
                g2.setPaint(glowGradient); // set paint to glow gradient
                g2.fillRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 18, 18); // draw glow overlay
            }

            // Standard border
            g2.setStroke(new BasicStroke(2f)); // normal border stroke
            g2.setColor(new Color(100, 120, 150, 140)); // border color (muted)
            g2.drawRoundRect(bxPos[i], byPos[i], btnSize, btnSize, 18, 18); // draw border

            // Enhanced border during highlight
            if (patternShowing && highlight == i) { // extra emphasis for highlighted button
                g2.setStroke(new BasicStroke(4f)); // thicker stroke for enhanced border
                g2.setColor(new Color(255, 230, 120)); // bright border color
                g2.drawRoundRect(bxPos[i] - 2, byPos[i] - 2, btnSize + 4, btnSize + 4, 20, 20); // draw enhanced border
                
                // Outer glow ring
                g2.setStroke(new BasicStroke(2f)); // thinner stroke for outer ring
                g2.setColor(new Color(255, 230, 120, 80)); // translucent glow color
                g2.drawRoundRect(bxPos[i] - 5, byPos[i] - 5, btnSize + 10, btnSize + 10, 23, 23); // draw outer glow ring
            }

            // Button number label
            Font numFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 1.0f); // number font size
            g2.setFont(numFont); // set font for number
            
            Color numColor = (patternShowing && highlight == i) ? 
                new Color(40, 35, 20) : new Color(230, 240, 255); // dark color when highlighted, light otherwise
            g2.setColor(numColor); // apply number color
            
            String n = String.valueOf(i); // label string for button number
            FontMetrics nfm = g2.getFontMetrics(); // metrics for current font
            int nx = bxPos[i] + (btnSize - nfm.stringWidth(n)) / 2; // center number horizontally in button
            int ny = byPos[i] + (btnSize - nfm.getHeight()) / 2 + nfm.getAscent(); // center number vertically
            g2.drawString(n, nx, ny); // draw button number
        }

        // ==================== TIMER DISPLAY (INPUT PHASE ONLY) ====================
        if (!patternShowing && !patternChecked) { // show timer only during input phase and before check
            Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f); // timer font
            g2.setFont(timerFont); // set timer font
            
            // Calculate remaining time
            int secLeft = (patternInputLimitFrames - patternInputTimerFrames + 59) / 60; // seconds left rounded up
            if (secLeft < 0) secLeft = 0; // clamp to zero

            // Color-code timer based on remaining time
            Color timerColor;
            if (secLeft > 3) { // plenty of time
                timerColor = new Color(180, 220, 180);
            } else if (secLeft > 1) { // moderate
                timerColor = new Color(240, 200, 100);
            } else { // critical
                timerColor = new Color(240, 120, 120);
            }
            
            g2.setColor(timerColor); // set timer color
            String timeText = "⏱ " + secLeft + "s"; // formatted timer text
            FontMetrics tfm = g2.getFontMetrics(); // metrics for timer font
            int tx = panelX + panelW - pad - tfm.stringWidth(timeText); // right-align timer inside panel
            int ty = titleY; // align vertically near the title
            g2.drawString(timeText, tx, ty); // draw timer text

            // Timer progress bar
            int barW = tfm.stringWidth(timeText); // bar width based on text width
            int barH = Math.max(6, tfm.getHeight() / 5); // bar height proportionate to font
            int barX = tx; // bar X aligned with timer text
            int barY = ty + 6; // small gap under timer text

            // Calculate fill ratio
            float ratio = 1f; // default full
            if (patternInputLimitFrames > 0) { // compute ratio only if limit > 0
                ratio = 1f - ((float)patternInputTimerFrames / (float)patternInputLimitFrames); // fraction remaining
            }
            ratio = Math.max(0f, Math.min(1f, ratio)); // clamp ratio to [0,1]

            // Bar background
            g2.setColor(new Color(20, 25, 30, 200)); // bar track background color
            g2.fillRoundRect(barX, barY, barW, barH, barH, barH); // draw track

            // Animated fill with gradient
            int fillW = Math.max(2, (int)(barW * ratio)); // compute fill width
            GradientPaint barGradient = new GradientPaint(
                barX, barY, timerColor,
                barX + fillW, barY, new Color(timerColor.getRed(), 
                                              timerColor.getGreen(), 
                                              timerColor.getBlue(), 150)
            ); // gradient for fill based on timerColor
            g2.setPaint(barGradient); // apply gradient
            g2.fillRoundRect(barX, barY, fillW, barH, barH, barH); // draw fill

            // Bar border
            g2.setColor(new Color(255, 255, 255, 50)); // subtle border color
            g2.setStroke(new BasicStroke(1f)); // border stroke width
            g2.drawRoundRect(barX, barY, barW, barH, barH, barH); // draw bar border
        }

        // ==================== PROGRESS INDICATOR DOTS ====================
        int dotsY = gridY + gridSize + (int)(gp.tileSize * 0.4); // Y position for dots below grid
        int dotSize = gp.tileSize / 5; // dot diameter
        int dotGap = 8; // gap between dots
        int totalDotsW = patternLength * dotSize + (patternLength - 1) * dotGap; // total width of dots row
        int dotsX = panelX + (panelW - totalDotsW) / 2; // center the dots row horizontally

        for (int i = 0; i < patternLength; i++) { // draw one dot for each step in pattern
            int dx = dotsX + i * (dotSize + dotGap); // x for this dot
            
            // Determine dot state
            boolean filled = (!patternShowing && i < patternInputIndex); // filled if input phase and this index already entered
            
            // Dot shadow
            g2.setColor(new Color(0, 0, 0, 80)); // shadow color
            g2.fillOval(dx + 2, dotsY + 2, dotSize, dotSize); // draw shadow slightly offset
            
            // Dot fill
            if (filled) {
                g2.setColor(new Color(120, 220, 140)); // green for filled
            } else {
                g2.setColor(new Color(80, 85, 95)); // gray for empty
            }
            g2.fillOval(dx, dotsY, dotSize, dotSize); // draw dot fill
            
            // Dot border
            g2.setColor(filled ? new Color(150, 240, 170) : new Color(100, 110, 125, 120)); // border color
            g2.setStroke(new BasicStroke(1.5f)); // border stroke
            g2.drawOval(dx, dotsY, dotSize, dotSize); // draw dot border
        }

        // ==================== FEEDBACK DISPLAY ====================
        if (patternChecked) { // if sequence has been checked (success or fail)
            
            Font feedbackFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.65f); // feedback font
            g2.setFont(feedbackFont); // set font

            String feedback = patternSuccess ? "✔ Perfect Match!" : "✖ Pattern Failed"; // choose message
            Color feedbackColor = patternSuccess ? new Color(120, 220, 140) : new Color(240, 120, 120); // color by success/fail
            
            int fbx = panelX + (panelW - g2.getFontMetrics().stringWidth(feedback)) / 2; // center feedback X
            int fby = panelY + panelH - (int)(gp.tileSize * 1.8); // Y position near bottom area

            // Feedback glow
            g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
                                 feedbackColor.getBlue(), 80)); // translucent glow color
            g2.drawString(feedback, fbx + 2, fby + 2); // draw glow offset
            
            g2.setColor(feedbackColor); // solid feedback color
            g2.drawString(feedback, fbx, fby); // draw feedback text

            // Continue prompt
            Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f); // hint font
            g2.setFont(hintFont); // set hint font
            g2.setColor(new Color(200, 210, 230, 200)); // hint color

            String hint = "Press ENTER to continue"; // continuation instruction
            int hx = panelX + (panelW - g2.getFontMetrics().stringWidth(hint)) / 2; // center hint X
            g2.drawString(hint, hx, fby + (int)(gp.tileSize * 0.6)); // draw hint below feedback

            // Handle continuation
            if (gp.keyH.enterPressed) { // if user pressed ENTER
                gp.keyH.enterPressed = false; // consume ENTER
                
                if (patternSuccess) { // success path
                    handleTaskSuccess("Task Completed!"); // notify success
                } else { // failure path
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                        "Task Failed, Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // notify failure
                }
                return; // exit after handling ENTER
            }

        } else { // if not checked yet show bottom help text
            // ==================== BOTTOM HELP TEXT ====================
            String hint = "Watch carefully • Press keys 1-4 in order • ESC to exit"; // help text
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f)); // small font for help
            g2.setColor(new Color(180, 190, 210, 180)); // help color
            FontMetrics hfm = g2.getFontMetrics(); // metrics for help font
            int hx = panelX + (panelW - hfm.stringWidth(hint)) / 2; // center help horizontally
            g2.drawString(hint, hx, panelY + panelH - pad / 2); // draw help near bottom of panel
        }
    } // end drawPatternSwitchTask

    // ------------------------ DRAW VAULT SEQUENCE TASK ------------------------
    public void drawVaultSequenceTask() { // start of drawVaultSequenceTask

        // Cooldown block (MATCHES other tasks)
        if (taskCooldownFrames > 0) { // if global cooldown active

            // Rendering hints (keep consistent)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable AA
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // enable text AA

            // Dim background with smoother fade
            g2.setColor(new Color(0, 0, 0, 180)); // overlay color
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // fill screen

            // Panel size (same as Riddle/Vault panel)
            int panelW = gp.tileSize * 10; // panel width
            int panelH = gp.tileSize * 6; // panel height
            int panelX = (gp.screenWidth - panelW) / 2; // center X
            int panelY = (gp.screenHeight - panelH) / 2; // center Y
            int arc = 30; // corner radius

            // Enhanced shadow with gradient effect
            g2.setColor(new Color(0, 0, 0, 140)); // shadow color
            g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc); // outer shadow
            g2.setColor(new Color(0, 0, 0, 80)); // inner shadow color
            g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc); // inner shadow
            
            // Modern dark background
            g2.setColor(new Color(25, 28, 35, 250)); // dark background color
            g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // fill panel

            // Centered cooldown text
            Font big = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f); // big font
            g2.setFont(big); // set font
            g2.setColor(new Color(220, 80, 80)); // red tint

            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s"; // message
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2; // center X
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2; // center Y
            g2.drawString(locked, lx, ly); // draw message

            // Eat input so nothing types during cooldown
            gp.keyH.typedChar = 0; // clear typed char
            gp.keyH.backspacePressed = false; // clear backspace
            gp.keyH.enterPressed = false; // clear enter
            gp.keyH.escapePressed = false; // clear escape

            return; // exit while cooldown
        }

        // Rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable AA
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // enable text AA

        // Dim background with smoother fade
        g2.setColor(new Color(0, 0, 0, 180)); // overlay color
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // draw overlay

        // Panel dimensions
        int panelW = gp.tileSize * 10; // panel width
        int panelH = gp.tileSize * 6; // panel height
        int panelX = (gp.screenWidth - panelW) / 2; // center X
        int panelY = (gp.screenHeight - panelH) / 2; // center Y
        int arc = 30; // corner radius

        // Enhanced shadow with gradient effect
        g2.setColor(new Color(0, 0, 0, 140)); // shadow color
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc); // outer shadow
        g2.setColor(new Color(0, 0, 0, 80)); // inner shadow
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc); // inner shadow
        
        // Modern dark background
        g2.setColor(new Color(25, 28, 35, 250)); // panel background color
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw panel

        // Accent border
        g2.setColor(new Color(100, 120, 200, 100)); // accent border color
        g2.setStroke(new BasicStroke(2f)); // stroke for accent
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw accent border

        // Title with glow effect
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.85f)); // title font
        String title = "Vault Sequence"; // title text
        int titleX = panelX + gp.tileSize/2; // title X (left-aligned with padding)
        int titleY = panelY + (int)(gp.tileSize * 0.9); // title Y (near top)
        
        // Title glow
        g2.setColor(new Color(100, 120, 200, 80)); // glow color
        g2.drawString(title, titleX + 1, titleY + 1); // draw glow offset
        g2.setColor(new Color(230, 240, 255)); // main title color
        g2.drawString(title, titleX, titleY); // draw title

        // Generate vault once
        if (!vaultGenerated) { // one-time initialization
            vaultGenerated = true; // mark generated

            // time limit
            vaultTimeLimitFrames = 45 * 60; // 45 seconds * 60 fps
            vaultTimerFrames = vaultTimeLimitFrames; // set timer

            // Initialize wrong answer counter
            vaultWrongAnswers = 0; // reset wrong answers

            // pick 4 UNIQUE riddles from your pool
            java.util.HashSet<Integer> used = new java.util.HashSet<>(); // set to ensure uniqueness
            for (int i = 0; i < 4; i++) { // for four riddles
                int idx;
                do {
                    idx = (int)(Math.random() * RIDDLE_QUESTIONS.length); // random index into pool
                } while (used.contains(idx)); // repeat until unique
                used.add(idx); // record usage

                vaultRiddleQ[i] = RIDDLE_QUESTIONS[idx]; // assign question text
                vaultRiddleA[i] = RIDDLE_ANSWERS[idx]; // assign corresponding answer

                // assign a random digit 0-9 for each riddle
                vaultDigits[i] = (int)(Math.random() * 10); // random digit 0..9

                vaultInputs[i] = ""; // initialize typed input for this riddle
                vaultSolved[i] = false; // mark unsolved
            }

            vaultIndex = 0; // start at first riddle
            vaultEnteringCode = false; // not yet entering final code
            vaultFinalInput = ""; // final code input buffer

            vaultFeedback = ""; // clear any feedback text
            vaultFeedbackFrames = 0; // reset feedback frames
        }

        // Escape abort
        if (gp.keyH.escapePressed) { // if user presses escape
            gp.keyH.escapePressed = false; // consume escape
            resetAllTaskState(); // reset task state
            gp.gameState = gp.playState; // return to play state
            return; // exit method
        }

        // Timer tick
        if (vaultTimerFrames > 0) vaultTimerFrames--; // decrement vault timer if positive
        if (vaultTimerFrames <= 0) { // if timer expired
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Vault failed. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
            return; // exit
        }

        // Time display - top right with modern styling
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f)); // font for time display
        int timeSeconds = (vaultTimerFrames + 59) / 60; // seconds left rounded up
        Color timeColor = timeSeconds <= 10 ? new Color(240, 100, 100) : new Color(180, 220, 180); // red if low, greenish otherwise
        g2.setColor(timeColor); // set color for time
        String timeText = "⏱ " + timeSeconds + "s"; // formatted time text
        int tW = g2.getFontMetrics().stringWidth(timeText); // width of time text
        g2.drawString(timeText, panelX + panelW - gp.tileSize/2 - tW, panelY + (int)(gp.tileSize * 0.9)); // draw time aligned to top-right

        // Progress indicators with visual styling
        int solvedCount = 0; // count how many riddles solved
        for (boolean b : vaultSolved) if (b) solvedCount++; // increment count for each solved

        int progressY = panelY + (int)(gp.tileSize * 1.4); // Y base for progress text
        
        // Riddle progress
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.35f)); // smaller progress font
        g2.setColor(new Color(180, 200, 230)); // color for progress labels
        g2.drawString("Riddle: " + (vaultIndex + 1) + " / 4", panelX + gp.tileSize/2, progressY); // draw current riddle index
        
        // Solved count with color coding
        progressY += (int)(gp.tileSize * 0.4); // move down for next line
        Color solvedColor = solvedCount == 4 ? new Color(120, 220, 140) : new Color(200, 200, 200); // green if all solved
        g2.setColor(solvedColor); // set color
        g2.drawString("Solved: " + solvedCount + " / 4", panelX + gp.tileSize/2, progressY); // draw solved count
        
        // Wrong answers indicator
        progressY += (int)(gp.tileSize * 0.4); // move down for next line
        Color wrongColor = vaultWrongAnswers == 1 ? new Color(240, 180, 100) : 
                           vaultWrongAnswers >= 2 ? new Color(240, 100, 100) : new Color(200, 200, 200); // color by mistakes
        g2.setColor(wrongColor); // set wrong color
        g2.drawString("Mistakes: " + vaultWrongAnswers + " / 2", panelX + gp.tileSize/2, progressY); // draw mistakes count

        // If feedback is active, show it and freeze input briefly
        if (vaultFeedbackFrames > 0) { // if a feedback message is currently being shown
            vaultFeedbackFrames--; // decrement feedback frames

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.42f)); // feedback font
            Color feedbackColor = vaultFeedback.startsWith("✔") ? new Color(120, 220, 140) : 
                                 new Color(240, 120, 120); // green for correct, red for wrong
            
            // Feedback glow effect
            g2.setColor(new Color(feedbackColor.getRed(), feedbackColor.getGreen(), 
                                 feedbackColor.getBlue(), 60)); // translucent glow
            int fW = g2.getFontMetrics().stringWidth(vaultFeedback); // width of feedback string
            int fX = panelX + (panelW - fW)/2; // center X for feedback
            int fY = panelY + (int)(gp.tileSize * 3.0); // Y for feedback
            g2.drawString(vaultFeedback, fX + 2, fY + 2); // draw glow offset
            
            g2.setColor(feedbackColor); // solid feedback color
            g2.drawString(vaultFeedback, fX, fY); // draw feedback text

            // eat input while feedback is showing
            gp.keyH.typedChar = 0; // clear typed char
            gp.keyH.backspacePressed = false; // clear backspace
            gp.keyH.enterPressed = false; // clear enter
            return; // exit while feedback visible
        }

        // =========================
        // STAGE A: Solve 4 riddles
        // =========================
        if (!vaultEnteringCode) { // if still solving riddles, not yet entering final code

            // Draw current riddle question with better spacing
            int qX = panelX + gp.tileSize/2; // left padding for question text
            int qY = panelY + (int)(gp.tileSize * 2.6); // starting Y for question block
            int qWArea = panelW - gp.tileSize; // available width for wrapped question text

            Font qFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.42f); // font for question text
            g2.setFont(qFont); // set question font
            g2.setColor(new Color(240, 245, 255)); // question text color

            java.util.List<String> qLines = wrapText(vaultRiddleQ[vaultIndex], g2.getFontMetrics(), qWArea); // wrap question to fit area
            int lineH = g2.getFontMetrics().getHeight(); // height of each wrapped line
            int drawY = qY; // starting Y for drawing lines
            for (String line : qLines) { // draw each wrapped line
                g2.drawString(line, qX, drawY); // draw one line of the question
                drawY += lineH; // advance Y by line height
            }

            // Modern input box with gradient
            int boxW = panelW - gp.tileSize; // input box width
            int boxH = (int)(gp.tileSize * 1.1); // input box height
            int boxX = panelX + gp.tileSize/2; // input box X
            int boxY = panelY + panelH - (int)(gp.tileSize * 1.6); // input box Y near bottom

            // Input box gradient background
            GradientPaint gradient = new GradientPaint(
                boxX, boxY, new Color(245, 248, 255), // light top
                boxX, boxY + boxH, new Color(235, 240, 250) // slightly darker bottom
            ); // gradient for input box
            g2.setPaint(gradient); // set paint
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16); // draw input box background
            
            // Input box border with accent
            g2.setColor(new Color(100, 120, 200, 120)); // accent border color
            g2.setStroke(new BasicStroke(2.5f)); // border stroke
            g2.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16); // draw input box border

            // Show typed input
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.50f)); // font for input text
            g2.setColor(new Color(30, 35, 45)); // input text color

            String curInput = vaultInputs[vaultIndex]; // current buffer for this riddle
            if (curInput.isEmpty()) { // placeholder if empty
                g2.setColor(new Color(150, 160, 180)); // placeholder color
                curInput = "Type your answer..."; // placeholder text
            }
            
            FontMetrics ifm = g2.getFontMetrics(); // metrics for input font
            int itx = boxX + gp.tileSize/3; // text X with padding inside box
            int ity = boxY + ((boxH - ifm.getHeight()) / 2) + ifm.getAscent(); // vertical centering for text
            g2.drawString(curInput, itx, ity); // draw input or placeholder

            // Handle typing
            char typed = gp.keyH.typedChar; // character typed by user
            if (typed != 0) { // if a char exists
                if (Character.isLetterOrDigit(typed) || Character.isWhitespace(typed) || isPunctuation(typed)) { // allow letters, digits, whitespace or punctuation
                    vaultInputs[vaultIndex] += typed; // append typed char to current input
                }
                gp.keyH.typedChar = 0; // consume typed char
            }

            if (gp.keyH.backspacePressed) { // handle backspace deletion
                if (vaultInputs[vaultIndex].length() > 0) { // if buffer non-empty
                    vaultInputs[vaultIndex] = vaultInputs[vaultIndex].substring(0, vaultInputs[vaultIndex].length() - 1); // remove last char
                }
                gp.keyH.backspacePressed = false; // consume backspace
            }

            // Submit with ENTER
            if (gp.keyH.enterPressed) { // if enter pressed
                gp.keyH.enterPressed = false; // consume enter

                String user = normalizeAnswer(vaultInputs[vaultIndex]); // normalize user answer (trim/case/punctuation)
                String correct = normalizeAnswer(vaultRiddleA[vaultIndex]); // normalized correct answer

                if (user.equals(correct)) { // if normalized answers match
                    vaultSolved[vaultIndex] = true; // mark this riddle solved
                    vaultFeedback = "✔ Correct! Digit: " + vaultDigits[vaultIndex]; // feedback showing digit awarded
                    vaultFeedbackFrames = 90; // display feedback for some frames

                    // move to next unsolved riddle
                    int next = -1; // index placeholder for next unsolved
                    for (int i = 0; i < 4; i++) {
                        if (!vaultSolved[i]) { next = i; break; } // pick the next unsolved riddle
                    }

                    if (next == -1) { // if none remain unsolved
                        // all solved -> move to code entry
                        vaultEnteringCode = true; // switch to code entry stage
                    } else {
                        vaultIndex = next; // jump to next unsolved riddle
                    }
                } else { // wrong answer branch
                    // Wrong answer - increment counter
                    vaultWrongAnswers++; // increment mistakes counter
                    
                    if (vaultWrongAnswers >= 2) { // too many mistakes -> fail
                        // Failed - 2 wrong answers
                        handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Too many mistakes. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
                        return; // exit
                    } else { // allow one mistake, show feedback and move on
                        // Show feedback and advance to next riddle
                        vaultFeedback = "✖ Wrong. Moving to next riddle..."; // feedback text
                        vaultFeedbackFrames = 90; // show feedback for a bit
                        
                        // Find next unsolved riddle
                        int next = -1; // placeholder
                        for (int i = 0; i < 4; i++) {
                            if (!vaultSolved[i] && i != vaultIndex) { 
                                next = i; 
                                break; 
                            }
                        }
                        
                        // If no other unsolved riddles, stay on current
                        if (next != -1) {
                            vaultIndex = next; // jump to next unsolved if exists
                        }
                    }
                }
            }

            // Helper text with modern styling
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f)); // help text font
            g2.setColor(new Color(180, 190, 210)); // help text color
            String helpText = "Type answer and press ENTER  •  ESC to exit"; // helper instruction
            int helpW = g2.getFontMetrics().stringWidth(helpText); // width of helper text
            g2.drawString(helpText, panelX + (panelW - helpW)/2, panelY + panelH - gp.tileSize/5); // draw helper centered near bottom

            return; // exit stage A block (still solving riddles)
        }

        // =========================
        // STAGE B: Enter final code
        // =========================
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f)); // font for prompt
        g2.setColor(new Color(230, 240, 255)); // prompt color

        String prompt = "Enter the 4-digit code:"; // prompt text
        int promptW = g2.getFontMetrics().stringWidth(prompt); // width of prompt
        g2.drawString(prompt, panelX + (panelW - promptW)/2, panelY + (int)(gp.tileSize * 2.7)); // draw prompt centered

        // show earned digits with visual styling
        String digitsLine = vaultDigits[0] + "  " + vaultDigits[1] + "  " + vaultDigits[2] + "  " + vaultDigits[3]; // space-separated digits
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.55f)); // digits font
        g2.setColor(new Color(120, 220, 140)); // greenish color for digits
        int digitsW = g2.getFontMetrics().stringWidth(digitsLine); // width of digits line
        g2.drawString(digitsLine, panelX + (panelW - digitsW)/2, panelY + (int)(gp.tileSize * 3.4)); // draw digits centered

        // Final input box with enhanced styling
        int boxW = (int)(gp.tileSize * 4.5); // final input width
        int boxH = (int)(gp.tileSize * 1.2); // final input height
        int boxX = panelX + (panelW - boxW)/2; // center box X
        int boxY = panelY + panelH - (int)(gp.tileSize * 2.0); // box Y near bottom

        // Gradient background
        GradientPaint gradient = new GradientPaint(
            boxX, boxY, new Color(245, 248, 255), // light top
            boxX, boxY + boxH, new Color(235, 240, 250) // slightly darker bottom
        ); // gradient for final input box
        g2.setPaint(gradient); // apply paint
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18); // draw box background
        
        g2.setColor(new Color(100, 120, 200, 140)); // accent border color
        g2.setStroke(new BasicStroke(3f)); // thicker stroke for final box border
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18); // draw border

        // input text - centered
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.75f)); // font for code display
        g2.setColor(new Color(30, 35, 45)); // color for input text
        FontMetrics fm = g2.getFontMetrics(); // metrics for this font

        String displayCode = vaultFinalInput.isEmpty() ? "____" : vaultFinalInput; // placeholder underscores or actual input
        int codeW = fm.stringWidth(displayCode); // width of display string
        int itx = boxX + (boxW - codeW)/2; // horizontally center the code display
        int ity = boxY + ((boxH - fm.getHeight()) / 2) + fm.getAscent(); // vertically center baseline
        g2.drawString(displayCode, itx, ity); // draw code / placeholder

        // typing: digits only, max 4
        char typed = gp.keyH.typedChar; // typed char for final code entry
        if (typed != 0) { // if char present
            if (Character.isDigit(typed) && vaultFinalInput.length() < 4) { // only allow digits and limit to 4
                vaultFinalInput += typed; // append digit
            }
            gp.keyH.typedChar = 0; // consume typed char
        }

        if (gp.keyH.backspacePressed) { // handle backspace for final input
            if (vaultFinalInput.length() > 0) { // if buffer non-empty
                vaultFinalInput = vaultFinalInput.substring(0, vaultFinalInput.length() - 1); // remove last char
            }
            gp.keyH.backspacePressed = false; // consume backspace
        }

        // submit final code
        if (gp.keyH.enterPressed) { // if ENTER pressed to submit code
            gp.keyH.enterPressed = false; // consume ENTER

            String correctCode = "" + vaultDigits[0] + vaultDigits[1] + vaultDigits[2] + vaultDigits[3]; // build correct 4-digit code

            if (vaultFinalInput.equals(correctCode)) { // if entered code matches
                handleTaskSuccess("Vault unlocked!"); // success handler
            } else { // mismatch
                handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, "Wrong code. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // failure
            }
            return; // exit after submission
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.32f)); // help text font
        g2.setColor(new Color(180, 190, 210)); // help text color
        String helpText = "Enter 4 digits then press ENTER  •  ESC to exit"; // help instructions
        int helpW = g2.getFontMetrics().stringWidth(helpText); // width of help text
        g2.drawString(helpText, panelX + (panelW - helpW)/2, panelY + panelH - gp.tileSize/5); // draw help text centered at bottom
    } // end drawVaultSequenceTask


    public void drawLogicPanelTask() { // start logic panel rendering method

        // ==================== RENDERING SETUP ====================
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable shape antialiasing
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // enable text antialiasing

        // Dim background overlay
        g2.setColor(new Color(0, 0, 0, 180)); // semi-transparent black overlay color
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // cover whole screen with overlay

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 12; // panel width in pixels
        int panelH = gp.tileSize * 9; // panel height in pixels
        int panelX = (gp.screenWidth - panelW) / 2; // center panel X
        int panelY = (gp.screenHeight - panelH) / 2; // center panel Y
        int arc = 30; // rounded corner radius
        int pad = gp.tileSize / 3; // panel padding

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140)); // darker outer shadow color
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc); // draw outer shadow
        g2.setColor(new Color(0, 0, 0, 80)); // lighter inner shadow color
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc); // draw inner shadow

        // Dark tech panel gradient
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(30, 35, 45), // top color
            panelX, panelY + panelH, new Color(20, 25, 35) // bottom color
        ); // create vertical gradient for the panel
        g2.setPaint(bgGradient); // apply gradient paint
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw panel background

        // Accent border (green tech theme)
        g2.setColor(new Color(100, 255, 150, 120)); // translucent green accent
        g2.setStroke(new BasicStroke(2.5f)); // border stroke width
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw accent border

        // ==================== RED FLASH EFFECT ====================
        if (logicFlashFrames > 0) { // if flash active
            logicFlashFrames--; // decrement flash timer
            int alpha = (int)(200 * (logicFlashFrames / 30.0)); // compute fade alpha over 30 frames
            g2.setColor(new Color(255, 50, 50, Math.max(0, Math.min(255, alpha)))); // red overlay with alpha clamp
            g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw red flash overlay
        }

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) { // if tasks are locked
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f); // big font for message
            g2.setFont(cooldownFont); // set font
            g2.setColor(new Color(240, 100, 100)); // red color

            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s"; // message text
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2; // center X for message
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2; // center Y for message
            g2.drawString(locked, lx, ly); // draw locked message

            gp.mouseClicked = false; // clear mouse clicked
            gp.keyH.typedChar = 0; // clear typed char
            gp.keyH.backspacePressed = false; // clear backspace
            gp.keyH.enterPressed = false; // clear enter
            gp.keyH.escapePressed = false; // clear escape
            return; // exit while cooldown active
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) { // if ESC pressed
            gp.keyH.escapePressed = false; // consume ESC
            resetAllTaskState(); // reset task state
            gp.gameState = gp.playState; // return to play state
            return; // exit
        }

        // ==================== TITLE SECTION ====================
        String title = "Logic Panel"; // title text
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.90f); // title font
        g2.setFont(titleFont); // set title font

        int titleY = panelY + (int)(gp.tileSize * 0.85); // Y position for title

        // Title shadow
        g2.setColor(new Color(0, 0, 0, 100)); // shadow color
        g2.drawString(title, panelX + pad + 2, titleY + 2); // draw shadow offset

        // Title with tech green color
        g2.setColor(new Color(180, 255, 200)); // greenish title color
        g2.drawString(title, panelX + pad, titleY); // draw title

        // ==================== DIVIDER LINE ====================
        int dividerY = titleY + (int)(gp.tileSize * 0.4); // divider Y below title
        g2.setStroke(new BasicStroke(1.5f)); // divider stroke
        g2.setColor(new Color(100, 255, 150, 60)); // faint green divider color
        g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY); // draw divider

        // ==================== INITIALIZATION ====================
        if (!logicGenerated) { // if not initialized yet
            // Pick 6 random unique statements
            java.util.List<Integer> availableIndices = new java.util.ArrayList<>(); // list of indices
            for (int i = 0; i < LOGIC_STATEMENTS.length; i++) { // populate indices
                availableIndices.add(i); // add each index
            }
            java.util.Collections.shuffle(availableIndices); // shuffle indices

            for (int i = 0; i < logicStatementCount; i++) { // pick first logicStatementCount indices
                int idx = availableIndices.get(i); // get selected index
                logicStatements[i] = LOGIC_STATEMENTS[idx][0]; // statement text
                logicCorrectAnswers[i] = LOGIC_STATEMENTS[idx][1].equals("true"); // parse correct boolean
                logicPlayerAnswers[i] = -1; // mark as unset for player
                logicTrueSwitches[i] = new Rectangle(); // create rectangle for true switch
                logicFalseSwitches[i] = new Rectangle(); // create rectangle for false switch
            }

            // Set timer based on level
            if (gp.level <= 1) {
                logicTimeLimitFrames = 90 * 60; // 90 seconds
            } else if (gp.level == 2) {
                logicTimeLimitFrames = 75 * 60; // 75 seconds
            } else if (gp.level == 3) {
                logicTimeLimitFrames = 60 * 60; // 60 seconds
            } else {
                logicTimeLimitFrames = 50 * 60; // 50 seconds
            }

            logicTimerFrames = logicTimeLimitFrames; // initialize countdown
            logicFlashFrames = 0; // reset flash
            logicGenerated = true; // mark as generated
        }

        // ==================== TIMER TICK ====================
        if (logicTimerFrames > 0) {
            logicTimerFrames--; // decrement timer each frame
        }

        if (logicTimerFrames <= 0) { // time expired
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                "Time's up! Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
            return; // exit
        }

        // ==================== TIMER DISPLAY ====================
        Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f); // timer font
        g2.setFont(timerFont); // set timer font

        int timeSeconds = (logicTimerFrames + 59) / 60; // compute seconds left rounded up
        Color timerColor = timeSeconds <= 15 ? new Color(240, 100, 100) : 
                           timeSeconds <= 30 ? new Color(240, 200, 100) :
                           new Color(180, 220, 180); // color code based on urgency

        g2.setColor(timerColor); // set color
        String timeText = timeSeconds + "s"; // formatted time string
        int tW = g2.getFontMetrics().stringWidth(timeText); // text width
        g2.drawString(timeText, panelX + panelW - pad - tW, titleY); // draw time right-aligned

        // Timer bar
        int barW = tW; // bar width equals text width
        int barH = g2.getFontMetrics().getHeight() / 5; // small bar height
        int barX = panelX + panelW - pad - tW; // bar X (aligned with text)
        int barY = titleY + 6; // bar Y slightly below text

        float ratio = (float)logicTimerFrames / (float)logicTimeLimitFrames; // fraction remaining
        ratio = Math.max(0f, Math.min(1f, ratio)); // clamp ratio

        g2.setColor(new Color(20, 25, 30, 200)); // bar track color
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH); // draw track

        int fillW = Math.max(2, (int)(barW * ratio)); // compute fill width
        GradientPaint barGradient = new GradientPaint(
            barX, barY, timerColor,
            barX + fillW, barY, new Color(timerColor.getRed(), 
                                          timerColor.getGreen(), 
                                          timerColor.getBlue(), 150)
        ); // gradient for fill
        g2.setPaint(barGradient); // set gradient paint
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH); // draw filled portion

        g2.setColor(new Color(255, 255, 255, 50)); // subtle border color
        g2.setStroke(new BasicStroke(1f)); // border stroke
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH); // draw bar border

        // ==================== PROGRESS DISPLAY ====================
        int answeredCount = 0; // count answered statements
        for (int i = 0; i < logicStatementCount; i++) { // iterate statements
            if (logicPlayerAnswers[i] != -1) answeredCount++; // increment if not -1
        }

        Font progressFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.35f); // progress font
        g2.setFont(progressFont); // set font

        Color progressColor = answeredCount == 6 ? new Color(120, 220, 140) : 
                             new Color(180, 200, 230); // green if all answered
        g2.setColor(progressColor); // set color

        String progressText = "Answered: " + answeredCount + " / 6"; // progress text
        
        FontMetrics pfm = g2.getFontMetrics();
        
        //Shifts to the right
        int offsetLeft = (int)(gp.tileSize * 0.8);

        int progressX = panelX + panelW - pad - pfm.stringWidth(progressText) - offsetLeft;
        g2.drawString(progressText, progressX, titleY);
        
        // ==================== STATEMENT ROWS ====================
        int rowHeight = (int)(gp.tileSize * 1.1); // height per statement row
        int rowGap = (int)(gp.tileSize * 0.1); // gap between rows
        int startY = dividerY + (int)(gp.tileSize * 0.2); // Y start for first row

        Font statementFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.30f); // statement font
        g2.setFont(statementFont); // set font

        for (int i = 0; i < logicStatementCount; i++) { // render each statement row
            int rowY = startY + i * (rowHeight + rowGap); // compute row Y
            int rowX = panelX + pad; // left edge of row
            int rowW = panelW - pad * 2; // width of row

            // Row background
            g2.setColor(new Color(40, 45, 55, 200)); // dark row background
            g2.fillRoundRect(rowX, rowY, rowW, rowHeight, 12, 12); // draw row background

            // Row border
            g2.setColor(new Color(100, 255, 150, 40)); // faint border color
            g2.setStroke(new BasicStroke(1.5f)); // border stroke
            g2.drawRoundRect(rowX, rowY, rowW, rowHeight, 12, 12); // draw border

            // Statement text
            g2.setColor(new Color(220, 230, 240)); // statement text color
            String statement = logicStatements[i]; // statement string

            // Wrap text if too long
            int maxWidth = rowW - (int)(gp.tileSize * 3.5); // reserve space for switches
            java.util.List<String> lines = wrapText(statement, g2.getFontMetrics(), maxWidth); // wrap into lines

            int textY = rowY + (rowHeight - lines.size() * g2.getFontMetrics().getHeight()) / 2 + 
                        g2.getFontMetrics().getAscent(); // start Y so text is vertically centered
            int textX = rowX + (int)(gp.tileSize * 0.3); // left padding for text

            for (String line : lines) { // draw each wrapped line
                g2.drawString(line, textX, textY); // draw line
                textY += g2.getFontMetrics().getHeight(); // advance Y
            }

            // ==================== TOGGLE SWITCHES ====================
            int switchW = (int)(gp.tileSize * 1.2); // switch width
            int switchH = (int)(gp.tileSize * 0.6); // switch height
            int switchGap = (int)(gp.tileSize * 0.2); // gap between false/true

            int switchesX = rowX + rowW - (switchW * 2 + switchGap) - (int)(gp.tileSize * 0.3); // X coordinate of left switch
            int switchY = rowY + (rowHeight - switchH) / 2; // Y coordinate for switches centered in row

            // FALSE switch (left)
            logicFalseSwitches[i].setBounds(switchesX, switchY, switchW, switchH); // set rectangle bounds for false switch

            boolean falseSelected = (logicPlayerAnswers[i] == 0); // true if player selected false

            // Switch background
            Color falseBg = falseSelected ? new Color(240, 100, 100, 200) : 
                                           new Color(60, 65, 75, 200); // red when selected, dark otherwise
            g2.setColor(falseBg); // apply background color
            g2.fillRoundRect(switchesX, switchY, switchW, switchH, 10, 10); // draw switch background

            // Switch border
            g2.setColor(falseSelected ? new Color(255, 150, 150) : new Color(100, 110, 120)); // border color varies with selection
            g2.setStroke(new BasicStroke(falseSelected ? 2.5f : 1.5f)); // thicker when selected
            g2.drawRoundRect(switchesX, switchY, switchW, switchH, 10, 10); // draw switch border

            // Text
            Font switchFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.28f); // font for switch labels
            g2.setFont(switchFont); // set switch font
            g2.setColor(falseSelected ? Color.white : new Color(180, 185, 190)); // text color white if selected
            String falseText = "FALSE"; // label
            int ftx = switchesX + (switchW - g2.getFontMetrics().stringWidth(falseText)) / 2; // center text X inside switch
            int fty = switchY + (switchH - g2.getFontMetrics().getHeight()) / 2 + 
                      g2.getFontMetrics().getAscent(); // center baseline vertically
            g2.drawString(falseText, ftx, fty); // draw FALSE label

            // TRUE switch (right)
            int trueX = switchesX + switchW + switchGap; // X for true switch
            logicTrueSwitches[i].setBounds(trueX, switchY, switchW, switchH); // set rectangle for true switch

            boolean trueSelected = (logicPlayerAnswers[i] == 1); // true if player selected true

            // Switch background
            Color trueBg = trueSelected ? new Color(100, 200, 120, 200) : 
                                         new Color(60, 65, 75, 200); // green when selected, dark otherwise
            g2.setColor(trueBg); // apply background color
            g2.fillRoundRect(trueX, switchY, switchW, switchH, 10, 10); // draw true switch background

            // Switch border
            g2.setColor(trueSelected ? new Color(150, 255, 180) : new Color(100, 110, 120)); // border color
            g2.setStroke(new BasicStroke(trueSelected ? 2.5f : 1.5f)); // border stroke width
            g2.drawRoundRect(trueX, switchY, switchW, switchH, 10, 10); // draw true switch border

            // Text
            g2.setFont(switchFont); // set font again for label
            g2.setColor(trueSelected ? Color.white : new Color(180, 185, 190)); // label color
            String trueText = "TRUE"; // label
            int ttx = trueX + (switchW - g2.getFontMetrics().stringWidth(trueText)) / 2; // center text X
            int tty = switchY + (switchH - g2.getFontMetrics().getHeight()) / 2 + 
                      g2.getFontMetrics().getAscent(); // center baseline vertically
            g2.drawString(trueText, ttx, tty); // draw TRUE label
        }

        // ==================== INPUT HANDLING ====================
        // NOTE: previously the code unconditionally cleared gp.mouseClicked when entering this block,
        // which prevented the submit button from ever seeing the click. We now only consume the
        // click if it actually hit one of the TRUE/FALSE switches. If it didn't, we leave the
        // click state intact so the submit button can handle it below.
        if (gp.mouseClicked) { // if mouse clicked this frame
            boolean consumed = false; // whether the click hit a switch
            for (int i = 0; i < logicStatementCount; i++) { // check each statement
                // Check TRUE switch
                if (logicTrueSwitches[i].contains(gp.mouseX, gp.mouseY)) { // clicked true switch
                    logicPlayerAnswers[i] = 1; // record player's answer as true
                    consumed = true; // mark consumed
                    break; // break loop to avoid multiple toggles
                }

                // Check FALSE switch
                if (logicFalseSwitches[i].contains(gp.mouseX, gp.mouseY)) { // clicked false switch
                    logicPlayerAnswers[i] = 0; // record player's answer as false
                    consumed = true; // mark consumed
                    break; // break loop
                }
            }

            if (consumed) { // if click hit a switch
                gp.mouseClicked = false; // consume click so it doesn't also hit submit
                return; // return early to avoid submit logic same frame
            }
            // if not consumed, leave gp.mouseClicked true so submit handling below can detect it
        }

        // ==================== SUBMIT BUTTON ====================
        int submitW = (int)(gp.tileSize * 4); // submit button width
        int submitH = (int)(gp.tileSize * 0.8); // submit button height
        int submitX = panelX + (panelW - submitW) / 2; // center submit button X
        int submitY = startY + 6 * (rowHeight + rowGap) + (int)(gp.tileSize * 0.3); // position submit below rows

        Rectangle submitButton = new Rectangle(submitX, submitY, submitW, submitH); // rectangle for mouse hit

        // Check if all answered
        boolean allAnswered = (answeredCount == 6); // true if user answered all statements

        // Button background
        Color submitBg = allAnswered ? new Color(100, 200, 120, 220) : 
                                      new Color(80, 85, 95, 180); // green if enabled, grey if disabled
        g2.setColor(submitBg); // set background color
        g2.fillRoundRect(submitX, submitY, submitW, submitH, 14, 14); // draw button background

        // Button border
        g2.setColor(allAnswered ? new Color(150, 255, 180) : new Color(100, 110, 120)); // border color based on enabled
        g2.setStroke(new BasicStroke(2f)); // border stroke
        g2.drawRoundRect(submitX, submitY, submitW, submitH, 14, 14); // draw border

        // Button text
        Font submitFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.45f); // submit font
        g2.setFont(submitFont); // set font
        g2.setColor(allAnswered ? Color.white : new Color(140, 145, 150)); // text color
        String submitText = "SUBMIT"; // label
        int stx = submitX + (submitW - g2.getFontMetrics().stringWidth(submitText)) / 2; // center text X
        int sty = submitY + (submitH - g2.getFontMetrics().getHeight()) / 2 + 
                  g2.getFontMetrics().getAscent(); // center baseline Y
        g2.drawString(submitText, stx, sty); // draw label

        // ==================== SUBMIT HANDLING ====================
        // Handle submit - note gp.mouseClicked may still be true here if the click didn't hit switches
        if (allAnswered && gp.mouseClicked) { // if enabled and mouse still flagged as clicked
            if (submitButton.contains(gp.mouseX, gp.mouseY)) { // if click landed on submit
                gp.mouseClicked = false; // consume click

                // Check if all answers are correct
                boolean allCorrect = true; // assume true until proven otherwise
                for (int i = 0; i < logicStatementCount; i++) { // check each statement
                    boolean playerSaysTrue = (logicPlayerAnswers[i] == 1); // player's boolean
                    if (playerSaysTrue != logicCorrectAnswers[i]) { // mismatch with correct answer
                        allCorrect = false; // mark incorrect
                        break; // stop checking further
                    }
                }

                if (allCorrect) { // all matched
                    handleTaskSuccess("Logic panel verified!"); // success handler
                } else { // one or more incorrect
                    // Show red flash and fail
                    logicFlashFrames = 30; // enable flash frames
                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                        "One or more answers are incorrect. Try again in " + 
                        DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail handler
                }
                return; // Exit to prevent further processing this frame
            }
        }

        // ==================== INSTRUCTIONS ====================
        Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.28f); // hint font
        g2.setFont(hintFont); // set font
        g2.setColor(new Color(180, 190, 210, 180)); // hint color

        String hint = allAnswered ? "Click SUBMIT to verify all answers" :
                                   "Click TRUE or FALSE for each statement"; // dynamic hint based on progress

        int hw = g2.getFontMetrics().stringWidth(hint); // width of hint text
        g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 3); // draw hint centered at bottom
    } // end drawLogicPanelTask()



    // ==================== DRAW FUSE REPAIR TASK ====================
    public void drawFuseRepairTask() { // start fuse repair rendering method

        // ==================== RENDERING SETUP ====================
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable AA
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // enable text AA

        // Dim background overlay
        g2.setColor(new Color(0, 0, 0, 180)); // dim overlay color
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); // cover screen

        // ==================== PANEL DIMENSIONS ====================
        int panelW = gp.tileSize * 11; // width
        int panelH = gp.tileSize * 8; // height
        int panelX = (gp.screenWidth - panelW) / 2; // center X
        int panelY = (gp.screenHeight - panelH) / 2; // center Y
        int arc = 30; // corner radius
        int pad = gp.tileSize / 3; // padding

        // ==================== PANEL VISUAL EFFECTS ====================
        // Multi-layered shadow for depth
        g2.setColor(new Color(0, 0, 0, 140)); // outer shadow
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, arc, arc); // draw outer shadow
        g2.setColor(new Color(0, 0, 0, 80)); // inner shadow
        g2.fillRoundRect(panelX + 4, panelY + 4, panelW, panelH, arc, arc); // draw inner shadow

        // Dark electrical-themed gradient background
        GradientPaint bgGradient = new GradientPaint(
            panelX, panelY, new Color(25, 30, 40), // top color
            panelX, panelY + panelH, new Color(15, 20, 28) // bottom color
        ); // gradient for panel
        g2.setPaint(bgGradient); // set paint
        g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw panel

        // Accent border (electric blue theme)
        g2.setColor(new Color(60, 180, 255, 120)); // blue accent
        g2.setStroke(new BasicStroke(2.5f)); // border stroke
        g2.drawRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw accent border

        // ==================== RED FLASH EFFECT ====================
        if (fuseFlashFrames > 0) { // if flash active
            fuseFlashFrames--; // decrement
            int alpha = (int)(200 * (fuseFlashFrames / 30.0)); // fade alpha over 30 frames
            g2.setColor(new Color(255, 50, 50, Math.max(0, Math.min(255, alpha)))); // red overlay
            g2.fillRoundRect(panelX, panelY, panelW, panelH, arc, arc); // draw flash
        }

        // ==================== COOLDOWN STATE ====================
        if (taskCooldownFrames > 0) { // if tasks locked
            Font cooldownFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.7f); // big font
            g2.setFont(cooldownFont); // set font
            g2.setColor(new Color(240, 100, 100)); // red color

            String locked = "Tasks locked. Try again in " + ((taskCooldownFrames + 59) / 60) + " s"; // message
            int lx = panelX + (panelW - g2.getFontMetrics().stringWidth(locked)) / 2; // center X
            int ly = panelY + panelH / 2 + g2.getFontMetrics().getAscent() / 2; // center Y
            g2.drawString(locked, lx, ly); // draw message

            gp.mouseClicked = false; // clear inputs
            gp.keyH.typedChar = 0;
            gp.keyH.backspacePressed = false;
            gp.keyH.enterPressed = false;
            gp.keyH.escapePressed = false;
            return; // exit while locked
        }

        // ==================== ESCAPE TO EXIT ====================
        if (gp.keyH.escapePressed) { // if ESC pressed
            gp.keyH.escapePressed = false; // consume
            resetAllTaskState(); // reset state
            gp.gameState = gp.playState; // return to play
            return; // exit
        }

        // ==================== TITLE SECTION ====================
        String title = "Fuse Repair"; // title text
        Font titleFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.90f); // title font
        g2.setFont(titleFont); // set title font

        int titleY = panelY + (int)(gp.tileSize * 0.85); // title Y

        // Title shadow
        g2.setColor(new Color(0, 0, 0, 100)); // shadow color
        g2.drawString(title, panelX + pad + 2, titleY + 2); // draw shadow

        // Title with electric blue color
        g2.setColor(new Color(160, 220, 255)); // blue title color
        g2.drawString(title, panelX + pad, titleY); // draw title

        // ==================== DIVIDER LINE ====================
        int dividerY = titleY + (int)(gp.tileSize * 0.2); // divider Y
        g2.setStroke(new BasicStroke(1.5f)); // stroke
        g2.setColor(new Color(60, 180, 255, 60)); // faint blue divider color
        g2.drawLine(panelX + pad, dividerY, panelX + panelW - pad, dividerY); // draw divider

        // ==================== INITIALIZATION ====================
        if (!fuseGenerated) { // if not initialized
            // Reset state
            fuseSelectedLeft = -1; // no left node selected
            fuseConnectionsMade = 0; // reset count
            fuseFlashFrames = 0; // reset flash

            // Initialize arrays
            for (int i = 0; i < fuseNodeCount; i++) { // for each node
                fuseLeftColors[i] = FUSE_COLORS[i]; // assign left color
                fuseConnected[i] = false; // mark not connected
                fuseLeftNodes[i] = new Rectangle(); // create left node rect
                fuseRightNodes[i] = new Rectangle(); // create right node rect
            }

            // Shuffle right side
            java.util.List<Integer> indices = new java.util.ArrayList<>(); // index list
            for (int i = 0; i < fuseNodeCount; i++) indices.add(i); // fill
            java.util.Collections.shuffle(indices); // shuffle order

            for (int i = 0; i < fuseNodeCount; i++) { // assign shuffled right order/colors
                fuseRightOrder[i] = indices.get(i); // position mapping
                fuseRightColors[i] = FUSE_COLORS[fuseRightOrder[i]]; // color for right node
            }

            // Set timer based on level
            if (gp.level <= 1) {
                fuseTimeLimitFrames = 60 * 60; // 60 seconds
            } else if (gp.level == 2) {
                fuseTimeLimitFrames = 50 * 60; // 50 seconds
            } else if (gp.level == 3) {
                fuseTimeLimitFrames = 40 * 60; // 40 seconds
            } else {
                fuseTimeLimitFrames = 35 * 60; // 35 seconds
            }

            fuseTimerFrames = fuseTimeLimitFrames; // initialize timer
            fuseGenerated = true; // mark generated
        }

        // ==================== TIMER TICK ====================
        if (fuseTimerFrames > 0) {
            fuseTimerFrames--; // decrement timer
        }

        if (fuseTimerFrames <= 0) { // time expired
            handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS, 
                "Sytem Fried! Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail
            return; // exit
        }

        // ==================== TIMER DISPLAY ====================
        Font timerFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f); // timer font
        g2.setFont(timerFont); // set font

        int timeSeconds = (fuseTimerFrames + 59) / 60; // seconds left rounded up
        Color timerColor = timeSeconds <= 10 ? new Color(240, 100, 100) : 
                           timeSeconds <= 20 ? new Color(240, 200, 100) :
                           new Color(180, 220, 180); // color code by urgency

        g2.setColor(timerColor); // set color
        String timeText = timeSeconds + "s"; // formatted time
        int tW = g2.getFontMetrics().stringWidth(timeText); // width
        g2.drawString(timeText, panelX + panelW - pad - tW, titleY); // draw time at top right

        // Timer bar
        int barW = tW; // bar width
        int barH = g2.getFontMetrics().getHeight() / 5; // bar height
        int barX = panelX + panelW - pad - tW; // bar X
        int barY = titleY + 6; // bar Y under text

        float ratio = (float)fuseTimerFrames / (float)fuseTimeLimitFrames; // fraction remaining
        ratio = Math.max(0f, Math.min(1f, ratio)); // clamp

        g2.setColor(new Color(20, 25, 30, 200)); // track color
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH); // draw track

        int fillW = Math.max(2, (int)(barW * ratio)); // filled width
        GradientPaint barGradient = new GradientPaint(
            barX, barY, timerColor,
            barX + fillW, barY, new Color(timerColor.getRed(), 
                                          timerColor.getGreen(), 
                                          timerColor.getBlue(), 150)
        ); // gradient for fill
        g2.setPaint(barGradient); // set paint
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH); // draw fill

        g2.setColor(new Color(255, 255, 255, 50)); // border color
        g2.setStroke(new BasicStroke(1f)); // border stroke
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH); // draw bar border

        // ==================== PROGRESS DISPLAY ====================
        Font progressFont = g2.getFont().deriveFont(Font.BOLD, gp.tileSize * 0.38f); // progress font
        g2.setFont(progressFont); // set font

        Color progressColor = fuseConnectionsMade == 9 ? new Color(120, 220, 140) : 
                             new Color(180, 200, 230); // green if all connected
        g2.setColor(progressColor); // set color

        String progressText = "Connections: " + fuseConnectionsMade + " / 9"; // progress label
        
        //Moves the progress to the right
        FontMetrics pfm = g2.getFontMetrics();
        int offsetLeft = (int)(gp.tileSize * 0.9); 
        int progressX = panelX + panelW - pad - pfm.stringWidth(progressText) - offsetLeft;
        g2.drawString(progressText, progressX, titleY);

        // ==================== NODE LAYOUT ====================
        int nodeSize = (int)(gp.tileSize * 0.6); // diameter of node
        int nodeGap = (int)(gp.tileSize * 0.15); // gap between nodes

        int leftColX = panelX + (int)(gp.tileSize * 1.5); // left column X for left nodes
        int rightColX = panelX + panelW - (int)(gp.tileSize * 1.5) - nodeSize; // right column X for right nodes

        int startY = dividerY + (int)(gp.tileSize * 0.8); // start Y for nodes
        int totalNodesHeight = (nodeSize * fuseNodeCount) + (nodeGap * (fuseNodeCount - 1)); // total height of nodes block
        int availableHeight = panelH - (startY - panelY) - (int)(gp.tileSize * 1.2); // available vertical space
        int centerOffset = (availableHeight - totalNodesHeight) / 2; // offset to center the nodes block
        int firstNodeY = startY + centerOffset; // Y for first node

        // ==================== DRAW WIRES (COMPLETED CONNECTIONS) ====================
        g2.setStroke(new BasicStroke(3f)); // stroke for wires

        for (int i = 0; i < fuseNodeCount; i++) { // iterate left nodes
            if (fuseConnected[i]) { // if left node is connected
                // Find matching right node
                int rightIdx = -1; // index of matching right position
                for (int j = 0; j < fuseNodeCount; j++) { // search right order
                    if (fuseRightOrder[j] == i) { // right slot maps to left index i
                        rightIdx = j; // found matching right slot
                        break; // break inner loop
                    }
                }

                if (rightIdx != -1) { // if matching right found
                    int leftY = firstNodeY + i * (nodeSize + nodeGap) + nodeSize / 2; // center Y of left node
                    int rightY = firstNodeY + rightIdx * (nodeSize + nodeGap) + nodeSize / 2; // center Y of right node

                    int leftX = leftColX + nodeSize; // X at right edge of left node
                    int rightX = rightColX; // X at left edge of right node

                    // Draw wire with glow effect
                    Color wireColor = fuseLeftColors[i]; // color assigned to this connection

                    // Outer glow
                    g2.setColor(new Color(wireColor.getRed(), wireColor.getGreen(), 
                                         wireColor.getBlue(), 60)); // translucent glow
                    g2.setStroke(new BasicStroke(7f)); // thick stroke for glow
                    g2.drawLine(leftX, leftY, rightX, rightY); // draw glow line

                    // Inner wire
                    g2.setColor(wireColor); // solid wire color
                    g2.setStroke(new BasicStroke(3f)); // inner stroke
                    g2.drawLine(leftX, leftY, rightX, rightY); // draw wire
                }
            }
        }

        // ==================== DRAW PREVIEW WIRE (WHEN LEFT NODE SELECTED) ====================
        if (fuseSelectedLeft != -1 && !fuseConnected[fuseSelectedLeft]) { // if left node selected and not already connected
            int leftY = firstNodeY + fuseSelectedLeft * (nodeSize + nodeGap) + nodeSize / 2; // center Y of selected left node
            int leftX = leftColX + nodeSize; // X at right edge of left node

            // Draw dotted line to mouse using dashed stroke
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, 
                                         BasicStroke.JOIN_ROUND, 1.0f, 
                                         new float[]{8f, 8f}, 0f)); // dashed pattern

            Color previewColor = fuseLeftColors[fuseSelectedLeft]; // color for preview
            g2.setColor(new Color(previewColor.getRed(), previewColor.getGreen(), 
                                 previewColor.getBlue(), 180)); // semi-opaque preview color
            g2.drawLine(leftX, leftY, gp.mouseX, gp.mouseY); // draw preview line from left node to mouse
        }

        // ==================== DRAW NODES ====================
        for (int i = 0; i < fuseNodeCount; i++) { // draw each node pair
            int nodeY = firstNodeY + i * (nodeSize + nodeGap); // Y coordinate for this node

            // LEFT NODE
            fuseLeftNodes[i].setBounds(leftColX, nodeY, nodeSize, nodeSize); // set rectangle bounds for left node

            boolean leftConnected = fuseConnected[i]; // whether left is connected
            boolean leftSelected = (fuseSelectedLeft == i); // whether left is currently selected

            // Node shadow
            g2.setColor(new Color(0, 0, 0, 120)); // shadow color
            g2.fillOval(leftColX + 3, nodeY + 3, nodeSize, nodeSize); // draw shadow ellipse

            // Node background (different when connected/selected)
            if (leftConnected) {
                g2.setColor(new Color(40, 45, 50)); // darker when connected
            } else if (leftSelected) {
                g2.setColor(new Color(60, 70, 85)); // highlighted when selected
            } else {
                g2.setColor(new Color(50, 55, 60)); // default background
            }
            g2.fillOval(leftColX, nodeY, nodeSize, nodeSize); // draw left node base

            // Node color ring
            g2.setStroke(new BasicStroke(leftSelected ? 5f : 3f)); // thicker ring when selected
            g2.setColor(fuseLeftColors[i]); // color ring uses assigned color
            g2.drawOval(leftColX + 3, nodeY + 3, nodeSize - 6, nodeSize - 6); // draw ring

            // Inner glow for selected
            if (leftSelected && !leftConnected) { // only show inner glow for unconnected selected node
                g2.setColor(new Color(fuseLeftColors[i].getRed(), 
                                      fuseLeftColors[i].getGreen(), 
                                      fuseLeftColors[i].getBlue(), 100)); // translucent glow color
                g2.fillOval(leftColX + 8, nodeY + 8, nodeSize - 16, nodeSize - 16); // draw inner glow
            }

            // RIGHT NODE
            fuseRightNodes[i].setBounds(rightColX, nodeY, nodeSize, nodeSize); // set rectangle for right node

            int colorIdx = fuseRightOrder[i]; // original left index that this right slot corresponds to
            boolean rightConnected = fuseConnected[colorIdx]; // right is considered connected if its matching left is connected

            // Node shadow
            g2.setColor(new Color(0, 0, 0, 120)); // shadow color
            g2.fillOval(rightColX + 3, nodeY + 3, nodeSize, nodeSize); // draw shadow

            // Node background
            g2.setColor(rightConnected ? new Color(40, 45, 50) : new Color(50, 55, 60)); // darker when connected
            g2.fillOval(rightColX, nodeY, nodeSize, nodeSize); // draw right node base

            // Node color ring
            g2.setStroke(new BasicStroke(3f)); // ring stroke
            g2.setColor(fuseRightColors[i]); // ring color from shuffled right colors
            g2.drawOval(rightColX + 3, nodeY + 3, nodeSize - 6, nodeSize - 6); // draw ring
        }

        // ==================== INPUT HANDLING ====================
        if (gp.mouseClicked) { // if mouse clicked this frame
            gp.mouseClicked = false; // consume click immediately for node handling

            // Check left nodes
            for (int i = 0; i < fuseNodeCount; i++) { // iterate left nodes
                if (fuseLeftNodes[i].contains(gp.mouseX, gp.mouseY) && !fuseConnected[i]) { // clicked an unconnected left node
                    if (fuseSelectedLeft == i) { // clicked same node as selected
                        fuseSelectedLeft = -1; // deselect it
                    } else {
                        fuseSelectedLeft = i; // select this left node
                    }
                    return; // return after selecting/deselecting to avoid further processing this frame
                }
            }

            // Check right nodes (only if left is selected)
            if (fuseSelectedLeft != -1) { // if a left node is selected
                for (int i = 0; i < fuseNodeCount; i++) { // iterate right nodes
                    if (fuseRightNodes[i].contains(gp.mouseX, gp.mouseY)) { // clicked a right node
                        int rightColorIdx = fuseRightOrder[i]; // left index that this right slot corresponds to

                        // Check if this is the correct match
                        if (rightColorIdx == fuseSelectedLeft) { // correct pairing
                            // CORRECT CONNECTION
                            fuseConnected[fuseSelectedLeft] = true; // mark left as connected
                            fuseConnectionsMade++; // increment connections count
                            fuseSelectedLeft = -1; // clear selection

                            // Check for completion
                            if (fuseConnectionsMade == 9) { // all connections made
                                handleTaskSuccess("Fuse repaired!"); // success handler
                            }
                        } else { // incorrect pairing
                            // WRONG CONNECTION - IMMEDIATE FAIL
                            fuseFlashFrames = 30; // trigger red flash

                            // Small delay before fail to show flash
                            new Thread(() -> { // spawn background thread to delay failure handling
                                try {
                                    Thread.sleep(500); // wait half a second
                                    handleTaskFailed(DEFAULT_TASK_COOLDOWN_SECONDS,
                                        "Incorrect connection. Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds"); // fail
                                } catch (InterruptedException e) {
                                    e.printStackTrace(); // print stack on interruption
                                }
                            }).start(); // start thread
                        }
                        return; // return after handling click on a right node
                    }
                }
            }
        }

        // ==================== INSTRUCTIONS ====================
        Font hintFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.30f); // hint font
        g2.setFont(hintFont); // set font
        g2.setColor(new Color(180, 190, 210, 180)); // hint color

        String hint = fuseSelectedLeft == -1 ? 
            "Click a node on the left, then its matching color on the right • ESC to exit" :
            "Click the matching color on the right • Click same node to deselect"; // dynamic hint

        int hw = g2.getFontMetrics().stringWidth(hint); // width of hint text
        g2.drawString(hint, panelX + (panelW - hw) / 2, panelY + panelH - pad / 2); // draw hint centered at bottom
    } // end drawFuseRepairTask


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
            
            String locked = "Food burnt. Try again in " + ((taskCooldownFrames + 59) / 60) + " s";
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
        String title = "Cooking Quiz";
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
                "You burnt the food! Try again in " + DEFAULT_TASK_COOLDOWN_SECONDS + " seconds");
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
        String timeText = timeSeconds + "s";
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
        int optionsY = qY + (int)(gp.tileSize * 0.0);
        int optionH = (int)(gp.tileSize * 0.8);
        int optionGap = (int)(gp.tileSize * 0.2);

        Font optionFont = g2.getFont().deriveFont(Font.PLAIN, gp.tileSize * 0.38f);
        g2.setFont(optionFont);

        String[] optionLabels = {"A", "B", "C", "D"};
        
        g2.setFont(optionFont);
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
                bgColor = new Color(245, 215, 160, 200); // Blue for current selection
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
                g2.drawString(":)", optX + optW - (int)(gp.tileSize * 0.6), textY);
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
                
                g2.setFont(optionFont);
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
            
            String feedback = cookingAnswerCorrect ? "Correct!" : "Incorrect";
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
