package nz.ac.ara.sjw296.androidmazeagain;

import nz.ac.ara.sjw296.androidmazeagain.communal.Point;

/**
 * Created by Sim on 22/06/2017.
 */

public interface MazeView {
    void newGameSetup(int rows, int cols);

    void addLeftWall(Point p);

    void addTopWall(Point p);

    void setTheseusPosition(Point p);

    void setTheseusMood(Mood mTheseusMood);

    void setMinotaur(Point p);

    void invalidate();
}
