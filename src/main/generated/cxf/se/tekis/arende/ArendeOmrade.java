
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for arendeOmrade complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arendeOmrade"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeObjekt"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="omrade" type="{www.tekis.se/arende}abstractOmrade" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arendeOmrade", propOrder = {
    "omrade"
})
public class ArendeOmrade
    extends AbstractArendeObjekt
{

    protected AbstractOmrade omrade;

    /**
     * Gets the value of the omrade property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractOmrade }
     *     
     */
    public AbstractOmrade getOmrade() {
        return omrade;
    }

    /**
     * Sets the value of the omrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractOmrade }
     *     
     */
    public void setOmrade(AbstractOmrade value) {
        this.omrade = value;
    }

}
