package nz.ac.ara.sjw296.androidmazeagain.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.communal.Wall;
import nz.ac.ara.sjw296.androidmazeagain.game.Direction;
import nz.ac.ara.sjw296.androidmazeagain.game.MazeGame;
import nz.ac.ara.sjw296.androidmazeagain.game.Savable;

/**
 * Created by Sim on 23/06/2017.
 */

public class SandboxGame extends MazeGame implements Sandbox {
    protected Stack<Move> solution = new Stack<>();

    @Override
    public void createGameState(Savable game) {
        this.setDepthDown(game.getDepthDown());
        this.setWidthAcross(game.getWidthAcross());
        this.installWalls(game);
        this.addTheseus(game.wheresTheseus());
        this.addMinotaur(game.wheresMinotaur());
        this.addExit(game.wheresExit());
    }

    protected void installWalls(Savable game) {
        for (int r = 0; r < this.depth; r++) {
            for (int c = 0; c < width; c++) {
                Point p = new MazePoint(r, c);
                if (game.whatsAbove(p) == Wall.SOMETHING) {
                    this.addWallAbove(p);
                }
                if (game.whatsLeft(p) == Wall.SOMETHING) {
                    this.addWallLeft(p);
                }
            }
        }
    }

    @Override
    public void begin() {
        //Try to move in direction of exit, then try to continue
        boolean canAddNewMove = true;
        while (!this.isWon()) {
            if (canAddNewMove) {
                this.setMove();
            }
            canAddNewMove = true;
            if (!this.makeMoves()) {
                if (!revertMove()) {
                    break;
                }
                canAddNewMove = false;
            }
            //check moves aren't repeating
            //pause and minotaur didn't move
            if (!this.pauseWasLegit() || this.gameStateHasRepeated()) {
                if (!this.revertMove()) {
                    break;
                }
                canAddNewMove = false;
            }
        }
    }

    protected boolean pauseWasLegit() {
        //full game turn no movement
        return !(this.solution.peek().getDirection() == Direction.PASS &&
                this.solution.peek().getMinotaurStart().equals(this.minotaur));
    }

    protected boolean gameStateHasRepeated() {
        for (int i = this.solution.size() - 2; i >= 0; i--) {
            Move thisMove = this.solution.elementAt(i);
            if (this.gameStateIsSame(thisMove.getTheseusStart(), thisMove.getMinotaurStart())) {
                return true;
            }
        }
        return false;
    }

    protected boolean gameStateIsSame(Point theseus, Point minotaur) {
        return (this.theseus.equals(theseus) && this.minotaur.equals(minotaur));
    }

    @Override
    public boolean isSolved() {
        return (!this.solution.isEmpty());
    }

    @Override
    public List<Direction> getSolution() {
        List<Direction> theSolution = new ArrayList<>();
        for (Move theMove:
                this.solution) {
            theSolution.add(theMove.getDirection());
        }
        return theSolution;
    }

    protected void setMove() {
        Move theMove = new GameMove(this.theseus, this.minotaur);
        List<Direction> posDirections = this.getDirectionsForMove();

        for (Direction thisDirection:
                posDirections) {
            theMove.addPossibleMove(thisDirection);
        }
        theMove.nextDirection();
        this.solution.push(theMove);
    }

    protected List<Direction> getDirectionsForMove() {
        List<Direction> returnDirections = new ArrayList<>();
        Direction[] theDirections = new Direction[] {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};

        Direction firstMove = this.getLastDirection();
        Direction firstHorizontal = this.getHorizontal();
        Direction firstVertical = this.getVertical();

        if (firstMove != null) {
            returnDirections.add(firstMove);
        }
        if (firstHorizontal != null && firstHorizontal != firstMove) {
            returnDirections.add(firstHorizontal);
        }
        if (firstVertical != null && firstVertical != firstMove) {
            returnDirections.add(firstVertical);
        }
        for (Direction thisDirection:
                theDirections) {
            if (!returnDirections.contains(thisDirection)) {
                returnDirections.add(thisDirection);
            }
        }

        for (int i = 0; i < returnDirections.size(); ) {
            if (this.wallIsBlocking(returnDirections.get(i), this.theseus)) {
                returnDirections.remove(i);
            }
            else {
                i++;
            }
        }
        return returnDirections;
    }

    protected Direction getLastDirection() {
        if (this.solution.isEmpty()) {
            return null;
        }
        else {
            return this.solution.peek().getDirection();
        }
    }

    protected Direction getHorizontal() {
        int colDiff = this.theseus.getCol() - this.exit.getCol();
        if (colDiff > 0) {
            return Direction.RIGHT;
        }
        else if (colDiff < 0) {
            return Direction.LEFT;
        }
        else {
            return null;
        }
    }

    protected Direction getVertical() {
        int rowDiff = this.theseus.getRow() - this.exit.getRow();
        if (rowDiff > 0) {
            return Direction.DOWN;
        }
        else if (rowDiff < 0) {
            return Direction.UP;
        }
        else {
            return null;
        }
    }

    protected boolean revertMove() {
        this.theseus.setLocation(this.solution.peek().getTheseusStart());
        this.minotaur.setLocation(this.solution.peek().getMinotaurStart());
        if (!this.solution.peek().nextDirection()) {
            if (this.solution.isEmpty()) {
                return false;
            }
            this.solution.pop();
            return this.revertMove();
        }
        return true;
    }

    protected boolean makeMoves() {
        Direction nextMove = this.solution.peek().getDirection();
        this.moveTheseus(nextMove);
        if (this.isWon()) {
            return true;
        }
        for (int i = 0; i < 2; i++) {
            if (this.isLost()) {
                return false;
            }
            this.moveMinotaur();
        }
        return (!this.isLost());
    }
}
