package nz.ac.ara.sjw296.androidmazeagain.communal;

import java.util.Locale;

/**
 * Created by Sim on 22/06/2017.
 */

public class MazePoint implements Point {
    protected int row;
    protected int col;

    public MazePoint(Point p) {
        this.row = p.getRow();
        this.col = p.getCol();
    }

    public MazePoint(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public boolean equals(Point p) {
        return (this.row == p.getRow() && this.col == p.getCol());
    }

    public void setLocation(Point p) {
        this.row = p.getRow();
        this.col = p.getCol();
    }

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void translate(int row, int col) {
        this.row += row;
        this.col += col;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%d, %d", row, col);
    }
}
