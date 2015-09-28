
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeDataSourceMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExchangeDataSourceMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LIST_SERVICES"/>
 *     &lt;enumeration value="REGISTER_SERVICE"/>
 *     &lt;enumeration value="UNREGISTER_SERVICE"/>
 *     &lt;enumeration value="GET_SETTINGS"/>
 *     &lt;enumeration value="GET_CAPABILITIES"/>
 *     &lt;enumeration value="GET_SERVICE"/>
 *     &lt;enumeration value="GET_LOG_BY_QUERY"/>
 *     &lt;enumeration value="CREATE_LOG"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangeDataSourceMethod")
@XmlEnum
public enum ExchangeDataSourceMethod {

    LIST_SERVICES,
    REGISTER_SERVICE,
    UNREGISTER_SERVICE,
    GET_SETTINGS,
    GET_CAPABILITIES,
    GET_SERVICE,
    GET_LOG_BY_QUERY,
    CREATE_LOG;

    public String value() {
        return name();
    }

    public static ExchangeDataSourceMethod fromValue(String v) {
        return valueOf(v);
    }

}
