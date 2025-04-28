package uk.co.rhilton.townplanning.util;

import com.gurobi.gurobi.GRBVar;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingPair;

public class ShopHousePair extends BuildingPair {
    private int index;
    private GRBVar included;

    public ShopHousePair(Building shop, Building house) {
        super(shop, house);
        this.index = -1;
    }

    public ShopHousePair index(int index) {
        this.index = index;
        return this;
    }

    public int index() {
        return index;
    }

    public ShopHousePair included(GRBVar included) {
        this.included = included;
        return this;
    }

    public GRBVar included() {
        return included;
    }

    public Building shop() {
        return first();
    }

    public Building house() {
        return second();
    }

}
