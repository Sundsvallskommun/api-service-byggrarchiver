
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for Roll complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="Roll"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="RollKod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="Beskrivning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="ArAktiv" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Roll", propOrder = {
    "rollKod",
    "beskrivning",
    "arAktiv"
})
public class Roll {

    @XmlElement(name = "RollKod")
    protected String rollKod;
    @XmlElement(name = "Beskrivning")
    protected String beskrivning;
    @XmlElement(name = "ArAktiv")
    protected boolean arAktiv;

    /**
     * Gets the value of the rollKod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRollKod() {
        return rollKod;
    }

    /**
     * Sets the value of the rollKod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRollKod(String value) {
        this.rollKod = value;
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

}
