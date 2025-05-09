package shmarovfedor.api.model;

import java.util.ArrayList;
import java.util.List;

import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;
import shmarovfedor.api.util.SolutionBuilding;
import uk.co.rhilton.townplanning.building.HouseBuilding;
import uk.co.rhilton.townplanning.building.ShopBuilding;

import static java.lang.Math.abs;
import static uk.co.rhilton.townplanning.building.HouseBuilding.HOUSE_ID;
import static uk.co.rhilton.townplanning.building.ShopBuilding.SHOP_ID;

/**
 * The Class SolutionManager.
 */
public class SolutionManager {

	/** The solutions. */
	private static List<List<SolutionBuilding>> solutions = new ArrayList<>();
	
	/** The objective. */
	private static double objective;
	
	/** The upper bound. */
	private static double upperBound;
	
	/** The lower bound. */
	private static double lowerBound = 0;
	
	private static double objectiveUpperBound;

	private static double currentBound;

	private static int pass = 0;
	
	public static void clear() {
		solutions = new ArrayList<>();
	}
		
	/**
	 * Adds the.
	 *
	 * @param solution the solution
	 * @return true, if successful
	 */
	public static boolean add(List<SolutionBuilding> solution) {
		var shops = solution.stream().filter(b -> b.type() instanceof ShopBuilding).toList();
		var house = solution.stream().filter(b -> b.type() instanceof HouseBuilding).toList();

		house
				.stream()
				.flatMapToDouble(h -> shops.stream().mapToDouble(s -> manhattan(h, s)).min().stream())
				.max()
				.ifPresent(max -> System.out.println("\n\nSolution Max Distance: " + max + "\n\n"));

		return solutions.add(solution);
	}

	private static double manhattan(SolutionBuilding one, SolutionBuilding two) {
		return abs(one.x() - two.x()) + abs(one.y() - two.y());
	}

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	public static List<List<SolutionBuilding>> getAll() {
		return solutions;
	}
	
	/**
	 * Reset.
	 */
	public static void reset() {
		solutions = new ArrayList<>();
	}
		
	/**
	 * Gets the objective.
	 *
	 * @return the objective
	 */
	public static double getObjective() {
		return objective;
	}
	
	/**
	 * Sets the objective.
	 *
	 * @param objective the new objective
	 */
	public static void setObjective(double objective) {
		SolutionManager.objective = objective;
	}
	
	/**
	 * Gets the last.
	 *
	 * @return the last
	 */
	public static List<SolutionBuilding> getLast() {
		return solutions.getLast();
	}

	
	/**
	 * Gets the upper bound.
	 *
	 * @return the upper bound
	 */
	public static double getUpperBound() {
		return upperBound;
	}

	/**
	 * Sets the upper bound.
	 *
	 * @param upperBound the new upper bound
	 */
	public static void setUpperBound(double upperBound) {
		SolutionManager.upperBound = upperBound;
	}
	
	/**
	 * Gets the lower bound.
	 *
	 * @return the lower bound
	 */
	public static double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Sets the lower bound.
	 *
	 * @param lowerBound the new lower bound
	 */
	public static void setLowerBound(double lowerBound) {
		SolutionManager.lowerBound = lowerBound;
	}

	public static double getObjectiveUpperBound() {
		return objectiveUpperBound;
	}

	public static void setObjectiveUpperBound(double objectiveUpperBound) {
		SolutionManager.objectiveUpperBound = objectiveUpperBound;
	}

	public static double getCurrentBound() {
		return currentBound;
	}

	public static void setCurrentBound(double currentBound) {
		SolutionManager.currentBound = currentBound;
	}

	public static void incrementPass() {
		pass++;
	}

	public static int getPassCount() {
		return pass;
	}

	public static void resetPass() {
		pass = 0;
	}
}
