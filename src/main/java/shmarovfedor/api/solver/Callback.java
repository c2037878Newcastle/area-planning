package shmarovfedor.api.solver;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBCallback;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBVar;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.util.BuildingType;
import shmarovfedor.api.util.SolutionBuilding;
import shmarovfedor.api.background.BackgroundWorker;

import java.util.ArrayList;

import static uk.co.rhilton.api.persist.DefaultSettings.BINARY_SEARCH;

public class Callback extends GRBCallback {

    private final Optimizer optimizer;
    private final GRBVar[] x;
    private final GRBVar[] y;
    private final GRBVar[] n;
    private final int[] maxN;

    public Callback(Optimizer optimizer, GRBVar[] x, GRBVar[] y, GRBVar[] n, int[] maxN) {
        this.optimizer = optimizer;
        this.x = x;
        this.y = y;
        this.n = n;
        this.maxN = maxN;
    }

    @Override
    protected void callback() {
        try {
            if (where == GRB.CB_MIPSOL) {

                double objective = getDoubleInfo(GRB.CB_MIPSOL_OBJ);
                double objBound = getDoubleInfo(GRB.CB_MIPSOL_OBJBND);

                if (objBound < 1e100) {

                    double[] xVar = getSolution(x);
                    double[] yVar = getSolution(y);
                    double[] nVar = getSolution(n);
					/*
					objective = getDoubleInfo(GRB.CB_MIPSOL_OBJ);
					objBound = getDoubleInfo(GRB.CB_MIPSOL_OBJBND);
					*/
                    //adding solution to the solution list
                    var solution = new ArrayList<SolutionBuilding>();
                    var types = BuildingType.types();
                    int k = 0;
                    for (int i = 0; i < maxN.length; i++) {
                        for (int j = 0; j < maxN[i]; j++) {
                            if (Math.abs(nVar[k] - 1) < 0.1) {
                                var building = new SolutionBuilding(types.get(i), xVar[k], yVar[k]);
                                solution.add(building);
                            }
                            k++;
                        }
                    }
                    SolutionManager.add(solution);
                    SolutionManager.setObjective(objective);
                    SolutionManager.setObjectiveUpperBound(objBound);
                    if (optimizer.problem().config().valueOf(BINARY_SEARCH))
                        optimizer.problem().optimizer().terminate();
                }
            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

}
