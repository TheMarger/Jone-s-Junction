/*
Name: Christina Heaven
Course: ICS4U0
Assignment Title: Jone's Junction
Date: 1/19/2026
File: completion.java
Program Description:
completion class. Handles the end-game sequence, including fade-in/out 
transitions, display of two ending images, and scrolling credits. 
Controls timing, fade speed, and rendering of images and text, allowing 
the player to experience a cinematic ending before returning to the title screen.
*/

package main; // Package for main game classes

import java.awt.*; // Import AWT for graphics and fonts
import java.awt.image.BufferedImage; // Import for image handling
import javax.imageio.ImageIO; // Import for reading images

public class completion {

    gamePanel gp; // Reference to main game panel

    // Enum representing the different phases of the ending sequence
    private enum Phase {
        FadeOut1, // Fade out to black before first image
        FadeIn1,  // Fade in first image
        Hold1,    // Hold first image on screen
        FadeOut2, // Fade out to black before second image
        FadeIn2,  // Fade in second image
        Hold2,    // Hold second image on screen
        Credits   // Scroll credits
    }

    private Phase phase = Phase.FadeOut1; // Current phase of ending sequence

    private float fadeT = 0f; // Fade overlay transparency (0 = transparent, 1 = fully black)
    private int timer = 0; // Timer for holding images (counts frames)

    private BufferedImage img1; // First ending image
    private BufferedImage img2; // Second ending image

    // Array storing lines for credits display
    private String[] creditsLines = {
        "JONE'S JUNCTION",
        "",
        "Thanks for playing:)",
        "",
        "Created by:",
        "The Best Team",
        "",
        "Project Management:",
        "Sukhmanpreet Singh",
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
        "Sukhmanpreet Singh",
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

    private int creditsY; // Vertical position for starting credits scroll

    private float fadeSpeed = 0.007f; // Speed of fade per frame
    private int showFramesImage1 = 180; // Duration to hold first image (3 seconds at 60fps)
    private int showFramesImage2 = 180; // Duration to hold second image

    public completion(gamePanel gp) { // Constructor receives game panel
        this.gp = gp;

        try { // Load ending images from resources
            img1 = ImageIO.read(getClass().getResourceAsStream("/ending/1.png"));
            img2 = ImageIO.read(getClass().getResourceAsStream("/ending/2.png"));
        } catch (Exception e) { // Handle exceptions in loading images
            e.printStackTrace();
        }

        reset(); // Initialize phase, timer, fade, and credits position
    }

    // Reset ending sequence variables
    public void reset() {
        phase = Phase.FadeOut1;
        fadeT = 0f;
        timer = 0;
        creditsY = gp.screenHeight + 50; // Start credits below screen
    }

    // Called once when player triggers the ending
    public void start() {
        reset();
        gp.gameState = gp.completionState; // Switch game into completion mode
    }

    // Update per-frame logic for the ending sequence
    public void update() {
        switch (phase) {
            case FadeOut1: // Fade to black before first image
                fadeT += fadeSpeed;
                if (fadeT >= 1f) {
                    fadeT = 1f;
                    phase = Phase.FadeIn1; // Transition to reveal first image
                }
                break;

            case FadeIn1: // Fade in first image
                fadeT -= fadeSpeed;
                if (fadeT <= 0f) {
                    fadeT = 0f;
                    phase = Phase.Hold1;
                    timer = 0;
                }
                break;

            case Hold1: // Hold first image
                timer++;
                if (timer >= showFramesImage1) {
                    phase = Phase.FadeOut2;
                    fadeT = 0f;
                }
                break;

            case FadeOut2: // Fade to black before second image
                fadeT += fadeSpeed;
                if (fadeT >= 1f) {
                    fadeT = 1f;
                    phase = Phase.FadeIn2; // Reveal second image
                }
                break;

            case FadeIn2: // Fade in second image
                fadeT -= fadeSpeed;
                if (fadeT <= 0f) {
                    fadeT = 0f;
                    phase = Phase.Hold2;
                    timer = 0;
                }
                break;

            case Hold2: // Hold second image
                timer++;
                if (timer >= showFramesImage2) {
                    phase = Phase.Credits;
                    fadeT = 0f; // No overlay during credits
                }
                break;

            case Credits: // Scroll credits upward
                creditsY -= 1; // Move credits up one pixel per frame
                int creditsHeight = creditsLines.length * 30; // Total height of all lines
                if (creditsY + creditsHeight < 0) { // When all credits have scrolled off screen
                    gp.gameState = gp.titleState; // Return to title screen
                }
                break;
        }
    }

    // Draw the current ending frame
    public void draw(Graphics2D g2) {
        g2.setColor(Color.black); // Clear background
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Draw first image if in FadeIn1 or Hold1 phase
        if (phase == Phase.FadeIn1 || phase == Phase.Hold1) {
            drawFullScreenImage(g2, img1);
        }
        // FadeOut2 uses previous image
        else if (phase == Phase.FadeOut2) {
            drawFullScreenImage(g2, img1);
        }
        // Draw second image
        else if (phase == Phase.FadeIn2 || phase == Phase.Hold2) {
            drawFullScreenImage(g2, img2);
        }
        // Draw scrolling credits
        else if (phase == Phase.Credits) {
            drawCredits(g2);
        }

        // Draw fade overlay for any fading phase
        if (phase == Phase.FadeOut1 || phase == Phase.FadeIn1 ||
            phase == Phase.FadeOut2 || phase == Phase.FadeIn2) {
            drawFadeOverlay(g2, fadeT);
        }
    }

    // Helper method to draw an image fullscreen
    private void drawFullScreenImage(Graphics2D g2, BufferedImage img) {
        if (img == null) return;
        g2.drawImage(img, 0, 0, gp.screenWidth, gp.screenHeight, null);
    }

    // Helper method to draw a fade overlay with transparency
    private void drawFadeOverlay(Graphics2D g2, float alpha) {
        if (alpha < 0f) alpha = 0f;
        if (alpha > 1f) alpha = 1f;

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setComposite(old);
    }

    // Draws the scrolling credits
    private void drawCredits(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.white);

        int xCenter = gp.screenWidth / 2;
        int y = creditsY;

        for (String line : creditsLines) {
            int textWidth = g2.getFontMetrics().stringWidth(line);
            int x = xCenter - (textWidth / 2); // Center text horizontally
            g2.drawString(line, x, y);
            y += 30; // Line spacing
        }
    }
}
