/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.hpi.layouting.model;

/**
 * Implements the basic geometry of an element
 * 
 * @author Team Royal Fawn
 * 
 */
public class LayoutingBoundsImpl implements LayoutingBounds {

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public LayoutingBoundsImpl(double x, double y, double width, double height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public LayoutingBoundsImpl() {
		this(0, 0, 0, 0);
	}

	private double x = 0;
	private double y = 0;
	private double width = 0;
	private double height = 0;

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the x2
	 */
	public double getX2() {
		return getX() + getWidth();
	}

	/**
	 * @return the y2
	 */
	public double getY2() {
		return getY() + getHeight();
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	public String toString() {
		String out = " x=" + getX();
		out += " y=" + getY();
		out += " width=" + getWidth();
		out += " height=" + getHeight();
		return out;
	}
}
