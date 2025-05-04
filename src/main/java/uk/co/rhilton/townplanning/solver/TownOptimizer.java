package uk.co.rhilton.townplanning.solver;

import com.gurobi.gurobi.*;
import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.solver.Callback;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.BuildingPair;
import shmarovfedor.api.util.Polygon;
import shmarovfedor.api.solver.Optimizer;
import uk.co.rhilton.townplanning.TownProblem;
import uk.co.rhilton.townplanning.building.HouseBuilding;
import uk.co.rhilton.townplanning.building.ShopBuilding;
import uk.co.rhilton.townplanning.util.ShopHousePair;

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
import static java.lang.System.arraycopy;
import static java.util.Arrays.stream;
import static shmarovfedor.api.util.SolverState.INITIALIZATION;
import static uk.co.rhilton.api.setting.DefaultSettings.TIME_LIMIT;
import static uk.co.rhilton.townplanning.setting.TownSettings.SHOP_DISTANCE;

public class TownOptimizer extends Optimizer {

    public TownOptimizer(Problem problem) {
        super(problem);
    }

    public TownProblem problem() {
        return (TownProblem) super.problem();
    }

    public void setModel() {

        System.out.println("Setting model at " + System.currentTimeMillis());

        // Application Status: Initialising
        setStatus(INITIALIZATION);


        double[] polyX = polygon.getX();
        double[] polyY = polygon.getY();

        // Calculating absolute maximum potential building number in area from total area size for each building type


        final var polyArea = polygon.getArea();
        var typeMax = new int[types.length];
        for (int i = 0; i < typeMax.length; i++)
            typeMax[i] = types[i].countPerArea(polyArea);

        var totalMax = stream(typeMax).sum();
        // Calculating length of array

        buildings = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            var list = new ArrayList<Building>();
            var type = types[i];

            for (var j = 0; j < typeMax[i]; j++) {
                list.add(type.createInstance());
            }

            buildings.put(type, list);
        }


        try {
            // Initialisation of Gurobi Optimiser
            environment = new GRBEnv("mip2.log");
            environment.set(Method, 1);
            environment.set(TimeLimit, problem().config().valueOf(TIME_LIMIT));
            environment.set(Heuristics, 1);
            environment.set(OutputFlag, 1);

            model = new GRBModel(environment);

            //creating variables
            var included = model.addVars(totalMax, BINARY);
            model.update();
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
            var toggles = model.addVars(4 * allPairs.size(), BINARY);
            model.update();
            for (var i = 0; i < allPairs.size(); i++) {
                var subToggle = new GRBVar[4];
                arraycopy(toggles, i * 4, subToggle, 0, 4);
                nonOverlap(allPairs.get(i), bigM, subToggle);
            }

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

                var checkCount = vertexCount + 4;
                var excludedBin = model.addVars(allBuildings.size() * (checkCount), BINARY);
                model.update();

                atK.set(0);

                allBuildings.forEach(building -> {
                    try {
                        var subExcludedBin = new GRBVar[checkCount];
                        arraycopy(
                                excludedBin, atK.getAndIncrement() * checkCount,
                                subExcludedBin, 0, checkCount
                        );

                        for (int vertex = 0; vertex < vertexCount; vertex++) {
                            outsideBounds(vertex, building, exPolygon, subExcludedBin[vertex]);
                        }
                        verticalConstraints(building, vertexCount, subExcludedBin, exPolygon);
                    } catch (GRBException e) {
                        throw new RuntimeException(e);
                    }
                });

            }

            System.out.println("Starting Custom Town Modelling:");

            var shops = allBuildings.stream().filter(b -> b.type() instanceof ShopBuilding).collect(Collectors.toSet());
            var houses = allBuildings.stream().filter(b -> b.type() instanceof HouseBuilding).collect(Collectors.toSet());

            System.out.println("Shops: " + shops.size());
            System.out.println("Houses: " + houses.size());

            var shopHousePairs = shops
                    .stream()
                    .flatMap(shop -> houses
                            .stream()
                            .map(
                                    house -> new ShopHousePair(shop, house)
                            )
                    )
                    .collect(Collectors.toSet());

            var shEnabled = model.addVars(shopHousePairs.size(), BINARY);
            model.update();
            var atomicCount = new AtomicInteger(0);

            shopHousePairs.forEach(pair -> {
                pair.index(atomicCount.getAndIncrement());
                pair.excluded(shEnabled[pair.index()]);
            });


            var maxShopDistance = problem().config().valueOf(SHOP_DISTANCE);
            // distance check
            shopHousePairs.forEach(pair ->
                    shopHouseDistance(pair, maxShopDistance, bigM)
            );

