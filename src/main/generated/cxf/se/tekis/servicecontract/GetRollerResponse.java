
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
 *         &amp;lt;element name="GetRollerResult" type="{www.tekis.se/ServiceContract}ArrayOfRoll" minOccurs="0"/&amp;gt;
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
    "getRollerResult"
})
@XmlRootElement(name = "GetRollerResponse")
public class GetRollerResponse {

    @XmlElement(name = "GetRollerResult")
    protected ArrayOfRoll getRollerResult;

    /**
     * Gets the value of the getRollerResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRoll }
     *     
     */
    public ArrayOfRoll getGetRollerResult() {
        return getRollerResult;
    }

    /**
     * Sets the value of the getRollerResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRoll }
     *     
     */
    public void setGetRollerResult(ArrayOfRoll value) {
        this.getRollerResult = value;
    }

}
