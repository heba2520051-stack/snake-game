import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JFrame {
    
    public SnakeGame() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
    }
    
    public static void main(String[] args) {
        SnakeGame frame = new SnakeGame();
        frame.setVisible(true);
    }
    
    static class GamePanel extends JPanel implements KeyListener {
        private static final int CELL_SIZE = 30;
        private static final int GRID_WIDTH = 20;
        private static final int GRID_HEIGHT = 20;
        private List<int[]> snake;
        private int[] food;
        private int dirX = 1;   // Current direction (1 for right, -1 for left, 0 for vertical)
        private int dirY = 0;   // Current direction (1 for down, -1 for up, 0 for horizontal)
        private int nextDirX = 1;  // Next direction input
        private int nextDirY = 0;
        private Timer gameTimer;
        private int score = 0;
        private boolean gameOver = false;
        private Random random = new Random();
        
        public GamePanel() {
            setBackground(new Color(50, 50, 50));
            setFocusable(true);
            addKeyListener(this);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    requestFocus();
                }
            });
            initializeSnake();
            spawnFood();
            setupTimer();
            requestFocus();
        }
        
        private void initializeSnake() {
            snake = new ArrayList<>();
            // Create 3-segment snake facing right, starting near center
            snake.add(new int[]{8, 10});   // tail
            snake.add(new int[]{9, 10});   // middle
            snake.add(new int[]{10, 10});  // head
        }
        
        private void spawnFood() {
            int x, y;
            boolean validPosition;
            do {
                validPosition = true;
                x = random.nextInt(GRID_WIDTH);
                y = random.nextInt(GRID_HEIGHT);
                
                // Check if position is occupied by snake
                for (int[] segment : snake) {
                    if (segment[0] == x && segment[1] == y) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);
            
            food = new int[]{x, y};
        }
        
        private void setupTimer() {
            gameTimer = new Timer(150, e -> {
                if (!gameOver) {
                    moveSnake();
                }
            });
            gameTimer.start();
        }
        
        private void moveSnake() {
            // Only change direction if it's not a 180-degree turn
            if (!(nextDirX == -dirX && nextDirY == -dirY)) {
                dirX = nextDirX;
                dirY = nextDirY;
            }
            
            // Get the head position
            int[] head = snake.get(snake.size() - 1);
            
            // Calculate new head position
            int newHeadX = head[0] + dirX;
            int newHeadY = head[1] + dirY;
            
            // Check wall collision (no wrapping)
            if (newHeadX < 0 || newHeadX >= GRID_WIDTH || newHeadY < 0 || newHeadY >= GRID_HEIGHT) {
                gameOver = true;
                repaint();
                return;
            }
            
            // Check self collision
            for (int[] segment : snake) {
                if (segment[0] == newHeadX && segment[1] == newHeadY) {
                    gameOver = true;
                    repaint();
                    return;
                }
            }
            
            // Add new head
            snake.add(new int[]{newHeadX, newHeadY});
            
            // Check if head ate food
            if (newHeadX == food[0] && newHeadY == food[1]) {
                score += 10;
                spawnFood();
                // Don't remove tail - snake grows
            } else {
                // Remove tail to maintain length
                snake.remove(0);
            }
            
            repaint();
        }
        
        private void resetGame() {
            snake.clear();
            initializeSnake();
            dirX = 1;
            dirY = 0;
            nextDirX = 1;
            nextDirY = 0;
            score = 0;
            gameOver = false;
            spawnFood();
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw grid
            g.setColor(new Color(100, 100, 100));
            for (int i = 0; i <= GRID_WIDTH; i++) {
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
            }
            for (int i = 0; i <= GRID_HEIGHT; i++) {
                g.drawLine(0, i * CELL_SIZE, GRID_WIDTH * CELL_SIZE, i * CELL_SIZE);
            }
            
            // Draw snake
            g.setColor(Color.GREEN);
            for (int[] segment : snake) {
                g.fillRect(segment[0] * CELL_SIZE, segment[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            
            // Draw food
            g.setColor(Color.RED);
            g.fillRect(food[0] * CELL_SIZE, food[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(16f));
            g.drawString("Score: " + score, 10, 20);
            
            // Draw game over message
            if (gameOver) {
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(0, 0, GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
                
                g.setColor(Color.RED);
                g.setFont(g.getFont().deriveFont(36f));
                g.drawString("Game Over", 150, 250);
                
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(20f));
                g.drawString("Final Score: " + score, 160, 300);
                g.drawString("Press R to restart", 140, 340);
            }
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP) {
                nextDirX = 0;
                nextDirY = -1;
            } else if (key == KeyEvent.VK_DOWN) {
                nextDirX = 0;
                nextDirY = 1;
            } else if (key == KeyEvent.VK_LEFT) {
                nextDirX = -1;
                nextDirY = 0;
            } else if (key == KeyEvent.VK_RIGHT) {
                nextDirX = 1;
                nextDirY = 0;
            } else if (key == KeyEvent.VK_R) {
                resetGame();
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {}
        
        @Override
        public void keyTyped(KeyEvent e) {}
    }
}
