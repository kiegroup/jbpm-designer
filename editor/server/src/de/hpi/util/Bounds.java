package de.hpi.util;

public class Bounds {
	
	private int x1, x2, y1, y2;

	public Bounds(int x1, int y1, int x2,  int y2) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public Bounds(Point point1, Point point2) {
		this(point1.x, point1.y, point2.x, point2.y);
	}
	
	public String toString(){
		return toString(",");
	}
	
	public String toString(String delimiter){
		return String.valueOf(x1)+delimiter+String.valueOf(y1)+delimiter+String.valueOf(x2)+delimiter+String.valueOf(y2);
	}
	
	public int getX1() {
		return x1;
	}

	public int getX2() {
		return x2;
	}

	public int getY1() {
		return y1;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Calculates center point relatively to bounds.
	 * E.g. new Bounds(1, 5, 3, 9).getCenterRelative() => new Point(2, 3)
	 * @return center point relatively to bounds
	 */
	public Point getCenterRelative(){
		return new Point((x2 - x1)/2, (y2 - y1)/2);
	}
}
