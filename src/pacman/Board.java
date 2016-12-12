package pacman;

//necessary imports of libraries for the program
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

//extension of jpanel to create the game's frame, implementation of actionlistener for responding to events
public class Board extends JPanel implements ActionListener {

    //create a dimension for the frame and set the font of the score message at the bottom
    private Dimension dim = new Dimension(400, 400);
    private final Font smallfont = new Font("Comic-Sans", Font.BOLD, 14);

    //variables to store dot color, and maze color
    private final Color dotcolor = new Color(255, 255, 255);
    private final Color mazecolor = new Color(5, 5, 200);

    //booleans to determine when the game is over
    private boolean ingame = false;
    private boolean dying = false;

    //variables to determine the size of the level
    private final int blocksize = 24;
    private final int nrofblocks = 15;
    private final int scrsize = nrofblocks * blocksize;

    //variables relating to the movement speed in game
    private final int pacanimdelay = 2;
    private final int pacmananimcount = 4;
    private final int maxghosts = 4;
    private final int pacmanspeed = 6;

    //variables to help determine position in the maze for pacman and the ghosts
    private int pacanimcount = pacanimdelay;
    private int pacanimdir = 1;
    private int pacmananimpos = 0;
    private int nrofghosts = 4;
    private int pacsleft, score;
    private int[] dx, dy;
    private int[] ghostx, ghosty, ghostdx, ghostdy, ghostspeed;

    //variables to store the pictures of the intro, ghosts, fruits, and pacman as he moves around
    private Image intro;
    private Image cherry, strawberry, apple, orange, empty;
    private Image ghost1, ghost2, ghost3, ghost4;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    //coordinates of pacman as he moves
    private int pacmanx, pacmany, pacmandx, pacmandy;
    private int reqdx, reqdy, viewdx, viewdy;

    //track how many consecutive levels the user has played
    int levelCount = 1;

    //possible speeds for the ghosts
    private final int validspeeds[] = {1, 2, 3, 4, 5, 6, 7, 8};
    private final int maxspeed = 6;

    //speed given to ghosts so that they are increasingly fast and the variable to hold level array and a timer
    private int currentspeed = 1;
    private short[] screendata;
    private Timer timer;
    private int high;

    //boolean to determine if extreme mode was selected
    private boolean extremeMode = false;

    //boolean to determine if the fruit was collected
    private boolean fruit = false;

