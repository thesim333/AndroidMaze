package nz.ac.ara.sjw296.androidmazeagain.solver;

import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.game.Direction;

/**
 * Created by Sim on 23/06/2017.
 */

public interface Move {
    void addPossibleMove(Direction direction);

    boolean nextDirection();

    Direction getDirection();

    Point getTheseusStart();

    Point getMinotaurStart();
}
