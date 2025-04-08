package shmarovfedor.api.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

import static shmarovfedor.api.util.BuildingType.stream;

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
		drawBuildingTypes(g);
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
	
	public void drawBuildingTypes(Graphics g) {
		var types = stream();
		var position = new AtomicInteger(40);
		types.forEachOrdered(type -> {
			g.setColor(Color.BLACK);
			int pos = position.get();
			g.drawString(type.id() + ": " +
					type.width() + "m x " +
					type.height() + "m x £" +
					type.benefit(), 20, pos);

			g.setColor(type.color());
			g.fillRect(5, pos - 10, 10, 10);

			g.setColor(Color.BLACK);
			g.drawRect(5, pos - 10, 10, 10);

			position.set(pos + 20);
		});		
	}
	
}
