
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
 *         &amp;lt;element name="GetUpdatedArendenCountResult" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
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
    "getUpdatedArendenCountResult"
})
@XmlRootElement(name = "GetUpdatedArendenCountResponse")
public class GetUpdatedArendenCountResponse {

    @XmlElement(name = "GetUpdatedArendenCountResult")
    protected int getUpdatedArendenCountResult;

    /**
     * Gets the value of the getUpdatedArendenCountResult property.
     * 
     */
    public int getGetUpdatedArendenCountResult() {
        return getUpdatedArendenCountResult;
    }

    /**
     * Sets the value of the getUpdatedArendenCountResult property.
     * 
     */
    public void setGetUpdatedArendenCountResult(int value) {
        this.getUpdatedArendenCountResult = value;
    }

}
