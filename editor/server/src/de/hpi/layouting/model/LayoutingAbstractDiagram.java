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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a diagram. It holds the elements an provides some
 * access-methods for them.
 * 
 * @author Team Royal Fawn
 * 
 */
public abstract class  LayoutingAbstractDiagram<T extends LayoutingElement> implements LayoutingDiagram {
	Map<String, T> elements = new HashMap<String, T>();

	@SuppressWarnings("unchecked")
	public Map<String, LayoutingElement> getElements() {
		return Collections.unmodifiableMap((Map<String, LayoutingElement>) elements);
	}

	public List<LayoutingElement> getChildElementsOf(LayoutingElement parent) {
		return getChildElementsOf(Collections.singletonList(parent));
	}

	public List<LayoutingElement> getChildElementsOf(List<LayoutingElement> parents) {
		List<LayoutingElement> result = new LinkedList<LayoutingElement>();
		for (String key : getElements().keySet()) {
			LayoutingElement element = getElements().get(key);
			if (parents.contains(element.getParent())) {
				result.add(element);
			}
		}
		return result;

	}

	public List<LayoutingElement> getElementsOfType(String type) {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (String key : getElements().keySet()) {
			LayoutingElement element = getElements().get(key);
			if (element.getType().equals(type)) {
				resultList.add(element);
			}
		}

		return resultList;
	}

	public List<LayoutingElement> getElementsWithoutType(String type) {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (String key : getElements().keySet()) {
			LayoutingElement element = getElements().get(key);
			if (!element.getType().equals(type)) {
				resultList.add(element);
			}
		}

		return resultList;
	}

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
		String out = "Diagramm: \n";
		out += getElements().size() + " Elements:\n";
		for (String key : getElements().keySet()) {
			LayoutingElement element = getElements().get(key);
			out += element.toString() + "\n";
		}
		return out;
	}

}