    //constructor for the board to be created and initialized
    public Board() {
        loadImages();
        initVariables();

        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.BLACK);
        setDoubleBuffered(true);
    }

    //initializes and sets variables for the arrays that will be used
    private void initVariables() {
        screendata = new short[nrofblocks * nrofblocks];

        ghostx = new int[maxghosts];
        ghostdx = new int[maxghosts];
        ghosty = new int[maxghosts];
        ghostdy = new int[maxghosts];
        ghostspeed = new int[maxghosts];

        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    //function for the listener to add a notify
    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }

    //pacman movement function checks how to animate him
    private void doAnim() {
        pacanimcount--;

        if (pacanimcount <= 0) {
            pacanimcount = pacanimdelay;
            pacmananimpos = pacmananimpos + pacanimdir;

            if (pacmananimpos == (pacmananimcount - 1) || pacmananimpos == 0) {
                pacanimdir = -pacanimdir;
            }
        }
    }

    //after all variables are initialzied, start menu is shown, and user presses 'Enter', the game method is called to check conditions
    //of the current play while calling the functions to move pacman + the ghosts
    private void playGame(Graphics2D g2d) throws FileNotFoundException, InterruptedException {
        if (dying) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    //function which creates the start screen
    private void showIntroScreen(Graphics2D g2d) throws FileNotFoundException {
        //set the color of the start screen using rgb
        g2d.setColor(new Color(0, 0, 0));
        //set the dimensions of the start screen
        g2d.fillRect(0, 0, scrsize - 1, scrsize + 32);
        //sets the border color and size
        g2d.setColor(Color.ORANGE);
        g2d.drawRect(1, 1, scrsize - 1, scrsize + 32);

        //strings to display on start screen and font settings
        String ent = "Press 'Enter' To Start The Game";
        String help = "To See The Help Page, Press 'H'";

        //access of file to get the current high score
        Scanner tFile = new Scanner(new FileReader("hi-score.txt"));
        high = tFile.nextInt();

        String highScore = "HIGH SCORE: " + high;

        Font small = new Font("Comic-Sans", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        //this draws the border box around the picture and instructions
        g2d.drawRect((scrsize - metr.stringWidth(ent) - 55) / 2, (scrsize / 3), 282, 101);
        g2d.drawRect((scrsize - metr.stringWidth(ent)) / 2 - 2, (scrsize / 6) - 20, 225, 50);

        //here the strings are drawn to the screen
        g2d.setColor(Color.WHITE);
        g2d.setFont(small);
        g2d.drawString(ent, (scrsize - metr.stringWidth(ent)) / 2, scrsize / 6);

        g2d.drawString(help, (scrsize - metr.stringWidth(ent)) / 2, (scrsize / 6) + 20);

        g2d.drawImage(intro, (scrsize - metr.stringWidth(ent) - 55) / 2, (scrsize / 3), this);

        Font large = new Font("Comic-Sans", Font.BOLD, 24);
        g2d.setFont(large);
        g2d.drawString(highScore, (scrsize - metr.stringWidth(ent)) / 2, 5 * (scrsize / 6));
    }

    //method to draw the score at the bottom of the screen
    private void drawScore(Graphics2D g2d) {
        int i;
        String s;

        g2d.setFont(smallfont);
        g2d.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g2d.drawString(s, scrsize / 2 + 96, scrsize + 16);

        for (i = 0; i < pacsleft; i++) {
            g2d.drawImage(pacman3left, i * 28 + 8, scrsize + 1, this);
        }
    }

    //method to check if the maze has been completed by the player
    private void checkMaze() {
        short i = 0;
        boolean finished = true;

        while (i < nrofblocks * nrofblocks && finished) {

            if ((screendata[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        //when a level is completed, award 50 bonus points, increment levelCount, and increase base speed for ghosts
        if (finished) {

            score += 50;

            if (currentspeed < maxspeed) {
                currentspeed++;
            }

            if (levelCount == 4) {
                levelCount = 1;
            } else {
                levelCount++;
            }

            fruit = false;

            initLevel();
        }
    }

    //function to handle the case of a lost life
    //checks that it was not last life, if so then checks for continue system
    //if no continue then it checks for high score and saves if new
    //USAGE OF DECORATOR PATTERN
    private void death() throws InterruptedException {
        pacsleft--;

        if (pacsleft == 0) {
            UIManager.put("OptionPane.minimumSize", new Dimension(200, 200));
            UIManager.put("OptionPane.messageFont", new Font("System", Font.PLAIN, 24));
            int choice = JOptionPane.showConfirmDialog(null, "   Would you like to continue?", "                      GAME OVER", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice == 0) {
                pacsleft = 3;
                Thread.sleep(1000);
            } else {
                if (score > high) {
                    String fileName = "hi-score.txt";
                    try {
                        FileWriter fileWriter = new FileWriter(fileName);
                        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                            bufferedWriter.write(String.valueOf(score));
                        }
                    } catch (IOException ex) {
                        System.out.println(
                                "Error writing to file '" + fileName + "'");
                    }

                }
                ingame = false;
            }
        }
        continueLevel();
    }

    //function to control how the ghosts move through the maze
    private void moveGhosts(Graphics2D g2d) {
        short i;
        int pos;
        int count;

        for (i = 0; i < nrofghosts; i++) {
            if (ghostx[i] % blocksize == 0 && ghosty[i] % blocksize == 0) {
                pos = ghostx[i] / blocksize + nrofblocks * (int) (ghosty[i] / blocksize);

                count = 0;

                if ((screendata[pos] & 1) == 0 && ghostdx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screendata[pos] & 2) == 0 && ghostdy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screendata[pos] & 4) == 0 && ghostdx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screendata[pos] & 8) == 0 && ghostdy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screendata[pos] & 15) == 15) {
                        ghostdx[i] = 0;
                        ghostdy[i] = 0;
                    } else {
                        ghostdx[i] = -ghostdx[i];
                        ghostdy[i] = -ghostdy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghostdx[i] = dx[count];
                    ghostdy[i] = dy[count];
                }

            }

            ghostx[i] = ghostx[i] + (ghostdx[i] * ghostspeed[i]);
            ghosty[i] = ghosty[i] + (ghostdy[i] * ghostspeed[i]);
            drawGhost(g2d, ghostx[i] + 1, ghosty[i] + 1, i);

            if (pacmanx > (ghostx[i] - 12) && pacmanx < (ghostx[i] + 12)
                    && pacmany > (ghosty[i] - 12) && pacmany < (ghosty[i] + 12)
                    && ingame) {

                dying = true;
            }
        }
    }

    //function to draw the ghosts with different colors
    private void drawGhost(Graphics2D g2d, int x, int y, int i) {
        switch (i) {
            case 0:
                g2d.drawImage(ghost1, x, y, this);
                break;
            case 1:
                g2d.drawImage(ghost2, x, y, this);
                break;
            case 2:
                g2d.drawImage(ghost3, x, y, this);
                break;
            default:
                g2d.drawImage(ghost4, x, y, this);
                break;
        }
    }

    //function to move pacman
    //also checks if he collected a dot or fruit in the maze
    private void movePacman() {
        int pos;
        short ch;

        if (reqdx == -pacmandx && reqdy == -pacmandy) {
            pacmandx = reqdx;
            pacmandy = reqdy;
            viewdx = pacmandx;
            viewdy = pacmandy;
        }

        if (pacmanx % blocksize == 0 && pacmany % blocksize == 0) {
            pos = pacmanx / blocksize + nrofblocks * (int) (pacmany / blocksize);
            ch = screendata[pos];

            if ((ch & 16) != 0) {
                screendata[pos] = (short) (ch & 15);
                score++;
            }

            if (pos == 22) {
                if (!fruit) {
                    score += 25 * levelCount;
                }
                fruit = true;
            }

            //gives the player an extra life if they reach 500 score
            if (score == 500) {
                pacsleft++;
            }

            if (reqdx != 0 || reqdy != 0) {
                if (!((reqdx == -1 && reqdy == 0 && (ch & 1) != 0)
                        || (reqdx == 1 && reqdy == 0 && (ch & 4) != 0)
                        || (reqdx == 0 && reqdy == -1 && (ch & 2) != 0)
                        || (reqdx == 0 && reqdy == 1 && (ch & 8) != 0))) {
                    pacmandx = reqdx;
                    pacmandy = reqdy;
                    viewdx = pacmandx;
                    viewdy = pacmandy;
                }
            }

            // Check for standstill
            if ((pacmandx == -1 && pacmandy == 0 && (ch & 1) != 0)
                    || (pacmandx == 1 && pacmandy == 0 && (ch & 4) != 0)
                    || (pacmandx == 0 && pacmandy == -1 && (ch & 2) != 0)
                    || (pacmandx == 0 && pacmandy == 1 && (ch & 8) != 0)) {
                pacmandx = 0;
                pacmandy = 0;
            }
        }
        pacmanx = pacmanx + pacmanspeed * pacmandx;
        pacmany = pacmany + pacmanspeed * pacmandy;
    }

    //start of all the used functions to draw pacman
    //this is the main function which calls the others based on current orientation
    private void drawPacman(Graphics2D g2d) {
        if (fruit) {
            g2d.drawImage(empty, 7 * blocksize, 1 * blocksize, this);
        }

        if (viewdx == -1) {
            drawPacmanLeft(g2d);
        } else if (viewdx == 1) {
            drawPacmanRight(g2d);
        } else if (viewdy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    //handles pacman moving upwards
    private void drawPacmanUp(Graphics2D g2d) {
        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(pacman2up, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    //handles pacman moving downwards
    private void drawPacmanDown(Graphics2D g2d) {
        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(pacman2down, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    //handles pacman moving leftwards
    private void drawPacmanLeft(Graphics2D g2d) {
        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(pacman2left, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    //handles pacman moving rightwards
    private void drawPacmanRight(Graphics2D g2d) {
        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(pacman2right, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }//end of functions to draw pacman

    //function to draw the maze walls, dots, and the fruit based on current level number
    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;

        //determine which fruit to display
        switch (levelCount) {
            case 1:
                g2d.drawImage(cherry, 7 * blocksize, 1 * blocksize, this);
                break;
            case 2:
                g2d.drawImage(strawberry, 7 * blocksize, 1 * blocksize, this);
                break;
            case 3:
                g2d.drawImage(apple, 7 * blocksize, 1 * blocksize, this);
                break;
            default:
                g2d.drawImage(orange, 7 * blocksize, 1 * blocksize, this);
                break;
        }

        //loop through the whole size of the maze drawing appropriate line for each block
        for (y = 0; y < scrsize; y += blocksize) {
            for (x = 0; x < scrsize; x += blocksize) {

                g2d.setColor(mazecolor);
                g2d.setStroke(new BasicStroke(2));

                if ((screendata[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + blocksize - 1);
                }

                if ((screendata[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + blocksize - 1, y);
                }

                if ((screendata[i] & 4) != 0) {
                    g2d.drawLine(x + blocksize - 1, y, x + blocksize - 1,
                            y + blocksize - 1);
                }

                if ((screendata[i] & 8) != 0) {
                    g2d.drawLine(x, y + blocksize - 1, x + blocksize - 1,
                            y + blocksize - 1);
                }

                if ((screendata[i] & 16) != 0) {
                    g2d.setColor(dotcolor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    //initializes the starting variables of the game so that you always start with 3 lives, 0 score, and have 4 ghosts
    private void initGame() {
        pacsleft = 3;
        score = 0;
        initLevel();
        nrofghosts = 4;
        currentspeed = 1;
    }

    //the level data is retrieved from the outside class maze
    private void initLevel() {
        //retrieve leveldata from maze object that helps cut clutter from original source code
        Maze leveldata = new Maze();
        short[] temp = leveldata.getLevelData();
        for (int i = 0; i < nrofblocks * nrofblocks; i++) {
            screendata[i] = temp[i];
        }

        continueLevel();
    }

    //function to continue the game
    private void continueLevel() {
        short i;
        int dx = 1;
        int random;

        for (i = 0; i < nrofghosts; i++) {

            //gives the starting position of ghosts in the maze
            ghosty[i] = 4 * blocksize;
            ghostx[i] = 4 * blocksize;
            ghostdy[i] = 0;
            ghostdx[i] = dx;
            dx = -dx;

            //set their speed to max if extreme mode was selected otherwise make them randomly, progressively faster
            if (extremeMode) {
                ghostspeed[i] = validspeeds[7];
            } else {
                random = (int) (Math.random() * (currentspeed + 1));

                //progressively make the ghosts faster so that they are unique
                if (random > currentspeed) {
                    random = currentspeed;
                }

                ghostspeed[i] = validspeeds[random];
            }
        }

        //gives the starting position of pacman in the maze and default movements
        pacmanx = 7 * blocksize;
        pacmany = 11 * blocksize;
        pacmandx = 0;
        pacmandy = 0;
        reqdx = 0;
        reqdy = 0;
        viewdx = -1;
        viewdy = 0;
        dying = false;
    }

    //function where the intro, ghosts, fruit, and pacman images are loaded and set for drawing purposes
    private void loadImages() {
        //home screen design
        intro = new ImageIcon("images/Intro.gif").getImage();

        //each colored ghost
        ghost1 = new ImageIcon("images/Ghost1.gif").getImage();
        ghost2 = new ImageIcon("images/Ghost2.gif").getImage();
        ghost3 = new ImageIcon("images/Ghost3.gif").getImage();
        ghost4 = new ImageIcon("images/Ghost4.gif").getImage();

        //the fruits and blank spot
        cherry = new ImageIcon("images/cherry.gif").getImage();
        strawberry = new ImageIcon("images/strawberry.gif").getImage();
        apple = new ImageIcon("images/apple.gif").getImage();
        orange = new ImageIcon("images/orange.gif").getImage();
        empty = new ImageIcon("images/empty.gif").getImage();

        //the pacman positions
        pacman1 = new ImageIcon("images/PacMan1.gif").getImage();
        pacman2up = new ImageIcon("images/PacMan2up.gif").getImage();
        pacman3up = new ImageIcon("images/PacMan3up.gif").getImage();
        pacman4up = new ImageIcon("images/PacMan4up.gif").getImage();
        pacman2down = new ImageIcon("images/PacMan2down.gif").getImage();
        pacman3down = new ImageIcon("images/PacMan3down.gif").getImage();
        pacman4down = new ImageIcon("images/PacMan4down.gif").getImage();
        pacman2left = new ImageIcon("images/PacMan2left.gif").getImage();
        pacman3left = new ImageIcon("images/PacMan3left.gif").getImage();
        pacman4left = new ImageIcon("images/PacMan4left.gif").getImage();
        pacman2right = new ImageIcon("images/PacMan2right.gif").getImage();
        pacman3right = new ImageIcon("images/PacMan3right.gif").getImage();
        pacman4right = new ImageIcon("images/PacMan4right.gif").getImage();
    }

    //first instance of the graphics g, here the super method is called and then passed to dodrawing method
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            doDrawing(g);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //function where the graphics g is converted to a 2D image and then passed to the necessary methods
    private void doDrawing(Graphics g) throws FileNotFoundException, InterruptedException {

        //conversion of the graphics object
        Graphics2D g2d = (Graphics2D) g;

        //set the background color of the board to black and fill the set dimension
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, dim.width, dim.height);

        //call the functions to draw the maze, the score, and animate pacman
        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        //show either the start menu or the game maze
        if (ingame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        //clear the map
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    //class that houses the methods to handle changes performed by the keyboard
    //USAGE OF MVC PATTERN THIS IS THE CONTROLLER, IMAGES + ARRAYS + SCORE PROVIDE MODEL, JFRAME SHOWN IS THE VIEW
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            //if the game is in session, change position based on the key pressed:LEAVE
            if (ingame) {
                //arrow keys correspond to shifting pacman's coordinates
                if (key == KeyEvent.VK_LEFT) {
                    reqdx = -1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    reqdx = 1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    reqdx = 0;
                    reqdy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    reqdx = 0;
                    reqdy = 1;
                } else if (key == KeyEvent.VK_0 && timer.isRunning()) {
                    //can end the game by pressing 0
                    timer.stop();
                    //show the restart confirmation
                    int choice = JOptionPane.showConfirmDialog(null, "Are you sure you wish to restart the game?",
                            "                  RESTART", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (choice == 0) {
                        timer.start();
                        fruit = false;
                        ingame = false;
                    } else {
                        timer.start();
                    }
                } else if (key == KeyEvent.VK_SPACE) {
                    //can pause the game by pressing spacebar
                    if (timer.isRunning()) {
                        timer.stop();
                        //shows the pause menu
                        JOptionPane.showMessageDialog(null, "                                PAUSED", "                   PAUSED", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        timer.start();
                    }
                }
            } else if (!ingame) {
                if (key == KeyEvent.VK_ENTER) {
                    //start the regular game by pressing the enter key
                    extremeMode = false;
                    ingame = true;
                    initGame();
                } else if (key == KeyEvent.VK_E) {
                    //start extreme mode through e key
                    extremeMode = true;
                    ingame = true;
                    initGame();
                } else if (key == KeyEvent.VK_H) {
                    //display of the help menu with various statements
                    JOptionPane.showConfirmDialog(null, "The objective of the game is simple: collect all dots in the maze.\n"
                            + "Use the arrow keys to move Pac-Man in the corresponding direction.\n"
                            + "Press the spacebar while in game to pause and unpause play.\n"
                            + "Press the 0 key to end the game and return to the menu.\n"
                            + "Collect fruit for extra points and aim for the high score!\n"
                            + "Don't fret over losing, there is a continue system upon death.\n"
                            + "For a real challenge, press 'E' on the menu for EXTREME MODE!\n",
                            "                          INSTRUCTIONS", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
            }
        }

        //overridden method to set the movement to zero if a key is no longer being pressed
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                reqdx = 0;
                reqdy = 0;
            }
        }
    }

    //overridden method to repaint the screen as dots are eaten and the character moves
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
