package shmarovfedor.api.util;

import com.gurobi.gurobi.GRBVar;

import java.util.Objects;

public class Building {
    private final BuildingType type;

    private int globalIndex;

    private GRBVar included;
    private GRBVar x;
    private GRBVar y;


    public Building(BuildingType type) {
        this.type = type;
        this.globalIndex = -1;
    }

    public Building setGlobalIndex(int globalIndex) {
        this.globalIndex = globalIndex;
        return this;
    }

    public int globalIndex() {
        return globalIndex;
    }

    public void setIncludedVariable(GRBVar enabledVar) {
        this.included = enabledVar;
    }

    public void setXVar(GRBVar x) {
        this.x = x;
    }

    public void setYVar(GRBVar y) {
        this.y = y;
    }

    public GRBVar included() {
        return included;
    }

    public double benefit() {
        return type.benefit();
    }

    public double height() {
        return type.height();
    }

    public double width() {
        return type.width();
    }

    public double area() {
        return type.area();
    }

    public BuildingType type() {
        return type;
    }

    public GRBVar xVar() {
        return x;
    }

    public GRBVar yVar() {
        return y;
    }

    public double x() {
        return 0;
    }

    public double y() {
        return 0;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        var building = (Building) o;
        return globalIndex == building.globalIndex && type.equals(building.type);
    }

    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + globalIndex;
        return result;
    }
}
