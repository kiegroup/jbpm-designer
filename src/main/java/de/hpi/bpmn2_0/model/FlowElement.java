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

package de.hpi.bpmn2_0.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.participant.Lane;


/**
 * <p>Java class for tFlowElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tFlowElement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}auditing" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}monitoring" minOccurs="0"/>
 *         &lt;element name="categoryValue" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlowElement", propOrder = {
//    "auditing",
//    "monitoring",
	"categoryValue"
//	"incoming",
//	"outgoing",
//	"process"
})
@XmlSeeAlso({
//    SequenceFlow.class,
//    DataObject.class,
//    DataStore.class,
	Lane.class,
    FlowNode.class
})
public abstract class FlowElement
    extends BaseElement
{

//    protected TAuditing auditing;
//    protected TMonitoring monitoring;
    protected List<QName> categoryValue;
    
    @XmlAttribute
    protected String name;
    
//    @XmlIDREF
//	@XmlSchemaType(name = "IDREF")
//	@XmlElement(name = "incoming", type = Edge.class)
    @XmlTransient
    protected List<Edge> incoming;
	
//	@XmlIDREF
//	@XmlSchemaType(name = "IDREF")
//	@XmlElement(name = "outgoing", type = Edge.class)
    @XmlTransient
    protected List<Edge> outgoing;
	
	/* The process the element belongs to */
//	@XmlIDREF
//	@XmlAttribute
//	@XmlSchemaType(name = "IDREF")
    @XmlTransient
	protected Process process;
    
    /**
     * Default constructor
     */
    public FlowElement() {
		
	}
    
    /**
     * Copy constructor
     */
    public FlowElement(FlowElement flowEl) {
    	super(flowEl);
    	
    	if(flowEl.getCategoryValue().size() > 0)
    		this.getCategoryValue().addAll(flowEl.getCategoryValue());
    	
    	if(flowEl.getIncoming().size() > 0)
    		this.getIncoming().addAll(flowEl.getIncoming());
    	
    	if(flowEl.getOutgoing().size() > 0)
    		this.getOutgoing().addAll(flowEl.getOutgoing());
    	
    	this.setProcess(flowEl.getProcess());
    	this.setName(flowEl.getName());
    }
	
	/**
	 * Basic method to set properties on the shape object. In common these 
	 * properties are related to the process logic.
	 * 
	 * @param shape
	 * 		The resource shape object containing graphical information only.
	 */
    public void toShape(Shape shape) {
    	shape.setResourceId(this.getId());
    	shape.putProperty("name", (this.getName()!= null ? this.getName() : ""));
    	
    	for(Edge edge : this.getOutgoing()) {
    		shape.getOutgoings().add(new Shape(edge.getId()));
    	}
    }
    
    
	/* Getter & Setter */
	
	/**
     * Gets the value of the incoming property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the incoming property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncoming().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<Edge> getIncoming() {
        if (incoming == null) {
            incoming = new ArrayList<Edge>();
        }
        return this.incoming;
    }

    /**
     * Gets the value of the outgoing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outgoing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutgoing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<Edge> getOutgoing() {
        if (outgoing == null) {
            outgoing = new ArrayList<Edge>();
        }
        return this.outgoing;
    }
	
	/**
     * Gets the value of the auditing property.
     * 
     * @return
     *     possible object is
     *     {@link TAuditing }
     *     
     */
//    public TAuditing getAuditing() {
//        return auditing;
//    }

    /**
     * Sets the value of the auditing property.
     * 
     * @param value
     *     allowed object is
     *     {@link TAuditing }
     *     
     */
//    public void setAuditing(TAuditing value) {
//        this.auditing = value;
//    }

    /**
     * Gets the value of the monitoring property.
     * 
     * @return
     *     possible object is
     *     {@link TMonitoring }
     *     
     */
//    public TMonitoring getMonitoring() {
//        return monitoring;
//    }
//
//    /**
//     * Sets the value of the monitoring property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link TMonitoring }
//     *     
//     */
//    public void setMonitoring(TMonitoring value) {
//        this.monitoring = value;
//    }

    /**
     * Gets the value of the categoryValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categoryValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategoryValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<QName> getCategoryValue() {
        if (categoryValue == null) {
            categoryValue = new ArrayList<QName>();
        }
        return this.categoryValue;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * @param process the process to set
	 */
	public void setProcess(Process process) {
		this.process = process;
	}

	/**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }
}
