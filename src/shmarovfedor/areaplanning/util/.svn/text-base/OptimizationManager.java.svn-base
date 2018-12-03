package shmarovfedor.areaplanning.util;

import java.util.List;

import javax.swing.SwingWorker;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import gurobi.GRB.DoubleParam;
import gurobi.GRB.IntAttr;
import gurobi.GRB.IntParam;
import shmarovfedor.areaplanning.graphics.MainFrame;
import shmarovfedor.areaplanning.graphics.MapPanel;
import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.model.RegionManager;

// TODO: Auto-generated Javadoc
/**
 * The Class OptimizationManager.
 */
public class OptimizationManager{
	
	//environment variables
	/** The time limit. */
	private static double timeLimit = 3600;

	/** The model. */
	private static GRBModel model;
	
	/** The env. */
	private static GRBEnv env;
	
	/** The max n. */
	private static int[] maxN;
	
	/** The polygon. */
	private static Polygon polygon;
	
	/** The building. */
	private static Building[] building;
	
	/** The n. */
	private static GRBVar[] n;
	
	/** The objective bound. */
	private static double objectiveBound;
	
	/** The correct termination. */
	private static boolean correctTermination;
	
	/** The execution termination. */
	private static boolean executionTermination;
	
	/** The lower bound constraint. */
	private static GRBConstr lowerBoundConstraint;
	
	private static GRBConstr lowerBoundPerturbationConstraint;
		
	private static int status = 0;
	
	/**
	 * Creates the.
	 *
	 * @param polygon the polygon
	 * @param building the building
	 */
	public static void create(Polygon polygon, Building[] building) {
	   	OptimizationManager.polygon = polygon;
		OptimizationManager.building = building;
		setExecutionTermination(false);
	}
	
