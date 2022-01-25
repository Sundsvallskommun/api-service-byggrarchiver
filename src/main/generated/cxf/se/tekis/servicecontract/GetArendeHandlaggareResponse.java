
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
 *         &amp;lt;element name="GetArendeHandlaggareResult" type="{www.tekis.se/ServiceContract}GetArendeHandlaggareResponse" minOccurs="0"/&amp;gt;
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
    "getArendeHandlaggareResult"
})
@XmlRootElement(name = "GetArendeHandlaggareResponse")
public class GetArendeHandlaggareResponse {

    @XmlElement(name = "GetArendeHandlaggareResult")
    protected GetArendeHandlaggareResponse2 getArendeHandlaggareResult;

    /**
     * Gets the value of the getArendeHandlaggareResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetArendeHandlaggareResponse2 }
     *     
     */
    public GetArendeHandlaggareResponse2 getGetArendeHandlaggareResult() {
        return getArendeHandlaggareResult;
    }

    /**
     * Sets the value of the getArendeHandlaggareResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetArendeHandlaggareResponse2 }
     *     
     */
    public void setGetArendeHandlaggareResult(GetArendeHandlaggareResponse2 value) {
        this.getArendeHandlaggareResult = value;
    }

}
