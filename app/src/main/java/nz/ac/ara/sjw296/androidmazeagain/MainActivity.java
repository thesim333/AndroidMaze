package nz.ac.ara.sjw296.androidmazeagain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import nz.ac.ara.sjw296.androidmazeagain.filer.Saver;
import nz.ac.ara.sjw296.androidmazeagain.game.Direction;
import nz.ac.ara.sjw296.androidmazeagain.game.Game;
import nz.ac.ara.sjw296.androidmazeagain.game.Loadable;
import nz.ac.ara.sjw296.androidmazeagain.game.MazeGame;
import nz.ac.ara.sjw296.androidmazeagain.game.Savable;
import nz.ac.ara.sjw296.androidmazeagain.solver.Sandbox;
import nz.ac.ara.sjw296.androidmazeagain.solver.SandboxGame;
import nz.ac.ara.sjw296.androidmazeagain.swipe.OnSwipeListener;

/**
 * The main activity that acts as a presenter for this app.
 * Controls the 2 views that show the game and the solution.
 * Controls data interaction the models the run the game and solution.
 * @author Simon Winder
 */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private Loader mMyLoader = new Filer();
    private Game mMyGame;
    private Sandbox mMySolver = new SandboxGame();
    private MazeView mMyGameView;
    private SolutionView mMySolutionView;
    private GestureDetector mDetector;
    final String CURRENT = "Current";
    final String MINOTAUR = "Minotaur";
    final String THESEUS = "Theseus";
    final String SOLUTION = "Solution";
    final String MOVES = "Moves";
    private Toolbar mToolbar;
    private MenuItem mNav_loadSave;
    private MenuItem mNav_save;
    private MenuItem mNav_solutions;
    private boolean mCheckSolutionOnCreate = false;

    /**
     * Creates the activity on start, restore (rotate).
     * @param savedInstanceState The saved state of this activity when restoring
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViews();
        setPauseButton();

        if (savedInstanceState != null) {
            restoreActivity(savedInstanceState);
        }
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Escape!");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(Color.parseColor("#555555"));
    }

    private void setupViews() {
        MazeGameView view = (MazeGameView) findViewById(R.id.mazeView);
        view.setOnTouchListener(this);
        mMyGameView = view;
        mMySolutionView = (SolutionView)findViewById(R.id.solutionView);
        mDetector = new GestureDetector(this, new OnSwipeListener() {
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
    }

    private void setPauseButton() {
        final Button button = (Button) findViewById(R.id.pause_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playMove(Direction.PASS);
            }
        });
    }

    private void restoreActivity(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(MINOTAUR)) {
            loadLevelFromResourceFile(savedInstanceState.getInt(CURRENT));
            Loadable game = (Loadable) mMyGame;
            game.addMinotaur(new MazePoint(savedInstanceState.getString(MINOTAUR)));
            game.addTheseus(new MazePoint(savedInstanceState.getString(THESEUS)));
            game.setMoveCount(savedInstanceState.getInt(MOVES));
            showPauseButton();
            setMoves();
        }
        mCheckSolutionOnCreate = savedInstanceState.getBoolean(SOLUTION);
    }

    /**
     * Event handler for touching view this activity handles touching for
     * @param v the view
     * @param event what?
     * @return happened
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    /**
     * Creating the menu
     * @param menu The menu belonging to the toolbar
     * @return happened
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options_menu, menu);
        mNav_save = menu.getItem(2);
        mNav_loadSave = menu.getItem(1);
        mNav_loadSave.setEnabled(mMyLoader.saveGameExists(this));
        mNav_solutions = menu.getItem(3);
        mNav_solutions.setChecked(mCheckSolutionOnCreate);
        return true;
    }

    /**
     * What happens when the menu options are touched
     * @param item which menu item was touched
     * @return the parent event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.nav_load) {
            makeLevelSelectDialog();
        } else if (id == R.id.nav_save) {
            if (gameIsLive()) {
                saveThisGame();
                mNav_loadSave.setEnabled(true);
            }
        } else if (id == R.id.nav_load_saved) {
            loadLevelFromSaveFile();
        } else if (id == R.id.nav_solution) {
            if (item.isChecked()) {
                item.setChecked(false);
                turnSolutionOff();

            } else {
                item.setChecked(true);
                turnSolutionOn();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the activity through rotates and other destruction events
     * @param outState holds all variables needed to redraw
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //game
        if (gameIsLive()) {
            int currentGame = mMyLoader.getCurrentLevel();
            String theseusCurrent = mMyGame.wheresTheseus().toString();
            String minotaurCurrent = mMyGame.wheresMinotaur().toString();
            outState.putInt(CURRENT, currentGame);
            outState.putString(MINOTAUR, minotaurCurrent);
            outState.putString(THESEUS, theseusCurrent);
            outState.putInt(MOVES, mMyGame.getMoveCount());
        }
        outState.putBoolean(SOLUTION, mNav_solutions.isChecked());
    }

    private void saveThisGame() {
        Saver saver = new Filer();
        saver.save((Savable) mMyGame, this);
    }

    private void turnSolutionOn() {
        if (gameIsLive()) {
            mMySolver.createGameState((Savable) mMyGame);
            mMySolver.begin();
            mMySolutionView.setSolution(mMySolver.getSolution());
            mMySolutionView.invalidate();
        }
    }

    private void turnSolutionOff() {
        mMySolutionView.stopShowingSolution();
    }

    private boolean gameIsLive() {
        return (mMyGame != null && !mMyGame.isLost() && !mMyGame.isWon());
    }

    private void makeLevelSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Level");
        builder.setItems(getLevelNamesList(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadLevelFromResourceFile(i);
                startGame();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String[] getLevelNamesList() {
        InputStream inputStream = getResources().openRawResource(R.raw.levels);
        Loader myLoader = new Filer();
        return myLoader.getLevelNamesFromFile(inputStream);
    }

    private void loadLevelFromResourceFile(int level) {
        InputStream inputStream = getResources().openRawResource(R.raw.levels);
        Loadable theGame = new MazeGame();
        mMyLoader.loadLevel(theGame, level, inputStream);
        mMyGame = (Game)theGame;
    }

    private void loadLevelFromSaveFile() {
        Loadable theGame = new MazeGame();
        mMyLoader.loadSave(theGame, this);
        mMyGame = (Game)theGame;
        startGame();
    }

    private void startGame() {
        showPauseButton();
        mNav_save.setEnabled(true);
        int rows = mMyGame.getDepthDown();
        int cols = mMyGame.getWidthAcross();
        mMyGameView.newGameSetup(rows, cols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Point p = new MazePoint(r, c);
                if (mMyGame.whatsAbove(p) == Wall.SOMETHING) {
                    mMyGameView.addTopWall(p);
                }
                if (mMyGame.whatsLeft(p) == Wall.SOMETHING) {
                    mMyGameView.addLeftWall(p);
                }
            }
        }

        setTheseus();
        mMyGameView.setMinotaur(mMyGame.wheresMinotaur());
        mMyGameView.invalidate();
        setMoves();

        if (mNav_solutions.isChecked()) {
            turnSolutionOn();
        }
    }

    private void showPauseButton() {
        Button pause = (Button) findViewById(R.id.pause_button);
        pause.setVisibility(View.VISIBLE);
    }

    private void playMove(final Direction direction) {
        if (gameIsLive()) {
            if (mMyGame.moveTheseus(direction)) {
                setTheseus();
                setMoves();
                if (mMyGame.isWon()) {
                    playSound(R.raw.happy);
                    showGameEndDialog("You won!", "What do you want to do now?");
                }  else if (mMyGame.isLost()) {
                    doLostEndGame();
                } else {
                    moveMinotaur(true);
                    if (mNav_solutions.isChecked()) {
                        progressSolution(direction);
                    }
                }
            }
        }
    }

    private void progressSolution(Direction direction) {
        if (mMySolutionView.getNextMove() == direction) {
            mMySolutionView.popAndDraw();
        } else {
            turnSolutionOn();
        }
    }

    private void setMoves() {
        mToolbar.setTitle("Moves: " + mMyGame.getMoveCount());
    }

    private void setTheseus() {
        mMyGameView.setTheseusMood(mMyGame.isWon() ? Mood.HAPPY : Mood.NORMAL);
        mMyGameView.setTheseusPosition(mMyGame.wheresTheseus());
        mMyGameView.invalidate();
    }

    private void moveMinotaur(final boolean repeat) {
        mMyGame.moveMinotaur();
        minotaurAnimation();
        if (mMyGame.isLost()) {
            doLostEndGame();
        } else if (repeat) {
            moveMinotaur(false);
        }
    }

    private void minotaurAnimation() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMyGameView.setMinotaur(mMyGame.wheresMinotaur());
                mMyGameView.invalidate();
            }
        }, 300);
    }

    private void doLostEndGame() {
        playSound(R.raw.sad);
        showGameEndDialog("You were killed by the fearsome minotaur!!!",
                "How do you want to get revenge?");
    }

    private void playSound(int sound) {
        MediaPlayer mp;
        mp = MediaPlayer.create(this, sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp=null;
            }
        });
        mp.start();
    }

    private void showGameEndDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadLevelFromResourceFile(mMyLoader.getCurrentLevel());
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
}
