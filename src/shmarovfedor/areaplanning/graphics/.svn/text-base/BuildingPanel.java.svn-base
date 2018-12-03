package shmarovfedor.areaplanning.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.util.Building;

public class BuildingPanel extends JPanel{

	/** The Constant BORDER_COLOR. */
	private static final Color BORDER_COLOR = Color.GRAY;
	
	/** The Constant BORDER_GAP. */
	private static final int BORDER_GAP = 0;
	
	private int width;
	private int height;
	
	public BuildingPanel(int width, int height) {
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBorder(g);
		drawHeader(g);
		drawBuildings(g);
		repaint();
	}
		
	public void drawBorder(Graphics g) {
		g.setColor(BORDER_COLOR);
		g.drawLine(BORDER_GAP, BORDER_GAP, width - BORDER_GAP, BORDER_GAP);
		g.drawLine(width - BORDER_GAP, BORDER_GAP, width - BORDER_GAP, height - BORDER_GAP);
		g.drawLine(width - BORDER_GAP, height - BORDER_GAP, BORDER_GAP, height - BORDER_GAP);
		g.drawLine(BORDER_GAP, height - BORDER_GAP, BORDER_GAP, BORDER_GAP);

	}
	
	public void drawHeader(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Buildings:", 10, 20);
	}
	
	public void drawBuildings(Graphics g) {
		List<Building> building = BuildingManager.getAll();
		int position = 40;
		for (int i = 0; i < building.size(); i++) {
			g.setColor(Color.BLACK);
			g.drawString(building.get(i).getName() + 
							building.get(i).getWidth() + "x" + 
							building.get(i).getHeight() + "x" + 
							building.get(i).getBenefit(), 20, position);

			g.setColor(building.get(i).getColor());
			g.fillRect(5, position - 10, 10, 10);	
			
			g.setColor(Color.BLACK);
			g.drawRect(5, position - 10, 10, 10);
		
			position += 20;
		}
		
	}
	
}
