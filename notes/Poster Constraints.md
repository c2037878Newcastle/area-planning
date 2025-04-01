Given a pair of shop $S$ with position $(S_x, S_y)$, and building $B$ with position $(B_x, B_y)$, calculate whether shop and building are within distance $d$ of each other using a **Manhattan Distance** formula. Uses **big-M** method to allow toggling whether these buildings exist in the solution, and whether they should be in range of each other. Let $z_n \in \{0, 1\}$ where $n$ is the shop, building, or shop-building combination be the binary variable to control existing in the solution.
$$
\displaylines{
{S_x}-{B_x}+{S_y}-{B_y}-Mz_{sb}-Mz_s-Mz_b \leq d\\
{S_x}-{B_x}+{B_y}-{S_y}-Mz_{sb}-Mz_s-Mz_b \leq d\\
{S_x}-{B_x}+{S_y}-{B_y}-Mz_{sb}-Mz_s-Mz_b \leq d\\
{S_x}-{B_x}+{B_y}-{S_y}-Mz_{sb}-Mz_s-Mz_b \leq d\\

}
$$
Checking that a shop has at least one building in range, ensuring shop is part of the solution using big-M method.
$$-Mz_s + \sum{z_{sb}}  < |B|$$

Checking that a building has at least one shop in range, ensuring building is part of the solution using big-M method. $$-Mz_b + \sum{z_{sb}} < |S|$$
Checking there is at least one shop and at least one building in the solution. $$
\displaylines{
\sum{z_n}<|S|\\
\sum{z_n}<|B|
}$$
Checking that there are no $(s,b)$ pairs included where $s$ or $b$ is not in the solution. $$\displaylines{
\prod^n{z_{nb}} = z_n\\
\prod^n{z_{sn}} = z_n
}$$

$$
\displaylines{
{euclidean}((x_1, y_1), (x_2,y_2))=\sqrt{(x_1-x_2)^2+(y_1-y_2)^2}\\
{manhattan}((x_1, y_1), (x_2,y_2))=|x_1-x_2|+|y_1-y_2|
}$$

$$
\displaylines{
 x > 10 \\ 
 OR \\
 x<5
 }
$$

$$
\displaylines{
x+Mz_1 > 10\\
AND\\
x-Mz_2 < 5\\
AND\\
z_1+z_2 < 2
}
$$
