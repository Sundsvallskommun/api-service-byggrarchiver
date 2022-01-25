
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for arendeRegByggnad complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arendeRegByggnad"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeObjektChild"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="byggnad" type="{www.tekis.se/arende}regByggnad" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arendeRegByggnad", propOrder = {
    "byggnad"
})
public class ArendeRegByggnad
    extends AbstractArendeObjektChild
{

    protected RegByggnad byggnad;

    /**
     * Gets the value of the byggnad property.
     * 
     * @return
     *     possible object is
     *     {@link RegByggnad }
     *     
     */
    public RegByggnad getByggnad() {
        return byggnad;
    }

    /**
     * Sets the value of the byggnad property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegByggnad }
     *     
     */
    public void setByggnad(RegByggnad value) {
        this.byggnad = value;
    }

}
