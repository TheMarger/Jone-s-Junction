package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import entity.entity;
import entity.player;
import task.Task;
import tile.TileManager;

import javax.swing.JPanel;

import Item.Item;
import tile.TileManager;

public class gamePanel extends JPanel implements Runnable {

	// Screen settings
	public final int originalTileSize = 16; // 16x16 tile
	public final int scale = 4; // scale tiles by 3 
	public final int tileSize = originalTileSize * scale; // 48x48 tile
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
	public final int screenHeight = tileSize * maxScreenRow; // 576 pixels
	
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	public int currentItemIndex = 0;
	public int equippedSkinIndex = 0; // default
	public String equippedSkin; // optional, just for readability
	public int level = 1;
	
	// FPS
	public final int FPS = 60;
	
	public String tasks[] = {
			"Math Task",
			"Cooking Task",
			"Riddle Task",
			"Shopping Task",
			"Exercise Task"
		};
				
		
		// keybinds: 0=forward,1=back,2=left,3=right,4=sprint,5=crouch,6=interact,7=throw,8=drop
		public int[] keybinds = new int[] {
		    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
		    KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_E, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_ESCAPE, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3
		};
		
		public final String[][][] skins = {
		    {   // Rabbit
		    	{"Rabbit"},
		    	{"unlocked"},
		        {"/assets/Rabbit.png", "/Rabbit/boy_up_1.png", "/Rabbit/boy_up_2.png", "/Rabbit/boy_down_1.png", "/Rabbit/boy_down_2.png", "/Rabbit/boy_right_1.png", "/Rabbit/boy_right_2.png", "/Rabbit/boy_left_1.png", "/Rabbit/boy_left_2.png"}
		    },
		    
		    {
		    	//Boy
		    	{"Boy"},
		    	{"unlocked"},
		        {"/player/boy_down_1.png", "/player/boy_up_1.png", "/player/boy_up_2.png", "/player/boy_down_1.png", "/player/boy_down_2.png", "/player/boy_right_1.png", "/player/boy_right_2.png", "/player/boy_left_1.png", "/player/boy_left_2.png"}
		    },
		    
		    {  
		    	// Old Timer
		    	{"Old Timer"},
		    	{"locked"},
		        {"/assets/OldTimer.png"},
		    },
		    {   // Froseph
		    	{"Froseph"},
		    	{"locked"},
		        {"/assets/Froseph.png"},
		    },
		    {   // Lifer
		    	{"Lifer"},
		    	{"locked"},
		        {"/assets/Lifer.png"},
		    },
		    {   // BillyGoat
		    	{"Billy Goat"},
		    	{"locked"},
		        {"/assets/BillyGoat.png"},
		    },
		    {   // Marv
		    	{"Marv"},
		    	{"locked"},
		        {"/assets/Marv.png"},
		    }
		};
	
		public Item items[] = new Item[20]; // max objects on map
		public entity npc[] = new entity[10];
		public entity gaurds[] = new entity[20];
		Sound music = new Sound();
		Sound soundEffect = new Sound();

		public int gameState;
		public final int titleState = 0;
		public final int playState = 1;
		public final int pauseState = 2;
		public final int dialogueState = 3;
		public final int deathState = 4;
		
		
	/*public UtilityTool uTool = new UtilityTool(this);
	public TileManager tileM = new TileManager(this);
	public keyHandler keyH = new keyHandler(this);
	public CollisionChecker cChecker = new CollisionChecker(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public player player = new player(this, keyH);
	public UserInterface ui = new UserInterface(this);
	public EventHandler eHandler = new EventHandler(this);*/
		
	public TileManager tileM;
	public UtilityTool uTool;
	public keyHandler keyH;
	public CollisionChecker cChecker;
	public AssetSetter aSetter;
	public player player;
	public UserInterface ui;
	public EventHandler eHandler;
	
