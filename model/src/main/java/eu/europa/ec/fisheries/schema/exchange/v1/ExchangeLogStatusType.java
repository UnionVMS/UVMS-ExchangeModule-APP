
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeLogStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExchangeLogStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDING"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="SUCCESSFUL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangeLogStatusType")
@XmlEnum
public enum ExchangeLogStatusType {

    PENDING,
    FAILED,
    SUCCESSFUL;

    public String value() {
        return name();
    }

    public static ExchangeLogStatusType fromValue(String v) {
        return valueOf(v);
    }

}
