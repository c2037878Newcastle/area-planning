package uk.co.rhilton.townplanning;

import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.api.background.BackgroundWorker;
import shmarovfedor.api.solver.Optimizer;
import uk.co.rhilton.api.persist.SettingGUI;
import uk.co.rhilton.api.persist.SettingStorage;
import uk.co.rhilton.townplanning.building.HouseBuilding;
import uk.co.rhilton.townplanning.building.ShopBuilding;
import uk.co.rhilton.townplanning.graphics.TownFrame;
import uk.co.rhilton.townplanning.solver.TownOptimizer;

import static shmarovfedor.api.util.BuildingType.types;
import static uk.co.rhilton.api.persist.DefaultSettings.BINARY_SEARCH;
import static uk.co.rhilton.api.persist.DefaultSettings.TIME_LIMIT;
import static uk.co.rhilton.townplanning.persist.TownSettings.*;

public class TownProblem extends Problem {

    private Optimizer optimizer;
    private BackgroundWorker worker;
    private TownFrame frame;

    private HouseBuilding houseType;
    private ShopBuilding shopType;

    private SettingStorage config;
    private SettingGUI settingGUI;

    public void initialize() {
        config = new SettingStorage();

        optimizer = new TownOptimizer(this);
        worker = new BackgroundWorker(this);

        // register custom Building types
        houseType = new HouseBuilding(this);
        shopType = new ShopBuilding(this);

        createSettings();

        frame = new TownFrame(this);
        frame.setVisible(true);
    }

    private void createSettings() {
        settingGUI = new SettingGUI(this);
        settingGUI.with(TIME_LIMIT, "Time Limit", i -> i > 0);
        settingGUI.with(BINARY_SEARCH, "Binary Search", b -> true);

        settingGUI.with(SHOP_DISTANCE, "Shop Distance", i -> i > 0);

        settingGUI.with(HOUSE_WIDTH, "House Width", i -> i > 0);
        settingGUI.with(HOUSE_HEIGHT, "House Height", i -> i > 0);
        settingGUI.with(HOUSE_VALUE, "House Value", i -> i > 0);

        settingGUI.with(SHOP_WIDTH, "Shop Width", i -> i > 0);
        settingGUI.with(SHOP_HEIGHT, "Shop Height", i -> i > 0);
        settingGUI.with(SHOP_VALUE, "Shop Value", i -> i > 0);
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
        settingGUI.fillAndOpen();
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
