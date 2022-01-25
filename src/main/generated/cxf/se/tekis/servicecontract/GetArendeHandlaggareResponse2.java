
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetArendeHandlaggareResponse complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="GetArendeHandlaggareResponse"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="ArendeHandlaggare" type="{www.tekis.se/ServiceContract}ArrayOfArendeHandlaggare" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetArendeHandlaggareResponse", propOrder = {
    "arendeHandlaggare"
})
@XmlSeeAlso({
    SaveArendeHandlaggareResponse2 .class
})
public class GetArendeHandlaggareResponse2 {

    @XmlElement(name = "ArendeHandlaggare")
    protected ArrayOfArendeHandlaggare arendeHandlaggare;

    /**
     * Gets the value of the arendeHandlaggare property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArendeHandlaggare }
     *     
     */
    public ArrayOfArendeHandlaggare getArendeHandlaggare() {
        return arendeHandlaggare;
    }

    /**
     * Sets the value of the arendeHandlaggare property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArendeHandlaggare }
     *     
     */
    public void setArendeHandlaggare(ArrayOfArendeHandlaggare value) {
        this.arendeHandlaggare = value;
    }

}
