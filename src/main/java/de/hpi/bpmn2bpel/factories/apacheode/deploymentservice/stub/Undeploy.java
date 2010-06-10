
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
 *         &lt;element name="packageName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
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
    "packageName"
})
@XmlRootElement(name = "undeploy")
public class Undeploy {

    @XmlElement(required = true)
    protected QName packageName;

    /**
     * Ruft den Wert der Eigenschaft packageName ab.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPackageName() {
        return packageName;
    }

    /**
     * Legt den Wert der Eigenschaft packageName fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPackageName(QName value) {
        this.packageName = value;
    }

}
