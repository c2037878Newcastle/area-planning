package uk.co.rhilton.townplanning.building;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;

import java.awt.*;

import static uk.co.rhilton.townplanning.persist.TownSettings.*;

public class HouseBuilding extends BuildingType {

    public static final String HOUSE_ID = "house";

    private final Color color;

    public HouseBuilding(Problem problem) {
        super(problem, HOUSE_ID);
        color = generateRandomColor();
    }

    public Color color() {
        return color;
    }

    public double width() {
        return problem().config().valueOf(HOUSE_WIDTH);
    }

    public double height() {
        return problem().config().valueOf(HOUSE_HEIGHT);
    }

    public double benefit() {
        return problem().config().valueOf(HOUSE_VALUE);
    }

    public Building createInstance() {
        return new Building(this);
    }
}
