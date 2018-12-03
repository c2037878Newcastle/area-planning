package shmarovfedor.areaplanning.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import shmarovfedor.areaplanning.graphics.MainFrame;
import shmarovfedor.areaplanning.graphics.MapPanel;
import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.model.SolutionManager;
import shmarovfedor.areaplanning.background.BackgroundWorker;
import gurobi.*;

public class Callback extends GRBCallback{

	private GRBVar[] x;
	private GRBVar[] y;
	private GRBVar[] n;
	private int[] maxN;

	public Callback(GRBVar[] x, GRBVar[] y, GRBVar[] n, int[] maxN) {
		this.x = x;
		this.y = y;
		this.n = n;
		this.maxN = maxN;
		//MainFrame mainFrame = new MainFrame();
		//mainFrame.setVisible(true);
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
					List<Building> solution = new ArrayList<Building>();
					List<Building> buildings = BuildingManager.getAll();
					int k = 0;
					for (int i = 0; i < maxN.length; i++) {
						for (int j = 0; j < maxN[i]; j++) {
							if (Math.abs(nVar[k] - 1) < 0.1) {
								Building building = new Building(buildings.get(i).getWidth(),
																	buildings.get(i).getHeight(),
																	buildings.get(i).getBenefit(),
																	buildings.get(i).getColor(),
																	buildings.get(i).getName());
								building.setX(xVar[k]);
								building.setY(yVar[k]);
								solution.add(building);
							}
							k++;
						}
					}
					SolutionManager.add(solution);
					SolutionManager.setObjective(objective);
					SolutionManager.setObjectiveUpperBound(objBound);
					if (BackgroundWorker.isBinarySearch()) OptimizationManager.terminate();
				}
			}	
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

}
