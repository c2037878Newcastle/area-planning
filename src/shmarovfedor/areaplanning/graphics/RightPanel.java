package shmarovfedor.areaplanning.graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class RightPanel extends JPanel{

	private ProgressPanel progressPanel;
	private BuildingPanel buildingPanel;
	private JButton addButton;
	
	public JButton getAddButton() {
		return addButton;
	}

	public void setAddButton(JButton addButton) {
		this.addButton = addButton;
	}

	private int width;
	private int height;
	
	public RightPanel(int width, int height) {
		super();
		this.width = width;
		this.height = height;

		setPreferredSize(new Dimension(width, height));
		
		progressPanel = new ProgressPanel(width, 140);
		buildingPanel = new BuildingPanel(width, height);
		
		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddBuildingFrame addBuildingFrame = new AddBuildingFrame(200, 200);
				addBuildingFrame.setVisible(true);
			}
			
		});
		
		setLayout(new BorderLayout());
		
		add(progressPanel, BorderLayout.NORTH);
		add(buildingPanel, BorderLayout.CENTER);
		add(addButton, BorderLayout.SOUTH);
		
		repaint();
				
	}
	

	
}
