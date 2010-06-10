
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
 *         &lt;element name="deployedPackages" type="{http://www.apache.org/ode/deployapi}packageNames"/>
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
    "deployedPackages"
})
@XmlRootElement(name = "listDeployedPackagesResponse")
public class ListDeployedPackagesResponse {

    @XmlElement(required = true)
    protected PackageNames deployedPackages;

    /**
     * Ruft den Wert der Eigenschaft deployedPackages ab.
     * 
     * @return
     *     possible object is
     *     {@link PackageNames }
     *     
     */
    public PackageNames getDeployedPackages() {
        return deployedPackages;
    }

    /**
     * Legt den Wert der Eigenschaft deployedPackages fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageNames }
     *     
     */
    public void setDeployedPackages(PackageNames value) {
        this.deployedPackages = value;
    }

}
