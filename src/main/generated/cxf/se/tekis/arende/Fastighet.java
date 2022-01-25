
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for fastighet complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="fastighet"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractObjekt"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="trakt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fbetNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="fnr" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fastighet", propOrder = {
    "trakt",
    "fbetNr"
})
@XmlSeeAlso({
    PrelFastighet.class
})
public class Fastighet
    extends AbstractObjekt
{

    protected String trakt;
    protected String fbetNr;
    @XmlAttribute(name = "fnr")
    protected Integer fnr;

    /**
     * Gets the value of the trakt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrakt() {
        return trakt;
    }

    /**
     * Sets the value of the trakt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrakt(String value) {
        this.trakt = value;
    }

    /**
     * Gets the value of the fbetNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFbetNr() {
        return fbetNr;
    }

    /**
     * Sets the value of the fbetNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFbetNr(String value) {
        this.fbetNr = value;
    }

    /**
     * Gets the value of the fnr property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFnr() {
        return fnr;
    }

    /**
     * Sets the value of the fnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFnr(Integer value) {
        this.fnr = value;
    }

}
