package shmarovfedor.api.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shmarovfedor.api.model.RegionManager;

public class MainPanel extends JPanel{

	private MapPanel mapPanel;
	private JPanel buttonPanel;
	private JPanel zoomPanel;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton leftButton;
	private JButton rightButton;
	private JButton upButton;
	private JButton downButton;
	private JButton clearButton;
	private JButton addMainPolygonButton;
	private JButton addExclusivePolygonButton;
	
	public MainPanel() {
		super();
		
		mapPanel = new MapPanel(500, 500);
		buttonPanel = new JPanel();
		zoomPanel = new JPanel();
	
		zoomInButton = new JButton("+");
		zoomOutButton = new JButton("-");
		leftButton = new JButton("<");
		rightButton = new JButton(">");
		upButton = new JButton("^");
		downButton = new JButton("v");
		clearButton = new JButton("CLEAR");
		addMainPolygonButton = new JButton("Main polygon");
		addExclusivePolygonButton = new JButton("Exclude polygon");
		
		zoomInButton.addActionListener(e -> mapPanel.zoomIn());
		
		zoomOutButton.addActionListener(e -> mapPanel.zoomOut());
		
		leftButton.addActionListener(e -> mapPanel.moveLeft());
		
		rightButton.addActionListener(e -> mapPanel.moveRight());
		
		upButton.addActionListener(e -> mapPanel.moveUp());
		
		downButton.addActionListener(e -> mapPanel.moveDown());
		
		clearButton.addActionListener(e -> RegionManager.clearPoints());
		
		addMainPolygonButton.addActionListener(e -> {
            if (!RegionManager.createPolygon()) JOptionPane.showMessageDialog(new JFrame(), "The polygon you entered is not convex");
        });
		
		addExclusivePolygonButton.addActionListener(e -> {
            if (!RegionManager.addExclusivePolygon()) JOptionPane.showMessageDialog(new JFrame(), "The polygon you entered is not convex");
        });
		
		zoomPanel.add(addMainPolygonButton);
		zoomPanel.add(addExclusivePolygonButton);
		zoomPanel.add(zoomInButton);
		zoomPanel.add(zoomOutButton);
		zoomPanel.add(clearButton);
		
		buttonPanel.setLayout(new BorderLayout());
		
		buttonPanel.add(leftButton, BorderLayout.WEST);
		buttonPanel.add(downButton, BorderLayout.SOUTH);
		buttonPanel.add(zoomPanel, BorderLayout.CENTER);
		buttonPanel.add(upButton, BorderLayout.NORTH);
		buttonPanel.add(rightButton, BorderLayout.EAST);
		
		buttonPanel.setBackground(Color.RED);
		
		setLayout(new BorderLayout());

		add(mapPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void saveToFile(String filename) {
		mapPanel.saveToFile(filename);
	}

}
