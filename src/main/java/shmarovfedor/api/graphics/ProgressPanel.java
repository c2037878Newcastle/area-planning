package shmarovfedor.api.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.areaplanning.solver.Optimizer;

public class ProgressPanel extends JPanel{
	
	/** The Constant BORDER_COLOR. */
	private static final Color BORDER_COLOR = Color.GRAY;
	
	/** The Constant BORDER_GAP. */
	private static final int BORDER_GAP = 0;
	
	private int width;
	private int height;
	
	public ProgressPanel(int width, int height) {
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
	}

	public void drawBorder(Graphics g) {
		g.setColor(BORDER_COLOR);
		g.drawLine(BORDER_GAP, BORDER_GAP, width - BORDER_GAP, BORDER_GAP);
		g.drawLine(width - BORDER_GAP, BORDER_GAP, width - BORDER_GAP, height - BORDER_GAP);
		g.drawLine(width - BORDER_GAP, height - BORDER_GAP, BORDER_GAP, height - BORDER_GAP);
		g.drawLine(BORDER_GAP, height - BORDER_GAP, BORDER_GAP, BORDER_GAP);

	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBorder(g);
		drawBenefit(g);
		drawLowerBound(g);
		drawCurrentBound(g);
		drawUpperBound(g);
		drawSolutionAmount(g);
		drawStatus(g);
		repaint();
	}
	
	public void drawLowerBound(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Lower bound: " + SolutionManager.getLowerBound(), 10, 40);
	}
	
	public void drawCurrentBound(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Current bound: " + SolutionManager.getCurrentBound(), 10, 60);
	}
	
	public void drawUpperBound(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Upper bound: " + SolutionManager.getUpperBound(), 10, 80);
	}
	
	public void drawBenefit(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Benefit: " + SolutionManager.getObjective(), 10, 20);
	}
	
	public void drawSolutionAmount(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(SolutionManager.getAll().size() + " solutions were found", 10, 100);
	}
	
	public void drawStatus(Graphics g) {
		g.setColor(Color.BLACK);
		
		
		String status = null;
		
		switch(Optimizer.getStatus()) {
		
			case 0:
				status = "Waiting";
				break;
				
			case 1:
				status = "Initialization";
				break;
				
			case 2:
				status = "In progress";
				break;

			case 3:
				status = "Terminated";
				break;

			case 4:
				status = "Recalculation";
				break;
				
		}
		
		g.drawString("Status: " + status, 10, 120);

	}
	
}
