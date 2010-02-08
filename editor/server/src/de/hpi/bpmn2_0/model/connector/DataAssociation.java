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

package de.hpi.bpmn2_0.model.connector;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.misc.Assignment;


/**
 * <p>Java class for tDataAssociation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tDataAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="transformation" type="{http://www.omg.org/bpmn20}tFormalExpression" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}assignment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataAssociation", propOrder = {
    "transformation",
    "assignment",
    "sourceRef",
    "targetRef"
})
@XmlSeeAlso({
    DataInputAssociation.class,
    DataOutputAssociation.class
})
public class DataAssociation extends Edge
{
	@XmlIDREF
	@XmlElement
	protected FlowElement sourceRef;
	
	@XmlIDREF
	@XmlElement
	protected FlowElement targetRef;
	
	
	@XmlElement
    protected FormalExpression transformation;
    @XmlElement
	protected List<Assignment> assignment;
    
    /* Getter & Setter */
    
    /**
	 * @return the sourceRef
	 */
	public FlowElement getSourceRef() {
		return this.sourceRef;
	}
	
	/**
	 * @return the targetRef
	 */
	public FlowElement getTargetRef() {
		return this.targetRef;
	}
    
    /**
	 * @param sourceRef the sourceRef to set
	 */
	public void setSourceRef(FlowElement sourceRef) {
		this.sourceRef = sourceRef;
	}
	/**
	 * @param targetRef the targetRef to set
	 */
	public void setTargetRef(FlowElement targetRef) {
		this.targetRef = targetRef;
	}
    
    /**
     * Gets the value of the transformation property.
     * 
     * @return
     *     possible object is
     *     {@link TFormalExpression }
     *     
     */
    public FormalExpression getTransformation() {
        return transformation;
    }

    /**
     * Sets the value of the transformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormalExpression }
     *     
     */
    public void setTransformation(FormalExpression value) {
        this.transformation = value;
    }

    /**
     * Gets the value of the assignment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assignment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssignment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TAssignment }
     * 
     * 
     */
    public List<Assignment> getAssignment() {
        if (assignment == null) {
            assignment = new ArrayList<Assignment>();
        }
        return this.assignment;
    }

}
