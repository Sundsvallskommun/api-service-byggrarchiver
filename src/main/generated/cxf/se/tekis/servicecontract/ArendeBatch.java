
package se.tekis.servicecontract;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * &lt;p&gt;Java class for ArendeBatch complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="ArendeBatch"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="BatchStart" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&amp;gt;
 *         &amp;lt;element name="BatchEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&amp;gt;
 *         &amp;lt;element name="Arenden" type="{www.tekis.se/ServiceContract}ArrayOfArende" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArendeBatch", propOrder = {
    "batchStart",
    "batchEnd",
    "arenden"
})
public class ArendeBatch {

    @XmlElement(name = "BatchStart", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime batchStart;
    @XmlElement(name = "BatchEnd", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime batchEnd;
    @XmlElement(name = "Arenden")
    protected ArrayOfArende arenden;

    /**
     * Gets the value of the batchStart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getBatchStart() {
        return batchStart;
    }

    /**
     * Sets the value of the batchStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchStart(LocalDateTime value) {
        this.batchStart = value;
    }

    /**
     * Gets the value of the batchEnd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getBatchEnd() {
        return batchEnd;
    }

    /**
     * Sets the value of the batchEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchEnd(LocalDateTime value) {
        this.batchEnd = value;
    }

    /**
     * Gets the value of the arenden property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArende }
     *     
     */
    public ArrayOfArende getArenden() {
        return arenden;
    }

    /**
     * Sets the value of the arenden property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArende }
     *     
     */
    public void setArenden(ArrayOfArende value) {
        this.arenden = value;
    }

}
