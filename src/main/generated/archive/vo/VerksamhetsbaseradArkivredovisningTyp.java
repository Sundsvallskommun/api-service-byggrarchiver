//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.01.19 at 12:33:39 PM CET 
//


package vo;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VerksamhetsbaseradArkivredovisningTyp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VerksamhetsbaseradArkivredovisningTyp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ArkivReferens" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="KlassificeringsstrukturNamn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;group ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}VerksamhetsbaseradArkivredovisningGrupp"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerksamhetsbaseradArkivredovisningTyp", propOrder = {
    "arkivReferens",
    "klassificeringsstrukturNamn",
    "klassReferens",
    "handlingstypsReferens",
    "forvaringsenhetsReferens"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
public class VerksamhetsbaseradArkivredovisningTyp {

    @XmlElement(name = "ArkivReferens", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String arkivReferens;
    @XmlElement(name = "KlassificeringsstrukturNamn", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String klassificeringsstrukturNamn;
    @XmlElement(name = "KlassReferens")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected List<String> klassReferens;
    @XmlElement(name = "HandlingstypsReferens")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected List<String> handlingstypsReferens;
    @XmlElement(name = "ForvaringsenhetsReferens")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String forvaringsenhetsReferens;

    /**
     * Gets the value of the arkivReferens property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getArkivReferens() {
        return arkivReferens;
    }

    /**
     * Sets the value of the arkivReferens property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setArkivReferens(String value) {
        this.arkivReferens = value;
    }

    /**
     * Gets the value of the klassificeringsstrukturNamn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getKlassificeringsstrukturNamn() {
        return klassificeringsstrukturNamn;
    }

    /**
     * Sets the value of the klassificeringsstrukturNamn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setKlassificeringsstrukturNamn(String value) {
        this.klassificeringsstrukturNamn = value;
    }

    /**
     * Gets the value of the klassReferens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the klassReferens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKlassReferens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public List<String> getKlassReferens() {
        if (klassReferens == null) {
            klassReferens = new ArrayList<String>();
        }
        return this.klassReferens;
    }

    /**
     * Gets the value of the handlingstypsReferens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handlingstypsReferens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHandlingstypsReferens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public List<String> getHandlingstypsReferens() {
        if (handlingstypsReferens == null) {
            handlingstypsReferens = new ArrayList<String>();
        }
        return this.handlingstypsReferens;
    }

    /**
     * Gets the value of the forvaringsenhetsReferens property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getForvaringsenhetsReferens() {
        return forvaringsenhetsReferens;
    }

    /**
     * Sets the value of the forvaringsenhetsReferens property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setForvaringsenhetsReferens(String value) {
        this.forvaringsenhetsReferens = value;
    }

}
