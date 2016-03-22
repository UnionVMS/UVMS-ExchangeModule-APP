
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchField.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SearchField">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TRANSFER_INCOMING"/>
 *     &lt;enumeration value="DATE_RECEIVED_FROM"/>
 *     &lt;enumeration value="DATE_RECEIVED_TO"/>
 *     &lt;enumeration value="SENDER_RECEIVER"/>
 *     &lt;enumeration value="RECIPIENT"/>
 *     &lt;enumeration value="TYPE"/>
 *     &lt;enumeration value="STATUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SearchField")
@XmlEnum
public enum SearchField {

    TRANSFER_INCOMING,
    DATE_RECEIVED_FROM,
    DATE_RECEIVED_TO,
    SENDER_RECEIVER,
    RECIPIENT,
    TYPE,
    STATUS;

    public String value() {
        return name();
    }

    public static SearchField fromValue(String v) {
        return valueOf(v);
    }

}
