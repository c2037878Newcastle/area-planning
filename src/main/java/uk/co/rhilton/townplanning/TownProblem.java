package uk.co.rhilton.townplanning;

import shmarovfedor.RunGUI;
import shmarovfedor.api.graphics.BaseFrame;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.areaplanning.background.BackgroundWorker;
import shmarovfedor.areaplanning.solver.Optimizer;
import uk.co.rhilton.townplanning.building.HouseBuilding;
import uk.co.rhilton.townplanning.building.ShopBuilding;
import uk.co.rhilton.townplanning.gui.TownFrame;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static shmarovfedor.api.util.BuildingType.types;

public class TownProblem extends Problem {

    private Optimizer optimizer;
    private BackgroundWorker worker;
    private TownFrame frame;

    public void initialize() {
        // TODO load config

        optimizer = new TownOptimizer(this);
        // register custom building types
        new HouseBuilding();
        new ShopBuilding();

        frame = new TownFrame(this);
        frame.setVisible(true);
    }

    public TownFrame frame() {
        return frame;
    }

    public String toString() {
        return "Town Planning by Ryan Hilton";
    }

    //TODO replace with better logic
    public void start() {
        SolutionManager.clear();
        Polygon polygon = RegionManager.getPolygon();
        optimizer.create(polygon, types());
        worker = new BackgroundWorker(this);
        worker.execute();
    }

    public void skip() {
        RunGUI.PROBLEMS[0].skip();
    }

    public void terminate() {
        RunGUI.PROBLEMS[0].terminate();
    }

    public void openSettings() {
        RunGUI.PROBLEMS[0].openSettings();
    }

    public Optimizer optimizer() {
        return optimizer;
    }

    public Optional<BackgroundWorker> worker() {
        return ofNullable(worker);
    }


}
