# Area Planning

This project aims to maximise the construction profit for a predefined region by formulating the optimisation problem as a [mixed-integer linear programming](https://en.wikipedia.org/wiki/Linear_programming#Integer_unknowns) (MILP) problem and solving it using [Gurobi Optimizer](https://www.gurobi.com/). See my [MSc dissertation](https://shmarov.com/files/fedor-shmarov-msc-dissertation.pdf) for more information.

## Quick Start
1. To get started first obtain a license for the [Gurobi Optimiser](https://www.gurobi.com/) and install the requirements by following the steps outlined [here](https://support.gurobi.com/hc/en-us/articles/14799677517585-Getting-Started-with-Gurobi-Optimizer). If you are an academic user, claim a Named-User Academic license. For commercial or NGO licenses, claim the equivalent license.
2. After claiming a gurobi license and installing this on your machine, clone this repository:
```shell
git clone https://github.com/shmarovfedor/area-planning.git
```
3. If using IntelliJ, add the gurobi.jar file, located in the gurobi install folder as an external library. For all other IDEs, follow the application specific documentation to perform this task.
4. Execute the program by running the [RunGUI](src/shmarovfedor/areaplanning/graphics/use/RunGUI.java) main program.