
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReportTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReportTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MOVEMENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReportTypeType")
@XmlEnum
public enum ReportTypeType {

    MOVEMENT;

    public String value() {
        return name();
    }

    public static ReportTypeType fromValue(String v) {
        return valueOf(v);
    }

}
