package nz.ac.ara.sjw296.androidmazeagain.solver;

import java.util.Stack;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.game.Direction;

/**
 * Created by Sim on 23/06/2017.
 */

public class GameMove implements Move {
    protected Point theseusStart;
    protected Point minotaurStart;
    protected Stack<Direction> couldBe;
    protected Direction moveDirectionIs;

    public GameMove(Point theseus, Point minotaur) {
        this.theseusStart = new MazePoint(theseus);
        this.minotaurStart = new MazePoint(minotaur);
        this.couldBe = new Stack<>();
        this.couldBe.add(Direction.PASS);
    }

    @Override
    public void addPossibleMove(Direction direction) {
        this.couldBe.push(direction);
    }

    @Override
    public boolean nextDirection() {
        if (this.couldBe.isEmpty()) {
            return false;
        }
        else {
            this.moveDirectionIs = this.couldBe.pop();
            return true;
        }
    }

    @Override
    public Point getTheseusStart() {
        return this.theseusStart;
    }

    @Override
    public Point getMinotaurStart() {
        return this.minotaurStart;
    }

    @Override
    public Direction getDirection() {
        return this.moveDirectionIs;
    }
}
