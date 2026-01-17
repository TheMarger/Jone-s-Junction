package task;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import main.UserInterface;
import main.gamePanel;

public class LogicPanelTaskUI {

    private static class LogicStatement {
        String text;
        boolean isTrue;
        LogicStatement(String text, boolean isTrue) {
            this.text = text;
            this.isTrue = isTrue;
        }
    }

    //The Statements//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final LogicStatement[] pool = new LogicStatement[] {
        new LogicStatement("All squares are rectangles.", true),
        new LogicStatement("Some rectangles are not squares.", true),
        new LogicStatement("If it rains, the ground gets wet.", true),
        new LogicStatement("The opposite of false is true.", true),
        new LogicStatement("If some cats are animals, then all animals are cats.", false),
        new LogicStatement("All birds can fly.", false),
        new LogicStatement("Penguins are birds.", true),
        new LogicStatement("If a number is even, it is divisible by 2.", true),
        new LogicStatement("All prime numbers are odd.", false),
        new LogicStatement("Zero is a positive number.", false),
        new LogicStatement("The sun rises in the east.", true),
        new LogicStatement("All fish can walk on land.", false),
        new LogicStatement("Water freezes at 0°C.", true),
        new LogicStatement("Some fruits are vegetables.", false),
        new LogicStatement("If a shape is a circle, it has corners.", false),
        new LogicStatement("All mammals have hair.", true),
        new LogicStatement("Bats are mammals.", true),
        new LogicStatement("All mammals lay eggs.", false),
        new LogicStatement("A triangle has three sides.", true),
        new LogicStatement("If 2+2=5, then pigs can fly.", true),
        new LogicStatement("If it is raining, then it is cloudy.", true),
        new LogicStatement("Some animals are plants.", false),
        new LogicStatement("Every even number is divisible by 4.", false),
        new LogicStatement("The Earth orbits the Sun.", true),
        new LogicStatement("Some rectangles are squares.", true),
        new LogicStatement("All fruits have seeds.", true),
        new LogicStatement("Some fruits do not have seeds.", false),
        new LogicStatement("If a number is divisible by 4, it is even.", true),
        new LogicStatement("The Moon produces its own light.", false),
        new LogicStatement("Fire is cold.", false),
        new LogicStatement("If a number is divisible by 5, it ends with 5.", false),
        new LogicStatement("A hexagon has 6 sides.", true),
        new LogicStatement("All colors are visible to humans.", false),
        new LogicStatement("Humans need oxygen to survive.", true),
        new LogicStatement("If a number is odd, it is not divisible by 2.", true),
        new LogicStatement("All cars have wheels.", true),
        new LogicStatement("Some cars do not have wheels.", false),
        new LogicStatement("If a shape has four equal sides, it is a square.", false),
        new LogicStatement("All triangles have 180° total angles.", true),
        new LogicStatement("Some circles have corners.", false),
    };

    //Task stetup//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int ROWS = 6;

    //The 6 active statements
    private int[] chosenPoolIndexes = new int[ROWS];

    //-1 = not chosen yet, 1=True, 0=False
    private int[] choice = new int[ROWS];

    private boolean generated = false;
    private boolean submitted = false;
    private boolean allCorrect = false;

    //The UI hit boxes
    private Rectangle[] trueBtn = new Rectangle[ROWS];
    private Rectangle[] falseBtn = new Rectangle[ROWS];
    private Rectangle submitBtn;

    //Reset states
    public void reset() {
        generated = false;
        submitted = false;
        allCorrect = false;
        for (int i = 0; i < ROWS; i++) choice[i] = -1;
    }

    
    public void updateAndDraw(Graphics2D g2, gamePanel gp, UserInterface ui) {

        //overlay
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        //Panel
        int panelW = gp.tileSize * 14;
        int panelH = gp.tileSize * 10;
        int panelX = (gp.screenWidth - panelW) / 4;
        int panelY = (gp.screenHeight - panelH) / 2;

        g2.setColor(new Color(30, 30, 30, 230));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 26, 26);
        g2.setColor(new Color(255, 255, 255, 70));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 26, 26);

        //title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 42f));
        g2.setColor(Color.white);
        g2.drawString("Logic Panel", panelX + gp.tileSize, panelY + gp.tileSize);

        //Cooldown block, so it can reset from previous answers//////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (ui.getTaskCooldownFrames() > 0) {
            reset();
            int s = (ui.getTaskCooldownFrames() + 59) / 60;

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26f));
            g2.setColor(Color.lightGray);
            drawCentered(g2, gp, "Tasks locked. Try again in " + s + "s", gp.screenHeight / 2);

            gp.mouseClicked = false;
            gp.keyH.enterPressed = false;
            return;
        }

        //Generate new set once
        if (!generated) {
            generatePanel();
            generated = true;
        }

        //Layout numbers//////////////////////////////////////////////////////////////////////////////////////////////////////////
        int leftX = panelX + gp.tileSize;
        int rowYStart = panelY + (int)(gp.tileSize * 2.0);
        int rowH = (int)(gp.tileSize * 1.1);

        int statementW = (int)(panelW * 0.62);
        int toggleW = (int)(panelW * 0.14);
        int toggleH = (int)(gp.tileSize * 0.8);

        int togglesX = leftX + statementW + gp.tileSize / 2;

        //Draw rows
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));
        for (int r = 0; r < ROWS; r++) {
            int y = rowYStart + r * rowH;

            //Statement text, wrapped
            g2.setColor(new Color(230, 230, 230));
            String text = pool[chosenPoolIndexes[r]].text;
            drawTrimmed(g2, text, leftX, y, statementW);

            //The true button//////////////////////////////////////////////////////////////////////////////////////////////////////////
            int toggleShiftLeft = gp.tileSize;
            int togglesXX = leftX + statementW + gp.tileSize / 2 - toggleShiftLeft;
           
            int gap = gp.tileSize / 6; //Small gap
            int falseX = togglesXX + toggleW + gap;

            trueBtn[r] = new Rectangle(togglesXX, y - toggleH + 8, toggleW, toggleH);
            falseBtn[r] = new Rectangle(falseX, y - toggleH + 8, toggleW, toggleH);

            drawToggle(g2, trueBtn[r], "TRUE", choice[r] == 1);
            drawToggle(g2, falseBtn[r], "FALSE", choice[r] == 0);
        }

        //The submit button
        int subW = gp.tileSize * 5;
        int subH = gp.tileSize;
        int subX = panelX + (panelW - subW)/2;
        int subY = panelY + panelH - subH - gp.tileSize;

        submitBtn = new Rectangle(subX, subY, subW, subH);
        drawSubmit(g2, submitBtn, "SUBMIT", submitted);

        //Click handling
        if (!allCorrect && gp.mouseClicked) {
            gp.mouseClicked = false;
            int mx = gp.mouseX;
            int my = gp.mouseY;

            //Toggles//////////////////////////////////////////////////////////////////////////////////////////////////////////
            for (int r = 0; r < ROWS; r++) {
                if (trueBtn[r] != null && trueBtn[r].contains(mx, my)) {
                    choice[r] = 1;
                    return;
                }
                if (falseBtn[r] != null && falseBtn[r].contains(mx, my)) {
                    choice[r] = 0;
                    return;
                }
            }

            //Submit
            if (submitBtn != null && submitBtn.contains(mx, my)) {
                submitted = true;

                //If any unanswered/wrong = fail 
                if (!checkAllCorrect()) {
                    ui.handleTaskFailed(10,
                        "One/more answers are incorrect. Task failed. Cooldown 10secs.");
                    return;
                }

                //All correct
                allCorrect = true;
            }
        }

        //Success screen
        if (allCorrect) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 34f));
            g2.setColor(new Color(120, 220, 140));
            drawCentered(g2, gp, "ALL CORRECT", panelY + panelH - gp.tileSize * 2);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
            g2.setColor(new Color(220, 220, 220));
            drawCentered(g2, gp, "Press ENTER to continue", panelY + panelH - gp.tileSize);

            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;
                ui.handleTaskSuccess("Logic Panel Completed!");
                return;
            }
        }
    }

    //Generate new 6 statements
    private void generatePanel() {
        ArrayList<Integer> idx = new ArrayList<>();
        for (int i = 0; i < pool.length; i++) idx.add(i);
        Collections.shuffle(idx);

        for (int r = 0; r < ROWS; r++) {
            chosenPoolIndexes[r] = idx.get(r);
            choice[r] = -1; //Reset choices
        }
        submitted = false;
        allCorrect = false;
    }

    //Check correctness on submit//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean checkAllCorrect() {
        for (int r = 0; r < ROWS; r++) {
            if (choice[r] == -1) return false; //Unanswered means fail
            boolean correctTruth = pool[chosenPoolIndexes[r]].isTrue;
            boolean pickedTruth = (choice[r] == 1);
            if (pickedTruth != correctTruth) return false;
        }
        return true;
    }

   
    //Drawing Panel//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawToggle(Graphics2D g2, Rectangle rect, String label, boolean selected) {
        Color base = selected ? new Color(40, 140, 70, 210) : new Color(0, 0, 0, 160);
        g2.setColor(base);
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        g2.setColor(new Color(255, 255, 255, 90));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(Color.white);
        int tx = rect.x + rect.width / 2 - g2.getFontMetrics().stringWidth(label) / 2;
        int ty = rect.y + rect.height / 2 + 6;
        g2.drawString(label, tx, ty);
    }
    //Submit button
    private void drawSubmit(Graphics2D g2, Rectangle rect, String label, boolean pressed) {
        Color base = pressed ? new Color(80, 80, 80, 220) : new Color(0, 0, 0, 160);
        g2.setColor(base);
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        g2.setColor(new Color(255, 255, 255, 90));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
        g2.setColor(new Color(240, 220, 80));
        int tx = rect.x + rect.width / 2 - g2.getFontMetrics().stringWidth(label) / 2;
        int ty = rect.y + rect.height / 2 + 7;
        g2.drawString(label, tx, ty);
    }

    private void drawCentered(Graphics2D g2, gamePanel gp, String text, int y) {
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        g2.drawString(text, x, y);
    }

    //Trims text to fit in a width 
    private void drawTrimmed(Graphics2D g2, String text, int x, int y, int maxW) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxW) {
            g2.drawString(text, x, y);
            return;
        }

        String ell = "...";
        int ellW = fm.stringWidth(ell);

        String s = text;
        while (s.length() > 0 && fm.stringWidth(s) + ellW > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        g2.drawString(s + ell, x, y);
    }
}

