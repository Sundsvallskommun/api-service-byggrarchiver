
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import se.tekis.arende.Handelse;


/**
 * &lt;p&gt;Java class for SaveNewHandelseMessage complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SaveNewHandelseMessage"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="Dnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="HandlaggarSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Handelse" type="{www.tekis.se/arende}handelse" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Handlingar" type="{www.tekis.se/ServiceContract}ArrayOfHandling" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="BesvaradHandelseId" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *         &amp;lt;element name="AnkomststamplaHandlingar" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="AutoGenereraBeslutNr" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveNewHandelseMessage", propOrder = {
    "dnr",
    "handlaggarSign",
    "handelse",
    "handlingar",
    "besvaradHandelseId",
    "ankomststamplaHandlingar",
    "autoGenereraBeslutNr"
})
public class SaveNewHandelseMessage {

    @XmlElement(name = "Dnr")
    protected String dnr;
    @XmlElement(name = "HandlaggarSign")
    protected String handlaggarSign;
    @XmlElement(name = "Handelse")
    protected Handelse handelse;
    @XmlElement(name = "Handlingar")
    protected ArrayOfHandling handlingar;
    @XmlElement(name = "BesvaradHandelseId", required = true, type = Integer.class, nillable = true)
    protected Integer besvaradHandelseId;
    @XmlElement(name = "AnkomststamplaHandlingar")
    protected boolean ankomststamplaHandlingar;
    @XmlElement(name = "AutoGenereraBeslutNr")
    protected boolean autoGenereraBeslutNr;

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
     * Gets the value of the handelse property.
     * 
     * @return
     *     possible object is
     *     {@link Handelse }
     *     
     */
    public Handelse getHandelse() {
        return handelse;
    }

    /**
     * Sets the value of the handelse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Handelse }
     *     
     */
    public void setHandelse(Handelse value) {
        this.handelse = value;
    }

    /**
     * Gets the value of the handlingar property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHandling }
     *     
     */
    public ArrayOfHandling getHandlingar() {
        return handlingar;
    }

    /**
     * Sets the value of the handlingar property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHandling }
     *     
     */
    public void setHandlingar(ArrayOfHandling value) {
        this.handlingar = value;
    }

    /**
     * Gets the value of the besvaradHandelseId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBesvaradHandelseId() {
        return besvaradHandelseId;
    }

    /**
     * Sets the value of the besvaradHandelseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBesvaradHandelseId(Integer value) {
        this.besvaradHandelseId = value;
    }

    /**
     * Gets the value of the ankomststamplaHandlingar property.
     * 
     */
    public boolean isAnkomststamplaHandlingar() {
        return ankomststamplaHandlingar;
    }

    /**
     * Sets the value of the ankomststamplaHandlingar property.
     * 
     */
    public void setAnkomststamplaHandlingar(boolean value) {
        this.ankomststamplaHandlingar = value;
    }

    /**
     * Gets the value of the autoGenereraBeslutNr property.
     * 
     */
    public boolean isAutoGenereraBeslutNr() {
        return autoGenereraBeslutNr;
    }

    /**
     * Sets the value of the autoGenereraBeslutNr property.
     * 
     */
    public void setAutoGenereraBeslutNr(boolean value) {
        this.autoGenereraBeslutNr = value;
    }

}
