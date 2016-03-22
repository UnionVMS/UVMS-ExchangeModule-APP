
package eu.europa.ec.fisheries.schema.exchange.plugin.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangePluginMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExchangePluginMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SET_CONFIG"/>
 *     &lt;enumeration value="SET_COMMAND"/>
 *     &lt;enumeration value="SET_REPORT"/>
 *     &lt;enumeration value="START"/>
 *     &lt;enumeration value="STOP"/>
 *     &lt;enumeration value="PING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangePluginMethod")
@XmlEnum
public enum ExchangePluginMethod {

    SET_CONFIG,
    SET_COMMAND,
    SET_REPORT,
    START,
    STOP,
    PING;

    public String value() {
        return name();
    }

    public static ExchangePluginMethod fromValue(String v) {
        return valueOf(v);
    }

}
