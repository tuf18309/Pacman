package pacman;

//necessary imports for the program
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

public class Board extends JPanel implements ActionListener {

    //create a dimension and set the font of the score message at the bottom
    private Dimension dim;
    private final Font smallfont = new Font("Comic-Sans", Font.BOLD, 14);

    //variables to store image, dot color, and maze color
    private Image ii;
    private final Color dotcolor = new Color(255, 255, 255);
    private Color mazecolor;

    //booleans to determine when the game is over
    private boolean ingame = false;
    private boolean dying = false;

    //variables to determine the size of the level
    private final int blocksize = BWD;
    private final int nrofblocks = 15;
    private final int scrsize = nrofblocks * blocksize;

    //variables relating to the movement speed in game
    private final int pacanimdelay = 2;
    private final int pacmananimcount = 4;
    private final int maxghosts = 4;
    private final int pacmanspeed = 6;

    //variables to help determine position in the maze
    private int pacanimcount = pacanimdelay;
    private int pacanimdir = 1;
    private int pacmananimpos = 0;
    private int nrofghosts = 4;
    private int pacsleft, score;
    private int[] dx, dy;
    private int[] ghostx, ghosty, ghostdx, ghostdy, ghostspeed;

    //variables to store the pictures of the ghosts and of pacman as he moves around
    //TODO add in option for MRS PACMAN - maybe special ghost for extreme mode
    private Image intro;
    private Image ghost1, ghost2, ghost3, ghost4;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    //coordinates of pacman
    private int pacmanx, pacmany, pacmandx, pacmandy;
    private int reqdx, reqdy, viewdx, viewdy;

    //level data for the three levels:TODO place into class
    //TODO:comment explaining each of the abbreviations
    //1 is left, 2 is top, 4 is right, 8 is bottom, 16 is dot, adding them combines the properties
    private static final int E = 0, LW = 1, TW = 2, RW = 4, BW = 8, TLC = 3, TRC = 6, BLC = 9, BRC = 12, D = 16,
            LWD = 17, TWD = 18, RWD = 20, BWD = 24, TLCD = 19, TRCD = 22, BLCD = 25, BRCD = 28,
            LRWD = 21, TBWD = 26;

    private final short leveldata1[] = {
        TLCD, TBWD, TBWD, TBWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TRCD,
        LRWD, E, E, E, LWD, D, D, D, D, D, D, D, D, D, RWD,
        LRWD, E, E, E, LWD, D, D, D, D, D, D, D, D, D, RWD,
        LRWD, E, E, E, LWD, D, D, BWD, D, D, D, D, D, D, RWD,
        LWD, TWD, TWD, TWD, D, D, RWD, E, LWD, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, RWD, E, LWD, D, D, D, D, BWD, RWD,
        BLCD, D, D, D, BWD, BWD, BRCD, E, BLCD, BWD, BWD, D, RWD, E, LRWD,
        LW, LWD, D, RWD, E, E, E, E, E, E, E, LWD, RWD, E, LRWD,
        LW, LWD, D, D, TWD, TWD, TRCD, E, TLCD, TWD, TWD, D, RWD, E, LRWD,
        LW, LWD, D, D, D, D, RWD, E, LWD, D, D, D, RWD, E, LRWD,
        LW, LWD, D, D, D, D, RWD, E, LWD, D, D, D, RWD, E, LRWD,
        LW, LWD, D, D, D, D, D, TWD, D, D, D, D, RWD, E, LRWD,
        LW, LWD, D, D, D, D, D, D, D, D, D, D, RWD, E, LRWD,
        LW, BLCD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, D, D, D, TWD, RWD,
        BLC, BW, BW, BW, BW, BW, BW, BW, BW, BW, BLCD, BWD, BWD, BWD, BRCD
    };

