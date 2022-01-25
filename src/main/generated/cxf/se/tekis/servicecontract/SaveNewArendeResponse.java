
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
 *         &amp;lt;element name="SaveNewArendeResult" type="{www.tekis.se/ServiceContract}SaveNewArendeResponse" minOccurs="0"/&amp;gt;
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
    "saveNewArendeResult"
})
@XmlRootElement(name = "SaveNewArendeResponse")
public class SaveNewArendeResponse {

    @XmlElement(name = "SaveNewArendeResult")
    protected SaveNewArendeResponse2 saveNewArendeResult;

    /**
     * Gets the value of the saveNewArendeResult property.
     * 
     * @return
     *     possible object is
     *     {@link SaveNewArendeResponse2 }
     *     
     */
    public SaveNewArendeResponse2 getSaveNewArendeResult() {
        return saveNewArendeResult;
    }

    /**
     * Sets the value of the saveNewArendeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SaveNewArendeResponse2 }
     *     
     */
    public void setSaveNewArendeResult(SaveNewArendeResponse2 value) {
        this.saveNewArendeResult = value;
    }

}
