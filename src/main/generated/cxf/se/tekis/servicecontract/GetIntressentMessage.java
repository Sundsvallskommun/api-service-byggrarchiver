
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetIntressentMessage complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="GetIntressentMessage"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="HandlaggarSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="PersOrgNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="KundNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="AttentionPersNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="StatusFilter" type="{www.tekis.se/ServiceContract}StatusFilter"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetIntressentMessage", propOrder = {
    "handlaggarSign",
    "persOrgNr",
    "kundNr",
    "attentionPersNr",
    "statusFilter"
})
public class GetIntressentMessage {

    @XmlElement(name = "HandlaggarSign")
    protected String handlaggarSign;
    @XmlElement(name = "PersOrgNr")
    protected String persOrgNr;
    @XmlElement(name = "KundNr")
    protected String kundNr;
    @XmlElement(name = "AttentionPersNr")
    protected String attentionPersNr;
    @XmlElement(name = "StatusFilter", required = true)
    @XmlSchemaType(name = "string")
    protected StatusFilter statusFilter;

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

    /**
     * Gets the value of the attentionPersNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttentionPersNr() {
        return attentionPersNr;
    }

    /**
     * Sets the value of the attentionPersNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttentionPersNr(String value) {
        this.attentionPersNr = value;
    }

    /**
     * Gets the value of the statusFilter property.
     * 
     * @return
     *     possible object is
     *     {@link StatusFilter }
     *     
     */
    public StatusFilter getStatusFilter() {
        return statusFilter;
    }

    /**
     * Sets the value of the statusFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusFilter }
     *     
     */
    public void setStatusFilter(StatusFilter value) {
        this.statusFilter = value;
    }

}
