package nz.ac.ara.sjw296.androidmazeagain.communal;

/**
 * Created by Sim on 21/05/2017.
 */

public interface Point {
    boolean equals(Point p);

    void setLocation(Point p);

    void setLocation(int row, int col);

    void translate(int row, int col);

    int getRow();

    int getCol();

    String toString();
}