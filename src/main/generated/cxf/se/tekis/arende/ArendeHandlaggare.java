
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for arendeHandlaggare complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arendeHandlaggare"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="handlaggare" type="{www.tekis.se/arende}handlaggareIdentity" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arHuvudHandlaggare" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arendeHandlaggare", propOrder = {
    "handlaggare",
    "arHuvudHandlaggare"
})
public class ArendeHandlaggare {

    protected HandlaggareIdentity handlaggare;
    protected boolean arHuvudHandlaggare;

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
     * Gets the value of the arHuvudHandlaggare property.
     * 
     */
    public boolean isArHuvudHandlaggare() {
        return arHuvudHandlaggare;
    }

    /**
     * Sets the value of the arHuvudHandlaggare property.
     * 
     */
    public void setArHuvudHandlaggare(boolean value) {
        this.arHuvudHandlaggare = value;
    }

}
