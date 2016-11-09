package pacman;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Pacman extends JFrame {

    //constructor to create the pacman object and call a function to initialize important variables:LEAVE
    public Pacman() {
        initUI();
    }

    //function to initialize the board by adding a new board object, setting the title, and setting the size:LEAVE
    private void initUI() {
        add(new Board());
        this.setTitle("Ed's Pac-Man");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(383, 450);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    //main method where pacman object is created and made visible:LEAVE
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Pacman ex = new Pacman();
        });
    }
}