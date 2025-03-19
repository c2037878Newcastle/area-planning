# Distance Calculations
Standard way to calculate distance is to use Euclidean Distance:
$$distance((x_1, y_1),(x_2, y_2)) = \sqrt{(x_1-x_2)^2+(y_1 - y_2)^2}$$

Due to the limitations of the Gurobi solver, euclidean distance is not suitable for use. This is because it relies on the operations of squaring and square rooting, which are not present in linear integer programming problems. Therefore, the Manhattan Distance calculations will be used instead.
$$distance((x_1, y_1),(x_2, y_2)) = |x_1 - x_2| + |y_1 - y_2|$$


### Expanding Distance Modulus

First, I tried using a similar technique to the “non-overlapping” constraint from the reference paper, expanding the modulus by splitting into two constraints per axis, assuming we want the distance to be less 100 (meters). This is done by providing the positive multiplications of both modulus parts.
$$
\displaylines{
x_1 - x_2 < 100\\
-x_1 + x_2 < 100\\
y_1 - y_2 < 100\\
-y_1 +y_2 < 100}
$$
Using these combined constraints in conjunction mode (tested in Excel), can provide the correct answer for some cases, but not all. 
If $(99, 2)$ and $(0,0)$ are used, the original Manhattan formula will return FALSE, as there is a distance of 101. However, as neither component on their own is greater than 100, therefore these constraints do not hold up, and return TRUE.

This can be corrected by combining the X and Y constraints into a single combination, with both combinations:
$$
\displaylines{
x_1 - x_2 + y_1 - y_2 < 100\\
-x_1 + x_2 + y_1 - y_2 < 100\\
x_1 - x_2 - y_1 + y_2 < 100\\
-x_1 + x_2 - y_1 + y_2 < 100}
$$
  
Now simulating the same example as previous, shows that the first constraint fails, and due to the conjunction, the result is FALSE. Ensuring the distance between the two does not cause an overlap is not needed here, as this is handled by generic building distance calculations.

This method can now be used to calculate minimum distance requirements for the building types.
## Big-M method

Additionally, the solver requires the use of conjunctive form only, so constraints must be converted into a form that can be provided to the solver. The way the reference project did this was by using the big M method.

For example, the current solution would not allow multiple shops with different areas, so big-M must be introduced.

Take $(x_1, y_1)$ to be a house. Take $(x_2, y_2), (x_3, y_3)$ to be two shops.

$$
\displaylines{
x_1 - x_2 + y_1 - y_2 - Mz_1 < 100\\
-x_1 + x_2 + y_1 - y_2 - Mz_1 < 100\\
x_1 - x_2 - y_1 + y_2 - Mz_1 < 100\\
-x_1 + x_2 - y_1 + y_2 - Mz_1< 100\\
x_1 - x_3 + y_1 - y_3 - Mz_2< 100\\
-x_1 + x_3 + y_1 - y_3 - Mz_2 < 100\\
x_1 - x_3 - y_1 + y_3 - Mz_2 < 100\\
-x_1 + x_3 - y_1 + y_3 - Mz_2< 100\\
z_1 + z_2 < 2
}
$$
Here, $z_1, z_2$ act as binary variables for the big M value, which is set arbitrarily large, such that it's value will always negate any other combination of values. You can read a TRUE $z_n$ value as representing the disablement of that building. The z constraint ensures that not all buildings are disabled with the value 2 representing the total number of potential shops - meaning always at least one must be in the solution.

# Example Buildings and Distances
Note: Example values to test logic, not grounded in reality (yet).

| Builidng       | Size ($x \times y$) | Value    | Justification                                             | Maximum distance from residence |
| -------------- | ------------------- | -------- | --------------------------------------------------------- | ------------------------------- |
| Residence      | 10 x 10             | £1000    | Assume rented, owned by the local authority.              | N/A                             |
| Shop           | 20 x 15             | £100     | Tax provided to LA based on profits. (Low profit margins) | 200 metres                      |
| Doctor Surgery | 25 x 25             | -£50,000 | Does not make a profit, required LA provided service.     | 1,000 metres                    |
| Employment     | 25 x 25             | £5000    | Tax provided to LA based on profits and levys.            | 500 metres                      |
| Park           | 50 x 20             | £-5000   | Low maintenance costs, LA funded amenity.                 | 500 metres                      |
