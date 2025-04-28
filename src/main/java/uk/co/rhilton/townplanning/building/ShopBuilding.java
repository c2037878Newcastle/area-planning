package uk.co.rhilton.townplanning.building;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;

import java.awt.*;

import static uk.co.rhilton.townplanning.setting.TownSettings.*;

public class ShopBuilding extends BuildingType {

    public static final String SHOP_ID = "shop";

    private final Color color;

    public ShopBuilding(Problem problem) {
        super(problem, SHOP_ID);
        color = generateRandomColor();
    }

    public Color color() {
        return color;
    }

    public double width() {
        return problem().config().valueOf(SHOP_WIDTH);
    }

    public double height() {
        return problem().config().valueOf(SHOP_HEIGHT);
    }

    public double benefit() {
        return problem().config().valueOf(SHOP_VALUE);
    }

    public Building createInstance() {
        return new Building(this);
    }
}
