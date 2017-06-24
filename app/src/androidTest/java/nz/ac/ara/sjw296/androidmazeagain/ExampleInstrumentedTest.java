package nz.ac.ara.sjw296.androidmazeagain;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.filer.Filer;
import nz.ac.ara.sjw296.androidmazeagain.filer.Loader;
import nz.ac.ara.sjw296.androidmazeagain.filer.Saver;
import nz.ac.ara.sjw296.androidmazeagain.game.Direction;
import nz.ac.ara.sjw296.androidmazeagain.game.Game;
import nz.ac.ara.sjw296.androidmazeagain.game.Loadable;
import nz.ac.ara.sjw296.androidmazeagain.game.MazeGame;
import nz.ac.ara.sjw296.androidmazeagain.game.Savable;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("nz.ac.ara.sjw296.androidmazeagain", appContext.getPackageName());
    }

    protected Game loadFirstLevel() {
        Game game = new MazeGame();
        Context appContext = InstrumentationRegistry.getTargetContext();
        InputStream inputStream = appContext.getResources().openRawResource(R.raw.levels);
        Loader loader = new Filer();
        loader.loadNextLevel((Loadable)game, inputStream);
        return game;
    }

    @Test
    public void saveGameLoadSaveDefault() {
        Game game = loadFirstLevel();
        assertTrue(new MazePoint(2, 1).equals(game.wheresTheseus()));
        game.moveTheseus(Direction.RIGHT);
        assertTrue(new MazePoint(2, 2).equals(game.wheresTheseus()));
        Saver saver = new Filer();
        saver.save((Savable)game, InstrumentationRegistry.getTargetContext());
        Loader loader = new Filer();
        Game game2 = new MazeGame();
        loader.loadSave((Loadable)game2, InstrumentationRegistry.getTargetContext());
        assertTrue(new MazePoint(2, 2).equals(game2.wheresTheseus()));
    }
}
