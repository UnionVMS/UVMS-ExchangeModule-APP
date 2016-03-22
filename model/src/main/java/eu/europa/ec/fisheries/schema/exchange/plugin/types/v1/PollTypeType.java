
package eu.europa.ec.fisheries.schema.exchange.plugin.types.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PollTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PollTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONFIG"/>
 *     &lt;enumeration value="SAMPLING"/>
 *     &lt;enumeration value="POLL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PollTypeType")
@XmlEnum
public enum PollTypeType {

    CONFIG,
    SAMPLING,
    POLL;

    public String value() {
        return name();
    }

    public static PollTypeType fromValue(String v) {
        return valueOf(v);
    }

}
