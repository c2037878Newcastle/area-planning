package uk.co.rhilton.townplanning.building;

import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;

import java.awt.*;

public class ShopBuilding extends BuildingType {

    public static final String SHOP_ID = "shop";

    private final Color color;

    public ShopBuilding() {
        super(SHOP_ID);
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
        return 10;
    }
}
