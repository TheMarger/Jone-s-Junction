/*
 Christina Heaven
 GamePanel class
 Description: This class controls the main game screen. 
 It loads the map, manages game objects,
 and draws everything to the screen.
 Used for: Rendering the game & handling map drawing
 */
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.Dimension;

public class GamePanel extends JPanel {

    private map map; //does the drawing & layout
    private TileManager tileManager; // Loads the tile images & solid data

    public GamePanel() {
        tileManager = new TileManager(); //Loads all tiles & their solidity

        // Load map data
        String mapData = loadMapFile("/Level1Map.txt");

        map = new map(mapData); //Creates the map 

    }
    @Override
    //Preferred panel size
    public Dimension getPreferredSize() { 

        return new Dimension(map.WIDTH * 16, map.HEIGHT * 16);
    }

    
    //Loads a mag file from res & returns it as a single string
    private String loadMapFile(String path) {
        StringBuilder sb = new StringBuilder();

        try (InputStream is = getClass().getResourceAsStream(path)) {

            if (is == null) { //I hate crashes
                System.out.println("ERORRRRRRRRRR: map file not found" + path);
                return "";   
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

         //Reads each line of the map file and store it as one string
            while ((line = br.readLine()) != null) {
                sb.append(line).append(" ");
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    //Draws the game
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        map.draw(g2, tileManager); //Draw map using tile manager
    }
}

