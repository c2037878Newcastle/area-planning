package shmarovfedor.api.util;

/**
 * The Class Point.
 */
public class Point {

	/** The x. */
	private final double x;
	
	/** The y. */
	private final double y;
	
	/**
	 * Instantiates a new point.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double getY() {
		return this.y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
        result = prime * result + Double.hashCode(x);
        result = prime * result + Double.hashCode(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	
}
