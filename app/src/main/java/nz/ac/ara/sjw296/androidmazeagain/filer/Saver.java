package nz.ac.ara.sjw296.androidmazeagain.filer;

import nz.ac.ara.sjw296.androidmazeagain.game.Savable;

/**
 * Created by Sim on 22/06/2017.
 */

public interface Saver {
    void save(Savable game);

    void save(Savable game, String fileName);

    void save(Savable game, String fileName, String levelName);
}