    private final short leveldata2[] = {
        TLCD, TBWD, TBWD, TBWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TBWD, TBWD, TBWD, TRCD,
        LRWD, E, E, E, LWD, D, D, D, D, D, RWD, E, E, E, LRWD,
        LRWD, E, E, E, LWD, D, D, D, D, D, RWD, E, E, E, LRWD,
        LRWD, E, E, E, LWD, D, D, D, D, D, RWD, E, E, E, LRWD,
        LWD, TWD, TWD, TWD, D, D, D, D, D, D, D, TWD, TWD, TWD, RWD,
        LWD, D, D, D, D, D, D, BWD, BWD, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, BRCD, E, E, BLCD, D, D, D, D, RWD,
        LWD, D, D, D, D, RWD, E, E, E, E, LWD, D, D, D, RWD,
        LWD, D, D, D, D, RWD, E, E, E, E, LWD, D, D, D, RWD,
        LWD, D, D, D, D, D, TRCD, E, E, TLCD, D, D, D, D, RWD,
        LWD, BWD, BWD, BWD, D, D, D, TWD, TWD, D, D, BWD, BWD, BWD, RWD,
        LRWD, E, E, E, LWD, D, D, D, D, D, RWD, E, E, E, LRWD,
        LRWD, E, E, E, LWD, D, D, D, D, D, RWD, E, E, E, LRWD,
        LRWD, E, E, E, LWD, D, D, D, D, D, RWD, E, E, E, LRWD,
        BLCD, TBWD, TBWD, TBWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, TBWD, TBWD, TBWD, BRCD
    };

    private final short leveldata3[] = {
        TLCD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TWD, TRCD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        LWD, D, D, D, D, D, D, D, D, D, D, D, D, D, RWD,
        BLCD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BWD, BRCD
    };

    int levelCount = 1;

    //possible speeds for the ghosts
    private final int validspeeds[] = {1, 2, 3, 4, 5, 6, 7, 8};
    private final int maxspeed = 6;

    //speed given to ghosts so that they are increasingly fast and the variable to hold level array and a timer
    private int currentspeed = 1;
    private short[] screendata;
    private Timer timer;
    private int high;

    //boolean for continue the game
    private boolean cont, wait;

