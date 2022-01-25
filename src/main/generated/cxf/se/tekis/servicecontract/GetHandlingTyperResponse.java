
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
 *         &amp;lt;element name="GetHandlingTyperResult" type="{www.tekis.se/ServiceContract}ArrayOfHandlingTyp" minOccurs="0"/&amp;gt;
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
    "getHandlingTyperResult"
})
@XmlRootElement(name = "GetHandlingTyperResponse")
public class GetHandlingTyperResponse {

    @XmlElement(name = "GetHandlingTyperResult")
    protected ArrayOfHandlingTyp getHandlingTyperResult;

    /**
     * Gets the value of the getHandlingTyperResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHandlingTyp }
     *     
     */
    public ArrayOfHandlingTyp getGetHandlingTyperResult() {
        return getHandlingTyperResult;
    }

    /**
     * Sets the value of the getHandlingTyperResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHandlingTyp }
     *     
     */
    public void setGetHandlingTyperResult(ArrayOfHandlingTyp value) {
        this.getHandlingTyperResult = value;
    }

}
