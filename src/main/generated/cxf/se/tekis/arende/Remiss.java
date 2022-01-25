
package se.tekis.arende;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * &lt;p&gt;Java class for remiss complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="remiss"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="dnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="mottagare" type="{www.tekis.se/arende}handelseIntressent" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fnr" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *         &amp;lt;element name="fastighetsbeteckning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="erfordras" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="utskicksHandlingar" type="{www.tekis.se/arende}ArrayOfHandelseHandling" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="senastSvarDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&amp;gt;
 *         &amp;lt;element name="remissText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="svarDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="remissId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "remiss", propOrder = {
    "dnr",
    "mottagare",
    "fnr",
    "fastighetsbeteckning",
    "erfordras",
    "utskicksHandlingar",
    "senastSvarDatum",
    "remissText",
    "svarDatum"
})
@XmlSeeAlso({
    se.tekis.servicecontract.Remiss.class
})
public class Remiss {

    protected String dnr;
    protected HandelseIntressent mottagare;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer fnr;
    protected String fastighetsbeteckning;
    protected boolean erfordras;
    protected ArrayOfHandelseHandling utskicksHandlingar;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime senastSvarDatum;
    protected String remissText;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime svarDatum;
    @XmlAttribute(name = "remissId", required = true)
    protected int remissId;

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
     * Gets the value of the mottagare property.
     * 
     * @return
     *     possible object is
     *     {@link HandelseIntressent }
     *     
     */
    public HandelseIntressent getMottagare() {
        return mottagare;
    }

    /**
     * Sets the value of the mottagare property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandelseIntressent }
     *     
     */
    public void setMottagare(HandelseIntressent value) {
        this.mottagare = value;
    }

    /**
     * Gets the value of the fnr property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFnr() {
        return fnr;
    }

    /**
     * Sets the value of the fnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFnr(Integer value) {
        this.fnr = value;
    }

    /**
     * Gets the value of the fastighetsbeteckning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFastighetsbeteckning() {
        return fastighetsbeteckning;
    }

    /**
     * Sets the value of the fastighetsbeteckning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFastighetsbeteckning(String value) {
        this.fastighetsbeteckning = value;
    }

    /**
     * Gets the value of the erfordras property.
     * 
     */
    public boolean isErfordras() {
        return erfordras;
    }

    /**
     * Sets the value of the erfordras property.
     * 
     */
    public void setErfordras(boolean value) {
        this.erfordras = value;
    }

    /**
     * Gets the value of the utskicksHandlingar property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHandelseHandling }
     *     
     */
    public ArrayOfHandelseHandling getUtskicksHandlingar() {
        return utskicksHandlingar;
    }

    /**
     * Sets the value of the utskicksHandlingar property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHandelseHandling }
     *     
     */
    public void setUtskicksHandlingar(ArrayOfHandelseHandling value) {
        this.utskicksHandlingar = value;
    }

    /**
     * Gets the value of the senastSvarDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getSenastSvarDatum() {
        return senastSvarDatum;
    }

    /**
     * Sets the value of the senastSvarDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenastSvarDatum(LocalDateTime value) {
        this.senastSvarDatum = value;
    }

    /**
     * Gets the value of the remissText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemissText() {
        return remissText;
    }

    /**
     * Sets the value of the remissText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemissText(String value) {
        this.remissText = value;
    }

    /**
     * Gets the value of the svarDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getSvarDatum() {
        return svarDatum;
    }

    /**
     * Sets the value of the svarDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvarDatum(LocalDateTime value) {
        this.svarDatum = value;
    }

    /**
     * Gets the value of the remissId property.
     * 
     */
    public int getRemissId() {
        return remissId;
    }

    /**
     * Sets the value of the remissId property.
     * 
     */
    public void setRemissId(int value) {
        this.remissId = value;
    }

}
