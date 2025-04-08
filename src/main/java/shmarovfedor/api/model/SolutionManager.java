package shmarovfedor.api.model;

import java.util.ArrayList;
import java.util.List;

import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.Building;

// TODO: Auto-generated Javadoc
/**
 * The Class SolutionManager.
 */
public class SolutionManager {

	/** The solutions. */
	private static List<List<Building>> solutions = new ArrayList<>();
	
	/** The objective. */
	private static double objective;
	
	/** The upper bound. */
	private static double upperBound;
	
	/** The lower bound. */
	private static double lowerBound = 0;
	
	private static double objectiveUpperBound;
	
	private static double currentBound;
	
	public static void clear() {
		solutions = new ArrayList<>();
	}
		
	/**
	 * Adds the.
	 *
	 * @param solution the solution
	 * @return true, if successful
	 */
	public static boolean add(List<Building> solution) {
		return solutions.add(solution);
	}

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	public static List<List<Building>> getAll() {
		return solutions;
	}
	
	/**
	 * Reset.
	 */
	public static void reset() {
		solutions = new ArrayList<List<Building>>();
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
	public static List<Building> getLast() {
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

}
