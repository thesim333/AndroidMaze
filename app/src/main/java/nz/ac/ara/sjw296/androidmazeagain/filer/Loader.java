package nz.ac.ara.sjw296.androidmazeagain.filer;

import android.content.Context;

import java.io.InputStream;

import nz.ac.ara.sjw296.androidmazeagain.game.Loadable;

/**
 * Created by Sim on 22/06/2017.
 */

public interface Loader {
    void loadNextLevel(Loadable game, InputStream inputStream);

    void loadLevel(Loadable game, int level, InputStream inputStream);

    void loadSave(Loadable game, String fileName, Context context);

    void loadSave(Loadable game, Context context);

    void loadSave(Loadable game, String fileName, String level, Context context);

    String[] getLevelNamesFromFile(InputStream inputStream);

    int getCurrentLevel();

    boolean saveGameExists(Context context);
}
