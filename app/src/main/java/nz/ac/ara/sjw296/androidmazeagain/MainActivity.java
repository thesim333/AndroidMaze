package nz.ac.ara.sjw296.androidmazeagain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.communal.Wall;
import nz.ac.ara.sjw296.androidmazeagain.filer.Filer;
import nz.ac.ara.sjw296.androidmazeagain.filer.Loader;
import nz.ac.ara.sjw296.androidmazeagain.game.Direction;
import nz.ac.ara.sjw296.androidmazeagain.game.Game;
import nz.ac.ara.sjw296.androidmazeagain.game.Loadable;
import nz.ac.ara.sjw296.androidmazeagain.game.MazeGame;
import nz.ac.ara.sjw296.androidmazeagain.solver.Sandbox;
import nz.ac.ara.sjw296.androidmazeagain.swipe.OnSwipeListener;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    protected Loader myLoader = new Filer();
    protected Game myGame;
    protected Sandbox mySolver;
    protected MazeGameView theView;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Escape!");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#555555"));
        theView = (MazeGameView) findViewById(R.id.mazeView);
        detector = new GestureDetector(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(SwipeDirection direction) {
                if (direction == SwipeDirection.up){
                    playMove(Direction.UP);
                }
                if (direction == SwipeDirection.down){
                    playMove(Direction.DOWN);
                }
                if (direction == SwipeDirection.left){
                    playMove(Direction.LEFT);
                }
                if (direction == SwipeDirection.right){
                    playMove(Direction.RIGHT);
                }
                return true;
            }
        });
        theView.setOnTouchListener(this);
        final Button button = (Button) findViewById(R.id.pause_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playMove(Direction.PASS);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.nav_load) {
            makeLevelSelectDialog();
        } else if (id == R.id.nav_save) {

        } else if (id == R.id.nav_load_saved) {

        } else if (id == R.id.nav_solution) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected void makeLevelSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Level");
        builder.setItems(getLevelNamesList(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadLevelFromResourceFile(i);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected String[] getLevelNamesList() {
        InputStream inputStream = getResources().openRawResource(R.raw.levels);
        Loader myLoader = new Filer();
        return myLoader.getLevelNamesFromFile(inputStream);
    }

    public void loadLevelFromResourceFile(int level) {
        InputStream inputStream = getResources().openRawResource(R.raw.levels);
        Loadable theGame = new MazeGame();
        myLoader.loadLevel(theGame, level, inputStream);
        myGame = (Game)theGame;
        startGame();
    }

    protected void startGame() {
        int rows = myGame.getDepthDown();
        int cols = myGame.getWidthAcross();
        theView.newGameSetup(rows, cols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Point p = new MazePoint(r, c);
                if (myGame.whatsAbove(p) == Wall.SOMETHING) {
                    theView.addTopWall(p);
                }
                if (myGame.whatsLeft(p) == Wall.SOMETHING) {
                    theView.addLeftWall(p);
                }
            }
        }

        theView.setTheseusMood(Mood.NORMAL);
        theView.setTheseusPosition(myGame.wheresTheseus());
        theView.setMinotaur(myGame.wheresMinotaur());
        theView.setMoves(myGame.getMoveCount());
        theView.invalidate();
    }

    protected void playMove(Direction direction) {
        if (myGame != null && !myGame.isLost() && !myGame.isWon()) {
            if (myGame.moveTheseus(direction)) {
                setTheseus();
                if (myGame.isWon()) {
                    showGameEndDialog("You won!", "What do you want to do now?");
                }  else if (myGame.isLost()) {
                    doEndGame();
                } else {
                    moveMinotaur(true);
                }
            }
        }
    }

    protected void setTheseus() {
        if (myGame.isWon()) {
            theView.setTheseusMood(Mood.HAPPY);
        } else {
            theView.setTheseusMood(Mood.NORMAL);
        }
        theView.setTheseusPosition(myGame.wheresTheseus());
        theView.setMoves(myGame.getMoveCount());
        theView.invalidate();
    }

    protected void moveMinotaur(final boolean repeat) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myGame.moveMinotaur();
                theView.setMinotaur(myGame.wheresMinotaur());
                theView.invalidate();

                if (myGame.isLost()) {
                    doEndGame();
                } else if (repeat) {
                    moveMinotaur(false);
                }
            }
        }, 300);
    }

    protected void doEndGame() {
        showGameEndDialog("You were killed by the fearsome minotaur!!!",
                "How do you want to get revenge?");
    }

    protected void showGameEndDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadLevelFromResourceFile(myLoader.getCurrentLevel());
            }
        });
        builder.setNeutralButton(R.string.load_resource, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeLevelSelectDialog();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected
}
