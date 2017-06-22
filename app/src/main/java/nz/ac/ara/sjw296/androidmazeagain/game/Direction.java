package nz.ac.ara.sjw296.androidmazeagain.game;

/**
 * Created by Sim on 22/06/2017.
 */

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    PASS(0, 0);

    public int colAdjust;
    public int rowAdjust;

    Direction(int row, int col) {
        colAdjust = col;
        rowAdjust = row;
    }
}
