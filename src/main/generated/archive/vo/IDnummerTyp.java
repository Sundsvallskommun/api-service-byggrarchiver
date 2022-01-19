//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.01.19 at 12:33:39 PM CET 
//


package vo;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * ID-nummer för person eller organisation.
 * 
 * <p>Java class for IDnummerTyp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IDnummerTyp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="Typ" type="{http://xml.ra.se/e-arkiv/FGS-ERMS}IDnummerTypEnum" default="1" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IDnummerTyp", propOrder = {
    "content"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
public class IDnummerTyp {

    @XmlValue
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected String content;
    @XmlAttribute(name = "Typ")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    protected BigInteger typ;

    /**
     * ID-nummer för person eller organisation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the typ property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public BigInteger getTyp() {
        if (typ == null) {
            return new BigInteger("1");
        } else {
            return typ;
        }
    }

    /**
     * Sets the value of the typ property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-19T12:33:39+01:00", comments = "JAXB RI v2.3.2")
    public void setTyp(BigInteger value) {
        this.typ = value;
    }

}
