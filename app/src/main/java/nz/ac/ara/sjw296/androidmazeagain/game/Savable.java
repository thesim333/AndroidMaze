package nz.ac.ara.sjw296.androidmazeagain.game;

import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.communal.Wall;

/**
 * Created by Sim on 22/06/2017.
 */

public interface Savable {
    int getWidthAcross();

    int getDepthDown();

    Wall whatsAbove(Point where);

    Wall whatsLeft(Point where);

    Point wheresTheseus();

    Point wheresMinotaur();

    Point wheresExit();

    String getLevelName();

    int getMoveCount();
}
