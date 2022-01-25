
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for handlaggareIdentity complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="handlaggareIdentity"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;attribute name="handlaggareId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="signatur" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "handlaggareIdentity")
@XmlSeeAlso({
    HandlaggareBas.class
})
public class HandlaggareIdentity {

    @XmlAttribute(name = "handlaggareId")
    protected Integer handlaggareId;
    @XmlAttribute(name = "signatur")
    protected String signatur;

    /**
     * Gets the value of the handlaggareId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHandlaggareId() {
        return handlaggareId;
    }

    /**
     * Sets the value of the handlaggareId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHandlaggareId(Integer value) {
        this.handlaggareId = value;
    }

    /**
     * Gets the value of the signatur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignatur() {
        return signatur;
    }

    /**
     * Sets the value of the signatur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignatur(String value) {
        this.signatur = value;
    }

}
