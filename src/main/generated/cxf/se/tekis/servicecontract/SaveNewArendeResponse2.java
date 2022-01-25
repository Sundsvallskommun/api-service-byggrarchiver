
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SaveNewArendeResponse complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SaveNewArendeResponse"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="Dnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveNewArendeResponse", propOrder = {
    "dnr"
})
public class SaveNewArendeResponse2 {

    @XmlElement(name = "Dnr")
    protected String dnr;

    /**
     * Gets the value of the dnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnr() {
        return dnr;
    }

    /**
     * Sets the value of the dnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnr(String value) {
        this.dnr = value;
    }

}
