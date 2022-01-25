
package se.tekis.arende;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * &lt;p&gt;Java class for handelse complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="handelse"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="riktning"&amp;gt;
 *           &amp;lt;simpleType&amp;gt;
 *             &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *               &amp;lt;enumeration value="In"/&amp;gt;
 *               &amp;lt;enumeration value="Ut"/&amp;gt;
 *               &amp;lt;enumeration value="Okänd"/&amp;gt;
 *             &amp;lt;/restriction&amp;gt;
 *           &amp;lt;/simpleType&amp;gt;
 *         &amp;lt;/element&amp;gt;
 *         &amp;lt;element name="rubrik" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="startDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&amp;gt;
 *         &amp;lt;element name="anteckning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handelseslag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handelsetyp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handelseutfall" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="sekretess" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="sekretessKapitel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="sekretessParagraf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="makulerad" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="beslut" type="{www.tekis.se/arende}beslut" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handlingLista" type="{www.tekis.se/arende}ArrayOfHandelseHandling" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="intressentLista" type="{www.tekis.se/arende}ArrayOfHandelseIntressent" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="bevakningLista" type="{www.tekis.se/arende}ArrayOfBevakning" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arbetsmaterial" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="TidDebiterbar" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="TidEjDebiterbar" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handlaggare" type="{www.tekis.se/arende}handlaggareIdentity" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="handelseId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "handelse", propOrder = {
    "riktning",
    "rubrik",
    "startDatum",
    "anteckning",
    "handelseslag",
    "handelsetyp",
    "handelseutfall",
    "sekretess",
    "sekretessKapitel",
    "sekretessParagraf",
    "makulerad",
    "beslut",
    "handlingLista",
    "intressentLista",
    "bevakningLista",
    "arbetsmaterial",
    "tidDebiterbar",
    "tidEjDebiterbar",
    "handlaggare"
})
public class Handelse {

    @XmlElement(required = true)
    protected String riktning;
    protected String rubrik;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime startDatum;
    protected String anteckning;
    protected String handelseslag;
    protected String handelsetyp;
    protected String handelseutfall;
    protected boolean sekretess;
    protected String sekretessKapitel;
    protected String sekretessParagraf;
    protected boolean makulerad;
    protected Beslut beslut;
    protected ArrayOfHandelseHandling handlingLista;
    protected ArrayOfHandelseIntressent intressentLista;
    protected ArrayOfBevakning bevakningLista;
    protected boolean arbetsmaterial;
    @XmlElement(name = "TidDebiterbar")
    protected Integer tidDebiterbar;
    @XmlElement(name = "TidEjDebiterbar")
    protected Integer tidEjDebiterbar;
    protected HandlaggareIdentity handlaggare;
    @XmlAttribute(name = "handelseId", required = true)
    protected int handelseId;
    @XmlAttribute(name = "checksum")
    protected String checksum;

    /**
     * Gets the value of the riktning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiktning() {
        return riktning;
    }

    /**
     * Sets the value of the riktning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiktning(String value) {
        this.riktning = value;
    }

    /**
     * Gets the value of the rubrik property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRubrik() {
        return rubrik;
    }

    /**
     * Sets the value of the rubrik property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRubrik(String value) {
        this.rubrik = value;
    }

    /**
     * Gets the value of the startDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getStartDatum() {
        return startDatum;
    }

    /**
     * Sets the value of the startDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDatum(LocalDateTime value) {
        this.startDatum = value;
    }

    /**
     * Gets the value of the anteckning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnteckning() {
        return anteckning;
    }

    /**
     * Sets the value of the anteckning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnteckning(String value) {
        this.anteckning = value;
    }

    /**
     * Gets the value of the handelseslag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandelseslag() {
        return handelseslag;
    }

    /**
     * Sets the value of the handelseslag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandelseslag(String value) {
        this.handelseslag = value;
    }

    /**
     * Gets the value of the handelsetyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandelsetyp() {
        return handelsetyp;
    }

    /**
     * Sets the value of the handelsetyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandelsetyp(String value) {
        this.handelsetyp = value;
    }

    /**
     * Gets the value of the handelseutfall property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandelseutfall() {
        return handelseutfall;
    }

    /**
     * Sets the value of the handelseutfall property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandelseutfall(String value) {
        this.handelseutfall = value;
    }

    /**
     * Gets the value of the sekretess property.
     * 
     */
    public boolean isSekretess() {
        return sekretess;
    }

