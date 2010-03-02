package de.hpi.layouting.model;

import java.util.ArrayList;
import java.util.List;

public class LayoutingDockers {
	public static class Point {
		public double x;
		public double y;

		/**
		 * @param x
		 * @param y
		 */
		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

		/**
		 * 
		 */
		public Point() {
			this(0, 0);
		}

	}

	private List<Point> points = new ArrayList<Point>();

	public void addPoint(double x, double y) {
		points.add(new Point(x, y));
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(double... coords) {
		if (coords.length % 2 != 0) {
			throw new IllegalArgumentException("coords must be of even length");
		}
		points.clear();
		for (int i = 0; i < coords.length; i += 2) {
			this.addPoint(coords[i], coords[i + 1]);
		}
	}

}
