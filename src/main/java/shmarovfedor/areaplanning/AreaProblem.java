package shmarovfedor.areaplanning;

import shmarovfedor.api.graphics.BaseFrame;
import shmarovfedor.api.graphics.SettingsFrame;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.api.background.BackgroundWorker;
import shmarovfedor.areaplanning.graphics.AreaFrame;
import shmarovfedor.areaplanning.solver.AreaOptimizer;
import shmarovfedor.api.solver.Optimizer;
import uk.co.rhilton.api.setting.SettingStorage;

import static shmarovfedor.api.util.BuildingType.types;

public class AreaProblem extends Problem {

    private AreaFrame frame;
    private AreaOptimizer optimizer;
    private BackgroundWorker worker;

    private SettingStorage config;

    public void initialize() {
        this.config = new SettingStorage();

        optimizer = new AreaOptimizer(this);
        worker = new BackgroundWorker(this);

        frame = new AreaFrame(this);
        frame.setVisible(true);

    }

    public BaseFrame frame() {
        return frame;
    }

    public String toString() {
        return "Area Planning by Fedor Shmarov";
    }

    public void start() {
        SolutionManager.clear();
        Polygon polygon = RegionManager.getPolygon();
        optimizer.create(polygon, types());
        worker = new BackgroundWorker(this);
        worker.execute();
    }

    public void skip() {
        optimizer.terminate();
        optimizer.setCorrectTermination(false);
    }

    public void terminate() {
        optimizer.terminateExecution();
    }

    public void openSettings() {
        var settings = new SettingsFrame(this, 200, 200);
        settings.setVisible(true);
    }

    public Optimizer optimizer() {
        return optimizer;
    }

    public BackgroundWorker worker() {
        return worker;
    }

    public boolean allowUserBuildings() {
        return true;
    }

    public SettingStorage config() {
        return config;
    }

    public void loadConfig(SettingStorage config) {
        this.config = config;
    }

}
