package shmarovfedor.areaplanning;

import shmarovfedor.api.graphics.BaseFrame;
import shmarovfedor.api.graphics.SettingsFrame;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.areaplanning.background.BackgroundWorker;
import shmarovfedor.areaplanning.graphics.AreaFrame;
import shmarovfedor.areaplanning.solver.AreaOptimizer;
import shmarovfedor.areaplanning.solver.Optimizer;

import java.util.Optional;

import static shmarovfedor.api.util.BuildingType.types;

public class AreaProblem extends Problem {

    private AreaFrame frame;
    private AreaOptimizer optimizer;
    private BackgroundWorker worker;

    public void initialize() {
        optimizer = new AreaOptimizer(this);

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

    public Optional<BackgroundWorker> worker() {
        return Optional.ofNullable(worker);
    }
}
