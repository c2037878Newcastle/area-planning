package shmarovfedor.areaplanning.background;

import javax.swing.SwingWorker;

import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.model.SolutionManager;
import shmarovfedor.areaplanning.solver.Optimizer;

// TODO: Auto-generated Javadoc
/**
 * The Class BackgroundWorker.
 */
public class BackgroundWorker  extends SwingWorker<String, Object>{

	private static boolean binarySearch = true;
	
	public static boolean isBinarySearch() {
		return binarySearch;
	}

	public static void setBinarySearch(boolean binarySearch) {
		BackgroundWorker.binarySearch = binarySearch;
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected String doInBackground() throws Exception {
		Optimizer.setModel();
		if (binarySearch) {
			Optimizer.optimize();
			double lowerBound = SolutionManager.getLowerBound();
			double upperBound = SolutionManager.getObjectiveUpperBound();
			Optimizer.dispose();
			while((upperBound - lowerBound) >= BuildingManager.getPrecision())
			{
				double bound = (upperBound + lowerBound) / 2;
				SolutionManager.setLowerBound(lowerBound);
				SolutionManager.setUpperBound(upperBound);
				SolutionManager.setCurrentBound(bound);
				
				Optimizer.setModel();
				Optimizer.setLowerBound(bound);
				Optimizer.optimize();
				if (Optimizer.isCorrectTermination()) lowerBound = bound; else upperBound = bound;
				if (Optimizer.isExecutionTermination()) {
					Optimizer.terminateExecution();
					Optimizer.dispose();
					break;
				}
			}
		} else {
			Optimizer.removeLowerBound();
			Optimizer.setLowerBound(SolutionManager.getLowerBound());
			Optimizer.optimize();
			if (!Optimizer.isExecutionTermination()) Optimizer.terminateExecution();
			Optimizer.dispose();
		}
		
		if (!Optimizer.isExecutionTermination()) {
			Optimizer.terminateExecution();
			Optimizer.dispose();
		}

		return null;
	}

}
