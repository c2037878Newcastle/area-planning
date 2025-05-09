package shmarovfedor.api.model;


import java.util.ArrayList;
import java.util.List;

import shmarovfedor.api.util.Point;
import shmarovfedor.api.util.Polygon;

/**
 * The Class RegionManager.
 */
public class RegionManager {
	
	/** The points. */
	private static List<Point> points = new ArrayList<>();
	
	/** The polygon. */
	private static Polygon polygon;
	
	/** The exclusive polygon. */
	private static List<Polygon> exclusivePolygon = new ArrayList<>();
	
	/**
	 * Adds the point.
	 *
	 * @param point the point
	 * @return true, if successful
	 */
	public static boolean addPoint(Point point) {
		return points.add(point);		
	}

	/**
	 * Gets the points.
	 *
	 * @return the points
	 */
	public static List<Point> getPoints() {
		return points;
	}

	/**
	 * Clear points.
	 */
	public static void clearPoints() {
		points = new ArrayList<>();
	}
	
	/**
	 * Creates the polygon.
	 */
	public static boolean createPolygon() {
		try {
			polygon = new Polygon(points);
			clearPoints();
			return true;
		} catch(Exception e) {
			clearPoints();
			return false;
		}
	}
	
	/**
	 * Gets the polygon.
	 *
	 * @return the polygon
	 */
	public static Polygon getPolygon() {
		return polygon;
	}
	
	/**
	 * Adds the exclusive polygon.
	 */
	public static boolean addExclusivePolygon() {
		try {
			exclusivePolygon.add(new Polygon(points));
			clearPoints();
			return true;
		} catch(Exception e) {
			clearPoints();
			return false;
		}
	}
	
	/**
	 * Gets the exclusive polygons.
	 *
	 * @return the exclusive polygons
	 */
	public static List<Polygon> getExclusivePolygons() {
		return exclusivePolygon;
	}

}
