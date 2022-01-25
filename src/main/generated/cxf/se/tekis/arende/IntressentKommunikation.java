
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for intressentKommunikation complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="intressentKommunikation"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="beskrivning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="komtyp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="attention" type="{www.tekis.se/arende}intressentAttention" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="arAktiv" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "intressentKommunikation", propOrder = {
    "beskrivning",
    "komtyp",
    "attention"
})
public class IntressentKommunikation {

    protected String beskrivning;
    protected String komtyp;
    protected IntressentAttention attention;
    @XmlAttribute(name = "arAktiv", required = true)
    protected boolean arAktiv;

    /**
     * Gets the value of the beskrivning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeskrivning() {
        return beskrivning;
    }

    /**
     * Sets the value of the beskrivning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeskrivning(String value) {
        this.beskrivning = value;
    }

    /**
     * Gets the value of the komtyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKomtyp() {
        return komtyp;
    }

    /**
     * Sets the value of the komtyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKomtyp(String value) {
        this.komtyp = value;
    }

    /**
     * Gets the value of the attention property.
     * 
     * @return
     *     possible object is
     *     {@link IntressentAttention }
     *     
     */
    public IntressentAttention getAttention() {
        return attention;
    }

    /**
     * Sets the value of the attention property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntressentAttention }
     *     
     */
    public void setAttention(IntressentAttention value) {
        this.attention = value;
    }

    /**
     * Gets the value of the arAktiv property.
     * 
     */
    public boolean isArAktiv() {
        return arAktiv;
    }

    /**
     * Sets the value of the arAktiv property.
     * 
     */
    public void setArAktiv(boolean value) {
        this.arAktiv = value;
    }

}
