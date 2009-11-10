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

package de.hpi.bpmn2_0.model.participant;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.diagram.OryxUUID;


/**
 * <p>Java class for tLane complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tLane">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="partitionElement" type="{http://www.omg.org/bpmn20}tBaseElement" minOccurs="0"/>
 *         &lt;element name="flowElementRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="childLaneSet" type="{http://www.omg.org/bpmn20}tLaneSet" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="partitionElementRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tLane", propOrder = {
    "partitionElement",
    "flowElementRef",
    "laneSet",
    "childLaneSet"
})
public class Lane
    extends FlowElement
{

	protected BaseElement partitionElement;
    
	@XmlIDREF
//	@XmlElements({
//		/* Events */
//		@XmlElement(type = StartEvent.class),
//		@XmlElement(type = EndEvent.class),
//		
//		/* Activities */
//		@XmlElement(type = Task.class),
//		
//		/* Gateways */
//		@XmlElement(type = ExclusiveGateway.class),
//		@XmlElement(type = ParallelGateway.class),
//		
//		/* Edges */
//		@XmlElement(type = SequenceFlow.class),
//		
//		/* Artifacts / Data elements */
//		@XmlElement(type = DataObject.class),
//		@XmlElement(type = TextAnnotation.class),
//		
//		/* Partner */
//		@XmlElement(type = Participant.class)
//	})
	@XmlElement(type = FlowNode.class)
    protected List<FlowElement> flowElementRef;
    
	@XmlElement(type = LaneSet.class)
	protected LaneSet childLaneSet;
	
	@XmlIDREF
	@XmlAttribute
	@XmlSchemaType(name = "IDREF")
//	@XmlElementRef(type = LaneSet.class)
	protected LaneSet laneSet;
    
    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object partitionElementRef;

    
    /**
     * Adds the child to the lane's flow elements if possible.
     */
    public void addChild(BaseElement child) {
    	if(child instanceof Lane) {
    		this.getChildLaneSet().getLanes().add((Lane) child);
    		((Lane) child).setLaneSet(this.getChildLaneSet());
    	} else if (!(child instanceof Edge)) {
    		this.getFlowElementRef().add((FlowElement) child);
    	}
    }
    
    /**
     * Set general properties of a lane while generating its json 
     * representation
     */
    public void toShape(Shape shape) {
    	super.toShape(shape);
    	
    	shape.setStencil(new StencilType("Lane"));
    } 
    
    /* Getter & Setter */
    
    /**
     * Gets the value of the partitionElement property.
     * 
     * @return
     *     possible object is
     *     {@link TBaseElement }
     *     
     */
    public BaseElement getPartitionElement() {
        return partitionElement;
    }

    /**
     * Sets the value of the partitionElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBaseElement }
     *     
     */
    public void setPartitionElement(BaseElement value) {
        this.partitionElement = value;
    }

    /**
	 * @return the laneSet
	 */
	public LaneSet getLaneSet() {
		return laneSet;
	}

	/**
	 * @param laneSet the laneSet to set
	 */
	public void setLaneSet(LaneSet laneSet) {
		this.laneSet = laneSet;
	}

	/**
     * Gets the value of the flowElementRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flowElementRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlowElementRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * 
     */
    @ChildElements
    public List<FlowElement> getFlowElementRef() {
        if (flowElementRef == null) {
            flowElementRef = new ArrayList<FlowElement>();
        }
        return this.flowElementRef;
    }

    /**
     * Gets the value of the childLaneSet property.
     * 
     * @return
     *     possible object is
     *     {@link LaneSet }
     *     
     */
    @ChildElements
    public LaneSet getChildLaneSet() {
    	if(childLaneSet == null) {
    		childLaneSet = new LaneSet();
    		childLaneSet.setId(OryxUUID.generate());
    		childLaneSet.setParentLane(this);
    	}
        return childLaneSet;
    }

    /**
     * Sets the value of the childLaneSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link LaneSet }
     *     
     */
    public void setChildLaneSet(LaneSet value) {
        this.childLaneSet = value;
    }

    /**
     * Gets the value of the partitionElementRef property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPartitionElementRef() {
        return partitionElementRef;
    }

    /**
     * Sets the value of the partitionElementRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPartitionElementRef(Object value) {
        this.partitionElementRef = value;
    }

}
