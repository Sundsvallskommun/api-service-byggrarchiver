
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
 *         &amp;lt;element name="GetArendeResult" type="{www.tekis.se/ServiceContract}Arende" minOccurs="0"/&amp;gt;
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
    "getArendeResult"
})
@XmlRootElement(name = "GetArendeResponse")
public class GetArendeResponse {

    @XmlElement(name = "GetArendeResult")
    protected Arende getArendeResult;

    /**
     * Gets the value of the getArendeResult property.
     * 
     * @return
     *     possible object is
     *     {@link Arende }
     *     
     */
    public Arende getGetArendeResult() {
        return getArendeResult;
    }

    /**
     * Sets the value of the getArendeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Arende }
     *     
     */
    public void setGetArendeResult(Arende value) {
        this.getArendeResult = value;
    }

}
