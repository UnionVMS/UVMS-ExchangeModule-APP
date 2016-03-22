
package eu.europa.ec.fisheries.schema.exchange.service.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CapabilityTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CapabilityTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="POLLABLE"/>
 *     &lt;enumeration value="ONLY_SINGLE_OCEAN"/>
 *     &lt;enumeration value="MULTIPLE_OCEAN"/>
 *     &lt;enumeration value="CONFIGURABLE"/>
 *     &lt;enumeration value="SAMPLING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CapabilityTypeType")
@XmlEnum
public enum CapabilityTypeType {

    POLLABLE,
    ONLY_SINGLE_OCEAN,
    MULTIPLE_OCEAN,
    CONFIGURABLE,
    SAMPLING;

    public String value() {
        return name();
    }

    public static CapabilityTypeType fromValue(String v) {
        return valueOf(v);
    }

}
