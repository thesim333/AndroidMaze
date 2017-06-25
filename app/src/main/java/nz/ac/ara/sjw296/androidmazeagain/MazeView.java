package nz.ac.ara.sjw296.androidmazeagain;

import nz.ac.ara.sjw296.androidmazeagain.communal.Point;

/**
 * @author Simon Winder
 */

interface MazeView {
    void newGameSetup(int rows, int cols);

    void addLeftWall(Point p);

    void addTopWall(Point p);

    void setTheseusPosition(Point p);

    void setTheseusMood(Mood mTheseusMood);

    void setMinotaur(Point p);

    void invalidate();
}
