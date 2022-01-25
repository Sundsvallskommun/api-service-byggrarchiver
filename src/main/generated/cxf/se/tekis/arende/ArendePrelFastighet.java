
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for arendePrelFastighet complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arendePrelFastighet"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeFastighet"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="fastighet" type="{www.tekis.se/arende}prelFastighet" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arendePrelFastighet", propOrder = {
    "fastighet"
})
public class ArendePrelFastighet
    extends AbstractArendeFastighet
{

    protected PrelFastighet fastighet;

    /**
     * Gets the value of the fastighet property.
     * 
     * @return
     *     possible object is
     *     {@link PrelFastighet }
     *     
     */
    public PrelFastighet getFastighet() {
        return fastighet;
    }

    /**
     * Sets the value of the fastighet property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrelFastighet }
     *     
     */
    public void setFastighet(PrelFastighet value) {
        this.fastighet = value;
    }

}
