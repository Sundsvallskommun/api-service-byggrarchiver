
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for arendeBelagenhetAdress complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arendeBelagenhetAdress"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeObjektChild"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="belAdress" type="{www.tekis.se/arende}belagenhetAdress" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arendeBelagenhetAdress", propOrder = {
    "belAdress"
})
public class ArendeBelagenhetAdress
    extends AbstractArendeObjektChild
{

    protected BelagenhetAdress belAdress;

    /**
     * Gets the value of the belAdress property.
     * 
     * @return
     *     possible object is
     *     {@link BelagenhetAdress }
     *     
     */
    public BelagenhetAdress getBelAdress() {
        return belAdress;
    }

    /**
     * Sets the value of the belAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link BelagenhetAdress }
     *     
     */
    public void setBelAdress(BelagenhetAdress value) {
        this.belAdress = value;
    }

}
