//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.01.03 at 03:03:41 PM CET 
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
 * <p>Java class for LeveransobjektTyp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LeveransobjektTyp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}SystemInfo" minOccurs="0"/&gt;
 *         &lt;element ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}ArkivbildarStruktur" minOccurs="0"/&gt;
 *         &lt;element ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}Informationsklass" minOccurs="0"/&gt;
 *         &lt;element name="VerksamhetsbaseradArkivredovisning" type="{http://xml.ra.se/e-arkiv/FGS-ERMS}VerksamhetsbaseradArkivredovisningTyp" minOccurs="0"/&gt;
 *         &lt;element ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}Bilaga" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}EgnaElement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://xml.ra.se/e-arkiv/FGS-ERMS}TillkommandeXMLdata" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="ArkivobjektListaArenden" type="{http://xml.ra.se/e-arkiv/FGS-ERMS}ArkivobjektListaArendenTyp" minOccurs="0"/&gt;
 *           &lt;element name="ArkivobjektListaHandlingar" type="{http://xml.ra.se/e-arkiv/FGS-ERMS}ArkivobjektListaHandlingarTyp" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LeveransobjektTyp", propOrder = {
    "systemInfo",
    "arkivbildarStruktur",
    "informationsklass",
    "verksamhetsbaseradArkivredovisning",
    "bilaga",
    "egnaElement",
    "tillkommandeXMLdata",
    "arkivobjektListaArenden",
    "arkivobjektListaHandlingar"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
public class LeveransobjektTyp {

    @XmlElement(name = "SystemInfo")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected SystemInfoTyp systemInfo;
    @XmlElement(name = "ArkivbildarStruktur")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected ArkivbildarStrukturTyp arkivbildarStruktur;
    @XmlElement(name = "Informationsklass")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected String informationsklass;
    @XmlElement(name = "VerksamhetsbaseradArkivredovisning")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected VerksamhetsbaseradArkivredovisningTyp verksamhetsbaseradArkivredovisning;
    @XmlElement(name = "Bilaga")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected List<BilagaTyp> bilaga;
    @XmlElement(name = "EgnaElement")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected List<EgnaElement> egnaElement;
    @XmlElement(name = "TillkommandeXMLdata")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected List<UtokandeKomplexTyp> tillkommandeXMLdata;
    @XmlElement(name = "ArkivobjektListaArenden")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected ArkivobjektListaArendenTyp arkivobjektListaArenden;
    @XmlElement(name = "ArkivobjektListaHandlingar")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected ArkivobjektListaHandlingarTyp arkivobjektListaHandlingar;

    /**
     * Elementet inte definerat.
     * 
     * @return
     *     possible object is
     *     {@link SystemInfoTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public SystemInfoTyp getSystemInfo() {
        return systemInfo;
    }

    /**
     * Sets the value of the systemInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemInfoTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public void setSystemInfo(SystemInfoTyp value) {
        this.systemInfo = value;
    }

    /**
     * Gets the value of the arkivbildarStruktur property.
     * 
     * @return
     *     possible object is
     *     {@link ArkivbildarStrukturTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public ArkivbildarStrukturTyp getArkivbildarStruktur() {
        return arkivbildarStruktur;
    }

    /**
     * Sets the value of the arkivbildarStruktur property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArkivbildarStrukturTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public void setArkivbildarStruktur(ArkivbildarStrukturTyp value) {
        this.arkivbildarStruktur = value;
    }

    /**
     * Kan användas för att ange informationsklassning utifrån informationssäkerhet.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public String getInformationsklass() {
        return informationsklass;
    }

    /**
     * Sets the value of the informationsklass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public void setInformationsklass(String value) {
        this.informationsklass = value;
    }

    /**
     * Gets the value of the verksamhetsbaseradArkivredovisning property.
     * 
     * @return
     *     possible object is
     *     {@link VerksamhetsbaseradArkivredovisningTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public VerksamhetsbaseradArkivredovisningTyp getVerksamhetsbaseradArkivredovisning() {
        return verksamhetsbaseradArkivredovisning;
    }

    /**
     * Sets the value of the verksamhetsbaseradArkivredovisning property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerksamhetsbaseradArkivredovisningTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public void setVerksamhetsbaseradArkivredovisning(VerksamhetsbaseradArkivredovisningTyp value) {
        this.verksamhetsbaseradArkivredovisning = value;
    }

    /**
     * Gets the value of the bilaga property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bilaga property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBilaga().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BilagaTyp }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public List<BilagaTyp> getBilaga() {
        if (bilaga == null) {
            bilaga = new ArrayList<BilagaTyp>();
        }
        return this.bilaga;
    }

    /**
     * Ger möjlighet att lägga till egendefinierade element som kompletterar de fördefinierade elementen.Gets the value of the egnaElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the egnaElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEgnaElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EgnaElement }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public List<EgnaElement> getEgnaElement() {
        if (egnaElement == null) {
            egnaElement = new ArrayList<EgnaElement>();
        }
        return this.egnaElement;
    }

    /**
     * Eventuellt tillkommande metadata i valfritt XML-format.Gets the value of the tillkommandeXMLdata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tillkommandeXMLdata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTillkommandeXMLdata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UtokandeKomplexTyp }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public List<UtokandeKomplexTyp> getTillkommandeXMLdata() {
        if (tillkommandeXMLdata == null) {
            tillkommandeXMLdata = new ArrayList<UtokandeKomplexTyp>();
        }
        return this.tillkommandeXMLdata;
    }

    /**
     * Gets the value of the arkivobjektListaArenden property.
     * 
     * @return
     *     possible object is
     *     {@link ArkivobjektListaArendenTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public ArkivobjektListaArendenTyp getArkivobjektListaArenden() {
        return arkivobjektListaArenden;
    }

    /**
     * Sets the value of the arkivobjektListaArenden property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArkivobjektListaArendenTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public void setArkivobjektListaArenden(ArkivobjektListaArendenTyp value) {
        this.arkivobjektListaArenden = value;
    }

    /**
     * Gets the value of the arkivobjektListaHandlingar property.
     * 
     * @return
     *     possible object is
     *     {@link ArkivobjektListaHandlingarTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public ArkivobjektListaHandlingarTyp getArkivobjektListaHandlingar() {
        return arkivobjektListaHandlingar;
    }

    /**
     * Sets the value of the arkivobjektListaHandlingar property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArkivobjektListaHandlingarTyp }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public void setArkivobjektListaHandlingar(ArkivobjektListaHandlingarTyp value) {
        this.arkivobjektListaHandlingar = value;
    }

}