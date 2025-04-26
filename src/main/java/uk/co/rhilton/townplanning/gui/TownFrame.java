package uk.co.rhilton.townplanning.gui;

import shmarovfedor.api.graphics.BaseFrame;
import shmarovfedor.api.problem.Problem;
import uk.co.rhilton.townplanning.TownProblem;

import javax.swing.*;

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
    }
}
