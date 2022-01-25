
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetArendeObjektResponse complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="GetArendeObjektResponse"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="ArendeObjekt" type="{www.tekis.se/ServiceContract}ArrayOfAbstractArendeObjekt" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetArendeObjektResponse", propOrder = {
    "arendeObjekt"
})
public class GetArendeObjektResponse2 {

    @XmlElement(name = "ArendeObjekt")
    protected ArrayOfAbstractArendeObjekt arendeObjekt;

    /**
     * Gets the value of the arendeObjekt property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAbstractArendeObjekt }
     *     
     */
    public ArrayOfAbstractArendeObjekt getArendeObjekt() {
        return arendeObjekt;
    }

    /**
     * Sets the value of the arendeObjekt property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAbstractArendeObjekt }
     *     
     */
    public void setArendeObjekt(ArrayOfAbstractArendeObjekt value) {
        this.arendeObjekt = value;
    }

}
