
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
 * &lt;p&gt;Java class for bevakning complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="bevakning"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="bevakningTyp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="bevakningDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&amp;gt;
 *         &amp;lt;element name="handlaggarSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="anteckning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="kvitteradDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="bevakningId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="timeStamp" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bevakning", propOrder = {
    "bevakningTyp",
    "bevakningDatum",
    "handlaggarSign",
    "anteckning",
    "kvitteradDatum"
})
public class Bevakning {

    protected String bevakningTyp;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime bevakningDatum;
    protected String handlaggarSign;
    protected String anteckning;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime kvitteradDatum;
    @XmlAttribute(name = "bevakningId")
    protected Integer bevakningId;
    @XmlAttribute(name = "timeStamp")
    protected String timeStamp;

    /**
     * Gets the value of the bevakningTyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBevakningTyp() {
        return bevakningTyp;
    }

    /**
     * Sets the value of the bevakningTyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBevakningTyp(String value) {
        this.bevakningTyp = value;
    }

    /**
     * Gets the value of the bevakningDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getBevakningDatum() {
        return bevakningDatum;
    }

    /**
     * Sets the value of the bevakningDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBevakningDatum(LocalDateTime value) {
        this.bevakningDatum = value;
    }

    /**
     * Gets the value of the handlaggarSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandlaggarSign() {
        return handlaggarSign;
    }

    /**
     * Sets the value of the handlaggarSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandlaggarSign(String value) {
        this.handlaggarSign = value;
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
     * Gets the value of the kvitteradDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getKvitteradDatum() {
        return kvitteradDatum;
    }

    /**
     * Sets the value of the kvitteradDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKvitteradDatum(LocalDateTime value) {
        this.kvitteradDatum = value;
    }

    /**
     * Gets the value of the bevakningId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBevakningId() {
        return bevakningId;
    }

    /**
     * Sets the value of the bevakningId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBevakningId(Integer value) {
        this.bevakningId = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeStamp(String value) {
        this.timeStamp = value;
    }

}
