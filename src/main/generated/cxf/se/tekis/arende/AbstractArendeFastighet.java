
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for abstractArendeFastighet complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="abstractArendeFastighet"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeObjekt"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="belAdressList" type="{www.tekis.se/arende}ArrayOfArendeBelagenhetAdress" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="byggnadList" type="{www.tekis.se/arende}ArrayOfArendeRegByggnad" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="specGeom" type="{www.tekis.se/arende}GeomType" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractArendeFastighet", propOrder = {
    "belAdressList",
    "byggnadList",
    "specGeom"
})
@XmlSeeAlso({
    ArendeFastighet.class,
    ArendePrelFastighet.class
})
public abstract class AbstractArendeFastighet
    extends AbstractArendeObjekt
{

    protected ArrayOfArendeBelagenhetAdress belAdressList;
    protected ArrayOfArendeRegByggnad byggnadList;
    protected GeomType specGeom;

    /**
     * Gets the value of the belAdressList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArendeBelagenhetAdress }
     *     
     */
    public ArrayOfArendeBelagenhetAdress getBelAdressList() {
        return belAdressList;
    }

    /**
     * Sets the value of the belAdressList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArendeBelagenhetAdress }
     *     
     */
    public void setBelAdressList(ArrayOfArendeBelagenhetAdress value) {
        this.belAdressList = value;
    }

    /**
     * Gets the value of the byggnadList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArendeRegByggnad }
     *     
     */
    public ArrayOfArendeRegByggnad getByggnadList() {
        return byggnadList;
    }

    /**
     * Sets the value of the byggnadList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArendeRegByggnad }
     *     
     */
    public void setByggnadList(ArrayOfArendeRegByggnad value) {
        this.byggnadList = value;
    }

    /**
     * Gets the value of the specGeom property.
     * 
     * @return
     *     possible object is
     *     {@link GeomType }
     *     
     */
    public GeomType getSpecGeom() {
        return specGeom;
    }

    /**
     * Sets the value of the specGeom property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeomType }
     *     
     */
    public void setSpecGeom(GeomType value) {
        this.specGeom = value;
    }

}
