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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.participant.Participant;


/**
 * <p>Java class for poolCompartmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="poolCompartmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bpmndi.org}bpmnCompartmentType">
 *       &lt;sequence>
 *         &lt;element name="lanes" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="participantRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "poolCompartmentType", namespace = "http://bpmndi.org", propOrder = {
    "lanes"
})
public class PoolCompartment
    extends BpmnCompartment
{
	@XmlIDREF
    @XmlElement(name = "laneCompRef", type = LaneCompartment.class)
    protected List<LaneCompartment> lanes;
    
    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Participant participantRef;

    public void addChild(BpmnNode childLane) {
    	if(!(childLane instanceof LaneCompartment)) {
    		return;
    	}
    	
    	this.getLane().add((LaneCompartment) childLane);
    }
    
    public List<Shape> toShape() {
    	List<Shape> shapes = super.toShape();
    	/* It is expected that only one shape is created for a pool element */
    	if(shapes.size() != 1)
    		return new ArrayList<Shape>();
    	
    	Shape poolShape = shapes.get(0);
    	this.getParticipantRef().toShape(poolShape);
    	
    	if(this.getLane().size() == 0) {
    		/* Collapsed pool */
    		poolShape.setStencil(new StencilType("CollapsedPool"));
    	} else if(this.getLane().size() > 0) {
    		poolShape.setStencil(new StencilType("Pool"));
    		for(LaneCompartment laneComp : this.getLane()) {
        		poolShape.getChildShapes().addAll(laneComp.toShape());
        	}
    	}
    	return shapes;
    }
    
    /* Getter & Setter */
    
    /**
     * Gets the value of the lanes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lanes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLaneCompRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LaneCompartment }{@code >}
     * 
     * 
     */
    public List<LaneCompartment> getLane() {
        if (lanes == null) {
            lanes = new ArrayList<LaneCompartment>();
        }
        return this.lanes;
    }

    /**
     * Gets the value of the participantRef property.
     * 
     * @return
     *     possible object is
     *     {@link Participant }
     *     
     */
    public Participant getParticipantRef() {
        return participantRef;
    }

    /**
     * Sets the value of the participantRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Participant }
     *     
     */
    public void setParticipantRef(Participant value) {
        this.participantRef = value;
    }

	@Override
	protected FlowElement getFlowElement() {
		return this.getParticipantRef();
	}

}
