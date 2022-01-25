
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &amp;lt;element name="GetRemisserByPersOrgNrResult" type="{www.tekis.se/ServiceContract}ArrayOfRemiss" minOccurs="0"/&amp;gt;
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
    "getRemisserByPersOrgNrResult"
})
@XmlRootElement(name = "GetRemisserByPersOrgNrResponse")
public class GetRemisserByPersOrgNrResponse {

    @XmlElement(name = "GetRemisserByPersOrgNrResult")
    protected ArrayOfRemiss getRemisserByPersOrgNrResult;

    /**
     * Gets the value of the getRemisserByPersOrgNrResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRemiss }
     *     
     */
    public ArrayOfRemiss getGetRemisserByPersOrgNrResult() {
        return getRemisserByPersOrgNrResult;
    }

    /**
     * Sets the value of the getRemisserByPersOrgNrResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRemiss }
     *     
     */
    public void setGetRemisserByPersOrgNrResult(ArrayOfRemiss value) {
        this.getRemisserByPersOrgNrResult = value;
    }

}
