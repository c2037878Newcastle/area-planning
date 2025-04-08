package shmarovfedor.areaplanning.solver;

import com.gurobi.gurobi.*;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Polygon;

import java.util.List;

import static shmarovfedor.api.util.SolverState.INITIALIZATION;

public class AreaOptimizer extends Optimizer {

    public AreaOptimizer(Problem problem) {
        super(problem);
    }

    public void setModel() {

        // Application Status: Initialising
        setStatus(INITIALIZATION);

        double[] vX = polygon.getX();
        double[] vY = polygon.getY();

        double[] a = polygon.getA();
        double[] b = polygon.getB();
        double[] c = polygon.getC();

        // Calculating absolute maximum potential building number in area from total area size for each building type
        maxN = new int[types.length];
        for (int i = 0; i < maxN.length; i++) maxN[i] = (int) (polygon.getArea() / types[i].area());

        // Calculating length of array
        int length = 0;
        for (int i = 0; i < maxN.length; i++) length += maxN[i];

        // Printing lengths of array
        double[] width = new double[length];
        double[] height = new double[length];

        int k = 0;
        for (int i = 0; i < maxN.length; i++) {
            for (int j = 0; j < maxN[i]; j++) {
                width[k] = types[i].width();
                height[k] = types[i].height();
                k++;
            }
        }


        try {
            // Initialisation of Gurobi Optimiser
            env = new GRBEnv("mip1.log");
            env.set(GRB.IntParam.Method, 1);
            env.set(GRB.DoubleParam.TimeLimit, timeLimit);
            env.set(GRB.DoubleParam.Heuristics, 1);
            env.set(GRB.IntParam.OutputFlag, 0);

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
                    objective.addTerm(types[i].benefit(), n[k]);
                    k++;
                }
            model.setObjective(objective, GRB.MAXIMIZE);

			/*
			Breaking Symmetry - reduces search time by eliminating symmetric parts of a search space e.g. 011, 101 110
			 */
            GRBLinExpr expr = new GRBLinExpr();
            k = 0;
            for (int i = 0; i < maxN.length; i++) {
                for (int j = 0; j < maxN[i] - 1; j++) {
                    expr = new GRBLinExpr();
                    expr.addTerm(-1.0, n[k]);
                    expr.addTerm(1.0, n[k + 1]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 0, null);
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

			/*
			Setting non-overlap constraint for buildings
			Calculate values
			 */
            double bigM = calculateMaxDistance(vX, vY, types);
            double M1 = calculateMaxDistance(vX, vY, types);
            expr = new GRBLinExpr();
            k = 0;
            for (int i = 0; i < length - 1; i++) {
                for (int j = i + 1; j < length; j++) {

                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, x[i]);
                    expr.addTerm(-1.0, x[j]);
                    expr.addTerm(-bigM, z[k]);
                    expr.addTerm(M1, n[i]);
                    expr.addTerm(M1, n[j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (width[i] + width[j]) / 2, null);
                    expr = new GRBLinExpr();
                    expr.addTerm(-1.0, x[i]);
                    expr.addTerm(1.0, x[j]);
                    expr.addTerm(-bigM, z[k + 1]);
                    expr.addTerm(M1, n[i]);
                    expr.addTerm(M1, n[j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (width[i] + width[j]) / 2, null);
                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, y[i]);
                    expr.addTerm(-1.0, y[j]);
                    expr.addTerm(-bigM, z[k + 2]);
                    expr.addTerm(M1, n[i]);
                    expr.addTerm(M1, n[j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (height[i] + height[j]) / 2, null);
                    expr = new GRBLinExpr();
                    expr.addTerm(-1.0, y[i]);
                    expr.addTerm(1.0, y[j]);
                    expr.addTerm(-bigM, z[k + 3]);
                    expr.addTerm(M1, n[i]);
                    expr.addTerm(M1, n[j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 2 * M1 - (height[i] + height[j]) / 2, null);
                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, z[k]);
                    expr.addTerm(1.0, z[k + 1]);
                    expr.addTerm(1.0, z[k + 2]);
                    expr.addTerm(1.0, z[k + 3]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 3, null);
                    k = k + 4;

                }
            }



			/*
			setting bound variables
		    calculate value
			Ensuring all vertices of buildings are in bounds
			*/
            double M2 = 100000;
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < length; j++) {
                    double minD = Double.MAX_VALUE;
                    if (-c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2 < minD)
                        minD = -c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2;
                    if (-c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2 < minD)
                        minD = -c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2;
                    if (-c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2 < minD)
                        minD = -c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2;
                    if (-c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2 < minD)
                        minD = -c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2;

                    expr = new GRBLinExpr();
                    expr.addTerm(a[i], x[j]);
                    expr.addTerm(b[i], y[j]);
                    expr.addTerm(M2, n[j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, M2 + minD, null);

                }
            }


            //setting exclusive polygons
            List<Polygon> exclusivePolygon = RegionManager.getExclusivePolygons();

			/*
			Ensuring all vertices of buildings are outside the excluded area
			 */
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
                        if (-c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2 > maxD)
                            maxD = -c[i] + a[i] * width[j] / 2 + b[i] * height[j] / 2;
                        if (-c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2 > maxD)
                            maxD = -c[i] - a[i] * width[j] / 2 + b[i] * height[j] / 2;
                        if (-c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2 > maxD)
                            maxD = -c[i] + a[i] * width[j] / 2 - b[i] * height[j] / 2;
                        if (-c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2 > maxD)
                            maxD = -c[i] - a[i] * width[j] / 2 - b[i] * height[j] / 2;

                        expr = new GRBLinExpr();
                        expr.addTerm(a[i], x[j]);
                        expr.addTerm(b[i], y[j]);
                        expr.addTerm(M2, n[j]);
                        expr.addTerm(M3, e[i]);
                        model.addConstr(expr, GRB.GREATER_EQUAL, M2 + maxD, null);

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

					/*
					Implementation of Vertical Constraint for buildings
					 */
                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, x[j]);
                    expr.addTerm(M3, e[a.length]);
                    model.addConstr(expr, GRB.GREATER_EQUAL, getMaxValue(polX) + width[j] / 2, null);

                    expr = new GRBLinExpr();
                    expr.addTerm(-1.0, x[j]);
                    expr.addTerm(M3, e[a.length + 1]);
                    model.addConstr(expr, GRB.GREATER_EQUAL, -getMinValue(polX) + width[j] / 2, null);

                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, y[j]);
                    expr.addTerm(M3, e[a.length + 2]);
                    model.addConstr(expr, GRB.GREATER_EQUAL, getMaxValue(polY) + height[j] / 2, null);

                    expr = new GRBLinExpr();
                    expr.addTerm(-1.0, y[j]);
                    expr.addTerm(M3, e[a.length + 3]);
                    model.addConstr(expr, GRB.GREATER_EQUAL, -getMinValue(polY) + height[j] / 2, null);

                    expr = new GRBLinExpr();
                    for (int i = 0; i < a.length + 4; i++) expr.addTerm(1.0, e[i]);
                    model.addConstr(expr, GRB.LESS_EQUAL, a.length + 4 - 1, null);

                }

            }

            //setting callback
            //setLowerBound(SolutionManager.getCurrentBound());
            model.setCallback(new Callback(this, x, y, n, maxN));

        } catch (GRBException e) {
            e.printStackTrace();
        }

    }

}
