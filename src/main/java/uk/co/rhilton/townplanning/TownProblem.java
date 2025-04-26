package uk.co.rhilton.townplanning;

import shmarovfedor.api.graphics.SettingsFrame;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.api.background.BackgroundWorker;
import shmarovfedor.api.solver.Optimizer;
import uk.co.rhilton.api.persist.SettingStorage;
import uk.co.rhilton.townplanning.building.HouseBuilding;
import uk.co.rhilton.townplanning.building.ShopBuilding;
import uk.co.rhilton.townplanning.gui.TownFrame;

import java.nio.file.Path;

import static java.util.Optional.ofNullable;
import static shmarovfedor.api.util.BuildingType.types;

public class TownProblem extends Problem {

    private Optimizer optimizer;
    private BackgroundWorker worker;
    private TownFrame frame;

    private HouseBuilding houseType;
    private ShopBuilding shopType;

    private SettingStorage config;

    public void initialize() {
        config = new SettingStorage();

        optimizer = new TownOptimizer(this);
        worker = new BackgroundWorker(this);

        // register custom building types
        houseType = new HouseBuilding();
        shopType = new ShopBuilding();

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
        return false;
    }

    public SettingStorage config() {
        return config;
    }

    public void loadConfig(SettingStorage config) {
        this.config = config;

    }

}
