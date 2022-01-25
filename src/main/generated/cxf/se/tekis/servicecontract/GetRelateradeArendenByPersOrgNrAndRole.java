
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
 *         &amp;lt;element name="persOrgNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="kundNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="arendeIntressentRoller" type="{www.tekis.se/ServiceContract}ArrayOfString" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="handelseIntressentRoller" type="{www.tekis.se/ServiceContract}ArrayOfString" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="statusfilter" type="{www.tekis.se/ServiceContract}StatusFilter"/&amp;gt;
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
    "persOrgNr",
    "kundNr",
    "arendeIntressentRoller",
    "handelseIntressentRoller",
    "statusfilter"
})
@XmlRootElement(name = "GetRelateradeArendenByPersOrgNrAndRole")
public class GetRelateradeArendenByPersOrgNrAndRole {

    protected String persOrgNr;
    protected String kundNr;
    protected ArrayOfString arendeIntressentRoller;
    protected ArrayOfString handelseIntressentRoller;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StatusFilter statusfilter;

    /**
     * Gets the value of the persOrgNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersOrgNr() {
        return persOrgNr;
    }

    /**
     * Sets the value of the persOrgNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersOrgNr(String value) {
        this.persOrgNr = value;
    }

    /**
     * Gets the value of the kundNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKundNr() {
        return kundNr;
    }

    /**
     * Sets the value of the kundNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKundNr(String value) {
        this.kundNr = value;
    }

    /**
     * Gets the value of the arendeIntressentRoller property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getArendeIntressentRoller() {
        return arendeIntressentRoller;
    }

    /**
     * Sets the value of the arendeIntressentRoller property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setArendeIntressentRoller(ArrayOfString value) {
        this.arendeIntressentRoller = value;
    }

    /**
     * Gets the value of the handelseIntressentRoller property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getHandelseIntressentRoller() {
        return handelseIntressentRoller;
    }

    /**
     * Sets the value of the handelseIntressentRoller property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setHandelseIntressentRoller(ArrayOfString value) {
        this.handelseIntressentRoller = value;
    }

    /**
     * Gets the value of the statusfilter property.
     * 
     * @return
     *     possible object is
     *     {@link StatusFilter }
     *     
     */
    public StatusFilter getStatusfilter() {
        return statusfilter;
    }

    /**
     * Sets the value of the statusfilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusFilter }
     *     
     */
    public void setStatusfilter(StatusFilter value) {
        this.statusfilter = value;
    }

}
