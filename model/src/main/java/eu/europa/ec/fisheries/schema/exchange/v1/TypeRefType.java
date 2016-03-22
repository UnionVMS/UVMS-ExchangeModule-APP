
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeRefType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeRefType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MOVEMENT"/>
 *     &lt;enumeration value="POLL"/>
 *     &lt;enumeration value="ALARM"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TypeRefType")
@XmlEnum
public enum TypeRefType {

    MOVEMENT,
    POLL,
    ALARM,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static TypeRefType fromValue(String v) {
        return valueOf(v);
    }

}
