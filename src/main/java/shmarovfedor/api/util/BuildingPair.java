package shmarovfedor.api.util;

import java.util.Objects;

public record BuildingPair(Building first, Building second) {

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
}
