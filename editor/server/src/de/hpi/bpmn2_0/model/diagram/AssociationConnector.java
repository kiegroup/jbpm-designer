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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.connector.Association;


/**
 * <p>Java class for associationConnectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="associationConnectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bpmndi.org}bpmnConnectorType">
 *       &lt;attribute name="isDirectional" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "associationConnectorType", namespace = "http://bpmndi.org")
@XmlSeeAlso({
    AssociationConnector.class,
    DataAssociationConnector.class
})
public class AssociationConnector
    extends BpmnConnector
{
	
	@XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Association associationRef;

	@XmlAttribute
	protected Boolean isDirectional;
	
    /**
     * Gets the value of the associationRef property.
     * 
     * @return
     *     possible object is
     *     {@link Association }
     *     
     */
    public Association getAssociationRef() {
        return associationRef;
    }

    /**
     * Sets the value of the associationRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Association }
     *     
     */
    public void setAssociationRef(Association value) {
        this.associationRef = value;
    }
	

    /**
     * Gets the value of the isDirectional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsDirectional() {
        return isDirectional;
    }

    /**
     * Sets the value of the isDirectional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDirectional(Boolean value) {
        this.isDirectional = value;
    }

}
