
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for HandlingTyp complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="HandlingTyp"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="Typ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Beskrivning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="ArAktiv" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="SortOrdn" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandlingTyp", propOrder = {
    "typ",
    "beskrivning",
    "arAktiv",
    "sortOrdn"
})
public class HandlingTyp {

    @XmlElement(name = "Typ")
    protected String typ;
    @XmlElement(name = "Beskrivning")
    protected String beskrivning;
    @XmlElement(name = "ArAktiv")
    protected boolean arAktiv;
    @XmlElement(name = "SortOrdn")
    protected int sortOrdn;

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

    /**
     * Gets the value of the sortOrdn property.
     * 
     */
    public int getSortOrdn() {
        return sortOrdn;
    }

    /**
     * Sets the value of the sortOrdn property.
     * 
     */
    public void setSortOrdn(int value) {
        this.sortOrdn = value;
    }

}
