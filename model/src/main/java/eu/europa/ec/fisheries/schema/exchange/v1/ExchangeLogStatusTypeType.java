
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeLogStatusTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExchangeLogStatusTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ISSUED"/>
 *     &lt;enumeration value="PENDING"/>
 *     &lt;enumeration value="PROBABLY_TRANSMITTED"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="SUCCESSFUL"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExchangeLogStatusTypeType")
@XmlEnum
public enum ExchangeLogStatusTypeType {

    ISSUED,
    PENDING,
    PROBABLY_TRANSMITTED,
    FAILED,
    SUCCESSFUL,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static ExchangeLogStatusTypeType fromValue(String v) {
        return valueOf(v);
    }

}
