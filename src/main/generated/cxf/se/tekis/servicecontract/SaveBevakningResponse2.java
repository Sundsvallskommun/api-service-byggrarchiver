
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import se.tekis.arende.Bevakning;


/**
 * &lt;p&gt;Java class for SaveBevakningResponse complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SaveBevakningResponse"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="Bevakning" type="{www.tekis.se/arende}bevakning" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveBevakningResponse", propOrder = {
    "bevakning"
})
public class SaveBevakningResponse2 {

    @XmlElement(name = "Bevakning")
    protected Bevakning bevakning;

    /**
     * Gets the value of the bevakning property.
     * 
     * @return
     *     possible object is
     *     {@link Bevakning }
     *     
     */
    public Bevakning getBevakning() {
        return bevakning;
    }

    /**
     * Sets the value of the bevakning property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bevakning }
     *     
     */
    public void setBevakning(Bevakning value) {
        this.bevakning = value;
    }

}
