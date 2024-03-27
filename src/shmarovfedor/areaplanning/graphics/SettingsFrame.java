package shmarovfedor.areaplanning.graphics;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import shmarovfedor.areaplanning.background.BackgroundWorker;
import shmarovfedor.areaplanning.model.SolutionManager;
import shmarovfedor.areaplanning.solver.OptimizationManager;

public class SettingsFrame extends JFrame{

	public SettingsFrame(int width, int height) {
		super();
		setSize(new Dimension(width, height));
		setResizable(false);
		
		JLabel timeLimitLabel = new JLabel("Time limit:");
		JLabel boundLabel = new JLabel("Bound:");
				
		final JTextField timeLimitTextField = new JTextField();
		timeLimitTextField.setText(String.valueOf(OptimizationManager.getTimeLimit()));
		final JTextField boundTextField = new JTextField();
		boundTextField.setText(String.valueOf(SolutionManager.getLowerBound()));
		
		JButton confirmButton = new JButton("Confirm");
		
		JCheckBox binarySearch = new JCheckBox("Binary", BackgroundWorker.isBinarySearch());
		
		setLayout(new GridLayout(4, 2));
		
		getContentPane().add(timeLimitLabel);
		getContentPane().add(timeLimitTextField);
		getContentPane().add(boundLabel);
		getContentPane().add(boundTextField);
		getContentPane().add(binarySearch);
		getContentPane().add(new JLabel());
		getContentPane().add(new JLabel());
		getContentPane().add(confirmButton);
		
		binarySearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BackgroundWorker.setBinarySearch(!BackgroundWorker.isBinarySearch());				
			}
			
		});
		
		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double timeLimit = Double.parseDouble(timeLimitTextField.getText());
					double bound = Double.parseDouble(boundTextField.getText());
					
					//SolutionManager.setCurrentBound(bound);
					SolutionManager.setLowerBound(bound);		
					if (timeLimit > 0) {
						OptimizationManager.setTimeLimit(timeLimit);
						dispose();
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Time limit must be positive");
					}
				
				} catch(NumberFormatException exception) {
					JOptionPane.showMessageDialog(new JFrame(), "Wrong number format. " + exception.getMessage());
				}
			}
			
		});
		
				
	}
	
	
	
}
