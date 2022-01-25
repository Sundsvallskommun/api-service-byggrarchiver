
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
 *         &amp;lt;element name="SaveArendeHandlaggareResult" type="{www.tekis.se/ServiceContract}SaveArendeHandlaggareResponse" minOccurs="0"/&amp;gt;
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
    "saveArendeHandlaggareResult"
})
@XmlRootElement(name = "SaveArendeHandlaggareResponse")
public class SaveArendeHandlaggareResponse {

    @XmlElement(name = "SaveArendeHandlaggareResult")
    protected SaveArendeHandlaggareResponse2 saveArendeHandlaggareResult;

    /**
     * Gets the value of the saveArendeHandlaggareResult property.
     * 
     * @return
     *     possible object is
     *     {@link SaveArendeHandlaggareResponse2 }
     *     
     */
    public SaveArendeHandlaggareResponse2 getSaveArendeHandlaggareResult() {
        return saveArendeHandlaggareResult;
    }

    /**
     * Sets the value of the saveArendeHandlaggareResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SaveArendeHandlaggareResponse2 }
     *     
     */
    public void setSaveArendeHandlaggareResult(SaveArendeHandlaggareResponse2 value) {
        this.saveArendeHandlaggareResult = value;
    }

}
