
package se.tekis.arende;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * &lt;p&gt;Java class for arende complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="arende"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="status" minOccurs="0"&amp;gt;
 *           &amp;lt;simpleType&amp;gt;
 *             &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *               &amp;lt;enumeration value="Avslutat"/&amp;gt;
 *               &amp;lt;enumeration value="Pågående"/&amp;gt;
 *               &amp;lt;enumeration value="Gallrat"/&amp;gt;
 *               &amp;lt;enumeration value="Makulerat"/&amp;gt;
 *             &amp;lt;/restriction&amp;gt;
 *           &amp;lt;/simpleType&amp;gt;
 *         &amp;lt;/element&amp;gt;
 *         &amp;lt;element name="beskrivning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="ankomstDatum" type="{http://www.w3.org/2001/XMLSchema}date"/&amp;gt;
 *         &amp;lt;element name="slutDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="uppdateradDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="registreradDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handlaggare" type="{www.tekis.se/arende}handlaggareBas" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="intressentLista" type="{www.tekis.se/arende}ArrayOfArendeIntressent" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handelseLista" type="{www.tekis.se/arende}ArrayOfHandelse" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arInomplan" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="projektnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="bevakningLista" type="{www.tekis.se/arende}ArrayOfBevakning" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="objektLista" type="{www.tekis.se/arende}ArrayOfAbstractArendeObjekt" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="atgardStartDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="atgardSlutDatum" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="arendeId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="dnr" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="diarieprefix" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="kommun" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="enhet" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="enhetkod" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="arendegrupp" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="arendetyp" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="arendeslag" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="arendeklass" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="namndkod" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="kalla" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arende", propOrder = {
    "status",
    "beskrivning",
    "ankomstDatum",
    "slutDatum",
    "uppdateradDatum",
    "registreradDatum",
    "handlaggare",
    "intressentLista",
    "handelseLista",
    "arInomplan",
    "projektnr",
    "bevakningLista",
    "objektLista",
    "atgardStartDatum",
    "atgardSlutDatum"
})
@XmlSeeAlso({
    se.tekis.servicecontract.Arende.class
})
public class Arende {

    protected String status;
    protected String beskrivning;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate ankomstDatum;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate slutDatum;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate uppdateradDatum;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate registreradDatum;
    protected HandlaggareBas handlaggare;
    protected ArrayOfArendeIntressent intressentLista;
    protected ArrayOfHandelse handelseLista;
    protected Boolean arInomplan;
    protected String projektnr;
    protected ArrayOfBevakning bevakningLista;
    protected ArrayOfAbstractArendeObjekt objektLista;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate atgardStartDatum;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate atgardSlutDatum;
    @XmlAttribute(name = "arendeId")
    protected Integer arendeId;
    @XmlAttribute(name = "dnr")
    protected String dnr;
    @XmlAttribute(name = "diarieprefix")
    protected String diarieprefix;
    @XmlAttribute(name = "kommun")
    protected String kommun;
    @XmlAttribute(name = "enhet")
    protected String enhet;
    @XmlAttribute(name = "enhetkod")
    protected String enhetkod;
    @XmlAttribute(name = "arendegrupp")
    protected String arendegrupp;
    @XmlAttribute(name = "arendetyp")
    protected String arendetyp;
    @XmlAttribute(name = "arendeslag")
    protected String arendeslag;
    @XmlAttribute(name = "arendeklass")
    protected String arendeklass;
    @XmlAttribute(name = "namndkod")
    protected String namndkod;
    @XmlAttribute(name = "kalla")
    protected String kalla;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the beskrivning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeskrivning() {
        return beskrivning;
    }

    /**
     * Sets the value of the beskrivning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeskrivning(String value) {
        this.beskrivning = value;
    }

    /**
     * Gets the value of the ankomstDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getAnkomstDatum() {
        return ankomstDatum;
    }

    /**
     * Sets the value of the ankomstDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnkomstDatum(LocalDate value) {
        this.ankomstDatum = value;
    }

    /**
     * Gets the value of the slutDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getSlutDatum() {
        return slutDatum;
    }

    /**
     * Sets the value of the slutDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSlutDatum(LocalDate value) {
        this.slutDatum = value;
    }

    /**
     * Gets the value of the uppdateradDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getUppdateradDatum() {
        return uppdateradDatum;
    }

    /**
     * Sets the value of the uppdateradDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUppdateradDatum(LocalDate value) {
        this.uppdateradDatum = value;
    }

    /**
     * Gets the value of the registreradDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getRegistreradDatum() {
        return registreradDatum;
    }

    /**
     * Sets the value of the registreradDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreradDatum(LocalDate value) {
        this.registreradDatum = value;
    }

    /**
     * Gets the value of the handlaggare property.
     * 
     * @return
     *     possible object is
     *     {@link HandlaggareBas }
     *     
     */
    public HandlaggareBas getHandlaggare() {
        return handlaggare;
    }

