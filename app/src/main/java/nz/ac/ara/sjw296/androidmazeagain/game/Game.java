package nz.ac.ara.sjw296.androidmazeagain.game;

import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.communal.Wall;

/**
 * Created by Sim on 22/06/2017.
 */

public interface Game {
    boolean moveTheseus(Direction direction);

    void moveMinotaur();

    boolean isWon();

    boolean isLost();

    int getMoveCount();

    String getLevelName();

    Point wheresTheseus();

    Point wheresMinotaur();

    Point wheresExit();

    Wall whatsAbove(Point where);

    Wall whatsLeft(Point where);

    int getDepthDown();

    int getWidthAcross();
}
