package shmarovfedor.api.util;

import shmarovfedor.api.problem.Problem;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.floor;


/**
 * The Class Building.
 */
public abstract class BuildingType {

    /**
     * Generate random color.
     *
     * @return the color
     */
    public static Color generateRandomColor() {
        Random random = new Random();
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    //
    // Type Registry
    //

    private final static Set<BuildingType> TYPES = new HashSet<>();

    public static void clear() {
        TYPES.clear();
    }

    public static Stream<BuildingType> stream() {
        return TYPES.stream();
    }

    public static List<BuildingType> types() {
        return new ArrayList<>(TYPES);
    }

    public static Optional<BuildingType> byID(String id) {
        return TYPES
                .stream()
                .filter(
                        type -> type.id().equalsIgnoreCase(id)
                ).findFirst();
    }

    public static double getPrecision() {
        return stream()
                .mapToDouble(BuildingType::benefit)
                .min()
                .orElse(Double.MAX_VALUE);
    }

    //
    // Constructor
    //

    private final Problem problem;
    private final String id;

    protected BuildingType(Problem problem, String id) {
        this.problem = problem;
        this.id = id;

        TYPES.add(this);
    }

    public Problem problem() {
        return problem;
    }

    public String id() {
        return id;
    }

    //
    // Parameters
    //

    public abstract Color color();

    public abstract double width();

    public abstract double height();

    public abstract double benefit();

    //
    // Helpers
    //

    public double area() {
        return height() * width();
    }

    public int countPerArea(double polygonArea) {
        return (int) floor(polygonArea / area());
    }

    public abstract Building createInstance();

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BuildingType type = (BuildingType) o;
        return Objects.equals(id, type.id);
    }

    public int hashCode() {
        return Objects.hashCode(id);
    }
}
