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

package de.hpi.bpmn2_0.model.activity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.resource.ActivityResource;
import de.hpi.bpmn2_0.model.activity.resource.HumanPerformer;
import de.hpi.bpmn2_0.model.activity.resource.Performer;
import de.hpi.bpmn2_0.model.activity.resource.PotentialOwner;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;


/**
 * <p>Java class for tActivity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}ioSpecification" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataInputAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataOutputAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}activityResource" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}loopCharacteristics" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isForCompensation" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="startQuantity" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="completionQuantity" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tActivity", propOrder = {
//    "ioSpecification",
//    "property",
//	"boundaryEventRef",
//    "dataInputAssociation",
//    "dataOutputAssociation"//,
//    "activityResource",
//    "loopCharacteristics"
})
@XmlSeeAlso({
    SubProcess.class,
//    Transaction.class,
    Task.class//,
//    CallActivity.class
})
public abstract class Activity
    extends FlowNode
{

//    protected TInputOutputSpecification ioSpecification;
//    protected List<TProperty> property;
	
	@XmlIDREF
	@XmlElement(name = "dataInputAssociation", type = DataInputAssociation.class)
    protected List<DataInputAssociation> dataInputAssociation;
	
	@XmlIDREF
	@XmlElement(name = "dataOutputAssociation", type = DataOutputAssociation.class)
    protected List<DataOutputAssociation> dataOutputAssociation;
	
    @XmlElementRefs({
    	@XmlElementRef(type = ActivityResource.class),
    	@XmlElementRef(type = Performer.class),
    	@XmlElementRef(type = HumanPerformer.class),
    	@XmlElementRef(type = PotentialOwner.class)
    })
	protected List<ActivityResource> activityResource;
//    protected LoopCharacteristics loopCharacteristics;
    
	@XmlIDREF
	@XmlElement(name = "boundaryEventRef", type = BoundaryEvent.class)
	protected List<BoundaryEvent> boundaryEventRefs;
	
	@XmlAttribute
    protected Boolean isForCompensation;
    
	@XmlAttribute
    protected BigInteger startQuantity;
    
	@XmlAttribute
    protected BigInteger completionQuantity;
    
	@XmlAttribute(name = "default")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object _default;
	
	/**
	 * @return The list of boundary event references
	 */
    public List<BoundaryEvent> getBoundaryEventRefs() {
    	if(this.boundaryEventRefs == null) {
    		this.boundaryEventRefs = new ArrayList<BoundaryEvent>();
    	}
    	return this.boundaryEventRefs;
    }
	
	/**
     * Gets the value of the ioSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link TInputOutputSpecification }
     *     
     */
//    public TInputOutputSpecification getIoSpecification() {
//        return ioSpecification;
//    }

    /**
     * Sets the value of the ioSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link TInputOutputSpecification }
     *     
     */
//    public void setIoSpecification(TInputOutputSpecification value) {
//        this.ioSpecification = value;
//    }

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TProperty }
     * 
     * 
     */
//    public List<TProperty> getProperty() {
//        if (property == null) {
//            property = new ArrayList<TProperty>();
//        }
//        return this.property;
//    }

    /**
     * Gets the value of the dataInputAssociation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataInputAssociation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataInputAssociation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataInputAssociation }
     * 
     * 
     */
    public List<DataInputAssociation> getDataInputAssociation() {
        if (dataInputAssociation == null) {
            dataInputAssociation = new ArrayList<DataInputAssociation>();
        }
        return this.dataInputAssociation;
    }

    /**
     * Gets the value of the dataOutputAssociation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutputAssociation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutputAssociation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutputAssociation }
     * 
     * 
     */
    public List<DataOutputAssociation> getDataOutputAssociation() {
        if (dataOutputAssociation == null) {
            dataOutputAssociation = new ArrayList<DataOutputAssociation>();
        }
        return this.dataOutputAssociation;
    }

    /**
     * Gets the value of the activityResource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activityResource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivityResource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link HumanPerformer }{@code >}
     * {@code <}{@link Performer }{@code >}
     * {@code <}{@link PotentialOwner }{@code >}
     * {@code <}{@link ActivityResource }{@code >}
     * 
     * 
     */
    public List<ActivityResource> getActivityResource() {
        if (activityResource == null) {
            activityResource = new ArrayList<ActivityResource>();
        }
        return this.activityResource;
    }

    /**
     * Gets the value of the loopCharacteristics property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TMultiInstanceLoopCharacteristics }{@code >}
     *     {@link JAXBElement }{@code <}{@link TLoopCharacteristics }{@code >}
     *     {@link JAXBElement }{@code <}{@link TStandardLoopCharacteristics }{@code >}
     *     
     */
//    public JAXBElement<? extends TLoopCharacteristics> getLoopCharacteristics() {
//        return loopCharacteristics;
//    }

    /**
     * Sets the value of the loopCharacteristics property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TMultiInstanceLoopCharacteristics }{@code >}
     *     {@link JAXBElement }{@code <}{@link TLoopCharacteristics }{@code >}
     *     {@link JAXBElement }{@code <}{@link TStandardLoopCharacteristics }{@code >}
     *     
     */
//    public void setLoopCharacteristics(JAXBElement<? extends TLoopCharacteristics> value) {
//        this.loopCharacteristics = ((JAXBElement<? extends TLoopCharacteristics> ) value);
//    }

    /**
     * Gets the value of the isForCompensation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsForCompensation() {
        if (isForCompensation == null) {
            return false;
        } else {
            return isForCompensation;
        }
    }

    /**
     * Sets the value of the isForCompensation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsForCompensation(Boolean value) {
        this.isForCompensation = value;
    }

    /**
     * Gets the value of the startQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStartQuantity() {
        if (startQuantity == null) {
            return new BigInteger("1");
        } else {
            return startQuantity;
        }
    }

    /**
     * Sets the value of the startQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStartQuantity(BigInteger value) {
        this.startQuantity = value;
    }

    /**
     * Gets the value of the completionQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCompletionQuantity() {
        if (completionQuantity == null) {
            return new BigInteger("1");
        } else {
            return completionQuantity;
        }
    }

    /**
     * Sets the value of the completionQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCompletionQuantity(BigInteger value) {
        this.completionQuantity = value;
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDefault(Object value) {
        this._default = value;
    }

}
