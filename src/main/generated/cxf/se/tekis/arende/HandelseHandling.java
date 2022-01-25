
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for handelseHandling complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="handelseHandling"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}handling"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="refTyp" type="{www.tekis.se/arende}referensTyp" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "handelseHandling", propOrder = {
    "refTyp"
})
public class HandelseHandling
    extends Handling
{

    @XmlSchemaType(name = "string")
    protected ReferensTyp refTyp;

    /**
     * Gets the value of the refTyp property.
     * 
     * @return
     *     possible object is
     *     {@link ReferensTyp }
     *     
     */
    public ReferensTyp getRefTyp() {
        return refTyp;
    }

    /**
     * Sets the value of the refTyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferensTyp }
     *     
     */
    public void setRefTyp(ReferensTyp value) {
        this.refTyp = value;
    }

}
