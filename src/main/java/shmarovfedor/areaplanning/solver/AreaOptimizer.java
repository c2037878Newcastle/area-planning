package shmarovfedor.areaplanning.solver;

import com.gurobi.gurobi.GRBEnv;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBLinExpr;
import com.gurobi.gurobi.GRBModel;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingPair;
import shmarovfedor.api.util.BuildingType;
import shmarovfedor.api.util.Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.gurobi.gurobi.GRB.*;
import static com.gurobi.gurobi.GRB.DoubleParam.Heuristics;
import static com.gurobi.gurobi.GRB.DoubleParam.TimeLimit;
import static com.gurobi.gurobi.GRB.IntParam.Method;
import static com.gurobi.gurobi.GRB.IntParam.OutputFlag;
import static java.util.Arrays.stream;
import static shmarovfedor.api.util.SolverState.INITIALIZATION;

public class AreaOptimizer extends Optimizer {

    public AreaOptimizer(Problem problem) {
        super(problem);
    }

    public void setModel() {

        // Application Status: Initialising
        setStatus(INITIALIZATION);


        double[] polyX = polygon.getX();
        double[] polyY = polygon.getY();

        // Calculating absolute maximum potential building number in area from total area size for each building type


        final var polyArea = polygon.getArea();
        typeMax = new int[types.length];
        for (int i = 0; i < typeMax.length; i++)
            typeMax[i] = types[i].countPerArea(polyArea);

        var totalMax = stream(typeMax).sum();
        // Calculating length of array

        buildings = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            var list = new ArrayList<Building>();
            var type = types[i];

            for (var j = 0; j < typeMax[i]; j++) {
                list.add(new Building(type));
            }

            buildings.put(type, list);
        }


        try {
            // Initialisation of Gurobi Optimiser
            environment = new GRBEnv("mip1.log");
            environment.set(Method, 1);
            environment.set(TimeLimit, timeLimit);
            environment.set(Heuristics, 1);
            environment.set(OutputFlag, 0);

            model = new GRBModel(environment);

            //creating variables
            var included = model.addVars(totalMax, BINARY);
            var atK = new AtomicInteger(0);
            buildings
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(building -> {
                                building.setGlobalIndex(atK.getAndIncrement());
                                building.setIncludedVariable(included[building.globalIndex()]);
                            }
                    );
            model.update();

            //setting objective
            var objective = new GRBLinExpr();
            buildings
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(building ->
                            objective.addTerm(building.benefit(), building.included())
                    );
            model.setObjective(objective, MAXIMIZE);

			/*
			Breaking Symmetry - reduces search time by eliminating symmetric parts of a search space e.g. 011, 101 110
			 */
            buildings
                    .keySet()
                    .stream()
                    .map(type -> {
                        var list = buildings.get(type);
                        var pairs = new ArrayList<BuildingPair>();
                        for (var i = 0; i < list.size() - 1; i++) {
                            var first = list.get(i);
                            var second = list.get(i + 1);
                            pairs.add(new BuildingPair(first, second));
                        }
                        return pairs;
                    })
                    .flatMap(Collection::stream)
                    .forEach(this::breakSymmetry);

            int zLength = 0;
            for (int i = 0; i < totalMax - 1; i++)
                for (int j = i + 1; j < totalMax; j++) zLength++;
            zLength *= 4;

            var x = model.addVars(totalMax, CONTINUOUS);
            var y = model.addVars(totalMax, CONTINUOUS);

            buildings.values().stream().flatMap(Collection::stream).forEach(b -> {
                b.setXVar(x[b.globalIndex()]);
                b.setYVar(y[b.globalIndex()]);
            });

            model.update();

			/*
			Setting non-overlap constraint for buildings
			Calculate values
			 */
            var allBuildings = buildings
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .toList();
            var allPairs = allBuildings.stream()
                    .map(b -> allBuildings
                            .stream()
                            .filter(b2 -> !b2.equals(b))
                            .map(b2 -> new BuildingPair(b, b2))
                            .toList()
                    )
                    .flatMap(Collection::stream)
                    .toList(); // TODO remove duplicates

            var bigM = calculateMaxDistance(polyX, polyY, types);
            allPairs.forEach(pair -> nonOverlap(pair, bigM));

			/*
			setting bound variables
		    calculate value
			Ensuring all vertices of buildings are in bounds
			*/
            for (int vertex = 0; vertex < polygon.vertices(); vertex++) {
                final var fVertex = vertex;
                allBuildings.forEach(b -> insideBounds(fVertex, b, 100000, polygon));
            }

            //setting exclusive polygons
            List<Polygon> exclusivePolygon = RegionManager.getExclusivePolygons();

			/*
			Ensuring all vertices of buildings are outside the excluded area
			 */
            for (var exPolygon : exclusivePolygon) {
                var vertexCount = exPolygon.getA().length;

                allBuildings.forEach(building -> {
                    try {
                        var excludedBin = model.addVars(vertexCount + 4, BINARY);
                        model.update();

                        for (int vertex = 0; vertex < vertexCount; vertex++) {
                            outsideBounds(vertex, building, exPolygon, excludedBin[vertex]);
                        }
                        verticalConstraints(building, vertexCount, excludedBin, exPolygon);
                    } catch (GRBException e) {
                        throw new RuntimeException(e);
                    }
                });

            }

            //setting callback
            //setLowerBound(SolutionManager.getCurrentBound());
            model.setCallback(new Callback(this, x, y, included, typeMax));

        } catch (GRBException e) {
            e.printStackTrace();
        }

    }

}
