package shmarovfedor.areaplanning.graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import shmarovfedor.areaplanning.background.BackgroundWorker;
import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.model.RegionManager;
import shmarovfedor.areaplanning.model.SolutionManager;
import shmarovfedor.areaplanning.util.Building;
import shmarovfedor.areaplanning.solver.OptimizationManager;
import shmarovfedor.areaplanning.util.Polygon;

public class MainFrame extends JFrame{
	
	private MainPanel mainPanel;
	private RightPanel rightPanel;
	private JPanel buttonPanel;
	private JButton startButton;
	private JButton terminateButton;
	private JButton saveButton;
	private JButton settingsButton;
	private JButton skipButton;
	private final Timer timer = new Timer(20, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			setButtons();			
		}
		
	});
	
	public MainFrame() {
		super();
		
		setSize(new Dimension());
		setResizable(false);
		setTitle("Packing Manager");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new MainPanel();
		rightPanel = new RightPanel(200, 500);
		
		buttonPanel = new JPanel();
		
		startButton = new JButton("Start");
		skipButton = new JButton("Skip");
		skipButton.setEnabled(false);
		terminateButton = new JButton("Terminate");
		terminateButton.setEnabled(false);
		saveButton = new JButton("Save As");
		settingsButton = new JButton("Settings");
		
		timer.start();
		
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				SolutionManager.clear();

				Polygon polygon = RegionManager.getPolygon();
				
				List<Building> building = BuildingManager.getAll();
				
				Building[] buildings = new Building[building.size()];
				
				for (int i = 0; i < buildings.length; i++) buildings[i] = building.get(i);

				OptimizationManager.create(polygon, buildings);
				(new BackgroundWorker()).execute();
			}
			
		});
		
		skipButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OptimizationManager.terminate();
				OptimizationManager.setCorrectTermination(false);
			}
			
		});
		
		terminateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OptimizationManager.terminateExecution();
			}
			
		});
		
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.saveToFile(String.valueOf(System.currentTimeMillis()));
			}

		});
		
		settingsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsFrame settingsFrame = new SettingsFrame(200, 200);
				settingsFrame.setVisible(true);
				
			}
			
		});
		
		buttonPanel.add(startButton);
		buttonPanel.add(skipButton);
		buttonPanel.add(terminateButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(settingsButton);
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		getContentPane().add(mainPanel, constraints);
		
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.gridx = 1;
		constraints.gridy = 0;
		getContentPane().add(rightPanel, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		getContentPane().add(buttonPanel, constraints);

		pack();

	}
	
	public void setButtons() {
		
		switch(OptimizationManager.getStatus()) {
		
			case 0:
				startButton.setEnabled(true);
				skipButton.setEnabled(false);
				terminateButton.setEnabled(false);
				settingsButton.setEnabled(true);
				rightPanel.getAddButton().setEnabled(true);
				break;
				
			case 1:
				startButton.setEnabled(false);
				skipButton.setEnabled(false);
				terminateButton.setEnabled(true);
				settingsButton.setEnabled(false);
				rightPanel.getAddButton().setEnabled(false);
				break;
				
			case 2:
				startButton.setEnabled(false);
				if (!BackgroundWorker.isBinarySearch()) skipButton.setEnabled(false); else skipButton.setEnabled(true);
				terminateButton.setEnabled(true);
				settingsButton.setEnabled(false);
				rightPanel.getAddButton().setEnabled(false);
				break;
				
			case 3:
				startButton.setEnabled(true);
				skipButton.setEnabled(false);
				terminateButton.setEnabled(false);
				settingsButton.setEnabled(true);
				rightPanel.getAddButton().setEnabled(true);
				break;
				
		}
		
	}
	
	
}
