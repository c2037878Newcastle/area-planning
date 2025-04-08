package shmarovfedor;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.areaplanning.AreaProblem;
import uk.co.rhilton.townplanning.TownProblem;

import javax.swing.*;
import java.util.Arrays;

public class RunGUI {

    public static Problem[] PROBLEMS = new Problem[]{ // program argument
            new AreaProblem(), // 0
            new TownProblem() // 1
    };

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                var index = Integer.parseInt(args[0]);
                if (index >= 0 && index < PROBLEMS.length)
                    PROBLEMS[index].initialize();
            } catch (NumberFormatException ignored) {
                main(new String[0]);
            }
        } else {
            var selection = JOptionPane.showInputDialog(
                    null,
                    "Select Problem to Solve",
                    "Problem Selector",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    PROBLEMS,
                    null
            );
            if (selection == null) return;
            var problem = ((Problem) selection);
            problem.initialize();
        }
    }

}
