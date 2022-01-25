
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="GetRelateradeArendenByFastighetResult" type="{www.tekis.se/ServiceContract}ArrayOfArende1" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getRelateradeArendenByFastighetResult"
})
@XmlRootElement(name = "GetRelateradeArendenByFastighetResponse")
public class GetRelateradeArendenByFastighetResponse {

    @XmlElement(name = "GetRelateradeArendenByFastighetResult")
    protected ArrayOfArende1 getRelateradeArendenByFastighetResult;

    /**
     * Gets the value of the getRelateradeArendenByFastighetResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArende1 }
     *     
     */
    public ArrayOfArende1 getGetRelateradeArendenByFastighetResult() {
        return getRelateradeArendenByFastighetResult;
    }

    /**
     * Sets the value of the getRelateradeArendenByFastighetResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArende1 }
     *     
     */
    public void setGetRelateradeArendenByFastighetResult(ArrayOfArende1 value) {
        this.getRelateradeArendenByFastighetResult = value;
    }

}
