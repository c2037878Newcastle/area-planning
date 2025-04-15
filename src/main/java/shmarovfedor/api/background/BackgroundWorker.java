package shmarovfedor.api.background;

import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.BuildingType;

import javax.swing.*;

// TODO: Auto-generated Javadoc

/**
 * The Class BackgroundWorker.
 */
public class BackgroundWorker extends SwingWorker<String, Object> {
    
    private final Problem problem;

    public BackgroundWorker(Problem problem) {
        this.problem = problem;
    }

    private boolean binarySearch = true;

    public boolean isBinarySearch() {
        return binarySearch;
    }

    public void setBinarySearch(boolean binarySearch) {
        this.binarySearch = binarySearch;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected String doInBackground() throws Exception {
        System.out.println("WORKING");
        problem.optimizer().setModel();
        if (binarySearch) {
            System.out.println("pre optimize");
            problem.optimizer().optimize();
            System.out.println("optimized");
            double lowerBound = SolutionManager.getLowerBound();
            double upperBound = SolutionManager.getObjectiveUpperBound();
            System.out.println("lowerBound = " + lowerBound);
            System.out.println("upperBound = " + upperBound);
            System.out.println("BuildingType.getPrecision() = " + BuildingType.getPrecision());
            problem.optimizer().dispose();
            System.out.println("disposed");
            while ((upperBound - lowerBound) >= BuildingType.getPrecision()) {
                System.out.println("binary recursing");
                double bound = (upperBound + lowerBound) / 2;
                SolutionManager.setLowerBound(lowerBound);
                SolutionManager.setUpperBound(upperBound);
                SolutionManager.setCurrentBound(bound);

                problem.optimizer().setModel();
                problem.optimizer().setLowerBound(bound);
                System.out.println("optimize 2");
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
            System.out.println("non binary optimize");
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
