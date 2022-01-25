
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
 *         &amp;lt;element name="SaveBevakningResult" type="{www.tekis.se/ServiceContract}SaveBevakningResponse" minOccurs="0"/&amp;gt;
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
    "saveBevakningResult"
})
@XmlRootElement(name = "SaveBevakningResponse")
public class SaveBevakningResponse {

    @XmlElement(name = "SaveBevakningResult")
    protected SaveBevakningResponse2 saveBevakningResult;

    /**
     * Gets the value of the saveBevakningResult property.
     * 
     * @return
     *     possible object is
     *     {@link SaveBevakningResponse2 }
     *     
     */
    public SaveBevakningResponse2 getSaveBevakningResult() {
        return saveBevakningResult;
    }

    /**
     * Sets the value of the saveBevakningResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SaveBevakningResponse2 }
     *     
     */
    public void setSaveBevakningResult(SaveBevakningResponse2 value) {
        this.saveBevakningResult = value;
    }

}
