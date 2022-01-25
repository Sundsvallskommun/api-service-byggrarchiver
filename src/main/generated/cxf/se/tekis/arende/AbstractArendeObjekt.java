
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for abstractArendeObjekt complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="abstractArendeObjekt"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeObjektId"&amp;gt;
 *       &amp;lt;attribute name="arHuvudObjekt" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *       &amp;lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractArendeObjekt")
@XmlSeeAlso({
    ArendeOmrade.class,
    AbstractArendeFastighet.class
})
public abstract class AbstractArendeObjekt
    extends AbstractArendeObjektId
{

    @XmlAttribute(name = "arHuvudObjekt", required = true)
    protected boolean arHuvudObjekt;
    @XmlAttribute(name = "checksum")
    protected String checksum;

    /**
     * Gets the value of the arHuvudObjekt property.
     * 
     */
    public boolean isArHuvudObjekt() {
        return arHuvudObjekt;
    }

    /**
     * Sets the value of the arHuvudObjekt property.
     * 
     */
    public void setArHuvudObjekt(boolean value) {
        this.arHuvudObjekt = value;
    }

    /**
     * Gets the value of the checksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the value of the checksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChecksum(String value) {
        this.checksum = value;
    }

}
