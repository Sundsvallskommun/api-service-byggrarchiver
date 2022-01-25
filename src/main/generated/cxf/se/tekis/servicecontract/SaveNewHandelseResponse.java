
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
 *         &amp;lt;element name="SaveNewHandelseResult" type="{www.tekis.se/ServiceContract}SaveNewHandelseResponse" minOccurs="0"/&amp;gt;
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
    "saveNewHandelseResult"
})
@XmlRootElement(name = "SaveNewHandelseResponse")
public class SaveNewHandelseResponse {

    @XmlElement(name = "SaveNewHandelseResult")
    protected SaveNewHandelseResponse2 saveNewHandelseResult;

    /**
     * Gets the value of the saveNewHandelseResult property.
     * 
     * @return
     *     possible object is
     *     {@link SaveNewHandelseResponse2 }
     *     
     */
    public SaveNewHandelseResponse2 getSaveNewHandelseResult() {
        return saveNewHandelseResult;
    }

    /**
     * Sets the value of the saveNewHandelseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SaveNewHandelseResponse2 }
     *     
     */
    public void setSaveNewHandelseResult(SaveNewHandelseResponse2 value) {
        this.saveNewHandelseResult = value;
    }

}
