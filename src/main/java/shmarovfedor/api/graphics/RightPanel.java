package shmarovfedor.api.graphics;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.areaplanning.graphics.AddBuildingFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class RightPanel extends JPanel {

    private ProgressPanel progressPanel;
    private BuildingPanel buildingPanel;
    private JButton addButton;

    public JButton getAddButton() {
        return addButton;
    }

    private int width;
    private int height;

    public RightPanel(Problem problem, int width, int height) {
        super();
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));

        progressPanel = new ProgressPanel(problem, width, 140);
        buildingPanel = new BuildingPanel(width, height);


        setLayout(new BorderLayout());

        add(progressPanel, BorderLayout.NORTH);
        add(buildingPanel, BorderLayout.CENTER);

        if (problem.allowUserBuildings()) {
            addButton = new JButton("Add");
            add(addButton, BorderLayout.SOUTH);
            addButton.addActionListener(e -> {
                AddBuildingFrame addBuildingFrame = new AddBuildingFrame(problem, 200, 200);
                addBuildingFrame.setVisible(true);
            });
        }

        repaint();

    }


}
