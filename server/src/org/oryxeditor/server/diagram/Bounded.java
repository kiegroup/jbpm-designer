package org.oryxeditor.server.diagram;

/**
 * @author Philipp
 * Bounded interface gets implement when spanning an
 * area
 */
public interface Bounded {
	/**
	 * @return Point Object of the upper left
	 */
	public Point getUpperLeft();
	/**
	 * @return Point Object of the lower right
	 */
	public Point getLowerRight();
	
	/**
	 * @return The width of the spanned area
	 */
	public double getWidth();
	
	/**
	 * @return The height of the spanned area
	 */
	public double getHeight();
}
