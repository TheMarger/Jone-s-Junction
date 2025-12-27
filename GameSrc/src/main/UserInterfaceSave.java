package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import entity.player;
import Item.*;

public class UserInterfaceSave {
	
	gamePanel gp;
	Graphics2D g2;
	Font arial_40;
	BufferedImage keyImage;
	BufferedImage greenKeyImage;
	BufferedImage redKeyImage;
	BufferedImage torchImage;
	BufferedImage blueKeyImage;
	public boolean messageOn = false;
	public boolean interactOn = false;
	public String interactMessage = "";
	public String message = "";
	int messageCounter = 0;
	public int commandNum = 0;
	public int titleScreenState = 0; // 0 = new game, 1 = Load game screen, 2 = character selection screen, 3 = keybindings screen
	
	public boolean levelFinished = false;
	double playTime;
	DecimalFormat df = new DecimalFormat("#0.00");
	
	public String currentDialogue = "";
	
	public void UserInterface(gamePanel gp) {
		this.gp = gp;
		arial_40 = new Font("Cambria", Font.PLAIN, 40);
		
		Key key = new Key(gp);
		greenKey greenKey = new greenKey(gp);
		redKey redKey = new redKey(gp);
		Flashlight torch = new Flashlight(gp);
		blueKey blueKey = new blueKey(gp);
		keyImage = key.image;
		greenKeyImage = greenKey.image;
		redKeyImage = redKey.image;
		torchImage = torch.image;
		blueKeyImage = blueKey.image;
		
	}
	
	public void showMessage(String text) {
		message = text;
		messageOn = true;
	}
	
	public void showInteract() {
		interactMessage = "[E] to interact";
		interactOn = true;
	}
	
	public void hideInteract() {
		interactOn = false;
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		g2.setFont(arial_40);
		g2.setColor(Color.white);
		//Title State
		if (gp.gameState == gp.titleState) {
			drawTitleScreen();
		}
		// PLAY STATE
		if (gp.gameState == gp.playState) {

		}
		// PAUSE STATE
		if (gp.gameState == gp.pauseState) {
			drawPauseScreen();
		}
		// Dialogue STATE
		if (gp.gameState == gp.dialogueState) {
			drawDialogueScreen();
		}
		
		//MESSAGES
		if (messageOn == true) {
			g2.drawString(message, gp.tileSize/2, gp.tileSize*5 + 20);
			messageCounter++;
			if (messageCounter > 120) {
				messageCounter = 0;
				messageOn = false;
			}
		}
		
		//INTERACT
		if (interactOn == true) {
			g2.drawString(interactMessage, gp.tileSize/2, gp.tileSize*6 + 20);
		}
		
	}
	
	public void drawTitleScreen() {
		if (titleScreenState == 0) {
			g2.setColor(new Color(0, 0, 0));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
			
			//TITLE NAME
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
			String text = "Jone's Junction";
			int x = getXforCenteredText(text);
			int y = gp.tileSize*3;
			
			//SHADOW
			g2.setColor(Color.gray);
			g2.drawString(text, x+5, y+5);
			
			//MAIN COLOR
			g2.setColor(Color.white);
			g2.drawString(text, x, y);
			
			//MENU OPTIONS
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
			
			text = "NEW GAME";
			x = getXforCenteredText(text);
			y += gp.tileSize*3;

			if (commandNum == 0) {
				g2.drawString(">", x-gp.tileSize, y);
				g2.setColor(Color.gray);
				g2.drawString(text, x, y);
			} else {
				g2.drawString(text, x, y);
			}
			g2.setColor(Color.white);
			
			text = "LOAD GAME";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			if (commandNum == 1) {
				g2.drawString(">", x-gp.tileSize, y);
				g2.setColor(Color.gray);
				g2.drawString(text, x, y);
			} else {
				g2.drawString(text, x, y);
			}
			g2.setColor(Color.white);
			
			text = "CHARACTERS";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			if (commandNum == 2) {
				g2.drawString(">", x-gp.tileSize, y);
				g2.setColor(Color.gray);
				g2.drawString(text, x, y);
				
			} else {
				g2.drawString(text, x, y);
			}
			g2.setColor(Color.white);
			
			text = "KEYBINDINGS";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			if (commandNum == 3) {
				g2.drawString(">", x-gp.tileSize, y);
				g2.setColor(Color.gray);
				g2.drawString(text, x, y);
			} else {
				g2.drawString(text, x, y);
			}
			g2.setColor(Color.white);
			
			text = "EXIT";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			if (commandNum == 4) {
				g2.drawString(">", x-gp.tileSize, y);
				g2.setColor(Color.gray);
				g2.drawString(text, x, y);
			}
			else {
				g2.drawString(text, x, y);
			}
			g2.setColor(Color.white);
		}
		else if (titleScreenState == 2) {
			// CLASS SELECTION SCREEN
			g2.setColor(new Color(0, 0, 0));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
			
			//TITLE NAME
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
			String text = "Select Your Class";
			int x = getXforCenteredText(text);
			int y = gp.tileSize*3;
			
			//SHADOW
			g2.setColor(Color.gray);
			g2.drawString(text, x+5, y+5);
			
			//MAIN COLOR
			g2.setColor(Color.white);
			g2.drawString(text, x, y);
			
			//CLASS OPTIONS
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
			
			text = "WARRIOR";
			x = getXforCenteredText(text);
			y += gp.tileSize*4;
			g2.drawString(text, x, y);
			if (commandNum == 0) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			
			text = "MAGE";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			
			text = "ARCHER";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 2) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			
			text = "BACK";
			x = getXforCenteredText(text);
			y += gp.tileSize*2;
			g2.drawString(text, x, y);
			if (commandNum == 3) {
				g2.drawString(">", x-gp.tileSize, y);
			}

		}
	}
	
	public void drawPauseScreen() {
		String text = "PAUSED";
		int x = getXforCenteredText(text);
		int y = gp.screenHeight/2;
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
		g2.drawString(text, x, y);
		
	}
	
	public void drawDialogueScreen() {
		// WINDOW
		int x = gp.tileSize*2;
		int y = gp.tileSize*6;
		int width = gp.screenWidth - (gp.tileSize*4);
		int height = gp.tileSize*4;
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			//g2.drawString(line, x, y);
			g2.drawString(line, x, y);
			y += 40;
		}
		
	}
	
	public void drawSubWindow(int x, int y, int width, int height) {
		Color c = new Color(0, 0, 0, 210);
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		c = new Color(255, 255, 255);
		g2.setColor(c);
		g2.setStroke(new java.awt.BasicStroke(5));
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
	}
	
	public int getXforCenteredText(String text) {
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = gp.screenWidth/2 - length/2;
		return x;
	}

}
