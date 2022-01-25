
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import se.tekis.arende.Bevakning;


/**
 * &lt;p&gt;Java class for SaveBevakningMessage complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SaveBevakningMessage"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="HandlaggarSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Bevakning" type="{www.tekis.se/arende}bevakning" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Dnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="HandelseId" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveBevakningMessage", propOrder = {
    "handlaggarSign",
    "bevakning",
    "dnr",
    "handelseId"
})
public class SaveBevakningMessage {

    @XmlElement(name = "HandlaggarSign")
    protected String handlaggarSign;
    @XmlElement(name = "Bevakning")
    protected Bevakning bevakning;
    @XmlElement(name = "Dnr")
    protected String dnr;
    @XmlElement(name = "HandelseId", required = true, type = Integer.class, nillable = true)
    protected Integer handelseId;

    /**
     * Gets the value of the handlaggarSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandlaggarSign() {
        return handlaggarSign;
    }

    /**
     * Sets the value of the handlaggarSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandlaggarSign(String value) {
        this.handlaggarSign = value;
    }

    /**
     * Gets the value of the bevakning property.
     * 
     * @return
     *     possible object is
     *     {@link Bevakning }
     *     
     */
    public Bevakning getBevakning() {
        return bevakning;
    }

    /**
     * Sets the value of the bevakning property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bevakning }
     *     
     */
    public void setBevakning(Bevakning value) {
        this.bevakning = value;
    }

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

    /**
     * Gets the value of the handelseId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHandelseId() {
        return handelseId;
    }

    /**
     * Sets the value of the handelseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHandelseId(Integer value) {
        this.handelseId = value;
    }

}
