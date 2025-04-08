package shmarovfedor.areaplanning.solver;

import com.gurobi.gurobi.*;
import com.gurobi.gurobi.GRB.DoubleParam;
import com.gurobi.gurobi.GRB.IntParam;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingType;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.api.util.SolverState;

import java.util.List;

import static shmarovfedor.api.util.SolverState.*;

/**
 * The Class OptimizationManager.
 */
public abstract class Optimizer {

    private final Problem problem;

    public Optimizer(Problem problem) {
        this.problem = problem;
    }

    public Problem problem() {
        return problem;
    }

    //environment variables
    /**
     * The time limit.
     */
    protected double timeLimit = 3600;

    /**
     * The model.
     */
    protected GRBModel model;

    /**
     * The env.
     */
    protected GRBEnv env;

    /**
     * The max n.
     */
    protected int[] maxN;

    /**
     * The polygon.
     */
    protected Polygon polygon;

    /**
     * The building.
     */
    protected BuildingType[] types;

    /**
     * The n.
     */
    protected GRBVar[] n;

    /**
     * The objective bound.
     */
    protected double objectiveBound;

    /**
     * The correct termination.
     */
    protected boolean correctTermination;

    /**
     * The execution termination.
     */
    protected boolean executionTermination;

    /**
     * The lower bound constraint.
     */
    protected GRBConstr lowerBoundConstraint;

    protected GRBConstr lowerBoundPerturbationConstraint;

    protected SolverState status = WAITING;

    /**
     * Class constructor
     *
     * @param polygon the polygon
     * @param types   the types
     */
    public void create(Polygon polygon, List<BuildingType> types) {
        this.polygon = polygon;
        this.types = types.toArray(BuildingType[]::new);
        setExecutionTermination(false);
    }

    /**
     * Sets the model, predominantly declaring all mathematical constraints for solving the problem.
     */
    public abstract void setModel();

    protected double getMaxValue(double[] array) {
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < array.length; i++)
            if (max < array[i]) max = array[i];
        return max;
    }

    protected double getMinValue(double[] array) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < array.length; i++)
            if (min > array[i]) min = array[i];
        return min;
    }

    /**
     * Calculate max distance.
     *
     * @param vX       the v x
     * @param vY       the v y
     * @param building the building
     * @return the double
     */
    public double calculateMaxDistance(double[] vX, double[] vY, BuildingType[] building) {
        double maxDistance = Double.MIN_VALUE;
        for (int i = 0; i < vX.length - 1; i++) {
            for (int j = i + 1; j < vX.length; j++) {
                double distance = Math.sqrt((vX[i] - vX[j]) * (vX[i] - vX[j]) + (vY[i] - vY[j]) * (vY[i] - vY[j]));
                if (distance > maxDistance) maxDistance = distance;
            }
        }

        double maxSize = Double.MIN_VALUE;
        for (int i = 0; i < building.length; i++) {
            if (building[i].height() > maxSize) maxSize = building[i].height();
            if (building[i].width() > maxSize) maxSize = building[i].width();
        }

        return maxDistance + maxSize;
    }

    /**
     * Reset.
     */
    public void reset() {
        try {
            model.reset();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Optimize.
     */
    public void optimize() {
        try {
            setStatus(IN_PROGRESS);
            setCorrectTermination(false);
            model.optimize();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Terminate execution.
     */
    public void terminateExecution() {
        setStatus(TERMINATED);
        setExecutionTermination(true);
        model.terminate();
    }

    /**
     * Terminate.
     */
    public void terminate() {
        setCorrectTermination(true);
        model.terminate();
    }

    /**
     * Dispose.
     */
    public void dispose() {
        model.dispose();
        try {
            env.dispose();
        } catch (GRBException e) {
            e.printStackTrace();
        }
        setStatus(WAITING);
    }

    /**
     * Sets the lower bound.
     *
     * @param newBound the new lower bound
     */
    public void setLowerBound(double newBound) {
        try {
            setStatus(RECALCULATION);
            model.reset();
            int k = 0;
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < maxN.length; i++)
                for (int j = 0; j < maxN[i]; j++) {
                    expr.addTerm(types[i].benefit(), n[k]);
                    k++;
                }
            lowerBoundConstraint = model.addConstr(expr, GRB.GREATER_EQUAL, newBound, null);
            lowerBoundPerturbationConstraint = model.addConstr(expr, GRB.LESS_EQUAL, newBound + BuildingType.getPrecision() - 0.0001, null);
            objectiveBound = newBound;
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the lower bound.
     */
    public void removeLowerBound() {
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
    public double getObjectiveBound() {
        return this.objectiveBound;
    }

    /**
     * Checks if is correct termination.
     *
     * @return true, if is correct termination
     */
    public boolean isCorrectTermination() {
        return correctTermination;
    }

    /**
     * Sets the correct termination.
     *
     * @param correctTermination the new correct termination
     */
    public void setCorrectTermination(boolean correctTermination) {
        this.correctTermination = correctTermination;
    }

    /**
     * Gets the time limit.
     *
     * @return the time limit
     */
    public double getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the time limit.
     *
     * @param timeLimit the new time limit
     */
    public void setTimeLimit(double timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Checks if is execution termination.
     *
     * @return true, if is execution termination
     */
    public boolean isExecutionTermination() {
        return executionTermination;
    }

    /**
     * Sets the execution termination.
     *
     * @param executionTermination the new execution termination
     */
    public void setExecutionTermination(boolean executionTermination) {
        this.executionTermination = executionTermination;
    }

    /**
     * Gets the optimiser status
     *
     * @return
     */
    public SolverState getStatus() {
        return status;
    }

    /**
     * Sets the optimiser status
     *
     * @param status
     */
    public void setStatus(SolverState status) {
        this.status = status;
    }


}
