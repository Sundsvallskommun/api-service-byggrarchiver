
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for handlaggareBas complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="handlaggareBas"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/arende}handlaggareIdentity"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="fornamn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="efternamn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "handlaggareBas", propOrder = {
    "fornamn",
    "efternamn"
})
public class HandlaggareBas
    extends HandlaggareIdentity
{

    protected String fornamn;
    protected String efternamn;

    /**
     * Gets the value of the fornamn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFornamn() {
        return fornamn;
    }

    /**
     * Sets the value of the fornamn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFornamn(String value) {
        this.fornamn = value;
    }

    /**
     * Gets the value of the efternamn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEfternamn() {
        return efternamn;
    }

    /**
     * Sets the value of the efternamn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEfternamn(String value) {
        this.efternamn = value;
    }

}
