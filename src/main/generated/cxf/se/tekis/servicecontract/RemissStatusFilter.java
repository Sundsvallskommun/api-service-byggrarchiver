
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RemissStatusFilter.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="RemissStatusFilter"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="None"/&amp;gt;
 *     &amp;lt;enumeration value="EjBesvarad"/&amp;gt;
 *     &amp;lt;enumeration value="Besvarad"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "RemissStatusFilter")
@XmlEnum
public enum RemissStatusFilter {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("EjBesvarad")
    EJ_BESVARAD("EjBesvarad"),
    @XmlEnumValue("Besvarad")
    BESVARAD("Besvarad");
    private final String value;

    RemissStatusFilter(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RemissStatusFilter fromValue(String v) {
        for (RemissStatusFilter c: RemissStatusFilter.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
