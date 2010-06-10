
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
 *         &lt;element name="processIds" type="{http://www.apache.org/ode/deployapi}processIds"/>
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
    "processIds"
})
@XmlRootElement(name = "listProcessesResponse")
public class ListProcessesResponse {

    @XmlElement(required = true)
    protected ProcessIds processIds;

    /**
     * Ruft den Wert der Eigenschaft processIds ab.
     * 
     * @return
     *     possible object is
     *     {@link ProcessIds }
     *     
     */
    public ProcessIds getProcessIds() {
        return processIds;
    }

    /**
     * Legt den Wert der Eigenschaft processIds fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessIds }
     *     
     */
    public void setProcessIds(ProcessIds value) {
        this.processIds = value;
    }

}
