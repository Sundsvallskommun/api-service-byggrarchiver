
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for belagenhetAdress complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="belagenhetAdress"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractObjekt"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="adressOmrade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="adressPlatsNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="adrPlId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "belagenhetAdress", propOrder = {
    "adressOmrade",
    "adressPlatsNr"
})
@XmlSeeAlso({
    PrelBelagenhetAdress.class
})
public class BelagenhetAdress
    extends AbstractObjekt
{

    protected String adressOmrade;
    protected String adressPlatsNr;
    @XmlAttribute(name = "adrPlId")
    protected Integer adrPlId;

    /**
     * Gets the value of the adressOmrade property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdressOmrade() {
        return adressOmrade;
    }

    /**
     * Sets the value of the adressOmrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdressOmrade(String value) {
        this.adressOmrade = value;
    }

    /**
     * Gets the value of the adressPlatsNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdressPlatsNr() {
        return adressPlatsNr;
    }

    /**
     * Sets the value of the adressPlatsNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdressPlatsNr(String value) {
        this.adressPlatsNr = value;
    }

    /**
     * Gets the value of the adrPlId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAdrPlId() {
        return adrPlId;
    }

    /**
     * Sets the value of the adrPlId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAdrPlId(Integer value) {
        this.adrPlId = value;
    }

}
