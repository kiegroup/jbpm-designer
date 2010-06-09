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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.diagram.activity.ActivityShape;
import de.hpi.bpmn2_0.model.diagram.activity.CalledSubprocessShape;
import de.hpi.bpmn2_0.model.diagram.activity.EmbeddedSubprocessShape;
import de.hpi.bpmn2_0.model.diagram.activity.SubprocessShape;
import de.hpi.bpmn2_0.model.participant.Lane;


/**
 * <p>Java class for laneCompartmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="laneCompartmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bpmndi.org}bpmnCompartmentType">
 *       &lt;sequence>
 *         &lt;element ref="{http://bpmndi.org}bpmnShape" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="subLane" type="{http://bpmndi.org}laneCompartmentType_1" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="laneRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "laneCompartmentType", namespace = "http://bpmndi.org", propOrder = {
    "bpmnShape",
    "subLane"
})
public class LaneCompartment
    extends BpmnCompartment
{

    @XmlElementRefs({
    	@XmlElementRef(type = BpmnCompartment.class),
    	@XmlElementRef(type = EventShape.class),
    	
    	@XmlElementRef(type = ActivityShape.class),
    	@XmlElementRef(type = EmbeddedSubprocessShape.class),
    	@XmlElementRef(type = CalledSubprocessShape.class),
    	
    	@XmlElementRef(type = GatewayShape.class),
    	
    	@XmlElementRef(type = DataObjectShape.class),
    	@XmlElementRef(type = DataStoreShape.class),
    	@XmlElementRef(type = DataInputShape.class),
    	@XmlElementRef(type = DataOutputShape.class),
    	@XmlElementRef(type = ITSystemShape.class),
    	@XmlElementRef(type = ProcessParticipantShape.class),

    	@XmlElementRef(type = TextAnnotationShape.class)
    })
    protected List<BpmnNode> bpmnShape;
    
    @XmlElement
    protected List<LaneCompartment> subLane;
    
    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Lane laneRef;
    
    public void addChild(BpmnNode child) {
    	if(child instanceof LaneCompartment) {
    		this.getSubLane().add((LaneCompartment) child);
    		return;
    	}
    	this.getBpmnShape().add(child);
    }
    
    public List<Shape> toShape() {
    	List<Shape> shapes = new ArrayList<Shape>();
    	if (this.isIsVisible()) {
    		shapes = super.toShape();
    		ArrayList<Shape> childShapes = new ArrayList<Shape>();
    		
    		/* Add BPMN elements */
    		for(BpmnNode node : this.getBpmnShape()) {
        		childShapes.addAll(node.toShape());
        	}
    		    		
    		/* Add sub-lanes */
    		for(LaneCompartment subLane : this.getSubLane()) {
        		childShapes.addAll(subLane.toShape());
        	}
    		
    		shapes.get(0).getChildShapes().addAll(childShapes);
    		return shapes;
    	}
    	
    	for(LaneCompartment subLane : this.getSubLane()) {
    		shapes.addAll(subLane.toShape());
    	}
    	for(BpmnNode node : this.getBpmnShape()) {
    		shapes.addAll(node.toShape());
    	}
    	
    	return shapes;
    }
    
    /* Getter & Setter */
    
    /**
     * Gets the value of the bpmnShape property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bpmnShape property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBpmnShape().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DataObjectShape }{@code >}
     * {@link JAXBElement }{@code <}{@link EventShape }{@code >}
     * {@link JAXBElement }{@code <}{@link SubprocessShape }{@code >}
     * {@link JAXBElement }{@code <}{@link ActivityShape }{@code >}
     * {@link JAXBElement }{@code <}{@link BpmnNode }{@code >}
     * {@link JAXBElement }{@code <}{@link CalledSubprocessShapeType }{@code >}
     * {@link JAXBElement }{@code <}{@link DataInputShape }{@code >}
     * {@link JAXBElement }{@code <}{@link GatewayShape }{@code >}
     * {@link JAXBElement }{@code <}{@link GroupShape }{@code >}
     * {@link JAXBElement }{@code <}{@link TextAnnotationShape }{@code >}
     * {@link JAXBElement }{@code <}{@link DataOutputShape }{@code >}
     * {@link JAXBElement }{@code <}{@link DataStoreShape }{@code >}
     * {@link JAXBElement }{@code <}{@link MessageShape }{@code >}
     * {@link JAXBElement }{@code <}{@link ActivityShape }{@code >}
     * 
     * 
     */
    public List<BpmnNode> getBpmnShape() {
        if (bpmnShape == null) {
            bpmnShape = new ArrayList<BpmnNode>();
        }
        return this.bpmnShape;
    }

    /**
     * Gets the value of the subLane property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subLane property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubLane().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LaneCompartmentType1 }
     * 
     * 
     */
    public List<LaneCompartment> getSubLane() {
        if (subLane == null) {
            subLane = new ArrayList<LaneCompartment>();
        }
        return this.subLane;
    }

    /**
     * Gets the value of the laneRef property.
     * 
     * @return
     *     possible object is
     *     {@link Lane }
     *     
     */
    public Lane getLaneRef() {
        return laneRef;
    }

    /**
     * Sets the value of the laneRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Lane }
     *     
     */
    public void setLaneRef(Lane value) {
        this.laneRef = value;
    }

	@Override
	protected FlowElement getFlowElement() {
		return this.getLaneRef();
	}

}
