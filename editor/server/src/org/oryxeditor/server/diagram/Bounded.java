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
	public Point lowerRight();
}
