package shmarovfedor.areaplanning.util;

import java.awt.Color;
import java.util.Random;

// TODO: Auto-generated Javadoc
/**
 * The Class Building.
 */
public class Building{
	
	/** The width. */
	private final double width;
	
	/** The height. */
	private final double height;
	
	/** The x. */
	private double x;
	
	/** The y. */
	private double y;
	
	/** The area. */
	private final double area;
	
	/** The benefit. */
	private final double benefit;
	
	/** The color. */
	private final Color color;
	
	/** The name. */
	private final String name;
	
	/**
	 * Instantiates a new building.
	 *
	 * @param width the width
	 * @param height the height
	 * @param benefit the benefit
	 * @param color the color
	 * @param name the name
	 */
	public Building(double width, double height, double benefit, Color color, String name) {
		if ((width <= 0) || (height <= 0) || (benefit <= 0)) throw new IllegalArgumentException("Width, length or benefit must be positive");
		if (color.equals(null)) throw new IllegalArgumentException("Color is null");
		if (name.equals("")) throw new IllegalArgumentException("Name of the building must not be empty");
		this.width = width;
		this.height = height;
		this.benefit = benefit;
		this.area = width * height;
		this.color = color;
		this.name = name;
	}
	
	/**
	 * Generate random color.
	 *
	 * @return the color
	 */
	public static Color generateRandomColor() {
		Random random = new Random();
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
	}
	
	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x.
	 *
	 * @param x the new x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y.
	 *
	 * @param y the new y
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public double getArea() {
		return area;
	}

	/**
	 * Gets the benefit.
	 *
	 * @return the benefit
	 */
	public double getBenefit() {
		return benefit;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	

}
