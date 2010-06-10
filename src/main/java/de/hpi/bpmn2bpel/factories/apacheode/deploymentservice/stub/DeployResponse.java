
package de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous komplexer Typ.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Inhalt an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="response" type="{http://www.apache.org/ode/deployapi}deployUnit"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "response"
})
@XmlRootElement(name = "deployResponse")
public class DeployResponse {

    @XmlElement(required = true)
    protected DeployUnit response;

    /**
     * Ruft den Wert der Eigenschaft response ab.
     * 
     * @return
     *     possible object is
     *     {@link DeployUnit }
     *     
     */
    public DeployUnit getResponse() {
        return response;
    }

    /**
     * Legt den Wert der Eigenschaft response fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DeployUnit }
     *     
     */
    public void setResponse(DeployUnit value) {
        this.response = value;
    }

}
