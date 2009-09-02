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

import de.hpi.layouting.model.LayoutingBounds;

/**
 * This decorator describes, how an attached event is positioned relative to the
 * task
 * 
 * @author Team Royal Fawn
 * 
 */
public class DocketEventDecorator extends AbstractDecorator {

	private LayoutingBounds relative;

	private int positionFromLeft;

	public DocketEventDecorator(LayoutingBounds target, LayoutingBounds relative,
			int positionFromLeft) {
		super(target);
		this.relative = relative;
		this.positionFromLeft = positionFromLeft;
	}

	@Override
	public double getX() {
		double firstX = this.relative.getX()
				+ LayoutConstants.EVENT_DOCKERS_MARGIN;
		// + LayoutConstants.EVENT_DIAMETER;

		double relPostion = this.positionFromLeft
				* (LayoutConstants.EVENT_DOCKERS_MARGIN + LayoutConstants.EVENT_DIAMETER);
		double newX = firstX + relPostion;
		return newX;
	}

	@Override
	public double getY() {
		return this.relative.getY2() - 18;
	}

}
