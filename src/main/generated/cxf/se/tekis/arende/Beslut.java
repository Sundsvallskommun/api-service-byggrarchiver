
package se.tekis.arende;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * &lt;p&gt;Java class for beslut complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="beslut"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="beslutstext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="delegatHandlaggare" type="{www.tekis.se/arende}handlaggareBas" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="instanstyp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="giltigTillDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arMindreAvvikelse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="beslutNr" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="arHuvudbeslut" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "beslut", propOrder = {
    "beslutstext",
    "delegatHandlaggare",
    "instanstyp",
    "giltigTillDatum",
    "arMindreAvvikelse"
})
public class Beslut {

    protected String beslutstext;
    protected HandlaggareBas delegatHandlaggare;
    protected String instanstyp;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate giltigTillDatum;
    protected Boolean arMindreAvvikelse;
    @XmlAttribute(name = "beslutNr")
    protected String beslutNr;
    @XmlAttribute(name = "arHuvudbeslut", required = true)
    protected boolean arHuvudbeslut;

    /**
     * Gets the value of the beslutstext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeslutstext() {
        return beslutstext;
    }

    /**
     * Sets the value of the beslutstext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeslutstext(String value) {
        this.beslutstext = value;
    }

    /**
     * Gets the value of the delegatHandlaggare property.
     * 
     * @return
     *     possible object is
     *     {@link HandlaggareBas }
     *     
     */
    public HandlaggareBas getDelegatHandlaggare() {
        return delegatHandlaggare;
    }

    /**
     * Sets the value of the delegatHandlaggare property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandlaggareBas }
     *     
     */
    public void setDelegatHandlaggare(HandlaggareBas value) {
        this.delegatHandlaggare = value;
    }

    /**
     * Gets the value of the instanstyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanstyp() {
        return instanstyp;
    }

    /**
     * Sets the value of the instanstyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanstyp(String value) {
        this.instanstyp = value;
    }

    /**
     * Gets the value of the giltigTillDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getGiltigTillDatum() {
        return giltigTillDatum;
    }

    /**
     * Sets the value of the giltigTillDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiltigTillDatum(LocalDate value) {
        this.giltigTillDatum = value;
    }

    /**
     * Gets the value of the arMindreAvvikelse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isArMindreAvvikelse() {
        return arMindreAvvikelse;
    }

    /**
     * Sets the value of the arMindreAvvikelse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArMindreAvvikelse(Boolean value) {
        this.arMindreAvvikelse = value;
    }

    /**
     * Gets the value of the beslutNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeslutNr() {
        return beslutNr;
    }

    /**
     * Sets the value of the beslutNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeslutNr(String value) {
        this.beslutNr = value;
    }

    /**
     * Gets the value of the arHuvudbeslut property.
     * 
     */
    public boolean isArHuvudbeslut() {
        return arHuvudbeslut;
    }

    /**
     * Sets the value of the arHuvudbeslut property.
     * 
     */
    public void setArHuvudbeslut(boolean value) {
        this.arHuvudbeslut = value;
    }

}
