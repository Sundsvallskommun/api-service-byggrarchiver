
package se.tekis.arende;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * &lt;p&gt;Java class for handling complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="handling"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="typ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handlingDatum" type="{http://www.w3.org/2001/XMLSchema}date"/&amp;gt;
 *         &amp;lt;element name="anteckning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="uuid" type="{http://microsoft.com/wsdl/types/}guid" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arkivStatus" type="{www.tekis.se/arende}ArkiveringStatus" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="makulerad" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="ejGallandeDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="dokument" type="{www.tekis.se/arende}dokument" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="handlingId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "handling", propOrder = {
    "typ",
    "status",
    "handlingDatum",
    "anteckning",
    "uuid",
    "arkivStatus",
    "makulerad",
    "ejGallandeDatum",
    "dokument"
})
@XmlSeeAlso({
    HandelseHandling.class
})
public class Handling {

    protected String typ;
    protected String status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate handlingDatum;
    protected String anteckning;
    protected String uuid;
    @XmlSchemaType(name = "string")
    protected ArkiveringStatus arkivStatus;
    protected boolean makulerad;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate ejGallandeDatum;
    protected Dokument dokument;
    @XmlAttribute(name = "handlingId", required = true)
    protected int handlingId;
    @XmlAttribute(name = "checksum")
    protected String checksum;

    /**
     * Gets the value of the typ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTyp() {
        return typ;
    }

    /**
     * Sets the value of the typ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTyp(String value) {
        this.typ = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the handlingDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getHandlingDatum() {
        return handlingDatum;
    }

    /**
     * Sets the value of the handlingDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandlingDatum(LocalDate value) {
        this.handlingDatum = value;
    }

    /**
     * Gets the value of the anteckning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnteckning() {
        return anteckning;
    }

    /**
     * Sets the value of the anteckning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnteckning(String value) {
        this.anteckning = value;
    }

    /**
     * Gets the value of the uuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the arkivStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ArkiveringStatus }
     *     
     */
    public ArkiveringStatus getArkivStatus() {
        return arkivStatus;
    }

    /**
     * Sets the value of the arkivStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArkiveringStatus }
     *     
     */
    public void setArkivStatus(ArkiveringStatus value) {
        this.arkivStatus = value;
    }

    /**
     * Gets the value of the makulerad property.
     * 
     */
    public boolean isMakulerad() {
        return makulerad;
    }

    /**
     * Sets the value of the makulerad property.
     * 
     */
    public void setMakulerad(boolean value) {
        this.makulerad = value;
    }

    /**
     * Gets the value of the ejGallandeDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getEjGallandeDatum() {
        return ejGallandeDatum;
    }

    /**
     * Sets the value of the ejGallandeDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEjGallandeDatum(LocalDate value) {
        this.ejGallandeDatum = value;
    }

    /**
     * Gets the value of the dokument property.
     * 
     * @return
     *     possible object is
     *     {@link Dokument }
     *     
     */
    public Dokument getDokument() {
        return dokument;
    }

    /**
     * Sets the value of the dokument property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dokument }
     *     
     */
    public void setDokument(Dokument value) {
        this.dokument = value;
    }

    /**
     * Gets the value of the handlingId property.
     * 
     */
    public int getHandlingId() {
        return handlingId;
    }

    /**
     * Sets the value of the handlingId property.
     * 
     */
    public void setHandlingId(int value) {
        this.handlingId = value;
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
