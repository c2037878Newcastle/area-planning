package shmarovfedor.api.util;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Polygon.
 */
public class Polygon {
	
	/** The x. */
	private double[] x;
	
	/** The y. */
	private double[] y;
	
	/** The a. */
	private double[] a;
	
	/** The b. */
	private double[] b;
	
	/** The c. */
	private double[] c;
	
	/** The area. */
	private double area;
	
	/**
	 * Instantiates a new polygon.
	 *
	 * @param point the point
	 */
	public Polygon(List<Point> point) {
		if (point == null) throw new IllegalArgumentException("List of points is null");
		if (point.size() < 3) throw new IllegalArgumentException("Less than 3 points");
		setVariables(point);
		calculateCoeff();
		if (!isConvex()) throw new IllegalArgumentException("Polygon is not convex");
		calculateArea();
		calculateSigns();
	}
	
	/**
	 * Calculate area.
	 */
	private void calculateArea() {
		double sum = 0;
		for (int i = 0; i < x.length - 1; i++) sum += x[i] * y[i + 1] - x[i + 1] * y[i];
		sum += x[x.length - 1] * y[0] - x[0] * y[x.length - 1];
		this.area = 0.5 * sum;	
	}
	
	/**
	 * Calculate coeff.
	 */
	private void calculateCoeff() {
		this.a = new double[x.length];
		this.b = new double[x.length];
		this.c = new double[x.length];
		for (int i = 0; i < x.length - 1; i++) {
			a[i] = y[i + 1] - y[i];
			b[i] = x[i] - x[i + 1];
			c[i] = - x[i + 1] * a[i] - y[i + 1] * b[i];
		}
		
		a[x.length - 1] = y[0] - y[x.length - 1];
		b[x.length - 1] = x[x.length - 1] - x[0];
		c[x.length - 1] = - x[0] * a[x.length - 1] - y[0] * b[x.length - 1];
	}
	
	
	/**
	 * Calculate signs.
	 */
	private void calculateSigns() {
		for (int i = 0; i < a.length - 1; i++) {
			if (calculateSign(a[i], b[i], c[i], x[i + 1], y[i + 1]) == 1) {
				a[i] = -a[i];
				b[i] = -b[i];
				c[i] = -c[i];
			}
		}
		if (calculateSign(a[a.length - 1], b[a.length - 1], c[a.length - 1], x[0], y[0]) == 1) {
			a[a.length - 1] = -a[a.length - 1];
			b[a.length - 1] = -b[a.length - 1];
			c[a.length - 1] = -c[a.length - 1];
		}
	}
	
	/**
	 * Calculate sign.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @param x the x
	 * @param y the y
	 * @return the int
	 */
	private int calculateSign(double a, double b, double c, double x, double y) {
		if (a * x + b * y + c > 0) return 1;
		return 0;
	}
	
	/**
	 * Sets the variables.
	 *
	 * @param point the new variables
	 */
	private void setVariables(List<Point> point) {
		this.x = new double[point.size()];
		this.y = new double[point.size()];
		for (int i = 0; i < x.length; i++) {
			x[i] = point.get(i).getX();
			y[i] = point.get(i).getY();
		}
	}
	
	/**
	 * Checks if is convex.
	 *
	 * @return true, if is convex
	 */
	private boolean isConvex() {
		for (int i = 0; i < x.length; i++) {
			int primarySign;
			if (i < x.length - 1) {
				primarySign = calculateSign(a[i], b[i], c[i], x[i + 1], y[i + 1]);
			} else {
				primarySign = calculateSign(a[i], b[i], c[i], x[x.length - 2], y[x.length - 2]);
			}
			for (int j = 0; j < x.length; j++) {
				if (i != j) if (primarySign != calculateSign(a[i], b[i], c[i], x[j], y[j])) return false;
			}
		}
		return true;
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double[] getX() {
		return x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double[] getY() {
		return y;
	}

	/**
	 * Gets the a.
	 *
	 * @return the a
	 */
	public double[] getA() {
		return a;
	}

	/**
	 * Gets the b.
	 *
	 * @return the b
	 */
	public double[] getB() {
		return b;
	}

	/**
	 * Gets the c.
	 *
	 * @return the c
	 */
	public double[] getC() {
		return c;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public double getArea() {
		return area;
	}

}
