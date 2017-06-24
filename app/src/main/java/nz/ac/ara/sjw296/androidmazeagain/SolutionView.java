package nz.ac.ara.sjw296.androidmazeagain;

import java.util.List;

import nz.ac.ara.sjw296.androidmazeagain.game.Direction;

/**
 * Created by Sim on 23/06/2017.
 */

public interface SolutionView {
    void setSolution(List<Direction> solution);

    boolean haveSolution();

    Direction getNextMove();

    void popAndDraw();

    void invalidate();

    void stopShowingSolution();
}
