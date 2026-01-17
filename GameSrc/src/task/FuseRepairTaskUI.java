package task;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import main.UserInterface;
import main.gamePanel;

public class FuseRepairTaskUI {

    //Colors
    private static final int NODES = 9;

    //colors 
    private final Color[] baseColors = new Color[] {
            new Color(235,  64,  52), //red
            new Color( 52, 140, 235), //blue
            new Color( 52, 235, 110), //green
            new Color(235, 220,  52), //yellow
            new Color(180,  52, 235), //purple
            new Color(235, 130,  52), //orange
            new Color( 52, 235, 235), //cyan
            new Color(235,  52, 160), //pink
            new Color(160, 160, 160)  //gray
    };

    //States
    private boolean generated = false;

    //0-8, the shuffled colors, and their id's
    private int[] leftOrder  = new int[NODES];
    private int[] rightOrder = new int[NODES];

    //UI hit areas
    private Rectangle[] leftHit  = new Rectangle[NODES];
    private Rectangle[] rightHit = new Rectangle[NODES];

    //Which left node is currently selected, -1 = none
    private int selectedLeftIndex = -1;

    //Track completed connections
    private boolean[] connectedColor = new boolean[NODES];
    private int connectionsMade = 0;

    // Red flash after wrong click
    private int wrongFlashFrames = 0;

    //After finishing
    private boolean allCorrect = false;

    //Resets task to start over
    public void reset() {
        generated = false;
        selectedLeftIndex = -1;
        connectionsMade = 0;
        allCorrect = false;
        wrongFlashFrames = 0;

        for (int i = 0; i < NODES; i++) {
            connectedColor[i] = false;
            leftHit[i] = null;
            rightHit[i] = null;
        }
    }

    public void updateAndDraw(Graphics2D g2, gamePanel gp, UserInterface ui) {

        //Cooldown block
        if (ui.getTaskCooldownFrames() > 0) {
            reset();

            //Dark overlay
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            int s = (ui.getTaskCooldownFrames() + 59) / 60;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26f));
            g2.setColor(Color.lightGray);
            drawCentered(g2, gp, "Tasks locked. Try again in " + s + "s", gp.screenHeight / 2);

