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

package de.hpi.bpmn2_0.model.diagram;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.diagram.BpmnConnector.Bendpoint;

/**
 * <p>
 * Java class for bpmnNodeType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;bpmnNodeType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;attribute name=&quot;id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}ID&quot; /&gt;
 *       &lt;attribute name=&quot;name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;x&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anySimpleType&quot; /&gt;
 *       &lt;attribute name=&quot;y&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anySimpleType&quot; /&gt;
 *       &lt;attribute name=&quot;width&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anySimpleType&quot; /&gt;
 *       &lt;attribute name=&quot;height&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anySimpleType&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bpmnNodeType", namespace = "http://bpmndi.org")
@XmlSeeAlso( {
// GroupShape.class,
		// TextAnnotationShape.class,
		// ActivityShape .class,
		// GatewayShape.class,
		// MessageShape.class,
		// DataObjectShape.class,
		// DataInputShape.class,
		// DataStoreShape.class,
		// DataOutputShape.class,
		EventShape.class, BpmnCompartment.class })
public abstract class BpmnNode implements BpmnShape {

	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;
	@XmlAttribute
	protected String name;
	@XmlAttribute
	@XmlSchemaType(name = "anySimpleType")
	protected double x;
	@XmlAttribute
	@XmlSchemaType(name = "anySimpleType")
	protected double y;
	@XmlAttribute
	@XmlSchemaType(name = "anySimpleType")
	protected double width;
	@XmlAttribute
	@XmlSchemaType(name = "anySimpleType")
	protected double height;

	public void addChild(BpmnNode childShape) {
		return;
	}

	public List<Shape> toShape() {
		Shape shape = new Shape("");

		/* Create bounds */
		Point lr = new Point(this.getX() + this.getWidth(), this.getY()
				+ this.getHeight());
		Point ul = new Point(this.getX(), this.getY());
		Bounds bounds = new Bounds(lr, ul);
		shape.setBounds(bounds);

		/* Handle properties */
		if(this.getFlowElement() != null)
			this.getFlowElement().toShape(shape);

		List<Shape> shapes = new ArrayList<Shape>();
		shapes.add(shape);
		return shapes;
	}

	/**
	 * Retrieves the center point of the BPMN shape.
	 * 
	 * @return A bendpoint describing the center point of the shape.
	 */
	public Bendpoint getCenterBendpoint() {
		Bendpoint bPoint = new Bendpoint();
		bPoint.setX(this.getWidth() / 2);
		bPoint.setY(this.getHeight() / 2);
		return bPoint;
	}

	public Point getAbsoluteCenterPoint() {
		Point point = new Point(this.getX() + this.getWidth() / 2, this.getY()
				+ this.getHeight() / 2);
		return point;
	}

	/* Getter & Setter */

	/**
	 * Retrieves the {@link FlowElement} related to the shape representation.
	 */
	protected abstract FlowElement getFlowElement();

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Gets the value of the x property.
	 * 
	 * @return possible object is {@link double }
	 * 
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the value of the x property.
	 * 
	 * @param value
	 *            allowed object is {@link double }
	 * 
	 */
	public void setX(double value) {
		this.x = value;
	}

	/**
	 * Gets the value of the y property.
	 * 
	 * @return possible object is {@link double }
	 * 
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the value of the y property.
	 * 
	 * @param value
	 *            allowed object is {@link double }
	 * 
	 */
	public void setY(double value) {
		this.y = value;
	}

	/**
	 * Gets the value of the width property.
	 * 
	 * @return possible object is {@link double }
	 * 
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the value of the width property.
	 * 
	 * @param value
	 *            allowed object is {@link double }
	 * 
	 */
	public void setWidth(double value) {
		this.width = value;
	}

	/**
	 * Gets the value of the height property.
	 * 
	 * @return possible object is {@link double }
	 * 
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the value of the height property.
	 * 
	 * @param value
	 *            allowed object is {@link double }
	 * 
	 */
	public void setHeight(double value) {
		this.height = value;
	}

}
