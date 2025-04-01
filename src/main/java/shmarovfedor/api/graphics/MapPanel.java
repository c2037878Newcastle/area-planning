package shmarovfedor.api.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shmarovfedor.api.model.RegionManager;
import shmarovfedor.api.model.SolutionManager;
import shmarovfedor.api.util.Building;
import shmarovfedor.api.util.Point;
import shmarovfedor.api.util.Polygon;

// TODO: Auto-generated Javadoc
/**
 * The Class MapPanel.
 */
public class MapPanel extends JPanel{
	
	/** The Constant BORDER_GAP. */
	private static final int BORDER_GAP = 0;
	
	/** The Constant NET_GAP. */
	private static final int NET_STEP = 10;

	/** The Constant BORDER_COLOR. */
	private static final Color BORDER_COLOR = Color.GRAY;
	
	/** The Constant NET_COLOR. */
	private static final Color NET_COLOR = Color.GRAY;
	
	private static final Color LABEL_COLOR = Color.GRAY;
	
	/** The Constant POLYGON_COLOR. */
	private static final Color POLYGON_COLOR = Color.RED;
	
	/** The Constant EXCLUSIVE_POLYGON_COLOR. */
	private static final Color EXCLUSIVE_POLYGON_COLOR = Color.BLUE;
	
	/** The Constant BUILDING_COLOR. */
	private static final Color BUILDING_COLOR = Color.BLACK;
	
	/** The Constant BUILDING_FILL. */
	//private static final Color BUILDING_FILL = Color.GREEN;
	
	private static final Color POINT_COLOR = Color.BLACK;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The left border. */
	private int leftBound;
	
	/** The right border. */
	private int rightBound;
	
	/** The top border. */
	private int topBound;
	
	/** The bottom border. */
	private int bottomBound;
	
	/** The x scale. */
	private double xScale;
	
	/** The y scale. */
	private double yScale;
	
	private double scale;
	
	/**
	 * Instantiates a new map panel.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public MapPanel(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.leftBound = BORDER_GAP;
		this.rightBound = width - BORDER_GAP;
		this.topBound = BORDER_GAP;
		this.bottomBound = height - BORDER_GAP;
		
		if (RegionManager.getPoints().size() != 0) calculateScale(); else scale = 2.5;

		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(width, height));
		
		addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent event){
				
				double x = (event.getX() - leftBound) / scale;
				double y = (bottomBound - event.getY()) / scale;

				RegionManager.addPoint(new Point(x, y));
			}
		});
		
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBorder(g);
		drawNet(g);
		drawPoints(g);
		
		if (RegionManager.getPolygon() != null) drawPolygon(g);
		if (RegionManager.getExclusivePolygons().size() > 0) drawExclusivePolygons(g);
		if (SolutionManager.getAll().size() != 0) drawBuildings(g);

		repaint();
	}
	
	public void saveToFile(String filename) {

		try {
            BufferedImage i = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = i.getGraphics();
            paint(g);
            ImageIO.write(i, "png", new File(filename + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void drawPoints(Graphics g) {
		g.setColor(POINT_COLOR);
		List<Point> point = RegionManager.getPoints();
		for (int i = 0; i < point.size(); i++) g.fillOval((int)(scale * point.get(i).getX() + leftBound) - 5, 
															(int)(bottomBound - scale * point.get(i).getY()) - 5, 10, 10);
	}
	
	/**
	 * Draw border.
	 *
	 * @param g the g
	 */
	public void drawBorder(Graphics g) {
		g.setColor(BORDER_COLOR);
		g.drawLine(BORDER_GAP, BORDER_GAP, width - BORDER_GAP, BORDER_GAP);
		g.drawLine(width - BORDER_GAP, BORDER_GAP, width - BORDER_GAP, height - BORDER_GAP);
		g.drawLine(width - BORDER_GAP, height - BORDER_GAP, BORDER_GAP, height - BORDER_GAP);
		g.drawLine(BORDER_GAP, height - BORDER_GAP, BORDER_GAP, BORDER_GAP);

	}
	
	/**
	 * Draw net.
	 *
	 * @param g the g
	 */
	public void drawNet(Graphics g) {
		g.setColor(NET_COLOR);
		for(int position = width - BORDER_GAP; position > BORDER_GAP ; position -= NET_STEP * scale) {
			g.drawLine(position, BORDER_GAP, position, height - BORDER_GAP);
			g.drawString(String.valueOf(Math.round((position - leftBound) / scale)), position, height);
		}
		
		for(int position = height - BORDER_GAP; position > BORDER_GAP; position -= NET_STEP * scale) {
			g.drawLine(BORDER_GAP, position, width - BORDER_GAP, position);
			g.drawString(String.valueOf(Math.round((bottomBound - position) / scale)), BORDER_GAP, position);
		}
	
	}
	
