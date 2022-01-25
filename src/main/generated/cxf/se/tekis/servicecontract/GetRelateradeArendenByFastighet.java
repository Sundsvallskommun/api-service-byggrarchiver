
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="fnr" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *         &amp;lt;element name="trakt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fBetNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arHuvudObjekt" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="statusFilter" type="{www.tekis.se/ServiceContract}StatusFilter"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fnr",
    "trakt",
    "fBetNr",
    "arHuvudObjekt",
    "statusFilter"
})
@XmlRootElement(name = "GetRelateradeArendenByFastighet")
public class GetRelateradeArendenByFastighet {

    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer fnr;
    protected String trakt;
    protected String fBetNr;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean arHuvudObjekt;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StatusFilter statusFilter;

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
     * Gets the value of the trakt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrakt() {
        return trakt;
    }

    /**
     * Sets the value of the trakt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrakt(String value) {
        this.trakt = value;
    }

    /**
     * Gets the value of the fBetNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFBetNr() {
        return fBetNr;
    }

    /**
     * Sets the value of the fBetNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFBetNr(String value) {
        this.fBetNr = value;
    }

    /**
     * Gets the value of the arHuvudObjekt property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isArHuvudObjekt() {
        return arHuvudObjekt;
    }

    /**
     * Sets the value of the arHuvudObjekt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArHuvudObjekt(Boolean value) {
        this.arHuvudObjekt = value;
    }

    /**
     * Gets the value of the statusFilter property.
     * 
     * @return
     *     possible object is
     *     {@link StatusFilter }
     *     
     */
    public StatusFilter getStatusFilter() {
        return statusFilter;
    }

    /**
     * Sets the value of the statusFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusFilter }
     *     
     */
    public void setStatusFilter(StatusFilter value) {
        this.statusFilter = value;
    }

}