	/**
	 * Sets the model.
	 */
	public static void setModel() {

		setStatus(1);
		
		double[] vX = polygon.getX();
		double[] vY = polygon.getY();
		
	    double[] a = polygon.getA();
	    double[] b = polygon.getB();
	    double[] c = polygon.getC();
	    
		//calculating maximum number of buildings of each type
		maxN = new int[building.length];
		for (int i = 0; i < maxN.length; i++) maxN[i] = (int)(polygon.getArea() / building[i].getArea());
		
		//calculating length of array
		int length = 0;
		for (int i = 0; i < maxN.length; i++) length += maxN[i];
		
		//printing out length of array
		double[] width = new double[length];
	    double[] height = new double[length];
	    
	    int k = 0;
	    for (int i = 0; i < maxN.length; i++) {
	    	for (int j = 0; j < maxN[i]; j++){
		    	width[k] = building[i].getWidth();
		    	height[k] = building[i].getHeight();
		    	k++;
	    	}
	    }

	    
		try {
			env = new GRBEnv("mip1.log");
			env.set(IntParam.Method, 1);
		    env.set(DoubleParam.TimeLimit, timeLimit);
		    env.set(DoubleParam.Heuristics, 1);
		    env.set(IntParam.OutputFlag, 0);
			
			model = new GRBModel(env);


			//creating array of type	
			char[] typeN = new char[length];
			for (int i = 0; i < length; i++) typeN[i] = GRB.BINARY;
			
			//creating variables
			n = model.addVars(null, null, null, typeN, null);
			model.update();
			
			//setting objective
			GRBLinExpr objective = new GRBLinExpr();
			k = 0;
			for (int i = 0; i < maxN.length; i++)
				for (int j = 0; j < maxN[i]; j++) {
					objective.addTerm(building[i].getBenefit(), n[k]);
					k++;
				}
			model.setObjective(objective, GRB.MAXIMIZE);
			
			GRBLinExpr expr = new GRBLinExpr();
			k = 0;
			for (int i = 0; i < maxN.length; i++) {
				for (int j = 0; j < maxN[i] - 1; j++) {
					expr = new GRBLinExpr();
					expr.addTerm(-1.0, n[k]); expr.addTerm(1.0, n[k + 1]); model.addConstr(expr, GRB.LESS_EQUAL, 0, null);
					k++;
				}
				k++;
			}
		    
		    //types of variables
		    char[] typeX = new char[length];
		    char[] typeY = new char[length];

		    for (int i = 0; i < length; i++) {
			    typeX[i] = GRB.CONTINUOUS;
			    typeY[i] = GRB.CONTINUOUS;
		    }

		    GRBVar[] x = model.addVars(null, null, null, typeX, null);
		    GRBVar[] y = model.addVars(null, null, null, typeY, null);
		    
		    int zLength = 0;
		    for (int i = 0; i < length - 1; i++)
		    	for (int j = i + 1; j < length; j++) zLength++;
		    
		    zLength *= 4;
		    
		    char[] typeZ = new char[zLength];
		    for (int i = 0; i < zLength; i++) typeZ[i] = GRB.BINARY;
		    GRBVar[] z = model.addVars(null, null, null, typeZ, null);
		    
			model.update();
			
			
			
			//setting non-overlap constraint
			//calculate values
			double bigM = calculateMaxDistance(vX, vY, building);
			double M1 = calculateMaxDistance(vX, vY, building);
		    expr = new GRBLinExpr();
		    k = 0;
		    for (int i = 0; i < length - 1; i++) {
		    	for (int j = i + 1; j < length; j++) {

		    		expr = new GRBLinExpr();
				    expr.addTerm(1.0, x[i]); expr.addTerm(-1.0, x[j]); expr.addTerm(-bigM, z[k]); expr.addTerm(M1, n[i]); expr.addTerm(M1, n[j]); model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (width[i] + width[j]) / 2, null);
				    expr = new GRBLinExpr();
				    expr.addTerm(-1.0, x[i]); expr.addTerm(1.0, x[j]); expr.addTerm(-bigM, z[k + 1]); expr.addTerm(M1, n[i]); expr.addTerm(M1, n[j]); model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (width[i] + width[j]) / 2, null);
				    expr = new GRBLinExpr();
				    expr.addTerm(1.0, y[i]); expr.addTerm(-1.0, y[j]); expr.addTerm(-bigM, z[k + 2]); expr.addTerm(M1, n[i]); expr.addTerm(M1, n[j]); model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (height[i] + height[j]) / 2, null);
				    expr = new GRBLinExpr();
				    expr.addTerm(-1.0, y[i]); expr.addTerm(1.0, y[j]); expr.addTerm(-bigM, z[k + 3]); expr.addTerm(M1, n[i]); expr.addTerm(M1, n[j]); model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (height[i] + height[j]) / 2, null);
				    expr = new GRBLinExpr();
				    expr.addTerm(1.0, z[k]); expr.addTerm(1.0, z[k + 1]); expr.addTerm(1.0, z[k + 2]); expr.addTerm(1.0, z[k + 3]); model.addConstr(expr, GRB.LESS_EQUAL, 3, null);
				    k = k + 4;
		    		
		    	}
		    }
			
		    //setting bound variables
		    //calculate value
		    double M2 = 100000;
			for (int i = 0; i < a.length; i++) {
		    	for (int j = 0; j < length; j++) {
		    		double minD = Double.MAX_VALUE;
				    if (- c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2 < minD) minD = - c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2;
				    if (- c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2 < minD) minD = - c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2;
				    if (- c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2 < minD) minD = - c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2;
				    if (- c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2 < minD) minD = - c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2;

				    expr = new GRBLinExpr();
				    expr.addTerm(a[i], x[j]); expr.addTerm(b[i], y[j]); expr.addTerm(M2, n[j]); model.addConstr(expr, GRB.LESS_EQUAL, M2 + minD, null);

		    	}
		    }
			
			
			
			//setting exclusive polygons
			List<Polygon> exclusivePolygon = RegionManager.getExclusivePolygons();
			
			for (k = 0; k < exclusivePolygon.size(); k++) {
				a = exclusivePolygon.get(k).getA();
				b = exclusivePolygon.get(k).getB();
				c = exclusivePolygon.get(k).getC();

			    double M3 = Double.MAX_VALUE;
				for (int j = 0; j < length; j++) {
										
					char[] typeE = new char[a.length + 4];
				    for (int i = 0; i < a.length + 4; i++) typeE[i] = GRB.BINARY;
				    GRBVar[] e = model.addVars(null, null, null, typeE, null);
				    model.update();
					
					for (int i = 0; i < a.length; i++) {
						M2 = 1000;
						
						double maxD = -Double.MAX_VALUE;
					    if (- c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2 > maxD) maxD = - c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2;
					    if (- c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2 > maxD) maxD = - c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2;
					    if (- c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2 > maxD) maxD = - c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2;
					    if (- c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2 > maxD) maxD = - c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2;
						
						expr = new GRBLinExpr();
						expr.addTerm(a[i], x[j]); expr.addTerm(b[i], y[j]); expr.addTerm(M2, n[j]); expr.addTerm(M3, e[i]); model.addConstr(expr, GRB.GREATER_EQUAL, M2 + maxD, null);
						
						/*
						expr = new GRBLinExpr();
						expr.addTerm(a[i], x[j]); expr.addTerm(b[i], y[j]); expr.addTerm(M2, n[j]); expr.addTerm(M3, e[i]); model.addConstr(expr, GRB.GREATER_EQUAL, M2 - c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2, null);
											
						expr = new GRBLinExpr();
						expr.addTerm(a[i], x[j]); expr.addTerm(b[i], y[j]); expr.addTerm(M2, n[j]); expr.addTerm(M3, e[i]); model.addConstr(expr, GRB.GREATER_EQUAL, M2 - c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2, null);
					
						expr = new GRBLinExpr();
						expr.addTerm(a[i], x[j]); expr.addTerm(b[i], y[j]); expr.addTerm(M2, n[j]); expr.addTerm(M3, e[i]); model.addConstr(expr, GRB.GREATER_EQUAL, M2 - c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2, null);
											
						expr = new GRBLinExpr();
						expr.addTerm(a[i], x[j]); expr.addTerm(b[i], y[j]); expr.addTerm(M2, n[j]); expr.addTerm(M3, e[i]); model.addConstr(expr, GRB.GREATER_EQUAL, M2 - c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2, null);
						*/
					}
					
					double[] polX = exclusivePolygon.get(k).getX();
					double[] polY = exclusivePolygon.get(k).getY();
					
					expr = new GRBLinExpr();
					expr.addTerm(1.0, x[j]); expr.addTerm(M3, e[a.length]); model.addConstr(expr, GRB.GREATER_EQUAL, getMaxValue(polX) + width[j] / 2, null);
					
					expr = new GRBLinExpr();
					expr.addTerm(-1.0, x[j]); expr.addTerm(M3, e[a.length + 1]); model.addConstr(expr, GRB.GREATER_EQUAL, - getMinValue(polX) + width[j] / 2, null);
					
					expr = new GRBLinExpr();
					expr.addTerm(1.0, y[j]); expr.addTerm(M3, e[a.length + 2]); model.addConstr(expr, GRB.GREATER_EQUAL, getMaxValue(polY) + height[j] / 2, null);
					
					expr = new GRBLinExpr();
					expr.addTerm(-1.0, y[j]); expr.addTerm(M3, e[a.length + 3]); model.addConstr(expr, GRB.GREATER_EQUAL, - getMinValue(polY) + height[j] / 2, null);
									
					expr = new GRBLinExpr();
					for (int i = 0; i < a.length + 4; i++) expr.addTerm(1.0, e[i]);
					model.addConstr(expr, GRB.LESS_EQUAL, a.length + 4 - 1, null);

				}
				
			}
						
			//setting callback
			//setLowerBound(SolutionManager.getCurrentBound());
			model.setCallback(new Callback(x, y, n, maxN));

		} catch (GRBException e) {
			e.printStackTrace();
		}
		
	}
	
	private static double getMaxValue(double[] array) {
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < array.length; i++)
			if (max < array[i]) max = array[i];
		return max;
	}
	
	private static double getMinValue(double[] array) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < array.length; i++)
			if (min > array[i]) min = array[i];
		return min;
	}

	/**
	 * Calculate max distance.
	 *
	 * @param vX the v x
	 * @param vY the v y
	 * @param building the building
	 * @return the double
	 */
	public static double calculateMaxDistance(double[] vX, double vY[], Building[] building) {
		double maxDistance = Double.MIN_VALUE;
		for (int i = 0; i < vX.length - 1; i++) {
			for (int j = i + 1; j < vX.length; j++) {
				double distance = Math.sqrt((vX[i] - vX[j]) * (vX[i] - vX[j]) + (vY[i] - vY[j]) * (vY[i] - vY[j]));
				if (distance > maxDistance) maxDistance = distance;				
			}
		}
		
		double maxSize = Double.MIN_VALUE;
		for (int i = 0; i < building.length; i++) {
			if (building[i].getHeight() > maxSize) maxSize = building[i].getHeight();
			if (building[i].getWidth() > maxSize) maxSize = building[i].getWidth();
		}
		
		return maxDistance + maxSize;
	}
	
	/**
	 * Reset.
	 */
	public static void reset() {
		try {
			model.reset();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Optimize.
	 */
	public static void optimize() {
		try {
			setStatus(2);
			setCorrectTermination(false);
			model.optimize();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Terminate execution.
	 */
	public static void terminateExecution() {
		setStatus(3);
		setExecutionTermination(true);
		model.terminate();
	}
	
	/**
	 * Terminate.
	 */
	public static void terminate() {
		setCorrectTermination(true);
		model.terminate();
	}
	
	/**
	 * Dispose.
	 */
	public static void dispose() {
		model.dispose();
		try {
			env.dispose();
		} catch (GRBException e) {
			e.printStackTrace();
		}
		setStatus(0);
	}
	
	/**
	 * Sets the lower bound.
	 *
	 * @param newBound the new lower bound
	 */
	public static void setLowerBound(double newBound) {
		try {
			setStatus(4);
			model.reset();
			int k = 0;
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < maxN.length; i++)
				for (int j = 0; j < maxN[i]; j++) {
					expr.addTerm(building[i].getBenefit(), n[k]);
					k++;
				}
			lowerBoundConstraint = model.addConstr(expr, GRB.GREATER_EQUAL, newBound, null);
			lowerBoundPerturbationConstraint = model.addConstr(expr, GRB.LESS_EQUAL, newBound + BuildingManager.getPrecision() - 0.0001, null);
			objectiveBound = newBound;
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the lower bound.
	 */
	public static void removeLowerBound() {
		try {
			if (lowerBoundConstraint != null) {
				model.remove(lowerBoundPerturbationConstraint);
				model.remove(lowerBoundConstraint);
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the objective bound.
	 *
	 * @return the objective bound
	 */
	public static double getObjectiveBound() {
		return OptimizationManager.objectiveBound;
	}

	/**
	 * Checks if is correct termination.
	 *
	 * @return true, if is correct termination
	 */
	public static boolean isCorrectTermination() {
		return correctTermination;
	}

	/**
	 * Sets the correct termination.
	 *
	 * @param correctTermination the new correct termination
	 */
	public static void setCorrectTermination(boolean correctTermination) {
		OptimizationManager.correctTermination = correctTermination;
	}
		
	/**
	 * Gets the time limit.
	 *
	 * @return the time limit
	 */
	public static double getTimeLimit() {
		return timeLimit;
	}
	
	/**
	 * Sets the time limit.
	 *
	 * @param timeLimit the new time limit
	 */
	public static void setTimeLimit(double timeLimit) {
		OptimizationManager.timeLimit = timeLimit;
	}

	/**
	 * Checks if is execution termination.
	 *
	 * @return true, if is execution termination
	 */
	public static boolean isExecutionTermination() {
		return executionTermination;
	}

	/**
	 * Sets the execution termination.
	 *
	 * @param executionTermination the new execution termination
	 */
	public static void setExecutionTermination(boolean executionTermination) {
		OptimizationManager.executionTermination = executionTermination;
	}

	public static int getStatus() {
		return status;
	}

	public static void setStatus(int status) {
		OptimizationManager.status = status;
	}



}
