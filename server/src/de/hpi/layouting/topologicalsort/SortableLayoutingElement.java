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
package de.hpi.layouting.topologicalsort;

import java.util.SortedSet;
import java.util.TreeSet;

import de.hpi.layouting.model.LayoutingElement;


/**
 * A simplified view on a <tt>LayoutingElement</tt>, suitable for topological sorting.
 * @author Team Royal Fawn
 *
 */
public class SortableLayoutingElement {

	private LayoutingElement element;
	private SortedSet<String> outgoingLinks;
	private SortedSet<String> incomingLinks;
	private boolean join;
	private int oldInCount;

	public SortableLayoutingElement(LayoutingElement element) {
		this.element = element;
		this.incomingLinks = new TreeSet<String>();
		this.outgoingLinks = new TreeSet<String>();
		for (LayoutingElement pre : element.getPrecedingElements()) {
			// String type = pre.getType();
			// if(!BPMNType.isASwimlane(type)){
			incomingLinks.add(pre.getId());
			// }
		}
		for (LayoutingElement post : element.getFollowingElements()) {
			outgoingLinks.add(post.getId());
		}
		this.join = element.isJoin();
		this.oldInCount = incomingLinks.size();
	}

	public LayoutingElement getLayoutingElement() {
		return this.element;
	}

	public String getId() {
		return this.element.getId();
	}

	public String getType() {
		return this.element.getType();
	}

	public boolean isFree() {
		return this.incomingLinks.isEmpty();
	}

	public boolean isJoin() {
		return join;
	}

	public int getOldInCount() {
		return oldInCount;
	}

	public SortedSet<String> getOutgoingLinks() {
		return this.outgoingLinks;
	}

	public SortedSet<String> getIncomingLinks() {
		return this.incomingLinks;
	}

	public void removeIncomingLinkFrom(String id) {
		incomingLinks.remove(id);
	}

	public void reverseIncomingLinkFrom(String id) {
		removeIncomingLinkFrom(id);
		outgoingLinks.add(id);
	}

	public void reverseOutgoingLinkTo(String id) {
		outgoingLinks.remove(id);
		incomingLinks.add(id);
	}
}
