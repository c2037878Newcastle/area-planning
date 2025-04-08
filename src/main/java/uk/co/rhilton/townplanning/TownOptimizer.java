package uk.co.rhilton.townplanning;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.areaplanning.solver.AreaOptimizer;
import shmarovfedor.areaplanning.solver.Optimizer;

public class TownOptimizer extends AreaOptimizer {

    public TownOptimizer(Problem problem) {
        super(problem);
    }
}
