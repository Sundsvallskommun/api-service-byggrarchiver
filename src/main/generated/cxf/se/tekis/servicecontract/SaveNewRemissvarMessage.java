
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SaveNewRemissvarMessage complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SaveNewRemissvarMessage"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="RemissId" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *         &amp;lt;element name="HandlaggarSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Erinran" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="Meddelande" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Handlingar" type="{www.tekis.se/ServiceContract}ArrayOfHandling" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveNewRemissvarMessage", propOrder = {
    "remissId",
    "handlaggarSign",
    "erinran",
    "meddelande",
    "handlingar"
})
public class SaveNewRemissvarMessage {

    @XmlElement(name = "RemissId")
    protected int remissId;
    @XmlElement(name = "HandlaggarSign")
    protected String handlaggarSign;
    @XmlElement(name = "Erinran")
    protected boolean erinran;
    @XmlElement(name = "Meddelande")
    protected String meddelande;
    @XmlElement(name = "Handlingar")
    protected ArrayOfHandling handlingar;

    /**
     * Gets the value of the remissId property.
     * 
     */
    public int getRemissId() {
        return remissId;
    }

    /**
     * Sets the value of the remissId property.
     * 
     */
    public void setRemissId(int value) {
        this.remissId = value;
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
     * Gets the value of the erinran property.
     * 
     */
    public boolean isErinran() {
        return erinran;
    }

    /**
     * Sets the value of the erinran property.
     * 
     */
    public void setErinran(boolean value) {
        this.erinran = value;
    }

    /**
     * Gets the value of the meddelande property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeddelande() {
        return meddelande;
    }

    /**
     * Sets the value of the meddelande property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeddelande(String value) {
        this.meddelande = value;
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

}
