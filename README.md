# Area Planning

This project aims to maximise the construction profit for a predefined region by 
formulating the optimisation problem as a [mixed-integer linear programming](https://en.wikipedia.org/wiki/Linear_programming#Integer_unknowns)
(MILP) problem and solving it using [Gurobi Optimizer](https://www.gurobi.com/).
See my [MSc dissertation](https://shmarov.com/files/fedor-shmarov-msc-dissertation.pdf) for more information.

## Quick Start
1. To get started first obtain a license for the [Gurobi Optimiser](https://www.gurobi.com/)
and install the requirements by following the steps outlined [here](https://support.gurobi.com/hc/en-us/articles/14799677517585-Getting-Started-with-Gurobi-Optimizer).
> If you are an academic user obtain either the Named-User Academic License (Linux: only works on Academic Networks)
> or a WLS Academic License (requires Internet Access to run).  

> For commercial or NGO licenses, claim the equivalent license.
2. Clone this repository
```shell
git clone https://github.com/c2037878Newcastle/area-planning.git
```
3. Import the gradle project into IntelliJ IDEA by opening the `build.gradle.kts` file.
4. Execute the program with `./gradlew run` or using the associated IntelliJ Gradle task.
