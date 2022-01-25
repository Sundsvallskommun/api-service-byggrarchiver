
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
 *         &amp;lt;element name="GetIntressentResult" type="{www.tekis.se/ServiceContract}GetIntressentResponse" minOccurs="0"/&amp;gt;
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
    "getIntressentResult"
})
@XmlRootElement(name = "GetIntressentResponse")
public class GetIntressentResponse {

    @XmlElement(name = "GetIntressentResult")
    protected GetIntressentResponse2 getIntressentResult;

    /**
     * Gets the value of the getIntressentResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetIntressentResponse2 }
     *     
     */
    public GetIntressentResponse2 getGetIntressentResult() {
        return getIntressentResult;
    }

    /**
     * Sets the value of the getIntressentResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetIntressentResponse2 }
     *     
     */
    public void setGetIntressentResult(GetIntressentResponse2 value) {
        this.getIntressentResult = value;
    }

}
