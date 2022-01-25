
package se.tekis.arende;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for referensTyp.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="referensTyp"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="Bifogad"/&amp;gt;
 *     &amp;lt;enumeration value="Refererad"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "referensTyp")
@XmlEnum
public enum ReferensTyp {

    @XmlEnumValue("Bifogad")
    BIFOGAD("Bifogad"),
    @XmlEnumValue("Refererad")
    REFERERAD("Refererad");
    private final String value;

    ReferensTyp(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReferensTyp fromValue(String v) {
        for (ReferensTyp c: ReferensTyp.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