            // at least one shop in solution
            var shopExpr = new GRBLinExpr();
            shops.forEach(shop -> shopExpr.addTerm(1, shop.included()));
            model.addConstr(shopExpr, GREATER_EQUAL, 1, null);

            // at least one house in solution
            var houseExpr = new GRBLinExpr();
            houses.forEach(house -> houseExpr.addTerm(1, house.included()));
            model.addConstr(houseExpr, GREATER_EQUAL, 1, null);

            var zCount = new AtomicInteger(0);
            var shopsZ = model.addVars(shops.size(), BINARY);
            model.update();
            shops.forEach(shop -> {
                try {
                    var shopExcludes =
                            shopHousePairs.stream()
                                    .filter(pair -> pair.shop().equals(shop))
                                    .map(ShopHousePair::excluded)
                                    .collect(Collectors.toSet());


                    var z = shopsZ[zCount.getAndIncrement()];
                    var expr = new GRBLinExpr();
                    expr.addTerm(-bigM, z);
                    shopExcludes.forEach(val -> expr.addTerm(1, val));
                    model.addConstr(expr, LESS_EQUAL, shopExcludes.size() + 1, null);

                    var includeCheck = new GRBLinExpr();
                    expr.addTerm(1, z);
                    expr.addTerm(1, shop.included());
                    model.addConstr(includeCheck, LESS_EQUAL, 1, null);
                } catch (GRBException e) {
                    throw new RuntimeException(e);
                }
            });

            zCount.set(0);
            var houseZ = model.addVars(houses.size(), BINARY);
            model.update();
            houses.forEach(house -> {
                try {
                    var houseExcludes =
                            shopHousePairs.stream()
                                    .filter(pair -> pair.house().equals(house))
                                    .map(ShopHousePair::excluded)
                                    .collect(Collectors.toSet());

                    var z = houseZ[zCount.getAndIncrement()];
                    var expr = new GRBLinExpr();
                    expr.addTerm(-bigM, z);
                    houseExcludes.forEach(val -> expr.addTerm(1, val));
                    model.addConstr(expr, LESS_EQUAL, houseExcludes.size() + 1, null);

                    var includeCheck = new GRBLinExpr();
                    expr.addTerm(1, z);
                    expr.addTerm(1, house.included());
                    model.addConstr(includeCheck, LESS_EQUAL, 1, null);

                } catch (GRBException e) {
                    throw new RuntimeException(e);
                }
            });

            System.out.println("Added all constraints");


            model.update();
            //setting callback
            model.setCallback(new Callback(this, x, y, included, typeMax));

        } catch (GRBException e) {
            e.printStackTrace();
        }

        System.out.println("MODEL INITIALIZED");
    }

    private void shopHouseDistance(ShopHousePair pair, int distance, double bigM) {
        try {
            var shop = pair.shop();
            var house = pair.house();

            var expr = new GRBLinExpr();
            expr.addTerm(1.0, shop.xVar());
            expr.addTerm(-1.0, house.xVar());
            expr.addTerm(1.0, shop.yVar());
            expr.addTerm(-1.0, house.yVar());
            expr.addTerm(-bigM, pair.excluded());
            model.addConstr(expr, LESS_EQUAL, distance, null);

            expr = new GRBLinExpr();
            expr.addTerm(-1.0, shop.xVar());
            expr.addTerm(1.0, house.xVar());
            expr.addTerm(1.0, shop.yVar());
            expr.addTerm(-1.0, house.yVar());
            expr.addTerm(-bigM, pair.excluded());
            model.addConstr(expr, LESS_EQUAL, distance, null);

            expr = new GRBLinExpr();
            expr.addTerm(1.0, shop.xVar());
            expr.addTerm(-1.0, house.xVar());
            expr.addTerm(-1.0, shop.yVar());
            expr.addTerm(1.0, house.yVar());
            expr.addTerm(-bigM, pair.excluded());
            model.addConstr(expr, LESS_EQUAL, distance, null);

            expr = new GRBLinExpr();
            expr.addTerm(-1.0, shop.xVar());
            expr.addTerm(1.0, house.xVar());
            expr.addTerm(-1.0, shop.yVar());
            expr.addTerm(1.0, house.yVar());
            expr.addTerm(-bigM, pair.excluded());
            model.addConstr(expr, LESS_EQUAL, distance, null);

            expr = new GRBLinExpr();
            expr.addTerm(1, house.included());
            expr.addTerm(1, shop.included());
            expr.addTerm(bigM, pair.excluded());
            model.addConstr(expr, GREATER_EQUAL, 2, null);
        } catch (GRBException e) {
            throw new RuntimeException(e);
        }
    }
}
