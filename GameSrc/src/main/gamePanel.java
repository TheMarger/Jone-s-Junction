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
import saves.*;
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
	public boolean playingMusic = false;
	
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	public int currentItemIndex = 0;
	public int equippedSkinIndex = 0; // default
	public String equippedSkin; // optional, just for readability
	public int level = 1;
	// mouse / selection state 
	public int mouseX = 0, mouseY = 0;                  // last mouse coordinates on screen
	public int hoveredTileCol = -1, hoveredTileRow = -1; // tile currently under mouse
	public int selectedThrowCol = -1, selectedThrowRow = -1; // tile selected by click (throw target)
	public boolean mouseClicked = false;               // flag to indicate a click occurred
	
	// saves
	public save1 save1 = new save1();
	public save2 save2 = new save2();
	public save3 save3 = new save3();
	
	// FPS
	public final int FPS = 60;
	
	public Task tasks[] = new Task[10]; // max tasks
		
		// keybinds: 0=forward,1=back,2=left,3=right,4=sprint,5=crouch,6=interact,7=throw,8=drop
		public int[] keybinds = new int[] {
		    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
		    KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_E, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_ESCAPE, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3
		};
		
		public String forwardKey = KeyEvent.getKeyText(keybinds[0]);
		public String backKey = KeyEvent.getKeyText(keybinds[1]);
		public String leftKey = KeyEvent.getKeyText(keybinds[2]);
		public String rightKey = KeyEvent.getKeyText(keybinds[3]);
		public String sprintKey = KeyEvent.getKeyText(keybinds[4]);
		public String crouchKey = KeyEvent.getKeyText(keybinds[5]);
		public String interactKey = KeyEvent.getKeyText(keybinds[6]);
		public String throwKey = KeyEvent.getKeyText(keybinds[7]);
		public String dropKey = KeyEvent.getKeyText(keybinds[8]);
		public String escapeKey = KeyEvent.getKeyText(keybinds[9]);
		
		
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
	public final int taskState = 5;
	public boolean speedRunState = false;
	
	public int speedRunTimerFrames = 0;
	public boolean speedRunLost = false;
		
	public TileManager tileM;
	public UtilityTool uTool;
	public keyHandler keyH;
	public CollisionChecker cChecker;
	public AssetSetter aSetter;
	public player player;
	public UserInterface ui;
	public EventHandler eHandler;
	public saveSystem saveSystem;
	
	Thread gameThread;
	
	
	public gamePanel() {

	    setPreferredSize(new Dimension(screenWidth, screenHeight));
	    setBackground(Color.black);
	    setDoubleBuffered(true);
	    setFocusable(true);
	    
		 // track mouse movement to update hovered tile
		 addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
		     @Override
		     public void mouseMoved(java.awt.event.MouseEvent e) {
		         mouseX = e.getX();
		         mouseY = e.getY();
		         updateHoveredTile();
		     }
	
		     @Override
		     public void mouseDragged(java.awt.event.MouseEvent e) {
		         mouseX = e.getX();
		         mouseY = e.getY();
		         updateHoveredTile();
		     }
		 });
	
		 // handle clicks to set the selected throw tile
		 addMouseListener(new java.awt.event.MouseAdapter() {
		     @Override
		     public void mouseClicked(java.awt.event.MouseEvent e) {
		         // only set selection while playing (optional tweak)
		         if (gameState == playState || gameState == taskState) {
		             // set selected target to the hovered tile (or clear if none)
		             selectedThrowCol = hoveredTileCol;
		             selectedThrowRow = hoveredTileRow;
		             mouseClicked = true; // flag for one-time processing
		         }
		     }
		 });
		 
		// allow mouse wheel to scroll instructions
		 // this assumes 'this' is a Component (e.g., JPanel) or you have access to the component to add the listener
		 this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
		     @Override
		     public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
		         // only scroll when instruction screen is active
		         if (ui.titleScreenState == 4) {
		             ui.instrScrollOffset += e.getWheelRotation() * ui.instrScrollSpeed;
		             ui.clampInstrScroll();
		         }
		     }
		 });

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
	    aSetter.setTasks();

	    if (player.level == 1) {
	        tileM.loadMap("/maps/Level1Map.txt");
	    } else if (player.level == 2) {
	        tileM.loadMap("/maps/Level2Map.txt");
	    } else if (player.level == 3) {
	        tileM.loadMap("/maps/Level3Map.txt");
	    } else if (player.level == 4) {
	        tileM.loadMap("/maps/Level4Map.txt");
	    }

	    gameState = titleState;

	    System.out.println("setupGame finished");
	}
	
	public void saveGame(int slot) {
		if (slot == 1) {
			saves.save1.saveGame(this);
		} else if (slot == 2) {
			saves.save2.saveGame(this);

		}
		else if (slot == 3) {
			saves.save3.saveGame(this);		
		}
	    
	}
	
	public void loadGame(int slot) {
		if (slot == 1) {
			if (saves.save1.fileExists()) {
				saves.save1.loadGame(this);
				player.updateInventory();
				ui.showBoxMessage("Game Loaded from Slot 1!");
				gameState = playState;
		}
			else {
				ui.showBoxMessage("No save file found in Slot 2!");
			}
		} else if (slot == 2) {
			if (saves.save2.fileExists()) {
				saves.save2.loadGame(this);
				player.updateInventory();
				ui.showBoxMessage("Game Loaded from Slot 2!");
				gameState = playState;
		}	else {
				ui.showBoxMessage("No save file found in Slot 2!");
			}
		}
		else if (slot == 3) {
			if (saves.save3.fileExists()) {
				saves.save3.loadGame(this);
				player.updateInventory();				ui.showBoxMessage("Game Loaded from Slot 3!");
				gameState = playState;
			}else {
				ui.showBoxMessage("No save file found in Slot 2!");
			}
		}
	    
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
		if (gameState == taskState) {
			
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
	    aSetter.setTasks();
	    
	    ui.levelFinished = false;
	    ui.slotRow = -1;
	    ui.selectedItem = null;
	    ui.showThrowRadius = false;
        ui.activeThrowable = null;
        selectedThrowCol = -1;
        selectedThrowRow = -1;
        
        speedRunTimerFrames = 0;
        speedRunLost = false;
        
	    if (restartFromTitle) {
	    	gameState = titleState;
	    	ui.titleScreenState = 0;
	    }
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
			
			// Draw Time
			if (speedRunState) {
				ui.drawSpeedRunTimer(g2);
				if (speedRunLost) {
					eHandler.playerDied();
				}
				speedRunTimerFrames++;
			}
			
			
			// Draw items
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					items[i].draw(g2);
				}
			}
			
			//Draw tasks
			for (int i = 0; i < tasks.length; i++) {
				if (tasks[i] != null) {
					tasks[i].draw(g2);
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
			    	//uTool.drawEntityHitbox(g2, gaurds[i]);
			    }
			}
			
			//UI
			ui.draw(g2);
			
		}
		g2.dispose();
	}
	
	public void playMusic(int i) {
		if (!playingMusic) {
			music.setFile(i);
			music.setVolume(0.6f);
			music.play();
			music.loop();
			playingMusic = true;
		} else {
			return;
		}
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
	
	public void updateHoveredTile() {
	    // Convert mouse screen coords to world coords (invert your screenX calculation)
	    if (player == null) return;
	    int worldXAtMouse = mouseX + player.worldX - player.getScreenX();
	    int worldYAtMouse = mouseY + player.worldY - player.getScreenY();

	    // tile col/row under mouse
	    int col = worldXAtMouse / tileSize;
	    int row = worldYAtMouse / tileSize;

	    // sanity check bounds
	    if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) {
	        hoveredTileCol = -1;
	        hoveredTileRow = -1;
	    } else {
	        hoveredTileCol = col;
	        hoveredTileRow = row;
	    }
	}

	
}
