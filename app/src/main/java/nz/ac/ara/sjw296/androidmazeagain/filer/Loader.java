package nz.ac.ara.sjw296.androidmazeagain.filer;

import java.io.InputStream;

import nz.ac.ara.sjw296.androidmazeagain.game.Loadable;

/**
 * Created by Sim on 22/06/2017.
 */

public interface Loader {
    void loadNextLevel(Loadable game, InputStream inputStream);

    void loadLevel(Loadable game, int level, InputStream inputStream);

    void loadSave(Loadable game, String fileName);

    void loadSave(Loadable game);

    void loadSave(Loadable game, String fileName, String level);

    String[] getLevelNamesFromFile(InputStream inputStream);

    int getCurrentLevel();
}