	public void drawLabels(Graphics g) {
		g.setColor(LABEL_COLOR);
		for(int position = BORDER_GAP; position < width - BORDER_GAP; position += NET_STEP * scale) {
			JLabel label = new JLabel(String.valueOf(leftBound + position));
			label.setBounds(position, height, 50, 10);
			add(label);
		}
	}
	
	/**
	 * Draw polygon.
	 *
	 * @param g the g
	 */
	public void drawPolygon(Graphics g) {

		Polygon polygon = RegionManager.getPolygon();
		
		double[] x = polygon.getX();
		double[] y = polygon.getY();
		
		g.setColor(POLYGON_COLOR);
		Graphics2D g2D = (Graphics2D) g;
		g2D.setStroke(new BasicStroke(2));
		for (int i = 0; i < x.length - 1; i++) 
			g2D.drawLine((int)(scale * x[i] + leftBound), 
						(int)(bottomBound - scale * y[i]), 
						(int)(scale * x[i + 1] + leftBound), 
						(int)(bottomBound - scale * y[i + 1]));
		
		g2D.drawLine((int)(scale * x[0] + leftBound), 
				(int)(bottomBound - scale * y[0]), 
				(int)(scale * x[x.length - 1] + leftBound), 
				(int)(bottomBound - scale * y[y.length - 1]));
	}
	
	public void drawExclusivePolygons(Graphics g) {
		
		List<Polygon> polygons = RegionManager.getExclusivePolygons();
		for (int i = 0; i < polygons.size(); i++) {
			double[] x = polygons.get(i).getX();
			double[] y = polygons.get(i).getY();
			
			g.setColor(EXCLUSIVE_POLYGON_COLOR);
			Graphics2D g2D = (Graphics2D) g;
			g2D.setStroke(new BasicStroke(2));
			
			for (int j = 0; j < x.length - 1; j++) 
				g2D.drawLine((int)(scale * x[j] + leftBound), 
							(int)(bottomBound - scale * y[j]), 
							(int)(scale * x[j + 1] + leftBound), 
							(int)(bottomBound - scale * y[j + 1]));
			
			g2D.drawLine((int)(scale * x[0] + leftBound), 
					(int)(bottomBound - scale * y[0]), 
					(int)(scale * x[x.length - 1] + leftBound), 
					(int)(bottomBound - scale * y[y.length - 1]));
		}
	}
	
	
	/**
	 * Draw buildings.
	 *
	 * @param g the g
	 */
	public void drawBuildings(Graphics g) {
		List<Building> building = SolutionManager.getLast();
		for (int i = 0; i < building.size(); i++) {
			g.setColor(building.get(i).getColor());
			g.fillRect((int)(scale * (building.get(i).getX() - building.get(i).getWidth() / 2) + leftBound),
					(int)(bottomBound - scale * (building.get(i).getY() + building.get(i).getHeight() / 2)), 
					(int)(scale * building.get(i).getWidth()), 
					(int)(scale * building.get(i).getHeight()));
			g.setColor(BUILDING_COLOR);
			g.drawRect((int)(scale * (building.get(i).getX() - building.get(i).getWidth() / 2) + leftBound),
						(int)(bottomBound - scale * (building.get(i).getY() + building.get(i).getHeight() / 2)), 
						(int)(scale * building.get(i).getWidth()), 
						(int)(scale * building.get(i).getHeight()));
		}
	}
	
	/**
	 * Calculate scale.
	 */
	private void calculateScale() {
		List<Point> point = RegionManager.getPoints();
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for (int i = 0; i < point.size(); i++) {
			if (point.get(i).getX() < minX) minX = point.get(i).getX();
			if (point.get(i).getX() > maxX) maxX = point.get(i).getX();
			if (point.get(i).getY() < minY) minY = point.get(i).getY();
			if (point.get(i).getY() > maxY) maxY = point.get(i).getY();
		}
		
		this.xScale = (width - 2 * BORDER_GAP) / (maxX - minX);
		this.yScale = (height - 2 * BORDER_GAP) / (maxY - minY);
		
		scale = xScale;
		if (xScale >= yScale) scale = yScale;
	}
	
	public void zoomIn() {
		scale += 0.1;
		repaint();
	}
	
	public void zoomOut() {
		if (scale <= 0.1) return;
		scale -= 0.1;
		repaint();
	}
	
	public void moveLeft() {
		leftBound -= NET_STEP * scale;
		rightBound -= NET_STEP * scale;
	}
	
	public void moveRight() {
		leftBound += NET_STEP * scale;
		rightBound += NET_STEP * scale;
	}
	
	public void moveUp() {
		topBound -= NET_STEP * scale;
		bottomBound -= NET_STEP * scale;
	}
	
	public void moveDown() {
		topBound += NET_STEP * scale;
		bottomBound += NET_STEP * scale;
	}
	
}
