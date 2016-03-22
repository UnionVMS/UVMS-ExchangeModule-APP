
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CommandTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CommandTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="POLL"/>
 *     &lt;enumeration value="EMAIL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CommandTypeType")
@XmlEnum
public enum CommandTypeType {

    POLL,
    EMAIL;

    public String value() {
        return name();
    }

    public static CommandTypeType fromValue(String v) {
        return valueOf(v);
    }

}
