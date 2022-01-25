
package se.tekis.arende;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * &lt;p&gt;Java class for aktorbehorighet complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="aktorbehorighet"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="behorighetRoll" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="niva" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="nr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="certifieradAv" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="certifieradTillDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aktorbehorighet", propOrder = {
    "behorighetRoll",
    "niva",
    "nr",
    "certifieradAv",
    "certifieradTillDatum"
})
public class Aktorbehorighet {

    protected String behorighetRoll;
    protected String niva;
    protected String nr;
    protected String certifieradAv;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate certifieradTillDatum;

    /**
     * Gets the value of the behorighetRoll property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBehorighetRoll() {
        return behorighetRoll;
    }

    /**
     * Sets the value of the behorighetRoll property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBehorighetRoll(String value) {
        this.behorighetRoll = value;
    }

    /**
     * Gets the value of the niva property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNiva() {
        return niva;
    }

    /**
     * Sets the value of the niva property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNiva(String value) {
        this.niva = value;
    }

    /**
     * Gets the value of the nr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNr() {
        return nr;
    }

    /**
     * Sets the value of the nr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNr(String value) {
        this.nr = value;
    }

    /**
     * Gets the value of the certifieradAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertifieradAv() {
        return certifieradAv;
    }

    /**
     * Sets the value of the certifieradAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertifieradAv(String value) {
        this.certifieradAv = value;
    }

    /**
     * Gets the value of the certifieradTillDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getCertifieradTillDatum() {
        return certifieradTillDatum;
    }

    /**
     * Sets the value of the certifieradTillDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertifieradTillDatum(LocalDate value) {
        this.certifieradTillDatum = value;
    }

}
