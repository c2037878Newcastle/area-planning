package shmarovfedor.api.solver;

import com.gurobi.gurobi.*;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    protected GRBModel model;
    protected GRBEnv environment;
    protected Polygon polygon;
    protected BuildingType[] types;
    protected double objectiveBound;
    protected boolean correctTermination;
    protected boolean executionTermination;
    protected GRBConstr lowerBoundConstraint;

    protected GRBConstr lowerBoundPerturbationConstraint;

    protected SolverState status = WAITING;

    protected Map<BuildingType, List<Building>> buildings;

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

    public void nonOverlap(BuildingPair pair, double bigM, GRBVar[] toggles) {
        try {

            var expr = new GRBLinExpr();
            expr.addTerm(1.0, pair.first().xVar());
            expr.addTerm(-1.0, pair.second().xVar());
            expr.addTerm(-bigM, toggles[0]);
            expr.addTerm(bigM, pair.first().included());
            expr.addTerm(bigM, pair.second().included());
            model.addConstr(expr, GRB.LESS_EQUAL, 2 * bigM - (pair.first().width() + pair.second().width()) / 2, null);
            expr = new GRBLinExpr();
            expr.addTerm(-1.0, pair.first().xVar());
            expr.addTerm(1.0, pair.second().xVar());
            expr.addTerm(-bigM, toggles[1]);
            expr.addTerm(bigM, pair.first().included());
            expr.addTerm(bigM, pair.second().included());
            model.addConstr(expr, GRB.LESS_EQUAL, 2 * bigM - (pair.first().width() + pair.second().width()) / 2, null);
            expr = new GRBLinExpr();
            expr.addTerm(1.0, pair.first().yVar());
            expr.addTerm(-1.0, pair.second().yVar());
            expr.addTerm(-bigM, toggles[2]);
            expr.addTerm(bigM, pair.first().included());
            expr.addTerm(bigM, pair.second().included());
            model.addConstr(expr, GRB.LESS_EQUAL, 2 * bigM - (pair.first().height() + pair.second().height()) / 2, null);
            expr = new GRBLinExpr();
            expr.addTerm(-1.0, pair.first().yVar());
            expr.addTerm(1.0, pair.second().yVar());
            expr.addTerm(-bigM, toggles[3]);
            expr.addTerm(bigM, pair.first().included());
            expr.addTerm(bigM, pair.second().included());
            model.addConstr(expr, GRB.LESS_EQUAL, 2 * bigM - (pair.first().height() + pair.second().height()) / 2, null);
            expr = new GRBLinExpr();
            expr.addTerm(1.0, toggles[0]);
            expr.addTerm(1.0, toggles[1]);
            expr.addTerm(1.0, toggles[2]);
            expr.addTerm(1.0, toggles[3]);
            model.addConstr(expr, GRB.LESS_EQUAL, 3, null);
        } catch (GRBException e) {
            throw new RuntimeException(e);
        }
    }

    public void breakSymmetry(BuildingPair pair) {
        try {
            var expr = new GRBLinExpr();
            expr.addTerm(-1.0, pair.first().included());
            expr.addTerm(1.0, pair.second().included());
            model.addConstr(expr, GRB.LESS_EQUAL, 0, null);
        } catch (GRBException e) {
            throw new RuntimeException(e);
        }
    }

    public void insideBounds(int vertex, Building building, double M, Polygon poly) {
        double[] a = poly.getA();
        double[] b = poly.getB();
        double[] c = poly.getC();

        double minD = Double.MAX_VALUE;
        if (-c[vertex] + a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2 < minD)
            minD = -c[vertex] + a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2;
        if (-c[vertex] - a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2 < minD)
            minD = -c[vertex] - a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2;
        if (-c[vertex] + a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2 < minD)
            minD = -c[vertex] + a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2;
        if (-c[vertex] - a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2 < minD)
            minD = -c[vertex] - a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2;

        var expr = new GRBLinExpr();
        expr.addTerm(a[vertex], building.xVar());
        expr.addTerm(b[vertex], building.yVar());
        expr.addTerm(M, building.included());

        try {
            model.addConstr(expr, GRB.LESS_EQUAL, M + minD, null);
        } catch (GRBException e) {
            throw new RuntimeException(e);
        }
    }

    public void outsideBounds(int vertex, Building building, Polygon excluded, GRBVar e) throws GRBException {
        double[] a = excluded.getA();
        double[] b = excluded.getB();
        double[] c = excluded.getC();

        double M3 = Double.MAX_VALUE;
        var M2 = 1000;

        double maxD = -Double.MAX_VALUE;
        if (-c[vertex] + a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2 > maxD)
            maxD = -c[vertex] + a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2;
        if (-c[vertex] - a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2 > maxD)
            maxD = -c[vertex] - a[vertex] * building.width() / 2 + b[vertex] * building.height() / 2;
        if (-c[vertex] + a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2 > maxD)
            maxD = -c[vertex] + a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2;
        if (-c[vertex] - a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2 > maxD)
            maxD = -c[vertex] - a[vertex] * building.width() / 2 - b[vertex] * building.height() / 2;

        var expr = new GRBLinExpr();
        expr.addTerm(a[vertex], building.xVar());
        expr.addTerm(b[vertex], building.yVar());
        expr.addTerm(M2, building.included());
        expr.addTerm(M3, e);
        model.addConstr(expr, GRB.GREATER_EQUAL, M2 + maxD, null);

    }

    public void verticalConstraints(Building building, int length, GRBVar[] excludedBin, Polygon exPolygon) throws GRBException {
        double[] exPolX = exPolygon.getX();
        double[] exPolY = exPolygon.getY();

        var M3 = Double.MAX_VALUE;

        var expr = new GRBLinExpr();
        expr.addTerm(1.0, building.xVar());
        expr.addTerm(M3, excludedBin[length]);
        model.addConstr(expr, GRB.GREATER_EQUAL, getMaxValue(exPolX) + building.width() / 2, null);

        expr = new GRBLinExpr();
        expr.addTerm(-1.0, building.xVar());
        expr.addTerm(M3, excludedBin[length + 1]);
        model.addConstr(expr, GRB.GREATER_EQUAL, -getMinValue(exPolX) + building.width() / 2, null);

        expr = new GRBLinExpr();
        expr.addTerm(1.0, building.yVar());
        expr.addTerm(M3, excludedBin[length + 2]);
        model.addConstr(expr, GRB.GREATER_EQUAL, getMaxValue(exPolY) + building.height() / 2, null);

        expr = new GRBLinExpr();
        expr.addTerm(-1.0, building.yVar());
        expr.addTerm(M3, excludedBin[length + 3]);
        model.addConstr(expr, GRB.GREATER_EQUAL, -getMinValue(exPolY) + building.height() / 2, null);

        expr = new GRBLinExpr();
        for (int i = 0; i < length + 4; i++)
            expr.addTerm(1.0, excludedBin[i]);
        model.addConstr(expr, GRB.LESS_EQUAL, length + 3, null);
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
            System.out.println("OPTIMIZING");
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
        if (model != null) model.terminate();
    }

    /**
     * Terminate.
     */
    public void terminate() {
        setCorrectTermination(true);
        if (model != null) model.terminate();
    }

    /**
     * Dispose.
     */
    public void dispose() {
        model.dispose();
        try {
            environment.dispose();
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
            buildings
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(b ->
                            expr.addTerm(b.benefit(), b.included())
                    );
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
