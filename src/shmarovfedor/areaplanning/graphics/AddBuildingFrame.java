package shmarovfedor.areaplanning.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.util.Building;

public class AddBuildingFrame extends JFrame{
	
	private Color color = Color.WHITE;
	
	public AddBuildingFrame(int width, int height) {
		super();
		setSize(new Dimension(width, height));
		setResizable(false);
		
		JLabel nameLabel = new JLabel("Name:");
		JLabel widthLabel = new JLabel("Width:");
		JLabel heightLabel = new JLabel("Length:");
		JLabel benefitLabel = new JLabel("Benefit:");
		JLabel colorLabel = new JLabel("Color:");
		
		final JTextField nameTextField = new JTextField();
		final JTextField widthTextField = new JTextField();
		final JTextField heightTextField = new JTextField();
		final JTextField benefitTextField = new JTextField();
		JButton chooseButton = new JButton("Choose");
		
		chooseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				color = JColorChooser.showDialog(new JFrame(), "Choose color", Color.WHITE);
			}
			
		});
		
		JButton confirmButton = new JButton("Confirm");
		
		setLayout(new GridLayout(6, 2));
		
		getContentPane().add(nameLabel);
		getContentPane().add(nameTextField);
		getContentPane().add(widthLabel);
		getContentPane().add(widthTextField);
		getContentPane().add(heightLabel);
		getContentPane().add(heightTextField);
		getContentPane().add(benefitLabel);
		getContentPane().add(benefitTextField);
		getContentPane().add(colorLabel);
		getContentPane().add(chooseButton);
		getContentPane().add(confirmButton);
		
		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					double width = Double.parseDouble(widthTextField.getText());
					double height = Double.parseDouble(heightTextField.getText());
					double benefit = Double.parseDouble(benefitTextField.getText());
					String name = nameTextField.getText();
					
					if (BuildingManager.get(name) == null) {
						try {
							BuildingManager.add(new Building(width, height, benefit, color, name));
							dispose();	
						} catch(IllegalArgumentException exception) {
							JOptionPane.showMessageDialog(new JFrame(), exception.getMessage());	
						}
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Building with this name already exists");
					}
				} catch(NumberFormatException exception) {
					JOptionPane.showMessageDialog(new JFrame(), "Wrong number format " + exception.getLocalizedMessage());
				}

			}
			
		});
		
	}

}
