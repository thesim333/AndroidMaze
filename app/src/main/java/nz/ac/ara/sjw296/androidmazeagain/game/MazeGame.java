package nz.ac.ara.sjw296.androidmazeagain.game;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.communal.Wall;

/**
 * Created by Sim on 22/06/2017.
 */

public class MazeGame implements Savable, Loadable {
    //walls stored in [row][col] format
    protected Wall[][] topWalls;
    protected Wall[][] leftWalls;
    protected Point theseus;
    protected Point minotaur;
    protected Point exit;
    protected int moveCount = 0;
    protected String levelName;
    protected int depth = 0;
    protected int width = 0;

    public void moveMinotaur() {
        // can move twice
        // Each move must come from controller
        // With check for game state and minotaur position between

        //get minotaur move direction
        // + = LEFT
        // - = RIGHT
        int relativeColPosition = minotaur.getCol() - theseus.getCol();
        // + = UP
        // - = RIGHT
        int relativeRowPosition = minotaur.getRow() - theseus.getRow();
        //try move left/right
        if (relativeColPosition > 0 &&
                !this.wallIsBlocking(Direction.LEFT, this.minotaur)) {
            // move left
            this.moveMyThing(this.minotaur, Direction.LEFT);
        }
        else if (relativeColPosition < 0 &&
                !this.wallIsBlocking(Direction.RIGHT, this.minotaur)) {
            //move right
            this.moveMyThing(this.minotaur, Direction.RIGHT);
        }
        // try move up/down
        else if (relativeRowPosition > 0 &&
                !this.wallIsBlocking(Direction.UP, this.minotaur)) {
            //move up
            this.moveMyThing(this.minotaur, Direction.UP);
        }
        else if (relativeRowPosition < 0 &&
                !this.wallIsBlocking(Direction.DOWN, this.minotaur)) {
            this.moveMyThing(this.minotaur, Direction.DOWN);
        }
    }

    protected void moveMyThing(Point thing, Direction direction) {
        thing.translate(direction.rowAdjust, direction.colAdjust);
    }

    public boolean moveTheseus(Direction direction) {
        if (direction == Direction.PASS) {
            moveCount++;
            return true;
        }
        if (!this.wallIsBlocking(direction, this.theseus)) {
            //Move Theseus
            this.moveMyThing(this.theseus, direction);
            //count + 1
            moveCount++;
            return true;
        }
        return false;
    }

    protected boolean wallIsBlocking(Direction direction, Point p) {
        Wall thisWall = Wall.NOTHING;
        switch(direction) {
            case UP:
                thisWall = topWalls[p.getRow()][p.getCol()];
                break;
            case DOWN:
                thisWall = topWalls[p.getRow() + 1][p.getCol()];
                break;
            case LEFT:
                thisWall = leftWalls[p.getRow()][p.getCol()];
                break;
            case RIGHT:
                thisWall = leftWalls[p.getRow()][p.getCol() + 1];
        }
        return (thisWall == Wall.SOMETHING);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public boolean isWon() {
        return theseus.equals(exit);
    }

    public boolean isLost() {
        return theseus.equals(minotaur);
    }

    public int getWidthAcross() {
        return topWalls[0].length;
    }

    public int getDepthDown() {
        return topWalls.length;
    }

    public Wall whatsAbove(Point where) {
        return topWalls[where.getRow()][where.getCol()];
    }

    public Wall whatsLeft(Point where) {
        return leftWalls[where.getRow()][where.getCol()];
    }

    public Point wheresTheseus() {
        return theseus;
    }

    public Point wheresMinotaur() {
        return minotaur;
    }

    public Point wheresExit() {
        return exit;
    }

    public void setDepthDown(int depthDown) {
        this.depth = depthDown;

        if (this.width > 0) {
            this.createWallArea();
        }
    }

    public void setWidthAcross(int widthAcross) {
        this.width = widthAcross;

        if (this.depth > 0) {
            this.createWallArea();
        }
    }

    protected void createWallArea() {
        this.leftWalls = new Wall[this.depth][this.width];
        this.topWalls = new Wall[this.depth][this.width];

        for (int i = 0; i < this.depth; ++i){
            for (int j = 0; j < this.width; j++) {
                this.topWalls[i][j] = Wall.NOTHING;
                this.leftWalls[i][j] = Wall.NOTHING;
            }
        }
    }

    public String getLevelName() {
        return this.levelName;
    }

    public void addWallAbove(Point where) {
        this.topWalls[where.getRow()][where.getCol()] = Wall.SOMETHING;
    }

    public void addWallLeft(Point where) {
        this.leftWalls[where.getRow()][where.getCol()] = Wall.SOMETHING;
    }

    public void addTheseus(Point where) {
        this.theseus = new MazePoint(where);
    }

    public void addMinotaur(Point where) {
        this.minotaur = new MazePoint(where);
    }

    public void addExit(Point where) {
        this.exit = new MazePoint(where);
    }

    public void setName(String name) {
        this.levelName = name;
    }
}
