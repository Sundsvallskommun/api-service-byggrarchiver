
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &amp;lt;element name="persOrgNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="kundNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
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
    "persOrgNr",
    "kundNr"
})
@XmlRootElement(name = "GetRelateradeArendenByPersOrgNr")
public class GetRelateradeArendenByPersOrgNr {

    protected String persOrgNr;
    protected String kundNr;

    /**
     * Gets the value of the persOrgNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersOrgNr() {
        return persOrgNr;
    }

    /**
     * Sets the value of the persOrgNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersOrgNr(String value) {
        this.persOrgNr = value;
    }

    /**
     * Gets the value of the kundNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKundNr() {
        return kundNr;
    }

    /**
     * Sets the value of the kundNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKundNr(String value) {
        this.kundNr = value;
    }

}
