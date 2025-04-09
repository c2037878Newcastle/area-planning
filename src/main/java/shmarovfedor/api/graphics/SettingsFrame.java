package shmarovfedor.api.graphics;

import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.background.BackgroundWorker;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame extends JFrame {

    private final Problem problem;

    public SettingsFrame(Problem problem, int width, int height) {
        super();
        this.problem = problem;
        setSize(new Dimension(width, height));
        setResizable(false);

        JLabel timeLimitLabel = new JLabel("Time limit:");
        JLabel boundLabel = new JLabel("Bound:");

        final JTextField timeLimitTextField = new JTextField();
        timeLimitTextField.setText(String.valueOf(problem.optimizer().getTimeLimit()));
        final JTextField boundTextField = new JTextField();
        boundTextField.setText(String.valueOf(SolutionManager.getLowerBound()));

        JButton confirmButton = new JButton("Confirm");

        JCheckBox binarySearch = new JCheckBox("Binary");
        problem
                .worker()
                .map(BackgroundWorker::isBinarySearch)
                .ifPresent(binarySearch::setSelected);

        setLayout(new GridLayout(4, 2));

        getContentPane().add(timeLimitLabel);
        getContentPane().add(timeLimitTextField);
        getContentPane().add(boundLabel);
        getContentPane().add(boundTextField);
        getContentPane().add(binarySearch);
        getContentPane().add(new JLabel());
        getContentPane().add(new JLabel());
        getContentPane().add(confirmButton);

        binarySearch.addActionListener(e ->
                problem.worker().ifPresent(w ->
                        w.setBinarySearch(!w.isBinarySearch())
                )
        );

        confirmButton.addActionListener(e -> {
            try {
                double timeLimit = Double.parseDouble(timeLimitTextField.getText());
                double bound = Double.parseDouble(boundTextField.getText());

                //SolutionManager.setCurrentBound(bound);
                SolutionManager.setLowerBound(bound);
                if (timeLimit > 0) {
                    problem.optimizer().setTimeLimit(timeLimit);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Time limit must be positive");
                }

            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Wrong number format. " + exception.getMessage());
            }
        });


    }


}
