
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for prelRegByggnad complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="prelRegByggnad"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}regByggnad"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="andamalKod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="detaljAndamalKod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="geom" type="{www.tekis.se/arende}GeomType" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "prelRegByggnad", propOrder = {
    "andamalKod",
    "detaljAndamalKod",
    "geom"
})
public class PrelRegByggnad
    extends RegByggnad
{

    protected String andamalKod;
    protected String detaljAndamalKod;
    protected GeomType geom;

    /**
     * Gets the value of the andamalKod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAndamalKod() {
        return andamalKod;
    }

    /**
     * Sets the value of the andamalKod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAndamalKod(String value) {
        this.andamalKod = value;
    }

    /**
     * Gets the value of the detaljAndamalKod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetaljAndamalKod() {
        return detaljAndamalKod;
    }

    /**
     * Sets the value of the detaljAndamalKod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetaljAndamalKod(String value) {
        this.detaljAndamalKod = value;
    }

    /**
     * Gets the value of the geom property.
     * 
     * @return
     *     possible object is
     *     {@link GeomType }
     *     
     */
    public GeomType getGeom() {
        return geom;
    }

    /**
     * Sets the value of the geom property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeomType }
     *     
     */
    public void setGeom(GeomType value) {
        this.geom = value;
    }

}
