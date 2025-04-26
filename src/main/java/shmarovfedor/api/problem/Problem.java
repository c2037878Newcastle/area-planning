package shmarovfedor.api.problem;

import shmarovfedor.api.graphics.BaseFrame;
import shmarovfedor.api.background.BackgroundWorker;
import shmarovfedor.api.solver.Optimizer;
import uk.co.rhilton.api.persist.SettingStorage;

import java.nio.file.Path;

public abstract class Problem {

    public abstract void initialize();

    public abstract BaseFrame frame();

    public abstract String toString();

    public abstract void start();

    public abstract void skip();

    public abstract void terminate();

    public abstract void openSettings();

    public abstract Optimizer optimizer();

    public abstract BackgroundWorker worker();

    public abstract boolean allowUserBuildings();

    public abstract SettingStorage config();

    public abstract void loadConfig(SettingStorage config);

    public boolean saveConfig(Path saveFile) {
        return config().saveToFile(saveFile);
    }

}
