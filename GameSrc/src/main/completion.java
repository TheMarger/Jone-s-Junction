package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class completion {

	  gamePanel gp;
	  
	//The different parts of the ending
	  private enum Phase {
		    FadeOut1,
		    FadeIn1,
		    Hold1,
		    FadeOut2,
		    FadeIn2,
		    Hold2,
		    Credits
		}

	    
	    private Phase phase = Phase.FadeOut1;
	    
	 //Fade control (0 = transparent, 1 = fully black)
	    private float fadeT = 0f;

	    //Timers (counts frames)
	    private int timer = 0;

	    //Images
	    private BufferedImage img1;
	    private BufferedImage img2;

	    //Credits
	    private String[] creditsLines = {
	    	    "JONE'S JUNCTION",
	    	    "",
	    	    "Thanks for playing:)",
	    	    "",
	    	    "Created by:",
	    	    "The Best Team",
	    	    "",
	    	    "Project Management:",
	    	    "Sukhmanpreet Gill",
	    	    "Rafay Salman",
	    	    "Christina Heaven",
	    	    "Samir Bhagat",
	    	    "Jeevan Dhillon",
	    	    "",
	    	    "Programming:",
	    	    "Team B",
	    	    "",
	    	    "UI & Interactions:",
	    	    "Rafay Salman",
	    	    "",
	    	    "Save System & Game Progress:",
	    	    "Samir Bhagat",
	    	    "",
	    	    "Items & Player Mechanics:",
	    	    "Jeevan Dhillon",
	    	    "",
	    	    "AI & Guard Behavior:",
	    	    "Sukhmanpreet Gill",
	    	    "",
	    	    "Art / Pixel Design:",
	    	    "Christina Heaven",
	    	    "",
	    	    "Tasks:",
	    	    "Rafay Salman",
	    	    "Sukhmanpreet Gill",
	    	    "Samir Bhagat",
	    	    "Christina Heaven",
	    	    "Jeevan Dhillon",
	    	    "",
	    	    "Special Thanks:",
	    	    "Mr. Jones",
	    	    "Class of 2026",
	    	    "",
	    	    "THE END"
	    	};


	    private int creditsY; //Where credits start drawing (& move up)

	    //Speeds
	    private float fadeSpeed = 0.007f; //Per frame
	    private int showFramesImage1 = 180; //3 seconds at 60fps
	    private int showFramesImage2 = 180;

	    public completion(gamePanel gp) {
	        this.gp = gp;

	        try {
	            img1 = ImageIO.read(getClass().getResourceAsStream("/ending/1.png"));
	            img2 = ImageIO.read(getClass().getResourceAsStream("/ending/2.png"));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        reset();
	    }

	    public void reset() {
	        phase = Phase.FadeOut1;
	        fadeT = 0f;
	        timer = 0;
	        creditsY = gp.screenHeight + 50;
	    }


	    //Call this once when player interacts with helicopter
	    public void start() {
	        reset();
	        //Switch the game into "completion mode"
	        gp.gameState = gp.completionState;
	    }

	    public void update() {

	        switch (phase) {

	            //Fade to black before image 1
	            case FadeOut1:
	                fadeT += fadeSpeed;// 0 to 1
	                if (fadeT >= 1f) {
	                    fadeT = 1f;
	                    phase = Phase.FadeIn1;//Reveal image 1
	                }
	                break;

	            //Reveal image 1, fade from black
	            case FadeIn1:
	                fadeT -= fadeSpeed;// 0 to 1
	                if (fadeT <= 0f) {
	                    fadeT = 0f;
	                    phase = Phase.Hold1;
	                    timer = 0;
	                }
	                break;

	            //Hold image 1 on screen
	            case Hold1:
	                timer++;
	                if (timer >= showFramesImage1) {
	                    phase = Phase.FadeOut2;
	                    fadeT = 0f;//Start fade out again
	                }
	                break;

	            //Fade to black before image 2
	            case FadeOut2:
	                fadeT += fadeSpeed;//0 to 1
	                if (fadeT >= 1f) {
	                    fadeT = 1f;
	                    phase = Phase.FadeIn2;//Next reveal image 2
	                }
	                break;

	            //Reveal image 2, fade from black
	            case FadeIn2:
	                fadeT -= fadeSpeed;//1 to 0
	                if (fadeT <= 0f) {
	                    fadeT = 0f;
	                    phase = Phase.Hold2;
	                    timer = 0;
	                }
	                break;

	            //Hold image 2
	            case Hold2:
	                timer++;
	                if (timer >= showFramesImage2) {
	                    phase = Phase.Credits;
	                    fadeT = 0f;//No overlay during credits
	                }
	                break;

	            //Credits scroll up
	            case Credits:
	                creditsY -= 1;

	                int creditsHeight = creditsLines.length * 30;
	                if (creditsY + creditsHeight < 0) {
	                    gp.gameState = gp.titleState;
	                }
	                break;
	        }
	    }


	    public void draw(Graphics2D g2) {
	        //Always clear background
	        g2.setColor(Color.black);
	        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

	        //Only show image 1 AFTER screen is fully black (FadeIn1 + Hold1)
	        if (phase == Phase.FadeIn1 || phase == Phase.Hold1) {
	            drawFullScreenImage(g2, img1);
	        }

	        //Fade in between images
	        else if (phase == Phase.FadeOut2) {
	            drawFullScreenImage(g2, img1);
	        }
	        
	        //Only show image 2 AFTER screen is fully black (FadeIn2 + Hold2)
	        else if (phase == Phase.FadeIn2 || phase == Phase.Hold2) {
	            drawFullScreenImage(g2, img2);
	        }

	        //Credits
	        else if (phase == Phase.Credits) {
	            drawCredits(g2);
	        }

	        //Fade overlay for FADE phases 
	        if (phase == Phase.FadeOut1 || phase == Phase.FadeIn1 ||
	        	    phase == Phase.FadeOut2 || phase == Phase.FadeIn2) {
	        	    drawFadeOverlay(g2, fadeT);
	        	}
	    }

	    private void drawFullScreenImage(Graphics2D g2, BufferedImage img) {
	        if (img == null) return;
	        g2.drawImage(img, 0, 0, gp.screenWidth, gp.screenHeight, null);
	    }

	    private void drawFadeOverlay(Graphics2D g2, float alpha) {
	        //Clamp
	        if (alpha < 0f) alpha = 0f;
	        if (alpha > 1f) alpha = 1f;

	        Composite old = g2.getComposite();
	        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	        g2.setColor(Color.black);
	        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
	        g2.setComposite(old);
	    }

	    private void drawCredits(Graphics2D g2) {
	        g2.setFont(new Font("Arial", Font.BOLD, 24));
	        g2.setColor(Color.white);

	        int xCenter = gp.screenWidth / 2;

	        int y = creditsY;
	        for (String line : creditsLines) {
	            int textWidth = g2.getFontMetrics().stringWidth(line);
	            int x = xCenter - (textWidth / 2);
	            g2.drawString(line, x, y);
	            y += 30;
	        }
	    }
	} 

