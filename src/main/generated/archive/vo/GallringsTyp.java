//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.01.19 at 12:33:39 PM CET 
//


package vo;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GallringsTyp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GallringsTyp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GallringsFrist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="GallringsForklaring" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="GallringsPeriodSlut" type="{http://xml.ra.se/e-arkiv/FGS-ERMS}EgetTidDatum" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Gallras" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GallringsTyp", propOrder = {
    "gallringsFrist",
    "gallringsForklaring",
    "gallringsPeriodSlut"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
public class GallringsTyp {

    @XmlElement(name = "GallringsFrist")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String gallringsFrist;
    @XmlElement(name = "GallringsForklaring")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String gallringsForklaring;
    @XmlElement(name = "GallringsPeriodSlut")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String gallringsPeriodSlut;
    @XmlAttribute(name = "Gallras", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected boolean gallras;

    /**
     * Gets the value of the gallringsFrist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getGallringsFrist() {
        return gallringsFrist;
    }

    /**
     * Sets the value of the gallringsFrist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setGallringsFrist(String value) {
        this.gallringsFrist = value;
    }

    /**
     * Gets the value of the gallringsForklaring property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getGallringsForklaring() {
        return gallringsForklaring;
    }

    /**
     * Sets the value of the gallringsForklaring property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setGallringsForklaring(String value) {
        this.gallringsForklaring = value;
    }

    /**
     * Gets the value of the gallringsPeriodSlut property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getGallringsPeriodSlut() {
        return gallringsPeriodSlut;
    }

    /**
     * Sets the value of the gallringsPeriodSlut property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setGallringsPeriodSlut(String value) {
        this.gallringsPeriodSlut = value;
    }

    /**
     * Gets the value of the gallras property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public boolean isGallras() {
        return gallras;
    }

    /**
     * Sets the value of the gallras property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setGallras(boolean value) {
        this.gallras = value;
    }

}