            gp.mouseClicked = false;
            gp.keyH.enterPressed = false;
            return;
        }

        //Generates a new layout each time
        if (!generated) {
            generateNewArrangement();
            generated = true;
        }

        // Background Overlay
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        //Panel size
        int panelW = gp.tileSize * 14;
        int panelH = gp.tileSize * 10;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;

        g2.setColor(new Color(30, 30, 30, 230));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 26, 26);
        g2.setColor(new Color(255, 255, 255, 70));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 26, 26);

        //Tile
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 42f));
        g2.setColor(Color.white);
        g2.drawString("Fuse Repair", panelX + gp.tileSize, panelY + gp.tileSize);

        //Subtitle
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));
        g2.setColor(new Color(220, 220, 220));
        g2.drawString("Connect matching nodes (" + connectionsMade + "/" + NODES + ")",
                panelX + gp.tileSize, panelY + (int)(gp.tileSize * 1.6));

        //Layout math for nodes
        int nodeRadius = (int)(gp.tileSize * 0.40);         //circle radius
        int nodeDiam   = nodeRadius * 2;

        int topY = panelY + (int)(gp.tileSize * 2.4);
        int bottomY = panelY + panelH - (int)(gp.tileSize * 1.1);
        int usableH = bottomY - topY;

        int gapY = usableH / (NODES - 1);

        int leftX  = panelX + (int)(gp.tileSize * 2.0);
        int rightX = panelX + panelW - (int)(gp.tileSize * 2.0);

        //Draw center guide line
        g2.setColor(new Color(255,255,255,40));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine((leftX + rightX)/2, topY - gp.tileSize/2, (leftX + rightX)/2, bottomY + gp.tileSize/2);

        //Wires
        g2.setStroke(new BasicStroke(4f));
        for (int colorId = 0; colorId < NODES; colorId++) {
            if (!connectedColor[colorId]) continue;

            int li = findIndexInOrder(leftOrder, colorId);
            int ri = findIndexInOrder(rightOrder, colorId);

            int ly = topY + li * gapY;
            int ry = topY + ri * gapY;

            g2.setColor(new Color(baseColors[colorId].getRed(), baseColors[colorId].getGreen(), baseColors[colorId].getBlue(), 200));
            g2.drawLine(leftX, ly, rightX, ry);
        }

        //Draw nodes and hit radius
        for (int i = 0; i < NODES; i++) {
            int y = topY + i * gapY;

            //Left node
            int leftColorId = leftOrder[i];
            leftHit[i] = new Rectangle(leftX - nodeRadius, y - nodeRadius, nodeDiam, nodeDiam);

            drawNode(g2, leftX, y, nodeRadius, baseColors[leftColorId],
                    connectedColor[leftColorId],
                    (selectedLeftIndex == i));

            //Right node
            int rightColorId = rightOrder[i];
            rightHit[i] = new Rectangle(rightX - nodeRadius, y - nodeRadius, nodeDiam, nodeDiam);

            drawNode(g2, rightX, y, nodeRadius, baseColors[rightColorId],
                    connectedColor[rightColorId],
                    false);
        }

        //Wrong flash overlay
        if (wrongFlashFrames > 0) {
            wrongFlashFrames--;
            g2.setColor(new Color(255, 0, 0, 80));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        //Click handling
        if (!allCorrect && gp.mouseClicked) {
            gp.mouseClicked = false;

            int mx = gp.mouseX;
            int my = gp.mouseY;

            //If no left selected, only accept left clicks
            if (selectedLeftIndex == -1) {
                for (int i = 0; i < NODES; i++) {
                    if (leftHit[i] != null && leftHit[i].contains(mx, my)) {
                        int colorId = leftOrder[i];

                        //Don't allow selecting already connected colors
                        if (!connectedColor[colorId]) {
                            selectedLeftIndex = i;
                        }
                        return;
                    }
                }
            } else {
                //Left is selected, therefore must click
                for (int j = 0; j < NODES; j++) {
                    if (rightHit[j] != null && rightHit[j].contains(mx, my)) {

                        int leftColorId  = leftOrder[selectedLeftIndex];
                        int rightColorId = rightOrder[j];

                        //If user clicked a color already connected, ignore the click
                        if (connectedColor[rightColorId]) {
                            selectedLeftIndex = -1;
                            return;
                        }

                        //Check match
                        if (leftColorId != rightColorId) {
                            wrongFlashFrames = 15; //Red flash
                            selectedLeftIndex = -1;

                            ui.handleTaskFailed(10,
                                    "Incorrect connection. The task failed. Retry after 10secs .");
                            return;
                        }

                        //Correct connection
                        connectedColor[leftColorId] = true;
                        connectionsMade++;
                        selectedLeftIndex = -1;

                        if (connectionsMade >= NODES) {
                            allCorrect = true;
                        }
                        return;
                    }
                }

                //If they clicked somewhere else, cancel selection
                selectedLeftIndex = -1;
            }
        }

        //Success
        if (allCorrect) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 34f));
            g2.setColor(new Color(120, 220, 140));
            drawCentered(g2, gp, "Fuse Repaired", panelY + panelH - gp.tileSize * 2);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
            g2.setColor(new Color(220, 220, 220));
            drawCentered(g2, gp, "Press ENTER to continue", panelY + panelH - gp.tileSize);

            if (gp.keyH.enterPressed) {
                gp.keyH.enterPressed = false;
                ui.handleTaskSuccess("Fuse Repair Completed!");
                return;
            }
        }
    }

    //Helpers

    private void generateNewArrangement() {
        //Make 0-8 nodes and shuffle for each side independently
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 0; i < NODES; i++) ids.add(i);

        Collections.shuffle(ids);
        for (int i = 0; i < NODES; i++) leftOrder[i] = ids.get(i);

        Collections.shuffle(ids);
        for (int i = 0; i < NODES; i++) rightOrder[i] = ids.get(i);

        //Reset progress
        for (int i = 0; i < NODES; i++) connectedColor[i] = false;
        connectionsMade = 0;
        selectedLeftIndex = -1;
        allCorrect = false;
        wrongFlashFrames = 0;
    }

    private int findIndexInOrder(int[] order, int colorId) {
        for (int i = 0; i < order.length; i++) {
            if (order[i] == colorId) return i;
        }
        return 0;
    }

    private void drawNode(Graphics2D g2, int cx, int cy, int r, Color col, boolean connected, boolean selected) {

        //Outer ring 
        if (selected) {
            g2.setColor(new Color(240, 220, 80, 220));
            g2.fillOval(cx - r - 6, cy - r - 6, (r + 6) * 2, (r + 6) * 2);
        }

        //Node fill
        g2.setColor(col);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

    }

    private void drawCentered(Graphics2D g2, gamePanel gp, String text, int y) {
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        g2.drawString(text, x, y);
    }
}

