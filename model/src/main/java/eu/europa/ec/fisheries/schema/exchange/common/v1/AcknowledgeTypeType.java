
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AcknowledgeTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AcknowledgeTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="NOK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AcknowledgeTypeType")
@XmlEnum
public enum AcknowledgeTypeType {

    OK,
    NOK;

    public String value() {
        return name();
    }

    public static AcknowledgeTypeType fromValue(String v) {
        return valueOf(v);
    }

}
