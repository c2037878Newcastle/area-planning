package shmarovfedor.api.graphics;

import java.awt.*;

import javax.swing.JPanel;

import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.problem.Problem;

public class ProgressPanel extends JPanel{
	
	/** The Constant BORDER_COLOR. */
	private static final Color BORDER_COLOR = Color.GRAY;
	
	/** The Constant BORDER_GAP. */
	private static final int BORDER_GAP = 0;
	
	private int width;
	private int height;

	private final BaseFrame base;
	
	public ProgressPanel(BaseFrame base, int width, int height) {
		this.base = base;
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
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
		base.paintProgressPanel(g);
		repaint();
	}
	
	public void drawLowerBound(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Lower bound: £" + SolutionManager.getLowerBound(), 10, 40);
	}
	
	public void drawCurrentBound(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Current bound: £" + SolutionManager.getCurrentBound(), 10, 60);
	}
	
	public void drawUpperBound(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Upper bound: £" + SolutionManager.getUpperBound(), 10, 80);
	}
	
	public void drawBenefit(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Benefit: £" + SolutionManager.getObjective(), 10, 20);
	}
	
	public void drawSolutionAmount(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(SolutionManager.getAll().size() + " solutions were found", 10, 100);
	}
	
	public void drawStatus(Graphics g) {
		g.setColor(Color.BLACK);
		
		
		var status = base.problem
				.optimizer()
				.getStatus();

		var color = g.getColor();
		g.setColor(status.color());
		g.drawString("Status: " + status.displayName(), 10, 120);
		g.setColor(color);
	}
	
}
