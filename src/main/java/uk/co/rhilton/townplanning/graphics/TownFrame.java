package uk.co.rhilton.townplanning.graphics;

import shmarovfedor.api.graphics.BaseFrame;
import shmarovfedor.api.model.SolutionManager;
import uk.co.rhilton.townplanning.TownProblem;

import javax.swing.*;
import java.awt.*;

public class TownFrame extends BaseFrame {

    public TownFrame(TownProblem problem) {
        super(problem);
    }

    public void createGUI() {
        var menu = getJMenuBar();
        var townBar = new JMenu("Town Planning");
        var disclaimer = new JMenuItem("About");
        disclaimer.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    """
                            Town Planning by Ryan Hilton
                            
                            Default Parameters based on data from HM Government.
                            Contains public sector information licensed under the Open Government Licence v3.0.""",
                    "About Program",
                    JOptionPane.PLAIN_MESSAGE
            );
        });
        townBar.add(disclaimer);
        menu.add(townBar);

        var button = new JButton("Reset Bounds");
        button.addActionListener(e -> {
            SolutionManager.setLowerBound(0);
            SolutionManager.setCurrentBound(0);
            SolutionManager.setUpperBound(0);
            SolutionManager.setObjective(0);
        });
        rightPanel.progressPanel.add(button, BorderLayout.SOUTH);
    }

    public void paintProgressPanel(Graphics g) {
        super.paintProgressPanel(g);
    }
}
