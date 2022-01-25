
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SaveArendeHandlaggareMessage complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="SaveArendeHandlaggareMessage"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;extension base="{www.tekis.se/ServiceContract}GetArendeHandlaggareMessage"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="saveList" type="{www.tekis.se/ServiceContract}ArrayOfArendeHandlaggare" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="deleteList" type="{www.tekis.se/ServiceContract}ArrayOfArendeHandlaggare" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/extension&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveArendeHandlaggareMessage", propOrder = {
    "saveList",
    "deleteList"
})
public class SaveArendeHandlaggareMessage
    extends GetArendeHandlaggareMessage
{

    protected ArrayOfArendeHandlaggare saveList;
    protected ArrayOfArendeHandlaggare deleteList;

    /**
     * Gets the value of the saveList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArendeHandlaggare }
     *     
     */
    public ArrayOfArendeHandlaggare getSaveList() {
        return saveList;
    }

    /**
     * Sets the value of the saveList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArendeHandlaggare }
     *     
     */
    public void setSaveList(ArrayOfArendeHandlaggare value) {
        this.saveList = value;
    }

    /**
     * Gets the value of the deleteList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArendeHandlaggare }
     *     
     */
    public ArrayOfArendeHandlaggare getDeleteList() {
        return deleteList;
    }

    /**
     * Sets the value of the deleteList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArendeHandlaggare }
     *     
     */
    public void setDeleteList(ArrayOfArendeHandlaggare value) {
        this.deleteList = value;
    }

}
