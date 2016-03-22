
package eu.europa.ec.fisheries.schema.exchange.registry.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeRegistryMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExchangeRegistryMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REGISTER_SERVICE"/>
 *     &lt;enumeration value="UNREGISTER_SERVICE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangeRegistryMethod")
@XmlEnum
public enum ExchangeRegistryMethod {

    REGISTER_SERVICE,
    UNREGISTER_SERVICE;

    public String value() {
        return name();
    }

    public static ExchangeRegistryMethod fromValue(String v) {
        return valueOf(v);
    }

}
