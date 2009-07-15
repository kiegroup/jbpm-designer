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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a bpmn diagram. It holds the elements an provides some
 * access-methods for them.
 * 
 * @author Team Royal Fawn
 * 
 */
public abstract class  BPMNAbstractDiagram<T extends BPMNElement> implements BPMNDiagram {
	Map<String, T> elements = new HashMap<String, T>();

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getElements()
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BPMNElement> getElements() {
		return Collections.unmodifiableMap((Map<String, BPMNElement>) elements);
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getChildElementsOf(de.unihannover.se.infocup2008.bpmn.model.BPMNElement)
	 */
	public List<BPMNElement> getChildElementsOf(BPMNElement parent) {
		return getChildElementsOf(Collections.singletonList(parent));
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getChildElementsOf(java.util.List)
	 */
	public List<BPMNElement> getChildElementsOf(List<BPMNElement> parents) {
		List<BPMNElement> result = new LinkedList<BPMNElement>();
		for (String key : getElements().keySet()) {
			BPMNElement element = getElements().get(key);
			if (parents.contains(element.getParent())) {
				result.add(element);
			}
		}
		return result;

	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getElementsOfType(java.lang.String)
	 */
	public List<BPMNElement> getElementsOfType(String type) {
		List<BPMNElement> resultList = new LinkedList<BPMNElement>();

		for (String key : getElements().keySet()) {
			BPMNElement element = getElements().get(key);
			if (element.getType().equals(type)) {
				resultList.add(element);
			}
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getElementsWithoutType(java.lang.String)
	 */
	public List<BPMNElement> getElementsWithoutType(String type) {
		List<BPMNElement> resultList = new LinkedList<BPMNElement>();

		for (String key : getElements().keySet()) {
			BPMNElement element = getElements().get(key);
			if (!element.getType().equals(type)) {
				resultList.add(element);
			}
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getElement(java.lang.String)
	 */
	public T getElement(String id) {
		T element = this.elements.get(id);
		if (element == null) {
			element = this.newElement();
			element.setId(id);
			this.elements.put(id, element);
		}
		return element;
	}

	protected abstract T newElement();


	@Override
	public String toString() {
		String out = "BPMNDiagramm: \n";
		out += getElements().size() + " Elements:\n";
		for (String key : getElements().keySet()) {
			BPMNElement element = getElements().get(key);
			out += element.toString() + "\n";
		}
		return out;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getStartEvents()
	 */
	public List<BPMNElement> getStartEvents() {
		List<BPMNElement> resultList = new LinkedList<BPMNElement>();

		for (BPMNElement element : getElements().values()) {
			if (BPMNType.isAStartEvent(element.getType())) {
				resultList.add(element);
			}
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getConnectingElements()
	 */
	public List<BPMNElement> getConnectingElements() {
		List<BPMNElement> resultList = new LinkedList<BPMNElement>();

		for (BPMNElement element : getElements().values()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				resultList.add(element);
			}
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getGateways()
	 */
	public List<BPMNElement> getGateways() {
		List<BPMNElement> resultList = new LinkedList<BPMNElement>();

		for (BPMNElement element : getElements().values()) {
			if (BPMNType.isAGateWay(element.getType())) {
				resultList.add(element);
			}
		}

		return resultList;
	}
}
