package shmarovfedor.api.background;

import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.BuildingType;

import javax.swing.*;

import static uk.co.rhilton.api.setting.DefaultSettings.BINARY_SEARCH;

// TODO: Auto-generated Javadoc

/**
 * The Class BackgroundWorker.
 */
public class BackgroundWorker extends SwingWorker<Void, Void> {
    
    private final Problem problem;

    public BackgroundWorker(Problem problem) {
        this.problem = problem;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception {
        problem.optimizer().setModel();
        if (problem.config().valueOf(BINARY_SEARCH)) {
            problem.optimizer().optimize();
            double lowerBound = SolutionManager.getLowerBound();
            double upperBound = SolutionManager.getObjectiveUpperBound();
            problem.optimizer().dispose();
            while ((upperBound - lowerBound) >= BuildingType.getPrecision()) {
                double bound = (upperBound + lowerBound) / 2;
                SolutionManager.setLowerBound(lowerBound);
                SolutionManager.setUpperBound(upperBound);
                SolutionManager.setCurrentBound(bound);

                problem.optimizer().setModel();
                problem.optimizer().setLowerBound(bound);
                problem.optimizer().optimize();
                if (problem.optimizer().isCorrectTermination()) lowerBound = bound;
                else upperBound = bound;
                if (problem.optimizer().isExecutionTermination()) {
                    problem.optimizer().terminateExecution();
                    problem.optimizer().dispose();
                    break;
                }
            }
        } else {
            problem.optimizer().removeLowerBound();
            problem.optimizer().setLowerBound(SolutionManager.getLowerBound());
            problem.optimizer().optimize();
            if (!problem.optimizer().isExecutionTermination()) problem.optimizer().terminateExecution();
            problem.optimizer().dispose();
        }

        if (!problem.optimizer().isExecutionTermination()) {
            problem.optimizer().terminateExecution();
            problem.optimizer().dispose();
        }

        return null;
    }

}
