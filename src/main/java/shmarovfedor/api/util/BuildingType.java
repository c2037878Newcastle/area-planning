package shmarovfedor.api.util;

import java.awt.Color;
import java.util.*;
import java.util.stream.Stream;

// TODO: Auto-generated Javadoc
/**
 * The Class Building.
 */
public abstract class BuildingType {

	/**
	 * Generate random color.
	 *
	 * @return the color
	 */
	// TODO move utils
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

	private final String id;

	protected BuildingType(String id) {
		this.id = id;

		TYPES.add(this);
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

}
