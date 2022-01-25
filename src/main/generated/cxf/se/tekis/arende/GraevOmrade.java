
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for graevOmrade complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="graevOmrade"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractOmrade"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="geom" type="{www.tekis.se/arende}GeomType" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="graevOmradeId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "graevOmrade", propOrder = {
    "geom"
})
public class GraevOmrade
    extends AbstractOmrade
{

    protected GeomType geom;
    @XmlAttribute(name = "graevOmradeId")
    protected Integer graevOmradeId;

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
     * Gets the value of the graevOmradeId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGraevOmradeId() {
        return graevOmradeId;
    }

    /**
     * Sets the value of the graevOmradeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGraevOmradeId(Integer value) {
        this.graevOmradeId = value;
    }

}
