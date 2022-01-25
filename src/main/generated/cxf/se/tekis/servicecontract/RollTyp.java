
package se.tekis.servicecontract;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RollTyp.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="RollTyp"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="Intressent"/&amp;gt;
 *     &amp;lt;enumeration value="RemissMottagare"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "RollTyp")
@XmlEnum
public enum RollTyp {

    @XmlEnumValue("Intressent")
    INTRESSENT("Intressent"),
    @XmlEnumValue("RemissMottagare")
    REMISS_MOTTAGARE("RemissMottagare");
    private final String value;

    RollTyp(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RollTyp fromValue(String v) {
        for (RollTyp c: RollTyp.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
