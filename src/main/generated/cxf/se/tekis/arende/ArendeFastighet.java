
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for arendeFastighet complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arendeFastighet"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeFastighet"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="fastighet" type="{www.tekis.se/arende}fastighet" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="specFastOmr" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arendeFastighet", propOrder = {
    "fastighet",
    "specFastOmr"
})
public class ArendeFastighet
    extends AbstractArendeFastighet
{

    protected Fastighet fastighet;
    protected Short specFastOmr;

    /**
     * Gets the value of the fastighet property.
     * 
     * @return
     *     possible object is
     *     {@link Fastighet }
     *     
     */
    public Fastighet getFastighet() {
        return fastighet;
    }

    /**
     * Sets the value of the fastighet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fastighet }
     *     
     */
    public void setFastighet(Fastighet value) {
        this.fastighet = value;
    }

    /**
     * Gets the value of the specFastOmr property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSpecFastOmr() {
        return specFastOmr;
    }

    /**
     * Sets the value of the specFastOmr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSpecFastOmr(Short value) {
        this.specFastOmr = value;
    }

}
