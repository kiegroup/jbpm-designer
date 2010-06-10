
package de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für package komplexer Typ.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Inhalt an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="package">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="zip" type="{http://www.w3.org/2005/05/xmlmime}base64Binary"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "package", namespace = "http://www.apache.org/ode/deployapi", propOrder = {

})
public class Package {

    @XmlElement(namespace = "http://www.apache.org/ode/deployapi", required = true)
    protected Base64Binary zip;

    /**
     * Ruft den Wert der Eigenschaft zip ab.
     * 
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *     
     */
    public Base64Binary getZip() {
        return zip;
    }

    /**
     * Legt den Wert der Eigenschaft zip fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *     
     */
    public void setZip(Base64Binary value) {
        this.zip = value;
    }

}
