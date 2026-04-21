import javax.swing.JFrame;
import javax.swing.JPanel;

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
    
    static class GamePanel extends JPanel {
        // Game panel placeholder
    }
}
