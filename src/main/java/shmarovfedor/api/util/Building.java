package shmarovfedor.api.util;

public record Building(BuildingType type, double x, double y) {

    public double benefit() {
        return type.benefit();
    }

}
