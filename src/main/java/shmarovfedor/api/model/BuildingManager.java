package shmarovfedor.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shmarovfedor.api.util.Building;

// TODO: Auto-generated Javadoc
/**
 * The Class BuildingManager.
 */
public class BuildingManager {

	/** The buildings. */
	//private static List<Building> buildings = new ArrayList<Building>();
	
	private static Map<String, Building> buildings = new HashMap<String, Building>();
	
	/**
	 * Adds the.
	 *
	 * @param building the building
	 * @return true, if successful
	 */
	public static Building add(Building building) {
		return buildings.put(building.getName(), building);
	}
	
	/**
	 * Removes the.
	 *
	 * @param building the building
	 * @return true, if successful
	 */
	public static Building remove(Building building) {
		return buildings.remove(building.getName());
	}
	
	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	public static List<Building> getAll() {
		return new ArrayList<Building>(buildings.values());
	}

	/**
	 * Gets the precision.
	 *
	 * @return the precision
	 */
	public static double getPrecision() {
		double precision = Double.MAX_VALUE;
		List<Building> building = new ArrayList<Building>(buildings.values());
		for (int i = 0; i < buildings.size(); i++) {
			if (building.get(i).getBenefit() < precision) precision = building.get(i).getBenefit();
		}
		return precision;
	}
	
	public static Building get(String name) {
		return buildings.get(name);
	}


}
