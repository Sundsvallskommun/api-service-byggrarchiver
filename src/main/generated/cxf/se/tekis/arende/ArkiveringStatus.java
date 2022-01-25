
package se.tekis.arende;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArkiveringStatus.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="ArkiveringStatus"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="ArkiverasEj"/&amp;gt;
 *     &amp;lt;enumeration value="Arkiveras"/&amp;gt;
 *     &amp;lt;enumeration value="Arkiverad"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "ArkiveringStatus")
@XmlEnum
public enum ArkiveringStatus {

    @XmlEnumValue("ArkiverasEj")
    ARKIVERAS_EJ("ArkiverasEj"),
    @XmlEnumValue("Arkiveras")
    ARKIVERAS("Arkiveras"),
    @XmlEnumValue("Arkiverad")
    ARKIVERAD("Arkiverad");
    private final String value;

    ArkiveringStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ArkiveringStatus fromValue(String v) {
        for (ArkiveringStatus c: ArkiveringStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
