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
package de.unihannover.se.infocup2008.bpmn.model;

import org.w3c.dom.Node;

import de.hpi.layouting.model.LayoutingDockers.Point;

/**
 * Implements the <code>BPMNElement</code> Interface.
 * 
 * @author Team Royal Fawn
 * 
 */
public class BPMNElementERDF extends BPMNAbstractElement implements BPMNElement {
	private Node dockersNode = null;
	protected Node boundsNode = null;

	public void updateDataModel() {
		this.boundsNode.setNodeValue(geometry.getX() + "," + geometry.getY()
				+ "," + geometry.getX2() + "," + geometry.getY2());
		StringBuilder dockerSB = new StringBuilder();
		for (Point p: dockers.getPoints()){
			dockerSB.append(p.x);
			dockerSB.append(" ");
			dockerSB.append(p.y);
			dockerSB.append(" ");
		}
		dockerSB.append(" # ");
		dockersNode.setNodeValue(dockerSB.toString());
	}

	public Node getDockersNode() {
		return this.dockersNode;
	}

	public void setDockersNode(Node node) {
		this.dockersNode = node;
	}

	public Node getBoundsNode() {
		return boundsNode;
	}

	public void setBoundsNode(Node node) {
		this.boundsNode = node;
	}

}
