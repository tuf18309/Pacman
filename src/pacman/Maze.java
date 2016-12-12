package pacman;

//import to help randomly assign a maze
import java.util.Random;

public class Maze {

    //level data for the three levels
    //the original source code used hard coded values, here I replaced them all with constants that have easy to understand
    //names: E=empty, L=left, T=top, R=right, B=bottom, W=wall, D=dot, C=corner
    //combininations of the letters creates the specific wall/dot combination available
    //o is nothing, 1 is left, 2 is top, 4 is right, 8 is bottom, 16 is dot, adding them combines the properties
    private static final int E = 0, LW = 1, TW = 2, RW = 4, BW = 8, TLC = 3, TRC = 6, BLC = 9, BRC = 12, D = 16,
            LWD = 17, TWD = 18, RWD = 20, BWD = 24, TLCD = 19, TRCD = 22, BLCD = 25, BRCD = 28,
            LRWD = 21, TBWD = 26;

    //adjusted base level so that there does not exist any holes that can crash the game
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

    //custom level with four squares and a center cross
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

    //bounded on the outsides empty level to construct more levels from
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

    public Maze() {
        //empty constructor
    }

    //function which returns a random maze from the levels
    public short[] getLevelData() {

        Random rand = new Random();
        double choice = rand.nextDouble();
        if (choice <= 0.33) {
            return leveldata1;
        } else if (choice > 0.33 && choice <= 0.66) {
            return leveldata2;
        } else {
            return leveldata3;
        }
    }
}
