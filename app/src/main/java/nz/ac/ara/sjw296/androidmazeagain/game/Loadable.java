package nz.ac.ara.sjw296.androidmazeagain.game;

import nz.ac.ara.sjw296.androidmazeagain.communal.Point;

/**
 * Created by Sim on 22/06/2017.
 */

public interface Loadable {
    void setDepthDown(int depthDown);

    void setWidthAcross(int widthAcross);

    void addWallAbove(Point where);

    void addWallLeft(Point where);

    void addTheseus(Point where);

    void addMinotaur(Point where);

    void addExit(Point where);

    void setName(String name);

    void setMoveCount(int moveCount);
}
