package shmarovfedor.api.util;

import java.util.Objects;

public class BuildingPair {
    private final Building first;
    private final Building second;

    public BuildingPair(Building first, Building second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BuildingPair that = (BuildingPair) o;
        return
                (Objects.equals(first, that.first) && Objects.equals(second, that.second))
                        || (Objects.equals(first, that.second) && Objects.equals(second, that.first));
    }

    public int hashCode() {
        var fGI = first.globalIndex();
        var sGI = second.globalIndex();
        if (fGI < sGI) {
            return 31 * first.hashCode() + second.hashCode();
        } else return 31 * second.hashCode() + first.hashCode();
    }

    public Building first() {
        return first;
    }

    public Building second() {
        return second;
    }

    @Override
    public String toString() {
        return "BuildingPair[" + first + ", " + second + ']';
    }

}