	Thread gameThread;
	
	
	public gamePanel() {

	    setPreferredSize(new Dimension(screenWidth, screenHeight));
	    setBackground(Color.black);
	    setDoubleBuffered(true);
	    setFocusable(true);

	    keyH = new keyHandler(this);
	    addKeyListener(keyH);

	    System.out.println("gamePanel constructed");
	}

	
	public void setupGame() {

	    System.out.println("setupGame started");

	    uTool = new UtilityTool(this);
	    tileM = new TileManager(this);
	    cChecker = new CollisionChecker(this);
	    aSetter = new AssetSetter(this);

	    player = new player(this, keyH);
	    ui = new UserInterface(this);
	    eHandler = new EventHandler(this);

	    aSetter.setItem();
	    aSetter.setNPC();
	    aSetter.setGaurds();
	    player.setTasks();

	    if (player.level == 1) {
	        tileM.loadMap("/maps/Level1Map.txt");
	    }

	    gameState = titleState;

	    System.out.println("setupGame finished");
	}

	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;
		
		// TODO Auto-generated method stub
		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			if (delta >= 1) {
				// Update: update information such as character positions
				update();
				// Draw: draw the screen with the updated information
				repaint();
				delta--;
				drawCount++;
			}
			if (timer >= 1000000000) {
				drawCount = 0;
				timer = 0;
			}
		}
	}
	
	public void update() {
		if (gameState == playState) {
			// Player
			player.update();
			// NPCs
			for (int i = 0; i < npc.length; i++) {
				if (npc[i] != null) {
					npc[i].update();
				}
			}
			// Gaurds
			for (int i = 0; i < gaurds.length; i++) {
				if (gaurds[i] != null) {
					gaurds[i].update();
				}
			}
			// Items
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					items[i].update();
				}
			}
			
		}
		if (gameState == pauseState) {
	
		}
		if (gameState == dialogueState) {
			
		}
		if (gameState == deathState) {
			
		}
	}
	
	public void resetGame(boolean restartFromTitle) {
	    // Clear world objects, reset player, map, etc.
	    for (int i = 0; i < items.length; i++) items[i] = null;
	    player.clearInventory();
	    player.setDefaultValues();
	    tileM.resetMap();

	    // Respawn items / NPCs but **do not create a new gamePanel**
	    aSetter.setItem();
	    aSetter.setNPC();
	    aSetter.setGaurds();
	    
	    if (restartFromTitle) gameState = titleState;
	    else gameState = playState;
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		//Title screen
		if (gameState == titleState) {
			ui.draw(g2);
		} else {
			// Draw tiles
			tileM.draw(g2);
			
			
			// Draw items
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					items[i].draw(g2);
				}
			}
			
			// Draw NPCs
			for (int i = 0; i < npc.length; i++) {
				if (npc[i] != null) {
					npc[i].draw(g2);
				}
			}
			// Draw Gaurds
			for (int i = 0; i < gaurds.length; i++) {
				if (gaurds[i] != null) {
					gaurds[i].draw(g2);
				}
			}
			
			// Draw player
			player.draw(g2);
			
			// Draw gaurds
			for (int i = 0; i < gaurds.length; i++) {
			    if (gaurds[i] != null) {
			    	gaurds[i].draw(g2);
			    	uTool.drawEntityHitbox(g2, gaurds[i]);
			    }
			}
			
			//UI
			ui.draw(g2);
			
		}
		g2.dispose();
	}
	
	public void playMusic(int i) {
		music.setFile(i);
		music.setVolume(0.6f);
		music.play();
		music.loop();
	}
	
	public int getEmptyItemSlot() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				return i;
			}
		}
		return -1; // no empty slot
	}
	
	public void stopMusic() {
		music.stop();
	}
	public void playSoundEffect(int i) {
		soundEffect.setFile(i);
		soundEffect.setVolume(0.65f);
		soundEffect.play();
	}
}
