//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.01.03 at 03:03:41 PM CET 
//


package vo;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArkivobjektListaHandlingarTyp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArkivobjektListaHandlingarTyp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ArkivobjektHandling" type="{http://xml.ra.se/e-arkiv/FGS-ERMS}ArkivobjektHandlingTyp" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArkivobjektListaHandlingarTyp", propOrder = {
    "arkivobjektHandling"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
public class ArkivobjektListaHandlingarTyp {

    @XmlElement(name = "ArkivobjektHandling", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    protected List<ArkivobjektHandlingTyp> arkivobjektHandling;

    /**
     * Gets the value of the arkivobjektHandling property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arkivobjektHandling property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArkivobjektHandling().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArkivobjektHandlingTyp }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2022-01-03T03:03:41+01:00", comments = "JAXB RI v2.3.2")
    public List<ArkivobjektHandlingTyp> getArkivobjektHandling() {
        if (arkivobjektHandling == null) {
            arkivobjektHandling = new ArrayList<ArkivobjektHandlingTyp>();
        }
        return this.arkivobjektHandling;
    }

}