    /**
     * Sets the value of the sekretess property.
     * 
     */
    public void setSekretess(boolean value) {
        this.sekretess = value;
    }

    /**
     * Gets the value of the sekretessKapitel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSekretessKapitel() {
        return sekretessKapitel;
    }

    /**
     * Sets the value of the sekretessKapitel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSekretessKapitel(String value) {
        this.sekretessKapitel = value;
    }

    /**
     * Gets the value of the sekretessParagraf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSekretessParagraf() {
        return sekretessParagraf;
    }

    /**
     * Sets the value of the sekretessParagraf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSekretessParagraf(String value) {
        this.sekretessParagraf = value;
    }

    /**
     * Gets the value of the makulerad property.
     * 
     */
    public boolean isMakulerad() {
        return makulerad;
    }

    /**
     * Sets the value of the makulerad property.
     * 
     */
    public void setMakulerad(boolean value) {
        this.makulerad = value;
    }

    /**
     * Gets the value of the beslut property.
     * 
     * @return
     *     possible object is
     *     {@link Beslut }
     *     
     */
    public Beslut getBeslut() {
        return beslut;
    }

    /**
     * Sets the value of the beslut property.
     * 
     * @param value
     *     allowed object is
     *     {@link Beslut }
     *     
     */
    public void setBeslut(Beslut value) {
        this.beslut = value;
    }

    /**
     * Gets the value of the handlingLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHandelseHandling }
     *     
     */
    public ArrayOfHandelseHandling getHandlingLista() {
        return handlingLista;
    }

    /**
     * Sets the value of the handlingLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHandelseHandling }
     *     
     */
    public void setHandlingLista(ArrayOfHandelseHandling value) {
        this.handlingLista = value;
    }

    /**
     * Gets the value of the intressentLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHandelseIntressent }
     *     
     */
    public ArrayOfHandelseIntressent getIntressentLista() {
        return intressentLista;
    }

    /**
     * Sets the value of the intressentLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHandelseIntressent }
     *     
     */
    public void setIntressentLista(ArrayOfHandelseIntressent value) {
        this.intressentLista = value;
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
     * Gets the value of the arbetsmaterial property.
     * 
     */
    public boolean isArbetsmaterial() {
        return arbetsmaterial;
    }

    /**
     * Sets the value of the arbetsmaterial property.
     * 
     */
    public void setArbetsmaterial(boolean value) {
        this.arbetsmaterial = value;
    }

    /**
     * Gets the value of the tidDebiterbar property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTidDebiterbar() {
        return tidDebiterbar;
    }

    /**
     * Sets the value of the tidDebiterbar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTidDebiterbar(Integer value) {
        this.tidDebiterbar = value;
    }

    /**
     * Gets the value of the tidEjDebiterbar property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTidEjDebiterbar() {
        return tidEjDebiterbar;
    }

    /**
     * Sets the value of the tidEjDebiterbar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTidEjDebiterbar(Integer value) {
        this.tidEjDebiterbar = value;
    }

    /**
     * Gets the value of the handlaggare property.
     * 
     * @return
     *     possible object is
     *     {@link HandlaggareIdentity }
     *     
     */
    public HandlaggareIdentity getHandlaggare() {
        return handlaggare;
    }

    /**
     * Sets the value of the handlaggare property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandlaggareIdentity }
     *     
     */
    public void setHandlaggare(HandlaggareIdentity value) {
        this.handlaggare = value;
    }

    /**
     * Gets the value of the handelseId property.
     * 
     */
    public int getHandelseId() {
        return handelseId;
    }

    /**
     * Sets the value of the handelseId property.
     * 
     */
    public void setHandelseId(int value) {
        this.handelseId = value;
    }

    /**
     * Gets the value of the checksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the value of the checksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChecksum(String value) {
        this.checksum = value;
    }

}
