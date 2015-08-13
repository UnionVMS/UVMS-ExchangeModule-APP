
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
 *     &lt;enumeration value="FROM_DATE"/>
 *     &lt;enumeration value="TO_DATE"/>
 *     &lt;enumeration value="SENT_BY"/>
 *     &lt;enumeration value="MESSAGE_CONTAINS"/>
 *     &lt;enumeration value="FORWARD_RULE"/>
 *     &lt;enumeration value="RECIPIENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SearchField")
@XmlEnum
public enum SearchField {

    FROM_DATE,
    TO_DATE,
    SENT_BY,
    MESSAGE_CONTAINS,
    FORWARD_RULE,
    RECIPIENT;

    public String value() {
        return name();
    }

    public static SearchField fromValue(String v) {
        return valueOf(v);
    }

}
