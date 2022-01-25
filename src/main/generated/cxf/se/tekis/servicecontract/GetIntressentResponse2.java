
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetIntressentResponse complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="GetIntressentResponse"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="Intressent" type="{www.tekis.se/ServiceContract}ArrayOfIntressent" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetIntressentResponse", propOrder = {
    "intressent"
})
public class GetIntressentResponse2 {

    @XmlElement(name = "Intressent")
    protected ArrayOfIntressent intressent;

    /**
     * Gets the value of the intressent property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfIntressent }
     *     
     */
    public ArrayOfIntressent getIntressent() {
        return intressent;
    }

    /**
     * Sets the value of the intressent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfIntressent }
     *     
     */
    public void setIntressent(ArrayOfIntressent value) {
        this.intressent = value;
    }

}
