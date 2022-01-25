
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for abstractArendeIntressent complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="abstractArendeIntressent"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}intressent"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="attention" type="{www.tekis.se/arende}intressentAttention" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="rollLista" type="{www.tekis.se/arende}ArrayOfString" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractArendeIntressent", propOrder = {
    "attention",
    "rollLista"
})
@XmlSeeAlso({
    ArendeIntressent.class,
    AbstractHandelseIntressent.class
})
public abstract class AbstractArendeIntressent
    extends Intressent
{

    protected IntressentAttention attention;
    protected ArrayOfString rollLista;

    /**
     * Gets the value of the attention property.
     * 
     * @return
     *     possible object is
     *     {@link IntressentAttention }
     *     
     */
    public IntressentAttention getAttention() {
        return attention;
    }

    /**
     * Sets the value of the attention property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntressentAttention }
     *     
     */
    public void setAttention(IntressentAttention value) {
        this.attention = value;
    }

    /**
     * Gets the value of the rollLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getRollLista() {
        return rollLista;
    }

    /**
     * Sets the value of the rollLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setRollLista(ArrayOfString value) {
        this.rollLista = value;
    }

}
