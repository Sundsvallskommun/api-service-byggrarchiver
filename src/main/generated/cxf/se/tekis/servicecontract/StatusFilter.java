
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for StatusFilter.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="StatusFilter"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="None"/&amp;gt;
 *     &amp;lt;enumeration value="Aktiv"/&amp;gt;
 *     &amp;lt;enumeration value="Inaktiv"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "StatusFilter")
@XmlEnum
public enum StatusFilter {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Aktiv")
    AKTIV("Aktiv"),
    @XmlEnumValue("Inaktiv")
    INAKTIV("Inaktiv");
    private final String value;

    StatusFilter(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusFilter fromValue(String v) {
        for (StatusFilter c: StatusFilter.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
