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

    public ProgressPanel progressPanel;
    public BuildingPanel buildingPanel;
    private JButton addButton;

    public JButton getAddButton() {
        return addButton;
    }

    private int width;
    private int height;

    public RightPanel(BaseFrame base, int width, int height) {
        super();
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));

        progressPanel = new ProgressPanel(base, width, 180);
        buildingPanel = new BuildingPanel(width, height);


        setLayout(new BorderLayout());

        add(progressPanel, BorderLayout.NORTH);
        add(buildingPanel, BorderLayout.CENTER);

        if (base.problem.allowUserBuildings()) {
            addButton = new JButton("Add");
            add(addButton, BorderLayout.SOUTH);
            addButton.addActionListener(e -> {
                AddBuildingFrame addBuildingFrame = new AddBuildingFrame(base.problem, 200, 200);
                addBuildingFrame.setVisible(true);
            });
        }

        repaint();

    }


}
