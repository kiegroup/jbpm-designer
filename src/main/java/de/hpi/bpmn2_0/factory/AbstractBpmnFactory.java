package de.hpi.bpmn2_0.factory;

/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Documentation;
import de.hpi.bpmn2_0.model.diagram.BpmnNode;

/**
 * This is the abstract factory that offers methods to create a process element
 * and a related diagram element from a {@link Shape}.
 */
public abstract class AbstractBpmnFactory {

	/**
	 * Creates a process element based on a {@link Shape}.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The constructed process element.
	 */
	protected abstract BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException;

	/**
	 * Creates a diagram element based on a {@link Shape}.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The constructed diagram element.
	 */
	protected abstract Object createDiagramElement(Shape shape);

	/**
	 * Creates BPMNElement that contains DiagramElement and ProcessElement
	 * 
	 * @param shape
	 *            The resource shape.
	 * @return The constructed BPMN element.
	 */
	public abstract BPMNElement createBpmnElement(Shape shape,
			BPMNElement parent) throws BpmnConverterException;

	/**
	 * Sets attributes of a {@link BaseElement} that are common for all
	 * elements.
	 * 
	 * @param element
	 *            The BPMN 2.0 element
	 * @param shape
	 *            The resource shape
	 */
	protected void setCommonAttributes(BaseElement element, Shape shape) {
		String documentation = shape.getProperty("documentation");
		if (documentation != null && !documentation.isEmpty())
			element.getDocumentation().add(new Documentation(documentation));
	}

	/**
	 * Sets the fields for the visual representation e.g. x and y coordinates,
	 * height and width
	 * 
	 * @param diaElement
	 *            The BPMN 2.0 diagram element
	 * @param shape
	 *            The resource shape
	 */
	protected void setVisualAttributes(BpmnNode diaElement, Shape shape) {
		diaElement.setId(shape.getResourceId() + "_gui");
		diaElement.setName(shape.getProperty("name"));

		/* Graphic fields */
		diaElement.setX(shape.getUpperLeft().getX());
		diaElement.setY(shape.getUpperLeft().getY());
		diaElement.setHeight(shape.getHeight());
		diaElement.setWidth(shape.getWidth());
	}

	protected BaseElement invokeCreatorMethod(Shape shape)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, BpmnConverterException {

		/* Retrieve the method to create the process element */
		for (Method method : Arrays
				.asList(this.getClass().getDeclaredMethods())) {
			StencilId stencilIdA = method.getAnnotation(StencilId.class);
			if (stencilIdA != null
					&& Arrays.asList(stencilIdA.value()).contains(
							shape.getStencilId())) {
				/* Create element with appropriate method */
				BaseElement createdElement = (BaseElement) method.invoke(this,
						shape);
				/* Invoke generalized method to set common element attributes */
				this.setCommonAttributes(createdElement, shape);
				
				return createdElement;
			}
		}

		throw new BpmnConverterException("Creator method for shape with id "
				+ shape.getStencilId() + " not found");
	}

	protected BaseElement invokeCreatorMethodAfterProperty(Shape shape)
			throws BpmnConverterException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		for (Method method : Arrays
				.asList(this.getClass().getMethods())) {
			Property property = method.getAnnotation(Property.class);

			if (property != null
					&& Arrays.asList(property.value()).contains(
							shape.getProperty(property.name()))) {
				
				/* Create element */
				BaseElement createdElement = (BaseElement) method.invoke(this,
						shape);
				/* Invoke generalized method to set common element attributes */
				this.setCommonAttributes(createdElement, shape);
				
				return createdElement;
			}
		}

		throw new BpmnConverterException("Creator method for shape with id "
				+ shape.getStencilId() + " not found");
	}
}
