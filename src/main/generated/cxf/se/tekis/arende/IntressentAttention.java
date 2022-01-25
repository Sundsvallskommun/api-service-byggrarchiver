
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for intressentAttention complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="intressentAttention"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="attention" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="attentionId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="persNr" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "intressentAttention", propOrder = {
    "attention"
})
public class IntressentAttention {

    protected String attention;
    @XmlAttribute(name = "attentionId")
    protected Integer attentionId;
    @XmlAttribute(name = "persNr")
    protected String persNr;

    /**
     * Gets the value of the attention property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttention() {
        return attention;
    }

    /**
     * Sets the value of the attention property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttention(String value) {
        this.attention = value;
    }

    /**
     * Gets the value of the attentionId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAttentionId() {
        return attentionId;
    }

    /**
     * Sets the value of the attentionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAttentionId(Integer value) {
        this.attentionId = value;
    }

    /**
     * Gets the value of the persNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersNr() {
        return persNr;
    }

    /**
     * Sets the value of the persNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersNr(String value) {
        this.persNr = value;
    }

}
