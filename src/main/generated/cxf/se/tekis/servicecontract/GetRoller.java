
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
 *         &amp;lt;element name="rollTyp" type="{www.tekis.se/ServiceContract}RollTyp"/&amp;gt;
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
    "rollTyp",
    "statusfilter"
})
@XmlRootElement(name = "GetRoller")
public class GetRoller {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RollTyp rollTyp;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StatusFilter statusfilter;

    /**
     * Gets the value of the rollTyp property.
     * 
     * @return
     *     possible object is
     *     {@link RollTyp }
     *     
     */
    public RollTyp getRollTyp() {
        return rollTyp;
    }

    /**
     * Sets the value of the rollTyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link RollTyp }
     *     
     */
    public void setRollTyp(RollTyp value) {
        this.rollTyp = value;
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
