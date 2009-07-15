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

import java.util.List;

import org.w3c.dom.Node;

/**
 * Represents an element of the bpmn diagram. Each task, sequenceflow, etc. is
 * an element.
 * 
 * @author Team Royal Fawn
 * 
 * FIXME make a Class of it (BPMNElementImpl)
 */
public interface BPMNElement {

	public abstract String getId();

	public abstract void setId(String id);
		
	
	public abstract String getType();

	public abstract void setType(String type);

	public abstract List<BPMNElement> getOutgoingLinks();

	public abstract void setOutgoingLinks(List<BPMNElement> outgoingLinks);

	public abstract void addOutgoingLink(BPMNElement element);

	public abstract void removeOutgoingLink(BPMNElement element);

	public abstract List<BPMNElement> getIncomingLinks();

	public abstract void setIncomingLinks(List<BPMNElement> incomingLinks);

	public abstract void addIncomingLink(BPMNElement element);

	public abstract void removeIncomingLink(BPMNElement element);

	public abstract List<BPMNElement> getFollowingElements();

	public abstract List<BPMNElement> getPrecedingElements();

	/**
	 * Indicates if the element has a parent other than the canvas e.g. a lane
	 * 
	 * @return <code>true</code> if the element has a parent other than the
	 *         canvas e.g. a lane
	 */
	public abstract boolean hasParent();

	public abstract void setParent(BPMNElement element);

	public abstract BPMNElement getParent();

	public abstract String toString();

	public abstract BPMNBounds getGeometry();

	public abstract void setGeometry(BPMNBounds geometry);
	
	public abstract BPMNDockers getDockers();
	
	public abstract void setDockers(BPMNDockers dockers);

	/**
	 * Updates the underlying DataModel e.g. JSONObject or XMLNode
	 */
	public abstract void updateDataModel();

	/**
	 * @return true if Element joins more then one path
	 */
	public abstract boolean isJoin();

	/**
	 * @return true if Element has more then one following Element
	 */
	public abstract boolean isSplit();

	/**
	 * Searches <code>other</code> in forward direction
	 * 
	 * @param other
	 *            BPMNElement to search for
	 * @return number of elements between <code>other</code> and
	 *         <code>this</code> or <code>Integer.MAX_VALUE</code> if the
	 *         elements are not connected in forward direction
	 */
	public abstract int forwardDistanceTo(BPMNElement other);

	/**
	 * Searches <code>other</code> in backward direction
	 * 
	 * @param other
	 *            BPMNElement to search for
	 * @return number of elements between <code>this</code> and
	 *         <code>other</code> or <code>Integer.MAX_VALUE</code> if the
	 *         elements are not connected in backward direction
	 */
	public abstract int backwardDistanceTo(BPMNElement other);

	/**
	 * 
	 * @return the closest split with the same parent before <code>this</code>.
	 *         Is never <code>this</code> on cycle-free diagramms but maybe
	 *         <code>null</code>
	 */
	public abstract BPMNElement prevSplit();

	public abstract boolean isADockedIntermediateEvent();
}