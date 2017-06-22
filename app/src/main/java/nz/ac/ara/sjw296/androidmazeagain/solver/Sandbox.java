package nz.ac.ara.sjw296.androidmazeagain.solver;

import java.util.List;

import nz.ac.ara.sjw296.androidmazeagain.game.Direction;
import nz.ac.ara.sjw296.androidmazeagain.game.Savable;

/**
 * Created by Sim on 23/06/2017.
 */

public interface Sandbox {
    void begin();

    boolean isSolved();

    List<Direction> getSolution();

    void createGameState(Savable game);
}
