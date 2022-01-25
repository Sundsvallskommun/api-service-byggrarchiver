
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
 *         &amp;lt;element name="statusFilter" type="{www.tekis.se/ServiceContract}RemissStatusFilter"/&amp;gt;
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
    "statusFilter"
})
@XmlRootElement(name = "GetRemisserByPersOrgNr")
public class GetRemisserByPersOrgNr {

    protected String persOrgNr;
    protected String kundNr;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RemissStatusFilter statusFilter;

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
     * Gets the value of the statusFilter property.
     * 
     * @return
     *     possible object is
     *     {@link RemissStatusFilter }
     *     
     */
    public RemissStatusFilter getStatusFilter() {
        return statusFilter;
    }

    /**
     * Sets the value of the statusFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RemissStatusFilter }
     *     
     */
    public void setStatusFilter(RemissStatusFilter value) {
        this.statusFilter = value;
    }

}
