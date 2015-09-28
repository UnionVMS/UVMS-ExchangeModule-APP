
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
 *     &lt;enumeration value="REGISTER_SERVICE"/>
 *     &lt;enumeration value="UNREGISTER_SERVICE"/>
 *     &lt;enumeration value="GET_SETTINGS"/>
 *     &lt;enumeration value="GET_CAPABILITIES"/>
 *     &lt;enumeration value="CREATE_POLL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangeModuleMethod")
@XmlEnum
public enum ExchangeModuleMethod {

    LIST_SERVICES,
    REGISTER_SERVICE,
    UNREGISTER_SERVICE,
    GET_SETTINGS,
    GET_CAPABILITIES,
    CREATE_POLL;

    public String value() {
        return name();
    }

    public static ExchangeModuleMethod fromValue(String v) {
        return valueOf(v);
    }

}
