package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import entity.player;
import tile.TileManager;

import javax.swing.JPanel;
import tile.TileManager;

public class gamePanel extends JPanel implements Runnable {

	//Screen settings
	public final int originalTileSize = 16; //zoom in out scale
	public final int scale = 1; //scale tiles by 3 
	public final int tileSize = originalTileSize * scale; 
	public final int maxScreenCol = 50;
	public final int maxScreenRow = 50;
	public final int screenWidth = tileSize * maxScreenCol; 
	public final int screenHeight = tileSize * maxScreenRow;
	
	//FPS
	final int FPS = 60;
	
	TileManager tileM = new TileManager(this);
	keyHandler keyH = new keyHandler();
	Thread gameThread;
	player player = new player(this, keyH);
	
	
	public gamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
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
		
		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			if (delta >= 1) {
				//Update: update info such as character positions
				update();
				//Draw: draw the screen with the updated information
				repaint();
				delta--;
				drawCount++;
			}
			if (timer >= 1000000000) {
				System.out.println("FPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
	}
	
	public void update() {
		player.update();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		tileM.draw(g2);
		player.draw(g2);
		
		g2.dispose();
	}
}
