package uk.co.rhilton.townplanning.building;

import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;

import java.awt.*;

public class HouseBuilding extends BuildingType {

    public static final String HOUSE_ID = "house";

    private final Color color;

    public HouseBuilding() {
        super(HOUSE_ID);
        color = generateRandomColor();
    }

    public Color color() {
        return color;
    }

    public double width() {
        return 10;
    }

    public double height() {
        return 10;
    }

    public double benefit() {
        return 1000;
    }

    public Building createInstance() {
        return new Building(this);
    }
}
