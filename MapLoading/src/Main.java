/*
 Christina Heaven
 Main class
 Description: This class starts the game. 
 It creates a window to display GamPanel.
 Used for: Launching the game application.
 */
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
    	//Create the window
        JFrame window = new JFrame("idk");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null); // centers on screen
        window.setVisible(true);
    }
}
