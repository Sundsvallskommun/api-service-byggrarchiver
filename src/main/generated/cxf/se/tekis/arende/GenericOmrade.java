
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for genericOmrade complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="genericOmrade"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractOmrade"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="geom" type="{www.tekis.se/arende}GeomType" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="genericOmradeTypId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="genericOmradeId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genericOmrade", propOrder = {
    "geom"
})
public class GenericOmrade
    extends AbstractOmrade
{

    protected GeomType geom;
    @XmlAttribute(name = "genericOmradeTypId", required = true)
    protected int genericOmradeTypId;
    @XmlAttribute(name = "genericOmradeId")
    protected Integer genericOmradeId;

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

    /**
     * Gets the value of the genericOmradeTypId property.
     * 
     */
    public int getGenericOmradeTypId() {
        return genericOmradeTypId;
    }

    /**
     * Sets the value of the genericOmradeTypId property.
     * 
     */
    public void setGenericOmradeTypId(int value) {
        this.genericOmradeTypId = value;
    }

    /**
     * Gets the value of the genericOmradeId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGenericOmradeId() {
        return genericOmradeId;
    }

    /**
     * Sets the value of the genericOmradeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGenericOmradeId(Integer value) {
        this.genericOmradeId = value;
    }

}
