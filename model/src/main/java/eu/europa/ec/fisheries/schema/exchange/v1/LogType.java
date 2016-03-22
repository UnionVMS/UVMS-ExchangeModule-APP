
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LogType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LogType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RECEIVE_MOVEMENT"/>
 *     &lt;enumeration value="SEND_MOVEMENT"/>
 *     &lt;enumeration value="SEND_EMAIL"/>
 *     &lt;enumeration value="SEND_POLL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LogType")
@XmlEnum
public enum LogType {

    RECEIVE_MOVEMENT,
    SEND_MOVEMENT,
    SEND_EMAIL,
    SEND_POLL;

    public String value() {
        return name();
    }

    public static LogType fromValue(String v) {
        return valueOf(v);
    }

}
