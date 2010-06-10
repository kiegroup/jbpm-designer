
package de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


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
 *         &lt;element name="processName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
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
    "processName"
})
@XmlRootElement(name = "getProcessPackage")
public class GetProcessPackage {

    @XmlElement(required = true)
    protected QName processName;

    /**
     * Ruft den Wert der Eigenschaft processName ab.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getProcessName() {
        return processName;
    }

    /**
     * Legt den Wert der Eigenschaft processName fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setProcessName(QName value) {
        this.processName = value;
    }

}
