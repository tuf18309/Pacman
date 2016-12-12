package pacman;

//imports of necessary libraries
import java.awt.EventQueue;
import javax.swing.JFrame;

//extension of jframe allows main to create the board and set appearance of the game frame
public class Pacman extends JFrame {

    //constructor to create the pacman object and call a function to initialize important variables
    public Pacman() {
        initUI();
    }

    //function to initialize the board by adding a new board object, setting the title, and setting the size
    //USAGE OF SINGLETON DESIGN PATTERN
    private void initUI() {
        add(new Board());
        this.setTitle("Ed's Pac-Man");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(383, 450);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    //main method where pacman object is created and made visible
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Pacman ex = new Pacman();
        });
    }
}
