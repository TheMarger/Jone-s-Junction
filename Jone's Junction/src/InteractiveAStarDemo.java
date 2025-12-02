import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class InteractiveAStarDemo extends JPanel implements KeyListener {

    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int TILE_SIZE = 32;

    private PathfindingNode[][] grid = new PathfindingNode[ROWS][COLS];
    private java.util.List<PathfindingNode> path = new ArrayList<>();
    private int guardIndex = 0;

    private PathfindingNode start, end;

    private int cursorX = 0;
    private int cursorY = 0;

    private enum Mode {OBSTACLE, START, END}
    private Mode mode = Mode.OBSTACLE;

    public InteractiveAStarDemo() {
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        setFocusable(true);
        addKeyListener(this);

        initGrid();
        runPathfinding(); // initial path

        Timer timer = new Timer(700, e -> {
            if (!path.isEmpty() && guardIndex < path.size() - 1) {
                guardIndex++;
                repaint();
            }
        });
        timer.start();
    }

    private void initGrid() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                grid[x][y] = new PathfindingNode(x, y, true);
            }
        }
        start = grid[0][0];
        end = grid[ROWS - 1][COLS - 1];
    }

    private void resetGridCosts() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                PathfindingNode node = grid[x][y];
                node.gCost = Integer.MAX_VALUE;
                node.hCost = 0;
                node.fCost = 0;
                node.parent = null;
            }
        }
    }

    private void runPathfinding() {
        resetGridCosts();
        path = Pathfinding.findPath(grid, start, end);
        guardIndex = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw tiles
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                PathfindingNode node = grid[x][y];
                g.setColor(node.walkable ? Color.LIGHT_GRAY : Color.BLACK);
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.GRAY);
                g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Draw path
        g.setColor(Color.CYAN);
        for (int i = 0; i <= guardIndex && i < path.size(); i++) {
            PathfindingNode node = path.get(i);
            g.fillRect(node.xPos * TILE_SIZE, node.yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw guard
        if (!path.isEmpty()) {
            PathfindingNode guard = path.get(guardIndex);
            g.setColor(Color.RED);
            g.fillOval(guard.xPos * TILE_SIZE + 4, guard.yPos * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        }

        // Draw start and end
        g.setColor(Color.GREEN);
        g.fillRect(start.xPos * TILE_SIZE, start.yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.MAGENTA);
        g.fillRect(end.xPos * TILE_SIZE, end.yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw cursor
        g.setColor(Color.YELLOW);
        g.drawRect(cursorX * TILE_SIZE, cursorY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.drawRect(cursorX * TILE_SIZE + 1, cursorY * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> cursorY = Math.max(0, cursorY - 1);
            case KeyEvent.VK_DOWN -> cursorY = Math.min(ROWS - 1, cursorY + 1);
            case KeyEvent.VK_LEFT -> cursorX = Math.max(0, cursorX - 1);
            case KeyEvent.VK_RIGHT -> cursorX = Math.min(COLS - 1, cursorX + 1);

            case KeyEvent.VK_1 -> mode = Mode.OBSTACLE;
            case KeyEvent.VK_2 -> mode = Mode.START;
            case KeyEvent.VK_3 -> mode = Mode.END;

            case KeyEvent.VK_SPACE -> {
                PathfindingNode selected = grid[cursorX][cursorY];
                if (mode == Mode.OBSTACLE && selected != start && selected != end) {
                    selected.walkable = !selected.walkable; // toggle obstacle
                } else if (mode == Mode.START) {
                    start = selected;
                } else if (mode == Mode.END) {
                    end = selected;
                }
                runPathfinding(); // recalc path immediately
            }

            case KeyEvent.VK_ENTER -> runPathfinding(); // recalc path without changing tiles
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Keyboard Pathfinding Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new InteractiveAStarDemo());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}