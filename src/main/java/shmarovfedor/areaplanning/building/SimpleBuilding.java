package shmarovfedor.areaplanning.building;

import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;

import java.awt.*;
import java.util.Optional;

public class SimpleBuilding extends BuildingType {

    private final double width, height;
    private final double benefit;
    private final Color color;

    public SimpleBuilding(String id, double width, double height, double benefit, Optional<Color> color) {
        super(id);
        this.width = width;
        this.height = height;
        this.benefit = benefit;
        this.color = color.orElseGet(BuildingType::generateRandomColor);
    }

    public Color color() {
        return color;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public double benefit() {
        return benefit;
    }

    public Building createInstance() {
        return new Building(this);
    }
}
