
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.opengis.gml.LineStringType;
import net.opengis.gml.PointType;
import net.opengis.gml.PolygonType;


/**
 * &lt;p&gt;Java class for GeomType complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="GeomType"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}AbstractGeomType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;choice&amp;gt;
 *           &amp;lt;element ref="{http://www.opengis.net/gml}Point" minOccurs="0"/&amp;gt;
 *           &amp;lt;element ref="{http://www.opengis.net/gml}LineString" minOccurs="0"/&amp;gt;
 *           &amp;lt;element ref="{http://www.opengis.net/gml}Polygon" minOccurs="0"/&amp;gt;
 *         &amp;lt;/choice&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeomType", propOrder = {
    "point",
    "lineString",
    "polygon"
})
public class GeomType
    extends AbstractGeomType
{

    @XmlElement(name = "Point", namespace = "http://www.opengis.net/gml")
    protected PointType point;
    @XmlElement(name = "LineString", namespace = "http://www.opengis.net/gml")
    protected LineStringType lineString;
    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    protected PolygonType polygon;

    /**
     * Gets the value of the point property.
     * 
     * @return
     *     possible object is
     *     {@link PointType }
     *     
     */
    public PointType getPoint() {
        return point;
    }

    /**
     * Sets the value of the point property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointType }
     *     
     */
    public void setPoint(PointType value) {
        this.point = value;
    }

    /**
     * Gets the value of the lineString property.
     * 
     * @return
     *     possible object is
     *     {@link LineStringType }
     *     
     */
    public LineStringType getLineString() {
        return lineString;
    }

    /**
     * Sets the value of the lineString property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineStringType }
     *     
     */
    public void setLineString(LineStringType value) {
        this.lineString = value;
    }

    /**
     * Gets the value of the polygon property.
     * 
     * @return
     *     possible object is
     *     {@link PolygonType }
     *     
     */
    public PolygonType getPolygon() {
        return polygon;
    }

    /**
     * Sets the value of the polygon property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolygonType }
     *     
     */
    public void setPolygon(PolygonType value) {
        this.polygon = value;
    }

}
