
package se.tekis.arende;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for intressent complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="intressent"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="namn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="adress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="coAdress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="postNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="ort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="land" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fornamn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="efternamn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="intressentKommunikationLista" type="{www.tekis.se/arende}ArrayOfIntressentKommunikation" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="aktorbehorighetLista" type="{www.tekis.se/arende}ArrayOfAktorbehorighet" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fakturaAdress" type="{www.tekis.se/arende}fakturaadress" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="besokAdress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *       &amp;lt;attribute name="intressentId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="persOrgNr" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *       &amp;lt;attribute name="arForetag" type="{http://www.w3.org/2001/XMLSchema}boolean" /&amp;gt;
 *       &amp;lt;attribute name="intressentVersionId" type="{http://www.w3.org/2001/XMLSchema}int" /&amp;gt;
 *       &amp;lt;attribute name="kundnr" type="{http://www.w3.org/2001/XMLSchema}string" /&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "intressent", propOrder = {
    "namn",
    "adress",
    "coAdress",
    "postNr",
    "ort",
    "land",
    "fornamn",
    "efternamn",
    "intressentKommunikationLista",
    "aktorbehorighetLista",
    "fakturaAdress",
    "besokAdress"
})
@XmlSeeAlso({
    AbstractArendeIntressent.class
})
public class Intressent {

    protected String namn;
    protected String adress;
    protected String coAdress;
    protected String postNr;
    protected String ort;
    protected String land;
    protected String fornamn;
    protected String efternamn;
    protected ArrayOfIntressentKommunikation intressentKommunikationLista;
    protected ArrayOfAktorbehorighet aktorbehorighetLista;
    protected Fakturaadress fakturaAdress;
    protected String besokAdress;
    @XmlAttribute(name = "intressentId")
    protected Integer intressentId;
    @XmlAttribute(name = "persOrgNr")
    protected String persOrgNr;
    @XmlAttribute(name = "arForetag")
    protected Boolean arForetag;
    @XmlAttribute(name = "intressentVersionId")
    protected Integer intressentVersionId;
    @XmlAttribute(name = "kundnr")
    protected String kundnr;

    /**
     * Gets the value of the namn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamn() {
        return namn;
    }

    /**
     * Sets the value of the namn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamn(String value) {
        this.namn = value;
    }

    /**
     * Gets the value of the adress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdress() {
        return adress;
    }

    /**
     * Sets the value of the adress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdress(String value) {
        this.adress = value;
    }

    /**
     * Gets the value of the coAdress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoAdress() {
        return coAdress;
    }

    /**
     * Sets the value of the coAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoAdress(String value) {
        this.coAdress = value;
    }

    /**
     * Gets the value of the postNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostNr() {
        return postNr;
    }

    /**
     * Sets the value of the postNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostNr(String value) {
        this.postNr = value;
    }

    /**
     * Gets the value of the ort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrt() {
        return ort;
    }

    /**
     * Sets the value of the ort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrt(String value) {
        this.ort = value;
    }

    /**
     * Gets the value of the land property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLand() {
        return land;
    }

    /**
     * Sets the value of the land property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLand(String value) {
        this.land = value;
    }

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

    /**
     * Gets the value of the intressentKommunikationLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfIntressentKommunikation }
     *     
     */
    public ArrayOfIntressentKommunikation getIntressentKommunikationLista() {
        return intressentKommunikationLista;
    }

    /**
     * Sets the value of the intressentKommunikationLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfIntressentKommunikation }
     *     
     */
    public void setIntressentKommunikationLista(ArrayOfIntressentKommunikation value) {
        this.intressentKommunikationLista = value;
    }

    /**
     * Gets the value of the aktorbehorighetLista property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAktorbehorighet }
     *     
     */
    public ArrayOfAktorbehorighet getAktorbehorighetLista() {
        return aktorbehorighetLista;
    }

    /**
     * Sets the value of the aktorbehorighetLista property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAktorbehorighet }
     *     
     */
    public void setAktorbehorighetLista(ArrayOfAktorbehorighet value) {
        this.aktorbehorighetLista = value;
    }

    /**
     * Gets the value of the fakturaAdress property.
     * 
     * @return
     *     possible object is
     *     {@link Fakturaadress }
     *     
     */
    public Fakturaadress getFakturaAdress() {
        return fakturaAdress;
    }

    /**
     * Sets the value of the fakturaAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fakturaadress }
     *     
     */
    public void setFakturaAdress(Fakturaadress value) {
        this.fakturaAdress = value;
    }

    /**
     * Gets the value of the besokAdress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBesokAdress() {
        return besokAdress;
    }

    /**
     * Sets the value of the besokAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBesokAdress(String value) {
        this.besokAdress = value;
    }

    /**
     * Gets the value of the intressentId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIntressentId() {
        return intressentId;
    }

    /**
     * Sets the value of the intressentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIntressentId(Integer value) {
        this.intressentId = value;
    }

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
     * Gets the value of the arForetag property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isArForetag() {
        return arForetag;
    }

    /**
     * Sets the value of the arForetag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArForetag(Boolean value) {
        this.arForetag = value;
    }

    /**
     * Gets the value of the intressentVersionId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIntressentVersionId() {
        return intressentVersionId;
    }

    /**
     * Sets the value of the intressentVersionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIntressentVersionId(Integer value) {
        this.intressentVersionId = value;
    }

    /**
     * Gets the value of the kundnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKundnr() {
        return kundnr;
    }

    /**
     * Sets the value of the kundnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKundnr(String value) {
        this.kundnr = value;
    }

}