    //constructor for the board to be created and initialized
    public Board() {

        loadImages();
        initVariables();

        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.BLACK);
        setDoubleBuffered(true);
    }

    //initializes and sets variables
    private void initVariables() {

        screendata = new short[nrofblocks * nrofblocks];
        //now color is blue
        mazecolor = new Color(5, 5, 200);
        dim = new Dimension(400, 400);

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

    //pacman movement function:LEAVE
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

    //
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

    //TODO:function which creates the start screen:TODO finish custom start screen by changing displayed strings
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

        Scanner tFile = new Scanner(new FileReader("hi-score.txt"));
        high = tFile.nextInt();

        String highScore = "HIGH SCORE: " + high;

        Font small = new Font("Comic-Sans", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        //this is where the string is created and put on the screen:TODO replace instructions with image
        //put instructions into new method screen
        g2d.setColor(Color.WHITE);
        g2d.setFont(small);
        g2d.drawString(ent, (scrsize - metr.stringWidth(ent)) / 2, scrsize / 6);

        g2d.drawString(help, (scrsize - metr.stringWidth(ent)) / 2, (scrsize / 6) + 20);
        
        g2d.drawImage(intro, (scrsize - metr.stringWidth(ent) - 55) / 2, (scrsize / 3), this);
        
        Font large = new Font("Comic-Sans", Font.BOLD, 24);
        g2d.setFont(large);
        g2d.drawString(highScore, (scrsize - metr.stringWidth(ent)) / 2, 5 * (scrsize / 6));
    }

    private void showHelpScreen(Graphics2D g2d) {

    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallfont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, scrsize / 2 + 96, scrsize + 16);

        for (i = 0; i < pacsleft; i++) {
            g.drawImage(pacman3left, i * 28 + 8, scrsize + 1, this);
        }
    }

    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < nrofblocks * nrofblocks && finished) {

            if ((screendata[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (nrofghosts < maxghosts) {
                nrofghosts++;
            }

            if (currentspeed < maxspeed) {
                currentspeed++;
            }

            if (levelCount == 3) {
                levelCount = 1;
            } else {
                levelCount++;
            }
            initLevel();
        }
    }

    //TODO:function to be modified for the continue system
    private void death() throws InterruptedException {

        pacsleft--;

        if (pacsleft == 0) {
            UIManager.put("OptionPane.minimumSize", new Dimension(359, 408));
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

    //function to control how the ghosts move
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

    //function to draw the ghosts
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

    //function to move pacman:LEAVE
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

            if ((ch & D) != 0) {
                screendata[pos] = (short) (ch & 15);
                score++;
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

    //functions to draw pacman:TODO possibly add in option for MRS PACMAN using this
    private void drawPacman(Graphics2D g2d) {

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
    }//end of functions to draw pacman:LEAVE

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

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

                if ((screendata[i] & D) != 0) {
                    g2d.setColor(dotcolor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {
        pacsleft = 3;
        score = 0;
        initLevel();
        nrofghosts = 4;
        currentspeed = 1;
    }

    private void initLevel() {

        //TODO: modify code to retrieve leveldata from object of level
        int i;
        switch (levelCount) {
            case 1:
                for (i = 0; i < nrofblocks * nrofblocks; i++) {
                    screendata[i] = leveldata1[i];
                }   break;
            case 2:
                for (i = 0; i < nrofblocks * nrofblocks; i++) {
                    screendata[i] = leveldata2[i];
                }   break;
            default:
                for (i = 0; i < nrofblocks * nrofblocks; i++) {
                    screendata[i] = leveldata3[i];
                }   break;
        }
        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < nrofghosts; i++) {

            ghosty[i] = 4 * blocksize;
            ghostx[i] = 4 * blocksize;
            ghostdy[i] = 0;
            ghostdx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentspeed + 1));

            if (random > currentspeed) {
                random = currentspeed;
            }

            ghostspeed[i] = validspeeds[random];
        }

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

    //function where the ghosts and pacman images are loaded and set for drawing purposes
    private void loadImages() {
        intro = new ImageIcon("images/Intro.gif").getImage();
        ghost1 = new ImageIcon("images/Ghost1.gif").getImage();
        ghost2 = new ImageIcon("images/Ghost2.gif").getImage();
        ghost3 = new ImageIcon("images/Ghost3.gif").getImage();
        ghost4 = new ImageIcon("images/Ghost4.gif").getImage();
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

    //first instance of the graphics g, here the super method is called and then passed to dodrawing method:LEAVE
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

    //function where the graphics g is converted to a 2D image and then passed to the necessary methods:TODO pause screen
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

        //TODO create boolean and use it to determine whether in the help screen or not
        if (ingame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        //LEAVE
        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    //class that houses the methods to handle changes performed by the keyboard:LEAVE
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            //if the game is in session, change position based on the key pressed:LEAVE
            if (ingame) {
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
                } else if (key == KeyEvent.VK_Y) {
                    cont = true;
                } else if (key == KeyEvent.VK_N) {
                    cont = false;
                } else if (key == KeyEvent.VK_0 && timer.isRunning()) {
                    //can end the game by pressing 0:TODO write 'are you sure message?'
                    ingame = false;
                } else if (key == KeyEvent.VK_SPACE) {
                    //can pause the game by pressing spacebar:TODO add pause screen
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else if (!ingame) {
                if (key == KeyEvent.VK_ENTER) {
                    //start the game by pressing the enter key:LEAVE
                    ingame = true;
                    initGame();
                } else if (key == KeyEvent.VK_H) {
                    //write function to display instruction screen and bring other statements in there:TODO
                    //System.out.println("Help Screen Activated");
                    JOptionPane.showConfirmDialog(null, "The objective of the game is simple: collect all dots in the maze\n"
                            + "Use the arrow keys to move Pac-Man in the corresponding direction.\n"
                            + "Press the spacebar while in game to pause and unpause play.\n"
                            + "Press the 0 key to end the game and return to the menu.\n"
                            ,"                      INSTRUCTIONS", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
            }
        }

        //overridden method to set the movement to zero if a key is no longer being pressed:LEAVE
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

    //overridden method to repaint the screen as dots are eaten and the character moves:LEAVE
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
