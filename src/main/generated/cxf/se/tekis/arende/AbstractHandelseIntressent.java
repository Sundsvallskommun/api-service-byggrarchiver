
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for abstractHandelseIntressent complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="abstractHandelseIntressent"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}abstractArendeIntressent"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="adressering" type="{www.tekis.se/arende}intressentKommunikation" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="docSplitToken" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractHandelseIntressent", propOrder = {
    "adressering"
})
@XmlSeeAlso({
    HandelseIntressent.class
})
public abstract class AbstractHandelseIntressent
    extends AbstractArendeIntressent
{

    protected IntressentKommunikation adressering;
    @XmlAttribute(name = "docSplitToken")
    protected String docSplitToken;

    /**
     * Gets the value of the adressering property.
     * 
     * @return
     *     possible object is
     *     {@link IntressentKommunikation }
     *     
     */
    public IntressentKommunikation getAdressering() {
        return adressering;
    }

    /**
     * Sets the value of the adressering property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntressentKommunikation }
     *     
     */
    public void setAdressering(IntressentKommunikation value) {
        this.adressering = value;
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