    /**
     * Sets the value of the handlaggare property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandlaggareBas }
     *     
     */
    public void setHandlaggare(HandlaggareBas value) {
        this.handlaggare = value;
    }

    /**
     * Gets the value of the intressentLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArendeIntressent }
     *     
     */
    public ArrayOfArendeIntressent getIntressentLista() {
        return intressentLista;
    }

    /**
     * Sets the value of the intressentLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArendeIntressent }
     *     
     */
    public void setIntressentLista(ArrayOfArendeIntressent value) {
        this.intressentLista = value;
    }

    /**
     * Gets the value of the handelseLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHandelse }
     *     
     */
    public ArrayOfHandelse getHandelseLista() {
        return handelseLista;
    }

    /**
     * Sets the value of the handelseLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHandelse }
     *     
     */
    public void setHandelseLista(ArrayOfHandelse value) {
        this.handelseLista = value;
    }

    /**
     * Gets the value of the arInomplan property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isArInomplan() {
        return arInomplan;
    }

    /**
     * Sets the value of the arInomplan property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArInomplan(Boolean value) {
        this.arInomplan = value;
    }

    /**
     * Gets the value of the projektnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjektnr() {
        return projektnr;
    }

    /**
     * Sets the value of the projektnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjektnr(String value) {
        this.projektnr = value;
    }

    /**
     * Gets the value of the bevakningLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBevakning }
     *     
     */
    public ArrayOfBevakning getBevakningLista() {
        return bevakningLista;
    }

    /**
     * Sets the value of the bevakningLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBevakning }
     *     
     */
    public void setBevakningLista(ArrayOfBevakning value) {
        this.bevakningLista = value;
    }

    /**
     * Gets the value of the objektLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAbstractArendeObjekt }
     *     
     */
    public ArrayOfAbstractArendeObjekt getObjektLista() {
        return objektLista;
    }

    /**
     * Sets the value of the objektLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAbstractArendeObjekt }
     *     
     */
    public void setObjektLista(ArrayOfAbstractArendeObjekt value) {
        this.objektLista = value;
    }

    /**
     * Gets the value of the atgardStartDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getAtgardStartDatum() {
        return atgardStartDatum;
    }

    /**
     * Sets the value of the atgardStartDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtgardStartDatum(LocalDate value) {
        this.atgardStartDatum = value;
    }

    /**
     * Gets the value of the atgardSlutDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getAtgardSlutDatum() {
        return atgardSlutDatum;
    }

    /**
     * Sets the value of the atgardSlutDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtgardSlutDatum(LocalDate value) {
        this.atgardSlutDatum = value;
    }

    /**
     * Gets the value of the arendeId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArendeId() {
        return arendeId;
    }

    /**
     * Sets the value of the arendeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArendeId(Integer value) {
        this.arendeId = value;
    }

    /**
     * Gets the value of the dnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnr() {
        return dnr;
    }

    /**
     * Sets the value of the dnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnr(String value) {
        this.dnr = value;
    }

    /**
     * Gets the value of the diarieprefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiarieprefix() {
        return diarieprefix;
    }

    /**
     * Sets the value of the diarieprefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiarieprefix(String value) {
        this.diarieprefix = value;
    }

    /**
     * Gets the value of the kommun property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKommun() {
        return kommun;
    }

    /**
     * Sets the value of the kommun property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKommun(String value) {
        this.kommun = value;
    }

    /**
     * Gets the value of the enhet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnhet() {
        return enhet;
    }

    /**
     * Sets the value of the enhet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnhet(String value) {
        this.enhet = value;
    }

    /**
     * Gets the value of the enhetkod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnhetkod() {
        return enhetkod;
    }

    /**
     * Sets the value of the enhetkod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnhetkod(String value) {
        this.enhetkod = value;
    }

    /**
     * Gets the value of the arendegrupp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArendegrupp() {
        return arendegrupp;
    }

    /**
     * Sets the value of the arendegrupp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArendegrupp(String value) {
        this.arendegrupp = value;
    }

    /**
     * Gets the value of the arendetyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArendetyp() {
        return arendetyp;
    }

    /**
     * Sets the value of the arendetyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArendetyp(String value) {
        this.arendetyp = value;
    }

    /**
     * Gets the value of the arendeslag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArendeslag() {
        return arendeslag;
    }

    /**
     * Sets the value of the arendeslag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArendeslag(String value) {
        this.arendeslag = value;
    }

    /**
     * Gets the value of the arendeklass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArendeklass() {
        return arendeklass;
    }

    /**
     * Sets the value of the arendeklass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArendeklass(String value) {
        this.arendeklass = value;
    }

    /**
     * Gets the value of the namndkod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamndkod() {
        return namndkod;
    }

    /**
     * Sets the value of the namndkod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamndkod(String value) {
        this.namndkod = value;
    }

    /**
     * Gets the value of the kalla property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKalla() {
        return kalla;
    }

    /**
     * Sets the value of the kalla property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKalla(String value) {
        this.kalla = value;
    }

}
