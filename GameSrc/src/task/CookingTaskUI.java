package task;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import main.UserInterface;
import main.gamePanel;

public class CookingTaskUI {
	 private gamePanel gp;
	private BufferedImage background;


    //Questions & Answers////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final String[] questions = {
        "What temperature does water boil at (at sea level)?",
        "Which ingredient makes bread rise?",
        "What is the main ingredient in guacamole?",
        "At approximately what temperature does chocolate begin to melt?",
        "Which cooking method uses dry heat and circulating air to cook food?",
        "Which knife is best for chopping vegetables?",
        "What does “al dente” refer to?",
        "Which of these is a dry-heat cooking technique?",
        "Which fat is commonly used for sautéing because of its high smoke point?",
        "What is the recommended minimum internal temperature for cooked chicken (whole or pieces)?",
        "What is the purpose of blanching vegetables?",
        "Which of the following is a leavening agent?",
        "What does “mise en place” mean?",
        "Which cut of meat is typically most tender?",
        "Which of these is best for making mayonnaise?",
        "Which spice is the main ingredient in curry powder?",
        "Which method cooks food by surrounding it with steam?",
        "What ingredient is primarily responsible for browning via the Maillard reaction?",
        "Which of the following is a dry, cured pork product?",
        "Which of these is NOT a mother sauce in classical French cuisine?",
        "What is the culinary term for cutting food into long, thin strips?",
        "Which cheese is traditionally used on pizza for its meltability?",
        "What does it mean to “deglaze” a pan?",
        "Which of these flours has the highest protein (gluten) content, typically used for bread?",
        "What is the safe refrigerator temperature to slow bacterial growth?",
        "Which fruit is high in vitamin C and commonly used to prevent scurvy?",
        "What does “folding” mean in baking?",
        "Which method uses water just below boiling to cook delicate foods?",
        "Which of these is a common emulsifier used in cooking?",
        "What kitchen tool measures dry ingredient volume most accurately?",
        "Which grain is used to make risotto?",
        "Which method is best for making stock from bones?",
        "Which herb is the primary ingredient in pesto?",
        "What’s the common thickener in cream soups?",
        "Which oil is traditionally used in Japanese tempura for frying?",
        "Which of these indicates a cake is fully baked?",
        "Which foodborne pathogen is commonly associated with undercooked eggs?",
        "What is \"umami\"?",
        "Which of the following is a quick method for tenderizing meat?",
        "Which acid is commonly used to “cook” fish in ceviche?"
    };

    private final String[] answers = {
        "100°C",
        "Yeast",
        "Avocado",
        "30–32°C",
        "Baking",
        "Chef’s (cook’s) knife",
        "Pasta cooked to be firm to the bite",
        "Grilling",
        "Vegetable oil (e.g., canola, sunflower)",
        "74°C (165°F)",
        "To briefly cook and stop enzyme action for color/texture",
        "Baking powder",
        "Everything in its place (prep before cooking)",
        "Tenderloin / Filet",
        "Raw egg yolk emulsified with oil and acid",
        "Turmeric",
        "Steaming",
        "Proteins and reducing sugars",
        "Prosciutto",
        "Pesto",
        "Julienne",
        "Mozzarella",
        "Add liquid to dissolve browned bits for sauce",
        "Bread (strong) flour",
        "4°C or below",
        "Orange",
        "Gently combine delicate ingredients to preserve air",
        "Poaching",
        "Lecithin (egg yolk)",
        "Kitchen scale (weight)",
        "Arborio rice",
        "Simmering bones for hours",
        "Basil",
        "Roux (butter + flour)",
        "Vegetable oil (neutral) or sesame blend",
        "A toothpick inserted comes out clean",
        "Salmonella",
        "A savory taste (fifth basic taste)",
        "Pounding with a mallet",
        "Citric acid (lime or lemon juice)"
    };

    //State//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean generated = false; //Checks if questions has happened already
    private int qIndex = 0; //Which question index was picked

    //Timer State, counts from 1800 to 0
    private final int timeLimitFrames = 30 * 60; 
    private int timerFrames = 0; 
    
    //Answer options state
    private String[] optionTexts = new String[4]; //Buttons
    private int correctOption = 0; //Index of the answer

    private boolean answered = false;
    private boolean answerCorrect = false;

    private Rectangle[] buttons = new Rectangle[4];
    
    //Sets the UI back to a start state, for cooldown & start of task
    public void reset() {
        generated = false;
        answered = false;
        answerCorrect = false;
        timerFrames = 0;
    }

    //Design/////////////////////////////////////////////////////////////
    public void updateAndDraw(Graphics2D g2, gamePanel gp, UserInterface ui) {
    	
        //The centered panel
        int panelW = gp.tileSize * 12;
        int panelH = gp.tileSize * 9;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;

        g2.setColor(new Color(30, 30, 30, 230));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 26, 26);
        g2.setColor(new Color(255, 255, 255, 70));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 26, 26);

        //Title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 42f));
        g2.setColor(Color.white);
        g2.drawString("Cooking Task", panelX + gp.tileSize, panelY + gp.tileSize);

        
     //Cooldown Check////////////////////////////////////////////////////////////////////////////////////////////
     if (ui.getTaskCooldownFrames() > 0) { //If cooldown is active, do not let them answer
         reset();
         int s = (ui.getTaskCooldownFrames() + 59) / 60;

         g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26f));
         g2.setColor(Color.lightGray);
         drawCentered(g2, gp, "Tasks locked. Try again in " + s + "s", gp.screenHeight / 2);
         
         // Make sure the hidden button can't be clicked
         gp.mouseClicked = false;
         gp.keyH.enterPressed = false;
         return; //Stops the drawing
     }


      //Generates the question once per attempt///////////////////////////////////////////////////////
       if (!generated) {
          generateNewQuestion();
          generated = true;
          answered = false;
          answerCorrect = false;

        //Starts a 30sec timer when question appears
         timerFrames = timeLimitFrames;
        }

       
        //Timer countdown/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (!answered && timerFrames > 0) timerFrames--;

        if (!answered && timerFrames <= 0) { //if time runs out, fail the task & start cooldown
            ui.handleTaskFailed(10, "You burned the food! Try again in 10 seconds");
            return;
        }

        
        //Draw the timer bar////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int secondsLeft = (timerFrames + 59) / 60;
        String timeText = "Time: " + secondsLeft + " s";
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));
        g2.setColor(new Color(220, 220, 220));
        int tx = panelX + panelW - gp.tileSize - g2.getFontMetrics().stringWidth(timeText);
        int ty = panelY + gp.tileSize;
        g2.drawString(timeText, tx, ty);

        //Bar
        int barW = g2.getFontMetrics().stringWidth(timeText);
        int barH = 8;
        int barX = tx;
        int barY = ty + 6;

        float ratio = (float) timerFrames / (float) timeLimitFrames;
        ratio = Math.max(0f, Math.min(1f, ratio));

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

        Color col = (ratio > 0.6f) ? new Color(120, 220, 140)
                : (ratio > 0.25f) ? new Color(240, 200, 80)
                : new Color(240, 120, 120);

        int fillW = Math.max(2, (int) (barW * ratio));
        g2.setColor(col);
        g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);

        g2.setColor(new Color(255, 255, 255, 70));
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH);

        
        //Draws the question///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
        g2.setColor(new Color(230, 230, 230));
        int qAreaX = panelX + gp.tileSize;
        int qAreaY = panelY + (int) (gp.tileSize * 1.6);
        int qAreaW = panelW - gp.tileSize * 2;
        drawWrapped(g2, questions[qIndex], qAreaX, qAreaY, qAreaW, 26);

        
        //Button layout/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int btnW = panelW - gp.tileSize * 2;
        int btnH = gp.tileSize;
        int btnX = panelX + gp.tileSize;
        int btnStartY = panelY +(int)( gp.tileSize * 2.2);

        for (int i = 0; i < 4; i++) {
            int by = btnStartY + i * (btnH + gp.tileSize / 3);
           //Detects if clicked inside box
            buttons[i] = new Rectangle(btnX, by, btnW, btnH);

            Color base = new Color(0, 0, 0, 160);
            
            //Highlights correct answer if answered correctly
            if (answered) {
                if (i == correctOption) base = new Color(40, 140, 70, 200);
                else if (!answerCorrect) base = new Color(70, 70, 70, 200);
            }

            g2.setColor(base);
            g2.fillRoundRect(btnX, by, btnW, btnH, 14, 14);
            g2.setColor(new Color(255, 255, 255, 90));
            g2.drawRoundRect(btnX, by, btnW, btnH, 14, 14);

            String label = switch (i) { 
            case 0 -> "A"; 
            case 1 -> "B"; 
            case 2 -> "C"; 
            default -> "D"; };

            //Draw label
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            g2.setColor(new Color(240, 220, 80));
            g2.drawString(label + ")", btnX + 16, by + btnH / 2 + 7);
            //Draw answer text next to label
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));
            g2.setColor(Color.white);
            g2.drawString(optionTexts[i], btnX + 60, by + btnH / 2 + 7);
        }

        
        //Click handling///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (!answered && gp.mouseClicked) {
            gp.mouseClicked = false;

            int mx = gp.mouseX;
            int my = gp.mouseY;

            //Checks which button was clicked
            for (int i = 0; i < 4; i++) {
                if (buttons[i] != null && buttons[i].contains(mx, my)) {
                    answered = true;
                    answerCorrect = (i == correctOption);

                    //Wrong answer means fail and countdown
                    if (!answerCorrect) {
                        ui.handleTaskFailed(10, "Food burned, Try again in 10 seconds");
                        return;
                    }
                    break;
                }
            }
        }

        //Correct answer means, message & wait for enter to complete task
        if (answered && answerCorrect) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 34f));
            g2.setColor(new Color(120, 220, 140));
            drawCentered(g2, gp, "Correct", panelY + panelH - gp.tileSize * 2);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
            g2.setColor(new Color(220, 220, 220));
            drawCentered(g2, gp, "Press ENTER to continue", panelY + panelH - gp.tileSize);

            //Task finish = Enter is clicked
            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;
                ui.handleTaskSuccess("Task Completed!");
                return;
            }
        }
    }

    //Generate new question
    private void generateNewQuestion() {
    	//Picks a random question
        qIndex = (int) (Math.random() * questions.length);
        String correct = answers[qIndex];

        //Builds a pool of wrong answers
        ArrayList<String> pool = new ArrayList<>();
        for (int i = 0; i < answers.length; i++) {
            if (i == qIndex) continue; //skip correct answer
            pool.add(answers[i]);
        }
        //Shuffle the wrong answer pool
        Collections.shuffle(pool);

        //Takes 4 wrong answers & the correct answer
        ArrayList<String> opts = new ArrayList<>();
        opts.add(correct);

        //Adds the wrong answers
        int idx = 0;
        while (opts.size() < 4 && idx < pool.size()) {
            String w = pool.get(idx++);
            if (!opts.contains(w)) opts.add(w); //Prevents doubles
        }

        Collections.shuffle(opts);//Shuffles options
        
        //Stores the 4 options
        for (int i = 0; i < 4; i++) optionTexts[i] = opts.get(i);
        correctOption = opts.indexOf(correct); //Store where correct answer ended up
    }

    //Centers the text
    private void drawCentered(Graphics2D g2, gamePanel gp, String text, int y) {
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        g2.drawString(text, x, y);
    }

    private void drawWrapped(Graphics2D g2, String text, int x, int y, int maxW, int lineH) {
        if (text == null) return;
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        int yy = y;

        for (String w : words) {
            String candidate = (line.length() == 0) ? w : (line + " " + w);
            if (fm.stringWidth(candidate) <= maxW) {
                if (line.length() == 0) line.append(w);
                else line.append(" ").append(w);
            } else {
                g2.drawString(line.toString(), x, yy);
                yy += lineH;
                line.setLength(0);
                line.append(w);
            }
        }
        if (line.length() > 0) g2.drawString(line.toString(), x, yy);
    }
}
