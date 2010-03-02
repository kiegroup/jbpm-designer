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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;


/**
 * <p>Java class for bpmnConnectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bpmnConnectorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bendpoint" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                 &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}double" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="sourceRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="targetRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bpmnConnectorType", namespace = "http://bpmndi.org", propOrder = {
    "bendpoint"
})
@XmlSeeAlso({
    SequenceFlowConnector.class,
    AssociationConnector.class,
    MessageFlowConnector.class
})
public class BpmnConnector implements BpmnShape {

    @XmlElement(namespace = "http://bpmndi.org")
    protected List<BpmnConnector.Bendpoint> bendpoint;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object sourceRef;
    @XmlAttribute(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object targetRef;
    @XmlAttribute
    protected String label;
    
    public Shape toShape() {
    	Shape shape = new Shape(this.getId());
    	
    	/* Variables to determine the bounds of a connector */
    	double ulx = 0;
    	double uly = 0;
    	double lrx = 0;
    	double lry = 0;
    	
    	/* Set Dockers */
    	if(this.getSourceRef() instanceof BpmnNode) {
    		Bendpoint bPoint = ((BpmnNode) this.getSourceRef()).getCenterBendpoint();
    		Point point = ((BpmnNode) this.getSourceRef()).getAbsoluteCenterPoint();
    		ulx = point.getX();
    		lrx = point.getX();
    		uly = point.getY();
    		lry = point.getY();
    		shape.getDockers().add(new Point(bPoint.getX(), bPoint.getY()));
    	}
    	
    	for(Bendpoint bPoint : this.getBendpoint()) {
    		ulx = (bPoint.getX() < ulx ? bPoint.getX() : ulx);
    		lrx = (bPoint.getX() > lrx ? bPoint.getX() : lrx);
    		uly = (bPoint.getY() < uly ? bPoint.getY() : uly);
    		lry = (bPoint.getY() > lry ? bPoint.getY() : lry);
    		shape.getDockers().add(new Point(bPoint.getX(), bPoint.getY()));
    	}
    	
    	if(this.getTargetRef() instanceof BpmnNode) {
    		Bendpoint bPoint = ((BpmnNode) this.getTargetRef()).getCenterBendpoint();
    		
    		Point point = ((BpmnNode) this.getSourceRef()).getAbsoluteCenterPoint();
    		ulx = (point.getX() < ulx ? point.getX() : ulx);
    		lrx = (point.getX() > lrx ? point.getX() : lrx);
    		uly = (point.getY() < uly ? point.getY() : uly);
    		lry = (point.getY() > lry ? point.getY() : lry);
    		
    		shape.getDockers().add(new Point(bPoint.getX(), bPoint.getY()));
    	}
    	
    	/* Set bounds */
    	Bounds bounds = new Bounds(new Point(lrx, lry), new Point(ulx, uly));
    	shape.setBounds(bounds);
    	
    	return shape;
    }
    
    /* Getter & Setter */
    
    /**
     * Gets the value of the bendpoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bendpoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBendpoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BpmnConnector.Bendpoint }
     * 
     * 
     */
    public List<BpmnConnector.Bendpoint> getBendpoint() {
        if (bendpoint == null) {
            bendpoint = new ArrayList<BpmnConnector.Bendpoint>();
        }
        return this.bendpoint;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the sourceRef property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getSourceRef() {
        return sourceRef;
    }

    /**
     * Sets the value of the sourceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link BpmnShape }
     *     
     */
    public void setSourceRef(BpmnShape value) {
        this.sourceRef = value;
    }

    /**
     * Gets the value of the targetRef property.
     * 
     * @return
     *     possible object is
     *     {@link BpmnShape }
     *     
     */
    public BpmnShape getTargetRef() {
        return (BpmnShape) targetRef;
    }

    /**
     * Sets the value of the targetRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link BpmnShape }
     *     
     */
    public void setTargetRef(BpmnShape value) {
        this.targetRef = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}double" />
     *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}double" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Bendpoint {

        @XmlAttribute
        protected Double x;
        @XmlAttribute
        protected Double y;

        /**
         * Gets the value of the x property.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getX() {
            return x;
        }

        /**
         * Sets the value of the x property.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setX(Double value) {
            this.x = value;
        }

        /**
         * Gets the value of the y property.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getY() {
            return y;
        }

        /**
         * Sets the value of the y property.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setY(Double value) {
            this.y = value;
        }

    }

}
