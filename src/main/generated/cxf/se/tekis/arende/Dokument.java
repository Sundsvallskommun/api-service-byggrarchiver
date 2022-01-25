
package se.tekis.arende;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * &lt;p&gt;Java class for dokument complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="dokument"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="namn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="beskrivning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="skapadDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handlaggare" type="{www.tekis.se/arende}handlaggareIdentity" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fil" type="{www.tekis.se/arende}dokumentFil" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="dokId" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dokument", propOrder = {
    "namn",
    "beskrivning",
    "skapadDatum",
    "handlaggare",
    "fil"
})
public class Dokument {

    protected String namn;
    protected String beskrivning;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime skapadDatum;
    protected HandlaggareIdentity handlaggare;
    protected DokumentFil fil;
    @XmlAttribute(name = "dokId")
    protected String dokId;
    @XmlAttribute(name = "checksum")
    protected String checksum;

    /**
     * Gets the value of the namn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamn() {
        return namn;
    }

    /**
     * Sets the value of the namn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamn(String value) {
        this.namn = value;
    }

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
     * Gets the value of the skapadDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getSkapadDatum() {
        return skapadDatum;
    }

    /**
     * Sets the value of the skapadDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkapadDatum(LocalDateTime value) {
        this.skapadDatum = value;
    }

    /**
     * Gets the value of the handlaggare property.
     * 
     * @return
     *     possible object is
     *     {@link HandlaggareIdentity }
     *     
     */
    public HandlaggareIdentity getHandlaggare() {
        return handlaggare;
    }

    /**
     * Sets the value of the handlaggare property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandlaggareIdentity }
     *     
     */
    public void setHandlaggare(HandlaggareIdentity value) {
        this.handlaggare = value;
    }

    /**
     * Gets the value of the fil property.
     * 
     * @return
     *     possible object is
     *     {@link DokumentFil }
     *     
     */
    public DokumentFil getFil() {
        return fil;
    }

    /**
     * Sets the value of the fil property.
     * 
     * @param value
     *     allowed object is
     *     {@link DokumentFil }
     *     
     */
    public void setFil(DokumentFil value) {
        this.fil = value;
    }

    /**
     * Gets the value of the dokId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDokId() {
        return dokId;
    }

    /**
     * Sets the value of the dokId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDokId(String value) {
        this.dokId = value;
    }

    /**
     * Gets the value of the checksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the value of the checksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChecksum(String value) {
        this.checksum = value;
    }

}
