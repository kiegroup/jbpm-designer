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
package de.unihannover.se.infocup2008.bpmn.layouter.decorator;

import de.unihannover.se.infocup2008.bpmn.model.BPMNBounds;

/**
 * AbstractDecorator implements just delegation for all methods.
 * 
 * @author Team Royal Fawn 
 * 
 */
public abstract class AbstractDecorator implements BPMNBounds {

	
	private BPMNBounds target;
	
	/**
	 * @param target
	 */
	protected AbstractDecorator(BPMNBounds target) {
		super();
		this.target = target;
	}
	
	/**
	 * @return
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNBounds#getHeight()
	 */
	public double getHeight() {
		return target.getHeight();
	}



	/**
	 * @return
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNBounds#getWidth()
	 */
	public double getWidth() {
		return target.getWidth();
	}



	/**
	 * @return
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNBounds#getX()
	 */
	public double getX() {
		return target.getX();
	}



	/**
	 * @return
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNBounds#getX2()
	 */
	public double getX2() {
		return getX() + getWidth();
	}



	/**
	 * @return
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNBounds#getY()
	 */
	public double getY() {
		return target.getY();
	}



	/**
	 * @return
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNBounds#getY2()
	 */
	public double getY2() {
		return getY() + getHeight();
	}



	@Override
	public String toString() {
		String out = " x=" + getX();
		out += " y=" + getY();
		out += " width=" + getWidth();
		out += " height=" + getHeight();
		return out;
	}

}
