
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for dokumentFil complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="dokumentFil"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="filBuffer" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="filAndelse" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="docSplitToken" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dokumentFil", propOrder = {
    "filBuffer"
})
public class DokumentFil {

    protected byte[] filBuffer;
    @XmlAttribute(name = "filAndelse")
    protected String filAndelse;
    @XmlAttribute(name = "docSplitToken")
    protected String docSplitToken;

    /**
     * Gets the value of the filBuffer property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFilBuffer() {
        return filBuffer;
    }

    /**
     * Sets the value of the filBuffer property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFilBuffer(byte[] value) {
        this.filBuffer = value;
    }

    /**
     * Gets the value of the filAndelse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilAndelse() {
        return filAndelse;
    }

    /**
     * Sets the value of the filAndelse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilAndelse(String value) {
        this.filAndelse = value;
    }

    /**
     * Gets the value of the docSplitToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocSplitToken() {
        return docSplitToken;
    }

    /**
     * Sets the value of the docSplitToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocSplitToken(String value) {
        this.docSplitToken = value;
    }

}
