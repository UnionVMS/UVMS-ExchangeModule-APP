
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
 *     &lt;enumeration value="SET_SETTINGS"/>
 *     &lt;enumeration value="GET_CAPABILITIES"/>
 *     &lt;enumeration value="SET_SERVICE_STATUS"/>
 *     &lt;enumeration value="GET_SERVICE"/>
 *     &lt;enumeration value="GET_LOG_BY_QUERY"/>
 *     &lt;enumeration value="CREATE_LOG"/>
 *     &lt;enumeration value="UPDATE_LOG_STATUS"/>
 *     &lt;enumeration value="GET_LOG_STATUS_HISTORY"/>
 *     &lt;enumeration value="GET_LOG_STATUS_HISTORY_BY_QUERY"/>
 *     &lt;enumeration value="GET_LOG_BY_GUID"/>
 *     &lt;enumeration value="SET_POLL_STATUS"/>
 *     &lt;enumeration value="GET_UNSENT_MESSAGE_LIST"/>
 *     &lt;enumeration value="CREATE_UNSENT_MESSAGE"/>
 *     &lt;enumeration value="RESEND_UNSENT_MESSAGE"/>
 *     &lt;enumeration value="REMOVE_UNSENT_MESSAGE"/>
 *     &lt;enumeration value="PING"/>
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
    SET_SETTINGS,
    GET_CAPABILITIES,
    SET_SERVICE_STATUS,
    GET_SERVICE,
    GET_LOG_BY_QUERY,
    CREATE_LOG,
    UPDATE_LOG_STATUS,
    GET_LOG_STATUS_HISTORY,
    GET_LOG_STATUS_HISTORY_BY_QUERY,
    GET_LOG_BY_GUID,
    SET_POLL_STATUS,
    GET_UNSENT_MESSAGE_LIST,
    CREATE_UNSENT_MESSAGE,
    RESEND_UNSENT_MESSAGE,
    REMOVE_UNSENT_MESSAGE,
    PING;

    public String value() {
        return name();
    }

    public static ExchangeDataSourceMethod fromValue(String v) {
        return valueOf(v);
    }

}
