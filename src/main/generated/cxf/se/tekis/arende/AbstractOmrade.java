
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for abstractOmrade complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="abstractOmrade"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractObjekt"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="beteckning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractOmrade", propOrder = {
    "beteckning"
})
@XmlSeeAlso({
    DetaljPlan.class,
    TillsynsOmrade.class,
    GenericOmrade.class,
    GraevOmrade.class
})
public abstract class AbstractOmrade
    extends AbstractObjekt
{

    protected String beteckning;

    /**
     * Gets the value of the beteckning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeteckning() {
        return beteckning;
    }

    /**
     * Sets the value of the beteckning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeteckning(String value) {
        this.beteckning = value;
    }

}
