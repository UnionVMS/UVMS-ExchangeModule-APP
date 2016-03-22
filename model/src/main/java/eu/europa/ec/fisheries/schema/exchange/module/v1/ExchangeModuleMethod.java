
package eu.europa.ec.fisheries.schema.exchange.module.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeModuleMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExchangeModuleMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LIST_SERVICES"/>
 *     &lt;enumeration value="UPDATE_PLUGIN_SETTING"/>
 *     &lt;enumeration value="SET_COMMAND"/>
 *     &lt;enumeration value="SEND_REPORT_TO_PLUGIN"/>
 *     &lt;enumeration value="SET_MOVEMENT_REPORT"/>
 *     &lt;enumeration value="PING"/>
 *     &lt;enumeration value="PROCESSED_MOVEMENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangeModuleMethod")
@XmlEnum
public enum ExchangeModuleMethod {

    LIST_SERVICES,
    UPDATE_PLUGIN_SETTING,
    SET_COMMAND,
    SEND_REPORT_TO_PLUGIN,
    SET_MOVEMENT_REPORT,
    PING,
    PROCESSED_MOVEMENT;

    public String value() {
        return name();
    }

    public static ExchangeModuleMethod fromValue(String v) {
        return valueOf(v);
    }

}
